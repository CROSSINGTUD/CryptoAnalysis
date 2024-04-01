package crypto.constraints;

import boomerang.scene.InvokeExpr;
import boomerang.scene.Method;
import boomerang.scene.Statement;
import boomerang.scene.Type;
import boomerang.scene.Val;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import crypto.analysis.errors.AbstractError;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractedValue;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CrySLComparisonConstraint;
import crypto.rules.CrySLConstraint;
import crypto.rules.CrySLExceptionConstraint;
import crypto.rules.CrySLPredicate;
import crypto.rules.CrySLValueConstraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public abstract class EvaluableConstraint {

	protected static final Logger LOGGER = LoggerFactory.getLogger(EvaluableConstraint.class);

	public static EvaluableConstraint getInstance(ISLConstraint con, ConstraintSolver context) {
		if (con instanceof CrySLComparisonConstraint) {
			return new ComparisonConstraint((CrySLComparisonConstraint) con, context);
		} else if (con instanceof CrySLValueConstraint) {
			return new ValueConstraint((CrySLValueConstraint) con, context);
		} else if (con instanceof CrySLPredicate) {
			return new PredicateConstraint((CrySLPredicate) con, context);
		} else if (con instanceof CrySLConstraint) {
			return new BinaryConstraint((CrySLConstraint) con, context);
		} else if (con instanceof CrySLExceptionConstraint) {
			return new ExceptionConstraint((CrySLExceptionConstraint) con, context);
		}
		return null;
	}

	final Set<AbstractError> errors = Sets.newHashSet();

	final ConstraintSolver context;

	final ISLConstraint origin;

	protected EvaluableConstraint(ISLConstraint origin, ConstraintSolver context) {
		this.origin = origin;
		this.context = context;
	}

	public abstract void evaluate();

	public boolean hasErrors() {
		return !errors.isEmpty();
	};

	protected Collection<AbstractError> getErrors() {
		return errors;
	}

	protected Map<String, CallSiteWithExtractedValue> extractValueAsString(String varName, ISLConstraint cons) {
		Map<String, CallSiteWithExtractedValue> varVal = Maps.newHashMap();
		for (CallSiteWithParamIndex wrappedCallSite : context.getParsAndVals().keySet()) {
			final Statement callSite = wrappedCallSite.stmt().getStart();

			for (ExtractedValue wrappedAllocSite : context.getParsAndVals().get(wrappedCallSite)) {
				final Statement allocSite = wrappedAllocSite.stmt().getStart();
				if (!wrappedCallSite.getVarName().equals(varName))
					continue;

				InvokeExpr invoker = callSite.getInvokeExpr();
				if (callSite.equals(allocSite)) {
					// TODO no retrieve
					varVal.put(retrieveConstantFromValue(invoker.getArg(wrappedCallSite.getIndex())),
							new CallSiteWithExtractedValue(wrappedCallSite, wrappedAllocSite));
				} else if (allocSite.isAssign()) {
					if (wrappedAllocSite.getValue().isConstant()) {
						String retrieveConstantFromValue = retrieveConstantFromValue(wrappedAllocSite.getValue());
						int pos = -1;

						for (int i = 0; i < invoker.getArgs().size(); i++) {
							Val allocVal = allocSite.getLeftOp();
							Val parameterVal = invoker.getArg(i);

							if (allocVal.equals(parameterVal)) {
								pos = i;
							}
						}
						Type parameterType = invoker.getArg(pos).getType();
						if (pos > -1 && parameterType.isBooleanType()) {
						//if (pos > -1 && "boolean".equals(invoker.getArg(pos).getType()..toQuotedString())) {
							varVal.put("0".equals(retrieveConstantFromValue) ? "false" : "true",
									new CallSiteWithExtractedValue(wrappedCallSite, wrappedAllocSite));
						} else {
							varVal.put(retrieveConstantFromValue,
									new CallSiteWithExtractedValue(wrappedCallSite, wrappedAllocSite));
						}
					} else if (wrappedAllocSite.getValue().isNewExpr()) {
						varVal.putAll(extractSootArray(wrappedCallSite, wrappedAllocSite));
					}
				}
			}
		}
		return varVal;
	}

	/***
	 * Function that finds the values assigned to a soot array.
	 * 
	 * @param callSite   call site at which sootValue is involved
	 * @param allocSite  allocation site at which sootValue is involved
	 * @return extracted array values
	 */
	protected Map<String, CallSiteWithExtractedValue> extractSootArray(CallSiteWithParamIndex callSite,
			ExtractedValue allocSite) {
		Val arrayLocal = allocSite.getValue();
		Method method = allocSite.stmt().getMethod();

		Map<String, CallSiteWithExtractedValue> arrVal = Maps.newHashMap();

		for (Statement statement : method.getStatements()) {
			if (!statement.isAssign()) {
				continue;
			}

			Val leftVal = statement.getLeftOp();
			Val rightVal = statement.getRightOp();

			if (leftVal.equals(arrayLocal) && !rightVal.toString().contains("newarray")) {
				arrVal.put(retrieveConstantFromValue(rightVal), new CallSiteWithExtractedValue(callSite, allocSite));
			}
		}

		/*Body methodBody = allocSite.stmt().getMethod().getActiveBody();

		if (methodBody == null)
			return arrVal;

		Iterator<Unit> unitIterator = methodBody.getUnits().snapshotIterator();
		while (unitIterator.hasNext()) {
			final Unit unit = unitIterator.next();
			if (!(unit instanceof AssignStmt))
				continue;
			AssignStmt uStmt = (AssignStmt) (unit);
			Value leftValue = uStmt.getLeftOp();
			Value rightValue = uStmt.getRightOp();
			if (leftValue.toString().contains(arrayLocal.toString()) && !rightValue.toString().contains("newarray")) {
				arrVal.put(retrieveConstantFromValue(rightValue), new CallSiteWithExtractedValue(callSite, allocSite));
			}
		}*/
		return arrVal;
	}

	private String retrieveConstantFromValue(Val val) {
		if (val.isStringConstant()) {
			return val.getStringValue();
		} else if (val.isIntConstant()) {
			return String.valueOf(val.getIntValue());
		} else if (val.isLongConstant()) {
			return String.valueOf(val.getLongValue()).replaceAll("L", "");
		} else {
			return "";
		}
	}

}
