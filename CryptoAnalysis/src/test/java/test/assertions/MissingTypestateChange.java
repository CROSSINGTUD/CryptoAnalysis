package test.assertions;

import boomerang.scene.Statement;
import test.Assertion;

/** Created by johannesspath on 24.12.17. */
public class MissingTypestateChange implements Assertion {
    private final Statement stmt;
    private int triggered;

    public MissingTypestateChange(Statement stmt) {
        this.stmt = stmt;
    }

    @Override
    public boolean isSatisfied() {
        return triggered > 0;
    }

    @Override
    public boolean isImprecise() {
        return triggered != 1;
    }

    @Override
    public String toString() {
        return "Expected a missing typestate change @ " + stmt;
    }

    public Statement getStmt() {
        return stmt;
    }

    public void trigger() {
        triggered++;
    }
}
