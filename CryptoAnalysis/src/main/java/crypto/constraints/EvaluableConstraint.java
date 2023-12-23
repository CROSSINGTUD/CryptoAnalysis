package crypto.constraints;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import soot.Body;
import soot.IntType;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.LongConstant;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.internal.JNewArrayExpr;

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
			final Stmt callSite = wrappedCallSite.stmt().getUnit().get();

			for (ExtractedValue wrappedAllocSite : context.getParsAndVals().get(wrappedCallSite)) {
				final Stmt allocSite = wrappedAllocSite.stmt().getUnit().get();
				if (!wrappedCallSite.getVarName().equals(varName))
					continue;

				InvokeExpr invoker = callSite.getInvokeExpr();
				if (callSite.equals(allocSite)) {
					varVal.put(retrieveConstantFromValue(invoker.getArg(wrappedCallSite.getIndex())),
							new CallSiteWithExtractedValue(wrappedCallSite, wrappedAllocSite));
				} else if (allocSite instanceof AssignStmt) {
					if (wrappedAllocSite.getValue() instanceof Constant) {
						// varVal.put(retrieveConstantFromValue(wrappedAllocSite.getValue()), new
						// CallSiteWithExtractedValue(wrappedCallSite, wrappedAllocSite));
						String retrieveConstantFromValue = retrieveConstantFromValue(wrappedAllocSite.getValue());
						int pos = -1;
						for (int i = 0; i < invoker.getArgs().size(); i++) {
							if (((AssignStmt) allocSite).getLeftOpBox().getValue().toString()
									.equals(invoker.getArgs().get(i).toString())) {
								pos = i;
							}
						}
						if (pos > -1 && "boolean".equals(invoker.getMethodRef().getParameterType(pos).toQuotedString())) {
							varVal.put("0".equals(retrieveConstantFromValue) ? "false" : "true",
									new CallSiteWithExtractedValue(wrappedCallSite, wrappedAllocSite));
						} else {
							varVal.put(retrieveConstantFromValue,
									new CallSiteWithExtractedValue(wrappedCallSite, wrappedAllocSite));
						}
					} else if (wrappedAllocSite.getValue() instanceof JNewArrayExpr) {
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
		Value arrayLocal = allocSite.getValue();
		Body methodBody = allocSite.stmt().getMethod().getActiveBody();
		Map<String, CallSiteWithExtractedValue> arrVal = Maps.newHashMap();

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
		}
		return arrVal;
	}

	private String retrieveConstantFromValue(Value val) {
		if (val instanceof StringConstant) {
			return ((StringConstant) val).value;
		} else if (val instanceof IntConstant || val.getType() instanceof IntType) {
			return val.toString();
		} else if (val instanceof LongConstant) {
			return val.toString().replaceAll("L", "");
		} else {
			return "";
		}
	}

}
