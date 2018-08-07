package test.assertions;

import soot.Unit;
import soot.jimple.Stmt;
import test.Assertion;

/**
 * Created by johannesspath on 24.12.17.
 */
public class MissingTypestateChange implements Assertion{
    private final Stmt stmt;
	private int triggered;

    public MissingTypestateChange(Stmt stmt) {
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

	public Unit getStmt() {
		return stmt;
	}
	
	public void trigger(){
		triggered++;
	}
}
