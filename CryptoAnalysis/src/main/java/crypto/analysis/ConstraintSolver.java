package crypto.analysis;

import com.google.common.collect.Multimap;

import crypto.rules.CryptSLArithmeticConstraint;
import crypto.rules.CryptSLComparisonConstraint;
import crypto.rules.CryptSLConstraint;
import crypto.rules.CryptSLConstraint.LogOps;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLValueConstraint;
import crypto.typestate.CallSiteWithParamIndex;
import soot.Value;
import typestate.interfaces.ISLConstraint;

public class ConstraintSolver {

	
	public ConstraintSolver(AnalysisSeedWithSpecification analysisSeedWithSpecification) {
	}
	
	public boolean evaluate(CryptSLComparisonConstraint comp, Multimap<CallSiteWithParamIndex, Value> actualValues) {
		int left = evaluate(comp.getLeft(), actualValues);
		int right = evaluate(comp.getRight(), actualValues);
		switch (comp.getOperator()) {
			case eq:
				return left == right;
			case g:
				return left > right;
			case ge:
				return left >= right;
			case l:
				return left < right;
			case le:
				return left <= right;
			default:
				return false;
		}
	}
	
	private int evaluate(CryptSLArithmeticConstraint arith, Multimap<CallSiteWithParamIndex, Value> actualValues) {
		int left = extractValueAsInt(arith.getLeft(), actualValues);
		int right = extractValueAsInt(arith.getRight(), actualValues);
		switch (arith.getOperator()) {
			case n:
				return left - right;
			case p:
				return left + right;
			default:
				return 0;
		}
	}
	
	private int extractValueAsInt(String exp, Multimap<CallSiteWithParamIndex, Value> actualValues) {
		int ret = -1;
		try {
			//1. left may (already) be an integer
			ret = Integer.parseInt(exp);
		} catch (NumberFormatException ex) {
			//2. If not, it's a variable name.
			//Get value of variable left from map
			String valueAsString = extractValueAsString(exp, actualValues);
			// and cast it to 
			try {
			ret = Integer.parseInt(valueAsString);
			} catch (NumberFormatException ex1) {
				//If that does not work either, I'm out of ideas ...
				throw new RuntimeException();
			}
		}

		return ret;
	}

	public boolean evaluate(CryptSLValueConstraint valueCons, Multimap<CallSiteWithParamIndex, Value> actualValues) {
		if (actualValues == null || actualValues.isEmpty()) {
			return false;
		}
		String actualValue = extractValueAsString(valueCons.getVarName(), actualValues);
		
		return valueCons.getValueRange().contains(actualValue);
	}

	private String extractValueAsString(String varName, Multimap<CallSiteWithParamIndex, Value> actualValues) {
		//Magic that retrieves the value of $varName from $actualValues
		//This is most likely wrong.
		for (CallSiteWithParamIndex cs : actualValues.keySet()) {
			if (cs.getVarName().equals(varName)) {
				return actualValues.get(cs).toString();
			}
		}
		return "";
	}

	public boolean evaluate(CryptSLConstraint cons, Multimap<CallSiteWithParamIndex, Value> actualValues) {
		Boolean left = evaluate(cons.getLeft(), actualValues);
		Boolean right = evaluate(cons.getRight(), actualValues);
		LogOps ops = cons.getOperator();
		if (ops.equals(LogOps.and)) {
			return left && right; 
		} else if (ops.equals(LogOps.or)) {
			return left || right;
		} else if (ops.equals(LogOps.implies)) {
			if (!left) {
				return true;
			} else {
				return right;
			}
		} else if (ops.equals(LogOps.eq)) {
			return left.equals(right);
		}
		return false;
	}

	public boolean evaluate(CryptSLPredicate pred, Multimap<CallSiteWithParamIndex, Value> actualValues) {
		//Where do I get the predicates from?
		return false;
	}
	

	public Boolean evaluate(ISLConstraint cons, Multimap<CallSiteWithParamIndex, Value> actualValues) {
//		if (cons instanceof CryptSLComparisonConstraint) {
//			return evaluate((CryptSLComparisonConstraint) cons, actualValues);
//		} else if (cons instanceof CryptSLValueConstraint) {
//			return evaluate((CryptSLValueConstraint)cons, actualValues);
//		} else if (cons instanceof CryptSLPredicate) {
//			return evaluate((CryptSLPredicate)cons, actualValues);
//		} else if (cons instanceof CryptSLConstraint) {
//			return evaluate(cons, actualValues);
//		}
		return false;
	}

	
}
