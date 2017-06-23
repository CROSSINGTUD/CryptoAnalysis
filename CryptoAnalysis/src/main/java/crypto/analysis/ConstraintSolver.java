package crypto.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.Multimap;

import crypto.rules.CryptSLArithmeticConstraint;
import crypto.rules.CryptSLComparisonConstraint;
import crypto.rules.CryptSLConstraint;
import crypto.rules.CryptSLConstraint.LogOps;
import crypto.rules.CryptSLObject;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLRule;
import crypto.rules.CryptSLSplitter;
import crypto.rules.CryptSLValueConstraint;
import typestate.interfaces.ICryptSLPredicateParameter;
import typestate.interfaces.ISLConstraint;

public class ConstraintSolver {

	private final ParentPredicate seed;
	private final List<ISLConstraint> allConstraints;
	private final List<ISLConstraint> relConstraints;
	private final Multimap<String, String> parsAndVals;
	private List<Entry<String, String>> objects;
	private final static List<String> trackedTypes = Arrays.asList("java.lang.String", "int", "java.lang.Integer");
	private final static List<String> predefinedPreds = Arrays.asList("callTo", "noCallTo", "neverTypeOf");

	public ConstraintSolver(ParentPredicate analysisSeedWithSpecification, CryptSLRule rule, Multimap<String, String> parsAndValues) {
		seed = analysisSeedWithSpecification;
		parsAndVals = parsAndValues;
		allConstraints = rule.getConstraints();
		objects = rule.getObjects();
		relConstraints = new ArrayList<ISLConstraint>();
		for (ISLConstraint cons : allConstraints) {
			List<String> involvedVarNames = cons.getInvolvedVarNames();
			involvedVarNames.removeAll(parsAndValues.keySet());

			if (involvedVarNames.isEmpty()) {
				relConstraints.add(cons);
			}
		}
	}

	private boolean noNonTrackedTypes(List<String> involvedVarNames) {
		for (String varName : involvedVarNames) {
			if (!isOfNonTrackableType(varName)) {
				return false;
			}
		}
		return true;
	}

	private boolean isOfNonTrackableType(String varName) {
		for (Entry<String, String> object : objects) {
			if (object.getValue().equals(varName) && trackedTypes.contains(object.getKey())) {
				return false;
			}
		}
		return true;
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

	private boolean evaluate(CryptSLValueConstraint valueCons) {
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

	private boolean evaluate(CryptSLConstraint cons) {
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

	private boolean evaluate(CryptSLPredicate pred) {
		String predName = pred.getPredName();
		if (predefinedPreds.contains(predName)) {
			return handlePredefinedNames(pred);
		} 
		if(seed == null){
			return pred.isNegated();
		}

		boolean neverFound = true;
		boolean requiredPredicatesExist = !seed.getEnsuredPredicates().isEmpty();
		for (EnsuredCryptSLPredicate enspred : seed.getEnsuredPredicates()) {
			CryptSLPredicate ensuredPredicate = enspred.getPredicate();
			if (ensuredPredicate.equals(pred)) {
				neverFound = false;
				for (int i = 0; i < pred.getParameters().size(); i++) {
					String var = pred.getParameters().get(i).getName();
					if (isOfNonTrackableType(var)) {
						requiredPredicatesExist &= true;
					} else if (pred.getInvolvedVarNames().contains(var)) {

						Collection<String> actVals = enspred.getParametersToValues().get(enspred.getPredicate().getParameters().get(i).getName());
						Collection<String> expVals = parsAndVals.get(var);

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
							if (index > -1) {
								foundVal = foundVal.split(splitter)[index];
							}
							requiredPredicatesExist &= actVals.contains(foundVal);
						}
					} else {
						requiredPredicatesExist &= false;
					}
				}
			}
		}
		requiredPredicatesExist &= !neverFound;
		return pred.isNegated() != requiredPredicatesExist;
	}

	private boolean handlePredefinedNames(CryptSLPredicate pred) {
		List<ICryptSLPredicateParameter> parameters = pred.getParameters();
		switch (pred.getPredName()) {
			case "callTo":
				List<ICryptSLPredicateParameter> predMethods = parameters;
				for (ICryptSLPredicateParameter predMethod : predMethods) {
					//check whether predMethod is in foundMethods, which type-state analysis has to figure out
					//((CryptSLMethod) predMethod);
				}
				return true;
			case "noCallTo":
				List<ICryptSLPredicateParameter> predForbiddenMethods = parameters;
				for (ICryptSLPredicateParameter predForbMethod : predForbiddenMethods) {
					//check whether predForbMethod is in foundForbMethods, which forbidden-methods analysis has to figure out
					//((CryptSLMethod) predForbMethod);
				}
				return true;
			case "neverTypeOf":
				//pred looks as follows: neverTypeOf($varName, $type)
				// -> first parameter is always the variable
				// -> second parameter is always the type
				String varName = ((CryptSLObject) parameters.get(0)).getVarName();
				//String type = parameters.get(1);
				return true;
			default:
				return true; //should be changed to false once implementation for the other cases works.
		}
	}

	private Boolean evaluate(ISLConstraint cons) {
		if (cons instanceof CryptSLComparisonConstraint) {
			return evaluate((CryptSLComparisonConstraint) cons);
		} else if (cons instanceof CryptSLValueConstraint) {
			return evaluate((CryptSLValueConstraint) cons);
		} else if (cons instanceof CryptSLPredicate) {
			return evaluate((CryptSLPredicate) cons);
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