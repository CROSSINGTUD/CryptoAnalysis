package crypto.constraints;

import java.util.HashMap;
import java.util.Map;

import boomerang.scene.Val;
import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.extractparameter.ExtractedValue;
import crypto.rules.ICrySLPredicateParameter;
import crypto.rules.ISLConstraint;
import crypto.rules.CrySLArithmeticConstraint;
import crypto.rules.CrySLComparisonConstraint;
import crypto.rules.CrySLPredicate;

public class ComparisonConstraint extends EvaluableConstraint {

	protected ComparisonConstraint(ISLConstraint origin, ConstraintSolver context) {
		super(origin, context);
	}

	@Override
	public void evaluate() {
		CrySLComparisonConstraint compConstraint = (CrySLComparisonConstraint) origin;

		Map<Integer, CallSiteWithExtractedValue> left = evaluate(compConstraint.getLeft());
		Map<Integer, CallSiteWithExtractedValue> right = evaluate(compConstraint.getRight());

		for (Map.Entry<Integer, CallSiteWithExtractedValue> entry : right.entrySet()) {
			if (entry.getKey() == Integer.MIN_VALUE) {
				ConstraintError error = new ConstraintError(context.getSeed(), entry.getValue(), context.getSpecification(), compConstraint);
				errors.add(error);

				return;
			}
		}

		for (Map.Entry<Integer, CallSiteWithExtractedValue> leftie : left.entrySet()) {
			if (leftie.getKey() == Integer.MIN_VALUE) {
				ConstraintError error = new ConstraintError(context.getSeed(), leftie.getValue(), context.getSpecification(), compConstraint);
				errors.add(error);

				return;
			}
			for (Map.Entry<Integer, CallSiteWithExtractedValue> rightie : right.entrySet()) {

				boolean cons;
				switch (compConstraint.getOperator()) {
					case eq:
						cons = leftie.getKey().equals(rightie.getKey());
						break;
					case g:
						cons = leftie.getKey() > rightie.getKey();
						break;
					case ge:
						cons = leftie.getKey() >= rightie.getKey();
						break;
					case l:
						cons = leftie.getKey() < rightie.getKey();
						break;
					case le:
						cons = leftie.getKey() <= rightie.getKey();
						break;
					case neq:
						cons = leftie.getKey() != rightie.getKey();
						break;
					default:
						cons = false;
				}
				if (!cons) {
					ConstraintError error = new ConstraintError(context.getSeed(), leftie.getValue(), context.getSpecification(), origin);
					errors.add(error);

					return;
				}
			}
		}
	}

	private Map<Integer, CallSiteWithExtractedValue> evaluate(CrySLArithmeticConstraint arith) {
		Map<Integer, CallSiteWithExtractedValue> left = extractValueAsInt(arith.getLeft(), arith);
		Map<Integer, CallSiteWithExtractedValue> right = extractValueAsInt(arith.getRight(), arith);
		for (Map.Entry<Integer, CallSiteWithExtractedValue> rightie : right.entrySet()) {
			if (rightie.getKey() == Integer.MIN_VALUE) {
				return left;
			}
		}

		Map<Integer, CallSiteWithExtractedValue> results = new HashMap<>();
		for (Map.Entry<Integer, CallSiteWithExtractedValue> leftie : left.entrySet()) {
			if (leftie.getKey() == Integer.MIN_VALUE) {
				return left;
			}

			for (Map.Entry<Integer, CallSiteWithExtractedValue> rightie : right.entrySet()) {
				int sum;
				switch (arith.getOperator()) {
					case n:
						sum = leftie.getKey() - rightie.getKey();
						break;
					case p:
						sum = leftie.getKey() + rightie.getKey();
						break;
					case m:
						sum = leftie.getKey() % rightie.getKey();
						break;
					default:
						sum = 0;
				}
				if (rightie.getValue() != null) {
					results.put(sum, rightie.getValue());
				} else {
					results.put(sum, leftie.getValue());
				}
			}
		}
		return results;
	}

	private Map<Integer, CallSiteWithExtractedValue> extractValueAsInt(ICrySLPredicateParameter par,
                                                                        CrySLArithmeticConstraint arith) {
		if (par instanceof CrySLPredicate) {
			PredicateConstraint predicateConstraint = new PredicateConstraint((CrySLPredicate) par, context);
			predicateConstraint.evaluate();
			if (!predicateConstraint.getErrors().isEmpty()) {
				for (AbstractError err : predicateConstraint.getErrors()) {
					errors.add(new ImpreciseValueExtractionError(context.getSeed(), err.getErrorStatement(), err.getRule(), arith));
				}
				predicateConstraint.errors.clear();
			}
			return new HashMap<>();
		} else {
			return extractValueAsInt(par.getName(), arith);
		}
	}

	private Map<Integer, CallSiteWithExtractedValue> extractValueAsInt(String exp, ISLConstraint cons) {
		final HashMap<Integer, CallSiteWithExtractedValue> valuesInt = new HashMap<>();
		// 0. exp may be true or false
		if (exp.equalsIgnoreCase("true")) {
			valuesInt.put(1, null);
			return valuesInt;
		}
		if (exp.equalsIgnoreCase("false")) {
			valuesInt.put(0, null);
			return valuesInt;
		}
		try {
			// 1. exp may (already) be an integer
			valuesInt.put(Integer.parseInt(exp), null);
			return valuesInt;
		} catch (NumberFormatException ex) {
			// 2. If not, it's a variable name.
			// Get value of variable left from map
			Map<String, CallSiteWithExtractedValue> valueCollection = extractValueAsString(exp);
			if (couldNotExtractValues(valueCollection, cons)) {
				return valuesInt;
			}

			try {
				for (Map.Entry<String, CallSiteWithExtractedValue> value : valueCollection.entrySet()) {
					ExtractedValue extractedValue = value.getValue().getExtractedValue();
					if (extractedValue.getVal().equals(Val.zero())) {
						continue;
					}

					if (value.getKey().equals("true"))
						valuesInt.put(1, value.getValue());
					else if (value.getKey().equals("false"))
						valuesInt.put(0, value.getValue());
					else
						valuesInt.put(Integer.parseInt(value.getKey()), value.getValue());
				}
			} catch (NumberFormatException ex1) {
				// If that does not work either, I'm out of ideas ...
				LOGGER.error("An exception occured when extracting value as Integer.", ex1);
			}
			return valuesInt;
		}
	}

}
