package crypto.typestate;

import boomerang.scene.ControlFlowGraph;
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
                rule, IdealStateMachine.makeStateMachine(rule.getUsagePattern()));
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
