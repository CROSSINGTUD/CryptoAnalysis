package crypto.extractparameter;

import boomerang.scene.CallGraph;
import boomerang.scene.DataFlowScope;
import boomerang.scene.Statement;
import boomerang.scene.sparse.SparseCFGCache;
import crypto.listener.AnalysisReporter;
import crypto.rules.CrySLRule;

import java.util.Collection;

public interface ExtractParameterDefinition {

    CallGraph getCallGraph();

    DataFlowScope getDataFlowScope();

    Collection<Statement> getCollectedCalls();

    CrySLRule getRule();

    AnalysisReporter getAnalysisReporter();

    SparseCFGCache.SparsificationStrategy getSparsificationStrategy();

    int getTimeout();
}
