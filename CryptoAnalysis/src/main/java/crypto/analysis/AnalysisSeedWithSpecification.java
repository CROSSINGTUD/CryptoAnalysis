package crypto.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import boomerang.debugger.Debugger;
import boomerang.jimple.AllocVal;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import boomerang.results.ForwardBoomerangResults;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.TypestateError;
import crypto.constraints.ConstraintSolver;
import crypto.constraints.ConstraintSolver.EvaluableConstraint;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractParameterAnalysis;
import crypto.extractparameter.ExtractedValue;
import crypto.interfaces.ICryptSLPredicateParameter;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CryptSLCondPredicate;
import crypto.rules.CryptSLMethod;
import crypto.rules.CryptSLObject;
import crypto.rules.CryptSLPredicate;
import crypto.rules.StateNode;
import crypto.rules.TransitionEdge;
import crypto.typestate.CryptSLMethodToSootMethod;
import crypto.typestate.ErrorStateNode;
import crypto.typestate.ExtendedIDEALAnaylsis;
import crypto.typestate.SootBasedStateMachineGraph;
import crypto.typestate.WrappedState;
import ideal.IDEALSeedSolver;
import soot.IntType;
import soot.Local;
import soot.RefType;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.ThrowStmt;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;
import typestate.finiteautomata.ITransition;
import typestate.finiteautomata.State;

public class AnalysisSeedWithSpecification extends IAnalysisSeed {

	private final ClassSpecification spec;
	private ExtendedIDEALAnaylsis analysis;
	private ForwardBoomerangResults<TransitionFunction> results;
	private Collection<EnsuredCryptSLPredicate> ensuredPredicates = Sets.newHashSet();
	private Multimap<Statement, State> typeStateChange = HashMultimap.create();
	private Collection<EnsuredCryptSLPredicate> indirectlyEnsuredPredicates = Sets.newHashSet();
	private Set<RequiredCryptSLPredicate> missingPredicates = Sets.newHashSet();
	private ConstraintSolver constraintSolver;
	private boolean internalConstraintSatisfied;
	protected Map<Statement, SootMethod> allCallsOnObject = Maps.newHashMap();
	private ExtractParameterAnalysis parameterAnalysis;

	public AnalysisSeedWithSpecification(CryptoScanner cryptoScanner, Statement stmt, Val val,
			ClassSpecification spec) {
		super(cryptoScanner, stmt, val, spec.getFSM().getInitialWeight(stmt));
		this.spec = spec;
		this.analysis = new ExtendedIDEALAnaylsis() {

			@Override
			public SootBasedStateMachineGraph getStateMachine() {
				return spec.getFSM();
			}

			@Override
			protected BiDiInterproceduralCFG<Unit, SootMethod> icfg() {
				return cryptoScanner.icfg();
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

	@Override
	public String toString() {
		return "AnalysisSeed [" + super.toString() + " with spec " + spec.getRule().getClassName() + "]";
	}

	public void execute() {
		cryptoScanner.getAnalysisListener().seedStarted(this);
		runTypestateAnalysis();
		if(results == null)
			//Timeout occured.
			return;
		allCallsOnObject = results.getInvokedMethodOnInstance();
		runExtractParameterAnalysis();
		checkInternalConstraints();

		Multimap<Statement, State> unitToStates = HashMultimap.create();
		for (Cell<Statement, Val, TransitionFunction> c : results.asStatementValWeightTable().cellSet()) {
			unitToStates.putAll(c.getRowKey(), getTargetStates(c.getValue()));
			for (EnsuredCryptSLPredicate pred : indirectlyEnsuredPredicates) {
				// TODO only maintain indirectly ensured predicate as long as they are not
				// killed by the rule
				predicateHandler.addNewPred(this, c.getRowKey(), c.getColumnKey(), pred);
			}
		}

		computeTypestateErrorUnits();
		computeTypestateErrorsForEndOfObjectLifeTime();
		
		cryptoScanner.getAnalysisListener().onSeedFinished(this, results);
		cryptoScanner.getAnalysisListener().collectedValues(this, parameterAnalysis.getCollectedValues());
	}


	private void checkInternalConstraints() {
		cryptoScanner.getAnalysisListener().beforeConstraintCheck(this);
		constraintSolver = new ConstraintSolver(this, allCallsOnObject.keySet(), cryptoScanner.getAnalysisListener());
		cryptoScanner.getAnalysisListener().checkedConstraints(this, constraintSolver.getRelConstraints());
		internalConstraintSatisfied = (0 == constraintSolver.evaluateRelConstraints());
		cryptoScanner.getAnalysisListener().afterConstraintCheck(this);
	}

	private void runTypestateAnalysis() {
		analysis.run(this);
		results = analysis.getResults();
	}

	private void runExtractParameterAnalysis() {
		this.parameterAnalysis = new ExtractParameterAnalysis(this.cryptoScanner, allCallsOnObject, spec.getFSM());
		this.parameterAnalysis .run();
	}
	
	private void computeTypestateErrorUnits() {
		Set<Statement> allTypestateChangeStatements = Sets.newHashSet();
		for (Cell<Statement, Val, TransitionFunction> c : results.asStatementValWeightTable().cellSet()) {
			allTypestateChangeStatements.addAll(c.getValue().getLastStateChangeStatements());
		}
		for (Cell<Statement, Val, TransitionFunction> c : results.asStatementValWeightTable().cellSet()) {
			Statement curr = c.getRowKey();
			for (Unit pred : cryptoScanner.icfg().getPredsOf(curr.getUnit().get())) {
				Statement typeStateChangeStatement = new Statement((Stmt) pred, curr.getMethod());
				if(allTypestateChangeStatements.contains(typeStateChangeStatement)) {
					Collection<? extends State> targetStates = getTargetStates(c.getValue());
					for (State newStateAtCurr : targetStates) {
						typeStateChangeAtStatement(typeStateChangeStatement, newStateAtCurr);
					}
				}
				
			}
		}
	}

	private void computeTypestateErrorsForEndOfObjectLifeTime() {
		Table<Statement, Val, TransitionFunction> endPathOfPropagation = results.getObjectDestructingStatements();

		for (Cell<Statement, Val, TransitionFunction> c : endPathOfPropagation.cellSet()) {
			Set<SootMethod> expectedMethodsToBeCalled = Sets.newHashSet();
			for (ITransition n : c.getValue().values()) {
				if (n.to() == null)
					continue;
				if (!n.to().isAccepting()) {
					if (n.to() instanceof WrappedState) {
						WrappedState wrappedState = (WrappedState) n.to();
						for (TransitionEdge t : spec.getRule().getUsagePattern().getAllTransitions()) {
							if (t.getLeft().equals(wrappedState.delegate())) {
								Collection<SootMethod> converted = CryptSLMethodToSootMethod.v().convert(t.getLabel());
								expectedMethodsToBeCalled.addAll(converted);
							}
						}
					}
				}
			}
			if (!expectedMethodsToBeCalled.isEmpty()) {
				Statement s = c.getRowKey();
				Val val = c.getColumnKey();
				if(!(s.getUnit().get() instanceof ThrowStmt)){
					cryptoScanner.getAnalysisListener().reportError(new IncompleteOperationError(s, val, getSpec().getRule(), this, 
							expectedMethodsToBeCalled));
				}
			}
		}
	}

	private void typeStateChangeAtStatement(Statement curr, State stateNode) {
		if(typeStateChange.put(curr, stateNode)) {
			if (stateNode instanceof ErrorStateNode) {
				ErrorStateNode errorStateNode = (ErrorStateNode) stateNode;
				cryptoScanner.getAnalysisListener().reportError(new TypestateError(curr, getSpec().getRule(), this, errorStateNode.getExpectedCalls()));
			}
		}
		onAddedTypestateChange(curr, stateNode);
	}

	private void onAddedTypestateChange(Statement curr, State stateNode) {
		for (CryptSLPredicate predToBeEnsured : spec.getRule().getPredicates()) {
			if (predToBeEnsured.isNegated()) {
				continue;
			}
			
			if (isPredicateGeneratingState(predToBeEnsured, stateNode)) {
				ensuresPred(predToBeEnsured, curr, stateNode);
			}
		}
	}

	private void ensuresPred(CryptSLPredicate predToBeEnsured, Statement currStmt, State stateNode) {
		if (predToBeEnsured.isNegated()) {
			return;
		}
		boolean satisfiesConstraintSytem = checkConstraintSystem();

		for (ICryptSLPredicateParameter predicateParam : predToBeEnsured.getParameters()) {
			if (predicateParam.getName().equals("this")) {
				expectPredicateWhenThisObjectIsInState(stateNode, currStmt, predToBeEnsured, satisfiesConstraintSytem);
			}
		}
		if (currStmt.isCallsite()) {
			InvokeExpr ie = ((Stmt) currStmt.getUnit().get()).getInvokeExpr();
			SootMethod invokedMethod = ie.getMethod();
			Collection<CryptSLMethod> convert = CryptSLMethodToSootMethod.v().convert(invokedMethod);

			for (CryptSLMethod cryptSLMethod : convert) {
				Entry<String, String> retObject = cryptSLMethod.getRetObject();
				if (!retObject.getKey().equals("_") && currStmt.getUnit().get() instanceof AssignStmt
						&& predicateParameterEquals(predToBeEnsured.getParameters(), retObject.getKey())) {
					AssignStmt as = (AssignStmt) currStmt.getUnit().get();
					Value leftOp = as.getLeftOp();
					AllocVal val = new AllocVal(leftOp, currStmt.getMethod(), as.getRightOp(), new Statement(as, currStmt.getMethod()));
					expectPredicateOnOtherObject(predToBeEnsured, currStmt, val, satisfiesConstraintSytem);
				}
				int i = 0;
				for (Entry<String, String> p : cryptSLMethod.getParameters()) {
					if (predicateParameterEquals(predToBeEnsured.getParameters(), p.getKey())) {
						Value param = ie.getArg(i);
						if (param instanceof Local) {
							Val val = new Val(param, currStmt.getMethod());
							expectPredicateOnOtherObject(predToBeEnsured, currStmt, val, satisfiesConstraintSytem);
						}
					}
					i++;
				}

			}

		}
	}

	private boolean predicateParameterEquals(List<ICryptSLPredicateParameter> parameters, String key) {
		for (ICryptSLPredicateParameter predicateParam : parameters) {
			if (key.equals(predicateParam.getName())) {
				return true;
			}
		}
		return false;
	}

	private void expectPredicateOnOtherObject(CryptSLPredicate predToBeEnsured, Statement currStmt, Val accessGraph,
			boolean satisfiesConstraintSytem) {
		//TODO refactor this method.
		boolean matched = false;
		for (ClassSpecification spec : cryptoScanner.getClassSpecifictions()) {
			if (accessGraph.value() == null) {
				continue;
			}
			Type baseType = accessGraph.value().getType();
			if (baseType instanceof RefType) {
				RefType refType = (RefType) baseType;
				if (spec.getRule().getClassName().equals(refType.getSootClass().getName())) {
					if (satisfiesConstraintSytem) {
					AnalysisSeedWithSpecification seed = cryptoScanner.getOrCreateSeedWithSpec(
							new AnalysisSeedWithSpecification(cryptoScanner, currStmt, accessGraph, spec));
					matched = true;
						seed.addEnsuredPredicateFromOtherRule(
								new EnsuredCryptSLPredicate(predToBeEnsured, parameterAnalysis.getCollectedValues()));
					}
				}
			}
		}
		if (matched)
			return;
		AnalysisSeedWithEnsuredPredicate seed = cryptoScanner
				.getOrCreateSeed(new Node<Statement, Val>(currStmt, accessGraph));
		predicateHandler.expectPredicate(seed, currStmt, predToBeEnsured);
		if (satisfiesConstraintSytem) {
			seed.addEnsuredPredicate(new EnsuredCryptSLPredicate(predToBeEnsured, parameterAnalysis.getCollectedValues()));
		} else {
			missingPredicates.add(new RequiredCryptSLPredicate(predToBeEnsured, currStmt));
		}
	}

	private void addEnsuredPredicateFromOtherRule(EnsuredCryptSLPredicate ensuredCryptSLPredicate) {
		indirectlyEnsuredPredicates.add(ensuredCryptSLPredicate);
		if (results == null)
			return;
		for (Cell<Statement, Val, TransitionFunction> c : results.asStatementValWeightTable().cellSet()) {
			for (EnsuredCryptSLPredicate pred : indirectlyEnsuredPredicates) {
				predicateHandler.addNewPred(this, c.getRowKey(), c.getColumnKey(), pred);
			}
		}
	}

	private void expectPredicateWhenThisObjectIsInState(State stateNode, Statement currStmt,
			CryptSLPredicate predToBeEnsured, boolean satisfiesConstraintSytem) {
		predicateHandler.expectPredicate(this, currStmt, predToBeEnsured);

		if (!satisfiesConstraintSytem)
			return;
		for (Cell<Statement, Val, TransitionFunction> e : results.asStatementValWeightTable().cellSet()) {
			// TODO check for any reachable state that don't kill
			// predicates.
			if (containsTargetState(e.getValue(), stateNode)) {
				predicateHandler.addNewPred(this, e.getRowKey(), e.getColumnKey(),
						new EnsuredCryptSLPredicate(predToBeEnsured, parameterAnalysis.getCollectedValues()));
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

	private boolean checkConstraintSystem() {
		cryptoScanner.getAnalysisListener().beforePredicateCheck(this);
		List<ISLConstraint> relConstraints = constraintSolver.getRelConstraints();
		boolean checkPredicates = checkPredicates(relConstraints);
		cryptoScanner.getAnalysisListener().afterPredicateCheck(this);
		if (!checkPredicates)
			return false;
		return internalConstraintSatisfied;
	}

	private boolean checkPredicates(Collection<ISLConstraint> relConstraints) {
		List<RequiredCryptSLPredicate> requiredPredicates = Lists.newArrayList();
		for (RequiredCryptSLPredicate con : constraintSolver.getRequiredPredicates()) {
			if (!ConstraintSolver.predefinedPreds.contains(con.getPred().getPredName())) {
				requiredPredicates.add(con);
			}
		}
		Set<RequiredCryptSLPredicate> remainingPredicates = Sets.newHashSet(requiredPredicates);
		missingPredicates.removeAll(remainingPredicates);
		
		for (RequiredCryptSLPredicate pred : requiredPredicates) {
			if (pred.getPred().isNegated()) {
				for (EnsuredCryptSLPredicate ensPred : ensuredPredicates) {
					if (ensPred.getPredicate().equals(pred.getPred()))
						return false;
				}
				remainingPredicates.remove(pred);
			} else {
				for (EnsuredCryptSLPredicate ensPred : ensuredPredicates) {
					if (ensPred.getPredicate().equals(pred.getPred()) && doPredsMatch(pred.getPred(), ensPred)) {
						remainingPredicates.remove(pred);
					}
				}
			}
		}
		for (RequiredCryptSLPredicate rem : Lists.newArrayList(remainingPredicates)) {
			final ISLConstraint conditional = rem.getPred().getConstraint();
			if (conditional != null) {
				EvaluableConstraint evalCons = constraintSolver.createConstraint(conditional);
				evalCons.evaluate();
				if (evalCons.hasErrors()) {
					remainingPredicates.remove(rem);
				}
			}
		}

		this.missingPredicates.addAll(remainingPredicates);
		return remainingPredicates.isEmpty();
	}

	private boolean doPredsMatch(CryptSLPredicate pred, EnsuredCryptSLPredicate ensPred) {
		boolean requiredPredicatesExist = true;
		for (int i = 0; i < pred.getParameters().size(); i++) {
			String var = pred.getParameters().get(i).getName();
			if (isOfNonTrackableType(var)) {
				continue;
			} else if (pred.getInvolvedVarNames().contains(var)) {

				final String parameterI = ensPred.getPredicate().getParameters().get(i).getName();
				Collection<String> actVals = null;
				Collection<String> expVals = null;

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
				if (pred.getParameters().get(i) instanceof CryptSLObject) {
					CryptSLObject obj = (CryptSLObject) pred.getParameters().get(i);
					if (obj.getSplitter() != null) {
						splitter = obj.getSplitter().getSplitter();
						index = obj.getSplitter().getIndex();
					}
				}
				for (String foundVal : expVals) {
					if (index > -1) {
						foundVal = foundVal.split(splitter)[index];
					}
					requiredPredicatesExist &= actVals.contains(foundVal);
				}
			} else {
				requiredPredicatesExist = false;
			}
		}
		return pred.isNegated() != requiredPredicatesExist;
	}

	private Collection<String> retrieveValueFromUnit(CallSiteWithParamIndex cswpi, Collection<ExtractedValue> collection) {
		Collection<String> values = new ArrayList<String>();
		for (ExtractedValue q : collection) {
			Unit u = q.stmt().getUnit().get();
			if (cswpi.stmt().equals(q.stmt())) {
				if (u instanceof AssignStmt) {
					values.add(retrieveConstantFromValue(
							((AssignStmt) u).getRightOp().getUseBoxes().get(cswpi.getIndex()).getValue()));
				} else {
					values.add(retrieveConstantFromValue(u.getUseBoxes().get(cswpi.getIndex()).getValue()));
				}
			} else if (u instanceof AssignStmt) {
				final Value rightSide = ((AssignStmt) u).getRightOp();
				if (rightSide instanceof Constant) {
					values.add(retrieveConstantFromValue(rightSide));
				} else {
					final List<ValueBox> useBoxes = rightSide.getUseBoxes();

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

	private String retrieveConstantFromValue(Value val) {
		if (val instanceof StringConstant) {
			return ((StringConstant) val).value;
		} else if (val instanceof IntConstant || val.getType() instanceof IntType) {
			return val.toString();
		} else {
			return "";
		}
	}

	private final static List<String> trackedTypes = Arrays.asList("java.lang.String", "int", "java.lang.Integer");

	private boolean isOfNonTrackableType(String varName) {
		for (Entry<String, String> object : spec.getRule().getObjects()) {
			if (object.getValue().equals(varName) && trackedTypes.contains(object.getKey())) {
				return false;
			}
		}
		return true;
	}

	public ClassSpecification getSpec() {
		return spec;
	}

	public void addEnsuredPredicate(EnsuredCryptSLPredicate ensPred) {
		if (ensuredPredicates.add(ensPred)) {
			for (Entry<Statement, State> e : typeStateChange.entries())
				onAddedTypestateChange(e.getKey(), e.getValue());
		}
	}

	private boolean isPredicateGeneratingState(CryptSLPredicate ensPred, State stateNode) {
		return ensPred instanceof CryptSLCondPredicate
				&& isConditionalState(((CryptSLCondPredicate) ensPred).getConditionalMethods(), stateNode)
				|| (!(ensPred instanceof CryptSLCondPredicate) && stateNode.isAccepting());
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


	public Set<RequiredCryptSLPredicate> getMissingPredicates() {
		return missingPredicates;
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

}
