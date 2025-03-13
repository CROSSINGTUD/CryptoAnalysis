package test.assertions.states;

import boomerang.scope.Statement;
import boomerang.scope.Val;
import java.util.Collection;

import typestate.finiteautomata.State;

public class MustBeInAcceptingStateAssertion extends StateResult {

    private boolean unsound;
    private boolean checked;

    public MustBeInAcceptingStateAssertion(Statement statement, Val seed) {
        super(statement, seed);

        this.unsound = false;
        this.checked = false;
    }

    @Override
    public void computedStates(Collection<State> states) {
        // Check if any state is not accepting
        for (State state : states) {
            unsound |= !state.isAccepting();
        }
        checked = true;
    }

    @Override
    public boolean isUnsound() {
        return !checked || unsound;
    }

    @Override
    public String getErrorMessage() {
        if (checked) {
            return seed.getVariableName()
                    + " must be in an accepting state @ "
                    + statement
                    + " @ line "
                    + statement.getStartLineNumber();
        } else {
            return statement + " @ line " + statement.getStartLineNumber() + " has not been checked";
        }
    }
}
