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
import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.ForbiddenMethodError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import crypto.constraints.ConstraintSolver;
import crypto.constraints.EvaluableConstraint;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.rules.CrySLCondPredicate;
import crypto.rules.CrySLForbiddenMethod;
import crypto.rules.CrySLMethod;
import crypto.rules.CrySLObject;
import crypto.rules.CrySLPredicate;
import crypto.rules.CrySLRule;
import crypto.rules.ICrySLPredicateParameter;
import crypto.rules.ISLConstraint;
import crypto.rules.StateNode;
import crypto.rules.TransitionEdge;
import crypto.typestate.ReportingErrorStateNode;
import crypto.typestate.WrappedState;
import crypto.utils.MatcherUtils;
import typestate.TransitionFunction;
import typestate.finiteautomata.ITransition;
import typestate.finiteautomata.State;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AnalysisSeedWithSpecification extends IAnalysisSeed {

	private final CrySLRule specification;

	private final ConstraintSolver constraintSolver;
	private boolean internalConstraintsSatisfied;

	private final Multimap<Statement, State> typeStateChange = HashMultimap.create();
	private final Map<ControlFlowGraph.Edge, DeclaredMethod> allCallsOnObject;

	private final Collection<AnalysisSeedWithSpecification> requiringSeeds = new HashSet<>();
	private final Multimap<Statement, Map.Entry<EnsuredCrySLPredicate, Integer>> ensuredPredicates = HashMultimap.create();
	private final Multimap<Statement, Map.Entry<HiddenPredicate, Integer>> hiddenPredicates = HashMultimap.create();
	private final Collection<EnsuredCrySLPredicate> indirectlyEnsuredPredicates = new HashSet<>();

	public AnalysisSeedWithSpecification(CryptoScanner scanner, Statement statement, Val fact, ForwardBoomerangResults<TransitionFunction> results, CrySLRule specification) {
		super(scanner, statement, fact, results);

		this.specification = specification;
		this.allCallsOnObject = results.getInvokedMethodOnInstance();
		this.constraintSolver = new ConstraintSolver(this);
	}

	@Override
	public String toString() {
		return "AnalysisSeedWithSpec [" + super.toString() + " with spec " + specification.getClassName() + "]";
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
	 *								Typestate checks								   *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	/**
	 * Check the FORBIDDEN section and report corresponding errors
	 */
	private void evaluateForbiddenMethods() {
		for (Map.Entry<ControlFlowGraph.Edge, DeclaredMethod> calledMethod : allCallsOnObject.entrySet()) {
			Optional<CrySLForbiddenMethod> forbiddenMethod = isForbiddenMethod(calledMethod.getValue());

			if (forbiddenMethod.isPresent()) {
				Collection<CrySLMethod> alternatives = forbiddenMethod.get().getAlternatives();
				Statement statement = calledMethod.getKey().getStart();
				DeclaredMethod declaredMethod = calledMethod.getValue();

				ForbiddenMethodError error = new ForbiddenMethodError(this, statement, specification, declaredMethod, alternatives);
				scanner.getAnalysisReporter().reportError(this, error);
			}
		}
	}

	private Optional<CrySLForbiddenMethod> isForbiddenMethod(DeclaredMethod declaredMethod) {
		Collection<CrySLForbiddenMethod> forbiddenMethods = specification.getForbiddenMethods();

		for (CrySLForbiddenMethod method : forbiddenMethods) {
			if (MatcherUtils.matchCryslMethodAndDeclaredMethod(method.getMethod(), declaredMethod)) {
				return Optional.of(method);
			}
		}
		return Optional.empty();
	}

	private void evaluateTypestateOrder() {
		Collection<ControlFlowGraph.Edge> allTypestateChangeStatements = new HashSet<>();
		for (Table.Cell<ControlFlowGraph.Edge, Val, TransitionFunction> cell : analysisResults.asStatementValWeightTable().cellSet()) {
			Collection<ControlFlowGraph.Edge> edges = cell.getValue().getLastStateChangeStatements();
			allTypestateChangeStatements.addAll(edges);
		}

		for (Table.Cell<ControlFlowGraph.Edge, Val, TransitionFunction> c : analysisResults.asStatementValWeightTable().cellSet()) {
			ControlFlowGraph.Edge curr = c.getRowKey();

			// The initial statement is always the start of the CFG edge, all other statements are targets
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
			if (stateNode instanceof ReportingErrorStateNode) {
				ReportingErrorStateNode errorStateNode = (ReportingErrorStateNode) stateNode;

				TypestateError typestateError = new TypestateError(this, statement, specification, errorStateNode.getExpectedCalls());
				this.addError(typestateError);
				scanner.getAnalysisReporter().reportError(this, typestateError);
			}
		}
	}

	private void evaluateIncompleteOperations() {
		Table<ControlFlowGraph.Edge, Val, TransitionFunction> endPathOfPropagation = analysisResults.getObjectDestructingStatements();
		Map<ControlFlowGraph.Edge, Collection<CrySLMethod>> incompleteOperations = new HashMap<>();

		for (Table.Cell<ControlFlowGraph.Edge, Val, TransitionFunction> c : endPathOfPropagation.cellSet()) {
			Collection<CrySLMethod> expectedMethodsToBeCalled = new HashSet<>();

			for (ITransition n : c.getValue().values()) {
				if (n.to() == null) {
					continue;
				}

				if (n.to().isAccepting()) {
					continue;
				}

				if (!(n.to() instanceof WrappedState)) {
					continue;
				}

				WrappedState wrappedState = (WrappedState) n.to();
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
			Map.Entry<ControlFlowGraph.Edge, Collection<CrySLMethod>> entry = incompleteOperations.entrySet().iterator().next();
			Collection<CrySLMethod> methodsToBeCalled = entry.getValue();
			Statement statement = entry.getKey().getTarget();

			if (statement.isThrowStmt()) {
				return;
			}

			IncompleteOperationError incompleteOperationError = new IncompleteOperationError(this, statement, specification, methodsToBeCalled);
			this.addError(incompleteOperationError);
			scanner.getAnalysisReporter().reportError(this, incompleteOperationError);
		}

		/* Multiple incomplete operations were found. Depending on the dataflow paths, the
		 * errors are reported:
		 * 1) A dataflow path ends in an accepting state: No error is reported
		 * 2) A dataflow path does not end in an accepting state: Report the error on the last used statement on this path
		 */
		if (incompleteOperations.size() > 1) {
			for (Map.Entry<ControlFlowGraph.Edge, Collection<CrySLMethod>> entry : incompleteOperations.entrySet()) {
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

				IncompleteOperationError incompleteOperationError = new IncompleteOperationError(this, statement, specification, expectedMethodsToBeCalled, true);
				this.addError(incompleteOperationError);
				scanner.getAnalysisReporter().reportError(this, incompleteOperationError);
			}
		}
	}

	private boolean isMethodToAcceptingState(DeclaredMethod method) {
		Collection<TransitionEdge> transitions = specification.getUsagePattern().getAllTransitions();

		for (TransitionEdge edge : transitions) {
			if (edge.getLabel().stream().noneMatch(e -> MatcherUtils.matchCryslMethodAndDeclaredMethod(e, method))) {
				continue;
			}

			if (edge.to().getAccepting()) {
				return true;
			}
		}

		return false;
	}

	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 *							   Predicate checks									*
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	@Override
	public void expectPredicate(Statement statement, CrySLPredicate predicate, IAnalysisSeed seed, int paramIndex) {
		expectedPredicates.put(statement, new ExpectedPredicateOnSeed(predicate, seed, paramIndex));
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
		for (ISLConstraint constraint : constraintSolver.getRequiredPredicates()) {
			if (constraint instanceof RequiredCrySLPredicate) {
				RequiredCrySLPredicate reqPred = (RequiredCrySLPredicate) constraint;

				Map.Entry<CrySLPredicate, Integer> entry = new AbstractMap.SimpleEntry<>(reqPred.getPred(), reqPred.getParamIndex());
				reqPreds.put(reqPred.getLocation(), entry);
			} else if (constraint instanceof AlternativeReqPredicate) {
				AlternativeReqPredicate altPred = (AlternativeReqPredicate) constraint;

				for (CrySLPredicate predicate : altPred.getAlternatives()) {
					Map.Entry<CrySLPredicate, Integer> entry = new AbstractMap.SimpleEntry<>(predicate, altPred.getParamIndex());

					reqPreds.put(altPred.getLocation(), entry);
				}
			}
		}

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

					Collection<IAnalysisSeed> requiredSeeds = computeRequiredSeeds(statement, base, seeds);
					for (IAnalysisSeed seed : requiredSeeds) {
						seed.expectPredicate(statement, predicate, this, paramIndex);

						if (seed instanceof AnalysisSeedWithSpecification) {
							((AnalysisSeedWithSpecification) seed).addRequiringSeed(this);
						}
					}
				} else {
					Val param = invokeExpr.getArg(paramIndex);

					Collection<IAnalysisSeed> requiredSeeds = computeRequiredSeeds(statement, param, seeds);
					for (IAnalysisSeed seed : requiredSeeds) {
						seed.expectPredicate(statement, predicate, this, paramIndex);

						if (seed instanceof AnalysisSeedWithSpecification) {
							((AnalysisSeedWithSpecification) seed).addRequiringSeed(this);
						}
					}
				}
			}
		}
	}

	private Collection<IAnalysisSeed> computeRequiredSeeds(Statement statement, Val val, Collection<IAnalysisSeed> seeds) {
		Collection<IAnalysisSeed> result = new HashSet<>();

		for (IAnalysisSeed seed : seeds) {
			for (Statement successor : statement.getMethod().getControlFlowGraph().getSuccsOf(statement)) {
				ControlFlowGraph.Edge edge = new ControlFlowGraph.Edge(statement, successor);

				Collection<Val> values = seed.getAnalysisResults().asStatementValWeightTable().row(edge).keySet();
				if (values.contains(val)) {
					result.add(seed);
				}
			}
		}

		return result;
	}

	public boolean canEnsurePredicate(CrySLPredicate predicate, Statement statement, int paramIndex) {
		DeclaredMethod declaredMethod = statement.getInvokeExpr().getMethod();

		Collection<CrySLMethod> methods = MatcherUtils.getMatchingCryslMethodsToDeclaredMethod(specification, declaredMethod);
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
			for (Map.Entry<ControlFlowGraph.Edge, DeclaredMethod> entry : allCallsOnObject.entrySet()) {
				Statement statement = entry.getKey().getStart();
				DeclaredMethod declaredMethod = entry.getValue();

				if (!statement.containsInvokeExpr()) {
					continue;
				}

				if (hasThisParameter(predicate)) {
					this.expectPredicate(statement, predicate, this, -1);
				}

				Collection<CrySLMethod> methods = MatcherUtils.getMatchingCryslMethodsToDeclaredMethod(specification, declaredMethod);

				for (CrySLMethod method : methods) {
					if (isPredicateGeneratingAssignStatement(predicate, statement, method)) {
						Val leftOp = statement.getLeftOp();
						Val rightOp = statement.getRightOp();

						AllocVal allocVal = new AllocVal(leftOp, statement, rightOp);

						Collection<IAnalysisSeed> dependentAssignSeeds = computeGeneratedAssignSeeds(statement, allocVal, seeds);
						for (IAnalysisSeed seed : dependentAssignSeeds) {
							this.expectPredicate(statement, predicate, seed, -1);
						}
					}

					for (int i = 0; i < method.getParameters().size(); i++) {
						Map.Entry<String, String> param = method.getParameters().get(i);

						if (isPredicateRelevantValue(param.getKey(), predicate)) {
							Val paramVal = statement.getInvokeExpr().getArg(i);

							Collection<IAnalysisSeed> dependantParamSeeds = computeGeneratedParameterSeeds(entry.getKey(), paramVal, seeds);
							for (IAnalysisSeed seed : dependantParamSeeds) {
								this.expectPredicate(statement, predicate, seed, i);
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

	private boolean isPredicateGeneratingAssignStatement(CrySLPredicate predicate, Statement statement, CrySLMethod method) {
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

	private Collection<IAnalysisSeed> computeGeneratedAssignSeeds(Statement statement, Val fact, Collection<IAnalysisSeed> seeds) {
		Collection<IAnalysisSeed> result = new HashSet<>();

		for (IAnalysisSeed seed : seeds) {
			if (seed.getOrigin().equals(statement) && seed.getFact().equals(fact)) {
				result.add(seed);
			}
		}

		return result;
	}

	private Collection<IAnalysisSeed> computeGeneratedParameterSeeds(ControlFlowGraph.Edge edge, Val fact, Collection<IAnalysisSeed> seeds) {
		Collection<IAnalysisSeed> result = new HashSet<>();

		for (IAnalysisSeed seed : seeds) {
			Collection<Val> values = seed.getAnalysisResults().asStatementValWeightTable().row(edge).keySet();

			if (values.contains(fact)) {
				result.add(seed);
			}
		}

		return result;
	}

	public void ensurePredicates() {
		boolean satisfiesConstraintSystem = isConstraintSystemSatisfied();

		Collection<Statement> expectedPredStatements = expectedPredicates.keySet();

		Collection<CrySLPredicate> predsToBeEnsured = new HashSet<>(specification.getPredicates());
		for (EnsuredCrySLPredicate predicate : indirectlyEnsuredPredicates) {
			predsToBeEnsured.add(predicate.getPredicate().toNormalCrySLPredicate());
		}

		for (CrySLPredicate predToBeEnsured : predsToBeEnsured) {

			for (Statement statement : expectedPredStatements) {
				boolean isPredicateGeneratingStateAvailable = false;

				if (!expectedPredicatesAtStatement(statement).contains(predToBeEnsured.toNormalCrySLPredicate())) {
					continue;
				}

				Collection<State> states = getStatesAtStatement(statement);

				for (State state : states) {
					// Check, whether the predicate should be generated in state
					if (!isPredicateGeneratingState(predToBeEnsured, state)) {
						continue;
					}

					// Check, whether the predicate is not negated in state
					if (isPredicateNegatingState(predToBeEnsured, state)) {
						continue;
					}

					isPredicateGeneratingStateAvailable = true;
					EnsuredCrySLPredicate ensPred;
					if (!satisfiesConstraintSystem && predToBeEnsured.getConstraint().isEmpty()) {
						// predicate has no condition, but the constraint system is not satisfied
						ensPred = new HiddenPredicate(predToBeEnsured, constraintSolver.getCollectedValues(), this, HiddenPredicate.HiddenPredicateType.ConstraintsAreNotSatisfied);
					} else if (predToBeEnsured.getConstraint().isPresent() && isPredConditionViolated(predToBeEnsured)) {
						// predicate has condition, but condition is not satisfied
						ensPred = new HiddenPredicate(predToBeEnsured, constraintSolver.getCollectedValues(), this, HiddenPredicate.HiddenPredicateType.ConditionIsNotSatisfied);
					} else {
						// constraints are satisfied and predicate has no condition or the condition is satisfied
						ensPred = new EnsuredCrySLPredicate(predToBeEnsured, constraintSolver.getCollectedValues());
					}

					ensurePredicateAtStatement(ensPred, statement);
				}

				if (!isPredicateGeneratingStateAvailable) {
					/* The predicate is not ensured in any state. However, we propagate a hidden predicate
					 * for all typestate changing statements because the predicate could have been ensured
					 * if a generating state had been reached
					 */
					HiddenPredicate hiddenPredicate = new HiddenPredicate(predToBeEnsured, constraintSolver.getCollectedValues(), this, HiddenPredicate.HiddenPredicateType.GeneratingStateIsNeverReached);
					ensurePredicateAtStatement(hiddenPredicate, statement);
				}
			}
		}

		scanner.getAnalysisReporter().ensuredPredicates(this, ensuredPredicates);
	}

	private void ensurePredicateAtStatement(EnsuredCrySLPredicate ensPred, Statement statement) {
		if (hasThisParameter(ensPred.getPredicate())) {
			this.addEnsuredPredicate(ensPred, statement, -1);
			scanner.getAnalysisReporter().onGeneratedPredicate(this, ensPred, this, statement);
		}

		if (!expectedPredicates.containsKey(statement)) {
			return;
		}

		Collection<ExpectedPredicateOnSeed> expectedPredsAtStatement = expectedPredicates.get(statement);
		for (ExpectedPredicateOnSeed expectedPredicateOnSeed : expectedPredsAtStatement) {
			CrySLPredicate predicate = expectedPredicateOnSeed.getPredicate();
			IAnalysisSeed seed = expectedPredicateOnSeed.getSeed();
			int paramIndex = expectedPredicateOnSeed.getParamIndex();

			if (predicate.equals(ensPred.getPredicate())) {
				if (seed instanceof AnalysisSeedWithSpecification) {
					AnalysisSeedWithSpecification seedWithSpec = (AnalysisSeedWithSpecification) seed;

					seedWithSpec.addEnsuredPredicateFromOtherRule(ensPred, statement, paramIndex);
					scanner.getAnalysisReporter().onGeneratedPredicate(this, ensPred, seedWithSpec, statement);
				} else if (seed instanceof AnalysisSeedWithEnsuredPredicate) {
					AnalysisSeedWithEnsuredPredicate seedWithoutSpec = (AnalysisSeedWithEnsuredPredicate) seed;

					seedWithoutSpec.addEnsuredPredicate(ensPred);
					scanner.getAnalysisReporter().onGeneratedPredicate(this, ensPred, seedWithoutSpec, statement);
				}
			}
		}
	}

	private void addEnsuredPredicateFromOtherRule(EnsuredCrySLPredicate pred, Statement statement, int paramIndex) {
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

			for (Statement pred : statement.getMethod().getControlFlowGraph().getPredsOf(statement)) {
				ControlFlowGraph.Edge edge = new ControlFlowGraph.Edge(pred, statement);

				Map<Val, TransitionFunction> rows = analysisResults.asStatementValWeightTable().row(edge);
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

	public void addEnsuredPredicate(EnsuredCrySLPredicate ensPred, Statement statement, int paramIndex) {
		if (ensPred instanceof HiddenPredicate) {
			HiddenPredicate hiddenPredicate = (HiddenPredicate) ensPred;

			Map.Entry<HiddenPredicate, Integer> predAtIndex = new AbstractMap.SimpleEntry<>(hiddenPredicate, paramIndex);
			hiddenPredicates.put(statement, predAtIndex);
		} else {
			Map.Entry<EnsuredCrySLPredicate, Integer> predAtIndex = new AbstractMap.SimpleEntry<>(ensPred, paramIndex);
			ensuredPredicates.put(statement, predAtIndex);
		}
	}


	private Collection<State> getTargetStates(TransitionFunction value) {
		Collection<State> res = Sets.newHashSet();
		for (ITransition t : value.values()) {
			if (t.to() != null)
				res.add(t.to());
		}
		return res;
	}

	private boolean isPredicateGeneratingState(CrySLPredicate ensPred, State stateNode) {
		// Predicate has a condition, i.e. "after" is specified -> Active predicate for corresponding states
		if (ensPred instanceof CrySLCondPredicate && isConditionalState(((CrySLCondPredicate) ensPred).getConditionalMethods(), stateNode)) {
			return true;
		}

		// If there is no condition, the predicate is activated for all accepting states
		if (!(ensPred instanceof CrySLCondPredicate) && stateNode.isAccepting()) {
			return true;
		}

		return false;
	}

	private boolean isConditionalState(Collection<StateNode> conditionalMethods, State state) {
		if (conditionalMethods == null)
			return false;
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

			// Negated predicate does not have a condition, i.e. no "after" -> predicate is negated in all states
			if (!(negPred instanceof CrySLCondPredicate)) {
				return true;
			}

			CrySLCondPredicate condNegPred = (CrySLCondPredicate) negPred;

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
			if (ensParameter.getJavaType().equals("null") || negParameter.getJavaType().equals("null")) {
				continue;
			}

			if (!ensParameter.getJavaType().equals(negParameter.getJavaType())) {
				return false;
			}
		}

		return true;
	}

	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 *								Constraint checks								  *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	/**
	 * Check the constraints from the CONSTRAINTS section
	 */
	private void checkInternalConstraints() {
		scanner.getAnalysisReporter().beforeConstraintsCheck(this);

		Collection<AbstractError> violatedConstraints = constraintSolver.evaluateConstraints();
		for (AbstractError violatedConstraint : violatedConstraints) {
			scanner.getAnalysisReporter().reportError(this, violatedConstraint);
		}

		scanner.getAnalysisReporter().checkedConstraints(this, constraintSolver.getRelConstraints(), violatedConstraints);
		scanner.getAnalysisReporter().afterConstraintsCheck(this, violatedConstraints.size());

		this.internalConstraintsSatisfied = violatedConstraints.isEmpty();
	}

	/**
	 * Check, whether the internal constraints and predicate constraints are satisfied.
	 * Requires a previous call to {@link #checkInternalConstraints()}
	 *
	 * @return true if all internal and required predicate constraints are satisfied
	 */
	private boolean isConstraintSystemSatisfied() {
		if (internalConstraintsSatisfied) {
			return computeMissingPredicates().isEmpty() && computeContradictedPredicates().isEmpty();
		}
		return false;
	}

	/**
	 * Check, whether all required predicates are satisfied, and return a set with all predicates that are not
	 * satisfied. If the set is empty, all required predicate constraints are satisfied.
	 *
	 * @return remainingPredicates predicates that are not satisfied
	 */
	public Collection<ISLConstraint> computeMissingPredicates() {
		Collection<ISLConstraint> requiredPredicates = constraintSolver.getRequiredPredicates();
		Collection<ISLConstraint> remainingPredicates = new HashSet<>(requiredPredicates);

		for (ISLConstraint pred : requiredPredicates) {
			Collection<Map.Entry<EnsuredCrySLPredicate, Integer>> predsAtStatement = ensuredPredicates.get(pred.getLocation());

			if (pred instanceof RequiredCrySLPredicate) {
				RequiredCrySLPredicate reqPred = (RequiredCrySLPredicate) pred;

				// If a negated predicate is ensured, a PredicateContradictionError has to be reported
				if (reqPred.getPred().isNegated()) {
					remainingPredicates.remove(pred);
					continue;
				}

				// Check for basic required predicates, e.g. randomized
				int reqParamIndex = reqPred.getParamIndex();
				for (Map.Entry<EnsuredCrySLPredicate, Integer> ensPredAtIndex : predsAtStatement) {
					if (doReqPredAndEnsPredMatch(reqPred.getPred(), reqParamIndex, ensPredAtIndex)) {
						remainingPredicates.remove(pred);
					}
				}
			} else if (pred instanceof AlternativeReqPredicate) {
				AlternativeReqPredicate altPred = (AlternativeReqPredicate) pred;
				Collection<CrySLPredicate> alternatives = altPred.getAlternatives();
				Collection<CrySLPredicate> positives = alternatives.stream().filter(e -> !e.isNegated()).collect(Collectors.toList());
				Collection<CrySLPredicate> negatives = alternatives.stream().filter(CrySLPredicate::isNegated).collect(Collectors.toList());
				int altParamIndex = altPred.getParamIndex();

				boolean satisfied = false;
				Collection<CrySLPredicate> ensuredNegatives = alternatives.stream().filter(CrySLPredicate::isNegated).collect(Collectors.toList());

				for (Map.Entry<EnsuredCrySLPredicate, Integer> ensPredAtIndex : predsAtStatement) {
					// If any positive alternative is satisfied, the whole predicate is satisfied
					if (positives.stream().anyMatch(e -> doReqPredAndEnsPredMatch(e, altParamIndex, ensPredAtIndex))) {
						satisfied = true;
					}

					// Remove all negated alternatives that are ensured
					Collection<CrySLPredicate> violatedNegAlternatives = negatives.stream().filter(e -> doReqPredAndEnsPredMatch(e, altParamIndex, ensPredAtIndex)).collect(Collectors.toList());
					ensuredNegatives.removeAll(violatedNegAlternatives);
				}

				if (satisfied || !ensuredNegatives.isEmpty()) {
					remainingPredicates.remove(pred);
				}
			}
		}

		// Check conditional required predicates
		for (ISLConstraint rem : new HashSet<>(remainingPredicates)) {
			if (rem instanceof RequiredCrySLPredicate) {
				RequiredCrySLPredicate singlePred = (RequiredCrySLPredicate) rem;

				if (isPredConditionViolated(singlePred.getPred())) {
					remainingPredicates.remove(singlePred);
				}
			} else if (rem instanceof AlternativeReqPredicate) {
				Collection<CrySLPredicate> altPred = ((AlternativeReqPredicate) rem).getAlternatives();

				if (altPred.parallelStream().anyMatch(this::isPredConditionViolated)) {
					remainingPredicates.remove(rem);
				}
			}
		}

		return remainingPredicates;
	}

	public Collection<RequiredCrySLPredicate> computeContradictedPredicates() {
		Collection<ISLConstraint> requiredPredicates = constraintSolver.getRequiredPredicates();
		Collection<RequiredCrySLPredicate> contradictedPredicates = new HashSet<>();

		for (ISLConstraint pred : requiredPredicates) {
			if (pred instanceof RequiredCrySLPredicate) {
				RequiredCrySLPredicate reqPred = (RequiredCrySLPredicate) pred;

				// Only negated predicates can be contradicted
				if (!reqPred.getPred().isNegated()) {
					continue;
				}

				if (isPredConditionViolated(reqPred.getPred())) {
					continue;
				}

				// Check for basic negated required predicates, e.g. randomized
				CrySLPredicate invertedPred = reqPred.getPred().invertNegation();
				Collection<Map.Entry<EnsuredCrySLPredicate, Integer>> predsAtStatement = ensuredPredicates.get(pred.getLocation());

				for (Map.Entry<EnsuredCrySLPredicate, Integer> ensPredAtIndex : predsAtStatement) {
					if (doReqPredAndEnsPredMatch(invertedPred, reqPred.getParamIndex(), ensPredAtIndex)) {
						contradictedPredicates.add(reqPred);
					}
				}
			}
		}

		return contradictedPredicates;
	}

	private boolean doReqPredAndEnsPredMatch(CrySLPredicate reqPred, int reqPredIndex, Map.Entry<EnsuredCrySLPredicate, Integer> ensPred) {
		return reqPred.equals(ensPred.getKey().getPredicate()) && doPredsMatch(reqPred, ensPred.getKey()) && reqPredIndex == ensPred.getValue();
	}

	/**
	 * Check for a predicate A =&gt; B, whether the condition A of B is satisfied
	 *
	 * @param pred the predicate to be checked
	 * @return true if the condition is satisfied
	 */
	private boolean isPredConditionViolated(CrySLPredicate pred) {
		return pred.getConstraint().map(conditional -> {
			EvaluableConstraint evalCons = EvaluableConstraint.getInstance(conditional, constraintSolver);
			evalCons.evaluate();
			return evalCons.hasErrors();
		}).orElse(false);
	}

	public Collection<AbstractError> retrieveErrorsForPredCondition(CrySLPredicate pred) {
		// Check, whether the predicate has a condition
		if (pred.getConstraint().isEmpty()) {
			return Collections.emptyList();
		}

		// TODO the condition should be reported itself?
		ISLConstraint condition = pred.getConstraint().get();
		return Collections.emptyList();
	}

	private boolean doPredsMatch(CrySLPredicate pred, EnsuredCrySLPredicate ensPred) {
		boolean requiredPredicatesExist = true;
		for (int i = 0; i < pred.getParameters().size(); i++) {
			String var = pred.getParameters().get(i).getName();
			if (isOfNonTrackableType(var)) {
				continue;
			} else if (pred.getInvolvedVarNames().contains(var)) {

				final String parameterI = ensPred.getPredicate().getParameters().get(i).getName();
				Collection<String> actVals = Collections.emptySet();
				Collection<String> expVals = Collections.emptySet();

				for (CallSiteWithExtractedValue cswpi : ensPred.getParametersToValues()) {
					if (cswpi.getCallSiteWithParam().getVarName().equals(parameterI)) {
						actVals = retrieveValueFromUnit(cswpi);
					}
				}
				for (CallSiteWithExtractedValue cswpi : constraintSolver.getCollectedValues()) {
					if (cswpi.getCallSiteWithParam().getVarName().equals(var)) {
						expVals = retrieveValueFromUnit(cswpi);
					}
				}

				String splitter = "";
				int index = -1;
				if (pred.getParameters().get(i) instanceof CrySLObject) {
					CrySLObject obj = (CrySLObject) pred.getParameters().get(i);
					if (obj.getSplitter() != null) {
						splitter = obj.getSplitter().getSplitter();
						index = obj.getSplitter().getIndex();
					}
				}
				for (String foundVal : expVals) {
					if (index > -1) {
						foundVal = foundVal.split(splitter)[index];
					}
					actVals = actVals.parallelStream().map(String::toLowerCase).collect(Collectors.toList());
					requiredPredicatesExist &= actVals.contains(foundVal.toLowerCase());
				}
			} else {
				requiredPredicatesExist = false;
			}
		}
		return requiredPredicatesExist;
	}

	public void addHiddenPredicatesToError(RequiredPredicateError reqPredError) {
		for (CrySLPredicate pred : reqPredError.getContradictedPredicates()) {
			Collection<HiddenPredicate> hiddenPredicatesEnsuringReqPred = new HashSet<>();

			for (Map.Entry<HiddenPredicate, Integer> entry : hiddenPredicates.values()) {
				HiddenPredicate hiddenPredicate = entry.getKey();

				if (hiddenPredicate.getPredicate().equals(pred) && doPredsMatch(pred, hiddenPredicate)) {
					hiddenPredicatesEnsuringReqPred.add(hiddenPredicate);
				}
			}

			reqPredError.addHiddenPredicates(hiddenPredicatesEnsuringReqPred);
		}
	}

	private Collection<String> retrieveValueFromUnit(CallSiteWithExtractedValue callSite) {
		Collection<String> values = new ArrayList<>();
		Statement statement = callSite.getCallSiteWithParam().stmt();

		if (statement.isAssign()) {
			Val rightSide = statement.getRightOp();

			if (rightSide.isConstant()) {
				values.add(retrieveConstantFromValue(rightSide));
			}
		}
		return values;
	}

	private String retrieveConstantFromValue(Val val) {
		if (val.isStringConstant()) {
			return val.getStringValue();
		} else if (val.isIntConstant()) {
			return String.valueOf(val.getIntValue());
		} else {
			return "";
		}
	}

	private final static Collection<String> trackedTypes = Arrays.asList("java.lang.String", "int", "java.lang.Integer");

	private boolean isOfNonTrackableType(String varName) {
		for (Map.Entry<String, String> object : specification.getObjects()) {
			if (object.getKey().equals(varName) && trackedTypes.contains(object.getValue())) {
				return false;
			}
		}
		return true;
	}

	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 *							   Additional methods									 *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	public CrySLRule getSpecification() {
		return specification;
	}

	public Map<ControlFlowGraph.Edge, DeclaredMethod> getAllCallsOnObject() {
		return allCallsOnObject;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((specification == null) ? 0 : specification.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AnalysisSeedWithSpecification other = (AnalysisSeedWithSpecification) obj;
		if (specification == null) {
			return other.specification == null;
		} else return specification.equals(other.specification);
	}

}
