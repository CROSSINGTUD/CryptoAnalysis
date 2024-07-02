package crypto.typestate;

import boomerang.WeightedForwardQuery;
import boomerang.scene.ControlFlowGraph;
import typestate.TransitionFunction;
import typestate.finiteautomata.MatcherTransition;
import typestate.finiteautomata.State;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;

import java.util.Collection;
import java.util.Collections;


public class TypestateFunction extends TypeStateMachineWeightFunctions {

    public TypestateFunction(Collection<LabeledMatcherTransition> transitions) {
        for (MatcherTransition transition : transitions) {
            this.addTransition(transition);
        }
    }

    @Override
    public Collection<WeightedForwardQuery<TransitionFunction>> generateSeed(ControlFlowGraph.Edge stmt) {
        return Collections.emptySet();
    }

    @Override
    protected State initialState() {
        throw new UnsupportedOperationException("This method should never be called.");
    }
}
