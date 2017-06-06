package crypto.analysis;

import java.util.List;
import java.util.Set;

import javax.crypto.spec.PBEKeySpec;

import com.google.common.collect.Multimap;

import boomerang.accessgraph.AccessGraph;
import crypto.rules.CryptSLRule;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.typestate.CallSiteWithParamIndex;
import crypto.typestate.CryptoTypestateAnaylsisProblem;
import crypto.typestate.ExtendedStandardFlowFunction;
import crypto.typestate.FiniteStateMachineToTypestateChangeFunction;
import ideal.Analysis;
import ideal.FactAtStatement;
import ideal.PerSeedAnalysisContext;
import ideal.ResultReporter;
import ideal.debug.IDebugger;
import ideal.debug.NullDebugger;
import ideal.flowfunctions.StandardFlowFunctions;
import soot.Body;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.StmtSwitch;
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.util.Switch;
import soot.util.queue.QueueReader;
import typestate.TypestateDomainValue;
import typestate.finiteautomata.Transition;
import typestate.interfaces.ISLConstraint;

public class ClassSpecification {
	private CryptoTypestateAnaylsisProblem problem;
	private CryptSLRule cryptSLRule;
	private Analysis<TypestateDomainValue<StateNode>> analysis;
	private final CryptoScanner cryptoScanner;

	public ClassSpecification(final CryptSLRule rule, final CryptoScanner cScanner) {
		this.cryptSLRule = rule;
		this.cryptoScanner = cScanner;
		this.problem = new CryptoTypestateAnaylsisProblem() {
			@Override
			public ResultReporter<TypestateDomainValue<StateNode>> resultReporter() {
				return cryptoScanner.analysisListener();
			}

			@Override
			public FiniteStateMachineToTypestateChangeFunction createTypestateChangeFunction() {
				return new FiniteStateMachineToTypestateChangeFunction(this) {
					@Override
					public Set<Transition<StateNode>> getCallToReturnTransitionsFor(AccessGraph d1, Unit callSite,
							AccessGraph d2, Unit returnSite, AccessGraph d3) {
						cryptoScanner.onCallToReturnFlow(ClassSpecification.this, d1, callSite, d2);
						return super.getCallToReturnTransitionsFor(d1, callSite, d2, returnSite, d3);
					}
				};
			}

			@Override
			public IInfoflowCFG icfg() {
				return cryptoScanner.icfg();
			}

			@Override
			public IDebugger<TypestateDomainValue<StateNode>> debugger() {
				return cryptoScanner.debugger();
			}

			@Override
			public StateMachineGraph getStateMachine() {
				return rule.getUsagePattern();
			}

			@Override
			public StandardFlowFunctions<TypestateDomainValue<StateNode>> flowFunctions(
					PerSeedAnalysisContext<TypestateDomainValue<StateNode>> context) {
				return new ExtendedStandardFlowFunction(context, rule);
			}

		};
		analysis = new Analysis<TypestateDomainValue<StateNode>>(problem);

	}

	public boolean isRootNode() {
		return true;
	}

	public void runTypestateAnalysisForAllSeeds() {
		analysis.run();
		cryptoScanner.analysisListener().collectedValues(this, problem.getCollectedValues());
		checkConstraintSystem();
	}

	private void checkConstraintSystem() {
		Multimap<CallSiteWithParamIndex, Value> actualValues = problem.getCollectedValues();
		ConstraintSolver solver = new ConstraintSolver();
		for (ISLConstraint cons : cryptSLRule.getConstraints()) {
			if (!solver.evaluate(cons, actualValues)) {
				// report error
			}
		}

	}

	public void runTypestateAnalysisForConcreteSeed(FactAtStatement seed) {
		analysis.analysisForSeed(seed);
		cryptoScanner.analysisListener().collectedValues(this, problem.getCollectedValues());
		checkConstraintSystem();
	}

	public CryptoTypestateAnaylsisProblem getAnalysisProblem() {
		return problem;
	}

	@Override
	public String toString() {
		return cryptSLRule.toString();
	}

	public void checkForForbiddenMethods() {
		List<String> forbiddenMethods = cryptSLRule.getForbiddenMethods();
		System.out.println(forbiddenMethods);
		// TODO Iterate over ICFG and report on usage of forbidden method.
		ReachableMethods rm = Scene.v().getReachableMethods();
		QueueReader<MethodOrMethodContext> listener = rm.listener();
		while (listener.hasNext()) {
			MethodOrMethodContext next = listener.next();
			SootMethod method = next.method();
			if (method == null || !method.hasActiveBody()) {
				continue;
			}
			invokesForbiddenMethod(method.getActiveBody());
		}
	}

	private void invokesForbiddenMethod(Body activeBody) {
		System.err.println(activeBody);
		for (Unit u : activeBody.getUnits()) {
			if (u instanceof Stmt) {
				Stmt stmt = (Stmt) u;
				if (!stmt.containsInvokeExpr())
					continue;
				InvokeExpr invokeExpr = stmt.getInvokeExpr();
				SootMethod method = invokeExpr.getMethod();
				if (isForbiddenMethod(method))
					cryptoScanner.analysisListener().callToForbiddenMethod(this, u);
			}
		}
	}

	private boolean isForbiddenMethod(SootMethod method) {
		// TODO replace by real specification once available.
		if (method.getDeclaringClass().toString().equals(PBEKeySpec.class.getName())) {
			if (method.isConstructor() && method.getParameterCount() < 4)
				return true;
		}
		return false;
	}
}
