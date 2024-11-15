package crypto.typestate;

import boomerang.WeightedForwardQuery;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.Val;
import crypto.rules.CrySLRule;
import java.util.Collection;
import typestate.TransitionFunction;

public class ForwardSeedQuery extends WeightedForwardQuery<TransitionFunction> {

    private final RuleTransitions transitions;

    private ForwardSeedQuery(
            ControlFlowGraph.Edge stmt,
            Val fact,
            TransitionFunction weight,
            RuleTransitions transitions) {
        super(stmt, fact, weight);

        this.transitions = transitions;
    }

    public static ForwardSeedQuery makeQueryWithSpecification(
            ControlFlowGraph.Edge stmt, Val fact, RuleTransitions transitions) {
        return new ForwardSeedQuery(stmt, fact, transitions.getInitialWeight(stmt), transitions);
    }

    public static ForwardSeedQuery makeQueryWithoutSpecification(
            ControlFlowGraph.Edge stmt, Val fact) {
        return new ForwardSeedQuery(stmt, fact, TransitionFunction.one(), RuleTransitions.of(null));
    }

    public boolean hasSpecification() {
        return transitions.getRule() != null;
    }

    public CrySLRule getRule() {
        return transitions.getRule();
    }

    public Collection<LabeledMatcherTransition> getAllTransitions() {
        return transitions.getAllTransitions();
    }
}
