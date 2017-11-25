package crypto.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import boomerang.WeightedBoomerang;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.analysis.util.StmtWithMethod;
import crypto.rules.CryptSLCondPredicate;
import crypto.rules.CryptSLMethod;
import crypto.rules.CryptSLObject;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLRule;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.typestate.CallSiteWithParamIndex;
import crypto.typestate.CryptSLMethodToSootMethod;
import crypto.typestate.ErrorStateNode;
import crypto.typestate.ExtendedIDEALAnaylsis;
import crypto.typestate.SootBasedStateMachineGraph;
import crypto.typestate.WrappedState;
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
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;
import typestate.finiteautomata.ITransition;
import typestate.finiteautomata.State;
import typestate.interfaces.ICryptSLPredicateParameter;
import typestate.interfaces.ISLConstraint;

public class AnalysisSeedWithSpecification extends IAnalysisSeed {

	private final ClassSpecification spec;
	private ExtendedIDEALAnaylsis analysis;
	private Multimap<CallSiteWithParamIndex, Unit> parametersToValues = HashMultimap.create();
	private HashBasedTable<Unit, Val, TransitionFunction> results;
	private Collection<EnsuredCryptSLPredicate> ensuredPredicates = Sets.newHashSet();
	private Multimap<Unit, State> typeStateChange = HashMultimap.create();
	private Collection<EnsuredCryptSLPredicate> indirectlyEnsuredPredicates = Sets.newHashSet();
	private Set<CryptSLPredicate> missingPredicates = Sets.newHashSet();
	private ConstraintSolver constraintSolver;
	private boolean internalConstraintSatisfied;
	protected Collection<Unit> allCallsOnObject = Sets.newHashSet();

	public AnalysisSeedWithSpecification(CryptoScanner cryptoScanner, Statement stmt, Val val, ClassSpecification spec) {
		super(cryptoScanner,stmt,val,spec.getFSM().getInitialWeight());
		this.spec = spec;
		analysis = new ExtendedIDEALAnaylsis(){

			@Override
			public SootBasedStateMachineGraph getStateMachine() {
				return spec.getFSM();
			}

			@Override
			protected BiDiInterproceduralCFG<Unit, SootMethod> icfg() {
				return cryptoScanner.icfg();
			}

			@Override
			public CrySLAnalysisResultsAggregator analysisListener() {
				return null;
			}};
	}


	@Override
	public String toString() {
		return "AnalysisSeed [" + super.toString() + " with spec " + spec.getRule().getClassName() + "]";
	}

	public void execute() {
		cryptoScanner.getAnalysisListener().seedStarted(this);
		WeightedBoomerang<TransitionFunction> solver = analysis.run(this);
		parametersToValues = analysis.getCollectedValues();
		allCallsOnObject = analysis.getInvokedMethodOnInstance();
		cryptoScanner.getAnalysisListener().onSeedFinished(this, solver);
		onSeedFinished(solver);
		cryptoScanner.getAnalysisListener().seedFinished(this);
		cryptoScanner.getAnalysisListener().collectedValues(this, analysis.getCollectedValues());
		final CryptSLRule rule = spec.getRule();
		for (ISLConstraint cons : rule.getConstraints()) {
			if (cons instanceof CryptSLPredicate && ((CryptSLPredicate) cons).isNegated()) {
				cryptoScanner.addDisallowedPredicatePair(rule.getPredicates().get(0), ((CryptSLPredicate) cons).setNegated(false));
			}
		}
	}

	public void onSeedFinished(WeightedBoomerang<TransitionFunction> solver) {
		// Merge all information (all access graph here point to the seed
		// object)
		cryptoScanner.getAnalysisListener().beforeConstraintCheck(this);
		constraintSolver = new ConstraintSolver(cryptoScanner, spec, parametersToValues, allCallsOnObject, new ConstraintReporter() {
			@Override
			public void constraintViolated(ISLConstraint con, StmtWithMethod unit) {
				cryptoScanner.getAnalysisListener().constraintViolation(AnalysisSeedWithSpecification.this, con, unit);
			}

			@Override
			public void callToForbiddenMethod(ClassSpecification classSpecification, Unit callSite) {
				cryptoScanner.getAnalysisListener().callToForbiddenMethod(classSpecification, new StmtWithMethod(callSite, cryptoScanner.icfg().getMethodOf(callSite)), Lists.newLinkedList());
			}
		});
		cryptoScanner.getAnalysisListener().checkedConstraints(this,constraintSolver.getRelConstraints());
		internalConstraintSatisfied = (0 == constraintSolver.evaluateRelConstraints());
		cryptoScanner.getAnalysisListener().afterConstraintCheck(this);
		results = transform(analysis.getResults(this));
		Multimap<Unit, State> unitToStates = HashMultimap.create();
		for (Cell<Unit, Val, TransitionFunction> c : results.cellSet()) {
			unitToStates.putAll(c.getRowKey(), getTargetStates(c.getValue()));
			for (EnsuredCryptSLPredicate pred : indirectlyEnsuredPredicates) {
				//TODO only maintain indirectly ensured predicate as long as they are not killed by the rule
				cryptoScanner.addNewPred(this, c.getRowKey(), c.getColumnKey(), pred);
			}
		}
		
		computeTypestateErrorUnits(unitToStates);
		computeTypestateErrorsForEndOfObjectLifeTime(solver);
	}


	private HashBasedTable<Unit, Val, TransitionFunction> transform(
			Table<Statement, Val, TransitionFunction> table) {

		HashBasedTable<Unit, Val, TransitionFunction> out = HashBasedTable.create();
		for(Cell<Statement, Val, TransitionFunction> e : table.cellSet()){
			out.put(e.getRowKey().getUnit().get(), e.getColumnKey(), e.getValue());
		}
		return out;
	}


	private void computeTypestateErrorUnits(Multimap<Unit, State> unitToStates) {
		for (Unit curr : unitToStates.keySet()) {
			Collection<State> stateAtCurrMinusPred = Sets.newHashSet(unitToStates.get(curr));
			for (Unit pred : cryptoScanner.icfg().getPredsOf(curr)) {
				Collection<State> stateAtPred = unitToStates.get(pred);
				stateAtCurrMinusPred.removeAll(stateAtPred);
				for (State newStateAtCurr : stateAtCurrMinusPred) {
					typeStateChangeAtStatement(pred, newStateAtCurr);
					if(newStateAtCurr.equals(ErrorStateNode.v())){
						Set<SootMethod> expectedMethodCalls = expectedMethodsCallsFor(stateAtPred);
						cryptoScanner.getAnalysisListener().typestateErrorAt(this, new StmtWithMethod(pred, cryptoScanner.icfg().getMethodOf(pred)), expectedMethodCalls);
					}
				}
			}
		}
	}


	private Set<SootMethod> expectedMethodsCallsFor(Collection<State> stateAtPred) {
		Set<SootMethod> res = Sets.newHashSet();
		for(State s : stateAtPred){
			res.addAll(spec.getFSM().getEdgesOutOf(s));
		}
		return res;
	}


	private void computeTypestateErrorsForEndOfObjectLifeTime(WeightedBoomerang<TransitionFunction> solver) {
		Table<Statement, Val, TransitionFunction> endPathOfPropagation = solver.getObjectDestructingStatements(this);
		for (Cell<Statement, Val, TransitionFunction> c : endPathOfPropagation.cellSet()) {
			for (ITransition n : c.getValue().values()) {
				if (!n.to().isAccepting()) {
					Statement s = c.getRowKey();
					cryptoScanner.getAnalysisListener().typestateErrorEndOfLifeCycle(this, new StmtWithMethod(s.getUnit().get(), s.getMethod()));
				}
			}
		}
	}

	private void typeStateChangeAtStatement(Unit curr, State stateNode) {
		typeStateChange.put(curr, stateNode);
		onAddedTypestateChange(curr, stateNode);
	}

	private void onAddedTypestateChange(Unit curr, State stateNode) {
		for (CryptSLPredicate predToBeEnsured : spec.getRule().getPredicates()) {
			if (predToBeEnsured.isNegated()) {
				continue;
			}

			if (isPredicateGeneratingState(predToBeEnsured, stateNode)) {
				ensuresPred(predToBeEnsured, curr, stateNode);
			}
		}
	}

	private void ensuresPred(CryptSLPredicate predToBeEnsured, Unit currStmt, State stateNode) {
		if (predToBeEnsured.isNegated()) {
			return;
		}
		boolean satisfiesConstraintSytem = checkConstraintSystem();

		for (ICryptSLPredicateParameter predicateParam : predToBeEnsured.getParameters()) {
			if (predicateParam.getName().equals("this")) {
				expectPredicateWhenThisObjectIsInState(stateNode, currStmt, predToBeEnsured, satisfiesConstraintSytem);
			}
		}
		if (currStmt instanceof Stmt && ((Stmt) currStmt).containsInvokeExpr()) {
			InvokeExpr ie = ((Stmt) currStmt).getInvokeExpr();
			SootMethod invokedMethod = ie.getMethod();
			Collection<CryptSLMethod> convert = CryptSLMethodToSootMethod.v().convert(invokedMethod);

			for (CryptSLMethod cryptSLMethod : convert) {
				Entry<String, String> retObject = cryptSLMethod.getRetObject();
				if (!retObject.getKey().equals("_") && currStmt instanceof AssignStmt && predicateParameterEquals(predToBeEnsured.getParameters(),retObject.getKey())) {
					AssignStmt as = (AssignStmt) currStmt;
					Value leftOp = as.getLeftOp();
					Val val = new Val(leftOp, cryptoScanner.icfg().getMethodOf(currStmt));
					expectPredicateOnOtherObject(predToBeEnsured, currStmt, val, satisfiesConstraintSytem);
				}
				int i = 0;
				for (Entry<String, String> p : cryptSLMethod.getParameters()) {
					if(predicateParameterEquals(predToBeEnsured.getParameters(),p.getKey())){
						Value param = ie.getArg(i);
						if (param instanceof Local) {
							Val val = new Val(param, cryptoScanner.icfg().getMethodOf(currStmt));
							expectPredicateOnOtherObject(predToBeEnsured, currStmt, val, satisfiesConstraintSytem);
						}
					}
					i++;
				}

			}

		}
	}

	private boolean predicateParameterEquals(List<ICryptSLPredicateParameter> parameters, String key) {
		for (ICryptSLPredicateParameter predicateParam :parameters) {
			if (key.equals(predicateParam.getName())){
				return true;
			}
		}
		return false;
	}

	private void expectPredicateOnOtherObject(CryptSLPredicate predToBeEnsured, Unit currStmt, Val accessGraph, boolean satisfiesConstraintSytem) {
		boolean matched = false;
		for (ClassSpecification spec : cryptoScanner.getClassSpecifictions()) {
			if(accessGraph.value() == null){
				continue;
			}
			Type baseType = accessGraph.value().getType();
			if (baseType instanceof RefType) {
				RefType refType = (RefType) baseType;
				if (spec.getRule().getClassName().equals(refType.getSootClass().getShortName())) {
					AnalysisSeedWithSpecification seed = cryptoScanner.getOrCreateSeedWithSpec(
						new AnalysisSeedWithSpecification(cryptoScanner, new Statement((Stmt)currStmt,cryptoScanner.icfg().getMethodOf(currStmt)), accessGraph, spec));
					matched = true;
					if (satisfiesConstraintSytem)
						seed.addEnsuredPredicateFromOtherRule(new EnsuredCryptSLPredicate(predToBeEnsured, parametersToValues));
				}
			}
		}
		if (matched)
			return;
		AnalysisSeedWithEnsuredPredicate seed = cryptoScanner.getOrCreateSeed(new Node<Statement,Val>(new Statement((Stmt)currStmt,cryptoScanner.icfg().getMethodOf(currStmt)), accessGraph));
		cryptoScanner.expectPredicate(seed, currStmt, predToBeEnsured);
		if (satisfiesConstraintSytem) {
			seed.addEnsuredPredicate(new EnsuredCryptSLPredicate(predToBeEnsured, parametersToValues));
		}
	}

	private void addEnsuredPredicateFromOtherRule(EnsuredCryptSLPredicate ensuredCryptSLPredicate) {
		indirectlyEnsuredPredicates.add(ensuredCryptSLPredicate);
		if (results == null)
			return;
		for (Cell<Unit, Val, TransitionFunction> c : results.cellSet()) {
			for (EnsuredCryptSLPredicate pred : indirectlyEnsuredPredicates) {
				cryptoScanner.addNewPred(this, c.getRowKey(), c.getColumnKey(), pred);
			}
		}
	}

	private void expectPredicateWhenThisObjectIsInState(State stateNode, Unit currStmt, CryptSLPredicate predToBeEnsured, boolean satisfiesConstraintSytem) {
		cryptoScanner.expectPredicate(this, currStmt, predToBeEnsured);

		if (!satisfiesConstraintSytem)
			return;
		for (Cell<Unit, Val, TransitionFunction> e : results.cellSet()) {
			// TODO check for any reachable state that don't kill
			// predicates.
			if (containsTargetState(e.getValue(),stateNode)) {
				cryptoScanner.addNewPred(this, e.getRowKey(), e.getColumnKey(), new EnsuredCryptSLPredicate(predToBeEnsured, parametersToValues));
			}
		}
	}


	private boolean containsTargetState(TransitionFunction value, State stateNode) {
		//TODO we still need to implement PhaseII of IDE in IDEAL
		for(ITransition t : value.values()){
			if(t.to().equals(stateNode))
				return true;
		}
		return false;
	}

	private Iterable<? extends State> getTargetStates(TransitionFunction value) {
		//TODO we still need to implement PhaseII of IDE in IDEAL
		Set<State> res = Sets.newHashSet();
		for(ITransition t : value.values()){
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

	private boolean checkPredicates(List<ISLConstraint> relConstraints) {
		List<CryptSLPredicate> requiredPredicates = Lists.newArrayList();
		for (ISLConstraint con : relConstraints) {
			if (con instanceof CryptSLPredicate && !ConstraintSolver.predefinedPreds.contains(((CryptSLPredicate) con).getPredName())) {
				requiredPredicates.add((CryptSLPredicate) con);
			}
		}
		Set<CryptSLPredicate> remainingPredicates = Sets.newHashSet(requiredPredicates);
		for (CryptSLPredicate pred : requiredPredicates) {
			if (pred.isNegated()) {
				for (EnsuredCryptSLPredicate ensPred : ensuredPredicates) {
					if (ensPred.getPredicate().equals(pred))
						return false;
				}
				remainingPredicates.remove(pred);
			} else {
				for (EnsuredCryptSLPredicate ensPred : ensuredPredicates) {
					if (ensPred.getPredicate().equals(pred) && doPredsMatch(pred, ensPred)) {
						remainingPredicates.remove(pred);
					}
				}
			}
		}
		for (CryptSLPredicate rem : Lists.newArrayList(remainingPredicates)) {
			final ISLConstraint conditional = rem.getConstraint();
			if (conditional != null) {
				if (constraintSolver.evaluate(conditional) != null) {
					remainingPredicates.remove(rem);
				}
			}
		}
		
		this.missingPredicates  = Sets.newHashSet(remainingPredicates);
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
				for (CallSiteWithParamIndex cswpi : parametersToValues.keySet()) {
					if (cswpi.getVarName().equals(var)) {
						expVals = retrieveValueFromUnit(cswpi, parametersToValues.get(cswpi));
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

	private Collection<String> retrieveValueFromUnit(CallSiteWithParamIndex cswpi, Collection<Unit> collection) {
		Collection<String> values = new ArrayList<String>();
		for (Unit u : collection) {
			if (cswpi.stmt().getUnit().get().equals(u)) {
				if (u instanceof AssignStmt) {
					values.add(retrieveConstantFromValue(((AssignStmt) u).getRightOp().getUseBoxes().get(cswpi.getIndex()).getValue()));
				} else {
					values.add(retrieveConstantFromValue(u.getUseBoxes().get(cswpi.getIndex()).getValue()));
				}
			} else if (u instanceof AssignStmt) {
				final Value rightSide = ((AssignStmt) u).getRightOp();
				if (rightSide instanceof Constant) {
					values.add(retrieveConstantFromValue(rightSide));
				} else {
					final List<ValueBox> useBoxes = rightSide.getUseBoxes();

					//					varVal.put(callSite.getVarName(), retrieveConstantFromValue(useBoxes.get(callSite.getIndex()).getValue()));
				}
			}
			//			if (u instanceof AssignStmt) {
			//				final List<ValueBox> useBoxes = ((AssignStmt) u).getRightOp().getUseBoxes();
			//				if (!(useBoxes.size() <= cswpi.getIndex())) {
			//					values.add(retrieveConstantFromValue(useBoxes.get(cswpi.getIndex()).getValue()));
			//				} 
			//			} else 	if (cswpi.getStmt().equals(u)) {
			//				values.add(retrieveConstantFromValue(cswpi.getStmt().getUseBoxes().get(cswpi.getIndex()).getValue()));
			//			}
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
			for (Entry<Unit, State> e : typeStateChange.entries())
				onAddedTypestateChange(e.getKey(), e.getValue());
		}
	}

	private boolean isPredicateGeneratingState(CryptSLPredicate ensPred, State stateNode) {
		return ensPred instanceof CryptSLCondPredicate && isConditionalState(((CryptSLCondPredicate) ensPred).getConditionalMethods()
			,stateNode) || (!(ensPred instanceof CryptSLCondPredicate) && stateNode.isAccepting());
	}

	private boolean isConditionalState(Set<StateNode> conditionalMethods, State state) {
		for(StateNode s : conditionalMethods){
			if(state.equals(new WrappedState(s))){
				return true;
			}
		}
		return false;
	}


	@Override
	public boolean contradictsNegations() {
		return false;
	}
	
	public Set<CryptSLPredicate> getMissingPredicates() {
		return missingPredicates;
	}
	
	public Multimap<CallSiteWithParamIndex, Unit> getExtractedValues(){
		return parametersToValues;
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
