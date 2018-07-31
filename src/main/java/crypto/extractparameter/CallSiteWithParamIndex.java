package crypto.extractparameter;

import boomerang.jimple.Statement;
import boomerang.jimple.Val;

public class CallSiteWithParamIndex{

	private String varName;
	
	/**
	 * @return the varName
	 */
	public String getVarName() {
		return varName;
	}

	private int index;
	private Val fact;
	private Statement statement;

	public CallSiteWithParamIndex(Statement u, Val fact, int index, String varName) {
		this.fact = fact;
		this.index = index;
		this.varName = varName;
		this.statement = u;
	}

	public int getIndex() {
		return index;
	}
	
	@Override
	public String toString() {
		return varName +" at " +stmt() + " and " +index;
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
			if (other.varName != null)
				return false;
		} else if (!varName.equals(other.varName))
			return false;
		return true;
	}

	public Val fact() {
		return fact;
	}

	public Statement stmt() {
		return statement;
	}

	
	
}
