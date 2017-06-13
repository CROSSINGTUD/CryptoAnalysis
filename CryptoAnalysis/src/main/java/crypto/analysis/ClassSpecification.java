package crypto.analysis;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.crypto.spec.PBEKeySpec;

import boomerang.accessgraph.AccessGraph;
import boomerang.cfg.IExtendedICFG;
import crypto.rules.CryptSLForbiddenMethod;
import crypto.rules.CryptSLRule;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.typestate.CryptoTypestateAnaylsisProblem;
import crypto.typestate.ExtendedStandardFlowFunction;
import crypto.typestate.FiniteStateMachineToTypestateChangeFunction;
import crypto.typestate.StatementLabelToSootMethod;
import ideal.Analysis;
import ideal.FactAtStatement;
import ideal.IFactAtStatement;
import ideal.PerSeedAnalysisContext;
import ideal.ResultReporter;
import ideal.debug.IDebugger;
import ideal.flowfunctions.StandardFlowFunctions;
import soot.Body;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.util.queue.QueueReader;
import typestate.TypestateDomainValue;
import typestate.finiteautomata.Transition;

public class ClassSpecification {
	private CryptoTypestateAnaylsisProblem problem;
	private CryptSLRule cryptSLRule;
	private final CryptoScanner cryptoScanner;

	public ClassSpecification(final CryptSLRule rule, final CryptoScanner cScanner) {
		this.cryptSLRule = rule;
		this.cryptoScanner = cScanner;
		createTypestateAnalysis();
	}

	public boolean isRootNode() {
		return true;
	}

	public Set<IFactAtStatement> getInitialSeeds() {
		return createTypestateAnalysis().computeSeeds();
	}

	public CryptoTypestateAnaylsisProblem getAnalysisProblem() {
		return problem;
	}

	@Override
	public String toString() {
		return cryptSLRule.toString();
	}

	public void checkForForbiddenMethods() {
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
		List<CryptSLForbiddenMethod> forbiddenMethods = cryptSLRule.getForbiddenMethods();
//		System.out.println(forbiddenMethods);
		//TODO Iterate over ICFG and report on usage of forbidden method.
		for(CryptSLForbiddenMethod m : forbiddenMethods){
			Collection<SootMethod> matchingMatched = StatementLabelToSootMethod.v().convert(m.getMethod());
			if(matchingMatched.contains(method))
				return true;
		}
		return false;
	}

	public Analysis<TypestateDomainValue<StateNode>> createTypestateAnalysis() {
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
			public IExtendedICFG icfg() {
				return cryptoScanner.icfg();
			}

			@Override
			public IDebugger<TypestateDomainValue<StateNode>> debugger() {
				return cryptoScanner.debugger();
			}

			@Override
			public StateMachineGraph getStateMachine() {
				return cryptSLRule.getUsagePattern();
			}

			@Override
			public StandardFlowFunctions<TypestateDomainValue<StateNode>> flowFunctions(
					PerSeedAnalysisContext<TypestateDomainValue<StateNode>> context) {
				return new ExtendedStandardFlowFunction(context, cryptSLRule);
			}

		};
		return new Analysis<TypestateDomainValue<StateNode>>(problem);
	}

	public CryptSLRule getRule() {
		return cryptSLRule;
	}

}
