package crypto.rules;

import java.util.List;

public class CrySLComparisonConstraint extends CrySLLiteral {

	public enum CompOp { l, g, le, ge, eq, neq}
	
	private final CompOp operator;
	private final CrySLArithmeticConstraint left;
	private final CrySLArithmeticConstraint right;
	
	public CrySLComparisonConstraint(CrySLArithmeticConstraint l, CrySLArithmeticConstraint r, CompOp op) {
		left = l;
		right = r;
		operator = op;
	}
	
	public String toString() {
		return left + " " + getOperatorString() + " " + right;
	}

	private String getOperatorString() {
		switch (operator) {
			case l:
				return "<";
			case le:
				return "<=";
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
	public List<String> getInvolvedVarNames() {
		List<String> varNames = left.getInvolvedVarNames();
		varNames.addAll(right.getInvolvedVarNames());
		return varNames;
	}

	@Override
	public String getName() {
		return toString();
	}
	
}
