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
import crypto.definition.Definitions;
import crysl.rule.CrySLRule;
import ideal.IDEALAnalysis;
import ideal.IDEALAnalysisDefinition;
import ideal.IDEALResultHandler;
import ideal.IDEALSeedSolver;
import ideal.StoreIDEALResultHandler;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import sync.pds.solver.WeightFunctions;
import typestate.TransitionFunction;

public class TypestateAnalysis {

    private final Definitions.TypestateDefinition definition;
    private final TypestateAnalysisScope analysisScope;
    private final StoreIDEALResultHandler<TransitionFunction> resultHandler;

    public TypestateAnalysis(Definitions.TypestateDefinition definition) {
        this.definition = definition;

        Map<String, RuleTransitions> transitions = new HashMap<>();
        for (CrySLRule rule : definition.rules()) {
            transitions.put(rule.getClassName(), RuleTransitions.of(rule));
        }

        analysisScope =
                new TypestateAnalysisScope(
                        definition.callGraph(), transitions, definition.dataFlowScope());
        resultHandler = new StoreIDEALResultHandler<>();
    }

    public void runTypestateAnalysis() {
        // Compute all seeds in the program
        Collection<Query> seeds = analysisScope.computeSeeds();

        for (Query seed : seeds) {
            if (!(seed instanceof ForwardSeedQuery query)) {
                continue;
            }

            runTypestateAnalysisForSeed(query);
        }
    }

    private void runTypestateAnalysisForSeed(ForwardSeedQuery query) {
        // Initialize typestate function
        Collection<LabeledMatcherTransition> transitions = query.getAllTransitions();
        TypestateFunction typestateFunction = new TypestateFunction(transitions);

        // Initialize and run IDE with Aliasing
        IDEALAnalysisDefinition<TransitionFunction> idealDefinition =
                getIdealAnalysisDefinition(query, typestateFunction);
        IDEALAnalysis<TransitionFunction> idealAnalysis = new IDEALAnalysis<>(idealDefinition);
        idealAnalysis.run(query);
    }

    private IDEALAnalysisDefinition<TransitionFunction> getIdealAnalysisDefinition(
            ForwardSeedQuery seedQuery, TypestateFunction typestateFunction) {
        return new IDEALAnalysisDefinition<>() {
            @Override
            public Collection<WeightedForwardQuery<TransitionFunction>> generate(
                    ControlFlowGraph.Edge stmt) {
                return typestateFunction.generateSeed(stmt);
            }

            @Override
            public WeightFunctions<
                            ControlFlowGraph.Edge, Val, ControlFlowGraph.Edge, TransitionFunction>
                    weightFunctions() {
                return typestateFunction;
            }

            @Override
            public CallGraph callGraph() {
                return definition.callGraph();
            }

            @Override
            public Debugger<TransitionFunction> debugger(
                    IDEALSeedSolver<TransitionFunction> idealSeedSolver) {
                return new Debugger<>();
            }

            @Override
            protected DataFlowScope getDataFlowScope() {
                return definition.dataFlowScope();
            }

            @Override
            public IDEALResultHandler<TransitionFunction> getResultHandler() {
                return resultHandler;
            }

            @Override
            public BoomerangOptions boomerangOptions() {
                return new TypestateAnalysisOptions(seedQuery, definition.timeout());
            }
        };
    }

    public Map<ForwardSeedQuery, ForwardBoomerangResults<TransitionFunction>> getResults() {
        Map<ForwardSeedQuery, ForwardBoomerangResults<TransitionFunction>> results =
                new HashMap<>();

        for (Map.Entry<
                        WeightedForwardQuery<TransitionFunction>,
                        ForwardBoomerangResults<TransitionFunction>>
                entry : resultHandler.getResults().entrySet()) {
            if (!(entry.getKey() instanceof ForwardSeedQuery forwardSeedQuery)) {
                continue;
            }

            results.put(forwardSeedQuery, entry.getValue());
        }
        return results;
    }
}
