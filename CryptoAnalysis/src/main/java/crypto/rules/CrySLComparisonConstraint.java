package crypto.rules;

import java.util.Set;

public class CrySLComparisonConstraint extends CrySLLiteral implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum CompOp { l, g, le, ge, eq, neq}
	
	private CompOp operator;
	
	private CrySLArithmeticConstraint left;
	private CrySLArithmeticConstraint right;
	
	public CrySLComparisonConstraint(CrySLArithmeticConstraint l, CrySLArithmeticConstraint r, CompOp op) {
		left = l;
		right = r;
		operator = op;
	}
	
	public String toString() {
		return left + " " + getOperatorString() + " " + right;
	}

	/**
	 * @return
	 */
	private String getOperatorString() {
		switch (operator) {
			case l:
				return "<";
			case le:
				return "<";
			case g:
				return ">";
			case ge:
				return ">=";
			case neq:
				return "!=";
			default:
				return "=";
		}
	}
	
	/**
	 * @return the operator
	 */
	public CompOp getOperator() {
		return operator;
	}

	
	/**
	 * @return the left
	 */
	public CrySLArithmeticConstraint getLeft() {
		return left;
	}

	
	/**
	 * @return the right
	 */
	public CrySLArithmeticConstraint getRight() {
		return right;
	}

	@Override
	public Set<String> getInvolvedVarNames() {
		Set<String> varNames = left.getInvolvedVarNames();
		varNames.addAll(right.getInvolvedVarNames());
		return varNames;
	}

	@Override
	public String getName() {
		return toString();
	}
	
}
