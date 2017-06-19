package crypto.rules;

import java.util.ArrayList;
import java.util.List;

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
	
	/**
	 * @return the operator
	 */
	public ArithOp getOperator() {
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

	public String toString() {
		return left + " " + operator + " " + right;
	}

	@Override
	public List<String> getInvolvedVarNames() {
		List<String> varNames = new ArrayList<String>();
		try {
			Integer.parseInt(left);
		} catch (NumberFormatException ex) {
			varNames.add(left);
		}
		
		try {
			Integer.parseInt(right);
		} catch (NumberFormatException ex) {
			varNames.add(right);
		}
		
		return varNames;
	}
	


}
