package crypto.constraints;

import boomerang.scene.DeclaredMethod;
import boomerang.scene.Statement;
import boomerang.scene.Type;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.CallToError;
import crypto.analysis.errors.ForbiddenMethodError;
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

	protected boolean isSubType(String typeOne, String typeTwo) {
		boolean subTypes = typeOne.equals(typeTwo);
		subTypes |= (typeOne + "[]").equals(typeTwo);
		if (!subTypes) {
			try {
				subTypes = Class.forName(typeOne).isAssignableFrom(Class.forName(typeTwo));
			} catch (ClassNotFoundException e) {
			}
		}
		return subTypes;
	}

	private void handlePredefinedNames(CrySLPredicate pred) {

		List<ICrySLPredicateParameter> parameters = pred.getParameters();
		switch (pred.getPredName()) {
			case "callTo":
                evaluateCallToPredicate(pred.getParameters());
				break;
			case "noCallTo":
                evaluateNoCallToPredicate(pred.getParameters());
				break;
			case "neverTypeOf":
				// pred looks as follows: neverTypeOf($varName, $type)
				// -> first parameter is always the variable
				// -> second parameter is always the type
				String varName = ((CrySLObject) parameters.get(0)).getVarName();
				for (CallSiteWithParamIndex cs : context.getParameterAnalysisQuerySites()) {
					if (cs.getVarName().equals(varName)) {
						Collection<Type> vals = context.getPropagatedTypes().get(cs);
						for (Type t : vals) {
							// TODO Refactor
							/*if (t.toQuotedString().equals(((CrySLObject) parameters.get(1)).getJavaType())) {
								for (ExtractedValue v : context.getParsAndVals().get(cs)) {
									errors.add(
											new NeverTypeOfError(new CallSiteWithExtractedValue(cs, v), context.getClassSpec().getRule(),
													context.getObject(),
													pred));
								}
								return;
							}*/
						}
					}
				}

				return;
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
				varName = ((CrySLObject) parameters.get(0)).getVarName();
				for (CallSiteWithParamIndex cs : context.getParameterAnalysisQuerySites()) {
					if (cs.getVarName().equals(varName)) {
						Collection<Type> vals = context.getPropagatedTypes().get(cs);
						String javaType = ((CrySLObject) parameters.get(1)).getJavaType();

						// TODO refactor
						/*if (!vals.parallelStream().anyMatch(e -> isSubType(e.toQuotedString(), javaType) || isSubType(javaType, e.toQuotedString()))) {
							for (ExtractedValue v : context.getParsAndVals().get(cs)) {
								errors.add(
										new InstanceOfError(new CallSiteWithExtractedValue(cs, v), context.getClassSpec().getRule(),
												context.getObject(),
												pred));
							}
						}*/
					}
				}
				return;
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

	private boolean isHardCodedArray(Map<String, CallSiteWithExtractedValue> extractSootArray) {
		return !(extractSootArray.keySet().size() == 1 && extractSootArray.containsKey(""));
	}
}
