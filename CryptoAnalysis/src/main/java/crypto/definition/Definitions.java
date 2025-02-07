package crypto.definition;

import boomerang.scene.CallGraph;
import boomerang.scene.DataFlowScope;
import boomerang.scene.sparse.SparseCFGCache;
import crypto.listener.AnalysisReporter;
import crysl.rule.CrySLRule;
import java.util.Collection;

public interface Definitions {

    record SeedDefinition(
            CallGraph callGraph,
            DataFlowScope dataFlowScope,
            int timeout,
            SparseCFGCache.SparsificationStrategy strategy,
            AnalysisReporter reporter) {}

    record TypestateDefinition(
            Collection<CrySLRule> rules,
            CallGraph callGraph,
            DataFlowScope dataFlowScope,
            int timeout) {}

    record ConstraintsDefinition(
            CallGraph callGraph,
            DataFlowScope dataFlowScope,
            int timeout,
            SparseCFGCache.SparsificationStrategy strategy,
            AnalysisReporter reporter) {}

    record ExtractParameterDefinition(
            CallGraph callGraph,
            DataFlowScope dataFlowScope,
            int timeout,
            SparseCFGCache.SparsificationStrategy strategy,
            AnalysisReporter reporter) {}

    record QuerySolverDefinition(
            CallGraph callGraph,
            DataFlowScope dataFlowScope,
            int timeout,
            SparseCFGCache.SparsificationStrategy strategy,
            AnalysisReporter reporter) {}

    record BoomerangOptionsDefinition(
            CallGraph callGraph,
            DataFlowScope dataFlowScope,
            int timeout,
            SparseCFGCache.SparsificationStrategy strategy) {}
}
