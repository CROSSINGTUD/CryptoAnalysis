package crypto.typestate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import boomerang.scene.CallGraph;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.DataFlowScope;
import com.google.common.collect.Maps;

import boomerang.BoomerangOptions;
import boomerang.ForwardQuery;
import boomerang.Query;
import boomerang.WeightedForwardQuery;
import boomerang.debugger.Debugger;
import boomerang.scene.Val;
import boomerang.results.ForwardBoomerangResults;
import crypto.analysis.CrySLResultsReporter;
import crypto.analysis.IAnalysisSeed;
import crypto.boomerang.CogniCryptBoomerangOptions;
import ideal.IDEALAnalysis;
import ideal.IDEALAnalysisDefinition;
import ideal.IDEALSeedSolver;
import ideal.IDEALSeedTimeout;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.util.queue.QueueReader;
import sync.pds.solver.WeightFunctions;
import typestate.TransitionFunction;

public abstract class ExtendedIDEALAnaylsis {

	private FiniteStateMachineToTypestateChangeFunction changeFunction;
	private final IDEALAnalysis<TransitionFunction> analysis;
	private ForwardBoomerangResults<TransitionFunction> results;

	public ExtendedIDEALAnaylsis() {
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
				return ExtendedIDEALAnaylsis.this.callGraph();
			}

			@Override
			public DataFlowScope getDataFlowScope() {
				return ExtendedIDEALAnaylsis.this.getDataFlowScope();
			}

            @Override
			public Debugger<TransitionFunction> debugger(IDEALSeedSolver<TransitionFunction> solver) {
				return ExtendedIDEALAnaylsis.this.debugger(solver);
			}

			@Override
			public BoomerangOptions boomerangOptions() {
				return new CogniCryptBoomerangOptions();
			}
		});
	}

	private FiniteStateMachineToTypestateChangeFunction getOrCreateTypestateChangeFunction() {
		if (this.changeFunction == null)
			this.changeFunction = new FiniteStateMachineToTypestateChangeFunction(getStateMachine());
		return this.changeFunction;
	}

	public abstract SootBasedStateMachineGraph getStateMachine();

	public void run(ForwardQuery query) {
		CrySLResultsReporter reports = analysisListener();
		try {
			results = analysis.run(query);
		} catch (IDEALSeedTimeout e) {
			if (reports != null && query instanceof IAnalysisSeed) {
				reports.onSeedTimeout(((IAnalysisSeed) query).asNode());
			}
		}
	}

	protected abstract CallGraph callGraph();

	protected abstract DataFlowScope getDataFlowScope();

	protected abstract Debugger<TransitionFunction> debugger(IDEALSeedSolver<TransitionFunction> solver);

	public void log(String string) {
		// System.out.println(string);
	}

	public abstract CrySLResultsReporter analysisListener();

	public Collection<WeightedForwardQuery<TransitionFunction>> computeSeeds(SootMethod method) {
		Collection<WeightedForwardQuery<TransitionFunction>> seeds = new HashSet<>();
		if (!method.hasActiveBody())
			return seeds;
		for (Unit u : method.getActiveBody().getUnits()) {
			seeds.addAll(getOrCreateTypestateChangeFunction().generateSeed(method, u));
		}
		return seeds;
	}


    /**
     * Only use this method for testing
     * 
     * @return map with the forward query
     */
	public Map<WeightedForwardQuery<TransitionFunction>, ForwardBoomerangResults<TransitionFunction>> run() {
		Set<WeightedForwardQuery<TransitionFunction>> seeds = new HashSet<>();
		ReachableMethods rm = Scene.v().getReachableMethods();
		QueueReader<MethodOrMethodContext> listener = rm.listener();
		while (listener.hasNext()) {
			MethodOrMethodContext next = listener.next();
			seeds.addAll(computeSeeds(next.method()));
		}
		Map<WeightedForwardQuery<TransitionFunction>, ForwardBoomerangResults<TransitionFunction>> seedToSolver = Maps
				.newHashMap();
		for (Query s : seeds) {
			if (s instanceof WeightedForwardQuery) {
				@SuppressWarnings("unchecked")
				WeightedForwardQuery<TransitionFunction> seed = (WeightedForwardQuery<TransitionFunction>) s;
				run((WeightedForwardQuery<TransitionFunction>) seed);
				if (getResults() != null) {
					seedToSolver.put(seed, getResults());
				}
			}
		}
		return seedToSolver;
	}

	public ForwardBoomerangResults<TransitionFunction> getResults() {
		return results;
	}

}
