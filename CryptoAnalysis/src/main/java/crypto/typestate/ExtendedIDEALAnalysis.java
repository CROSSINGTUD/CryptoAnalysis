package crypto.typestate;

import boomerang.BoomerangOptions;
import boomerang.ForwardQuery;
import boomerang.WeightedForwardQuery;
import boomerang.debugger.Debugger;
import boomerang.results.ForwardBoomerangResults;
import boomerang.scene.CallGraph;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.DataFlowScope;
import boomerang.scene.Method;
import boomerang.scene.Statement;
import boomerang.scene.Val;
import crypto.analysis.CrySLResultsReporter;
import crypto.boomerang.CogniCryptBoomerangOptions;
import ideal.IDEALAnalysis;
import ideal.IDEALAnalysisDefinition;
import ideal.IDEALSeedSolver;
import ideal.IDEALSeedTimeout;
import sync.pds.solver.WeightFunctions;
import typestate.TransitionFunction;

import java.util.Collection;
import java.util.HashSet;

public abstract class ExtendedIDEALAnalysis {

	private FiniteStateMachineToTypestateChangeFunction changeFunction;
	private final IDEALAnalysis<TransitionFunction> analysis;
	private ForwardBoomerangResults<TransitionFunction> results;

	public ExtendedIDEALAnalysis() {
		analysis = new IDEALAnalysis<>(new IDEALAnalysisDefinition<TransitionFunction>() {
			@Override
			public Collection<WeightedForwardQuery<TransitionFunction>> generate(ControlFlowGraph.Edge edge) {
				return getOrCreateTypestateChangeFunction().generateSeed(edge);
			}

			@Override
			public WeightFunctions<ControlFlowGraph.Edge, Val, ControlFlowGraph.Edge, TransitionFunction> weightFunctions() {
				return getOrCreateTypestateChangeFunction();
			}

			@Override
			public CallGraph callGraph() {
				return ExtendedIDEALAnalysis.this.callGraph();
			}

			@Override
			public DataFlowScope getDataFlowScope() {
				return ExtendedIDEALAnalysis.this.getDataFlowScope();
			}

            @Override
			public Debugger<TransitionFunction> debugger(IDEALSeedSolver<TransitionFunction> solver) {
				return ExtendedIDEALAnalysis.this.debugger(solver);
			}

			@Override
			public BoomerangOptions boomerangOptions() {
				return new CogniCryptBoomerangOptions();
			}
		});
	}

	private FiniteStateMachineToTypestateChangeFunction getOrCreateTypestateChangeFunction() {
		if (this.changeFunction == null)
			this.changeFunction = new FiniteStateMachineToTypestateChangeFunction(getMatcherTransitions());
		return this.changeFunction;
	}

	public abstract MatcherTransitionCollection getMatcherTransitions();

	public void run(ForwardQuery query) {
		CrySLResultsReporter reports = analysisListener();
		try {
			results = analysis.run(query);
		} catch (IDEALSeedTimeout e) {
			if (reports != null) {
				reports.onSeedTimeout(query.asNode());
			}
		}
	}

	protected abstract CallGraph callGraph();

	protected abstract DataFlowScope getDataFlowScope();

	protected abstract Debugger<TransitionFunction> debugger(IDEALSeedSolver<TransitionFunction> solver);

	public abstract CrySLResultsReporter analysisListener();

	public Collection<WeightedForwardQuery<TransitionFunction>> computeSeeds(Method method) {
		Collection<WeightedForwardQuery<TransitionFunction>> seeds = new HashSet<>();

		for (Statement statement : method.getStatements()) {
			Collection<Statement> successors = method.getControlFlowGraph().getSuccsOf(statement);

			for (Statement succStatement : successors) {
				seeds.addAll(getOrCreateTypestateChangeFunction().generateSeed(new ControlFlowGraph.Edge(statement, succStatement)));
			}
		}
		return seeds;
	}

	public ForwardBoomerangResults<TransitionFunction> getResults() {
		return results;
	}

}
