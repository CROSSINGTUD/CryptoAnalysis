package test;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.beust.jcommander.internal.Sets;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import boomerang.Query;
import boomerang.WeightedBoomerang;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.ClassSpecification;
import crypto.analysis.CrySLAnalysisListener;
import crypto.analysis.CrySLAnalysisResultsAggregator;
import crypto.analysis.CryptoScanner;
import crypto.analysis.EnsuredCryptSLPredicate;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.util.StmtWithMethod;
import crypto.rules.CryptSLMethod;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLRule;
import crypto.rules.CryptSLRuleReader;
import crypto.typestate.CallSiteWithParamIndex;
import crypto.typestate.ExtendedIDEALAnaylsis.AdditionalBoomerangQuery;
import soot.Body;
import soot.Local;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import sync.pds.solver.nodes.Node;
import test.assertions.Assertions;
import test.assertions.CallToForbiddenMethodAssertion;
import test.assertions.ExtractedValueAssertion;
import test.assertions.HasEnsuredPredicateAssertion;
import test.assertions.InAcceptingStateAssertion;
import test.assertions.NotHasEnsuredPredicateAssertion;
import test.assertions.NotInAcceptingStateAssertion;
import test.core.selfrunning.AbstractTestingFramework;
import test.core.selfrunning.ImprecisionException;
import typestate.TransitionFunction;
import typestate.interfaces.ISLConstraint;

public abstract class UsagePatternTestingFramework extends AbstractTestingFramework{

	protected BiDiInterproceduralCFG<Unit, SootMethod> icfg;
//	private IDEVizDebugger<TypestateDomainValue<StateNode>> debugger;
//
//	protected IDebugger<TypestateDomainValue<StateNode>> getDebugger() {
//		if(debugger == null)
//			debugger = new CryptoVizDebugger(ideVizFile, icfg);
//		return debugger;
//	}
	@Override
	protected SceneTransformer createAnalysisTransformer() throws ImprecisionException {
		return new SceneTransformer() {
			protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {
				icfg = new JimpleBasedInterproceduralCFG(true);
				final Set<Assertion> expectedResults = extractBenchmarkMethods(sootTestMethod);
				final TestingResultReporter resultReporter = new TestingResultReporter(expectedResults);
				CryptoScanner scanner = new CryptoScanner(getRules()) {
					
					@Override
					public BiDiInterproceduralCFG<Unit, SootMethod> icfg() {
						return icfg;
					}

					@Override
					public CrySLAnalysisResultsAggregator getAnalysisListener() {
						CrySLAnalysisListener cryslListener = new CrySLAnalysisListener() {
							@Override
							public void onSeedFinished(IAnalysisSeed seed,
									WeightedBoomerang<TransitionFunction> solver) {
								resultReporter.onSeedFinished(seed.asNode(), solver.getSolvers().get(seed));
							}

							@Override
							public void collectedValues(AnalysisSeedWithSpecification seed,
									Multimap<CallSiteWithParamIndex, Unit> collectedValues) {
								for(Assertion a: expectedResults){
									if(a instanceof ExtractedValueAssertion){
										((ExtractedValueAssertion) a).computedValues(collectedValues);
									}
								}
							}

							@Override
							public void callToForbiddenMethod(ClassSpecification classSpecification, StmtWithMethod callSite, List<CryptSLMethod> alternatives) {
								for(Assertion e : expectedResults){
									if(e instanceof CallToForbiddenMethodAssertion){
										CallToForbiddenMethodAssertion expectedResults = (CallToForbiddenMethodAssertion) e;
										expectedResults.reported(callSite.getStmt());
									}
								}
							}

							@Override
							public void discoveredSeed(IAnalysisSeed curr) {
								
							}

							@Override
							public void ensuredPredicates(Table<Statement, Val, Set<EnsuredCryptSLPredicate>> existingPredicates,
									Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> expectedPredicates,
									Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> missingPredicates) {
								for(Cell<Statement, Val, Set<EnsuredCryptSLPredicate>> c : existingPredicates.cellSet()){
									for(Assertion e : expectedResults){
										if(e instanceof HasEnsuredPredicateAssertion){
											HasEnsuredPredicateAssertion assertion = (HasEnsuredPredicateAssertion) e;
											if(assertion.getStmt().equals(c.getRowKey().getUnit().get())){
												for(EnsuredCryptSLPredicate pred : c.getValue()){
													assertion.reported(c.getColumnKey(),pred);
												}	
											}
										}
										if(e instanceof NotHasEnsuredPredicateAssertion){
											NotHasEnsuredPredicateAssertion assertion = (NotHasEnsuredPredicateAssertion) e;
											if(assertion.getStmt().equals(c.getRowKey().getUnit().get())){
												for(EnsuredCryptSLPredicate pred : c.getValue()){
													assertion.reported(c.getColumnKey(),pred);
												}	
											}
										}
									}
								}
							}

							@Override
							public void seedFinished(IAnalysisSeed analysisSeedWithSpecification) {
								
							}

							@Override
							public void seedStarted(IAnalysisSeed analysisSeedWithSpecification) {
								
							}

							@Override
							public void boomerangQueryStarted(Query seed, AdditionalBoomerangQuery q) {
								
							}

							@Override
							public void boomerangQueryFinished(Query seed, AdditionalBoomerangQuery q) {
								
							}

							@Override
							public void predicateContradiction(Node<Statement, Val> node,
									Entry<CryptSLPredicate, CryptSLPredicate> disPair) {
								throw new RuntimeException("IMPLEMENTE predicate contradicition" + node);
							}

							@Override
							public void missingPredicates(AnalysisSeedWithSpecification seed,
									Set<CryptSLPredicate> missingPredicates) {
							}

							@Override
							public void constraintViolation(AnalysisSeedWithSpecification analysisSeedWithSpecification,
									ISLConstraint con, StmtWithMethod unit) {
								
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
							public void typestateErrorEndOfLifeCycle(AnalysisSeedWithSpecification classSpecification,
									StmtWithMethod stmt) {
							}

							@Override
							public void typestateErrorAt(AnalysisSeedWithSpecification classSpecification, StmtWithMethod stmt, Collection<SootMethod> expectedCalls) {
								
							}

							@Override
							public void onSeedTimeout(Node<Statement, Val> seed) {
								
							}

							

						};
						CrySLAnalysisResultsAggregator reporters = new CrySLAnalysisResultsAggregator(icfg, ideVizFile);
						reporters.addReportListener(cryslListener);
						return reporters;
					}
//					@Override
//					public IDebugger<TypestateDomainValue<StateNode>> debugger() {
//						return UsagePatternTestingFramework.this.getDebugger();
//					}

					@Override
					public boolean isCommandLineMode() {
						return true;
					}

				};
				scanner.scan();
				
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
	protected List<CryptSLRule> getRules() {
		List<CryptSLRule> rules = Lists.newArrayList();    

		File[] listFiles = new File(IDEALCrossingTestingFramework.RESOURCE_PATH).listFiles();
		for (File file : listFiles) {
			if (file.getName().endsWith(".cryptslbin")) {
				System.out.println(file.getName());
				rules.add(CryptSLRuleReader.readFromFile(file));
			}
		}
		return rules;
	}
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
		for (Unit callSite : icfg.getCallsFromWithin(m)) {
			for (SootMethod callee : icfg.getCalleesOfCallAt(callSite))
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
