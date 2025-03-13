package crypto.typestate;

import boomerang.WeightedForwardQuery;
import boomerang.scope.AllocVal;
import boomerang.scope.ControlFlowGraph;
import crysl.rule.CrySLRule;
import java.util.Collection;
import typestate.TransitionFunction;

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
        return new ForwardSeedQuery(stmt, fact, TransitionFunction.one(), RuleTransitions.of(null));
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
