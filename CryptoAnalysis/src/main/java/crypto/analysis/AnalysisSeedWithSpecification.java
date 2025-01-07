package crypto.analysis;

import boomerang.results.ForwardBoomerangResults;
import boomerang.scene.AllocVal;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.DeclaredMethod;
import boomerang.scene.InvokeExpr;
import boomerang.scene.Statement;
import boomerang.scene.Val;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import crypto.analysis.errors.AbstractConstraintsError;
import crypto.analysis.errors.ForbiddenMethodError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.TypestateError;
import crypto.constraints.ConstraintsAnalysis;
import crypto.constraints.RequiredPredicate;
import crypto.definition.Definitions;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.typestate.ReportingErrorStateNode;
import crypto.typestate.WrappedState;
import crypto.utils.MatcherUtils;
import crysl.rule.CrySLCondPredicate;
import crysl.rule.CrySLForbiddenMethod;
import crysl.rule.CrySLMethod;
import crysl.rule.CrySLObject;
import crysl.rule.CrySLPredicate;
import crysl.rule.CrySLRule;
import crysl.rule.ICrySLPredicateParameter;
import crysl.rule.StateNode;
import crysl.rule.TransitionEdge;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import typestate.TransitionFunction;
import typestate.finiteautomata.ITransition;
import typestate.finiteautomata.State;

public class AnalysisSeedWithSpecification extends IAnalysisSeed {

    private final CrySLRule specification;

    private final ConstraintsAnalysis constraintsAnalysis;
    private boolean internalConstraintsSatisfied;

    private final Multimap<Statement, State> typeStateChange = HashMultimap.create();
    private final Map<ControlFlowGraph.Edge, DeclaredMethod> allCallsOnObject;

    private final Collection<AnalysisSeedWithSpecification> requiringSeeds = new HashSet<>();
    private final Multimap<Statement, Map.Entry<EnsuredPredicate, Integer>> ensuredPredicates =
            HashMultimap.create();
    private final Multimap<Statement, Map.Entry<UnEnsuredPredicate, Integer>> hiddenPredicates =
            HashMultimap.create();
    private final Collection<AbstractPredicate> indirectlyEnsuredPredicates = new HashSet<>();

    public AnalysisSeedWithSpecification(
            CryptoScanner scanner,
            Statement statement,
            Val fact,
            ForwardBoomerangResults<TransitionFunction> results,
            CrySLRule specification) {
        super(scanner, statement, fact, results);

        this.specification = specification;
        this.allCallsOnObject = results.getInvokedMethodOnInstance();

        Definitions.ConstraintsDefinition definition =
                new Definitions.ConstraintsDefinition(
                        scanner.getCallGraph(),
                        scanner.getDataFlowScope(),
                        scanner.getTimeout(),
                        scanner.getSparsificationStrategy(),
                        scanner.getAnalysisReporter());
        this.constraintsAnalysis = new ConstraintsAnalysis(this, definition);
    }

    @Override
    public String toString() {
        return "AnalysisSeedWithSpec ["
                + super.toString()
                + " with spec "
                + specification.getClassName()
                + "]";
    }

    @Override
    public void execute() {
        scanner.getAnalysisReporter().onSeedStarted(this);

        // Check the CONSTRAINTS section
        checkInternalConstraints();

        // Check the FORBIDDEN section
        evaluateForbiddenMethods();

        // Check the ORDER section
        evaluateTypestateOrder();
        evaluateIncompleteOperations();

        scanner.getAnalysisReporter().onSeedFinished(this);
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                Typestate checks                                   *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /** Check the FORBIDDEN section and report corresponding errors */
    private void evaluateForbiddenMethods() {
        for (Map.Entry<ControlFlowGraph.Edge, DeclaredMethod> calledMethod :
                allCallsOnObject.entrySet()) {
            Optional<CrySLForbiddenMethod> forbiddenMethod =
                    isForbiddenMethod(calledMethod.getValue());

            if (forbiddenMethod.isPresent()) {
                Collection<CrySLMethod> alternatives = forbiddenMethod.get().getAlternatives();
                Statement statement = calledMethod.getKey().getStart();
                DeclaredMethod declaredMethod = calledMethod.getValue();

                ForbiddenMethodError error =
                        new ForbiddenMethodError(
                                this, statement, specification, declaredMethod, alternatives);
                this.addError(error);
                scanner.getAnalysisReporter().reportError(this, error);
            }
        }
    }

    private Optional<CrySLForbiddenMethod> isForbiddenMethod(DeclaredMethod declaredMethod) {
        Collection<CrySLForbiddenMethod> forbiddenMethods = specification.getForbiddenMethods();

        for (CrySLForbiddenMethod method : forbiddenMethods) {
            if (MatcherUtils.matchCryslMethodAndDeclaredMethod(
                    method.getMethod(), declaredMethod)) {
                return Optional.of(method);
            }
        }
        return Optional.empty();
    }

    private void evaluateTypestateOrder() {
        Collection<ControlFlowGraph.Edge> allTypestateChangeStatements = new HashSet<>();
        for (Table.Cell<ControlFlowGraph.Edge, Val, TransitionFunction> cell :
                analysisResults.asStatementValWeightTable().cellSet()) {
            Collection<ControlFlowGraph.Edge> edges =
                    cell.getValue().getLastStateChangeStatements();
            allTypestateChangeStatements.addAll(edges);
        }

        for (Table.Cell<ControlFlowGraph.Edge, Val, TransitionFunction> c :
                analysisResults.asStatementValWeightTable().cellSet()) {
            ControlFlowGraph.Edge curr = c.getRowKey();

            // The initial statement is always the start of the CFG edge, all other statements are
            // targets
            Statement typestateChangeStatement;
            if (curr.getStart().equals(getOrigin())) {
                typestateChangeStatement = curr.getStart();
            } else {
                typestateChangeStatement = curr.getTarget();
            }

            if (allTypestateChangeStatements.contains(curr)) {
                Collection<? extends State> targetStates = getTargetStates(c.getValue());

                for (State newStateAtCurr : targetStates) {
                    typeStateChangeAtStatement(typestateChangeStatement, newStateAtCurr);
                }
            }
        }
    }

    private void typeStateChangeAtStatement(Statement statement, State stateNode) {
        if (typeStateChange.put(statement, stateNode)) {
            if (stateNode instanceof ReportingErrorStateNode errorStateNode) {
                TypestateError typestateError =
                        new TypestateError(
                                this, statement, specification, errorStateNode.expectedCalls());
                this.addError(typestateError);
                scanner.getAnalysisReporter().reportError(this, typestateError);
            }
        }
    }

    private void evaluateIncompleteOperations() {
        Table<ControlFlowGraph.Edge, Val, TransitionFunction> endPathOfPropagation =
                analysisResults.getObjectDestructingStatements();
        Map<ControlFlowGraph.Edge, Collection<CrySLMethod>> incompleteOperations = new HashMap<>();

        for (Table.Cell<ControlFlowGraph.Edge, Val, TransitionFunction> c :
                endPathOfPropagation.cellSet()) {
            Collection<CrySLMethod> expectedMethodsToBeCalled = new HashSet<>();

            for (ITransition n : c.getValue().values()) {
                if (n.to() == null) {
                    continue;
                }

                if (n.to().isAccepting()) {
                    continue;
                }

                if (!(n.to() instanceof WrappedState wrappedState)) {
                    continue;
                }

                for (TransitionEdge t : specification.getUsagePattern().getAllTransitions()) {
                    if (t.getLeft().equals(wrappedState.delegate()) && !t.from().equals(t.to())) {
                        Collection<CrySLMethod> labels = t.getLabel();
                        expectedMethodsToBeCalled.addAll(labels);
                    }
                }
            }

            if (!expectedMethodsToBeCalled.isEmpty()) {
                incompleteOperations.put(c.getRowKey(), expectedMethodsToBeCalled);
            }
        }

        // No incomplete operations were found
        if (incompleteOperations.entrySet().isEmpty()) {
            return;
        }

        /* If there is only one incomplete operation, then there is only one dataflow path. Hence,
         * the error can be reported directly.
         */
        if (incompleteOperations.entrySet().size() == 1) {
            Map.Entry<ControlFlowGraph.Edge, Collection<CrySLMethod>> entry =
                    incompleteOperations.entrySet().iterator().next();
            Collection<CrySLMethod> methodsToBeCalled = entry.getValue();
            Statement statement = entry.getKey().getTarget();

            if (statement.isThrowStmt()) {
                return;
            }

            IncompleteOperationError incompleteOperationError =
                    new IncompleteOperationError(this, statement, specification, methodsToBeCalled);
            this.addError(incompleteOperationError);
            scanner.getAnalysisReporter().reportError(this, incompleteOperationError);
        }

        /* Multiple incomplete operations were found. Depending on the dataflow paths, the
         * errors are reported:
         * 1) A dataflow path ends in an accepting state: No error is reported
         * 2) A dataflow path does not end in an accepting state: Report the error on the last used statement on this path
         */
        if (incompleteOperations.size() > 1) {
            for (Map.Entry<ControlFlowGraph.Edge, Collection<CrySLMethod>> entry :
                    incompleteOperations.entrySet()) {
                Collection<CrySLMethod> expectedMethodsToBeCalled = entry.getValue();
                Statement statement = entry.getKey().getTarget();

                if (statement.isThrowStmt()) {
                    continue;
                }

                if (!statement.containsInvokeExpr()) {
                    continue;
                }

                // Only if the path does not end in an accepting state, the error should be reported
                DeclaredMethod declaredMethod = statement.getInvokeExpr().getMethod();
                if (isMethodToAcceptingState(declaredMethod)) {
                    continue;
                }

                IncompleteOperationError incompleteOperationError =
                        new IncompleteOperationError(
                                this, statement, specification, expectedMethodsToBeCalled, true);
                this.addError(incompleteOperationError);
                scanner.getAnalysisReporter().reportError(this, incompleteOperationError);
            }
        }
    }

    private boolean isMethodToAcceptingState(DeclaredMethod method) {
        Collection<TransitionEdge> transitions =
                specification.getUsagePattern().getAllTransitions();

        for (TransitionEdge edge : transitions) {
            if (edge.getLabel().stream()
                    .noneMatch(e -> MatcherUtils.matchCryslMethodAndDeclaredMethod(e, method))) {
                continue;
            }

            if (edge.to().getAccepting()) {
                return true;
            }
        }

        return false;
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                               Predicate checks                                    *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    public void expectPredicate(
            Statement statement, CrySLPredicate predicate, IAnalysisSeed seed, int paramIndex) {
        CrySLPredicate expectedPred;
        if (predicate.isNegated()) {
            expectedPred = predicate.invertNegation();
        } else {
            expectedPred = predicate;
        }
        expectedPredicates.put(
                statement, new ExpectedPredicateOnSeed(expectedPred, seed, paramIndex));
    }

    public void addRequiringSeed(AnalysisSeedWithSpecification seed) {
        requiringSeeds.add(seed);
    }

    public Collection<AnalysisSeedWithSpecification> getRequiringSeeds() {
        return requiringSeeds;
    }

    public void computeExpectedPredicates(Collection<IAnalysisSeed> seeds) {
        initializeDependantSeedsFromRequiredPredicates(seeds);
        initializeDependantSeedsFromEnsuringPredicates(seeds);
    }

    private void initializeDependantSeedsFromRequiredPredicates(Collection<IAnalysisSeed> seeds) {
        Multimap<Statement, Map.Entry<CrySLPredicate, Integer>> reqPreds = HashMultimap.create();
        for (RequiredPredicate reqPred : constraintsAnalysis.getRequiredPredicates()) {
            Map.Entry<CrySLPredicate, Integer> entry =
                    new AbstractMap.SimpleEntry<>(reqPred.predicate(), reqPred.index());
            reqPreds.put(reqPred.statement(), entry);
        }
        /*for (ISLConstraint constraint : constraintSolver.getRequiredPredicates()) {
            if (constraint instanceof RequiredCrySLPredicate reqPred) {
                Map.Entry<CrySLPredicate, Integer> entry =
                        new AbstractMap.SimpleEntry<>(reqPred.getPred(), reqPred.getParamIndex());
                reqPreds.put(reqPred.getLocation(), entry);
            } else if (constraint instanceof AlternativeReqPredicate altPred) {
                for (RequiredCrySLPredicate reqPred : altPred.getRelAlternatives()) {
                    CrySLPredicate predicate = reqPred.getPred();

                    Map.Entry<CrySLPredicate, Integer> entry =
                            new AbstractMap.SimpleEntry<>(predicate, reqPred.getParamIndex());
                    reqPreds.put(reqPred.getLocation(), entry);
                }
            }
        }*/

        for (Statement statement : reqPreds.keySet()) {
            if (!statement.containsInvokeExpr()) {
                continue;
            }

            InvokeExpr invokeExpr = statement.getInvokeExpr();

            Collection<Map.Entry<CrySLPredicate, Integer>> preds = reqPreds.get(statement);
            for (Map.Entry<CrySLPredicate, Integer> entry : preds) {
                CrySLPredicate predicate = entry.getKey();
                int paramIndex = entry.getValue();

                if (paramIndex == -1) {
                    if (!statement.isAssign() || invokeExpr.isStaticInvokeExpr()) {
                        continue;
                    }

                    Val base = invokeExpr.getBase();

                    Collection<IAnalysisSeed> requiredSeeds =
                            computeRequiredSeeds(statement, base, seeds);
                    for (IAnalysisSeed seed : requiredSeeds) {
                        seed.expectPredicate(
                                statement, predicate.toNormalCrySLPredicate(), this, paramIndex);

                        if (seed instanceof AnalysisSeedWithSpecification) {
                            ((AnalysisSeedWithSpecification) seed).addRequiringSeed(this);
                        }
                    }
                } else {
                    Val param = invokeExpr.getArg(paramIndex);

                    Collection<IAnalysisSeed> requiredSeeds =
                            computeRequiredSeeds(statement, param, seeds);
                    for (IAnalysisSeed seed : requiredSeeds) {
                        seed.expectPredicate(
                                statement, predicate.toNormalCrySLPredicate(), this, paramIndex);

                        if (seed instanceof AnalysisSeedWithSpecification) {
                            ((AnalysisSeedWithSpecification) seed).addRequiringSeed(this);
                        }
                    }
                }
            }
        }
    }

    private Collection<IAnalysisSeed> computeRequiredSeeds(
            Statement statement, Val val, Collection<IAnalysisSeed> seeds) {
        Collection<IAnalysisSeed> result = new HashSet<>();

        for (IAnalysisSeed seed : seeds) {
            for (Statement successor :
                    statement.getMethod().getControlFlowGraph().getSuccsOf(statement)) {
                ControlFlowGraph.Edge edge = new ControlFlowGraph.Edge(statement, successor);

                Collection<Val> values =
                        seed.getAnalysisResults().asStatementValWeightTable().row(edge).keySet();
                if (values.contains(val)) {
                    result.add(seed);
                }
            }
        }

        return result;
    }

    public boolean canEnsurePredicate(
            CrySLPredicate predicate, Statement statement, int paramIndex) {
        DeclaredMethod declaredMethod = statement.getInvokeExpr().getMethod();

        Collection<CrySLMethod> methods =
                MatcherUtils.getMatchingCryslMethodsToDeclaredMethod(specification, declaredMethod);
        for (CrySLMethod method : methods) {
            String param;
            if (paramIndex == -1) {
                param = method.getRetObject().getKey();
            } else {
                param = method.getParameters().get(paramIndex).getKey();
            }

            for (CrySLPredicate ensPred : specification.getPredicates()) {
                if (!predicate.equals(ensPred)) {
                    continue;
                }

                if (isPredicateRelevantValue(param, ensPred)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void initializeDependantSeedsFromEnsuringPredicates(Collection<IAnalysisSeed> seeds) {
        for (CrySLPredicate predicate : specification.getPredicates()) {
            for (Map.Entry<ControlFlowGraph.Edge, DeclaredMethod> entry :
                    allCallsOnObject.entrySet()) {
                Statement statement = entry.getKey().getStart();
                DeclaredMethod declaredMethod = entry.getValue();

                if (!statement.containsInvokeExpr()) {
                    continue;
                }

                if (hasThisParameter(predicate)) {
                    this.expectPredicate(statement, predicate.toNormalCrySLPredicate(), this, -1);
                }

                Collection<CrySLMethod> methods =
                        MatcherUtils.getMatchingCryslMethodsToDeclaredMethod(
                                specification, declaredMethod);

                for (CrySLMethod method : methods) {
                    if (isPredicateGeneratingAssignStatement(predicate, statement, method)) {
                        Val leftOp = statement.getLeftOp();
                        Val rightOp = statement.getRightOp();

                        AllocVal allocVal = new AllocVal(leftOp, statement, rightOp);

                        Collection<IAnalysisSeed> dependentAssignSeeds =
                                computeGeneratedAssignSeeds(statement, allocVal, seeds);
                        for (IAnalysisSeed seed : dependentAssignSeeds) {
                            this.expectPredicate(
                                    statement, predicate.toNormalCrySLPredicate(), seed, -1);
                        }
                    }

                    for (int i = 0; i < method.getParameters().size(); i++) {
                        Map.Entry<String, String> param = method.getParameters().get(i);

                        if (isPredicateRelevantValue(param.getKey(), predicate)) {
                            Val paramVal = statement.getInvokeExpr().getArg(i);

                            Collection<IAnalysisSeed> dependantParamSeeds =
                                    computeGeneratedParameterSeeds(entry.getKey(), paramVal, seeds);
                            for (IAnalysisSeed seed : dependantParamSeeds) {
                                this.expectPredicate(
                                        statement, predicate.toNormalCrySLPredicate(), seed, i);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean hasThisParameter(CrySLPredicate predicate) {
        for (ICrySLPredicateParameter parameter : predicate.getParameters()) {
            if (parameter.getName().equals("this")) {
                return true;
            }
        }
        return false;
    }

    private boolean isPredicateGeneratingAssignStatement(
            CrySLPredicate predicate, Statement statement, CrySLMethod method) {
        if (!statement.isAssign()) {
            return false;
        }

        Map.Entry<String, String> retObject = method.getRetObject();

        if (retObject.getKey().equals(CrySLMethod.NO_NAME)) {
            return false;
        }

        return isPredicateRelevantValue(retObject.getKey(), predicate);
    }

    private boolean isPredicateRelevantValue(String key, CrySLPredicate predicate) {
        for (ICrySLPredicateParameter param : predicate.getParameters()) {
            if (key.equals(param.getName())) {
                return true;
            }
        }
        return false;
    }

    private Collection<IAnalysisSeed> computeGeneratedAssignSeeds(
            Statement statement, Val fact, Collection<IAnalysisSeed> seeds) {
        Collection<IAnalysisSeed> result = new HashSet<>();

        for (IAnalysisSeed seed : seeds) {
            if (seed.getOrigin().equals(statement) && seed.getFact().equals(fact)) {
                result.add(seed);
            }
        }

        return result;
    }

    private Collection<IAnalysisSeed> computeGeneratedParameterSeeds(
            ControlFlowGraph.Edge edge, Val fact, Collection<IAnalysisSeed> seeds) {
        Collection<IAnalysisSeed> result = new HashSet<>();

        for (IAnalysisSeed seed : seeds) {
            Collection<Val> values =
                    seed.getAnalysisResults().asStatementValWeightTable().row(edge).keySet();

            if (values.contains(fact)) {
                result.add(seed);
            }
        }

        return result;
    }

    /**
     * Ensure the predicates from the ENSURES section and transfer them to this seed or other seeds.
     * If there are no violations for the rule, propagate an {@link EnsuredPredicate}. Otherwise, if
     * there is at least one violation, collect them and propagate a corresponding {@link
     * UnEnsuredPredicate}.
     */
    public void ensurePredicates() {
        // Check whether all constraints from the CONSTRAINTS and REQUIRES section is satisfied
        boolean satisfiesConstraintSystem = isConstraintSystemSatisfied();

        Collection<Statement> expectedPredStatements = expectedPredicates.keySet();

        // TODO Check for relevant predicates?
        Collection<CrySLPredicate> predsToBeEnsured = new HashSet<>(specification.getPredicates());
        for (AbstractPredicate predicate : indirectlyEnsuredPredicates) {
            predsToBeEnsured.add(predicate.getPredicate().toNormalCrySLPredicate());
        }

        for (CrySLPredicate predToBeEnsured : predsToBeEnsured) {
            Collection<UnEnsuredPredicate.Violations> violations = new HashSet<>();

            // Check whether there is a ForbiddenMethodError from previous checks
            if (errorCollection.stream().anyMatch(e -> e instanceof ForbiddenMethodError)) {
                violations.add(UnEnsuredPredicate.Violations.CallToForbiddenMethod);
            }

            if (!satisfiesConstraintSystem) {
                violations.add(UnEnsuredPredicate.Violations.ConstraintsAreNotSatisfied);
            }

            // Check whether there is a predicate condition and whether it is satisfied
            if (predToBeEnsured.getConstraint().isPresent()
                    && constraintsAnalysis.isPredConditionViolated(predToBeEnsured)) {
                violations.add(UnEnsuredPredicate.Violations.ConditionIsNotSatisfied);
            }

            for (Statement statement : expectedPredStatements) {
                Collection<CrySLPredicate> expectedPreds = expectedPredicatesAtStatement(statement);
                if (!expectedPreds.contains(predToBeEnsured.toNormalCrySLPredicate())) {
                    continue;
                }

                /* Check for all states whether an accepting state is reached:
                 * 1) All states are accepting -> Predicate is generated
                 * 2) No state is accepting -> Predicate is definitely not generated
                 * 3) There are generating and non-generating states -> At least one
                 *    dataflow path leads to a non-generating state s.t. the predicate
                 *    is not generated (over approximation)
                 */
                Collection<State> states = getStatesAtStatement(statement);
                boolean allStatesNonGenerating =
                        states.stream()
                                .noneMatch(s -> doesStateGeneratePredicate(s, predToBeEnsured));
                boolean someStatesNonGenerating =
                        states.stream()
                                .anyMatch(s -> !doesStateGeneratePredicate(s, predToBeEnsured));

                Collection<UnEnsuredPredicate.Violations> allViolations = new HashSet<>(violations);
                if (allStatesNonGenerating) {
                    allViolations.add(UnEnsuredPredicate.Violations.GeneratingStateIsNeverReached);
                } else if (someStatesNonGenerating) {
                    /* TODO
                     *  Due to a bug, IDEal returns the states [0,1] whenever there is a
                     *  single call to a method, e.g. Object o = new Object(); o.m();. After
                     *  the call to m1(), o is always in state 0 and 1, although it should only be 1
                     */
                    // allViolations.add(UnEnsuredPredicate.Violations.GeneratingStateMayNotBeReached);
                }

                AbstractPredicate generatedPred;
                if (!allViolations.isEmpty()) {
                    generatedPred =
                            new UnEnsuredPredicate(
                                    predToBeEnsured.toNormalCrySLPredicate(),
                                    // constraintSolver.getCollectedValues(),
                                    new HashSet<>(),
                                    this,
                                    allViolations);
                } else {
                    generatedPred =
                            new EnsuredPredicate(
                                    predToBeEnsured.toNormalCrySLPredicate(),
                                    // constraintSolver.getCollectedValues()
                                    new HashSet<>());
                }

                ensurePredicateAtStatement(generatedPred, statement);
            }
        }

        scanner.getAnalysisReporter().ensuredPredicates(this, ensuredPredicates);
    }

    private boolean doesStateGeneratePredicate(State state, CrySLPredicate predicate) {
        return isPredicateGeneratingState(predicate, state)
                && !isPredicateNegatingState(predicate, state);
    }

    private void ensurePredicateAtStatement(AbstractPredicate ensPred, Statement statement) {
        if (hasThisParameter(ensPred.getPredicate())) {
            this.addEnsuredPredicate(ensPred, statement, -1);
            scanner.getAnalysisReporter().onGeneratedPredicate(this, ensPred, this, statement);
        }

        if (!expectedPredicates.containsKey(statement)) {
            return;
        }

        Collection<ExpectedPredicateOnSeed> expectedPredsAtStatement =
                expectedPredicates.get(statement);
        for (ExpectedPredicateOnSeed expectedPredicateOnSeed : expectedPredsAtStatement) {
            CrySLPredicate predicate = expectedPredicateOnSeed.predicate();
            IAnalysisSeed seed = expectedPredicateOnSeed.seed();
            int paramIndex = expectedPredicateOnSeed.paramIndex();

            if (predicate.equals(ensPred.getPredicate())) {
                if (seed instanceof AnalysisSeedWithSpecification seedWithSpec) {
                    seedWithSpec.addEnsuredPredicateFromOtherRule(ensPred, statement, paramIndex);
                    scanner.getAnalysisReporter()
                            .onGeneratedPredicate(this, ensPred, seedWithSpec, statement);
                } else if (seed instanceof AnalysisSeedWithEnsuredPredicate seedWithoutSpec) {
                    seedWithoutSpec.addEnsuredPredicate(ensPred);
                    scanner.getAnalysisReporter()
                            .onGeneratedPredicate(this, ensPred, seedWithoutSpec, statement);
                }
            }
        }
    }

    private void addEnsuredPredicateFromOtherRule(
            AbstractPredicate pred, Statement statement, int paramIndex) {
        addEnsuredPredicate(pred, statement, paramIndex);
        indirectlyEnsuredPredicates.add(pred);
    }

    private Collection<State> getStatesAtStatement(Statement statement) {
        Collection<State> states = new HashSet<>();

        if (typeStateChange.containsKey(statement)) {
            states.addAll(typeStateChange.get(statement));
        }

        if (statement.containsInvokeExpr()) {
            InvokeExpr invokeExpr = statement.getInvokeExpr();

            for (Statement pred :
                    statement.getMethod().getControlFlowGraph().getPredsOf(statement)) {
                ControlFlowGraph.Edge edge = new ControlFlowGraph.Edge(pred, statement);

                Map<Val, TransitionFunction> rows =
                        analysisResults.asStatementValWeightTable().row(edge);
                Collection<Val> values = rows.keySet();

                for (Val arg : invokeExpr.getArgs()) {
                    if (values.contains(arg)) {
                        TransitionFunction transition = rows.get(arg);
                        Collection<State> targetStates = getTargetStates(transition);

                        states.addAll(targetStates);
                    }
                }
            }
        }

        return states;
    }

    public void addEnsuredPredicate(
            AbstractPredicate ensPred, Statement statement, int paramIndex) {
        if (ensPred instanceof UnEnsuredPredicate unEnsuredPredicate) {
            Map.Entry<UnEnsuredPredicate, Integer> predAtIndex =
                    new AbstractMap.SimpleEntry<>(unEnsuredPredicate, paramIndex);
            hiddenPredicates.put(statement, predAtIndex);
        } else if (ensPred instanceof EnsuredPredicate ensuredPredicate) {
            Map.Entry<EnsuredPredicate, Integer> predAtIndex =
                    new AbstractMap.SimpleEntry<>(ensuredPredicate, paramIndex);
            ensuredPredicates.put(statement, predAtIndex);
        }
    }

    private Collection<State> getTargetStates(TransitionFunction value) {
        Collection<State> res = Sets.newHashSet();
        for (ITransition t : value.values()) {
            if (t.to() != null) res.add(t.to());
        }
        return res;
    }

    private boolean isPredicateGeneratingState(CrySLPredicate ensPred, State stateNode) {
        // Predicate has a condition, i.e. "after" is specified -> Active predicate for
        // corresponding states
        if (ensPred instanceof CrySLCondPredicate
                && isConditionalState(
                        ((CrySLCondPredicate) ensPred).getConditionalMethods(), stateNode)) {
            return true;
        }

        // If there is no condition, the predicate is activated for all accepting states
        if (!(ensPred instanceof CrySLCondPredicate) && stateNode.isAccepting()) {
            return true;
        }

        return false;
    }

    private boolean isConditionalState(Collection<StateNode> conditionalMethods, State state) {
        if (conditionalMethods == null) return false;
        for (StateNode s : conditionalMethods) {
            if (WrappedState.of(s).equals(state)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPredicateNegatingState(CrySLPredicate ensPred, State stateNode) {
        // Check, whether the predicate is negated in the given state
        for (CrySLPredicate negPred : specification.getNegatedPredicates()) {
            // Compare names
            if (!ensPred.getPredName().equals(negPred.getPredName())) {
                continue;
            }

            // Compare parameters
            if (!doParametersMatch(ensPred, negPred)) {
                continue;
            }

            // Negated predicate does not have a condition, i.e. no "after" -> predicate is negated
            // in all states
            if (!(negPred instanceof CrySLCondPredicate condNegPred)) {
                return true;
            }

            for (StateNode s : condNegPred.getConditionalMethods()) {
                if (WrappedState.of(s).equals(stateNode)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean doParametersMatch(CrySLPredicate ensPred, CrySLPredicate negPred) {
        // Compare number of parameters
        if (!(ensPred.getParameters().size() == negPred.getParameters().size())) {
            return false;
        }

        // Compare type of parameters pairwise
        for (int i = 0; i < ensPred.getParameters().size(); i++) {
            CrySLObject ensParameter = (CrySLObject) ensPred.getParameters().get(i);
            CrySLObject negParameter = (CrySLObject) negPred.getParameters().get(i);

            // If "_" is used as a parameter, the type is arbitrary
            if (ensParameter.getJavaType().equals("null")
                    || negParameter.getJavaType().equals("null")) {
                continue;
            }

            if (!ensParameter.getJavaType().equals(negParameter.getJavaType())) {
                return false;
            }
        }

        return true;
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                Constraint checks                                  *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /** Check the constraints from the CONSTRAINTS section */
    private void checkInternalConstraints() {
        scanner.getAnalysisReporter().beforeConstraintsCheck(this);

        constraintsAnalysis.initialize();
        Collection<AbstractConstraintsError> violatedConstraints =
                constraintsAnalysis.evaluateConstraints();
        this.internalConstraintsSatisfied = violatedConstraints.isEmpty();

        for (AbstractConstraintsError error : violatedConstraints) {
            this.addError(error);
            scanner.getAnalysisReporter().reportError(this, error);
        }

        scanner.getAnalysisReporter().afterConstraintsCheck(this, violatedConstraints.size());
    }

    /**
     * Check, whether the internal constraints and predicate constraints are satisfied. Requires a
     * previous call to {@link #checkInternalConstraints()}
     *
     * @return true if all internal and required predicate constraints are satisfied
     */
    private boolean isConstraintSystemSatisfied() {
        Collection<AbstractConstraintsError> violatedPredicates =
                constraintsAnalysis.evaluateRequiredPredicates();
        for (AbstractConstraintsError error : violatedPredicates) {
            addError(error);
            scanner.getAnalysisReporter().reportError(this, error);
        }
        return internalConstraintsSatisfied && violatedPredicates.isEmpty();
    }

    private boolean doPredsMatch(CrySLPredicate pred, AbstractPredicate ensPred) {
        boolean requiredPredicatesExist = true;
        for (int i = 0; i < pred.getParameters().size(); i++) {
            String var = pred.getParameters().get(i).getName();
            if (isOfNonTrackableType(var)) {
                continue;
            }

            if (pred.getInvolvedVarNames().contains(var)) {
                final String parameterI = ensPred.getPredicate().getParameters().get(i).getName();
                Collection<String> actVals = Collections.emptySet();
                Collection<String> expVals = Collections.emptySet();

                for (CallSiteWithExtractedValue callSite : ensPred.getParametersToValues()) {
                    if (callSite.callSiteWithParam().varName().equals(parameterI)) {
                        actVals = retrieveValueFromUnit(callSite);
                    }
                }
                /*for (CallSiteWithExtractedValue callSite : constraintSolver.getCollectedValues()) {
                    if (callSite.callSiteWithParam().varName().equals(var)) {
                        expVals = retrieveValueFromUnit(callSite);
                    }
                }*/

                String splitter = "";
                int index = -1;
                if (pred.getParameters().get(i) instanceof CrySLObject obj) {
                    if (obj.getSplitter() != null) {
                        splitter = obj.getSplitter().getSplitter();
                        index = obj.getSplitter().getIndex();
                    }
                }
                for (String foundVal : expVals) {
                    if (index > -1) {
                        foundVal = foundVal.split(splitter)[index];
                    }
                    actVals =
                            actVals.parallelStream()
                                    .map(String::toLowerCase)
                                    .collect(Collectors.toList());
                    requiredPredicatesExist &= actVals.contains(foundVal.toLowerCase());
                }
            } else {
                requiredPredicatesExist = false;
            }
        }
        return requiredPredicatesExist;
    }

    private Collection<String> retrieveValueFromUnit(CallSiteWithExtractedValue callSite) {
        Collection<String> values = new ArrayList<>();
        Statement statement = callSite.callSiteWithParam().statement();

        if (statement.isAssign()) {
            Val rightSide = statement.getRightOp();

            if (rightSide.isIntConstant()) {
                values.add(String.valueOf(rightSide.getIntValue()));
            } else if (rightSide.isLongConstant()) {
                values.add(String.valueOf(rightSide.getLongValue()));
            } else if (rightSide.isStringConstant()) {
                values.add(rightSide.getStringValue());
            }
        }
        return values;
    }

    private static final Collection<String> trackedTypes =
            Arrays.asList("java.lang.String", "int", "java.lang.Integer");

    private boolean isOfNonTrackableType(String varName) {
        for (Map.Entry<String, String> object : specification.getObjects()) {
            if (object.getKey().equals(varName) && trackedTypes.contains(object.getValue())) {
                return false;
            }
        }
        return true;
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                               Additional methods	                                 *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public CrySLRule getSpecification() {
        return specification;
    }

    public Map<ControlFlowGraph.Edge, DeclaredMethod> getAllCallsOnObject() {
        return allCallsOnObject;
    }

    public Collection<Map.Entry<EnsuredPredicate, Integer>> getEnsuredPredicatesAtStatement(
            Statement statement) {
        return ensuredPredicates.get(statement);
    }

    public Collection<Map.Entry<UnEnsuredPredicate, Integer>> getUnEnsuredPredicatesAtStatement(
            Statement statement) {
        return hiddenPredicates.get(statement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), specification);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && obj instanceof AnalysisSeedWithSpecification other
                && Objects.equals(specification, other.specification);
    }
}
