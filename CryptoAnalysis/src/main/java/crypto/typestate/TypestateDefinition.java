package crypto.typestate;

import boomerang.scene.CallGraph;
import boomerang.scene.DataFlowScope;
import crysl.rule.CrySLRule;
import java.util.Collection;

public interface TypestateDefinition {

    Collection<CrySLRule> getRuleset();

    CallGraph getCallGraph();

    DataFlowScope getDataFlowScope();

    int getTimeout();
}
