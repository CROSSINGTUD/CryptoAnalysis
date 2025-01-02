package crypto.definition;

import boomerang.scene.CallGraph;
import boomerang.scene.DataFlowScope;
import boomerang.scene.sparse.SparseCFGCache;
import crypto.listener.AnalysisReporter;

public interface Definitions {

    record ConstraintsDefinition(
            CallGraph callGraph,
            DataFlowScope dataFlowScope,
            int timeout,
            boomerang.scene.sparse.SparseCFGCache.SparsificationStrategy strategy,
            AnalysisReporter reporter) {}

    record ExtractParameterDefinition(
            CallGraph callGraph,
            DataFlowScope dataFlowScope,
            int timeout,
            SparseCFGCache.SparsificationStrategy strategy) {}

    record QuerySolverDefinition(
            CallGraph callGraph,
            DataFlowScope dataFlowScope,
            int timeout,
            SparseCFGCache.SparsificationStrategy strategy) {}

    record BoomerangOptionsDefinition(
            CallGraph callGraph,
            DataFlowScope dataFlowScope,
            int timeout,
            SparseCFGCache.SparsificationStrategy strategy) {}
}
