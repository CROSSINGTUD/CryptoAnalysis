package crypto.rules;

import java.util.HashSet;
import java.util.Set;

import crypto.interfaces.ICrySLPredicateParameter;

public class CrySLArithmeticConstraint extends CrySLLiteral implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public enum ArithOp { p, n, m}
	/* p = +
	 * n = -
	 * m = % 
	 */
	
	private ArithOp operator;
	private ICrySLPredicateParameter  left;
	private ICrySLPredicateParameter  right;
	
	public CrySLArithmeticConstraint(ICrySLPredicateParameter l, ICrySLPredicateParameter  r, ArithOp op) {
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
	public ICrySLPredicateParameter getLeft() {
		return left;
	}

	/**
	 * @return the right
	 */
	public ICrySLPredicateParameter getRight() {
		return right;
	}

	public String toString() {
		return left + " " + (operator.equals(ArithOp.p) ? "+" : (operator.equals(ArithOp.m) ? "%" : "-")) + " " + right;
	}

	@Override
	public Set<String> getInvolvedVarNames() {
		Set<String> varNames = new HashSet<String>();
		String name = left.getName();
		if(!isIntOrBoolean(name)) {
			varNames.add(name);
		}
		
		name = right.getName();
		if(!isIntOrBoolean(name)) {
			varNames.add(name);
		}
		return varNames;
	}

	private boolean isIntOrBoolean(String name) {
		try {
			Integer.parseInt(name);
			return true;
		} catch(NumberFormatException ex) {
		}
		
		return name.equalsIgnoreCase("false") || name.equalsIgnoreCase("true");
	}

	@Override
	public String getName() {
		return toString();
	}
	
}
