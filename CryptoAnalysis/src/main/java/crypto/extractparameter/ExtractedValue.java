package crypto.extractparameter;

import boomerang.scene.Statement;
import boomerang.scene.Val;

import java.util.Arrays;

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
		return Arrays.hashCode(new Object[]{
				val,
				stmt
		});
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;

		ExtractedValue other = (ExtractedValue) obj;
		if (stmt == null) {
			if (other.stmt() != null) return false;
		} else if (!stmt.equals(other.stmt())) {
			return false;
		}

		if (val == null) {
            if (other.getValue() != null) return false;
		} else if (!val.equals(other.val)) {
			return false;
		}

		return true;
    }
	
}
