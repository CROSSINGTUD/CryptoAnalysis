package crypto.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import boomerang.callgraph.ObservableICFG;
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
import crypto.interfaces.ICrySLPredicateParameter;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CrySLCondPredicate;
import crypto.rules.CrySLConstraint;
import crypto.rules.CrySLMethod;
import crypto.rules.CrySLObject;
import crypto.rules.CrySLPredicate;
import crypto.rules.StateNode;
import crypto.rules.TransitionEdge;
import crypto.typestate.CrySLMethodToSootMethod;
import crypto.typestate.ExtendedIDEALAnaylsis;
import crypto.typestate.ReportingErrorStateNode;
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
import soot.jimple.InvokeStmt;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.ThrowStmt;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;
import typestate.finiteautomata.ITransition;
import typestate.finiteautomata.State;

public class AnalysisSeedWithSpecification extends IAnalysisSeed {

	private final ClassSpecification spec;
	private ExtendedIDEALAnaylsis analysis;
	private ForwardBoomerangResults<TransitionFunction> results;
	private Collection<EnsuredCrySLPredicate> ensuredPredicates = Sets.newHashSet();
	private Multimap<Statement, State> typeStateChange = HashMultimap.create();
	private Collection<EnsuredCrySLPredicate> indirectlyEnsuredPredicates = Sets.newHashSet();
	private Set<ISLConstraint> missingPredicates = Sets.newHashSet();
	private ConstraintSolver constraintSolver;
	private boolean internalConstraintSatisfied;
	protected Map<Statement, SootMethod> allCallsOnObject = Maps.newLinkedHashMap();
	private ExtractParameterAnalysis parameterAnalysis;
	private Set<ResultsHandler> resultHandlers = Sets.newHashSet();
	private boolean secure = true;

	public AnalysisSeedWithSpecification(CryptoScanner cryptoScanner, Statement stmt, Val val, ClassSpecification spec) {
		super(cryptoScanner, stmt, val, spec.getFSM().getInitialWeight(stmt));
		this.spec = spec;
		this.analysis = new ExtendedIDEALAnaylsis() {

			@Override
			public SootBasedStateMachineGraph getStateMachine() {
				return spec.getFSM();
			}

			@Override
			protected ObservableICFG<Unit, SootMethod> icfg() {
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
		if (results == null)
			// Timeout occured.
			return;
		allCallsOnObject = results.getInvokedMethodOnInstance();
		runExtractParameterAnalysis();
		checkInternalConstraints();

		Multimap<Statement, State> unitToStates = HashMultimap.create();
		for (Cell<Statement, Val, TransitionFunction> c : results.asStatementValWeightTable().cellSet()) {
			unitToStates.putAll(c.getRowKey(), getTargetStates(c.getValue()));
			for (EnsuredCrySLPredicate pred : indirectlyEnsuredPredicates) {
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
		if (results != null) {
			for (ResultsHandler handler : Lists.newArrayList(resultHandlers)) {
				handler.done(results);
			}
		}
	}

	public void registerResultsHandler(ResultsHandler handler) {
		if (results != null) {
			handler.done(results);
		} else {
			resultHandlers.add(handler);
		}
	}

	private void runExtractParameterAnalysis() {
		this.parameterAnalysis = new ExtractParameterAnalysis(this.cryptoScanner, allCallsOnObject, spec.getFSM());
		this.parameterAnalysis.run();
	}

	private void computeTypestateErrorUnits() {
		Set<Statement> allTypestateChangeStatements = Sets.newHashSet();
		for (Cell<Statement, Val, TransitionFunction> c : results.asStatementValWeightTable().cellSet()) {
			allTypestateChangeStatements.addAll(c.getValue().getLastStateChangeStatements());
		}
		for (Cell<Statement, Val, TransitionFunction> c : results.asStatementValWeightTable().cellSet()) {
			Statement curr = c.getRowKey();
			if (allTypestateChangeStatements.contains(curr)) {
				Collection<? extends State> targetStates = getTargetStates(c.getValue());
				for (State newStateAtCurr : targetStates) {
					typeStateChangeAtStatement(curr, newStateAtCurr);
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
							if (t.getLeft().equals(wrappedState.delegate()) && !t.from().equals(t.to())) {
								Collection<SootMethod> converted = CrySLMethodToSootMethod.v().convert(t.getLabel());
								expectedMethodsToBeCalled.addAll(converted);
							}
						}
					}
				}
			}
			if (!expectedMethodsToBeCalled.isEmpty()) {
				Statement s = c.getRowKey();
				Val val = c.getColumnKey();
				if (!(s.getUnit().get() instanceof ThrowStmt)) {
					cryptoScanner.getAnalysisListener().reportError(this, new IncompleteOperationError(s, val, getSpec().getRule(), this, expectedMethodsToBeCalled));
				}
			}
		}
	}

	private void typeStateChangeAtStatement(Statement curr, State stateNode) {
		if (typeStateChange.put(curr, stateNode)) {
			if (stateNode instanceof ReportingErrorStateNode) {
				ReportingErrorStateNode errorStateNode = (ReportingErrorStateNode) stateNode;
				cryptoScanner.getAnalysisListener().reportError(this, new TypestateError(curr, getSpec().getRule(), this, errorStateNode.getExpectedCalls()));
			}
		}
		onAddedTypestateChange(curr, stateNode);
	}

	private void onAddedTypestateChange(Statement curr, State stateNode) {
		for (CrySLPredicate predToBeEnsured : spec.getRule().getPredicates()) {
			if (predToBeEnsured.isNegated()) {
				continue;
			}

			if (isPredicateGeneratingState(predToBeEnsured, stateNode)) {
				ensuresPred(predToBeEnsured, curr, stateNode);
			}
		}
	}

	private void ensuresPred(CrySLPredicate predToBeEnsured, Statement currStmt, State stateNode) {
		if (predToBeEnsured.isNegated()) {
			return;
		}
		boolean satisfiesConstraintSytem = checkConstraintSystem();
		if(predToBeEnsured.getConstraint() != null) {
			ArrayList<ISLConstraint> temp = new ArrayList<>();
			temp.add(predToBeEnsured.getConstraint());
			satisfiesConstraintSytem = !evaluatePredCond(predToBeEnsured);
		}
		
		for (ICrySLPredicateParameter predicateParam : predToBeEnsured.getParameters()) {
			if (predicateParam.getName().equals("this")) {
				expectPredicateWhenThisObjectIsInState(stateNode, currStmt, predToBeEnsured, satisfiesConstraintSytem);
			}
		}
		if (currStmt.isCallsite()) {
			InvokeExpr ie = ((Stmt) currStmt.getUnit().get()).getInvokeExpr();
			SootMethod invokedMethod = ie.getMethod();
			Collection<CrySLMethod> convert = CrySLMethodToSootMethod.v().convert(invokedMethod);

			for (CrySLMethod crySLMethod : convert) {
				Entry<String, String> retObject = crySLMethod.getRetObject();
				int paramEqualsAt = predicateParameterEqualsAt(predToBeEnsured.getParameters(), retObject.getKey());
				if (!retObject.getKey().equals("_") && currStmt.getUnit().get() instanceof AssignStmt && paramEqualsAt > -1) {
					AssignStmt as = (AssignStmt) currStmt.getUnit().get();
					Value leftOp = as.getLeftOp();
					AllocVal val = new AllocVal(leftOp, currStmt.getMethod(), as.getRightOp(), new Statement(as, currStmt.getMethod()));
					expectPredicateOnOtherObject(predToBeEnsured, currStmt, val, satisfiesConstraintSytem, paramEqualsAt);
				}
				int i = 0;
				for (Entry<String, String> p : crySLMethod.getParameters()) {
					paramEqualsAt = predicateParameterEqualsAt(predToBeEnsured.getParameters(), p.getKey());
					if (paramEqualsAt > -1) {
						Value param = ie.getArg(i);
						if (param instanceof Local) {
							Val val = new Val(param, currStmt.getMethod());
							expectPredicateOnOtherObject(predToBeEnsured, currStmt, val, satisfiesConstraintSytem, paramEqualsAt);
						}
					}
					i++;
				}

			}

		}
	}

	private int predicateParameterEqualsAt(List<ICrySLPredicateParameter> parameters, String key) {
		for(int i=0; i<parameters.size(); i++) {
			ICrySLPredicateParameter predicateParam = parameters.get(i);
			if (key.equals(predicateParam.getName())) {
				return i;
			}
		}
		return -1;
	}

	private void expectPredicateOnOtherObject(CrySLPredicate predToBeEnsured, Statement currStmt, Val accessGraph, boolean satisfiesConstraintSytem, int paramMatchPosition) {
		// TODO refactor this method.
		boolean matched = false;
		for (ClassSpecification spec : cryptoScanner.getClassSpecifictions()) {
			if (accessGraph.value() == null) {
				continue;
			}
			Type baseType = accessGraph.value().getType();
			if (baseType instanceof RefType) {
				RefType refType = (RefType) baseType;
				if (spec.getRule().getClassName().equals(refType.getSootClass().getName()) || spec.getRule().getClassName().equals(refType.getSootClass().getShortName())) {
					if (satisfiesConstraintSytem) {
						AnalysisSeedWithSpecification seed = cryptoScanner.getOrCreateSeedWithSpec(new AnalysisSeedWithSpecification(cryptoScanner, currStmt, accessGraph, spec));
						matched = true;
						EnsuredCrySLPredicate ensPred = new EnsuredCrySLPredicate(predToBeEnsured, parameterAnalysis.getCollectedValues());
						ensPred.addAnalysisSeedToParameter(seed, paramMatchPosition);
						seed.addEnsuredPredicateFromOtherRule(ensPred);
					}
				}
			}
		}
		if (matched)
			return;
		AnalysisSeedWithEnsuredPredicate seed = cryptoScanner.getOrCreateSeed(new Node<Statement, Val>(currStmt, accessGraph));
		predicateHandler.expectPredicate(seed, currStmt, predToBeEnsured);
		if (satisfiesConstraintSytem) {
			EnsuredCrySLPredicate ensPred = new EnsuredCrySLPredicate(predToBeEnsured, parameterAnalysis.getCollectedValues());
			ensPred.addAnalysisSeedToParameter(seed, paramMatchPosition);
			seed.addEnsuredPredicate(ensPred);
		} else {
			missingPredicates.add(new RequiredCrySLPredicate(predToBeEnsured, currStmt));
		}
	}

	private void addEnsuredPredicateFromOtherRule(EnsuredCrySLPredicate ensuredCrySLPredicate) {
		indirectlyEnsuredPredicates.add(ensuredCrySLPredicate);
		if (results == null)
			return;
		for (Cell<Statement, Val, TransitionFunction> c : results.asStatementValWeightTable().cellSet()) {
			for (EnsuredCrySLPredicate pred : indirectlyEnsuredPredicates) {
				predicateHandler.addNewPred(this, c.getRowKey(), c.getColumnKey(), pred);
			}
		}
	}

	private void expectPredicateWhenThisObjectIsInState(State stateNode, Statement currStmt, CrySLPredicate predToBeEnsured, boolean satisfiesConstraintSytem) {
		predicateHandler.expectPredicate(this, currStmt, predToBeEnsured);

		if (!satisfiesConstraintSytem)
			return;
		for (Cell<Statement, Val, TransitionFunction> e : results.asStatementValWeightTable().cellSet()) {
			// TODO check for any reachable state that don't kill
			// predicates.
			if (containsTargetState(e.getValue(), stateNode)) {
				EnsuredCrySLPredicate ensuredPred = new EnsuredCrySLPredicate(predToBeEnsured, parameterAnalysis.getCollectedValues());
				predicateHandler.addNewPred(this, e.getRowKey(), e.getColumnKey(), ensuredPred);
				ensuredPred.addAnalysisSeedToParameter(this, 0); // by definition, "this" can only be at first position in parameter list
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
		Set<ISLConstraint> relConstraints = constraintSolver.getRelConstraints();
		boolean checkPredicates = checkPredicates(relConstraints);
		cryptoScanner.getAnalysisListener().afterPredicateCheck(this);
		if (!checkPredicates)
			return false;
		return internalConstraintSatisfied;
	}

	private boolean checkPredicates(Collection<ISLConstraint> relConstraints) {
		List<ISLConstraint> requiredPredicates = Lists.newArrayList();
		for (ISLConstraint con : constraintSolver.getRequiredPredicates()) {
			if (!ConstraintSolver.predefinedPreds.contains((con instanceof RequiredCrySLPredicate) ? ((RequiredCrySLPredicate) con).getPred().getPredName()
					: ((AlternativeReqPredicate) con).getAlternatives().get(0).getPredName())) {
				requiredPredicates.add(con);
			}
		}
		Set<ISLConstraint> remainingPredicates = Sets.newHashSet(requiredPredicates);
		missingPredicates.removeAll(remainingPredicates);

		for (ISLConstraint pred : requiredPredicates) {
			if (pred instanceof RequiredCrySLPredicate) {
				RequiredCrySLPredicate reqPred = (RequiredCrySLPredicate) pred;
				if (reqPred.getPred().isNegated()) {
					for (EnsuredCrySLPredicate ensPred : ensuredPredicates) {
						if (ensPred.getPredicate().equals(reqPred.getPred())) {
							return false;
						}
					}
					remainingPredicates.remove(pred);
				} else {
					for (EnsuredCrySLPredicate ensPred : ensuredPredicates) {
						if (ensPred.getPredicate().equals(reqPred.getPred()) && doPredsMatch(reqPred.getPred(), ensPred)) {
							remainingPredicates.remove(pred);
						}
					}
				}
			} else {
				AlternativeReqPredicate alt = (AlternativeReqPredicate) pred;
				List<CrySLPredicate> alternatives = alt.getAlternatives();
				boolean satisfied = false;
				List<CrySLPredicate> negatives = alternatives.parallelStream().filter(e -> e.isNegated()).collect(Collectors.toList());
				
				if (negatives.size() == alternatives.size()) {
					for (EnsuredCrySLPredicate ensPred : ensuredPredicates) {
						if (alternatives.parallelStream().anyMatch(e -> e.getPredName().equals(ensPred.getPredicate().getPredName()))) {
							return false;
						}
					}
					remainingPredicates.remove(pred);
				} else if (negatives.isEmpty()) {
					for (EnsuredCrySLPredicate ensPred : ensuredPredicates) {
						if (alternatives.parallelStream().anyMatch(e -> ensPred.getPredicate().equals(e) && doPredsMatch(e, ensPred))) {
							remainingPredicates.remove(pred);
							break;
						}
					}
				} else {
					boolean neg = true;

					for (EnsuredCrySLPredicate ensPred : ensuredPredicates) {
						if (negatives.parallelStream().anyMatch(e -> e.equals(ensPred.getPredicate()))) {
							neg = false;
						}

						alternatives.removeAll(negatives);
						if (alternatives.parallelStream().allMatch(e -> ensPred.getPredicate().equals(e) && doPredsMatch(e, ensPred))) {
							satisfied = true;
						}

						if (satisfied | neg) {
							remainingPredicates.remove(pred);
						}
					}
				}

			}
		}

		for (ISLConstraint rem : Lists.newArrayList(remainingPredicates)) {
			if (rem instanceof RequiredCrySLPredicate) {
				RequiredCrySLPredicate singlePred = (RequiredCrySLPredicate) rem;
				if (evaluatePredCond(singlePred.getPred())) {
					remainingPredicates.remove(singlePred);
				}
			} else if (rem instanceof CrySLConstraint) {
				List<CrySLPredicate> altPred = ((AlternativeReqPredicate) rem).getAlternatives();
				if (altPred.parallelStream().anyMatch(e -> evaluatePredCond(e))) {
					remainingPredicates.remove(rem);
				}
			}
		}

		this.missingPredicates.addAll(remainingPredicates);
		return remainingPredicates.isEmpty();
	}

	private boolean evaluatePredCond(CrySLPredicate pred) {
		final ISLConstraint conditional = pred.getConstraint();
		if (conditional != null) {
			EvaluableConstraint evalCons = constraintSolver.createConstraint(conditional);
			evalCons.evaluate();
			if (evalCons.hasErrors()) {
				return true;
			}
		}
		return false;
	}

	private boolean doPredsMatch(CrySLPredicate pred, EnsuredCrySLPredicate ensPred) {
		if(pred.getParameters().size() != ensPred.getParameterToAnalysisSeed().length) {
			return false;
		}
		for(int i=0; i<pred.getParameters().size(); i++) {
			ICrySLPredicateParameter param = pred.getParameters().get(i);
			if(param.getName().equals("_") || ensPred.getPredicate().getParameters().get(i).getName().equals("_")) {
				// this can be anything
				continue;
			}
			IAnalysisSeed seedAtParamI = ensPred.getParameterToAnalysisSeed()[i];
			if(param.getName().equals("this")){
				if((seedAtParamI == null || !seedAtParamI.equals(this))) {
					// this is not this seed
					return false;
				}
				else {
					// this is also this seed
					continue;
				}
			}
			if(param instanceof CrySLMethod) {
				// methods are only defined for prefined predicates (noCallTo etc.)
				return false;
			}
			if(param instanceof CrySLObject) {
				CrySLObject paramObj = (CrySLObject) param;
				
				if(!isOfNonTrackableType(param.getName())) {
					// try to collect expected values for param
					Collection<String> expVals = Collections.emptySet();
					for (CallSiteWithParamIndex cswpi : parameterAnalysis.getCollectedValues().keySet()) {
						if (cswpi.getVarName().equals(param.getName())) {
							expVals = retrieveValueFromUnit(cswpi, parameterAnalysis.getCollectedValues().get(cswpi));
						}
					}
					
					if(!expVals.isEmpty()) {
						// required predicate holds a value, not an object
						Collection<String> actVals = Collections.emptySet();
						for (CallSiteWithParamIndex cswpi : ensPred.getParametersToValues().keySet()) {
							if (cswpi.getVarName().equals(ensPred.getPredicate().getParameters().get(i).getName())) {
								actVals = retrieveValueFromUnit(cswpi, ensPred.getParametersToValues().get(cswpi));
							}
						}
						String splitter = "";
						int index = -1;
						if (paramObj.getSplitter() != null) {
							splitter = paramObj.getSplitter().getSplitter();
							index = paramObj.getSplitter().getIndex();
						}
						for (String foundVal : expVals) {
							if (index > -1) {
								foundVal = foundVal.split(splitter)[index];
							}
							actVals = actVals.parallelStream().map(e -> e.toLowerCase()).collect(Collectors.toList());
							if(!actVals.contains(foundVal.toLowerCase())) {
								return false;
							}
						}
					}
				} else {
					// TODO Refactor
					// required predicate could still be references to other objects
					IAnalysisSeed ensSeed = ensPred.getParameterToAnalysisSeed()[i];
					IAnalysisSeed reqSeed = null;
					if(ensSeed != null) {
						Stmt ensStmt = ensSeed.stmt().getUnit().get();
						if(ensSeed instanceof AnalysisSeedWithSpecification) {
							AnalysisSeedWithSpecification ensSeedWithSpec = (AnalysisSeedWithSpecification) ensSeed;
							String ensClassName = ensSeedWithSpec.getSpec().getRule().getClassName();
							
							checkValues:
							for (CallSiteWithParamIndex cswpi : parameterAnalysis.getCollectedValues().keySet()) {
								if (cswpi.getVarName().equals(param.getName())) {
									for(ExtractedValue q : parameterAnalysis.getCollectedValues().get(cswpi)) {
										// q is a value, that matches 
										
										if(ensStmt instanceof AssignStmt && q.stmt().getUnit().get() instanceof AssignStmt) {
											// important for classes such as SecretKey, where the seed statement is an assign statement
											AssignStmt qStmt = (AssignStmt) q.stmt().getUnit().get();
											RefType qRefType = (RefType) qStmt.getLeftOpBox().getValue().getType();
											String qClassName = qRefType.getSootClass().getName();
											String qShortClassName = qRefType.getSootClass().getShortName();
											
											if(!ensClassName.equals(qClassName) && !ensClassName.equals(qShortClassName)) {
												// the required seed cannot match the ensured seed
												// they must specify the same class
												continue;
											}
											if(!ensStmt.toString().equals(qStmt.toString())) {
												// the required seed cannot match the ensured seed
												// they must be defined on the same statement
												continue;
											}
											
											for (ClassSpecification spec : cryptoScanner.getClassSpecifictions()) {
												// check if refType is matching type of spec
												if (spec.getRule().getClassName().equals(qClassName) || spec.getRule().getClassName().equals(qShortClassName)) {
													AllocVal val = new AllocVal(qStmt.getLeftOp(), q.stmt().getMethod(), qStmt.getRightOp(), new Statement(qStmt, q.stmt().getMethod()));
													reqSeed = this.cryptoScanner.getOrCreateSeedWithSpec(new AnalysisSeedWithSpecification(this.cryptoScanner, q.stmt(), val, spec));
													break checkValues;
												}
											}
										}
										else if(ensStmt instanceof InvokeStmt && q.stmt().getUnit().get() instanceof InvokeStmt) {
											// important for classes such as SecretKey, where the seed statement is an assign statement
											InvokeStmt qStmt = (InvokeStmt) q.stmt().getUnit().get();
											Val val = new Val(q.getValue(), q.stmt().getMethod());
											if(val.getType() instanceof RefType) {
												RefType qRefType = (RefType) val.getType();
												String qClassName = qRefType.getSootClass().getName();
												String qShortClassName = qRefType.getSootClass().getShortName();
											
												if(!ensClassName.equals(qClassName) && !ensClassName.equals(qShortClassName)) {
													// the required seed cannot match the ensured seed
													// they must specify the same class
													continue;
												}
												if(!ensStmt.toString().equals(qStmt.toString())) {
													// the required seed cannot match the ensured seed
													// they must be defined on the same statement
													continue;
												}
												
												for (ClassSpecification spec : cryptoScanner.getClassSpecifictions()) {
													// check if refType is matching type of spec
													if (spec.getRule().getClassName().equals(qClassName) || spec.getRule().getClassName().equals(qShortClassName)) {
														reqSeed = this.cryptoScanner.getOrCreateSeedWithSpec(new AnalysisSeedWithSpecification(this.cryptoScanner, q.stmt(), val, spec));
														break checkValues;
													}
												}
											}
										}
									}
								}
							}
						}
						else {
							AnalysisSeedWithEnsuredPredicate ensSeedWithEnsPred = (AnalysisSeedWithEnsuredPredicate) ensSeed;
							checkValues:
							for (CallSiteWithParamIndex cswpi : parameterAnalysis.getCollectedValues().keySet()) {
								if (cswpi.getVarName().equals(param.getName())) {
									for(ExtractedValue q : parameterAnalysis.getCollectedValues().get(cswpi)) {
										if(ensStmt instanceof AssignStmt && q.stmt().getUnit().get() instanceof AssignStmt) {
											AssignStmt as = (AssignStmt) q.stmt().getUnit().get();
											Value leftOp = as.getLeftOp();
											AllocVal val = new AllocVal(leftOp, q.stmt().getMethod(), as.getRightOp(), new Statement(as, q.stmt().getMethod()));
											reqSeed = this.cryptoScanner.getOrCreateSeed(new Node<Statement, Val>(q.stmt(), val));
											break checkValues;
										}
										else if(ensStmt instanceof InvokeStmt && q.stmt().getUnit().get() instanceof InvokeStmt) {
											Val val = new Val(q.getValue(), q.stmt().getMethod());
											reqSeed = this.cryptoScanner.getOrCreateSeed(new Node<Statement, Val>(q.stmt(), val));
											break checkValues;
										}
										else {
											// TODO this should be done better
											if(q.stmt().getMethod().getActiveBody().getUnits().contains(ensStmt)) {
												reqSeed = ensSeed;
											}	
										}
									}
								}
							}
							
						}
						
					}
					if(reqSeed == null || !reqSeed.equals(ensSeed)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private Collection<String> retrieveValueFromUnit(CallSiteWithParamIndex cswpi, Collection<ExtractedValue> collection) {
		Collection<String> values = new ArrayList<String>();
		for (ExtractedValue q : collection) {
			Unit u = q.stmt().getUnit().get();
			if (cswpi.stmt().equals(q.stmt())) {
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

	public void addEnsuredPredicate(EnsuredCrySLPredicate ensPred) {
		if (ensuredPredicates.add(ensPred)) {
			for (Entry<Statement, State> e : typeStateChange.entries())
				onAddedTypestateChange(e.getKey(), e.getValue());
		}
	}

	private boolean isPredicateGeneratingState(CrySLPredicate ensPred, State stateNode) {
		return ensPred instanceof CrySLCondPredicate && isConditionalState(((CrySLCondPredicate) ensPred).getConditionalMethods(), stateNode) || (!(ensPred instanceof CrySLCondPredicate) && stateNode.isAccepting());
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

	public Set<ISLConstraint> getMissingPredicates() {
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

	public boolean isSecure() {
		return secure;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	@Override
	public Set<Node<Statement, Val>> getDataFlowPath() {
		return results.getDataFlowPath();
	}

	public Map<Statement, SootMethod> getAllCallsOnObject() {
		return allCallsOnObject;
	}

}
