package crypto.DSL;


public class CryptSLComparisonConstraint extends CryptSLLiteral {

	public enum CompOp { l, g, le, ge, eq}
	
	private CompOp operator;
	
	private String left;
	private String right;
	
	public CryptSLComparisonConstraint(String l, String r, CompOp op) {
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
	public String getLeft() {
		return left;
	}

	
	/**
	 * @return the right
	 */
	public String getRight() {
		return right;
	}
	
}
