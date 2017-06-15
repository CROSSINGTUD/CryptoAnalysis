package crypto.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Multimap;

import crypto.rules.CryptSLArithmeticConstraint;
import crypto.rules.CryptSLComparisonConstraint;
import crypto.rules.CryptSLConstraint;
import crypto.rules.CryptSLConstraint.LogOps;
import crypto.rules.CryptSLObject;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLSplitter;
import crypto.rules.CryptSLValueConstraint;
import typestate.interfaces.ISLConstraint;

public class ConstraintSolver {

	ParentPredicate seed = null;
	List<ISLConstraint> allConstraints;
	List<ISLConstraint> relConstraints;
	Multimap<String, String> parsAndVals;
	
	public ConstraintSolver(ParentPredicate analysisSeedWithSpecification, List<ISLConstraint> constraints, Multimap<String, String> parsAndValues) {
		seed = analysisSeedWithSpecification;
		parsAndVals = parsAndValues;
		allConstraints  = constraints;
		relConstraints = new ArrayList<ISLConstraint>();
		for (ISLConstraint cons : constraints) {
			List<String> involvedVarNames = cons.getInvolvedVarNames();
			involvedVarNames.removeAll(parsAndValues.keySet());
			if (involvedVarNames.isEmpty()) {
				relConstraints.add(cons);
			}
		}
	}
	

	public int evaluateRelConstraints() {
		int fail = 0;
		for (ISLConstraint con : relConstraints) {
			if (!evaluate(con)) {
				System.out.println(con);
				fail++;
			}
		}
		return fail;
	}
	
	
	private boolean evaluate(CryptSLComparisonConstraint comp) {
		int left = evaluate(comp.getLeft());
		int right = evaluate(comp.getRight());
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
	
	private int evaluate(CryptSLArithmeticConstraint arith) {
		int left = extractValueAsInt(arith.getLeft());
		int right = extractValueAsInt(arith.getRight());
		switch (arith.getOperator()) {
			case n:
				return left - right;
			case p:
				return left + right;
			default:
				return 0;
		}
	}
	
	private int extractValueAsInt(String exp) {
		int ret = -1;
		try {
			//1. exp may (already) be an integer
			ret = Integer.parseInt(exp);
		} catch (NumberFormatException ex) {
			//2. If not, it's a variable name.
			//Get value of variable left from map
			String valueAsString = getVerifiedValue(extractValueAsString(exp));
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

	public boolean evaluate(CryptSLValueConstraint valueCons) {
		if (parsAndVals == null || parsAndVals.isEmpty()) {
			return false;
		}
		CryptSLObject var = valueCons.getVar();
		String val = getValFromVar(var); 
		if (val.isEmpty()) {
			return false;
		} else {
			return valueCons.getValueRange().contains(val);	
		}
	}

	private String getValFromVar(CryptSLObject var) {
		String val = getVerifiedValue(extractValueAsString(var.getVarName()));
		CryptSLSplitter splitter = var.getSplitter();
		if (splitter != null) {
			int ind = splitter.getIndex();
			String splitElement = splitter.getSplitter();
			if (ind > 0) {
				String[] splits = val.split(splitElement);
				if (splits.length > ind) {
					return splits[ind];
				} else {
					return "";
				}
			} else {
				return val.split(splitElement)[ind];
			}
		} else {
			return val;
		}
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


	private Collection<String> extractValueAsString(String varName) {
		//Magic that retrieves the value of $varName from $actualValues
		//This is most likely wrong.
		for (String cs : parsAndVals.keySet()) {
			if (cs.equals(varName)) {
				return parsAndVals.get(cs);
			}
		}
		return null;
	}

	public boolean evaluate(CryptSLConstraint cons) {
		boolean left = evaluate(cons.getLeft());
		
		LogOps ops = cons.getOperator();
		if (ops.equals(LogOps.implies)) {
			if (!left) {
				return true;
			} else {
				return evaluate(cons.getRight());
			}
		}
		
		boolean right = evaluate(cons.getRight());
		if (ops.equals(LogOps.and)) {
			return left && right; 
		} else if (ops.equals(LogOps.or)) {
			return left || right;
		} else if (ops.equals(LogOps.eq)) {
			return left == right;
		}
		return false;
	}

	public boolean evaluate(CryptSLPredicate pred) {
		boolean requiredPredicatesExist = true;
		for (EnsuredCryptSLPredicate enspred :  seed.getEnsuredPredicates()) {
			CryptSLPredicate ensuredPredicate = enspred.getPredicate();
			if (ensuredPredicate.equals(pred)) {
				for (int i = 0; i < pred.getParameters().size(); i++) {
					if (pred.getInvolvedVarNames().contains(pred.getParameters().get(i).getName())) {
						
						Collection<String> actVals = enspred.getParametersToValues().get(enspred.getPredicate().getParameters().get(i).getName());
						Collection<String> expVals = parsAndVals.get(pred.getParameters().get(i).getName());
						
						String splitter = "";
						int index = -1;
						if (pred.getParameters().get(i) instanceof CryptSLObject) {
							CryptSLObject obj = (CryptSLObject) pred.getParameters().get(i);
							if (obj.getSplitter() != null) {
								splitter = obj.getSplitter().getSplitter();
								index = obj.getSplitter().getIndex();
							}
						}
						
						for (String foundVal : expVals) {
							foundVal = foundVal.split(splitter)[index];
							requiredPredicatesExist &= actVals.contains(foundVal);
						}
					}
				}
			}
		}
		return requiredPredicatesExist;
	}
	

	public Boolean evaluate(ISLConstraint cons) {
		if (cons instanceof CryptSLComparisonConstraint) {
			return evaluate((CryptSLComparisonConstraint) cons);
		} else if (cons instanceof CryptSLValueConstraint) {
			return evaluate((CryptSLValueConstraint)cons);
		} else if (cons instanceof CryptSLPredicate) {
			return evaluate((CryptSLPredicate)cons);
		} else if (cons instanceof CryptSLConstraint) {
			return evaluate((CryptSLConstraint) cons);
		}
		return false;
	}

	/**
	 * @return the allConstraints
	 */
	public List<ISLConstraint> getAllConstraints() {
		return allConstraints;
	}
	
	
	/**
	 * @return the relConstraints
	 */
	public List<ISLConstraint> getRelConstraints() {
		return relConstraints;
	}
}