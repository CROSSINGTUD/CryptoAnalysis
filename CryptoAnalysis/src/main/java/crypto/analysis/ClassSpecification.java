package crypto.analysis;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import boomerang.cfg.IExtendedICFG;
import boomerang.util.StmtWithMethod;
import crypto.rules.CryptSLForbiddenMethod;
import crypto.rules.CryptSLRule;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.typestate.CryptSLMethodToSootMethod;
import crypto.typestate.CryptoTypestateAnaylsisProblem;
import crypto.typestate.FiniteStateMachineToTypestateChangeFunction;
import ideal.Analysis;
import ideal.AnalysisSolver;
import ideal.IFactAtStatement;
import ideal.ResultReporter;
import ideal.debug.IDebugger;
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

public class ClassSpecification {
	private CryptoTypestateAnaylsisProblem problem;
	private CryptSLRule cryptSLRule;
	private final CryptoScanner cryptoScanner;

	public ClassSpecification(final CryptSLRule rule, final CryptoScanner cScanner) {
		this.cryptSLRule = rule;
		this.cryptoScanner = cScanner;
		this.problem = new CryptoTypestateAnaylsisProblem() {
			@Override
			public ResultReporter<TypestateDomainValue<StateNode>> resultReporter() {
				return new ResultReporter<TypestateDomainValue<StateNode>>() {
					@Override
					public void onSeedFinished(IFactAtStatement seed, AnalysisSolver<TypestateDomainValue<StateNode>> solver) {
					}

					@Override
					public void onSeedTimeout(IFactAtStatement seed) {
					}
				};
			}

			@Override
			public FiniteStateMachineToTypestateChangeFunction createTypestateChangeFunction() {
				return new FiniteStateMachineToTypestateChangeFunction(this);
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
		};
	}

	public boolean isLeafRule() {
		return cryptSLRule.isLeafRule();
	}

	public Set<IFactAtStatement> getInitialSeeds() {
		return new Analysis<TypestateDomainValue<StateNode>>(problem).computeSeeds();
	}

	public CryptoTypestateAnaylsisProblem getAnalysisProblem() {
		return problem;
	}

	@Override
	public String toString() {
		return cryptSLRule.getClassName().toString();
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
					cryptoScanner.getAnalysisListener().callToForbiddenMethod(this, new StmtWithMethod(u, cryptoScanner.icfg().getMethodOf(u)));
			}
		}
	}

	private boolean isForbiddenMethod(SootMethod method) {
		// TODO replace by real specification once available.
		List<CryptSLForbiddenMethod> forbiddenMethods = cryptSLRule.getForbiddenMethods();
//		System.out.println(forbiddenMethods);
		//TODO Iterate over ICFG and report on usage of forbidden method.
		for(CryptSLForbiddenMethod m : forbiddenMethods){
			if(!m.getSilent()){
				Collection<SootMethod> matchingMatched = CryptSLMethodToSootMethod.v().convert(m.getMethod());
				if(matchingMatched.contains(method))
					return true;
			}
		}
		return false;
	}


	public CryptSLRule getRule() {
		return cryptSLRule;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cryptSLRule == null) ? 0 : cryptSLRule.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClassSpecification other = (ClassSpecification) obj;
		if (cryptSLRule == null) {
			if (other.cryptSLRule != null)
				return false;
		} else if (!cryptSLRule.equals(other.cryptSLRule))
			return false;
		return true;
	}

}
