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

import boomerang.scope.ControlFlowGraph;
import crysl.rule.CrySLRule;
import java.util.Collection;
import typestate.TransitionFunction;

public class RuleTransitions {

    private final CrySLRule rule;
    private final IdealStateMachine stateMachine;

    private RuleTransitions(CrySLRule rule, IdealStateMachine transitions) {
        this.rule = rule;
        this.stateMachine = transitions;
    }

    public static RuleTransitions of(CrySLRule rule) {
        if (rule == null) {
            return new RuleTransitions(null, IdealStateMachine.makeOne());
        }

        return new RuleTransitions(
                rule, IdealStateMachine.makeStateMachine(rule.getEvents(), rule.getUsagePattern()));
    }

    public CrySLRule getRule() {
        return rule;
    }

    public Collection<LabeledMatcherTransition> getStateMachineTransitions() {
        return stateMachine.getAllTransitions();
    }

    public TransitionFunction getInitialWeight(ControlFlowGraph.Edge edge) {
        return stateMachine.getInitialWeight(edge);
    }
}
