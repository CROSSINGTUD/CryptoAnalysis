package crypto.extractparameter;

import boomerang.jimple.Statement;
import soot.Value;

public class ExtractedValue {
	private Statement stmt;
	private Value val;

	public ExtractedValue(Statement stmt, Value val) {
		this.stmt = stmt;
		this.val = val;
	}

	public Statement stmt() {
		return stmt;
	}
	
	public Value getValue() {
		return val;
	}
	
	@Override
	public String toString() {
		return "Extracted Value: " + val + " at " +stmt;
	}
}
