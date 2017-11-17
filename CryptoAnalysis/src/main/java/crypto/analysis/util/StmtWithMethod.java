package crypto.analysis.util;

import soot.SootMethod;
import soot.Unit;

public class StmtWithMethod {
	private Unit stmt;
	private SootMethod method;

	public StmtWithMethod(Unit stmt, SootMethod method) {
		this.stmt = stmt;
		this.method = method;
	}

	@Override
	public String toString() {
		return stmt +"€" + method;
	}

	public Unit getStmt() {
		return stmt;
	}

	public SootMethod getMethod() {
		return method;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((stmt == null) ? 0 : stmt.hashCode());
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
		StmtWithMethod other = (StmtWithMethod) obj;
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		if (stmt == null) {
			if (other.stmt != null)
				return false;
		} else if (!stmt.equals(other.stmt))
			return false;
		return true;
	}
}
