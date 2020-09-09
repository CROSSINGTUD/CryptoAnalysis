package test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import boomerang.WeightedForwardQuery;
import boomerang.callgraph.ObservableDynamicICFG;
import boomerang.callgraph.ObservableICFG;
import boomerang.debugger.Debugger;
import boomerang.debugger.IDEVizDebugger;
import boomerang.jimple.Val;
import boomerang.preanalysis.BoomerangPretransformer;
import boomerang.results.ForwardBoomerangResults;
import crypto.HeadlessCryptoScanner;
import crypto.analysis.CrySLResultsReporter;
import crypto.analysis.CrySLRulesetSelector;
import crypto.analysis.CrySLRulesetSelector.RuleFormat;
import crypto.analysis.CrySLRulesetSelector.Ruleset;
import crypto.exceptions.CryptoAnalysisException;
import crypto.rules.CrySLRule;
import crypto.typestate.CrySLMethodToSootMethod;
import crypto.typestate.ExtendedIDEALAnaylsis;
import crypto.typestate.SootBasedStateMachineGraph;
import ideal.IDEALSeedSolver;
import soot.Body;
import soot.Local;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import test.assertions.MustBeInState;
import test.core.selfrunning.AbstractTestingFramework;
import test.core.selfrunning.ImprecisionException;
import typestate.TransitionFunction;

public abstract class IDEALCrossingTestingFramework extends AbstractTestingFramework{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IDEALCrossingTestingFramework.class);

	protected JimpleBasedInterproceduralCFG staticIcfg;
	protected ObservableICFG<Unit, SootMethod> icfg;
	protected long analysisTime;
	private  Debugger<TransitionFunction>  debugger;
	public static final String RULES_BASE_DIR = "src/main/resources/";
	private static final RuleFormat ruleFormat = RuleFormat.SOURCE;
	
	protected ExtendedIDEALAnaylsis createAnalysis() {
		return new ExtendedIDEALAnaylsis() {
			
			@Override
			protected ObservableICFG<Unit, SootMethod> icfg() {
				return icfg;
			}
			
			@Override
			public SootBasedStateMachineGraph getStateMachine() {
				return new SootBasedStateMachineGraph(getRule().getUsagePattern());
			}
			
			@Override
			public CrySLResultsReporter analysisListener() {
				return null;
			}
			
			@Override
			protected Debugger<TransitionFunction> debugger(IDEALSeedSolver<TransitionFunction> solver) {
				return getDebugger();
			}
		};
	}

	protected CrySLRule getRule() {
		try {
			return CrySLRulesetSelector.makeSingleRule(RULES_BASE_DIR, ruleFormat, getRuleset(), getRulename());
		} catch (CryptoAnalysisException e) {
			LOGGER.error("Error happened when getting the CrySL rules from the specified directory: "+RULES_BASE_DIR, e);
		}
		return null;
	}

	protected abstract String getRulename();

	protected abstract Ruleset getRuleset();

	@Override
	public List<String> excludedPackages() {
		List<String> excludedPackages = super.excludedPackages();
		excludedPackages.add(getRule().getClassName());
		return excludedPackages;
	}
	
	protected Debugger<TransitionFunction> getDebugger() {
		if(debugger == null)
			debugger =  new IDEVizDebugger<>(ideVizFile, icfg);
		return debugger;
	}

	@Override
	protected SceneTransformer createAnalysisTransformer() throws ImprecisionException {
		return new SceneTransformer() {
			protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {
				BoomerangPretransformer.v().reset();
				BoomerangPretransformer.v().apply();
				staticIcfg = new JimpleBasedInterproceduralCFG(true);
//				icfg = new ObservableStaticICFG(new JimpleBasedInterproceduralCFG(true));
				icfg = new ObservableDynamicICFG(true);
				Set<Assertion> expectedResults = parseExpectedQueryResults(sootTestMethod);
				TestingResultReporter testingResultReporter = new TestingResultReporter(expectedResults);
				Map<WeightedForwardQuery<TransitionFunction>, ForwardBoomerangResults<TransitionFunction>> seedToSolvers = executeAnalysis();
				for(WeightedForwardQuery<TransitionFunction> seed : seedToSolvers.keySet()){
					ForwardBoomerangResults<TransitionFunction> res = seedToSolvers.get(seed);
					testingResultReporter.onSeedFinished(seed.asNode(), res.asStatementValWeightTable());
				}
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

	protected Map<WeightedForwardQuery<TransitionFunction>, ForwardBoomerangResults<TransitionFunction>> executeAnalysis() {
		CrySLMethodToSootMethod.reset();
		ExtendedIDEALAnaylsis analysis = IDEALCrossingTestingFramework.this.createAnalysis();
		return analysis.run();
	}

	private Set<Assertion> parseExpectedQueryResults(SootMethod sootTestMethod) {
		Set<Assertion> results = new HashSet<>();
		parseExpectedQueryResults(sootTestMethod, results, new HashSet<SootMethod>());
		return results;
	}

	private void parseExpectedQueryResults(SootMethod m, Set<Assertion> queries, Set<SootMethod> visited) {
		if (!m.hasActiveBody() || visited.contains(m))
			return;
		visited.add(m);
		Body activeBody = m.getActiveBody();
		for (Unit callSite : staticIcfg.getCallsFromWithin(m)) {
			for (SootMethod callee : staticIcfg.getCalleesOfCallAt(callSite))
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
			Val val = new Val(queryVar, m);
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
