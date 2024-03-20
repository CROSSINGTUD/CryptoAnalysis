package crypto.extractparameter;

import boomerang.scene.ControlFlowGraph;
import boomerang.scene.Val;

public class ExtractedValue {
	private ControlFlowGraph.Edge stmt;
	private Val val;

	public ExtractedValue(ControlFlowGraph.Edge stmt, Val val) {
		this.stmt = stmt;
		this.val = val;
	}

	public ControlFlowGraph.Edge stmt() {
		return stmt;
	}
	
	public Val getValue() {
		return val;
	}
	
	@Override
	public String toString() {
		return "Extracted Value: " + val + " at " +stmt;
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
