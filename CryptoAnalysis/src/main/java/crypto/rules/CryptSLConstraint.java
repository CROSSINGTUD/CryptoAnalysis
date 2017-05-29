package crypto.rules;

import typestate.interfaces.ISLConstraint;

public class CryptSLConstraint implements ISLConstraint{
	
	public enum LogOps { and , or , implies , eq}
	
	private LogOps operator;
	private ISLConstraint left;
	private ISLConstraint right;

	public CryptSLConstraint(ISLConstraint l, ISLConstraint r, LogOps op) {
		left = l;
		right = r;
		operator = op;
	}
	
	/**
	 * @return the operator
	return operator;
	 */
	public LogOps getOperator() {
		return operator;
	}

	/**
	 * @return the left
	 */
	public ISLConstraint getLeft() {
		return left;
	}
	
	/**
	 * @return the right
	 */
	public ISLConstraint getRight() {
		return right;
	}


	public String toString() {
		StringBuilder constraintSB = new StringBuilder();
		constraintSB.append(left.toString());
		constraintSB.append(operator);
		constraintSB.append(right.toString());
		return constraintSB.toString();
	}
	
	

}
