package test.assertions.states;

import boomerang.scope.Statement;
import boomerang.scope.Val;
import java.util.Collection;

import typestate.finiteautomata.State;

public class StateAssertion extends StateResult {

    private final String stateLabel;
    private boolean satisfied;
    private boolean checked;

    public StateAssertion(Statement statement, Val val, String stateLabel) {
        super(statement, val);

        this.stateLabel = stateLabel;
        this.satisfied = false;
        this.checked = false;
    }

    @Override
    public void computedStates(Collection<State> states) {
        // Check if any label matches the target label
        Collection<String> labels = states.stream().map(Object::toString).toList();
        satisfied |= labels.contains(stateLabel);

        checked = true;
    }

    @Override
    public boolean isUnsound() {
        return !checked || !satisfied;
    }

    @Override
    public String getErrorMessage() {
        if (checked) {
            return seed.getVariableName()
                    + " @ "
                    + statement
                    + " @ line "
                    + statement.getStartLineNumber()
                    + " is expected to be in state "
                    + stateLabel;
        } else {
            return statement + " @ line " + statement.getStartLineNumber() + " has not been checked";
        }
    }
}
