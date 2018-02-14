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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import boomerang.jimple.Statement;
import crypto.rules.CryptSLArithmeticConstraint;
import crypto.rules.CryptSLComparisonConstraint;
import crypto.rules.CryptSLConstraint;
import crypto.rules.CryptSLConstraint.LogOps;
import crypto.rules.CryptSLMethod;
import crypto.rules.CryptSLObject;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLSplitter;
import crypto.rules.CryptSLValueConstraint;
import crypto.typestate.CallSiteWithParamIndex;
import crypto.typestate.CryptSLMethodToSootMethod;
import soot.IntType;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.IntConstant;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import typestate.interfaces.ICryptSLPredicateParameter;
import typestate.interfaces.ISLConstraint;

public class ConstraintSolver {

	private final ClassSpecification classSpec;
	private final List<ISLConstraint> allConstraints;
	private final List<ISLConstraint> relConstraints;
	private final Collection<Statement> collectedCalls;
	private final Multimap<CallSiteWithParamIndex, Statement> parsAndVals;
	public final static List<String> predefinedPreds = Arrays.asList("callTo", "noCallTo", "neverTypeOf", "length");
	private final ConstraintReporter reporter;

	public ConstraintSolver(ClassSpecification spec, Multimap<CallSiteWithParamIndex, Statement> parametersToValues, Collection<Statement> collectedCalls, ConstraintReporter reporter) {
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
							relConstraints.add(new LocatedCrySLPredicate(pred, cwpi.stmt())); 
						}
					}
				} else {
					relConstraints.add(cons);
				}
			}
		}
		this.reporter = reporter;
	}

	public static Multimap<String, String> convertToStringMultiMap(Multimap<CallSiteWithParamIndex, Statement> actualValues) {
		Multimap<String, String> varVal = HashMultimap.create();
		for (CallSiteWithParamIndex callSite : actualValues.keySet()) {
			for (Statement s : actualValues.get(callSite)) {
				Stmt u = s.getUnit().get();
				Stmt cs = callSite.stmt().getUnit().get();
				if (cs.equals(u)) {
					if (u instanceof AssignStmt) {
						varVal.put(callSite.getVarName(), retrieveConstantFromValue(((AssignStmt) u).getRightOp().getUseBoxes().get(callSite.getIndex()).getValue()));
					} else {
						varVal.put(callSite.getVarName(), retrieveConstantFromValue(cs.getUseBoxes().get(callSite.getIndex()).getValue()));
					}
				} else if (u instanceof AssignStmt) {
					final Value rightSide = ((AssignStmt) u).getRightOp();
					if (rightSide instanceof Constant) {
						varVal.put(callSite.getVarName(), retrieveConstantFromValue(rightSide));
					} else {

					}
				}
			}
		}
		return varVal;
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
			Statement unit;
			
			try {
				unit = evaluate(con);
				if (unit != null) {
					fail++;
					reporter.constraintViolated(con, unit);
				}
			} catch (UnevaluableConstraintException ex) {
				reporter.unevaluableConstraint(ex.getFailedConstraint(), ex.getUnit());
			}
		}
		return fail;
	}

	private Statement evaluate(CryptSLComparisonConstraint comp) {
		Map<Integer, Statement> left = evaluate(comp.getLeft());
		Map<Integer, Statement> right = evaluate(comp.getRight());

		for (Entry<Integer, Statement> entry : right.entrySet()) {
			if (entry.getKey() == Integer.MIN_VALUE) {
				return entry.getValue();
			}
		}

		for (Entry<Integer, Statement> leftie : left.entrySet()) {
			if (leftie.getKey() == Integer.MIN_VALUE) {
				return leftie.getValue();
			}
			for (Entry<Integer, Statement> rightie : right.entrySet()) {

				boolean cons = true;
				switch (comp.getOperator()) {
					case eq:
						cons = leftie.getKey() == rightie.getKey();
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
					return leftie.getValue();
				}
			}
		}
		return null;
	}

	private Map<Integer, Statement> evaluate(CryptSLArithmeticConstraint arith) {
		Map<Integer, Statement> left = extractValueAsInt(arith.getLeft().getName());
		Map<Integer, Statement> right = extractValueAsInt(arith.getRight().getName());
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

	private Map<Integer, Statement> extractValueAsInt(String exp) {
		final HashMap<Integer, Statement> valuesInt = new HashMap<Integer, Statement>();
		try {
			//1. exp may (already) be an integer
			valuesInt.put(Integer.parseInt(exp), null);
			return valuesInt;
		} catch (NumberFormatException ex) {
			//2. If not, it's a variable name.
			//Get value of variable left from map
			final Entry<List<String>, Statement> valueCollection = extractValueAsString(exp);
			if (valueCollection.getKey().isEmpty()) {
				return valuesInt;
			}
			//			Entry<String, Statement> valueAsString = valueCollection;
			//			if (valueAsString.equals(INV)) {
			//				return new AbstractMap.SimpleEntry<Integer, Statement>(Integer.MIN_VALUE, valueAsString.getValue());
			//			}
			// and cast it to Integer
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

	private Statement evaluate(CryptSLValueConstraint valueCons) {
		CryptSLObject var = valueCons.getVar();
		final List<Entry<String, Statement>> vals = getValFromVar(var);
		if (vals.isEmpty()) {
			//TODO: Check whether this works as desired
			return null;
		}
		for (Entry<String, Statement> val : vals) {
			if (!valueCons.getValueRange().contains(val.getKey())) {
				return val.getValue();
			}
		}
		return null;
	}

	private List<Entry<String, Statement>> getValFromVar(CryptSLObject var) {
		final String varName = var.getVarName();
		final Entry<List<String>, Statement> valueCollection = extractValueAsString(varName);
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

	private Entry<List<String>, Statement> extractValueAsString(String varName) {
		List<String> varVal = Lists.newArrayList();
		Statement witness = null;
		for (CallSiteWithParamIndex callSite : parsAndVals.keySet()) {
			for (Statement currStmt : parsAndVals.get(callSite)) {
				final Unit u = callSite.stmt().getUnit().get();
				if (callSite.getVarName().equals(varName)) {
					witness = currStmt;
					if (callSite.stmt().equals(currStmt)) {
						if (u instanceof AssignStmt) {
							varVal.add(retrieveConstantFromValue(((AssignStmt) u).getRightOp().getUseBoxes().get(callSite.getIndex()).getValue()));
						} else {
							varVal.add(retrieveConstantFromValue(u.getUseBoxes().get(callSite.getIndex()).getValue()));
						}
					} else if (u instanceof AssignStmt) {
						final Value rightSide = ((AssignStmt) u).getRightOp();
						if (rightSide instanceof Constant) {
							varVal.add(retrieveConstantFromValue(rightSide));
						}
					}
				}
			}
		}

		return new AbstractMap.SimpleEntry<List<String>, Statement>(varVal, witness);
	}

	private Statement evaluate(CryptSLConstraint cons) throws UnevaluableConstraintException {
		Statement left = evaluate(cons.getLeft());
		LogOps ops = cons.getOperator();

		if (ops.equals(LogOps.implies)) {
			if (left != null) {
				return null;
			} else {
				return evaluate(cons.getRight());
			}
		} else if (ops.equals(LogOps.or)) {
			if (left == null) {
				return null;
			} else {
				return evaluate(cons.getRight());
			}
		} else if (ops.equals(LogOps.and)) {
			if (left != null) {
				return left;
			} else {
				return evaluate(cons.getRight());
			}
		} else if (ops.equals(LogOps.eq)) {
			Statement right = evaluate(cons.getRight());
			if ((left != null && right != null) || (left == null && right == null)) {
				return null;
			} else {
				return right;
			}
		}

		return left;
	}

	private Statement evaluate(CryptSLPredicate pred) throws UnevaluableConstraintException {
		String predName = pred.getPredName();
		if (predefinedPreds.contains(predName)) {
			return handlePredefinedNames(pred);
		}
		return null;
	}

	private Statement handlePredefinedNames(CryptSLPredicate pred) throws UnevaluableConstraintException {
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
							reporter.callToForbiddenMethod(classSpec, call);
							//TODO: Needs to be fixed
							return call;
						}
					}
				}
				return null;
			case "neverTypeOf":
				//pred looks as follows: neverTypeOf($varName, $type)
				// -> first parameter is always the variable
				// -> second parameter is always the type
				String varName = ((CryptSLObject) parameters.get(0)).getVarName();
				for (CallSiteWithParamIndex cs : parsAndVals.keySet()) {
					if (cs.getVarName().equals(varName)) {
						Collection<Statement> vals = parsAndVals.get(cs);
						for (Statement stmt : vals) {
							if (stmt.getUnit().get() instanceof AssignStmt) {
								Value rightAss = ((AssignStmt) stmt.getUnit().get()).getRightOp();
								if (!rightAss.getType().getEscapedName().equals(parameters.get(1).getName())) {
									return null;
								} else {
									//TODO: Fix null
									return stmt;
								}
							} else {
								return null;
							}

						}
					}
				}

				return null;
			case "length":
				//pred looks as follows: neverTypeOf($varName)
				// -> parameter is always the variable
				String var = ((CryptSLObject) pred.getParameters().get(0)).getVarName();
				for (CallSiteWithParamIndex cs : parsAndVals.keySet()) {
					if (cs.getVarName().equals(var)) {
						throw new UnevaluableConstraintException("Encountered length predicate on " + var, pred);
					}
				}
				return null;
			default:
				return null;
		}
	}

	public Statement evaluate(ISLConstraint cons) throws UnevaluableConstraintException {
		if (cons instanceof CryptSLComparisonConstraint) {
			return evaluate((CryptSLComparisonConstraint) cons);
		} else if (cons instanceof CryptSLValueConstraint) {
			return evaluate((CryptSLValueConstraint) cons);
		} else if (cons instanceof CryptSLPredicate) {
			return evaluate((CryptSLPredicate) cons);
		} else if (cons instanceof CryptSLConstraint) {
			return evaluate((CryptSLConstraint) cons);
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
}