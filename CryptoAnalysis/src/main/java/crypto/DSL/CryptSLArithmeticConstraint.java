package crypto.DSL;

public class CryptSLArithmeticConstraint extends CryptSLLiteral {

	public enum ArithOp { p, n}
	
	private ArithOp operator;
	private String left;
	private String right;
	
	public CryptSLArithmeticConstraint(String l, String r, ArithOp op) {
		left = l;
		right = r;
		operator = op;
	}
	
	
	public String toString() {
		return left + " " + operator + " " + right;
	}

}
