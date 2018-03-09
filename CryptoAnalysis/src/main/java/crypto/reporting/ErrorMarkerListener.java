package crypto.reporting;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import com.beust.jcommander.internal.Sets;
import com.google.common.base.Joiner;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

import boomerang.BackwardQuery;
import boomerang.Query;
import boomerang.WeightedBoomerang;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.CrySLAnalysisListener;
import crypto.analysis.EnsuredCryptSLPredicate;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.LocatedCrySLPredicate;
import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.ErrorVisitor;
import crypto.analysis.errors.ForbiddenMethodError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import crypto.rules.CryptSLArithmeticConstraint;
import crypto.rules.CryptSLComparisonConstraint;
import crypto.rules.CryptSLComparisonConstraint.CompOp;
import crypto.rules.CryptSLConstraint;
import crypto.rules.CryptSLObject;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLValueConstraint;
import crypto.typestate.CallSiteWithParamIndex;
import soot.ArrayType;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.ValueBox;
import soot.jimple.Constant;
import soot.jimple.Stmt;
import soot.jimple.internal.JAssignStmt;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;
import typestate.interfaces.ISLConstraint;

/**
 * This listener is notified of any misuses the analysis finds.
 *
 * @author Stefan Krueger
 * @author Johannes Spaeth
 *
 */
public class ErrorMarkerListener extends CrySLAnalysisListener {

	protected final Table<SootClass, SootMethod, Set<ErrorMarker>> errorMarkers = HashBasedTable.create(); 

	private void addMarker(Statement location, String string) {
		SootMethod method = location.getMethod();
		SootClass sootClass = method.getDeclaringClass();
		
		Set<ErrorMarker> set = errorMarkers.get(sootClass, method);
		if(set == null){
			set = Sets.newHashSet();
		}
		set.add(new ErrorMarker(location, string));
		errorMarkers.put(sootClass, method, set);
	}
	
	@Override
	public void reportError(AbstractError error) {
		error.accept(new ErrorVisitor(){

			@Override
			public void visit(ConstraintError constraintError) {
				addMarker(constraintError.getErrorLocation(), evaluateBrokenConstraint(constraintError.getBrokenConstraint(), constraintError.getErrorLocation()));
			}

			@Override
			public void visit(ForbiddenMethodError forbiddenMethodError) {
				final StringBuilder msg = new StringBuilder();
				msg.append("Call to forbidden method ");
				msg.append(forbiddenMethodError.getCalledMethod().getSubSignature());
				if (!forbiddenMethodError.getAlternatives().isEmpty()) {
					msg.append(". Instead, call to method ");
					Collection<String> subSignatures = toSubSignatures(forbiddenMethodError.getAlternatives());
					msg.append(Joiner.on(", ").join(subSignatures));
					msg.append(".");
				}
				addMarker(forbiddenMethodError.getErrorLocation(), msg.toString());
			}

			@Override
			public void visit(IncompleteOperationError incompleteOperationError) {
				Statement location = incompleteOperationError.getErrorLocation();
				Val errorVariable = incompleteOperationError.getErrorVariable();
				Collection<SootMethod> expectedCalls = incompleteOperationError.getExpectedMethodCalls();
				final StringBuilder msg = new StringBuilder();
				msg.append("Operation with ");
				final String type = errorVariable.value().getType().toString();
				msg.append(type.substring(type.lastIndexOf('.') + 1));
				msg.append(" object not completed. Expected call to ");
				msg.append(Joiner.on(" or ").join(expectedCalls));
				addMarker(location, msg.toString());
			}

			@Override
			public void visit(TypestateError typestateError) {
				Statement location = typestateError.getErrorLocation();
				Collection<SootMethod> expectedCalls = typestateError.getExpectedMethodCalls();
				final StringBuilder msg = new StringBuilder();
				msg.append("Unexpected call to method ");
				msg.append(getCalledMethodName(location));
				msg.append(". Expect a call to one of the following methods ");
				final Set<String> altMethods = new HashSet<>();
				for (final SootMethod expectedCall : expectedCalls) {
					altMethods.add(expectedCall.getName());
				}
				msg.append(Joiner.on(",").join(altMethods));
				addMarker(location, msg.toString());
			}

			@Override
			public void visit(RequiredPredicateError predicateError) {
				// TODO Auto-generated method stub
				
			}});
	}
	
	protected Collection<String> toSubSignatures(Collection<SootMethod> methods) {
		Set<String> subSignatures = Sets.newHashSet();
		for(SootMethod m : methods){
			subSignatures.add(m.getSubSignature());
		}
		return subSignatures;
	}


	private String evaluateBrokenConstraint(final ISLConstraint brokenConstraint, Statement location) {
		StringBuilder msg = new StringBuilder();
		if (brokenConstraint instanceof CryptSLPredicate) {
			CryptSLPredicate brokenPred = (CryptSLPredicate) brokenConstraint;

			switch (brokenPred.getPredName()) {
				case "neverTypeOf":

					if (location.getUnit().get() instanceof JAssignStmt) {
						msg.append("Variable ");
						msg.append(((JAssignStmt) location.getUnit().get()).getLeftOp());
					} else {
						msg.append("This variable");
					}
					msg.append(" must not be of type ");
					msg.append(brokenPred.getParameters().get(1).getName());
					msg.append(".");
					break;
			}
		} else if (brokenConstraint instanceof CryptSLValueConstraint) {
			return evaluateValueConstraint((CryptSLValueConstraint) brokenConstraint);
		} else if (brokenConstraint instanceof CryptSLArithmeticConstraint) {
			final CryptSLArithmeticConstraint brokenArthConstraint = (CryptSLArithmeticConstraint) brokenConstraint;
			msg.append(brokenArthConstraint.getLeft());
			msg.append(" ");
			msg.append(brokenArthConstraint.getOperator());
			msg.append(" ");
			msg.append(brokenArthConstraint.getRight());
		} else if (brokenConstraint instanceof CryptSLComparisonConstraint) {
			final CryptSLComparisonConstraint brokenCompCons = (CryptSLComparisonConstraint) brokenConstraint;
			msg.append("Variable ");
			msg.append(brokenCompCons.getLeft().getLeft().getName());
			msg.append("must be ");
			msg.append(evaluateCompOp(brokenCompCons.getOperator()));
			msg.append(brokenCompCons.getRight().getLeft().getName());
		} else if (brokenConstraint instanceof CryptSLConstraint) {
			final CryptSLConstraint cryptSLConstraint = (CryptSLConstraint) brokenConstraint;
			final CryptSLValueConstraint leftSide = (CryptSLValueConstraint) cryptSLConstraint.getLeft();
			final CryptSLValueConstraint rightSide = (CryptSLValueConstraint) cryptSLConstraint.getRight();
			switch (cryptSLConstraint.getOperator()) {
				case and:
					msg.append(evaluateValueConstraint(leftSide));
					msg.append(" or ");
					msg.append(evaluateValueConstraint(rightSide));
					break;
				case implies:
					msg.append(evaluateValueConstraint(rightSide));
					break;
				case or:
					msg.append(evaluateValueConstraint(leftSide));
					msg.append(" and ");
					msg.append(evaluateValueConstraint(rightSide));
					break;
				default:
					break;
			}

		}
		return msg.toString();
	}

	private String evaluateCompOp(CompOp operator) {
		switch (operator) {
			case ge:
				return " at least ";
			case g:
				return " greater than ";
			case l:
				return " lesser than ";
			case le:
				return " at most ";
			default:
				return "equal to";
		}
	}

	private String evaluateValueConstraint(final CryptSLValueConstraint brokenConstraint) {
		StringBuilder msg = new StringBuilder();
		msg.append(brokenConstraint.getVarName());
		msg.append(" should be any of {");
		for (final String val : brokenConstraint.getValueRange()) {
			msg.append(val);
			msg.append(", ");
		}
		msg.delete(msg.length() - 2, msg.length());
		return msg.append('}').toString();
	}

	@Override
	public void missingPredicates(final AnalysisSeedWithSpecification spec, final Set<CryptSLPredicate> missingPred) {
		for (final CryptSLPredicate pred : missingPred) {
			final StringBuilder msg = new StringBuilder();
			msg.append("Predicate ");
			msg.append(pred.getPredName());
			msg.append(" is missing for ");
			Statement stmt = null;
			if (pred instanceof LocatedCrySLPredicate) {
				stmt = ((LocatedCrySLPredicate) pred).getLocation();
				for (ValueBox parameter : stmt.getUnit().get().getInvokeExpr().getUseBoxes()) {
					Value value = parameter.getValue();
					if (!(value instanceof Constant)) {
						boolean neverFound = true;
						for (CallSiteWithParamIndex a : spec.getExtractedValues().keySet()) {
							if (a.getVarName().equals(pred.getParameters().get(0).getName())) {
								if (a.fact().value().getType().equals(parameter.getValue().getType())) {
									String varName = parameter.getValue().toString();
									if (varName.matches("\\$[a-z][0-9]+")) {
										msg.append("object of type ");
										msg.append(parameter.getValue().getType().toQuotedString());
										neverFound = false;
									} else {
										msg.append("variable ");
										msg.append(varName);
										neverFound = false;
									}
									break;
								}
							}
						}
						if (neverFound) {
							Type valueType = value.getType();
							String type = (valueType instanceof ArrayType) ? ((ArrayType) valueType).getArrayElementType().toQuotedString() : valueType.toQuotedString();
							if (((CryptSLObject) pred.getParameters().get(0)).getJavaType().equals(type)) {
								String varName = parameter.getValue().toString();
								if (varName.matches("\\$[a-z][0-9]+")) {
									msg.append("object of type ");
									msg.append(parameter.getValue().getType().toQuotedString());
									neverFound = false;
								} else {
									msg.append("variable ");
									msg.append(varName);
									neverFound = false;
								}
								break;
							}
						}
					}
				}
			} else {
				stmt = spec.stmt();
				msg.append("variable ");
				msg.append(spec.var().value().toString());
			}
			msg.append(".");
			addMarker(stmt, msg.toString());
		}
	}

	@Override
	public void predicateContradiction(final Node<Statement, Val> location, final Entry<CryptSLPredicate, CryptSLPredicate> arg1) {
		addMarker(location.stmt(), "Predicate mismatch");
	}

	private String getCalledMethodName(Statement location) {
		Stmt stmt = location.getUnit().get();
		if(stmt.containsInvokeExpr()){
			return stmt.getInvokeExpr().getMethod().getName();
		}
		return stmt.toString();
	}


	@Override
	public void unevaluableConstraint(AnalysisSeedWithSpecification seed, ISLConstraint con, Statement location) {
		final StringBuilder msg = new StringBuilder();

		msg.append("Constraint ");
		msg.append(con);
		msg.append(" could not be evaluted due to insufficient information.");
		addMarker(location, msg.toString());
	}

	@Override
	public void afterAnalysis() {
		// Nothing
	}

	@Override
	public void afterConstraintCheck(final AnalysisSeedWithSpecification arg0) {
		// nothing
	}

	@Override
	public void afterPredicateCheck(final AnalysisSeedWithSpecification arg0) {
		// Nothing
	}

	@Override
	public void beforeAnalysis() {
		// Nothing

	}

	@Override
	public void beforeConstraintCheck(final AnalysisSeedWithSpecification arg0) {
		// Nothing
	}

	@Override
	public void beforePredicateCheck(final AnalysisSeedWithSpecification arg0) {
		// Nothing
	}

	@Override
	public void boomerangQueryFinished(final Query arg0, final BackwardQuery arg1) {
		// Nothing
	}

	@Override
	public void boomerangQueryStarted(final Query arg0, final BackwardQuery arg1) {
		// Nothing
	}

	@Override
	public void checkedConstraints(final AnalysisSeedWithSpecification arg0, final Collection<ISLConstraint> arg1) {
		// Nothing
	}

	@Override
	public void collectedValues(final AnalysisSeedWithSpecification arg0, final Multimap<CallSiteWithParamIndex, Statement> arg1) {
		// Nothing
	}

	@Override
	public void discoveredSeed(final IAnalysisSeed arg0) {
		// Nothing
	}

	@Override
	public void ensuredPredicates(final Table<Statement, Val, Set<EnsuredCryptSLPredicate>> arg0, final Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> arg1, final Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> arg2) {
		// Nothing
	}

	@Override
	public void onSeedFinished(final IAnalysisSeed arg0, final WeightedBoomerang<TransitionFunction> arg1) {
		// Nothing
	}

	@Override
	public void onSeedTimeout(final Node<Statement, Val> arg0) {
		//Nothing
	}

	@Override
	public void seedStarted(final IAnalysisSeed arg0) {
		// Nothing
	}
}

