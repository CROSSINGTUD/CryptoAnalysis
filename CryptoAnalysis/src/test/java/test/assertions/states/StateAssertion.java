package test.assertions.states;

import boomerang.scene.Statement;
import boomerang.scene.Val;
import java.util.Collection;
import test.Assertion;
import typestate.finiteautomata.State;

public class StateAssertion implements Assertion, StateResult {

    private final Statement statement;
    private final Val val;
    private final String stateLabel;
    private boolean satisfied;

    public StateAssertion(Statement statement, Val val, String stateLabel) {
        this.statement = statement;
        this.val = val;
        this.stateLabel = stateLabel;
        this.satisfied = false;
    }

    @Override
    public Val getVal() {
        return val;
    }

    @Override
    public Statement getStmt() {
        return statement;
    }

    @Override
    public void computedStatesAtStatement(Collection<State> states) {
        Collection<String> labels = states.stream().map(Object::toString).toList();

        satisfied |= labels.contains(stateLabel);
    }

    @Override
    public boolean isSatisfied() {
        return satisfied;
    }

    @Override
    public boolean isImprecise() {
        return false;
    }

    @Override
    public String toString() {
        return val.getVariableName()
                + " @ "
                + statement
                + " @ line "
                + statement.getStartLineNumber()
                + " is expected to be in state "
                + stateLabel;
    }
}
