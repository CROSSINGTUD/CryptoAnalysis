package test;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import boomerang.BackwardQuery;
import boomerang.Query;
import boomerang.callgraph.ObservableDynamicICFG;
import boomerang.callgraph.ObservableICFG;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import boomerang.preanalysis.BoomerangPretransformer;
import boomerang.results.ForwardBoomerangResults;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.CrySLAnalysisListener;
import crypto.analysis.CrySLResultsReporter;
import crypto.analysis.CrySLRulesetSelector;
import crypto.analysis.CrySLRulesetSelector.RuleFormat;
import crypto.analysis.CrySLRulesetSelector.Ruleset;
import crypto.analysis.CryptoScanner;
import crypto.analysis.EnsuredCrySLPredicate;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.ErrorVisitor;
import crypto.analysis.errors.ForbiddenMethodError;
import crypto.analysis.errors.HardCodedError;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.NeverTypeOfError;
import crypto.analysis.errors.PredicateContradictionError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import crypto.exceptions.CryptoAnalysisException;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractedValue;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CrySLPredicate;
import crypto.rules.CrySLRule;
import soot.Body;
import soot.Local;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import sync.pds.solver.nodes.Node;
import test.assertions.Assertions;
import test.assertions.CallToForbiddenMethodAssertion;
import test.assertions.ConstraintErrorCountAssertion;
import test.assertions.ExtractedValueAssertion;
import test.assertions.HasEnsuredPredicateAssertion;
import test.assertions.InAcceptingStateAssertion;
import test.assertions.MissingTypestateChange;
import test.assertions.NoMissingTypestateChange;
import test.assertions.NotHasEnsuredPredicateAssertion;
import test.assertions.NotInAcceptingStateAssertion;
import test.assertions.PredicateContradiction;
import test.assertions.PredicateErrorCountAssertion;
import test.assertions.TypestateErrorCountAssertion;
import test.core.selfrunning.AbstractTestingFramework;
import test.core.selfrunning.ImprecisionException;
import typestate.TransitionFunction;

public abstract class UsagePatternTestingFramework extends AbstractTestingFramework{

	protected ObservableICFG<Unit, SootMethod> icfg;
	private JimpleBasedInterproceduralCFG staticIcfg;
	private static final RuleFormat ruleFormat= RuleFormat.SOURCE;
	List<CrySLRule> rules;
	
	@Override
	protected SceneTransformer createAnalysisTransformer() throws ImprecisionException {
		return new SceneTransformer() {

			protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {
				BoomerangPretransformer.v().reset();
				BoomerangPretransformer.v().apply();
				staticIcfg = new JimpleBasedInterproceduralCFG(true);
//				icfg = new ObservableStaticICFG(new JimpleBasedInterproceduralCFG(true));
				icfg = new ObservableDynamicICFG(true);
				final Set<Assertion> expectedResults = extractBenchmarkMethods(sootTestMethod);
				final TestingResultReporter resultReporter = new TestingResultReporter(expectedResults);
				CryptoScanner scanner = new CryptoScanner() {
					
					@Override
					public ObservableICFG<Unit, SootMethod> icfg() {
						return icfg;
					}

					@Override
					public CrySLResultsReporter getAnalysisListener() {
						CrySLAnalysisListener cryslListener = new CrySLAnalysisListener() {
							@Override
							public void onSeedFinished(IAnalysisSeed seed,
									ForwardBoomerangResults<TransitionFunction> res) {
								resultReporter.onSeedFinished(seed.asNode(), res.asStatementValWeightTable());
							}

							@Override
							public void collectedValues(AnalysisSeedWithSpecification seed,
									Multimap<CallSiteWithParamIndex, ExtractedValue> collectedValues) {
								for(Assertion a : expectedResults){
									if(a instanceof ExtractedValueAssertion){
										((ExtractedValueAssertion) a).computedValues(collectedValues);
									}
								}
							}
							
							@Override
							public void reportError(AbstractError error) {
								error.accept(new ErrorVisitor() {
									
									@Override
									public void visit(RequiredPredicateError predicateError) {
										for(Assertion a: expectedResults){
											if(a instanceof PredicateErrorCountAssertion){
												PredicateErrorCountAssertion errorCountAssertion = (PredicateErrorCountAssertion) a;
												errorCountAssertion.increaseCount();
											}
										}
									}
									
									@Override
									public void visit(TypestateError typestateError) {
										for(Assertion a: expectedResults){
											if(a instanceof TypestateErrorCountAssertion){
												TypestateErrorCountAssertion errorCountAssertion = (TypestateErrorCountAssertion) a;
												errorCountAssertion.increaseCount();
											}
										}
									}
									
									@Override
									public void visit(IncompleteOperationError incompleteOperationError) {
										boolean hasTypestateChangeError = false;
										boolean expectsTypestateChangeError = false;
										for(Assertion a: expectedResults){
											if(a instanceof MissingTypestateChange){
												MissingTypestateChange missingTypestateChange = (MissingTypestateChange) a;
												if(missingTypestateChange.getStmt().equals(incompleteOperationError.getErrorLocation().getUnit().get())){
													missingTypestateChange.trigger();
													hasTypestateChangeError = true;
												}
												expectsTypestateChangeError = true;
											}
											if(a instanceof NoMissingTypestateChange){
												throw new RuntimeException("Reports a typestate error that should not be reported");
											}
										}
										if(hasTypestateChangeError != expectsTypestateChangeError){
											throw new RuntimeException("Reports a typestate error that should not be reported");
										}
									}
									
									@Override
									public void visit(ForbiddenMethodError abstractError) {
										for(Assertion e : expectedResults){
											if(e instanceof CallToForbiddenMethodAssertion){
												CallToForbiddenMethodAssertion expectedResults = (CallToForbiddenMethodAssertion) e;
												expectedResults.reported(abstractError.getErrorLocation().getUnit().get());
											}
										}
									}
									
									@Override
									public void visit(ConstraintError constraintError) {
										for(Assertion a: expectedResults){
											if(a instanceof ConstraintErrorCountAssertion){
												ConstraintErrorCountAssertion errorCountAssertion = (ConstraintErrorCountAssertion) a;
												errorCountAssertion.increaseCount();
											}
										}
									}

									@Override
									public void visit(ImpreciseValueExtractionError predicateError) {

									}

									@Override
									public void visit(NeverTypeOfError predicateError) {
										// TODO Auto-generated method stub
										
									}

									@Override
									public void visit(PredicateContradictionError predicateContradictionError) {
										for (Assertion e : expectedResults) {
											if (e instanceof PredicateContradiction) {
												PredicateContradiction p = (PredicateContradiction) e;
												p.trigger();
											}
										}
									}

									@Override
									public void visit(HardCodedError predicateError) {
										
									}
								});
							}

							@Override
							public void discoveredSeed(IAnalysisSeed curr) {
								
							}

							@Override
							public void ensuredPredicates(Table<Statement, Val, Set<EnsuredCrySLPredicate>> existingPredicates,
									Table<Statement, IAnalysisSeed, Set<CrySLPredicate>> expectedPredicates,
									Table<Statement, IAnalysisSeed, Set<CrySLPredicate>> missingPredicates) {
								for(Cell<Statement, Val, Set<EnsuredCrySLPredicate>> c : existingPredicates.cellSet()){
									for(Assertion e : expectedResults){
										if(e instanceof HasEnsuredPredicateAssertion){
											HasEnsuredPredicateAssertion assertion = (HasEnsuredPredicateAssertion) e;
											if(assertion.getStmt().equals(c.getRowKey().getUnit().get())){
												for(EnsuredCrySLPredicate pred : c.getValue()){
													assertion.reported(c.getColumnKey(),pred);
												}	
											}
										}
										if(e instanceof NotHasEnsuredPredicateAssertion){
											NotHasEnsuredPredicateAssertion assertion = (NotHasEnsuredPredicateAssertion) e;
											if(assertion.getStmt().equals(c.getRowKey().getUnit().get())){
												for(EnsuredCrySLPredicate pred : c.getValue()){
													assertion.reported(c.getColumnKey(),pred);
												}	
											}
										}
									}
								}
							}

							@Override
							public void seedStarted(IAnalysisSeed analysisSeedWithSpecification) {
								
							}

							@Override
							public void boomerangQueryStarted(Query seed, BackwardQuery q) {
								
							}

							@Override
							public void boomerangQueryFinished(Query seed, BackwardQuery q) {
								
							}


							@Override
							public void checkedConstraints(AnalysisSeedWithSpecification analysisSeedWithSpecification,
									Collection<ISLConstraint> relConstraints) {
								
							}

							@Override
							public void beforeAnalysis() {
								
							}

							@Override
							public void afterAnalysis() {
								
							}

							@Override
							public void beforeConstraintCheck(
									AnalysisSeedWithSpecification analysisSeedWithSpecification) {
								
							}

							@Override
							public void afterConstraintCheck(
									AnalysisSeedWithSpecification analysisSeedWithSpecification) {
								
							}

							@Override
							public void beforePredicateCheck(
									AnalysisSeedWithSpecification analysisSeedWithSpecification) {
								
							}

							@Override
							public void afterPredicateCheck(
									AnalysisSeedWithSpecification analysisSeedWithSpecification) {
								
							}

						

							@Override
							public void onSeedTimeout(Node<Statement, Val> seed) {
								
							}

							@Override
							public void onSecureObjectFound(IAnalysisSeed analysisObject) {
								// TODO Auto-generated method stub
								
							}

							@Override
							public void addProgress(int processedSeeds, int workListsize) {
								// TODO Auto-generated method stub
								
							}
							

						};
						CrySLResultsReporter reporters = new CrySLResultsReporter();
						reporters.addReportListener(cryslListener);
						return reporters;
					}
				};
				scanner.scan(getRules());
				
				List<Assertion> unsound = Lists.newLinkedList();
				List<Assertion> imprecise = Lists.newLinkedList();
				for (Assertion r : expectedResults) {
					if (!r.isSatisfied()) {
						unsound.add(r);
					}
				}
				for (Assertion r : expectedResults) {
					if (r.isImprecise()) {
						imprecise.add(r);
					}
				}
				if (!unsound.isEmpty())
					throw new RuntimeException("Unsound results: \n" + Joiner.on("\n").join(unsound));
				if (!imprecise.isEmpty()) {
					throw new ImprecisionException("Imprecise results: " + Joiner.on("\n").join(imprecise));
				}
			}


		};
	}

	private List<CrySLRule> getRules() {
		if(rules == null) {
			try {
				rules = CrySLRulesetSelector.makeFromRuleset(IDEALCrossingTestingFramework.RULES_BASE_DIR, ruleFormat, getRuleSet());
			} catch (CryptoAnalysisException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return rules;
	}
	@Override
	public List<String> excludedPackages() {
		List<String> excludedPackages = super.excludedPackages();
		for(CrySLRule r : getRules()) {
			excludedPackages.add(r.getClassName());
		}
		return excludedPackages;
	}
	
	protected abstract Ruleset getRuleSet();



	private Set<Assertion> extractBenchmarkMethods(SootMethod sootTestMethod) {
		Set<Assertion> results = new HashSet<>();
		extractBenchmarkMethods(sootTestMethod, results, new HashSet<SootMethod>());
		return results;
	}

	private void extractBenchmarkMethods(SootMethod m, Set<Assertion> queries, Set<SootMethod> visited) {
		if (!m.hasActiveBody() || visited.contains(m))
			return;
		visited.add(m);
		Body activeBody = m.getActiveBody();
		for (Unit callSite : staticIcfg.getCallsFromWithin(m)) {
			for (SootMethod callee : staticIcfg.getCalleesOfCallAt(callSite))
				extractBenchmarkMethods(callee, queries, visited);
		}
		for (Unit u : activeBody.getUnits()) {
			if (!(u instanceof Stmt))
				continue;

			Stmt stmt = (Stmt) u;
			if (!(stmt.containsInvokeExpr()))
				continue;
			InvokeExpr invokeExpr = stmt.getInvokeExpr();
			if (!invokeExpr.getMethod().getDeclaringClass().toString().equals(Assertions.class.getName()))
				continue;
			String invocationName = invokeExpr.getMethod().getName();
			if(invocationName.startsWith("extValue")){
				Value param = invokeExpr.getArg(0);
				if (!(param instanceof IntConstant))
					continue;
				IntConstant paramIndex = (IntConstant) param;
				for(Unit pred : getPredecessorsNotBenchmark(stmt))
					queries.add(new ExtractedValueAssertion(pred, paramIndex.value));
			}
			if(invocationName.startsWith("callToForbiddenMethod")){
				for(Unit pred : getPredecessorsNotBenchmark(stmt))
					queries.add(new CallToForbiddenMethodAssertion(pred));
			}
			if(invocationName.startsWith("mustBeInAcceptingState")){
				Value param = invokeExpr.getArg(0);
				if (!(param instanceof Local))
					continue;
				Local queryVar = (Local) param;
				Val val = new Val(queryVar,m);
				queries.add(new InAcceptingStateAssertion(stmt, val));
			}
			
//			if (invocationName.startsWith("violatedConstraint")) {
//				queries.add(new ConstraintViolationAssertion(stmt));
//			}

			if(invocationName.startsWith("hasEnsuredPredicate")){
				Value param = invokeExpr.getArg(0);
				if (!(param instanceof Local))
					continue;
				Local queryVar = (Local) param;
				Val val = new Val(queryVar, m);
				queries.add(new HasEnsuredPredicateAssertion(stmt, val));
			}
			
			if(invocationName.startsWith("notHasEnsuredPredicate")){
				Value param = invokeExpr.getArg(0);
				if (!(param instanceof Local))
					continue;
				Local queryVar = (Local) param;
				Val val = new Val(queryVar, m);
				queries.add(new NotHasEnsuredPredicateAssertion(stmt, val));
			}
			
			if(invocationName.startsWith("mustNotBeInAcceptingState")){
				Value param = invokeExpr.getArg(0);
				if (!(param instanceof Local))
					continue;
				Local queryVar = (Local) param;
				Val val = new Val(queryVar, m);
				queries.add(new NotInAcceptingStateAssertion(stmt, val));
			}

			if(invocationName.startsWith("predicateContradiction")){
				queries.add(new PredicateContradiction());
			}
			if(invocationName.startsWith("missingTypestateChange")){
				for(Unit pred : getPredecessorsNotBenchmark(stmt))
					queries.add(new MissingTypestateChange((Stmt) pred));
			}


			if(invocationName.startsWith("noMissingTypestateChange")){
				for(Unit pred : getPredecessorsNotBenchmark(stmt))
					queries.add(new NoMissingTypestateChange((Stmt) pred));
			}
			
			if(invocationName.startsWith("predicateErrors")){	
				Value param = invokeExpr.getArg(0);
				if (!(param instanceof IntConstant))
					continue;
				IntConstant queryVar = (IntConstant) param;
				queries.add(new PredicateErrorCountAssertion(queryVar.value));
			}
			if(invocationName.startsWith("constraintErrors")){	
				Value param = invokeExpr.getArg(0);
				if (!(param instanceof IntConstant))
					continue;
				IntConstant queryVar = (IntConstant) param;
				queries.add(new ConstraintErrorCountAssertion(queryVar.value));
			}
			if(invocationName.startsWith("typestateErrors")){	
				Value param = invokeExpr.getArg(0);
				if (!(param instanceof IntConstant))
					continue;
				IntConstant queryVar = (IntConstant) param;
				queries.add(new TypestateErrorCountAssertion(queryVar.value));
			}
		}
	}
	private Set<Unit> getPredecessorsNotBenchmark(Stmt stmt) {
		Set<Unit> res = Sets.newHashSet();
		Set<Unit> visited = Sets.newHashSet();
		LinkedList<Unit> worklist = Lists.newLinkedList();
		worklist.add(stmt);
		while(!worklist.isEmpty()){
			Unit curr = worklist.poll();
			if(!visited.add(curr))
				continue;
			if(!curr.toString().contains(Assertions.class.getSimpleName()) && (curr instanceof Stmt) && ((Stmt) curr).containsInvokeExpr()){
				res.add(curr);
				continue;
			}
			worklist.addAll(icfg.getPredsOf(curr));
		}
		return res;
	}
}
