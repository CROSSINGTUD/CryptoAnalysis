package test.assertions.states;

import boomerang.scene.Statement;
import boomerang.scene.Val;
import java.util.Collection;
import test.Assertion;
import typestate.finiteautomata.State;

public class MustBeInAcceptingStateAssertion implements Assertion, StateResult {

    private final Statement statement;
    private final Val val;
    private boolean satisfied;

    public MustBeInAcceptingStateAssertion(Statement statement, Val val) {
        this.statement = statement;
        this.val = val;
        this.satisfied = true;
    }

    public Val getVal() {
        return val;
    }

    public Statement getStmt() {
        return statement;
    }

    public void computedStatesAtStatement(Collection<State> states) {
        for (State state : states) {
            satisfied &= state.isAccepting();
        }
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
                + " must not be in error state";
    }
}
