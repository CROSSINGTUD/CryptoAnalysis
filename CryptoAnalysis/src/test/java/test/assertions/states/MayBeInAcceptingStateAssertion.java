package test.assertions.states;

import boomerang.scope.Statement;
import boomerang.scope.Val;
import java.util.Collection;
import typestate.finiteautomata.State;

public class MayBeInAcceptingStateAssertion extends StateResult {

    private boolean satisfied;
    private boolean checked;

    public MayBeInAcceptingStateAssertion(Statement statement, Val val) {
        super(statement, val);

        this.satisfied = false;
        this.checked = false;
    }

    @Override
    public void computedStates(Collection<State> states) {
        // Check if any state is accepting
        for (State state : states) {
            satisfied |= state.isAccepting();
        }
        checked = true;
    }

    @Override
    public boolean isUnsound() {
        return !checked || !satisfied;
    }

    @Override
    public String getErrorMessage() {
        return seed.getVariableName()
                + " @ "
                + statement
                + " @ line "
                + statement.getStartLineNumber()
                + " must not be in error state";
    }
}
