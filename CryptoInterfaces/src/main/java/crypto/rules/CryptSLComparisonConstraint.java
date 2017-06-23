package crypto.rules;

import java.util.List;

public class CryptSLComparisonConstraint extends CryptSLLiteral implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum CompOp { l, g, le, ge, eq}
	
	private CompOp operator;
	
	private CryptSLArithmeticConstraint left;
	private CryptSLArithmeticConstraint right;
	
	public CryptSLComparisonConstraint(CryptSLArithmeticConstraint l, CryptSLArithmeticConstraint r, CompOp op) {
		left = l;
		right = r;
		operator = op;
	}
	
	public String toString() {
		return "C:" + left + operator + right;
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
	public CryptSLArithmeticConstraint getLeft() {
		return left;
	}

	
	/**
	 * @return the right
	 */
	public CryptSLArithmeticConstraint getRight() {
		return right;
	}

	@Override
	public List<String> getInvolvedVarNames() {
		List<String> varNames = left.getInvolvedVarNames();
		varNames.addAll(right.getInvolvedVarNames());
		return varNames;
	}
	
}
