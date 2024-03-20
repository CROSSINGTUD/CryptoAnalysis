package crypto.analysis;

import boomerang.WeightedForwardQuery;
import boomerang.debugger.Debugger;
import boomerang.scene.CallGraph;
import boomerang.scene.DataFlowScope;
import boomerang.scene.DeclaredMethod;
import boomerang.scene.InvokeExpr;
import boomerang.scene.Method;
import boomerang.scene.Statement;
import boomerang.scene.jimple.JimpleStatement;
import crypto.analysis.errors.ForbiddenMethodError;
import crypto.rules.CrySLForbiddenMethod;
import crypto.rules.CrySLRule;
import crypto.typestate.CrySLMethodToSootMethod;
import crypto.typestate.ExtendedIDEALAnaylsis;
import crypto.typestate.SootBasedStateMachineGraph;
import ideal.IDEALSeedSolver;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.Stmt;
import typestate.TransitionFunction;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ClassSpecification {
	private ExtendedIDEALAnaylsis extendedIdealAnalysis;
	private CrySLRule crySLRule;
	private final CryptoScanner cryptoScanner;
	private final SootBasedStateMachineGraph fsm;

	public ClassSpecification(final CrySLRule rule, final CryptoScanner cScanner) {
		this.crySLRule = rule;
		this.cryptoScanner = cScanner;
		this.fsm = new SootBasedStateMachineGraph(rule.getUsagePattern());
		this.extendedIdealAnalysis = new ExtendedIDEALAnaylsis() {
			@Override
			public SootBasedStateMachineGraph getStateMachine() {
				return fsm;
			}

			@Override
			public CrySLResultsReporter analysisListener() {
				return cryptoScanner.getAnalysisListener();
			}

			@Override
			public CallGraph callGraph() {
				return cryptoScanner.callGraph();
			}

			@Override
			public DataFlowScope getDataFlowScope() {
				return cryptoScanner.getDataFlowScope();
			}

			@Override
			protected Debugger<TransitionFunction> debugger(IDEALSeedSolver<TransitionFunction> solver) {
				return cryptoScanner.debugger(solver, null);
			}
		};
	}

	public boolean isLeafRule() {
		return crySLRule.isLeafRule();
	}

	

	public Collection<WeightedForwardQuery<TransitionFunction>> getInitialSeeds(Method m) {
		return extendedIdealAnalysis.computeSeeds(m);
	}


	@Override
	public String toString() {
		return crySLRule.getClassName().toString();
	}

	public void invokesForbiddenMethod(Method m) {
		for (Statement statement : m.getStatements()) {
			if (!statement.containsInvokeExpr()) {
				continue;
			}

			DeclaredMethod declaredMethod = statement.getInvokeExpr().getMethod();
			Method method = CrySLMethodToSootMethod.declaredMethodToJimpleMethod(declaredMethod);
			Optional<CrySLForbiddenMethod> forbiddenMethod = isForbiddenMethod(method);
			if (forbiddenMethod.isPresent()){
				cryptoScanner.getAnalysisListener().reportError(null, new ForbiddenMethodError(statement, this.getRule(), method, CrySLMethodToSootMethod.v().convert(forbiddenMethod.get().getAlternatives())));
			}
		}
	}

	private Optional<CrySLForbiddenMethod> isForbiddenMethod(Method method) {
		// TODO replace by real specification once available.
		List<CrySLForbiddenMethod> forbiddenMethods = crySLRule.getForbiddenMethods();
//		System.out.println(forbiddenMethods);
		//TODO Iterate over ICFG and report on usage of forbidden method.
		for(CrySLForbiddenMethod m : forbiddenMethods){
			if(!m.getSilent()){
				Collection<Method> matchingMethod = CrySLMethodToSootMethod.v().convert(m.getMethod());
				if(matchingMethod.contains(method))
					return Optional.of(m);
				
			}
		}
		return Optional.empty();
	}


	public CrySLRule getRule() {
		return crySLRule;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((crySLRule == null) ? 0 : crySLRule.hashCode());
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
		if (crySLRule == null) {
			if (other.crySLRule != null)
				return false;
		} else if (!crySLRule.equals(other.crySLRule))
			return false;
		return true;
	}

	public SootBasedStateMachineGraph getFSM(){
		return fsm;
	}

}
