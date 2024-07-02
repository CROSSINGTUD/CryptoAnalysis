package crypto.typestate;

import boomerang.BoomerangOptions;
import boomerang.Query;
import boomerang.WeightedForwardQuery;
import boomerang.debugger.Debugger;
import boomerang.results.ForwardBoomerangResults;
import boomerang.scene.CallGraph;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.DataFlowScope;
import boomerang.scene.Val;
import crypto.rules.CrySLRule;
import ideal.IDEALAnalysis;
import ideal.IDEALAnalysisDefinition;
import ideal.IDEALResultHandler;
import ideal.IDEALSeedSolver;
import ideal.StoreIDEALResultHandler;
import sync.pds.solver.WeightFunctions;
import typestate.TransitionFunction;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class TypestateAnalysis {

    private final StoreIDEALResultHandler<TransitionFunction> resultHandler;
    private final TypestateAnalysisScope analysisScope;

    public TypestateAnalysis(Collection<CrySLRule> rules) {
        Map<String, RuleTransitions> transitions = new HashMap<>();

        for (CrySLRule rule : rules) {
            transitions.put(rule.getClassName(), RuleTransitions.of(rule));
        }

        analysisScope = new TypestateAnalysisScope(callGraph(), transitions, getDataFlowScope());
        resultHandler = new StoreIDEALResultHandler<>();
    }

    public void runTypestateAnalysis() {
        // Compute all seeds in the program
        Collection<Query> seeds = analysisScope.computeSeeds();

        for (Query seed : seeds) {
            if (!(seed instanceof ForwardSeedQuery)) {
                continue;
            }

            ForwardSeedQuery query = (ForwardSeedQuery) seed;
            runTypestateAnalysisForSeed(query);
        }
    }

    private void runTypestateAnalysisForSeed(ForwardSeedQuery query) {
        // Initialize typestate function
        Collection<LabeledMatcherTransition> transitions = query.getAllTransitions();
        TypestateFunction typestateFunction = new TypestateFunction(transitions);

        // Initialize and run IDE with Aliasing
        IDEALAnalysis<TransitionFunction> idealAnalysis = new IDEALAnalysis<>(getIdealAnalysisDefinition(typestateFunction));
        idealAnalysis.run(query);
    }

    private IDEALAnalysisDefinition<TransitionFunction> getIdealAnalysisDefinition(TypestateFunction typestateFunction) {
        return new IDEALAnalysisDefinition<TransitionFunction>() {
            @Override
            public Collection<WeightedForwardQuery<TransitionFunction>> generate(ControlFlowGraph.Edge stmt) {
                return typestateFunction.generateSeed(stmt);
            }

            @Override
            public WeightFunctions<ControlFlowGraph.Edge, Val, ControlFlowGraph.Edge, TransitionFunction> weightFunctions() {
                return typestateFunction;
            }

            @Override
            public CallGraph callGraph() {
                return TypestateAnalysis.this.callGraph();
            }

            @Override
            public Debugger<TransitionFunction> debugger(IDEALSeedSolver<TransitionFunction> idealSeedSolver) {
                return TypestateAnalysis.this.debugger(idealSeedSolver);
            }

            @Override
            protected DataFlowScope getDataFlowScope() {
                return TypestateAnalysis.this.getDataFlowScope();
            }

            @Override
            public IDEALResultHandler<TransitionFunction> getResultHandler() {
                return resultHandler;
            }

            @Override
            public BoomerangOptions boomerangOptions() {
                return super.boomerangOptions();
            }
        };
    }

    public Map<ForwardSeedQuery, ForwardBoomerangResults<TransitionFunction>> getResults() {
        Map<ForwardSeedQuery, ForwardBoomerangResults<TransitionFunction>> results = new HashMap<>();

        for (Map.Entry<WeightedForwardQuery<TransitionFunction>, ForwardBoomerangResults<TransitionFunction>> entry : resultHandler.getResults().entrySet()) {
            if (!(entry.getKey() instanceof ForwardSeedQuery)) {
                continue;
            }

            ForwardSeedQuery forwardSeedQuery = (ForwardSeedQuery) entry.getKey();
            results.put(forwardSeedQuery, entry.getValue());
        }
        return results;
    }

    public abstract CallGraph callGraph();

    public abstract DataFlowScope getDataFlowScope();

    public abstract Debugger<TransitionFunction> debugger(IDEALSeedSolver<TransitionFunction> idealSeedSolver);
}
