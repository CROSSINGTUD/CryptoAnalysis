package crypto.analysis.errors;

import boomerang.scene.Statement;
import boomerang.scene.Type;
import boomerang.scene.Val;
import com.google.common.base.CharMatcher;
import crypto.analysis.IAnalysisSeed;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.rules.ISLConstraint;
import crypto.rules.CrySLArithmeticConstraint;
import crypto.rules.CrySLComparisonConstraint;
import crypto.rules.CrySLComparisonConstraint.CompOp;
import crypto.rules.CrySLConstraint;
import crypto.rules.CrySLPredicate;
import crypto.rules.CrySLRule;
import crypto.rules.CrySLSplitter;
import crypto.rules.CrySLValueConstraint;

import java.util.Collection;

public class ConstraintError extends ErrorWithObjectAllocation {

	private final ISLConstraint brokenConstraint;
	private final CallSiteWithExtractedValue callSiteWithParamIndex;

	public ConstraintError(CallSiteWithExtractedValue cs,  CrySLRule rule, IAnalysisSeed objectLocation, ISLConstraint con) {
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

	public CallSiteWithExtractedValue getCallSiteWithExtractedValue() {
		return callSiteWithParamIndex;
	}

	@Override
	public String toErrorMarkerString() {
		return callSiteWithParamIndex.toString() + evaluateBrokenConstraint(brokenConstraint);
	}

	private String evaluateBrokenConstraint(final ISLConstraint brokenConstraint) {
		StringBuilder msg = new StringBuilder();
		if (brokenConstraint instanceof CrySLPredicate) {

			CrySLPredicate brokenPred = (CrySLPredicate) brokenConstraint;

			switch (brokenPred.getPredName()) {
				case "neverTypeOf":
					msg.append(" should never be of type ");
					msg.append(brokenPred.getParameters().get(0).getName());
					msg.append(".");
					break;
				case "notHardCoded":
					msg.append(" should never be hardcoded.");
					break;
			}
		} else if (brokenConstraint instanceof CrySLValueConstraint) {
			return evaluateValueConstraint((CrySLValueConstraint) brokenConstraint);
		} else if (brokenConstraint instanceof CrySLArithmeticConstraint) {
			final CrySLArithmeticConstraint brokenArthConstraint = (CrySLArithmeticConstraint) brokenConstraint;
			msg.append(brokenArthConstraint.getLeft());
			msg.append(" ");
			msg.append(brokenArthConstraint.getOperator());
			msg.append(" ");
			msg.append(brokenArthConstraint.getRight());
		} else if (brokenConstraint instanceof CrySLComparisonConstraint) {
			final CrySLComparisonConstraint brokenCompCons = (CrySLComparisonConstraint) brokenConstraint;
			msg.append(" Variable ");
			msg.append(brokenCompCons.getLeft().getLeft().getName());
			msg.append(" must be ");
			msg.append(evaluateCompOp(brokenCompCons.getOperator()));
			msg.append(" ");
			msg.append(brokenCompCons.getRight().getLeft().getName());
		} else if (brokenConstraint instanceof CrySLConstraint) {
			final CrySLConstraint crySLConstraint = (CrySLConstraint) brokenConstraint;
			final ISLConstraint leftSide = crySLConstraint.getLeft();
			final ISLConstraint rightSide = crySLConstraint.getRight();
			switch (crySLConstraint.getOperator()) {
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
				return "at least";
			case g:
				return "greater than";
			case l:
				return "lesser than";
			case le:
				return "at most";
			case eq:
				return "equal to";
			case neq:
				return "not equal to";
		}
		return "";
	}

	private String evaluateValueConstraint(final CrySLValueConstraint brokenConstraint) {
		StringBuilder msg = new StringBuilder();
		msg.append(" should be any of ");
		CrySLSplitter splitter = brokenConstraint.getVar().getSplitter();
		if (splitter != null) {
			Statement stmt = callSiteWithParamIndex.getVal().stmt();
			String[] splitValues = new String[] { "" };
			if (stmt.isAssign()) {
				Val rightSide = stmt.getRightOp();
				if (rightSide.isConstant()) {
					splitValues = filterQuotes(rightSide.getVariableName()).split(splitter.getSplitter());
				} else if (stmt.containsInvokeExpr()) {
					Collection<Val> parameters = stmt.getInvokeExpr().getArgs();

					// TODO Refactor
					for (Val parameter : parameters) {
						//if (arg.getType().toQuotedString().equals(brokenConstraint.getVar().getJavaType())) {
						Type parameterType = parameter.getType();
						String javaType = brokenConstraint.getVar().getJavaType();

						if (parameterType.toString().equals(javaType)) {
							splitValues = filterQuotes(parameter.getVariableName()).split(splitter.getSplitter());
							break;
						}
					}
				}
			} else {
				//splitValues = filterQuotes(stmt.getInvokeExpr().getUseBoxes().get(0).getValue().toString()).split(splitter.getSplitter());
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
	
	@Override
	public int hashCode() {
		final int paramIndex = callSiteWithParamIndex.getCallSite().getIndex();
		final Val parameterValue = callSiteWithParamIndex.getVal().getValue();
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + paramIndex;
		result = prime * result + ((parameterValue == null) ? 0 : parameterValue.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConstraintError other = (ConstraintError) obj;
		if (callSiteWithParamIndex.getCallSite().getIndex() != other.callSiteWithParamIndex.getCallSite().getIndex()) {
			return false;
		} 
		if (callSiteWithParamIndex.getVal().getValue() == null) {
			if (other.callSiteWithParamIndex.getVal().getValue() != null)
				return false;
		} else if (!callSiteWithParamIndex.getVal().getValue().equals(other.callSiteWithParamIndex.getVal().getValue()))
			return false;
		return true;
	}
}
