package crypto.extractparameter;

import boomerang.scene.Statement;
import boomerang.scene.Val;

public class ExtractedValue {

	private final Statement stmt;
	private final Val val;

	public ExtractedValue(Statement stmt, Val val) {
		this.stmt = stmt;
		this.val = val;
	}

	public Statement stmt() {
		return stmt;
	}
	
	public Val getValue() {
		return val;
	}
	
	@Override
	public String toString() {
		return "Extracted Value: " + val + " at " + stmt;
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
            return other.val == null;
		} else return val.equals(other.val);
    }
	
}
