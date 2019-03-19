package crypto.extractparameter;

import java.util.Set;

import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import soot.Value;
import sync.pds.solver.nodes.Node;

public class ExtractedValue {
	private Statement stmt;
	private Value val;
	private Set<Node<Statement, Val>> dataFlowPath;

	public ExtractedValue(Statement stmt, Value val, Set<Node<Statement, Val>> dataFlowPath) {
		this.stmt = stmt;
		this.val = val;
		this.dataFlowPath = dataFlowPath;
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
	
	public Set<Node<Statement, Val>> getDataFlowPath() {
		return dataFlowPath;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((stmt == null) ? 0 : stmt.hashCode());
		result = prime * result + ((val == null) ? 0 : val.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExtractedValue other = (ExtractedValue) obj;
		if (stmt == null) {
			if (other.stmt != null)
				return false;
		} else if (!stmt.equals(other.stmt))
			return false;
		if (val == null) {
			if (other.val != null)
				return false;
		} else if (!val.equals(other.val))
			return false;
		return true;
	}
	
}
