package crypto.rules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	public Set<String> getInvolvedVarNames() {
		Set<String> varNames = new HashSet<String>();
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
