package test;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.beust.jcommander.internal.Sets;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import boomerang.accessgraph.AccessGraph;
import boomerang.cfg.ExtendedICFG;
import boomerang.cfg.IExtendedICFG;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.ClassSpecification;
import crypto.analysis.CryptSLAnalysisListener;
import crypto.analysis.CryptoScanner;
import crypto.analysis.CryptoVizDebugger;
import crypto.analysis.EnsuredCryptSLPredicate;
import crypto.analysis.IAnalysisSeed;
import crypto.rules.CryptSLRule;
import crypto.rules.CryptSLRuleReader;
import crypto.rules.StateNode;
import crypto.typestate.CallSiteWithParamIndex;
import ideal.AnalysisSolver;
import ideal.IFactAtStatement;
import ideal.debug.IDEVizDebugger;
import ideal.debug.IDebugger;
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
import test.assertions.Assertions;
import test.assertions.CallToForbiddenMethodAssertion;
import test.assertions.ConstraintViolationAssertion;
import test.assertions.HasEnsuredPredicateAssertion;
import test.assertions.NotHasEnsuredPredicateAssertion;
import test.core.selfrunning.AbstractTestingFramework;
import test.core.selfrunning.ImprecisionException;
import typestate.TypestateDomainValue;

public abstract class UsagePatternTestingFramework extends AbstractTestingFramework{

	protected ExtendedICFG icfg;
	private IDEVizDebugger<TypestateDomainValue<StateNode>> debugger;

	protected IDebugger<TypestateDomainValue<StateNode>> getDebugger() {
		if(debugger == null)
			debugger = new CryptoVizDebugger(ideVizFile, icfg);
		return debugger;
	}
	@Override
	protected SceneTransformer createAnalysisTransformer() throws ImprecisionException {
		return new SceneTransformer() {
			protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {
				icfg = new ExtendedICFG(new JimpleBasedInterproceduralCFG(true));
				final Set<Assertion> expectedResults = extractBenchmarkMethods(sootTestMethod);
//				testingResultReporter = new TestingResultReporter<StateNode>(expectedResults);
				CryptoScanner scanner = new CryptoScanner(getRules()) {
					
					@Override
					public IExtendedICFG icfg() {
						return icfg;
					}

					@Override
					public CryptSLAnalysisListener analysisListener() {
						return new CryptSLAnalysisListener(){
							@Override
							public void onSeedFinished(IFactAtStatement seed,
									AnalysisSolver<TypestateDomainValue<StateNode>> solver) {
								for(Cell<Unit,AccessGraph, TypestateDomainValue<StateNode>>c : solver.results().cellSet()){
									for(Assertion e : expectedResults){
										if(e instanceof ComparableResult){
											ComparableResult<StateNode> expectedResults = (ComparableResult) e;
											TypestateDomainValue<StateNode> resultAt = solver.resultAt(expectedResults.getStmt(), expectedResults.getAccessGraph());
											if(resultAt != null)
												expectedResults.computedResults(resultAt);
										}
									}
								}
							}

							@Override
							public void collectedValues(AnalysisSeedWithSpecification seed,
									Multimap<CallSiteWithParamIndex, Value> collectedValues) {
								for(Assertion a: expectedResults){
									if(a instanceof ExtractedValueAssertion){
										((ExtractedValueAssertion) a).computedValues(collectedValues);
									}
								}
							}

							@Override
							public void callToForbiddenMethod(ClassSpecification classSpecification, Unit callSite) {
								for(Assertion e : expectedResults){
									if(e instanceof CallToForbiddenMethodAssertion){
										CallToForbiddenMethodAssertion expectedResults = (CallToForbiddenMethodAssertion) e;
										expectedResults.reported(callSite);
									}
								}
							}

							@Override
							public void violateConstraint(ClassSpecification spec, Unit callSite) {
							}

							@Override
							public void discoveredSeed(IAnalysisSeed curr) {
								
							}

							@Override
							public void onSeedTimeout(IFactAtStatement seed) {
							}

							@Override
							public void ensuredPredicates(
									Table<Unit, AccessGraph, Set<EnsuredCryptSLPredicate>> existingPredicates) {
								for(Cell<Unit, AccessGraph, Set<EnsuredCryptSLPredicate>> c : existingPredicates.cellSet()){
									for(Assertion e : expectedResults){
										if(e instanceof HasEnsuredPredicateAssertion){
											HasEnsuredPredicateAssertion assertion = (HasEnsuredPredicateAssertion) e;
											if(assertion.getStmt().equals(c.getRowKey())){
												for(EnsuredCryptSLPredicate pred : c.getValue()){
													assertion.reported(c.getColumnKey(),pred);
												}	
											}
										}
										if(e instanceof NotHasEnsuredPredicateAssertion){
											NotHasEnsuredPredicateAssertion assertion = (NotHasEnsuredPredicateAssertion) e;
											if(assertion.getStmt().equals(c.getRowKey())){
												for(EnsuredCryptSLPredicate pred : c.getValue()){
													assertion.reported(c.getColumnKey(),pred);
												}	
											}
										}
									}
								}
							}
						};
					}
					@Override
					public IDebugger<TypestateDomainValue<StateNode>> debugger() {
						return UsagePatternTestingFramework.this.getDebugger();
					}

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
		LinkedList<CryptSLRule> rules = Lists.newLinkedList();        
		rules.add(CryptSLRuleReader.readFromFile(new File(IDEALCrossingTestingFramework.RESOURCE_PATH + "Cipher.cryptslbin")));
		rules.add(CryptSLRuleReader.readFromFile(new File(IDEALCrossingTestingFramework.RESOURCE_PATH + "KeyGenerator.cryptslbin")));
		rules.add(CryptSLRuleReader.readFromFile(new File(IDEALCrossingTestingFramework.RESOURCE_PATH + "KeyPairGenerator.cryptslbin")));
//		rules.add(CryptSLRuleReader.readFromFile(new File(IDEALCrossingTestingFramework.RESOURCE_PATH + "MessageDigest.cryptslbin")));
		rules.add(CryptSLRuleReader.readFromFile(new File(IDEALCrossingTestingFramework.RESOURCE_PATH + "PBEKeySpec.cryptslbin")));
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
			if(invocationName.startsWith("assertNotErrorState")){
				Value param = invokeExpr.getArg(0);
				if (!(param instanceof Local))
					continue;
				Local queryVar = (Local) param;
				AccessGraph val = new AccessGraph(queryVar, queryVar.getType());
				queries.add(new NotInErrorStateAssertion(stmt, val));
			}
			
//			if (invocationName.startsWith("violatedConstraint")) {
//				queries.add(new ConstraintViolationAssertion(stmt));
//			}

			if(invocationName.startsWith("hasEnsuredPredicate")){
				Value param = invokeExpr.getArg(0);
				if (!(param instanceof Local))
					continue;
				Local queryVar = (Local) param;
				AccessGraph val = new AccessGraph(queryVar, queryVar.getType());
				queries.add(new HasEnsuredPredicateAssertion(stmt, val));
			}
			
			if(invocationName.startsWith("notHasEnsuredPredicate")){
				Value param = invokeExpr.getArg(0);
				if (!(param instanceof Local))
					continue;
				Local queryVar = (Local) param;
				AccessGraph val = new AccessGraph(queryVar, queryVar.getType());
				queries.add(new NotHasEnsuredPredicateAssertion(stmt, val));
			}
			
			if(invocationName.startsWith("assertErrorState")){
				Value param = invokeExpr.getArg(0);
				if (!(param instanceof Local))
					continue;
				Local queryVar = (Local) param;
				AccessGraph val = new AccessGraph(queryVar, queryVar.getType());
				queries.add(new InErrorStateAssertion(stmt, val));
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
