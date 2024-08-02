package test.assertions;

import boomerang.scene.Statement;
import test.Assertion;

/**
 * Created by johannesspath on 24.12.17.
 */
public class NoMissingTypestateChange implements Assertion {

    private final Statement stmt;

    public NoMissingTypestateChange(Statement stmt) {
        this.stmt = stmt;
    }

    @Override
    public boolean isSatisfied() {
        return true;
    }

    @Override
    public boolean isImprecise() {
        return false;
    }

    @Override
    public String toString() {
        return "Expected a missing typestate change @ " + stmt;
    }

	public Statement getStmt() {
		return stmt;
	}
}
