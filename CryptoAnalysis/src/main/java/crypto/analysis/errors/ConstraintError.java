package crypto.analysis.errors;

import java.util.List;
import java.util.Set;

import com.google.common.base.CharMatcher;

import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.analysis.IAnalysisSeed;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CryptSLArithmeticConstraint;
import crypto.rules.CryptSLComparisonConstraint;
import crypto.rules.CryptSLComparisonConstraint.CompOp;
import crypto.rules.CryptSLConstraint;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLRule;
import crypto.rules.CryptSLSplitter;
import crypto.rules.CryptSLValueConstraint;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.Stmt;
import soot.jimple.internal.AbstractInvokeExpr;
import sync.pds.solver.nodes.Node;

public class ConstraintError extends ErrorWithObjectAllocation{

	private ISLConstraint brokenConstraint;
	private CallSiteWithExtractedValue callSiteWithParamIndex;

	public ConstraintError(CallSiteWithExtractedValue cs,  CryptSLRule rule, IAnalysisSeed objectLocation, ISLConstraint con) {
		super(cs.getCallSite().stmt(), rule, objectLocation);
		this.callSiteWithParamIndex = cs;
		this.brokenConstraint = con;
	}
	
	public ISLConstraint getBrokenConstraint() {
		return brokenConstraint;
	}

	public void accept(ErrorVisitor visitor){
		visitor.visit(this);
	}
	
	@Override
	public Set<Node<Statement, Val>> getDataFlowPath() {
		return callSiteWithParamIndex.getVal().getDataFlowPath();
	}


	public CallSiteWithExtractedValue getCallSiteWithExtractedValue() {
		return callSiteWithParamIndex;
	}

	@Override
	public String toErrorMarkerString() {
		return callSiteWithParamIndex.toString() + evaluateBrokenConstraint(brokenConstraint);
	}
	
	

	private String evaluateBrokenConstraint(final ISLConstraint brokenConstraint) {
		StringBuilder msg = new StringBuilder();
		if (brokenConstraint instanceof CryptSLPredicate) {

			CryptSLPredicate brokenPred = (CryptSLPredicate) brokenConstraint;

			switch (brokenPred.getPredName()) {
				case "neverTypeOf":
					msg.append(" should never be of type ");
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
			final ISLConstraint leftSide = cryptSLConstraint.getLeft();
			final ISLConstraint rightSide = cryptSLConstraint.getRight();
			switch (cryptSLConstraint.getOperator()) {
				case and:
					msg.append(evaluateBrokenConstraint(leftSide));
					msg.append(" or ");
					msg.append(evaluateBrokenConstraint(rightSide));
					break;
				case implies:
					msg.append(evaluateBrokenConstraint(rightSide));
					break;
				case or:
					msg.append(evaluateBrokenConstraint(leftSide));
					msg.append(" and ");
					msg.append(evaluateBrokenConstraint(rightSide));
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
		msg.append(" should be any of ");
		CryptSLSplitter splitter = brokenConstraint.getVar().getSplitter();
		if (splitter != null) {
			Stmt stmt = callSiteWithParamIndex.getVal().stmt().getUnit().get();
			String[] splitValues = new String[] { "" };
			if (stmt instanceof AssignStmt) {
				Value rightSide = ((AssignStmt) stmt).getRightOp();
				if (rightSide instanceof Constant) {
					splitValues = filterQuotes(rightSide.toString()).split(splitter.getSplitter());
				} else if (rightSide instanceof AbstractInvokeExpr) {
					List<Value> args = ((AbstractInvokeExpr) rightSide).getArgs();
					for (Value arg : args) {
						if (arg.getType().toQuotedString().equals(brokenConstraint.getVar().getJavaType())) {
							splitValues = filterQuotes(arg.toString()).split(splitter.getSplitter());
							break;
						}
					}
				}
			} else {
				splitValues = filterQuotes(stmt.getInvokeExpr().getUseBoxes().get(0).getValue().toString()).split(splitter.getSplitter());
			}
			if (splitValues.length >= splitter.getIndex()) {
				for (int i = 0; i < splitter.getIndex(); i++) {
					msg.append(splitValues[i]);
					msg.append(splitter.getSplitter());
				}
			}
		}
		msg.append("{");
		for (final String val : brokenConstraint.getValueRange()) {
			if (val.isEmpty()) {
				msg.append("Empty String");
			} else {
				msg.append(val);
			}
			msg.append(", ");
		}
		msg.delete(msg.length() - 2, msg.length());
		return msg.append('}').toString();
	}
	public static String filterQuotes(final String dirty) {
		return CharMatcher.anyOf("\"").removeFrom(dirty);
	}
}
