package test;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;

import boomerang.accessgraph.AccessGraph;
import crypto.rules.StateMachineGraphReader;
import crypto.rules.StateNode;
import crypto.statemachine.CryptoTypestateAnaylsisProblem;
import crypto.statemachine.FiniteStateMachineToTypestateChangeFunction;
import ideal.Analysis;
import ideal.ResultReporter;
import ideal.debug.IDEVizDebugger;
import ideal.debug.IDebugger;
import soot.Body;
import soot.Local;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import test.core.selfrunning.AbstractTestingFramework;
import test.core.selfrunning.ImprecisionException;
import typestate.TypestateChangeFunction;
import typestate.TypestateDomainValue;

public abstract class IDEALCrossingTestingFramework extends AbstractTestingFramework{
	protected IInfoflowCFG icfg;
	protected long analysisTime;
	private IDEVizDebugger<TypestateDomainValue<StateNode>> debugger;
	protected TestingResultReporter<StateNode> testingResultReporter;
	protected String RESOURCE_PATH = "src/test/resources/";
	
	protected abstract File getSMGFile();

	protected Analysis<TypestateDomainValue<StateNode>> createAnalysis() {
		return new Analysis<TypestateDomainValue<StateNode>>(new CryptoTypestateAnaylsisProblem() {

			@Override
			public File getStateMachineFile() {
				return new File(RESOURCE_PATH + getSMGFile());
			}

			public ResultReporter<TypestateDomainValue<StateNode>> resultReporter() {
				return IDEALCrossingTestingFramework.this.testingResultReporter;
			}

			@Override
			public IInfoflowCFG icfg() {
				return icfg;
			}

			@Override
			public IDebugger<TypestateDomainValue<StateNode>> debugger() {
				return IDEALCrossingTestingFramework.this.getDebugger();
			}});
	}

	protected IDebugger<TypestateDomainValue<StateNode>> getDebugger() {
		if(debugger == null)
			debugger = new IDEVizDebugger<>(ideVizFile, icfg);
		return debugger;
	}

	@Override
	protected SceneTransformer createAnalysisTransformer() throws ImprecisionException {
		return new SceneTransformer() {
			protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {
				icfg = new InfoflowCFG(new JimpleBasedInterproceduralCFG(true));
				Set<IExpectedResults<StateNode>> expectedResults = parseExpectedQueryResults(sootTestMethod);
				testingResultReporter = new TestingResultReporter<StateNode>(expectedResults);
				
				executeAnalysis();
				List<IExpectedResults<StateNode>> unsound = Lists.newLinkedList();
				List<IExpectedResults<StateNode>> imprecise = Lists.newLinkedList();
				for (IExpectedResults<StateNode> r : expectedResults) {
					if (!r.isSatisfied()) {
						unsound.add(r);
					}
				}
				for (IExpectedResults<StateNode> r : expectedResults) {
					if (r.isImprecise()) {
						imprecise.add(r);
					}
				}
				if (!unsound.isEmpty())
					throw new RuntimeException("Unsound results: " + unsound);
				if (!imprecise.isEmpty()) {
					throw new ImprecisionException("Imprecise results: " + imprecise);
				}
				IDEALCrossingTestingFramework.this.removeVizFile();
			}
		};
	}

	protected void executeAnalysis() {
		IDEALCrossingTestingFramework.this.createAnalysis().run();
	}

	private Set<IExpectedResults<StateNode>> parseExpectedQueryResults(SootMethod sootTestMethod) {
		Set<IExpectedResults<StateNode>> results = new HashSet<>();
		parseExpectedQueryResults(sootTestMethod, results, new HashSet<SootMethod>());
		return results;
	}

	private void parseExpectedQueryResults(SootMethod m, Set<IExpectedResults<StateNode>> queries, Set<SootMethod> visited) {
		if (!m.hasActiveBody() || visited.contains(m))
			return;
		visited.add(m);
		Body activeBody = m.getActiveBody();
		for (Unit callSite : icfg.getCallsFromWithin(m)) {
			for (SootMethod callee : icfg.getCalleesOfCallAt(callSite))
				parseExpectedQueryResults(callee, queries, visited);
		}
		for (Unit u : activeBody.getUnits()) {
			if (!(u instanceof Stmt))
				continue;

			Stmt stmt = (Stmt) u;
			if (!(stmt.containsInvokeExpr()))
				continue;
			InvokeExpr invokeExpr = stmt.getInvokeExpr();
			String invocationName = invokeExpr.getMethod().getName();
			if (!invocationName.startsWith("assertState"))
				continue;
			Value param = invokeExpr.getArg(0);
			if (!(param instanceof Local))
				continue;
			Local queryVar = (Local) param;
			Value param2 = invokeExpr.getArg(1);
			AccessGraph val = new AccessGraph(queryVar, queryVar.getType());
			queries.add(new MustBeInState(stmt, val, param2.toString()));
		}
	}

	/**
	 * This method can be used in test cases to create branching. It is not
	 * optimized away.
	 * 
	 * @return
	 */
	protected boolean staticallyUnknown() {
		return true;
	}

}
