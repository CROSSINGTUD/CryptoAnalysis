package crypto.constraints;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import boomerang.jimple.Statement;
import crypto.analysis.AlternativeReqPredicate;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.ClassSpecification;
import crypto.analysis.CrySLResultsReporter;
import crypto.analysis.RequiredCrySLPredicate;
import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.ForbiddenMethodError;
import crypto.analysis.errors.HardCodedError;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.analysis.errors.InstanceOfError;
import crypto.analysis.errors.NeverTypeOfError;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractedValue;
import crypto.interfaces.ICrySLPredicateParameter;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CrySLArithmeticConstraint;
import crypto.rules.CrySLComparisonConstraint;
import crypto.rules.CrySLConstraint;
import crypto.rules.CrySLConstraint.LogOps;
import crypto.rules.CrySLMethod;
import crypto.rules.CrySLObject;
import crypto.rules.CrySLPredicate;
import crypto.rules.CrySLSplitter;
import crypto.rules.CrySLValueConstraint;
import crypto.typestate.CrySLMethodToSootMethod;
import soot.Body;
import soot.IntType;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.LongConstant;
import soot.jimple.NewExpr;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.internal.JNewArrayExpr;

public class ConstraintSolver {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConstraintSolver.class);

	private final List<ISLConstraint> allConstraints;
	private final Set<ISLConstraint> relConstraints = Sets.newHashSet();
	private final List<ISLConstraint> requiredPredicates = Lists.newArrayList();
	private final Collection<Statement> collectedCalls;
	private final Multimap<CallSiteWithParamIndex, ExtractedValue> parsAndVals;
	public final static List<String> predefinedPreds = Arrays.asList("callTo", "noCallTo", "neverTypeOf", "length", "notHardCoded", "instanceOf");
	private final CrySLResultsReporter reporter;
	private final AnalysisSeedWithSpecification object;
	private final ClassSpecification classSpec;
	private Collection<CallSiteWithParamIndex> parameterAnalysisQuerySites;
	private Multimap<CallSiteWithParamIndex, Type> propagatedTypes;

	public ConstraintSolver(AnalysisSeedWithSpecification object, Collection<Statement> collectedCalls, CrySLResultsReporter crySLResultsReporter) {
		this.object = object;
		this.classSpec = object.getSpec();
		this.parsAndVals = object.getParameterAnalysis().getCollectedValues();
		this.propagatedTypes = object.getParameterAnalysis().getPropagatedTypes();
		this.parameterAnalysisQuerySites = object.getParameterAnalysis().getAllQuerySites();
		this.collectedCalls = collectedCalls;
		this.allConstraints = this.classSpec.getRule().getConstraints();
		for (ISLConstraint cons : allConstraints) {

			Set<String> involvedVarNames = cons.getInvolvedVarNames();
			for (CallSiteWithParamIndex cwpi : this.parameterAnalysisQuerySites) {
				involvedVarNames.remove(cwpi.getVarName());
			}

			if (involvedVarNames.isEmpty() || (cons.toString().contains("speccedKey") && involvedVarNames.size() == 1)) {
				if (cons instanceof CrySLPredicate) {
					RequiredCrySLPredicate pred = retrieveValuesForPred(cons);
					if (pred != null) {
						CrySLPredicate innerPred = pred.getPred();
						if (innerPred != null) {
							relConstraints.add(innerPred);
							requiredPredicates.add(pred);
						}
					}
				} else if (cons instanceof CrySLConstraint) {
					ISLConstraint right = ((CrySLConstraint) cons).getRight();
					if (right instanceof CrySLPredicate && !predefinedPreds.contains(((CrySLPredicate) right).getPredName())) {
						requiredPredicates.add(collectAlternativePredicates((CrySLConstraint) cons, null));
					} else {
						relConstraints.add(cons);
					}
				} else {
					relConstraints.add(cons);
				}
			}
		}
		this.reporter = crySLResultsReporter;
	}

	private ISLConstraint collectAlternativePredicates(CrySLConstraint cons, AlternativeReqPredicate alt) {
		CrySLPredicate right = (CrySLPredicate) cons.getRight();
		if (alt == null) {
			alt = new AlternativeReqPredicate(right, right.getLocation());
		} else {
			alt.addAlternative(right);
		}

		if (cons.getLeft() instanceof CrySLPredicate) {
			alt.addAlternative((CrySLPredicate) cons.getLeft());
		} else {
			return collectAlternativePredicates((CrySLConstraint) cons.getLeft(), alt);
		}

		return alt;
	}

	private RequiredCrySLPredicate retrieveValuesForPred(ISLConstraint cons) {
		CrySLPredicate pred = (CrySLPredicate) cons;
		for (CallSiteWithParamIndex cwpi : this.parameterAnalysisQuerySites) {
			for (ICrySLPredicateParameter p : pred.getParameters()) {
				// TODO: FIX Cipher rule
				if (p.getName().equals("transformation"))
					continue;
				if (cwpi.getVarName().equals(p.getName())) {
					return new RequiredCrySLPredicate(pred, cwpi.stmt());
				}
			}
		}
		return null;
	}

	private static String retrieveConstantFromValue(Value val) {
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

	public int evaluateRelConstraints() {
		int fail = 0;
		for (ISLConstraint con : relConstraints) {
			EvaluableConstraint currentConstraint = createConstraint(con);
			currentConstraint.evaluate();
			for (AbstractError e : currentConstraint.getErrors()) {
				if (e instanceof ImpreciseValueExtractionError) {
					reporter.reportError(object, new ImpreciseValueExtractionError(con, e.getErrorLocation(), e.getRule()));
					break;
				} else {
					fail++;
					reporter.reportError(object, e);
				}
			}
		}
		return fail;
	}

	public EvaluableConstraint createConstraint(ISLConstraint con) {
		if (con instanceof CrySLComparisonConstraint) {
			return new ComparisonConstraint((CrySLComparisonConstraint) con);
		} else if (con instanceof CrySLValueConstraint) {
			return new ValueConstraint((CrySLValueConstraint) con);
		} else if (con instanceof CrySLPredicate) {
			return new PredicateConstraint((CrySLPredicate) con);
		} else if (con instanceof CrySLConstraint) {
			return new BinaryConstraint((CrySLConstraint) con);
		}
		return null;
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
	public Set<ISLConstraint> getRelConstraints() {
		return relConstraints;
	}

	private class BinaryConstraint extends EvaluableConstraint {

		public BinaryConstraint(CrySLConstraint c) {
			super(c);
		}

		@Override
		public void evaluate() {
			CrySLConstraint binaryConstraint = (CrySLConstraint) origin;
			EvaluableConstraint left = createConstraint(binaryConstraint.getLeft());
			EvaluableConstraint right = createConstraint(binaryConstraint.getRight());
			left.evaluate();
			LogOps ops = binaryConstraint.getOperator();

			if (ops.equals(LogOps.implies)) {
				if (left.hasErrors()) {
					return;
				} else {
					right.evaluate();
					errors.addAll(right.getErrors());
					return;
				}
			} else if (ops.equals(LogOps.or)) {
				right.evaluate();
				errors.addAll(left.getErrors());
				errors.addAll(right.getErrors());
				return;
			} else if (ops.equals(LogOps.and)) {
				if (left.hasErrors()) {
					errors.addAll(left.getErrors());
					return;
				} else {
					right.evaluate();
					errors.addAll(right.getErrors());
					return;
				}
			} else if (ops.equals(LogOps.eq)) {
				right.evaluate();
				if ((left.hasErrors() && right.hasErrors()) || (!left.hasErrors() && !right.hasErrors())) {
					return;
				} else {
					errors.addAll(right.getErrors());
					return;
				}
			}
			errors.addAll(left.getErrors());
		}

	}

	public class PredicateConstraint extends EvaluableConstraint {

		public PredicateConstraint(CrySLPredicate c) {
			super(c);
		}

		@Override
		public void evaluate() {
			CrySLPredicate predicateConstraint = (CrySLPredicate) origin;
			String predName = predicateConstraint.getPredName();
			if (predefinedPreds.contains(predName)) {
				handlePredefinedNames(predicateConstraint);
			}
		}

		private void handlePredefinedNames(CrySLPredicate pred) {

			List<ICrySLPredicateParameter> parameters = pred.getParameters();
			switch (pred.getPredName()) {
				case "callTo":
					List<ICrySLPredicateParameter> predMethods = parameters;
					for (ICrySLPredicateParameter predMethod : predMethods) {
						// check whether predMethod is in foundMethods, which type-state analysis has to figure out
						CrySLMethod reqMethod = (CrySLMethod) predMethod;
						for (Statement unit : collectedCalls) {
							if (!(unit.isCallsite()))
								continue;
							SootMethod foundCall = ((Stmt) unit.getUnit().get()).getInvokeExpr().getMethod();
							Collection<SootMethod> convert = CrySLMethodToSootMethod.v().convert(reqMethod);
							if (convert.contains(foundCall)) {
								return;
							}
						}
					}
					// TODO: Need seed here.
					return;
				case "noCallTo":
					if (collectedCalls.isEmpty()) {
						return;
					}
					List<ICrySLPredicateParameter> predForbiddenMethods = parameters;
					for (ICrySLPredicateParameter predForbMethod : predForbiddenMethods) {
						// check whether predForbMethod is in foundForbMethods, which forbidden-methods analysis has to figure out
						CrySLMethod reqMethod = ((CrySLMethod) predForbMethod);

						for (Statement call : collectedCalls) {
							if (!call.isCallsite())
								continue;
							SootMethod foundCall = call.getUnit().get().getInvokeExpr().getMethod();
							Collection<SootMethod> convert = CrySLMethodToSootMethod.v().convert(reqMethod);
							if (convert.contains(foundCall)) {
								errors.add(new ForbiddenMethodError(call, classSpec.getRule(), foundCall, convert));
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
					for (CallSiteWithParamIndex cs : parameterAnalysisQuerySites) {
						if (cs.getVarName().equals(varName)) {
							Collection<Type> vals = propagatedTypes.get(cs);
							for (Type t : vals) {
								if (t.toQuotedString().equals(parameters.get(1).getName())) {
									for (ExtractedValue v : parsAndVals.get(cs)) {
										errors.add(new NeverTypeOfError(new CallSiteWithExtractedValue(cs, v), classSpec.getRule(), object, pred));
									}
									return;
								}
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
					for (CallSiteWithParamIndex cs : parsAndVals.keySet()) {
						if (cs.getVarName().equals(name)) {
							Collection<ExtractedValue> values = parsAndVals.get(cs);
							for (ExtractedValue v : values) {
								if (isSubType(type,  v.getValue().getType().toQuotedString()) && (isHardCoded(v) || isHardCodedArray(extractSootArray(cs, v)))) {
									errors.add(new HardCodedError(new CallSiteWithExtractedValue(cs, v), classSpec.getRule(), object, pred));
								}
							}
						}
					}
					return;
				case "instanceOf":
					varName = ((CrySLObject) parameters.get(0)).getVarName();
					for (CallSiteWithParamIndex cs : parameterAnalysisQuerySites) {
						if (cs.getVarName().equals(varName)) {
							Collection<Type> vals = propagatedTypes.get(cs);
							if (!vals.parallelStream().anyMatch(e -> isSubType(e.toQuotedString(), parameters.get(1).getName()) || isSubType(parameters.get(1).getName(), e.toQuotedString()))) {
								for (ExtractedValue v : parsAndVals.get(cs)) {
									errors.add(new InstanceOfError(new CallSiteWithExtractedValue(cs, v), classSpec.getRule(), object, pred));
								}
							}
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

	public class ComparisonConstraint extends EvaluableConstraint {

		public ComparisonConstraint(CrySLComparisonConstraint c) {
			super(c);
		}

		@Override
		public void evaluate() {
			CrySLComparisonConstraint compConstraint = (CrySLComparisonConstraint) origin;

			Map<Integer, CallSiteWithExtractedValue> left = evaluate(compConstraint.getLeft());
			Map<Integer, CallSiteWithExtractedValue> right = evaluate(compConstraint.getRight());

			for (Entry<Integer, CallSiteWithExtractedValue> entry : right.entrySet()) {
				if (entry.getKey() == Integer.MIN_VALUE) {
					errors.add(new ConstraintError(entry.getValue(), classSpec.getRule(), object, compConstraint));
					return;
				}
			}

			for (Entry<Integer, CallSiteWithExtractedValue> leftie : left.entrySet()) {
				if (leftie.getKey() == Integer.MIN_VALUE) {
					errors.add(new ConstraintError(leftie.getValue(), classSpec.getRule(), object, compConstraint));
					return;
				}
				for (Entry<Integer, CallSiteWithExtractedValue> rightie : right.entrySet()) {

					boolean cons = true;
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
						errors.add(new ConstraintError(leftie.getValue(), classSpec.getRule(), object, origin));
						return;
					}
				}
			}
		}

		private Map<Integer, CallSiteWithExtractedValue> evaluate(CrySLArithmeticConstraint arith) {
			Map<Integer, CallSiteWithExtractedValue> left = extractValueAsInt(arith.getLeft(), arith);
			Map<Integer, CallSiteWithExtractedValue> right = extractValueAsInt(arith.getRight(), arith);
			for (Entry<Integer, CallSiteWithExtractedValue> rightie : right.entrySet()) {
				if (rightie.getKey() == Integer.MIN_VALUE) {
					return left;
				}
			}

			Map<Integer, CallSiteWithExtractedValue> results = new HashMap<>();
			for (Entry<Integer, CallSiteWithExtractedValue> leftie : left.entrySet()) {
				if (leftie.getKey() == Integer.MIN_VALUE) {
					return left;
				}

				for (Entry<Integer, CallSiteWithExtractedValue> rightie : right.entrySet()) {
					int sum = 0;
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

		private Map<Integer, CallSiteWithExtractedValue> extractValueAsInt(ICrySLPredicateParameter par, CrySLArithmeticConstraint arith) {
			if (par instanceof CrySLPredicate) {
				PredicateConstraint predicateConstraint = new PredicateConstraint((CrySLPredicate) par);
				predicateConstraint.evaluate();
				if (!predicateConstraint.getErrors().isEmpty()) {
					for (AbstractError err : predicateConstraint.getErrors()) {
						errors.add(new ImpreciseValueExtractionError(arith, err.getErrorLocation(), err.getRule()));
					}
					predicateConstraint.errors.clear();
				}
				return new HashMap<Integer, CallSiteWithExtractedValue>();
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
			}
			catch (NumberFormatException ex) {
				// 2. If not, it's a variable name.
				// Get value of variable left from map
				final Map<String, CallSiteWithExtractedValue> valueCollection = extractValueAsString(exp, cons);
				if (valueCollection.isEmpty()) {
					return valuesInt;
				}
				try {
					for (Entry<String, CallSiteWithExtractedValue> value : valueCollection.entrySet()) {
						if(value.getKey().equals("true"))
							valuesInt.put(1, value.getValue());
						else if(value.getKey().equals("false"))
							valuesInt.put(0, value.getValue());
						else
							valuesInt.put(Integer.parseInt(value.getKey()), value.getValue());
					}
				}
				catch (NumberFormatException ex1) {
					// If that does not work either, I'm out of ideas ...
					LOGGER.error("An exception occured when extracting value as Integer.", ex1);
				}
				return valuesInt;
			}
		}

	}

	public class ValueConstraint extends EvaluableConstraint {

		public ValueConstraint(CrySLValueConstraint c) {
			super(c);
		}

		@Override
		public void evaluate() {
			CrySLValueConstraint valCons = (CrySLValueConstraint) origin;

			CrySLObject var = valCons.getVar();
			final List<Entry<String, CallSiteWithExtractedValue>> vals = getValFromVar(var, valCons);
			if (vals.isEmpty()) {
				// TODO: Check whether this works as desired
				return;
			}
			for (Entry<String, CallSiteWithExtractedValue> val : vals) {
				List<String> values = valCons.getValueRange().parallelStream().map(e -> e.toLowerCase()).collect(Collectors.toList());
				if (!values.contains(val.getKey().toLowerCase())) {
					errors.add(new ConstraintError(val.getValue(), classSpec.getRule(), object, valCons));
				}
			}
			return;
		}

		private List<Entry<String, CallSiteWithExtractedValue>> getValFromVar(CrySLObject var, ISLConstraint cons) {
			final String varName = var.getVarName();
			final Map<String, CallSiteWithExtractedValue> valueCollection = extractValueAsString(varName, cons);
			List<Entry<String, CallSiteWithExtractedValue>> vals = new ArrayList<>();
			if (valueCollection.isEmpty()) {
				return vals;
			}
			for (Entry<String, CallSiteWithExtractedValue> e : valueCollection.entrySet()) {
				CrySLSplitter splitter = var.getSplitter();
				final CallSiteWithExtractedValue location = e.getValue();
				String val = e.getKey();
				if (splitter != null) {
					int ind = splitter.getIndex();
					String splitElement = splitter.getSplitter();
					if (ind > 0) {
						String[] splits = val.split(splitElement);
						if (splits.length > ind) {
							vals.add(new AbstractMap.SimpleEntry<>(splits[ind], location));
						} else {
							vals.add(new AbstractMap.SimpleEntry<>("", location));
						}
					} else {
						vals.add(new AbstractMap.SimpleEntry<>(val.split(splitElement)[ind], location));
					}
				} else {
					vals.add(new AbstractMap.SimpleEntry<>(val, location));
				}
			}
			return vals;
		}

	}

	public abstract class EvaluableConstraint {

		Set<AbstractError> errors = Sets.newHashSet();
		ISLConstraint origin;

		public abstract void evaluate();

		public EvaluableConstraint(ISLConstraint con) {
			origin = con;
		}

		protected Collection<AbstractError> getErrors() {
			return errors;
		};

		public boolean hasErrors() {
			return !errors.isEmpty();
		}

		protected Map<String, CallSiteWithExtractedValue> extractValueAsString(String varName, ISLConstraint cons) {
			Map<String, CallSiteWithExtractedValue> varVal = Maps.newHashMap();
			for (CallSiteWithParamIndex wrappedCallSite : parsAndVals.keySet()) {
				final Stmt callSite = wrappedCallSite.stmt().getUnit().get();

				for (ExtractedValue wrappedAllocSite : parsAndVals.get(wrappedCallSite)) {
					final Stmt allocSite = wrappedAllocSite.stmt().getUnit().get();
					if (wrappedCallSite.getVarName().equals(varName)) {
						InvokeExpr invoker = callSite.getInvokeExpr();
						if (callSite.equals(allocSite)) {
							varVal.put(retrieveConstantFromValue(invoker.getArg(wrappedCallSite.getIndex())), new CallSiteWithExtractedValue(wrappedCallSite, wrappedAllocSite));
						} else if (allocSite instanceof AssignStmt) {
							if (wrappedAllocSite.getValue() instanceof Constant) {
//								varVal.put(retrieveConstantFromValue(wrappedAllocSite.getValue()), new CallSiteWithExtractedValue(wrappedCallSite, wrappedAllocSite));
								String retrieveConstantFromValue = retrieveConstantFromValue(wrappedAllocSite.getValue());
								int pos = -1;
								for (int i = 0; i < invoker.getArgs().size(); i++) {
									if (((AssignStmt) allocSite).getLeftOpBox().getValue().toString().equals(invoker.getArgs().get(i).toString())) {
										pos = i;
									}
								}
								if (pos > -1 && "boolean".equals(invoker.getMethodRef().getParameterType(pos).toQuotedString())) {
									varVal.put("0".equals(retrieveConstantFromValue) ? "false" : "true", new CallSiteWithExtractedValue(wrappedCallSite, wrappedAllocSite));
								} else {
									varVal.put(retrieveConstantFromValue, new CallSiteWithExtractedValue(wrappedCallSite, wrappedAllocSite));
								}
							} else if (wrappedAllocSite.getValue() instanceof JNewArrayExpr) {								
								varVal.putAll(extractSootArray(wrappedCallSite, wrappedAllocSite));
							}
						}
					}
				}
			}
			return varVal;
		}
		
		/***
		 * Function that finds the values assigned to a soot array.
		 * @param callSite call site at which sootValue is involved
		 * @param allocSite allocation site at which sootValue is involved
		 * @param arrayLocal soot array local variable for which values are to be found
		 * @return extracted array values
		 */
		protected Map<String, CallSiteWithExtractedValue> extractSootArray(CallSiteWithParamIndex callSite, ExtractedValue allocSite){
			Value arrayLocal = allocSite.getValue();
			Body methodBody = allocSite.stmt().getMethod().getActiveBody();
			Map<String, CallSiteWithExtractedValue> arrVal = Maps.newHashMap();
				if (methodBody != null) {
					Iterator<Unit> unitIterator = methodBody.getUnits().snapshotIterator();
					while (unitIterator.hasNext()) {
						final Unit unit = unitIterator.next();
						if (unit instanceof AssignStmt) {
							AssignStmt uStmt = (AssignStmt) (unit);
							Value leftValue = uStmt.getLeftOp();
							Value rightValue = uStmt.getRightOp();
							if (leftValue.toString().contains(arrayLocal.toString()) && !rightValue.toString().contains("newarray")) {
								arrVal.put(retrieveConstantFromValue(rightValue), new CallSiteWithExtractedValue(callSite, allocSite));
							}
						}
					}
				}
			return arrVal;
		}
	}

	public List<ISLConstraint> getRequiredPredicates() {
		return requiredPredicates;
	}

	protected boolean isSubType(String typeOne, String typeTwo) {
		boolean subTypes = typeOne.equals(typeTwo);
		subTypes |= (typeOne + "[]").equals(typeTwo);
		if (!subTypes) {
			try {
				subTypes = Class.forName(typeOne).isAssignableFrom(Class.forName(typeTwo));
			}
			catch (ClassNotFoundException e) {}
		}
		return subTypes;
	}

	public boolean isHardCoded(ExtractedValue val) {
		return val.getValue() instanceof IntConstant || val.getValue() instanceof StringConstant || (val.getValue() instanceof NewExpr && ((NewExpr) val.getValue()).getType().toString().equals("java.math.BigInteger"));
	}
}
