package crypto.typestate;

import boomerang.debugger.Debugger;
import boomerang.scene.CallGraph;
import boomerang.scene.DataFlowScope;
import crypto.rules.CrySLRule;
import ideal.IDEALSeedSolver;
import typestate.TransitionFunction;

import java.util.Collection;

public interface TypestateDefinition {

    Collection<CrySLRule> getRuleset();

    CallGraph getCallGraph();

    DataFlowScope getDataFlowScope();

    Debugger<TransitionFunction> getDebugger(IDEALSeedSolver<TransitionFunction> idealSeedSolver);

    int getTimeout();
}
