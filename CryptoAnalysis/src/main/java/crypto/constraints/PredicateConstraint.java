package crypto.constraints;

import boomerang.scene.ControlFlowGraph;
import boomerang.scene.DeclaredMethod;
import boomerang.scene.Method;
import boomerang.scene.Statement;
import boomerang.scene.Type;
import crypto.analysis.errors.ForbiddenMethodError;
import crypto.analysis.errors.HardCodedError;
import crypto.analysis.errors.InstanceOfError;
import crypto.analysis.errors.NeverTypeOfError;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractedValue;
import crypto.interfaces.ICrySLPredicateParameter;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CrySLMethod;
import crypto.rules.CrySLObject;
import crypto.rules.CrySLPredicate;
import crypto.typestate.CrySLMethodToSootMethod;

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
				List<ICrySLPredicateParameter> predMethods = parameters;
				for (ICrySLPredicateParameter predMethod : predMethods) {
					// check whether predMethod is in foundMethods, which type-state analysis has to
					// figure out
					CrySLMethod reqMethod = (CrySLMethod) predMethod;
					for (ControlFlowGraph.Edge edge : context.getCollectedCalls()) {
						Statement statement = edge.getTarget();
						if (!(statement.containsInvokeExpr())) {
							continue;
						}

						DeclaredMethod foundCall = statement.getInvokeExpr().getMethod();
						Method methodFoundCall = CrySLMethodToSootMethod.declaredMethodToJimpleMethod(foundCall);
						Collection<Method> convert = CrySLMethodToSootMethod.v().convert(reqMethod);
						if (convert.contains(methodFoundCall)) {
							return;
						}
					}
				}
				// TODO: Need seed here.
				return;
			case "noCallTo":
				if (context.getCollectedCalls().isEmpty()) {
					return;
				}
				List<ICrySLPredicateParameter> predForbiddenMethods = parameters;
				for (ICrySLPredicateParameter predForbMethod : predForbiddenMethods) {
					// check whether predForbMethod is in foundForbMethods, which forbidden-methods
					// analysis has to figure out
					CrySLMethod reqMethod = ((CrySLMethod) predForbMethod);

					for (ControlFlowGraph.Edge call : context.getCollectedCalls()) {
						Statement statement = call.getTarget();

						if (!statement.containsInvokeExpr()) {
							continue;
						}

						DeclaredMethod foundCall = statement.getInvokeExpr().getMethod();
						Method methodFoundCall = CrySLMethodToSootMethod.declaredMethodToJimpleMethod(foundCall);
						Collection<Method> convert = CrySLMethodToSootMethod.v().convert(reqMethod);
						if (convert.contains(methodFoundCall)) {
							errors.add(new ForbiddenMethodError(statement, context.getClassSpec().getRule(), methodFoundCall, convert));
							return;
						}
					}
				}
				return;
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

	private boolean isHardCodedArray(Map<String, CallSiteWithExtractedValue> extractSootArray) {
		return !(extractSootArray.keySet().size() == 1 && extractSootArray.containsKey(""));
	}
}
