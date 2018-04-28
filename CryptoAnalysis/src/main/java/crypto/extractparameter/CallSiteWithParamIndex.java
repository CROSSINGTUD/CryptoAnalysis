package crypto.extractparameter;

import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import sync.pds.solver.nodes.Node;

public class CallSiteWithParamIndex extends Node<Statement,Val>{

	private String varName;
	
	/**
	 * @return the varName
	 */
	public String getVarName() {
		return varName;
	}

	private int index;

	public CallSiteWithParamIndex(Statement u, Val fact, int index, String varName) {
		super(u, fact);
		this.index = index;
		this.varName = varName;
	}

	public int getIndex() {
		return index;
	}
	
	@Override
	public String toString() {
		return varName +" has values: " +super.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + index;
		result = prime * result + ((varName == null) ? 0 : varName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CallSiteWithParamIndex other = (CallSiteWithParamIndex) obj;
		if (index != other.index)
			return false;
		if (varName == null) {
			if (other.varName != null)
				return false;
		} else if (!varName.equals(other.varName))
			return false;
		return true;
	}
	
	
}
