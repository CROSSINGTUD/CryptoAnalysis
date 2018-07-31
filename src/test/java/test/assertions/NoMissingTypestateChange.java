package test.assertions;

import soot.Unit;
import soot.jimple.Stmt;
import test.Assertion;

/**
 * Created by johannesspath on 24.12.17.
 */
public class NoMissingTypestateChange implements Assertion{
    private final Stmt stmt;

    public NoMissingTypestateChange(Stmt stmt) {
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

	public Unit getStmt() {
		return stmt;
	}
}
