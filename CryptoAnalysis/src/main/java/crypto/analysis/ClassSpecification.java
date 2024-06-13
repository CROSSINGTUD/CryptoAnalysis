package crypto.analysis;

import boomerang.WeightedForwardQuery;
import boomerang.debugger.Debugger;
import boomerang.scene.CallGraph;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.DataFlowScope;
import boomerang.scene.Method;
import crypto.rules.CrySLRule;
import crypto.typestate.ExtendedIDEALAnalysis;
import crypto.typestate.MatcherTransitionCollection;
import ideal.IDEALSeedSolver;
import typestate.TransitionFunction;

import java.util.Collection;

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
