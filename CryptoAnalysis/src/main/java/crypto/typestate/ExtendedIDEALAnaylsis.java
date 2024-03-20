package crypto.typestate;

import boomerang.BoomerangOptions;
import boomerang.ForwardQuery;
import boomerang.Query;
import boomerang.WeightedForwardQuery;
import boomerang.debugger.Debugger;
import boomerang.results.ForwardBoomerangResults;
import boomerang.scene.CallGraph;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.DataFlowScope;
import boomerang.scene.Method;
import boomerang.scene.Statement;
import boomerang.scene.Val;
import boomerang.scene.jimple.JimpleMethod;
import com.google.common.collect.Maps;
import crypto.analysis.CrySLResultsReporter;
import crypto.analysis.IAnalysisSeed;
import crypto.boomerang.CogniCryptBoomerangOptions;
import ideal.IDEALAnalysis;
import ideal.IDEALAnalysisDefinition;
import ideal.IDEALSeedSolver;
import ideal.IDEALSeedTimeout;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.util.queue.QueueReader;
import sync.pds.solver.WeightFunctions;
import typestate.TransitionFunction;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

	public abstract CrySLResultsReporter analysisListener();

	public Collection<WeightedForwardQuery<TransitionFunction>> computeSeeds(Method method) {
		Collection<WeightedForwardQuery<TransitionFunction>> seeds = new HashSet<>();
		/*if (!method.hasActiveBody())
			return seeds;
		for (Unit u : method.getActiveBody().getUnits()) {
			seeds.addAll(getOrCreateTypestateChangeFunction().generateSeed(method, u));
		}*/

		for (Statement statement : method.getStatements()) {
			seeds.addAll(getOrCreateTypestateChangeFunction().generateSeed(new ControlFlowGraph.Edge(statement, statement)));
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
			seeds.addAll(computeSeeds(JimpleMethod.of(next.method())));
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
