package crypto.analysis;

import boomerang.WeightedForwardQuery;
import boomerang.debugger.Debugger;
import boomerang.scene.CallGraph;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.DataFlowScope;
import boomerang.scene.DeclaredMethod;
import boomerang.scene.Method;
import boomerang.scene.Statement;
import crypto.analysis.errors.ForbiddenMethodError;
import crypto.rules.CrySLForbiddenMethod;
import crypto.rules.CrySLMethod;
import crypto.rules.CrySLRule;
import crypto.typestate.ExtendedIDEALAnalysis;
import crypto.typestate.MatcherTransitionCollection;
import crypto.typestate.MatcherUtils;
import ideal.IDEALSeedSolver;
import typestate.TransitionFunction;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ClassSpecification {

	private final ExtendedIDEALAnalysis extendedIdealAnalysis;
	private final CrySLRule crySLRule;
	private final CryptoScanner cryptoScanner;
	private final MatcherTransitionCollection matcherTransitions;

	public ClassSpecification(final CrySLRule rule, final CryptoScanner cScanner) {
		this.crySLRule = rule;
		this.cryptoScanner = cScanner;
		this.matcherTransitions = new MatcherTransitionCollection(rule.getUsagePattern());
		this.extendedIdealAnalysis = new ExtendedIDEALAnalysis() {

			@Override
			public MatcherTransitionCollection getMatcherTransitions() {
				return matcherTransitions;
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

	public Collection<WeightedForwardQuery<TransitionFunction>> getInitialSeeds(Method m) {
		return extendedIdealAnalysis.computeSeeds(m);
	}


	@Override
	public String toString() {
		return crySLRule.getClassName();
	}

	public void invokesForbiddenMethod(Method m) {
		for (Statement statement : m.getStatements()) {
			if (!statement.containsInvokeExpr()) {
				continue;
			}

			DeclaredMethod declaredMethod = statement.getInvokeExpr().getMethod();
			Optional<CrySLForbiddenMethod> forbiddenMethod = isForbiddenMethod(declaredMethod);
			if (forbiddenMethod.isPresent()){
				Collection<CrySLMethod> alternatives = forbiddenMethod.get().getAlternatives();
				ForbiddenMethodError error = new ForbiddenMethodError(statement, this.getRule(), declaredMethod, alternatives);
				cryptoScanner.getAnalysisListener().reportError(null, error);
			}
		}
	}

	private Optional<CrySLForbiddenMethod> isForbiddenMethod(DeclaredMethod declaredMethod) {
		// TODO replace by real specification once available.
		List<CrySLForbiddenMethod> forbiddenMethods = crySLRule.getForbiddenMethods();
		//TODO Iterate over ICFG and report on usage of forbidden method.
		for (CrySLForbiddenMethod method : forbiddenMethods) {
			if (method.getSilent()) {
				continue;
			}

			// TODO Refactoring
			//Collection<Method> matchingMethod = CrySLMethodToSootMethod.v().convert(m.getMethod());
			//if(matchingMethod.contains(method))
			//		return Optional.of(m);
			if (MatcherUtils.matchCryslMethodAndDeclaredMethod(method.getMethod(), declaredMethod)) {
				return Optional.of(method);
			}
		}
		return Optional.empty();
	}

	public CrySLRule getRule() {
		return crySLRule;
	}

	public MatcherTransitionCollection getMatcherTransitions() {
		return matcherTransitions;
	}

	public TransitionFunction getInitialWeight(ControlFlowGraph.Edge stmt) {
		return matcherTransitions.getInitialWeight(stmt);
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

}
