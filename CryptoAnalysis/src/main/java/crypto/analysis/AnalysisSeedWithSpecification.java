package crypto.analysis;

import boomerang.WeightedForwardQuery;
import boomerang.debugger.Debugger;
import boomerang.results.ForwardBoomerangResults;
import boomerang.scene.AllocVal;
import boomerang.scene.CallGraph;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.DataFlowScope;
import boomerang.scene.DeclaredMethod;
import boomerang.scene.Statement;
import boomerang.scene.Type;
import boomerang.scene.Val;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import crypto.constraints.ConstraintSolver;
import crypto.constraints.EvaluableConstraint;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractParameterAnalysis;
import crypto.extractparameter.ExtractedValue;
import crypto.interfaces.ICrySLPredicateParameter;
import crypto.interfaces.ISLConstraint;
import crypto.predicates.PredicateHandler;
import crypto.rules.CrySLCondPredicate;
import crypto.rules.CrySLMethod;
import crypto.rules.CrySLObject;
import crypto.rules.CrySLPredicate;
import crypto.rules.StateNode;
import crypto.rules.TransitionEdge;
import crypto.typestate.ExtendedIDEALAnalysis;
import crypto.typestate.MatcherTransitionCollection;
import crypto.typestate.MatcherUtils;
import crypto.typestate.ReportingErrorStateNode;
import crypto.typestate.WrappedState;
import ideal.IDEALSeedSolver;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;
import typestate.finiteautomata.ITransition;
import typestate.finiteautomata.State;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

public class AnalysisSeedWithSpecification extends IAnalysisSeed {

	private final ClassSpecification spec;
	private ExtendedIDEALAnalysis analysis;
	private ForwardBoomerangResults<TransitionFunction> results;
	private Collection<EnsuredCrySLPredicate> ensuredPredicates = Sets.newHashSet();
	private Multimap<ControlFlowGraph.Edge, State> typeStateChange = HashMultimap.create();
	private Collection<EnsuredCrySLPredicate> indirectlyEnsuredPredicates = Sets.newHashSet();
	private Set<HiddenPredicate> hiddenPredicates = Sets.newHashSet();
	private ConstraintSolver constraintSolver;
	private boolean internalConstraintsSatisfied;
	private Map<ControlFlowGraph.Edge, DeclaredMethod> allCallsOnObject = Maps.newLinkedHashMap();
	private ExtractParameterAnalysis parameterAnalysis;
	private Set<ResultsHandler> resultHandlers = Sets.newHashSet();
	private boolean secure = true;

	public AnalysisSeedWithSpecification(CryptoScanner cryptoScanner, WeightedForwardQuery<TransitionFunction> forwardQuery, ClassSpecification spec) {
		super(cryptoScanner, forwardQuery);
		this.spec = spec;
		this.analysis = new ExtendedIDEALAnalysis() {

			@Override
			public MatcherTransitionCollection getMatcherTransitions() {
				return spec.getMatcherTransitions();
			}

			@Override
			protected CallGraph callGraph() {
				return cryptoScanner.callGraph();
			}

			@Override
			protected DataFlowScope getDataFlowScope() {
				return cryptoScanner.getDataFlowScope();
			}

			@Override
			protected Debugger<TransitionFunction> debugger(IDEALSeedSolver<TransitionFunction> solver) {
				return cryptoScanner.debugger(solver, AnalysisSeedWithSpecification.this);
			}

			@Override
			public CrySLResultsReporter analysisListener() {
				return cryptoScanner.getAnalysisListener();
			}
		};
	}

	public AnalysisSeedWithSpecification(CryptoScanner cryptoScanner, ControlFlowGraph.Edge edge, Val val, ClassSpecification spec) {
		this(cryptoScanner, new WeightedForwardQuery<>(edge, val, spec.getInitialWeight(edge)), spec);
	}

	@Override
	public String toString() {
		return "AnalysisSeed [" + super.toString() + " with spec " + spec.getRule().getClassName() + "]";
	}

	public void execute() {
		cryptoScanner.getAnalysisListener().seedStarted(this);
		runTypestateAnalysis();
		if (results == null)
			// Timeout occured.
			return;
		allCallsOnObject = results.getInvokedMethodOnInstance();
		runExtractParameterAnalysis();
		this.internalConstraintsSatisfied = checkInternalConstraints();

		computeTypestateErrorUnits();
		computeTypestateErrorsForEndOfObjectLifeTime();

		activateIndirectlyEnsuredPredicates();
		checkConstraintsAndEnsurePredicates();

		cryptoScanner.getAnalysisListener().onSeedFinished(this, results);
		cryptoScanner.getAnalysisListener().collectedValues(this, parameterAnalysis.getCollectedValues());
	}

	public void registerResultsHandler(ResultsHandler handler) {
		if (results != null) {
			handler.done(results);
		} else {
			resultHandlers.add(handler);
		}
	}

	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 *                                Typestate checks                                   *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	private void runTypestateAnalysis() {
		analysis.run(getForwardQuery());
		results = analysis.getResults();

		if (results != null) {
			for (ResultsHandler handler : Lists.newArrayList(resultHandlers)) {
				handler.done(results);
			}
		}
	}

	private void runExtractParameterAnalysis() {
		this.parameterAnalysis = new ExtractParameterAnalysis(this.cryptoScanner, allCallsOnObject, spec.getMatcherTransitions());
		this.parameterAnalysis.run();
	}

	private void computeTypestateErrorUnits() {
		Set<ControlFlowGraph.Edge> allTypestateChangeStatements = Sets.newHashSet();
		for (Cell<ControlFlowGraph.Edge, Val, TransitionFunction> c : results.asStatementValWeightTable().cellSet()) {
			allTypestateChangeStatements.addAll(c.getValue().getLastStateChangeStatements());
		}
		for (Cell<ControlFlowGraph.Edge, Val, TransitionFunction> c : results.asStatementValWeightTable().cellSet()) {
			ControlFlowGraph.Edge curr = c.getRowKey();

			if (allTypestateChangeStatements.contains(curr)) {
				/*if (!curr.getStart().containsInvokeExpr()) {
					continue;
				}*/

				Collection<? extends State> targetStates = getTargetStates(c.getValue());
				for (State newStateAtCurr : targetStates) {
					typeStateChangeAtStatement(curr, newStateAtCurr);
				}
			}
		}
	}

	private void computeTypestateErrorsForEndOfObjectLifeTime() {
		Table<ControlFlowGraph.Edge, Val, TransitionFunction> endPathOfPropagation = results.getObjectDestructingStatements();
		Map<ControlFlowGraph.Edge, Set<CrySLMethod>> incompleteOperations = new HashMap<>();

		for (Cell<ControlFlowGraph.Edge, Val, TransitionFunction> c : endPathOfPropagation.cellSet()) {
			Set<CrySLMethod> expectedMethodsToBeCalled = new HashSet<>();

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
				for (TransitionEdge t : spec.getRule().getUsagePattern().getAllTransitions()) {
					if (t.getLeft().equals(wrappedState.delegate()) && !t.from().equals(t.to())) {
						List<CrySLMethod> labels = t.getLabel();
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
			Entry<ControlFlowGraph.Edge, Set<CrySLMethod>> entry = incompleteOperations.entrySet().iterator().next();
			Set<CrySLMethod> methodsToBeCalled = entry.getValue();
			Statement statement = entry.getKey().getTarget();

			if (statement.isThrowStmt()) {
				return;
			}

			IncompleteOperationError incompleteOperationError = new IncompleteOperationError(this, statement, spec.getRule(), methodsToBeCalled);
			this.addError(incompleteOperationError);
			cryptoScanner.getAnalysisListener().reportError(this, incompleteOperationError);
		}

		/* Multiple incomplete operations were found. Depending on the dataflow paths, the
		 * errors are reported:
		 * 1) A dataflow path ends in an accepting state: No error is reported
		 * 2) A dataflow path does not end in an accepting state: Report the error on the last used statement on this path
		 */
		if (incompleteOperations.size() > 1) {
			for (Entry<ControlFlowGraph.Edge, Set<CrySLMethod>> entry : incompleteOperations.entrySet()) {
				Set<CrySLMethod> expectedMethodsToBeCalled = entry.getValue();
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

				IncompleteOperationError incompleteOperationError = new IncompleteOperationError(this, statement, spec.getRule(), expectedMethodsToBeCalled, true);
				this.addError(incompleteOperationError);
				cryptoScanner.getAnalysisListener().reportError(this, incompleteOperationError);
			}
		}
	}

	private boolean isMethodToAcceptingState(DeclaredMethod method) {
		Collection<TransitionEdge> transitions = spec.getRule().getUsagePattern().getAllTransitions();

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

	private void typeStateChangeAtStatement(ControlFlowGraph.Edge statement, State stateNode) {
		if (typeStateChange.put(statement, stateNode)) {
			if (stateNode instanceof ReportingErrorStateNode) {
				ReportingErrorStateNode errorStateNode = (ReportingErrorStateNode) stateNode;

				TypestateError typestateError = new TypestateError(statement.getTarget(), getSpec().getRule(), this, errorStateNode.getExpectedCalls());
				this.addError(typestateError);
				cryptoScanner.getAnalysisListener().reportError(this, typestateError);
			}
		}
	}

	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 *                               Predicate checks                                    *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	/**
	 * Add an ensured predicate to the seed and implicitly rerun all constraint and
	 * predicate checks
	 *
	 * @param ensPred the ensured predicate
	 */
	public void addEnsuredPredicate(EnsuredCrySLPredicate ensPred) {
		// Hidden predicates do not satisfy any constraints
		if (ensPred instanceof HiddenPredicate) {
			HiddenPredicate hiddenPredicate = (HiddenPredicate) ensPred;
			hiddenPredicates.add(hiddenPredicate);
			return;
		}

		// If the predicate was not ensured before, ensure it and check the constraints
		if (ensuredPredicates.add(ensPred)) {
			checkConstraintsAndEnsurePredicates();
		}
	}

	private void checkConstraintsAndEnsurePredicates() {
		boolean satisfiesConstraintSystem = isConstraintSystemSatisfied();

		for (CrySLPredicate predToBeEnsured : spec.getRule().getPredicates()) {
			boolean isPredicateGeneratingStateAvailable = false;
			for (Entry<ControlFlowGraph.Edge, State> entry : typeStateChange.entries()) {
				State state = entry.getValue();

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
				if (!satisfiesConstraintSystem && !predToBeEnsured.getConstraint().isPresent()) {
					// predicate has no condition, but the constraint system is not satisfied
					ensPred = new HiddenPredicate(predToBeEnsured, parameterAnalysis.getCollectedValues(), this, HiddenPredicate.HiddenPredicateType.ConstraintsAreNotSatisfied);
				} else if (predToBeEnsured.getConstraint().isPresent() && !isPredConditionSatisfied(predToBeEnsured)) {
					// predicate has condition, but condition is not satisfied
					ensPred = new HiddenPredicate(predToBeEnsured, parameterAnalysis.getCollectedValues(), this, HiddenPredicate.HiddenPredicateType.ConditionIsNotSatisfied);
				} else {
					// constraints are satisfied and predicate has no condition or the condition is satisfied
					ensPred = new EnsuredCrySLPredicate(predToBeEnsured, parameterAnalysis.getCollectedValues());
				}
				ensurePredicate(ensPred, entry.getKey(), entry.getValue());
			}

			if (parameterAnalysis != null && !isPredicateGeneratingStateAvailable) {
				/* The predicate is not ensured in any state. However, we propagate a hidden predicate
				 * for all typestate changing statements because the predicate could have been ensured
				 * if a generating state had been reached
				 */
				HiddenPredicate hiddenPredicate = new HiddenPredicate(predToBeEnsured, parameterAnalysis.getCollectedValues(), this, HiddenPredicate.HiddenPredicateType.GeneratingStateIsNeverReached);

				for (Entry<ControlFlowGraph.Edge, State> entry : typeStateChange.entries()) {
					ensurePredicate(hiddenPredicate, entry.getKey(), entry.getValue());
				}
			}
		}
	}

	/**
	 * Ensure a {@link EnsuredCrySLPredicate}, if all constraints are satisfied, or a {@link HiddenPredicate},
	 * if any constraint (CONSTRAINTS, ORDER or REQUIRES) is not satisfied, for the given statement.
	 *
	 * @param ensuredPred the predicate to be ensured
	 * @param currStmt the statement before the type change
	 * @param stateNode the next state after executing {@code currStmt}
	 */
	private void ensurePredicate(EnsuredCrySLPredicate ensuredPred, ControlFlowGraph.Edge currStmt, State stateNode) {
		// TODO only for first parameter?
		for (ICrySLPredicateParameter predicateParam : ensuredPred.getPredicate().getParameters()) {
			if (predicateParam.getName().equals("this")) {
				expectPredicateWhenThisObjectIsInState(ensuredPred, stateNode, currStmt);
			}
		}

		// Check, whether the predicate should be ensured on another object
		Statement statement = currStmt.getTarget();
		if (!statement.containsInvokeExpr()) {
			return;
		}

		DeclaredMethod invokedMethod = statement.getInvokeExpr().getMethod();
		Collection<CrySLMethod> convert = MatcherUtils.getMatchingCryslMethodsToDeclaredMethod(spec.getRule(), invokedMethod);

		for (CrySLMethod crySLMethod : convert) {
			Entry<String, String> retObject = crySLMethod.getRetObject();
			if (!retObject.getKey().equals("_") && statement.isAssign() && predicateParameterEquals(ensuredPred.getPredicate().getParameters(), retObject.getKey())) {
				Val leftOp = statement.getLeftOp();
				Val rightOp = statement.getRightOp();

				AllocVal val = new AllocVal(leftOp, statement, rightOp);
				expectPredicateOnOtherObject(ensuredPred, statement, val);
			}
			int i = 0;
			for (Entry<String, String> p : crySLMethod.getParameters()) {
				if (predicateParameterEquals(ensuredPred.getPredicate().getParameters(), p.getKey())) {
					Val param = statement.getInvokeExpr().getArg(i);

					if (param.isLocal()) {
						AllocVal allocVal = new AllocVal(param, statement, param);
						expectPredicateOnOtherObject(ensuredPred, statement, allocVal);
					}
				}
				i++;
			}
		}
	}

	private boolean predicateParameterEquals(List<ICrySLPredicateParameter> parameters, String key) {
		for (ICrySLPredicateParameter predicateParam : parameters) {
			if (key.equals(predicateParam.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Update the {@link PredicateHandler} to expect the given predicate on this seed, if the seed is in
	 * the given state. If {@code ensuredPred} is a {@link HiddenPredicate} (i.e. not all constraints are
	 * satisfied), the {@link PredicateHandler} is able to distinguish the predicates.
	 *
	 * @param ensuredPred the predicate to ensure on this seed
	 * @param stateNode the state, where the predicate is expected to be ensured
	 * @param currStmt the statement that leads to the state
	 */
	private void expectPredicateWhenThisObjectIsInState(EnsuredCrySLPredicate ensuredPred, State stateNode, ControlFlowGraph.Edge currStmt) {
		predicateHandler.expectPredicate(this, currStmt.getTarget(), ensuredPred.getPredicate());

		for (Cell<ControlFlowGraph.Edge, Val, TransitionFunction> e : results.asStatementValWeightTable().cellSet()) {
			if (containsTargetState(e.getValue(), stateNode)) {
				predicateHandler.addNewPred(this, e.getRowKey(), e.getColumnKey(), ensuredPred);
			}
		}
	}

	private boolean containsTargetState(TransitionFunction value, State stateNode) {
		return getTargetStates(value).contains(stateNode);
	}

	private Collection<? extends State> getTargetStates(TransitionFunction value) {
		Set<State> res = Sets.newHashSet();
		for (ITransition t : value.values()) {
			if (t.to() != null)
				res.add(t.to());
		}
		return res;
	}

	/**
	 * Ensure a predicate on another object. The predicate is added to the other seed's ensured predicates
	 *
	 * @param ensPred the predicate to ensure
	 * @param currStmt the statement that ensures the predicate
	 * @param accessGraph holds the value for the other seed's type
	 */
	private void expectPredicateOnOtherObject(EnsuredCrySLPredicate ensPred, Statement currStmt, Val accessGraph) {
		boolean specificationExists = false;

		// Check, whether there is a specification (i.e. a CrySL rule) for the target object
		for (ClassSpecification spec : cryptoScanner.getClassSpecifications()) {
			if (accessGraph.isNull()) {
				continue;
			}

			Type baseType = accessGraph.getType();
			if (!baseType.isRefType()) {
				continue;
			}

			// TODO Use refType (return type) or static type?
			if (baseType.getWrappedClass().getName().equals(spec.getRule().getClassName())) {
				Collection<Statement> successors = currStmt.getMethod().getControlFlowGraph().getSuccsOf(currStmt);

				// The initial edge for a seed has to contain the instantiation statement as start element
				for (Statement statement : successors) {
					ControlFlowGraph.Edge edge = new ControlFlowGraph.Edge(currStmt, statement);

					AnalysisSeedWithSpecification seed = cryptoScanner.getOrCreateSeedWithSpec(new AnalysisSeedWithSpecification(cryptoScanner, edge, accessGraph, spec));
					seed.addEnsuredPredicateFromOtherRule(ensPred);
				}

				specificationExists = true;
			}
		}

		// If no specification has been found, create a seed without a specification
		if (!specificationExists) {
            Collection<Statement> successors = currStmt.getMethod().getControlFlowGraph().getSuccsOf(currStmt);

            for (Statement statement : successors) {
                ControlFlowGraph.Edge edge = new ControlFlowGraph.Edge(currStmt, statement);

                AnalysisSeedWithEnsuredPredicate seed = cryptoScanner.getOrCreateSeed(new Node<>(edge, accessGraph));
                predicateHandler.expectPredicate(seed, currStmt, ensPred.getPredicate());
                seed.addEnsuredPredicate(ensPred);
            }
		}
	}

	private void addEnsuredPredicateFromOtherRule(EnsuredCrySLPredicate ensuredCrySLPredicate) {
		indirectlyEnsuredPredicates.add(ensuredCrySLPredicate);
		if (results == null) {
			return;
		}

		activateIndirectlyEnsuredPredicates();
	}

	/**
	 * Activate the predicates that were ensured from other seeds and passed to this seed
	 */
	private void activateIndirectlyEnsuredPredicates() {
		for (EnsuredCrySLPredicate pred : indirectlyEnsuredPredicates) {
			Collection<ICrySLPredicateParameter> parameters = pred.getPredicate().getParameters();
			String specName = spec.getRule().getClassName();
			boolean hasThisParameter = parameters.stream().anyMatch(p -> p instanceof CrySLObject && ((CrySLObject) p).getJavaType().equals(specName));

			if (!hasThisParameter) {
				continue;
			}

			/* Replace the original parameter corresponding to this seed with 'this'. For example,
			 * the KeyGenerator ensures 'generatedKey[key, algorithm]' on a SecretKey 'key'. Therefore,
			 * the seed for the SecretKey ensures 'generated[this, algorithm]'.
			 */
			List<ICrySLPredicateParameter> updatedParams = parameters.stream().map(
					p -> p instanceof CrySLObject && ((CrySLObject) p).getJavaType().equals(specName) ?
							new CrySLObject("this", specName) : p).collect(Collectors.toList());

			CrySLPredicate updatedPred = new CrySLPredicate(null, pred.getPredicate().getPredName(), updatedParams, false);

			EnsuredCrySLPredicate predWithThis;
			if (pred instanceof HiddenPredicate) {
				HiddenPredicate hiddenPredicate = (HiddenPredicate) pred;
				predWithThis = new HiddenPredicate(updatedPred, hiddenPredicate.getParametersToValues(), hiddenPredicate.getGeneratingSeed(), hiddenPredicate.getType());
			} else {
				predWithThis = new EnsuredCrySLPredicate(updatedPred, pred.getParametersToValues());
			}

			/* Add the predicate with 'this' to the ensured predicates, check the required predicate constraints
			 * and ensure it in all accepting states that do not negate it
			 */
			addEnsuredPredicate(predWithThis);
			for (Cell<ControlFlowGraph.Edge, Val, TransitionFunction> c : results.asStatementValWeightTable().cellSet()) {
				Collection<? extends State> states = getTargetStates(c.getValue());

				for (State state : states) {
					if (isPredicateNegatingState(predWithThis.getPredicate(), state)) {
						continue;
					}

					if (state.isAccepting()) {
						predicateHandler.addNewPred(this, c.getRowKey(), c.getColumnKey(), predWithThis);
					}
				}
			}
		}
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

	private boolean isConditionalState(Set<StateNode> conditionalMethods, State state) {
		if (conditionalMethods == null)
			return false;
		for (StateNode s : conditionalMethods) {
			if (new WrappedState(s).equals(state)) {
				return true;
			}
		}
		return false;
	}

	private boolean isPredicateNegatingState(CrySLPredicate ensPred, State stateNode) {
		// Check, whether the predicate is negated in the given state
		for (CrySLPredicate negPred : spec.getRule().getNegatedPredicates()) {
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
				if (new WrappedState(s).equals(stateNode)) {
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
	 *                                Constraint checks                                  *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	/**
	 * Check the constraints from the CONSTRAINTS section
	 */
	private boolean checkInternalConstraints() {
		cryptoScanner.getAnalysisListener().beforeConstraintCheck(this);
		constraintSolver = new ConstraintSolver(this, allCallsOnObject.keySet(), cryptoScanner.getAnalysisListener());
		cryptoScanner.getAnalysisListener().checkedConstraints(this, constraintSolver.getRelConstraints());
		boolean constraintsSatisfied = (0 == constraintSolver.evaluateRelConstraints());
		cryptoScanner.getAnalysisListener().afterConstraintCheck(this);

		return constraintsSatisfied;
	}

	/**
	 * Check, whether the internal constraints and predicate constraints are satisfied.
	 * Requires a previous call to {@link #checkInternalConstraints()}
	 *
	 * @return true if all internal and required predicate constraints are satisfied
	 */
	private boolean isConstraintSystemSatisfied() {
		if (internalConstraintsSatisfied) {
			cryptoScanner.getAnalysisListener().beforePredicateCheck(this);
			boolean requiredPredicatesEnsured = checkPredicates().isEmpty();
			cryptoScanner.getAnalysisListener().afterPredicateCheck(this);

			return requiredPredicatesEnsured;
		}
		return false;
	}

	/**
	 * Check, whether all required predicates are satisfied, and return a set with all predicates that are not
	 * satisfied. If the set is empty, all required predicate constraints are satisfied.
	 *
	 * @return remainingPredicates predicates that are not satisfied
	 */
	public Collection<ISLConstraint> checkPredicates() {
		List<ISLConstraint> requiredPredicates = Lists.newArrayList();
		for (ISLConstraint con : constraintSolver.getRequiredPredicates()) {
			if (!ConstraintSolver.predefinedPreds.contains((con instanceof RequiredCrySLPredicate) ? ((RequiredCrySLPredicate) con).getPred().getPredName()
					: ((AlternativeReqPredicate) con).getAlternatives().get(0).getPredName())) {
				requiredPredicates.add(con);
			}
		}
		Set<ISLConstraint> remainingPredicates = Sets.newHashSet(requiredPredicates);

		for (ISLConstraint pred : requiredPredicates) {
			if (pred instanceof RequiredCrySLPredicate) {
				RequiredCrySLPredicate reqPred = (RequiredCrySLPredicate) pred;

				if (reqPred.getPred().isNegated()) {
					boolean violated = false;

					for (EnsuredCrySLPredicate ensPred : ensuredPredicates) {
						if (reqPred.getPred().equals(ensPred.getPredicate()) && doPredsMatch(reqPred.getPred(), ensPred)) {
							violated = true;
						}
					}

					if (!violated) {
						remainingPredicates.remove(pred);
					}
				} else {
					for (EnsuredCrySLPredicate ensPred : ensuredPredicates) {
						if (reqPred.getPred().equals(ensPred.getPredicate()) && doPredsMatch(reqPred.getPred(), ensPred)) {
							remainingPredicates.remove(pred);
						}
					}
				}
			} else if (pred instanceof AlternativeReqPredicate) {
				AlternativeReqPredicate alt = (AlternativeReqPredicate) pred;
				List<CrySLPredicate> alternatives = alt.getAlternatives();
				List<CrySLPredicate> positives = alternatives.stream().filter(e -> !e.isNegated()).collect(Collectors.toList());
				List<CrySLPredicate> negatives = alternatives.stream().filter(e -> e.isNegated()).collect(Collectors.toList());

				boolean satisfied = false;
				List<CrySLPredicate> ensuredNegatives = alternatives.stream().filter(e -> e.isNegated()).collect(Collectors.toList());

				for (EnsuredCrySLPredicate ensPred : ensuredPredicates) {
					// Check if any positive alternative is satisfied by the ensured predicate
					if (positives.stream().anyMatch(e -> e.equals(ensPred.getPredicate()) && doPredsMatch(e, ensPred))) {
						satisfied = true;
					}

					// Negated alternatives that are ensured are not satisfied
					List<CrySLPredicate> violatedNegAlternatives = negatives.stream().filter(e -> e.equals(ensPred.getPredicate()) && doPredsMatch(e, ensPred)).collect(Collectors.toList());
					ensuredNegatives.removeAll(violatedNegAlternatives);
				}

				if (satisfied || !ensuredNegatives.isEmpty()) {
					remainingPredicates.remove(pred);
				}
			}
		}

		for (ISLConstraint rem : Lists.newArrayList(remainingPredicates)) {
			if (rem instanceof RequiredCrySLPredicate) {
				RequiredCrySLPredicate singlePred = (RequiredCrySLPredicate) rem;
				if (isPredConditionSatisfied(singlePred.getPred())) {
					remainingPredicates.remove(singlePred);
				}
			} else if (rem instanceof AlternativeReqPredicate) {
				List<CrySLPredicate> altPred = ((AlternativeReqPredicate) rem).getAlternatives();
				if (altPred.parallelStream().anyMatch(e -> isPredConditionSatisfied(e))) {
					remainingPredicates.remove(rem);
				}
			}
		}
		return remainingPredicates;
	}

	/**
	 * Check for a predicate A =&gt; B, whether the condition A of B is satisfied
	 *
	 * @param pred the predicate to be checked
	 * @return true if the condition is satisfied
	 */
	private boolean isPredConditionSatisfied(CrySLPredicate pred) {
		return pred.getConstraint().map(conditional -> {
			EvaluableConstraint evalCons = EvaluableConstraint.getInstance(conditional, constraintSolver);
			evalCons.evaluate();
			return evalCons.hasErrors();
		}).orElse(false);
	}

	public Collection<AbstractError> retrieveErrorsForPredCondition(CrySLPredicate pred) {
		// Check, whether the predicate has a condition
		if (!pred.getConstraint().isPresent()) {
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

				for (CallSiteWithParamIndex cswpi : ensPred.getParametersToValues().keySet()) {
					if (cswpi.getVarName().equals(parameterI)) {
						actVals = retrieveValueFromUnit(cswpi, ensPred.getParametersToValues().get(cswpi));
					}
				}
				for (CallSiteWithParamIndex cswpi : parameterAnalysis.getCollectedValues().keySet()) {
					if (cswpi.getVarName().equals(var)) {
						expVals = retrieveValueFromUnit(cswpi, parameterAnalysis.getCollectedValues().get(cswpi));
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
					actVals = actVals.parallelStream().map(e -> e.toLowerCase()).collect(Collectors.toList());
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
			Collection<HiddenPredicate> hiddenPredicatesEnsuringReqPred = hiddenPredicates.parallelStream().filter(p -> p.getPredicate().equals(pred) && doPredsMatch(pred, p)).collect(Collectors.toList());
			reqPredError.addHiddenPredicates(hiddenPredicatesEnsuringReqPred);
		}
	}

	private Collection<String> retrieveValueFromUnit(CallSiteWithParamIndex cswpi, Collection<ExtractedValue> collection) {
		Collection<String> values = new ArrayList<String>();
		for (ExtractedValue q : collection) {
			Statement statement = q.stmt().getTarget();

			if (cswpi.stmt().equals(q.stmt())) {
				if (statement.isAssign()) {
					Val leftOp = statement.getLeftOp();
					//values.add(retrieveConstantFromValue(((AssignStmt) u).getRightOp().getUseBoxes().get(cswpi.getIndex()).getValue()));
				} else {
					//values.add(retrieveConstantFromValue(u.getUseBoxes().get(cswpi.getIndex()).getValue()));
				}
			} else if (statement.isAssign()) {
				Val rightSide = statement.getRightOp();
				if (rightSide.isConstant()) {
					values.add(retrieveConstantFromValue(rightSide));
				} else {
					// final List<ValueBox> useBoxes = rightSide.getUseBoxes();

					// varVal.put(callSite.getVarName(),
					// retrieveConstantFromValue(useBoxes.get(callSite.getIndex()).getValue()));
				}
			}
			// if (u instanceof AssignStmt) {
			// final List<ValueBox> useBoxes = ((AssignStmt) u).getRightOp().getUseBoxes();
			// if (!(useBoxes.size() <= cswpi.getIndex())) {
			// values.add(retrieveConstantFromValue(useBoxes.get(cswpi.getIndex()).getValue()));
			// }
			// } else if (cswpi.getStmt().equals(u)) {
			// values.add(retrieveConstantFromValue(cswpi.getStmt().getUseBoxes().get(cswpi.getIndex()).getValue()));
			// }
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

	private final static List<String> trackedTypes = Arrays.asList("java.lang.String", "int", "java.lang.Integer");

	private boolean isOfNonTrackableType(String varName) {
		for (Entry<String, String> object : spec.getRule().getObjects()) {
			if (object.getKey().equals(varName) && trackedTypes.contains(object.getValue())) {
				return false;
			}
		}
		return true;
	}

	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 *                               Additional methods	                                 *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	public ClassSpecification getSpec() {
		return spec;
	}

	public ExtractParameterAnalysis getParameterAnalysis() {
		return parameterAnalysis;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((spec == null) ? 0 : spec.hashCode());
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
		if (spec == null) {
			if (other.spec != null)
				return false;
		} else if (!spec.equals(other.spec))
			return false;
		return true;
	}

	public boolean isSecure() {
		return secure;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}

}
