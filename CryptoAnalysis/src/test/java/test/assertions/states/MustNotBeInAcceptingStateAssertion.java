package test.assertions.states;

import boomerang.scope.Statement;
import boomerang.scope.Val;
import java.util.Collection;
import typestate.finiteautomata.State;

public class MustNotBeInAcceptingStateAssertion extends StateResult {

    private boolean unsound;
    private boolean checked;

    public MustNotBeInAcceptingStateAssertion(Statement statement, Val val) {
        super(statement, val);

        this.unsound = false;
        this.checked = false;
    }

    @Override
    public void computedStates(Collection<State> states) {
        // Check if any state is accepting
        for (State state : states) {
            unsound |= state.isAccepting();
        }
        checked = true;
    }

    @Override
    public boolean isUnsound() {
        return !checked || unsound;
    }

    @Override
    public String getErrorMessage() {
        return seed.getVariableName()
                + " @ "
                + statement
                + " @ line "
                + statement.getStartLineNumber()
                + " must not be in accepting state";
    }
}
