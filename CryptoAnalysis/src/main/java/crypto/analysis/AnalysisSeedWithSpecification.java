/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.analysis;

import boomerang.results.ForwardBoomerangResults;
import boomerang.scope.DeclaredMethod;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import boomerang.utils.MethodWrapper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import crypto.analysis.errors.AbstractConstraintsError;
import crypto.analysis.errors.ForbiddenMethodError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.TypestateError;
import crypto.constraints.ConstraintsAnalysis;
import crypto.constraints.RequiredPredicate;
import crypto.definition.Definitions;
import crypto.predicates.AbstractPredicate;
import crypto.predicates.EnsuredPredicate;
import crypto.predicates.ExpectedPredicate;
import crypto.predicates.UnEnsuredPredicate;
import crypto.typestate.ReportingErrorStateNode;
import crypto.typestate.WrappedState;
import crypto.utils.MatcherUtils;
import crysl.rule.CrySLCondPredicate;
import crysl.rule.CrySLForbiddenMethod;
import crysl.rule.CrySLMethod;
import crysl.rule.CrySLObject;
import crysl.rule.CrySLPredicate;
import crysl.rule.CrySLRule;
import crysl.rule.StateNode;
import crysl.rule.TransitionEdge;
import de.fraunhofer.iem.cryptoanalysis.scope.CryptoAnalysisScope;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import typestate.StatementSequence;
import typestate.TransitionFunction;
import typestate.finiteautomata.State;
import typestate.finiteautomata.Transition;

public class AnalysisSeedWithSpecification extends IAnalysisSeed {

    private final CrySLRule specification;

    private final ConstraintsAnalysis constraintsAnalysis;
    private boolean internalConstraintsSatisfied;

    private final Collection<Statement> allCallsOnObject;
    private final Multimap<Statement, Integer> relevantStatements = HashMultimap.create();
    private final Collection<AbstractPredicate> indirectlyEnsuredPredicates = new HashSet<>();

    public AnalysisSeedWithSpecification(
            CryptoScanner scanner,
            Statement statement,
            Val fact,
            CryptoAnalysisScope scope,
            ForwardBoomerangResults<TransitionFunction> results,
            CrySLRule specification) {
        super(scanner, statement, fact, results);

        this.specification = specification;
        this.allCallsOnObject = results.getInvokeStatementsOnInstance();

        Definitions.ConstraintsDefinition definition =
                new Definitions.ConstraintsDefinition(
                        scope,
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
        for (Statement statement : allCallsOnObject) {
            if (!statement.containsInvokeExpr()) {
                continue;
            }

            DeclaredMethod declaredMethod = statement.getInvokeExpr().getDeclaredMethod();
            Optional<CrySLForbiddenMethod> forbiddenMethod = isForbiddenMethod(declaredMethod);

            if (forbiddenMethod.isPresent()) {
                Collection<CrySLMethod> alternatives = forbiddenMethod.get().getAlternatives();

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

    /**
     * Check the ORDER section and report a corresponding {@link TypestateError} for each sequence
     * of statements that visits a {@link ReportingErrorStateNode}
     */
    private void evaluateTypestateOrder() {
        for (Statement statement : allCallsOnObject) {
            Collection<State> statesAtStatement = getStatesAtStatement(statement);
            for (State state : statesAtStatement) {
                if (state instanceof ReportingErrorStateNode errorStateNode) {
                    TypestateError typestateError =
                            new TypestateError(
                                    this, statement, specification, errorStateNode.expectedCalls());
                    this.addError(typestateError);
                    scanner.getAnalysisReporter().reportError(this, typestateError);
                }
            }
        }
    }

    /**
     * Check the ORDER section and report a corresponding {@link IncompleteOperationError} for each
     * sequence of statements that does not end in an accepting state
     */
    private void evaluateIncompleteOperations() {
        Multimap<Statement, CrySLMethod> incompleteOperations = HashMultimap.create();

        Table<Statement, Val, TransitionFunction> weights = analysisResults.computeFinalWeights();
        for (TransitionFunction weight : weights.values()) {
            for (Transition transition : weight.getStateChangeSequences().keySet()) {
                State targetState = transition.to();

                if (targetState.isAccepting()) {
                    continue;
                }

                if (targetState instanceof WrappedState wrappedState) {
                    for (TransitionEdge t : specification.getUsagePattern().getAllTransitions()) {
                        if (t.from().equals(t.to())) {
                            continue;
                        }

                        if (t.getLeft().equals(wrappedState.delegate())) {
                            Collection<CrySLMethod> labels = t.getLabel();

                            Collection<StatementSequence> stmtSequences =
                                    weight.getStateChangeSequences().get(transition);
                            Collection<Statement> lastStatements =
                                    stmtSequences.stream()
                                            .map(
                                                    e ->
                                                            e.getSequence()
                                                                    .get(e.getSequence().size() - 1)
                                                                    .getStatement())
                                            .toList();

                            for (Statement stmt : lastStatements) {
                                incompleteOperations.putAll(stmt, labels);
                            }
                        }
                    }
                }
            }
        }

        // No incomplete operations were found
        if (incompleteOperations.keySet().isEmpty()) {
            return;
        }

        /* If there is only one incomplete operation, then there is only one dataflow path. Hence,
         * the error can be reported directly.
         */
        if (incompleteOperations.keySet().size() == 1) {
            Statement statement = incompleteOperations.keySet().iterator().next();
            Collection<CrySLMethod> methodsToBeCalled = incompleteOperations.get(statement);

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
        if (incompleteOperations.keySet().size() > 1) {
            for (Statement statement : incompleteOperations.keySet()) {
                Collection<CrySLMethod> expectedMethodsToBeCalled =
                        incompleteOperations.get(statement);

                if (!statement.containsInvokeExpr()) {
                    continue;
                }

                // Only if the path does not end in an accepting state, the error should be reported
                DeclaredMethod declaredMethod = statement.getInvokeExpr().getDeclaredMethod();
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
    public void beforePredicateChecks(Collection<IAnalysisSeed> seeds) {
        for (Statement statement : getInvokedMethodStatements()) {
            relevantStatements.put(statement, -1);
        }

        for (Statement statement : statementValWeightTable.rowKeySet()) {
            if (!statement.containsInvokeExpr()) {
                continue;
            }

            InvokeExpr invokeExpr = statement.getInvokeExpr();
            Collection<Val> values = getAliasesAtStatement(statement);

            for (int i = 0; i < invokeExpr.getArgs().size(); i++) {
                Val param = invokeExpr.getArg(i);

                if (values.contains(param)) {
                    relevantStatements.put(statement, i);
                }
            }
        }

        Collection<RequiredPredicate> reqPreds = constraintsAnalysis.getRequiredPredicates();

        for (RequiredPredicate reqPred : reqPreds) {
            Statement statement = reqPred.statement();

            if (!statement.containsInvokeExpr()) {
                continue;
            }

            InvokeExpr invokeExpr = statement.getInvokeExpr();
            int paramIndex = reqPred.index();

            if (paramIndex == -1) {
                if (!statement.isAssignStmt() || invokeExpr.isStaticInvokeExpr()) {
                    continue;
                }

                Val base = invokeExpr.getBase();

                Collection<IAnalysisSeed> requiredSeeds = getSeedsForVal(statement, base, seeds);
                for (IAnalysisSeed seed : requiredSeeds) {
                    CrySLPredicate predicate = formatCrySLPredicate(reqPred.predicate());
                    ExpectedPredicate expectedPredicate =
                            new ExpectedPredicate(this, predicate, statement, paramIndex);

                    seed.registerExpectedPredicate(expectedPredicate);
                }
            } else {
                Val param = statement.getInvokeExpr().getArg(paramIndex);

                Collection<IAnalysisSeed> requiredSeeds = getSeedsForVal(statement, param, seeds);
                for (IAnalysisSeed seed : requiredSeeds) {
                    CrySLPredicate predicate = formatCrySLPredicate(reqPred.predicate());
                    ExpectedPredicate expectedPredicate =
                            new ExpectedPredicate(this, predicate, statement, paramIndex);

                    seed.registerExpectedPredicate(expectedPredicate);
                }
            }
        }
    }

    private Collection<IAnalysisSeed> getSeedsForVal(
            Statement statement, Val val, Collection<IAnalysisSeed> seeds) {
        Collection<IAnalysisSeed> result = new HashSet<>();

        for (IAnalysisSeed seed : seeds) {
            Collection<Val> values = seed.getAliasesAtStatement(statement);

            if (values.contains(val)) {
                result.add(seed);
            }
        }

        return result;
    }

    private CrySLPredicate formatCrySLPredicate(CrySLPredicate predicate) {
        if (predicate.isNegated()) {
            return predicate.invertNegation().toNormalCrySLPredicate();
        } else {
            return predicate.toNormalCrySLPredicate();
        }
    }

    @Override
    public void propagatePredicates() {
        // Check whether all constraints from the CONSTRAINTS and REQUIRES section is satisfied
        boolean satisfiesConstraintSystem = isConstraintSystemSatisfied();

        Collection<CrySLPredicate> predsToBeEnsured = specification.getPredicates();
        for (CrySLPredicate predToBeEnsured : predsToBeEnsured) {
            propagatePredicate(predToBeEnsured, satisfiesConstraintSystem);
        }

        for (AbstractPredicate indirectPred : indirectlyEnsuredPredicates) {
            propagatePredicate(indirectPred.getPredicate(), satisfiesConstraintSystem, true);
        }
    }

    private void propagatePredicate(CrySLPredicate predicate, boolean satisfiesConstraintSystem) {
        propagatePredicate(predicate, satisfiesConstraintSystem, false);
    }

    private void propagatePredicate(
            CrySLPredicate predicate,
            boolean satisfiesConstraintSystem,
            boolean isIndirectlyEnsured) {
        Collection<UnEnsuredPredicate.Violations> violations = new HashSet<>();

        // Check whether there is a ForbiddenMethodError from previous checks
        if (errorCollection.stream().anyMatch(e -> e instanceof ForbiddenMethodError)) {
            violations.add(UnEnsuredPredicate.Violations.CallToForbiddenMethod);
        }

        if (!satisfiesConstraintSystem) {
            // violations.add(UnEnsuredPredicate.Violations.ConstraintsAreNotSatisfied);
        }

        // Check whether there is a predicate condition and whether it is satisfied
        if (constraintsAnalysis.isPredConditionViolated(predicate)) {
            violations.add(UnEnsuredPredicate.Violations.ConditionIsNotSatisfied);
        }

        for (Statement statement : relevantStatements.keySet()) {
            Collection<UnEnsuredPredicate.Violations> allViolations = new HashSet<>(violations);

            /* Check whether there is a constraint violation on a statement up until the current statement.
             * This includes the statements that have been called until the current statement. For example,
             * we have a sequence Con -> A -> B -> C and the current statement is B, then we evaluate the
             * constraints only on the statements Con, A, B because C has not been called yet
             */
            Collection<Statement> callsAtStatement = getCallsAtStatement(statement);
            Collection<AbstractConstraintsError> consErrors =
                    constraintsAnalysis.evaluateConstraints(callsAtStatement);
            Collection<AbstractConstraintsError> predErrors =
                    constraintsAnalysis.evaluateRequiredPredicates(callsAtStatement);

            if (!consErrors.isEmpty() || !predErrors.isEmpty()) {
                allViolations.add(UnEnsuredPredicate.Violations.ConstraintsAreNotSatisfied);
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
                    states.stream().noneMatch(s -> doesStateGeneratePredicate(s, predicate));
            boolean someStatesNonGenerating =
                    states.stream().anyMatch(s -> !doesStateGeneratePredicate(s, predicate));

            if (allStatesNonGenerating) {
                allViolations.add(UnEnsuredPredicate.Violations.GeneratingStateIsNeverReached);
            } else if (someStatesNonGenerating) {
                allViolations.add(UnEnsuredPredicate.Violations.GeneratingStateMayNotBeReached);
            }

            /* To ensure a predicate with an 'after' condition, we have to make sure that there is
             * no sequence of calls that does not contain an event from the 'after' event. If there
             * is a sequence, we cannot ensure the predicate (over approximation)
             */
            if (predicate instanceof CrySLCondPredicate condPredicate) {
                boolean generatingEventMissing =
                        isGeneratingEventMissing(statement, condPredicate.getConditionalEvents());

                if (generatingEventMissing) {
                    allViolations.add(UnEnsuredPredicate.Violations.GeneratingEventIsNotCalled);
                }
            }

            if (isIndirectlyEnsured) {
                propagateIndirectlyEnsuredPredicate(
                        predicate.toNormalCrySLPredicate(), statement, allViolations);
            } else {
                propagateEnsuredPredicate(
                        predicate.toNormalCrySLPredicate(), statement, allViolations);
            }
        }
    }

    private Collection<Statement> getCallsAtStatement(Statement statement) {
        Collection<TransitionFunction> weights = statementValWeightTable.row(statement).values();

        Collection<Statement> calls = new HashSet<>();
        for (TransitionFunction weight : weights) {
            Collection<StatementSequence> sequences = weight.getStateChangeSequences().values();

            for (StatementSequence sequence : sequences) {
                Collection<Statement> statements = sanitizeStatements(sequence);

                calls.addAll(statements);
            }
        }

        return calls;
    }

    /**
     * Method to sanitize duplicate statements from a call sequence. This method returns a set with
     * the statements sanitizes from the end of the sequence. For example, if we have a sequence Con
     * -> A -> B -> A, then the method returns a set with Con, B, A where A is the second one in the
     * sequence. This way, we can ensure that the call to A is the last 'state' of the call sequence
     * and possible violations at the first A are overridden.
     *
     * @param sequence the sequence to sanitize
     * @return the sanitized statements
     */
    private Collection<Statement> sanitizeStatements(StatementSequence sequence) {
        Collection<Statement> sanitizedStatements = new HashSet<>();
        Collection<MethodWrapper> addedMethods = new HashSet<>();

        List<StatementSequence.Entry> entries = sequence.getSequence();
        for (int i = entries.size() - 1; i >= 0; i--) {
            Statement statement = entries.get(i).getStatement();

            if (!statement.containsInvokeExpr()) {
                continue;
            }

            MethodWrapper declaredMethod =
                    statement.getInvokeExpr().getDeclaredMethod().toMethodWrapper();
            if (!addedMethods.contains(declaredMethod)) {
                addedMethods.add(declaredMethod);
                sanitizedStatements.add(statement);
            }
        }

        return sanitizedStatements;
    }

    private boolean isGeneratingEventMissing(Statement statement, Collection<CrySLMethod> events) {
        Collection<TransitionFunction> weights = statementValWeightTable.row(statement).values();

        for (TransitionFunction weight : weights) {
            Collection<StatementSequence> sequences = weight.getStateChangeSequences().values();

            /* Check whether there is a sequence of calls that does not contain a method
             * from the 'after' condition;
             */
            for (StatementSequence sequence : sequences) {
                if (isSequenceWithoutEvent(sequence, events)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isSequenceWithoutEvent(
            StatementSequence sequence, Collection<CrySLMethod> events) {
        for (StatementSequence.Entry entry : sequence.getSequence()) {
            Statement statement = entry.getStatement();

            if (!statement.containsInvokeExpr()) {
                continue;
            }

            DeclaredMethod declaredMethod = statement.getInvokeExpr().getDeclaredMethod();
            for (CrySLMethod event : events) {
                // If the call matches at least one event, there is a corresponding call
                if (MatcherUtils.matchCryslMethodAndDeclaredMethod(event, declaredMethod)) {
                    return false;
                }
            }
        }

        return true;
    }

    private void propagateIndirectlyEnsuredPredicate(
            CrySLPredicate predicate,
            Statement statement,
            Collection<UnEnsuredPredicate.Violations> violations) {
        Collection<AbstractPredicate> indirectPreds =
                indirectlyEnsuredPredicates.stream()
                        .filter(p -> p.getPredicate().equals(predicate))
                        .collect(Collectors.toSet());
        for (AbstractPredicate indirectPred : indirectPreds) {
            AbstractPredicate generatedPred =
                    createIndirectPredicate(indirectPred, statement, violations);
            this.onGeneratedPredicate(generatedPred);

            Collection<Integer> indices = relevantStatements.get(statement);
            for (Integer index : indices) {
                AbstractPredicate predForOtherSeed =
                        createPredicate(this, predicate, statement, index, violations);

                notifyExpectingSeeds(predForOtherSeed, false);
            }
        }
    }

    private AbstractPredicate createIndirectPredicate(
            AbstractPredicate predicate,
            Statement statement,
            Collection<UnEnsuredPredicate.Violations> violations) {
        // If the generating seed ensured a hidden predicate, we have to propagate it as a hidden
        // one
        if (predicate instanceof UnEnsuredPredicate unEnsPred) {
            return new UnEnsuredPredicate(
                    unEnsPred.getGeneratingSeed(),
                    unEnsPred.getPredicate(),
                    statement,
                    -1,
                    unEnsPred.getViolations());
        }

        // If the generating seed ensured a valid predicate, we have to check if the current seed is
        // secure
        if (!violations.isEmpty()) {
            return new UnEnsuredPredicate(
                    predicate.getGeneratingSeed(),
                    predicate.getPredicate(),
                    statement,
                    -1,
                    violations);
        } else {
            return new EnsuredPredicate(
                    predicate.getGeneratingSeed(), predicate.getPredicate(), statement, -1);
        }
    }

    private void propagateEnsuredPredicate(
            CrySLPredicate predicate,
            Statement statement,
            Collection<UnEnsuredPredicate.Violations> violations) {
        if (hasThisParameter(predicate)) {
            AbstractPredicate generatedPred =
                    createPredicate(this, predicate, statement, -1, violations);
            this.onGeneratedPredicate(generatedPred);

            Collection<Integer> indices = relevantStatements.get(statement);
            for (Integer index : indices) {
                AbstractPredicate predForOtherSeed =
                        createPredicate(this, predicate, statement, index, violations);

                notifyExpectingSeeds(predForOtherSeed);
            }
        }

        Collection<CrySLMethod> methods =
                MatcherUtils.getMatchingCryslMethodsToDeclaredMethod(
                        specification.getEvents(), statement.getInvokeExpr().getDeclaredMethod());
        for (CrySLMethod method : methods) {
            if (isPredicateGeneratingAssignStatement(statement, predicate, method)) {
                AbstractPredicate generatedPred =
                        createPredicate(this, predicate, statement, -1, violations);
                Val otherSeedVal = statement.getLeftOp();

                propagatePredicateToOtherSeeds(generatedPred, otherSeedVal);
            }

            for (int i = 0; i < method.getParameters().size(); i++) {
                Map.Entry<String, String> param = method.getParameters().get(i);

                if (isPredicateRelevantValue(predicate, param.getKey())) {
                    AbstractPredicate generatedPred =
                            createPredicate(this, predicate, statement, i, violations);
                    Val otherSeedVal = statement.getInvokeExpr().getArg(i);

                    propagatePredicateToOtherSeeds(generatedPred, otherSeedVal);
                }
            }
        }
    }

    private void propagatePredicateToOtherSeeds(AbstractPredicate predicate, Val otherSeedVal) {
        for (IAnalysisSeed seed : scanner.getDiscoveredSeeds()) {
            Collection<Val> values = seed.getAliasesAtStatement(predicate.getStatement());

            if (values.contains(otherSeedVal)) {
                if (seed instanceof AnalysisSeedWithSpecification seedWithSpec) {
                    seedWithSpec.onGeneratedPredicateFromOtherSeed(predicate);
                } else if (seed instanceof AnalysisSeedWithEnsuredPredicate seedWithoutSpec) {
                    seedWithoutSpec.onPredicateGeneratedFromOtherSeed(predicate);
                }
            }
        }
    }

    private AbstractPredicate createPredicate(
            AnalysisSeedWithSpecification seed,
            CrySLPredicate predicate,
            Statement statement,
            int index,
            Collection<UnEnsuredPredicate.Violations> violations) {
        if (!violations.isEmpty()) {
            return new UnEnsuredPredicate(seed, predicate, statement, index, violations);
        } else {
            return new EnsuredPredicate(seed, predicate, statement, index);
        }
    }

    private void notifyExpectingSeeds(AbstractPredicate predicate) {
        notifyExpectingSeeds(predicate, true);
    }

    private void notifyExpectingSeeds(AbstractPredicate predicate, boolean isOriginalPredicate) {
        Collection<ExpectedPredicate> predsAtStatement =
                expectedPredicates.get(predicate.getStatement());

        for (ExpectedPredicate expectedPredicate : predsAtStatement) {
            if (expectedPredicate.predicate().equals(predicate.getPredicate())
                    && expectedPredicate.paramIndex() == predicate.getIndex()) {
                IAnalysisSeed seed = expectedPredicate.seed();

                if (seed instanceof AnalysisSeedWithSpecification seedWithSpec) {
                    seedWithSpec.onGeneratedPredicateFromOtherSeed(predicate, isOriginalPredicate);
                } else if (seed instanceof AnalysisSeedWithEnsuredPredicate seedWithoutSpec) {
                    seedWithoutSpec.onPredicateGeneratedFromOtherSeed(predicate);
                }
            }
        }
    }

    public void onGeneratedPredicateFromOtherSeed(AbstractPredicate predicate) {
        onGeneratedPredicateFromOtherSeed(predicate, true);
    }

    private void onGeneratedPredicateFromOtherSeed(
            AbstractPredicate predicate, boolean isOriginalPredicate) {
        if (isOriginalPredicate) {
            Collection<AbstractPredicate> currentPreds = new HashSet<>(indirectlyEnsuredPredicates);

            for (AbstractPredicate existingPred : currentPreds) {
                if (existingPred.equalsSimple(predicate)) {
                    indirectlyEnsuredPredicates.remove(existingPred);
                }
            }

            indirectlyEnsuredPredicates.add(predicate);
        }
        onGeneratedPredicate(predicate);
    }

    @Override
    public void afterPredicateChecks() {
        scanner.getAnalysisReporter().ensuredPredicates(this, ensuredPredicates);
        scanner.getAnalysisReporter().unEnsuredPredicates(this, unEnsuredPredicates);

        Collection<AbstractConstraintsError> violatedPredicates =
                constraintsAnalysis.evaluateRequiredPredicates();
        for (AbstractConstraintsError error : violatedPredicates) {
            addError(error);
            scanner.getAnalysisReporter().reportError(this, error);
        }
    }

    private boolean hasThisParameter(CrySLPredicate predicate) {
        return predicate.getParameters().get(0).getName().equals("this");
    }

    private boolean isPredicateGeneratingAssignStatement(
            Statement statement, CrySLPredicate predicate, CrySLMethod method) {
        if (!statement.isAssignStmt()) {
            return false;
        }

        Map.Entry<String, String> retObject = method.getRetObject();

        if (retObject.getKey().equals(CrySLMethod.NO_NAME)) {
            return false;
        }

        return isPredicateRelevantValue(predicate, retObject.getKey());
    }

    private boolean isPredicateRelevantValue(CrySLPredicate predicate, String key) {
        return predicate.getParameters().get(0).getName().equals(key);
    }

    private boolean doesStateGeneratePredicate(State state, CrySLPredicate predicate) {
        return isPredicateGeneratingState(predicate, state)
                && !isPredicateNegatingState(predicate, state);
    }

    private boolean isPredicateGeneratingState(CrySLPredicate ensPred, State stateNode) {
        // Predicate has a condition, i.e. "after" is specified -> Active predicate for
        // corresponding states
        if (ensPred instanceof CrySLCondPredicate condPred) {
            if (isConditionalState(condPred.getConditionalNodes(), stateNode)) {
                return true;
            }
        }

        // If there is no condition, the predicate is activated for all accepting states
        if (!(ensPred instanceof CrySLCondPredicate)) {
            return stateNode.isAccepting();
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

            for (StateNode s : condNegPred.getConditionalNodes()) {
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

        return internalConstraintsSatisfied && violatedPredicates.isEmpty();
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                               Additional methods	                                 *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public CrySLRule getSpecification() {
        return specification;
    }

    public Collection<Statement> getInvokedMethodStatements() {
        return allCallsOnObject;
    }

    public Collection<EnsuredPredicate> getEnsuredPredicatesAtStatement(Statement statement) {
        return ensuredPredicates.get(statement);
    }

    public Collection<UnEnsuredPredicate> getUnEnsuredPredicatesAtStatement(Statement statement) {
        return unEnsuredPredicates.get(statement);
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
