package crypto.analysis;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import boomerang.ForwardQuery;
import boomerang.jimple.Statement;
import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.ForbiddenMethodError;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.analysis.errors.NeverTypeOfError;
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
import fj.data.vector.V;
import java_cup.symbol_set;
import soot.IntType;
import soot.SootMethod;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.IntConstant;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;

public class ConstraintSolver {

	private final ClassSpecification classSpec;
	private final List<ISLConstraint> allConstraints;
	private final List<ISLConstraint> relConstraints;
	private final Collection<Statement> collectedCalls;
	private final Multimap<CallSiteWithParamIndex, ExtractedValue> parsAndVals;
	public final static List<String> predefinedPreds = Arrays.asList("callTo", "noCallTo", "neverTypeOf", "length");
	private final CrySLResultsReporter reporter;

	public ConstraintSolver(ClassSpecification spec, Multimap<CallSiteWithParamIndex, ExtractedValue> parametersToValues, Collection<Statement> collectedCalls, CrySLResultsReporter crySLResultsReporter) {
		classSpec = spec;
		parsAndVals = parametersToValues;
		this.collectedCalls = collectedCalls;
		allConstraints = spec.getRule().getConstraints();
		relConstraints = new ArrayList<ISLConstraint>();
		for (ISLConstraint cons : allConstraints) {
			
			Set<String> involvedVarNames = cons.getInvolvedVarNames();
			for (CallSiteWithParamIndex cwpi : parametersToValues.keySet()) {
				involvedVarNames.remove(cwpi.getVarName());
			}
			
			if (involvedVarNames.isEmpty()) {
				if (cons instanceof CryptSLPredicate) {
					CryptSLPredicate pred = (CryptSLPredicate) cons;
					for (CallSiteWithParamIndex cwpi : parametersToValues.keySet()) {
						if (cwpi.getVarName().equals(pred.getParameters().get(0).getName())) {
							pred.setLocation(cwpi.stmt());
							relConstraints.add(pred);
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
		} else {
			return "";
		}
	}

	public int evaluateRelConstraints() {
		int fail = 0;
		for (ISLConstraint con : relConstraints) {
			EvaluatableConstraint c  = createConstraint(con);
			c.evaluate();
			for(AbstractError e : c.getErrors()){
				fail++;
				reporter.reportError(e);
//				reporter.reportError(new ConstraintError(unit, classSpec.getRule(), null, con, parsAndVals));
			}
		}
		return fail;
	}

	public EvaluatableConstraint createConstraint(ISLConstraint con) {
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
	public List<ISLConstraint> getRelConstraints() {
		return relConstraints;
	}
	
	private class BinaryConstraint extends EvaluatableConstraint{
		final CryptSLConstraint binaryConstraint;
		public BinaryConstraint(CryptSLConstraint c) {
			this.binaryConstraint = c;
		}
		@Override
		void evaluate() {
			EvaluatableConstraint left = createConstraint(binaryConstraint.getLeft());
			EvaluatableConstraint right = createConstraint(binaryConstraint.getRight());
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
	private class PredicateConstraint extends EvaluatableConstraint{
		final CryptSLPredicate predicateConstraint;
		public PredicateConstraint(CryptSLPredicate c) {
			this.predicateConstraint = c;
		}
		@Override
		void evaluate() {
			String predName = predicateConstraint.getPredName();
			if (predefinedPreds.contains(predName)) {
				 handlePredefinedNames(predicateConstraint);
			}
		}

		private Statement handlePredefinedNames(CryptSLPredicate pred) {
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
								return null;
							}
						}
					}
					//TODO: Need seed here.
					return null;
				case "noCallTo":
					if (collectedCalls.isEmpty()) {
						return null;
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
								return null;
							}
						}
					}
					return null;
				case "neverTypeOf":
					//pred looks as follows: neverTypeOf($varName, $type)
					// -> first parameter is always the variable
					// -> second parameter is always the type
					String varName = ((CryptSLObject) parameters.get(0)).getVarName();
					Statement res = null;
					for (CallSiteWithParamIndex cs : parsAndVals.keySet()) {
						if (cs.getVarName().equals(varName)) {
							Collection<ExtractedValue> vals = parsAndVals.get(cs);
							for (ExtractedValue extractedVal : vals) {
								Statement stmt = extractedVal.stmt();
								if (stmt.getUnit().get() instanceof AssignStmt) {
									Value rightAss = ((AssignStmt) stmt.getUnit().get()).getRightOp();
									if (!rightAss.getType().toQuotedString().equals(parameters.get(1).getName())) {
									} else {
										//TODO: Fix NeverTypeOfErrors also report a ConstraintError									
										errors.add(new NeverTypeOfError(stmt, classSpec.getRule(), null, pred, parsAndVals));
										return stmt;
									}
								} else {
								}

							}
						}
					}

					return res;
				case "length":
					//pred looks as follows: neverTypeOf($varName)
					// -> parameter is always the variable
					String var = ((CryptSLObject) pred.getParameters().get(0)).getVarName();
					for (CallSiteWithParamIndex cs : parsAndVals.keySet()) {
						if (cs.getVarName().equals(var)) {
							errors.add(new ImpreciseValueExtractionError(pred, cs.stmt(), classSpec.getRule()));
						}
					}
					return null;
				default:
					return null;
			}
		}
	}
	private class ComparisonConstraint extends EvaluatableConstraint{
		final CryptSLComparisonConstraint comp;
		public ComparisonConstraint(CryptSLComparisonConstraint c) {
			this.comp = c;
		}
		@Override
		void evaluate() {
			Map<Integer, Statement> left = evaluate(comp.getLeft());
			Map<Integer, Statement> right = evaluate(comp.getRight());

			for (Entry<Integer, Statement> entry : right.entrySet()) {
				if (entry.getKey() == Integer.MIN_VALUE) {
					errors.add(new ConstraintError(entry.getValue(), classSpec.getRule(), null, comp, parsAndVals));
					return;
				}
			}

			for (Entry<Integer, Statement> leftie : left.entrySet()) {
				if (leftie.getKey() == Integer.MIN_VALUE) {
					errors.add(new ConstraintError(leftie.getValue(), classSpec.getRule(), null, comp, parsAndVals));
					return;
				}
				for (Entry<Integer, Statement> rightie : right.entrySet()) {

					boolean cons = true;
					switch (comp.getOperator()) {
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
						errors.add(new ConstraintError(leftie.getValue(), classSpec.getRule(), null, comp, parsAndVals));
						return;
					}
				}
			}
		}
		private Map<Integer, Statement> evaluate(CryptSLArithmeticConstraint arith) {
			Map<Integer, Statement> left = extractValueAsInt(arith.getLeft().getName(), arith);
			Map<Integer, Statement> right = extractValueAsInt(arith.getRight().getName(), arith);
			for (Entry<Integer, Statement> rightie : right.entrySet()) {
				if (rightie.getKey() == Integer.MIN_VALUE) {
					return left;
				}
			}

			Map<Integer, Statement> results = new HashMap<Integer, Statement>();
			for (Entry<Integer, Statement> leftie : left.entrySet()) {
				if (leftie.getKey() == Integer.MIN_VALUE) {
					return left;
				}

				for (Entry<Integer, Statement> rightie : right.entrySet()) {
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

		private Map<Integer, Statement> extractValueAsInt(String exp, ISLConstraint cons) {
			final HashMap<Integer, Statement> valuesInt = new HashMap<Integer, Statement>();
			try {
				//1. exp may (already) be an integer
				valuesInt.put(Integer.parseInt(exp), null);
				return valuesInt;
			} catch (NumberFormatException ex) {
				//2. If not, it's a variable name.
				//Get value of variable left from map
				final Entry<List<String>, Statement> valueCollection = extractValueAsString(exp, cons);
				if (valueCollection.getKey().isEmpty()) {
					return valuesInt;
				}
				try {
					for (String value : valueCollection.getKey()) {
						valuesInt.put(Integer.parseInt(value), valueCollection.getValue());
					}
				} catch (NumberFormatException ex1) {
					//If that does not work either, I'm out of ideas ...
					throw new RuntimeException();
				}
				return valuesInt;
			}
		}

	}

	private class ValueConstraint extends EvaluatableConstraint{
		final CryptSLValueConstraint valueCons;
		public ValueConstraint(CryptSLValueConstraint c) {
			this.valueCons = c;
		}
		@Override
		void evaluate() {
			CryptSLObject var = valueCons.getVar();
			final List<Entry<String, Statement>> vals = getValFromVar(var, valueCons);
			if (vals.isEmpty()) {
				//TODO: Check whether this works as desired
				return;
			}
			for (Entry<String, Statement> val : vals) {
				if (!valueCons.getValueRange().contains(val.getKey())) {
					errors.add(new ConstraintError(val.getValue(), classSpec.getRule(), null, valueCons, parsAndVals));
					return;
				}
			}
			return;
		}

		private List<Entry<String, Statement>> getValFromVar(CryptSLObject var, ISLConstraint cons) {
			final String varName = var.getVarName();
			final Entry<List<String>, Statement> valueCollection = extractValueAsString(varName, cons);
			List<Entry<String, Statement>> vals = new ArrayList<Entry<String, Statement>>();
			if (valueCollection.getKey().isEmpty()) {
				return vals;
			}
			for (String val : valueCollection.getKey()) {
				CryptSLSplitter splitter = var.getSplitter();
				final Statement location = valueCollection.getValue();
				if (splitter != null) {
					int ind = splitter.getIndex();
					String splitElement = splitter.getSplitter();
					if (ind > 0) {
						String[] splits = val.split(splitElement);
						if (splits.length > ind) {
							vals.add(new AbstractMap.SimpleEntry<String, Statement>(splits[ind], location));
						} else {
							vals.add(new AbstractMap.SimpleEntry<String, Statement>("", location));
						}
					} else {
						vals.add(new AbstractMap.SimpleEntry<String, Statement>(val.split(splitElement)[ind], location));
					}
				} else {
					vals.add(new AbstractMap.SimpleEntry<String, Statement>(val, location));
				}
			}
			return vals;
		}

	}
	public abstract class EvaluatableConstraint{
		Set<AbstractError> errors = Sets.newHashSet();
		EvaluatableConstraint origin;
		abstract void evaluate();
		protected Collection<AbstractError> getErrors(){
			return errors;
		};
		
		boolean hasErrors(){
			return !errors.isEmpty();
		}

		protected Entry<List<String>, Statement> extractValueAsString(String varName, ISLConstraint cons) {
			List<String> varVal = Lists.newArrayList();
			Statement witness = null;
			for (CallSiteWithParamIndex wrappedCallSite : parsAndVals.keySet()) {
				final Stmt callSite = wrappedCallSite.stmt().getUnit().get();

				for (ExtractedValue wrappedAllocSite : parsAndVals.get(wrappedCallSite)) {
					final Stmt allocSite = wrappedAllocSite.stmt().getUnit().get();

					if (wrappedCallSite.getVarName().equals(varName)) {
						if (callSite.equals(allocSite)) {
							varVal.add(retrieveConstantFromValue(callSite.getInvokeExpr().getArg(wrappedCallSite.getIndex())));
							witness = wrappedCallSite.stmt();
						} else if (allocSite instanceof AssignStmt) {
							final Value rightSide = ((AssignStmt) allocSite).getRightOp();
							if (rightSide instanceof Constant) {
								varVal.add(retrieveConstantFromValue(rightSide));
								witness = wrappedCallSite.stmt();
							} else {
								errors.add(new ImpreciseValueExtractionError(cons, wrappedCallSite.stmt(), classSpec.getRule()));
							}
						}
					}
				}
			}
			return new AbstractMap.SimpleEntry<List<String>, Statement>(varVal, witness);
		}
	}
}