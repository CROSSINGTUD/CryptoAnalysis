package crypto.extractparameter;

import boomerang.scene.CallGraph;
import boomerang.scene.DataFlowScope;
import boomerang.scene.sparse.SparseCFGCache;

public interface Definitions {

    record ExtractParameterDefinition(CallGraph callGraph, DataFlowScope dataFlowScope, int timeout, SparseCFGCache.SparsificationStrategy strategy) {}

    record QuerySolverDefinition(CallGraph callGraph, DataFlowScope dataFlowScope, int timeout, SparseCFGCache.SparsificationStrategy strategy) {}

    record BoomerangOptionsDefinition(CallGraph callGraph, DataFlowScope dataFlowScope, int timeout, SparseCFGCache.SparsificationStrategy strategy) {}
}
