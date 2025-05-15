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
import boomerang.scope.AllocVal;
import boomerang.scope.ControlFlowGraph;
import crysl.rule.CrySLRule;
import java.util.Collection;
import typestate.TransitionFunction;
import typestate.TransitionFunctionOne;

public class ForwardSeedQuery extends WeightedForwardQuery<TransitionFunction> {

    private final RuleTransitions transitions;

    private ForwardSeedQuery(
            ControlFlowGraph.Edge stmt,
            AllocVal fact,
            TransitionFunction weight,
            RuleTransitions transitions) {
        super(stmt, fact, weight);

        this.transitions = transitions;
    }

    public static ForwardSeedQuery makeQueryWithSpecification(
            ControlFlowGraph.Edge stmt, AllocVal fact, RuleTransitions transitions) {
        return new ForwardSeedQuery(stmt, fact, transitions.getInitialWeight(stmt), transitions);
    }

    public static ForwardSeedQuery makeQueryWithoutSpecification(
            ControlFlowGraph.Edge stmt, AllocVal fact) {
        return new ForwardSeedQuery(
                stmt, fact, TransitionFunctionOne.one(), RuleTransitions.of(null));
    }

    public boolean hasSpecification() {
        return transitions.getRule() != null;
    }

    public CrySLRule getRule() {
        return transitions.getRule();
    }

    public Collection<LabeledMatcherTransition> getAllTransitions() {
        return transitions.getStateMachineTransitions();
    }
}
