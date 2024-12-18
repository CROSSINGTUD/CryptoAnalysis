package test.assertions;

import boomerang.scene.Statement;
import boomerang.scene.Val;
import java.util.Collection;
import test.Assertion;
import typestate.finiteautomata.State;

public class NotInAcceptingStateAssertion implements Assertion, StateResult {

    private final Statement unit;
    private final Collection<Val> val;
    private boolean satisfied;

    public NotInAcceptingStateAssertion(Statement unit, Collection<Val> accessGraph) {
        this.unit = unit;
        this.val = accessGraph;
        this.satisfied = true;
    }

    public Collection<Val> getVal() {
        return val;
    }

    public Statement getStmt() {
        return unit;
    }

    public void computedResults(State s) {
        satisfied &= !s.isAccepting();
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
        return "[" + val + " @ " + unit + " must not be in accepting state]";
    }
}
