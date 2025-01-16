package crypto.definition;

import boomerang.scene.CallGraph;
import boomerang.scene.DataFlowScope;
import boomerang.scene.sparse.SparseCFGCache;
import crypto.listener.AnalysisReporter;

public interface Definitions {

    record SeedDefinition(
            CallGraph callGraph,
            DataFlowScope dataFlowScope,
            int timeout,
            SparseCFGCache.SparsificationStrategy strategy,
            AnalysisReporter reporter) {}

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
