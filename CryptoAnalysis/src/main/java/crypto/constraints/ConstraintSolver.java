package crypto.constraints;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import boomerang.jimple.Statement;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.ClassSpecification;
import crypto.analysis.CrySLResultsReporter;
import crypto.analysis.RequiredCryptSLPredicate;
import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.ForbiddenMethodError;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.analysis.errors.NeverTypeOfError;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractedValue;
import crypto.interfaces.ICryptSLPredicateParameter;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CryptSLArithmeticConstraint;
import crypto.rules.CryptSLComparisonConstraint;
import crypto.rules.CryptSLConstraint;
import crypto.rules.CryptSLConstraint.LogOps;
import crypto.rules.CryptSLMethod;
import crypto.rules.CryptSLObject;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLSplitter;
import crypto.rules.CryptSLValueConstraint;
import crypto.typestate.CryptSLMethodToSootMethod;
import soot.IntType;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;

public class ConstraintSolver {

	private final List<ISLConstraint> allConstraints;
	private final Set<ISLConstraint> relConstraints = Sets.newHashSet();
	private final List<RequiredCryptSLPredicate> requiredPredicates = Lists.newArrayList();
	private final Collection<Statement> collectedCalls;
	private final Multimap<CallSiteWithParamIndex, ExtractedValue> parsAndVals;
	public final static List<String> predefinedPreds = Arrays.asList("callTo", "noCallTo", "neverTypeOf", "length");
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
				if (cons instanceof CryptSLPredicate) {
					CryptSLPredicate pred = (CryptSLPredicate) cons;
					for (CallSiteWithParamIndex cwpi : this.parameterAnalysisQuerySites) {
						for(ICryptSLPredicateParameter p : pred.getParameters()) {
							// TODO: FIX Cipher rule
							if (p.getName().equals("transformation"))
								continue;
							if (cwpi.getVarName().equals(p.getName())) {
								
								relConstraints.add(pred);
								requiredPredicates.add(new RequiredCryptSLPredicate(pred, cwpi.stmt()));
							}
						}
					}
				} else {
					relConstraints.add(cons);
				}
			}
		}
		this.reporter = crySLResultsReporter;
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
		if (con instanceof CryptSLComparisonConstraint) {
			return new ComparisonConstraint((CryptSLComparisonConstraint) con);
		} else if (con instanceof CryptSLValueConstraint) {
			return new ValueConstraint((CryptSLValueConstraint) con);
		} else if (con instanceof CryptSLPredicate) {
			return new PredicateConstraint((CryptSLPredicate) con);
		} else if (con instanceof CryptSLConstraint) {
			return new BinaryConstraint((CryptSLConstraint) con);
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

		public BinaryConstraint(CryptSLConstraint c) {
			super(c);
		}

		@Override
		public void evaluate() {
			CryptSLConstraint binaryConstraint = (CryptSLConstraint) origin;
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

		public PredicateConstraint(CryptSLPredicate c) {
			super(c);
		}

		@Override
		public void evaluate() {
			CryptSLPredicate predicateConstraint = (CryptSLPredicate) origin;
			String predName = predicateConstraint.getPredName();
			if (predefinedPreds.contains(predName)) {
				handlePredefinedNames(predicateConstraint);
			}
		}

		private void handlePredefinedNames(CryptSLPredicate pred) {

			List<ICryptSLPredicateParameter> parameters = pred.getParameters();
			switch (pred.getPredName()) {
				case "callTo":
					List<ICryptSLPredicateParameter> predMethods = parameters;
					for (ICryptSLPredicateParameter predMethod : predMethods) {
						//check whether predMethod is in foundMethods, which type-state analysis has to figure out
						CryptSLMethod reqMethod = (CryptSLMethod) predMethod;
						for (Statement unit : collectedCalls) {
							if (!(unit.isCallsite()))
								continue;
							SootMethod foundCall = ((Stmt) unit.getUnit().get()).getInvokeExpr().getMethod();
							Collection<SootMethod> convert = CryptSLMethodToSootMethod.v().convert(reqMethod);
							if (convert.contains(foundCall)) {
								return;
							}
						}
					}
					//TODO: Need seed here.
					return;
				case "noCallTo":
					if (collectedCalls.isEmpty()) {
						return;
					}
					List<ICryptSLPredicateParameter> predForbiddenMethods = parameters;
					for (ICryptSLPredicateParameter predForbMethod : predForbiddenMethods) {
						//check whether predForbMethod is in foundForbMethods, which forbidden-methods analysis has to figure out
						CryptSLMethod reqMethod = ((CryptSLMethod) predForbMethod);

						for (Statement call : collectedCalls) {
							if (!call.isCallsite())
								continue;
							SootMethod foundCall = call.getUnit().get().getInvokeExpr().getMethod();
							Collection<SootMethod> convert = CryptSLMethodToSootMethod.v().convert(reqMethod);
							if (convert.contains(foundCall)) {
								errors.add(new ForbiddenMethodError(call, classSpec.getRule(), foundCall, convert));
								return;
							}
						}
					}
					return;
				case "neverTypeOf":
					//pred looks as follows: neverTypeOf($varName, $type)
					// -> first parameter is always the variable
					// -> second parameter is always the type
					String varName = ((CryptSLObject) parameters.get(0)).getVarName();
					for (CallSiteWithParamIndex cs : parameterAnalysisQuerySites) {
						if (cs.getVarName().equals(varName)) {
							Collection<Type> vals = propagatedTypes.get(cs);
							for (Type t : vals) {
								if (t.toQuotedString().equals(parameters.get(1).getName())) {
									for(ExtractedValue v : parsAndVals.get(cs)) {
										errors.add(new NeverTypeOfError(new CallSiteWithExtractedValue(cs, v), classSpec.getRule(), object, pred));
									}
									return;
								}
							}
						}
					}

					return;
				case "length":
					//pred looks as follows: neverTypeOf($varName)
					// -> parameter is always the variable
					String var = ((CryptSLObject) pred.getParameters().get(0)).getVarName();
					for (CallSiteWithParamIndex cs : parsAndVals.keySet()) {
						if (cs.getVarName().equals(var)) {
							errors.add(new ImpreciseValueExtractionError(origin, cs.stmt(), classSpec.getRule()));
							break;
						}
					}
					return;
				default:
					return;
			}
		}
	}

	public class ComparisonConstraint extends EvaluableConstraint {

		public ComparisonConstraint(CryptSLComparisonConstraint c) {
			super(c);
		}

		@Override
		public void evaluate() {
			CryptSLComparisonConstraint compConstraint = (CryptSLComparisonConstraint) origin;

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

		private Map<Integer, CallSiteWithExtractedValue> evaluate(CryptSLArithmeticConstraint arith) {
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

		private Map<Integer, CallSiteWithExtractedValue> extractValueAsInt(ICryptSLPredicateParameter par, CryptSLArithmeticConstraint arith) {
			if (par instanceof CryptSLPredicate) {
				PredicateConstraint predicateConstraint = new PredicateConstraint((CryptSLPredicate) par);
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
			try {
				//1. exp may (already) be an integer
				valuesInt.put(Integer.parseInt(exp), null);
				return valuesInt;
			} catch (NumberFormatException ex) {
				//2. If not, it's a variable name.
				//Get value of variable left from map
				final Map<String, CallSiteWithExtractedValue> valueCollection = extractValueAsString(exp, cons);
				if (valueCollection.isEmpty()) {
					return valuesInt;
				}
				try {
					for (Entry<String, CallSiteWithExtractedValue> value : valueCollection.entrySet()) {
						valuesInt.put(Integer.parseInt(value.getKey()), value.getValue());
					}
				} catch (NumberFormatException ex1) {
					//If that does not work either, I'm out of ideas ...
					throw new RuntimeException();
				}
				return valuesInt;
			}
		}

	}

	public class ValueConstraint extends EvaluableConstraint {

		public ValueConstraint(CryptSLValueConstraint c) {
			super(c);
		}

		@Override
		public void evaluate() {
			CryptSLValueConstraint valCons = (CryptSLValueConstraint) origin;

			CryptSLObject var = valCons.getVar();
			final List<Entry<String, CallSiteWithExtractedValue>> vals = getValFromVar(var, valCons);
			if (vals.isEmpty()) {
				//TODO: Check whether this works as desired
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

		private List<Entry<String, CallSiteWithExtractedValue>> getValFromVar(CryptSLObject var, ISLConstraint cons) {
			final String varName = var.getVarName();
			final Map<String, CallSiteWithExtractedValue> valueCollection = extractValueAsString(varName, cons);
			List<Entry<String, CallSiteWithExtractedValue>> vals = new ArrayList<>();
			if (valueCollection.isEmpty()) {
				return vals;
			}
			for (Entry<String, CallSiteWithExtractedValue> e : valueCollection.entrySet()) {
				CryptSLSplitter splitter = var.getSplitter();
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
						if (callSite.equals(allocSite)) {
							varVal.put(retrieveConstantFromValue(callSite.getInvokeExpr().getArg(wrappedCallSite.getIndex())), new CallSiteWithExtractedValue(wrappedCallSite, wrappedAllocSite));
						} else if (allocSite instanceof AssignStmt) {
							if (wrappedAllocSite.getValue() instanceof Constant) {
								varVal.put(retrieveConstantFromValue(wrappedAllocSite.getValue()), new CallSiteWithExtractedValue(wrappedCallSite, wrappedAllocSite));
							}
						}
					}
				}
			}
			return varVal;
		}
	}

	public List<RequiredCryptSLPredicate> getRequiredPredicates() {
		return requiredPredicates;
	}
}