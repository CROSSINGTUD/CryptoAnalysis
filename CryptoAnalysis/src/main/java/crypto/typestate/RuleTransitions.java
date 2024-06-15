package crypto.typestate;

import boomerang.scene.ControlFlowGraph;
import crypto.rules.CrySLRule;
import typestate.TransitionFunction;

import java.util.Collection;
import java.util.Optional;

public class RuleTransitions {

    private final CrySLRule rule;
    private final MatcherTransitionCollection transitions;

    private RuleTransitions(CrySLRule rule, MatcherTransitionCollection transitions) {
        this.rule = rule;
        this.transitions = transitions;
    }

    public static RuleTransitions of(CrySLRule rule) {
        if (rule == null) {
            return new RuleTransitions(null, MatcherTransitionCollection.makeOne());
        }
        return new RuleTransitions(rule, MatcherTransitionCollection.makeCollection(rule.getUsagePattern()));
    }

    public CrySLRule getRule() {
        return rule;
    }

    public Collection<LabeledMatcherTransition> getAllTransitions() {
        return transitions.getAllTransitions();
    }

    public TransitionFunction getInitialWeight(ControlFlowGraph.Edge edge) {
        return transitions.getInitialWeight(edge);
    }
}
