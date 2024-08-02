package crypto.extractparameter;

import boomerang.scene.Statement;
import boomerang.scene.Val;

public class CallSiteWithParamIndex {

	private final Statement statement;
	private final Val fact;
	private final String varName;
	private final int index;

	public CallSiteWithParamIndex(Statement statement, Val fact, int index, String varName) {
		this.statement = statement;
		this.fact = fact;
		this.index = index;
		this.varName = varName;
	}

	public Statement stmt() {
		return statement;
	}

	public Val fact() {
		return fact;
	}

	public int getIndex() {
		return index;
	}

	public String getVarName() {
		return varName;
	}
	
	@Override
	public String toString() {
		return varName + " at " + stmt() + " and " + index;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		result = prime * result + ((statement == null) ? 0 : statement.hashCode());
		result = prime * result + ((varName == null) ? 0 : varName.hashCode());
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
		CallSiteWithParamIndex other = (CallSiteWithParamIndex) obj;
		if (index != other.index)
			return false;
		if (statement == null) {
			if (other.statement != null)
				return false;
		} else if (!statement.equals(other.statement))
			return false;
		if (varName == null) {
            return other.varName == null;
		} else return varName.equals(other.varName);
    }
	
}
