package crypto.rules;

import java.util.HashSet;
import java.util.Set;

import crypto.interfaces.ICryptSLPredicateParameter;

public class CryptSLArithmeticConstraint extends CryptSLLiteral implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public enum ArithOp { p, n}
	
	private ArithOp operator;
	private ICryptSLPredicateParameter  left;
	private ICryptSLPredicateParameter  right;
	
	public CryptSLArithmeticConstraint(ICryptSLPredicateParameter l, ICryptSLPredicateParameter  r, ArithOp op) {
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
	public ICryptSLPredicateParameter getLeft() {
		return left;
	}

	/**
	 * @return the right
	 */
	public ICryptSLPredicateParameter getRight() {
		return right;
	}

	public String toString() {
		return left + " " + (operator.equals(ArithOp.p) ? "+" : "-") + " " + right;
	}

	@Override
	public Set<String> getInvolvedVarNames() {
		Set<String> varNames = new HashSet<String>();
		String name = left.getName();
		try {
			Integer.parseInt(name);
		} catch(NumberFormatException ex) {
			varNames.add(name);
		}
		
		name = right.getName();
		try {
			Integer.parseInt(name);
		} catch(NumberFormatException ex) {
			varNames.add(name);
		}
		return varNames;
	}

	@Override
	public String getName() {
		return toString();
	}
	


}
