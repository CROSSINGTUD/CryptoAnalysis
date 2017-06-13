package crypto.analysis;

import java.util.Collection;
import java.util.Iterator;

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

	ParentPredicate seed = null;
	
	
	public ConstraintSolver(ParentPredicate analysisSeedWithSpecification) {
		seed = analysisSeedWithSpecification;
	}
	
	
	public boolean evaluate(CryptSLComparisonConstraint comp, Multimap<String, String> actualValues) {
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
	
	private int evaluate(CryptSLArithmeticConstraint arith, Multimap<String, String> actualValues) {
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
	
	private int extractValueAsInt(String exp, Multimap<String, String> actualValues) {
		int ret = -1;
		try {
			//1. exp may (already) be an integer
			ret = Integer.parseInt(exp);
		} catch (NumberFormatException ex) {
			//2. If not, it's a variable name.
			//Get value of variable left from map
			String valueAsString = getVerifiedValue(extractValueAsString(exp, actualValues));
			// and cast it to Integer
			try {
			ret = Integer.parseInt(valueAsString);
			} catch (NumberFormatException ex1) {
				//If that does not work either, I'm out of ideas ...
				throw new RuntimeException();
			}
		}

		return ret;
	}

	public boolean evaluate(CryptSLValueConstraint valueCons, Multimap<String, String> actualValues) {
		if (actualValues == null || actualValues.isEmpty()) {
			return false;
		}
		String val = getVerifiedValue(extractValueAsString(valueCons.getVarName(), actualValues));
		return valueCons.getValueRange().contains(val);	
		
	}

	private String getVerifiedValue(Collection<String> actualValue) {
		Iterator<String> valueIterator = actualValue.iterator();
		String val = null;
		val = valueIterator.next();
		if (actualValue.size() > 1) {
			while (valueIterator.hasNext()) {
				if (!val.equals(valueIterator.next())) {
					return "";
				}
			}
		}
		return val;
	}


	private Collection<String> extractValueAsString(String varName, Multimap<String, String> actualValues) {
		//Magic that retrieves the value of $varName from $actualValues
		//This is most likely wrong.
		for (String cs : actualValues.keySet()) {
			if (cs.equals(varName)) {
				return actualValues.get(cs);
			}
		}
		return null;
	}

	public boolean evaluate(CryptSLConstraint cons, Multimap<String, String> actualValues) {
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

	public boolean evaluate(CryptSLPredicate pred, Multimap<String, String> actualValues) {
		for (EnsuredCryptSLPredicate enspred :  seed.getEnsuredPredicates()) {
			CryptSLPredicate ensuredPredicate = enspred.getPredicate();
			if (ensuredPredicate.equals(pred)) {
				for (String predParameter: pred.getParameters()) {
					String val = getVerifiedValue(extractValueAsString(predParameter, actualValues));
					if (enspred.getParametersToValues().get(predParameter).contains(val)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	

	public Boolean evaluate(ISLConstraint cons, Multimap<String, String> actualValues) {
		if (cons instanceof CryptSLComparisonConstraint) {
			return evaluate((CryptSLComparisonConstraint) cons, actualValues);
		} else if (cons instanceof CryptSLValueConstraint) {
			return evaluate((CryptSLValueConstraint)cons, actualValues);
		} else if (cons instanceof CryptSLPredicate) {
			return evaluate((CryptSLPredicate)cons, actualValues);
		} else if (cons instanceof CryptSLConstraint) {
			return evaluate((CryptSLConstraint) cons, actualValues);
		}
		return false;
	}

}