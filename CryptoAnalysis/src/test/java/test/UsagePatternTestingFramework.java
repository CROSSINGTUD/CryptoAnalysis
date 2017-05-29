package test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;

import soot.Body;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import test.core.selfrunning.AbstractTestingFramework;
import test.core.selfrunning.ImprecisionException;
import typestate.tests.crypto.Benchmark;

public abstract class UsagePatternTestingFramework extends AbstractTestingFramework{

	protected InfoflowCFG icfg;

	@Override
	protected SceneTransformer createAnalysisTransformer() throws ImprecisionException {
		return new SceneTransformer() {
			protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {
				icfg = new InfoflowCFG(new JimpleBasedInterproceduralCFG(true));
				Set<Assertion> expectedResults = extractBenchmarkMethods(sootTestMethod);
//				testingResultReporter = new TestingResultReporter<StateNode>(expectedResults);
//				CryptoScanner scanner = new CryptoScanner(null) {
//					
//					@Override
//					public IInfoflowCFG icfg() {
//						return icfg;
//					}
//
//					@Override
//					public ErrorReporter errorReporter() {
//						// TODO Auto-generated method stub
//						return new ErrorReporter(){
//
//							@Override
//							public void report(ClassSpecification spec, Unit stmt, Violation details) {
//								System.err.println("Class Specification " + spec +" reporter Error at \n\t" + stmt);
//							}
//							
//						};
//					}
//				};
//				scanner.scan();
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
					throw new RuntimeException("Unsound results: " + unsound);
				if (!imprecise.isEmpty()) {
					throw new ImprecisionException("Imprecise results: " + imprecise);
				}
			}

		};
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
			if (!invokeExpr.getMethod().getDeclaringClass().toString().equals(Benchmark.class.getName()))
				continue;
			String invocationName = invokeExpr.getMethod().getName();
			if(invocationName.startsWith("extValue")){
				Value param = invokeExpr.getArg(0);
				if (!(param instanceof IntConstant))
					continue;
				IntConstant paramIndex = (IntConstant) param;
				for(Unit pred : icfg.getPredsOf(stmt))
					queries.add(new ExtractedValueAssertion(pred, paramIndex.value));
			}
		}
	}
}
