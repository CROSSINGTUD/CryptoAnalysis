package crypto.constraints;

import boomerang.scene.DeclaredMethod;
import boomerang.scene.Statement;
import boomerang.scene.Type;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.CallToError;
import crypto.analysis.errors.InstanceOfError;
import crypto.analysis.errors.NeverTypeOfError;
import crypto.analysis.errors.NoCallToError;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractedValue;
import crypto.interfaces.ICrySLPredicateParameter;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CrySLMethod;
import crypto.rules.CrySLObject;
import crypto.rules.CrySLPredicate;
import crypto.utils.MatcherUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class PredicateConstraint extends EvaluableConstraint {

	protected PredicateConstraint(ISLConstraint origin, ConstraintSolver context) {
		super(origin, context);
	}

	@Override
	public void evaluate() {
		CrySLPredicate predicateConstraint = (CrySLPredicate) origin;
		String predName = predicateConstraint.getPredName();
		if (ConstraintSolver.predefinedPreds.contains(predName)) {
			handlePredefinedNames(predicateConstraint);
		}
	}

	public boolean isHardCoded(ExtractedValue val) {
		return val.getValue().isIntConstant() || val.getValue().isStringConstant()
				|| (val.getValue().isNewExpr()
						&& val.getValue().getType().toString().equals("java.math.BigInteger"));
	}

	private void handlePredefinedNames(CrySLPredicate pred) {
		switch (pred.getPredName()) {
			case "callTo":
                evaluateCallToPredicate(pred.getParameters());
				break;
			case "noCallTo":
                evaluateNoCallToPredicate(pred.getParameters());
				break;
			case "neverTypeOf":
				evaluateNeverTypeOfPredicate(pred);
				break;
			case "length":
				// TODO Not implemented!
				return;
			case "notHardCoded":
				CrySLObject varNotToBeHardCoded = (CrySLObject) pred.getParameters().get(0);
				String name = varNotToBeHardCoded.getVarName();
				String type = varNotToBeHardCoded.getJavaType();
				for (CallSiteWithParamIndex cs : context.getParsAndVals().keySet()) {
					if (cs.getVarName().equals(name)) {
						Collection<ExtractedValue> values = context.getParsAndVals().get(cs);
						for (ExtractedValue v : values) {
							// TODO Refactor
							/*if (isSubType(type, v.getValue().getType().toQuotedString())
									&& (isHardCoded(v) || isHardCodedArray(extractSootArray(cs, v)))) {
								errors.add(
										new HardCodedError(new CallSiteWithExtractedValue(cs, v), context.getClassSpec().getRule(),
												context.getObject(),
												pred));
							}*/
						}
					}
				}
				return;
			case "instanceOf":
				evaluateInstanceOfPredicate(pred);
				break;
			default:
				return;
		}
	}

	private void evaluateCallToPredicate(List<ICrySLPredicateParameter> callToMethods) {
		boolean isCalled = false;
		Collection<CrySLMethod> methods = parametersToCryslMethods(callToMethods);

		for (CrySLMethod predMethod : methods) {
			for (Statement statement : context.getCollectedCalls()) {
				if (!statement.containsInvokeExpr()) {
					continue;
				}

				DeclaredMethod foundCall = statement.getInvokeExpr().getMethod();
				Collection<CrySLMethod> matchingCryslMethods = MatcherUtils.getMatchingCryslMethodsToDeclaredMethod(context.getClassSpec().getRule(), foundCall);
				if (matchingCryslMethods.contains(predMethod)) {
					isCalled = true;
				}
			}
		}

		if (!isCalled) {
			IAnalysisSeed seed = context.getObject();
			CallToError typestateError = new CallToError(seed.cfgEdge().getStart(), seed, context.getClassSpec().getRule(), methods);
			errors.add(typestateError);
		}
	}

	private void evaluateNoCallToPredicate(List<ICrySLPredicateParameter> noCallToMethods) {
		Collection<CrySLMethod> methods = parametersToCryslMethods(noCallToMethods);

		for (CrySLMethod predMethod : methods) {
			for (Statement statement : context.getCollectedCalls()) {
				if (!statement.containsInvokeExpr()) {
					continue;
				}

				DeclaredMethod foundCall = statement.getInvokeExpr().getMethod();
				if (MatcherUtils.matchCryslMethodAndDeclaredMethod(predMethod, foundCall)) {
					NoCallToError noCallToError = new NoCallToError(statement, context.getObject(), context.getClassSpec().getRule());
					errors.add(noCallToError);
				}
			}
		}
	}

	private void evaluateNeverTypeOfPredicate(CrySLPredicate neverTypeOfPredicate) {
		List<CrySLObject> objects = parametersToCryslObjects(neverTypeOfPredicate.getParameters());

		if (objects.size() != 2) {
			return;
		}

		// neverTypeOf[$variable, $type]
		CrySLObject variable = objects.get(0);
		CrySLObject parameterType = objects.get(1);

		for (CallSiteWithParamIndex cs : context.getParameterAnalysisQuerySites()) {
			if (!variable.getName().equals(cs.getVarName())) {
				continue;
			}

			Collection<Type> types = context.getPropagatedTypes().get(cs);
			for (Type type : types) {
				if (!parameterType.getJavaType().equals(type.toString())) {
					continue;
				}

				ExtractedValue extractedValue = new ExtractedValue(cs.stmt(), cs.fact());
				CallSiteWithExtractedValue callSite = new CallSiteWithExtractedValue(cs, extractedValue);
				NeverTypeOfError neverTypeOfError = new NeverTypeOfError(callSite, context.getClassSpec().getRule(), context.getObject(), neverTypeOfPredicate);
				errors.add(neverTypeOfError);
			}
		}
	}

	private void evaluateInstanceOfPredicate(CrySLPredicate instanceOfPredicate) {
		List<CrySLObject> objects = parametersToCryslObjects(instanceOfPredicate.getParameters());

		if (objects.size() != 2) {
			return;
		}

		// instanceOf[$variable, $type]
		CrySLObject variable = objects.get(0);
		CrySLObject parameterType = objects.get(1);

		for (CallSiteWithParamIndex cs : context.getParameterAnalysisQuerySites()) {
			if (!variable.getName().equals(cs.getVarName())) {
				continue;
			}

			boolean isSubType = false;
			Collection<Type> types = context.getPropagatedTypes().get(cs);
			for (Type type : types) {
				if (type.isSubtypeOf(parameterType.getJavaType())) {
					isSubType = true;
				}
			}

			if (!isSubType) {
				ExtractedValue extractedValue = new ExtractedValue(cs.stmt(), cs.fact());
				CallSiteWithExtractedValue callSite = new CallSiteWithExtractedValue(cs, extractedValue);
				InstanceOfError instanceOfError = new InstanceOfError(callSite, context.getClassSpec().getRule(), context.getObject(), instanceOfPredicate);
				errors.add(instanceOfError);
			}
		}
	}

	private Collection<CrySLMethod> parametersToCryslMethods(Collection<ICrySLPredicateParameter> parameters) {
		List<CrySLMethod> methods = new ArrayList<>();

		for (ICrySLPredicateParameter parameter : parameters) {
			if (!(parameter instanceof CrySLMethod)) {
				continue;
			}

			CrySLMethod crySLMethod = (CrySLMethod) parameter;
			methods.add(crySLMethod);
		}
		return methods;
	}

	private List<CrySLObject> parametersToCryslObjects(Collection<ICrySLPredicateParameter> parameters) {
		List<CrySLObject> objects = new ArrayList<>();

		for (ICrySLPredicateParameter parameter : parameters) {
			if (!(parameter instanceof CrySLObject)) {
				continue;
			}

			CrySLObject crySLObject = (CrySLObject) parameter;
			objects.add(crySLObject);
		}
		return objects;
	}

	private boolean isSubType(String subType, String superType) {
		boolean subTypes = subType.equals(superType);
		subTypes |= (subType + "[]").equals(superType);

		if (subTypes) {
			return true;
		}

        try {
            return Class.forName(superType).isAssignableFrom(Class.forName(subType));
        } catch (ClassNotFoundException e) {
            return false;
        }
	}

	private boolean isHardCodedArray(Map<String, CallSiteWithExtractedValue> extractSootArray) {
		return !(extractSootArray.keySet().size() == 1 && extractSootArray.containsKey(""));
	}
}
