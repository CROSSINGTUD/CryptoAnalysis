package crypto.typestate;

import boomerang.debugger.Debugger;
import boomerang.scene.CallGraph;
import boomerang.scene.DataFlowScope;
import crysl.rule.CrySLRule;
import ideal.IDEALSeedSolver;
import java.util.Collection;
import typestate.TransitionFunction;

public interface TypestateDefinition {

    Collection<CrySLRule> getRuleset();

    CallGraph getCallGraph();

    DataFlowScope getDataFlowScope();

    Debugger<TransitionFunction> getDebugger(IDEALSeedSolver<TransitionFunction> idealSeedSolver);

    int getTimeout();
}
