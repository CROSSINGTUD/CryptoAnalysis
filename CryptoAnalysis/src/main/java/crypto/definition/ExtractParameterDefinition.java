package crypto.definition;

import boomerang.scene.CallGraph;
import boomerang.scene.DataFlowScope;
import boomerang.scene.Statement;
import crypto.listener.AnalysisReporter;
import crypto.rules.CrySLRule;

import java.util.Collection;

public interface ExtractParameterDefinition {

    CallGraph getCallGraph();

    DataFlowScope getDataFlowScope();

    Collection<Statement> getCollectedCalls();

    CrySLRule getRule();

    AnalysisReporter getAnalysisReporter();

    int getTimeout();
}
