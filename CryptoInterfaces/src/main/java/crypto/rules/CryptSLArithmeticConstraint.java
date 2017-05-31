package crypto.rules;

public class CryptSLArithmeticConstraint extends CryptSLLiteral implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

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
