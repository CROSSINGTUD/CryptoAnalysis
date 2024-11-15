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

public class TypestateAnalysis {

    private final TypestateDefinition definition;
    private final TypestateAnalysisScope analysisScope;
    private final StoreIDEALResultHandler<TransitionFunction> resultHandler;

    public TypestateAnalysis(TypestateDefinition definition) {
        this.definition = definition;

        Map<String, RuleTransitions> transitions = new HashMap<>();
        for (CrySLRule rule : definition.getRuleset()) {
            transitions.put(rule.getClassName(), RuleTransitions.of(rule));
        }

        analysisScope = new TypestateAnalysisScope(definition.getCallGraph(), transitions, definition.getDataFlowScope());
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
        return new IDEALAnalysisDefinition<>() {
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
                return definition.getCallGraph();
            }

            @Override
            public Debugger<TransitionFunction> debugger(IDEALSeedSolver<TransitionFunction> idealSeedSolver) {
                return definition.getDebugger(idealSeedSolver);
            }

            @Override
            protected DataFlowScope getDataFlowScope() {
                return definition.getDataFlowScope();
            }

            @Override
            public IDEALResultHandler<TransitionFunction> getResultHandler() {
                return resultHandler;
            }

            @Override
            public BoomerangOptions boomerangOptions() {
                return new TypestateAnalysisOptions(definition.getTimeout());
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

}
