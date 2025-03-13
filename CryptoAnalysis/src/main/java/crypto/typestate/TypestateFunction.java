/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.typestate;

import boomerang.WeightedForwardQuery;
import boomerang.scope.ControlFlowGraph;
import java.util.Collection;
import java.util.Collections;
import typestate.TransitionFunction;
import typestate.finiteautomata.MatcherTransition;
import typestate.finiteautomata.State;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;

public class TypestateFunction extends TypeStateMachineWeightFunctions {

    public TypestateFunction(Collection<LabeledMatcherTransition> transitions) {
        for (MatcherTransition transition : transitions) {
            this.addTransition(transition);
        }
    }

    @Override
    public Collection<WeightedForwardQuery<TransitionFunction>> generateSeed(
            ControlFlowGraph.Edge stmt) {
        return Collections.emptySet();
    }

    @Override
    protected State initialState() {
        throw new UnsupportedOperationException("This method should never be called.");
    }
}
