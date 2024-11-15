package crypto.analysis.errors;

import boomerang.scene.Statement;
import boomerang.scene.Type;
import boomerang.scene.Val;
import com.google.common.base.CharMatcher;
import crypto.analysis.IAnalysisSeed;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.rules.CrySLArithmeticConstraint;
import crypto.rules.CrySLComparisonConstraint;
import crypto.rules.CrySLComparisonConstraint.CompOp;
import crypto.rules.CrySLConstraint;
import crypto.rules.CrySLRule;
import crypto.rules.CrySLSplitter;
import crypto.rules.CrySLValueConstraint;
import crypto.rules.ISLConstraint;
import java.util.Arrays;
import java.util.Collection;

public class ConstraintError extends AbstractError {

    private final CallSiteWithExtractedValue callSite;
    private final ISLConstraint violatedConstraint;

    public ConstraintError(
            IAnalysisSeed seed,
            CallSiteWithExtractedValue cs,
            CrySLRule rule,
            ISLConstraint constraint) {
        super(seed, cs.getCallSiteWithParam().stmt(), rule);

        this.callSite = cs;
        this.violatedConstraint = constraint;
    }

    public ISLConstraint getViolatedConstraint() {
        return violatedConstraint;
    }

    public CallSiteWithExtractedValue getCallSiteWithExtractedValue() {
        return callSite;
    }

    @Override
    public String toErrorMarkerString() {
        return callSite.toString() + evaluateBrokenConstraint(violatedConstraint);
    }

    private String evaluateBrokenConstraint(final ISLConstraint constraint) {
        StringBuilder msg = new StringBuilder();
        if (constraint instanceof CrySLValueConstraint) {
            return evaluateValueConstraint((CrySLValueConstraint) constraint);
        } else if (constraint instanceof CrySLArithmeticConstraint) {
            final CrySLArithmeticConstraint brokenArthConstraint =
                    (CrySLArithmeticConstraint) constraint;
            msg.append(brokenArthConstraint.getLeft());
            msg.append(" ");
            msg.append(brokenArthConstraint.getOperator());
            msg.append(" ");
            msg.append(brokenArthConstraint.getRight());
        } else if (constraint instanceof CrySLComparisonConstraint) {
            final CrySLComparisonConstraint brokenCompCons = (CrySLComparisonConstraint) constraint;
            msg.append(" Variable ");
            msg.append(brokenCompCons.getLeft().getLeft().getName());
            msg.append(" must be ");
            msg.append(evaluateCompOp(brokenCompCons.getOperator()));
            msg.append(" ");
            msg.append(brokenCompCons.getRight().getLeft().getName());
        } else if (constraint instanceof CrySLConstraint) {
            final CrySLConstraint crySLConstraint = (CrySLConstraint) constraint;
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
            Statement stmt = callSite.getCallSiteWithParam().stmt();
            String[] splitValues = new String[] {""};
            if (stmt.isAssign()) {
                Val rightSide = stmt.getRightOp();
                if (rightSide.isConstant()) {
                    splitValues =
                            filterQuotes(rightSide.getVariableName()).split(splitter.getSplitter());
                } else if (stmt.containsInvokeExpr()) {
                    Collection<Val> parameters = stmt.getInvokeExpr().getArgs();

                    for (Val parameter : parameters) {
                        Type parameterType = parameter.getType();
                        String javaType = brokenConstraint.getVar().getJavaType();

                        if (parameterType.toString().equals(javaType)) {
                            splitValues =
                                    filterQuotes(parameter.getVariableName())
                                            .split(splitter.getSplitter());
                            break;
                        }
                    }
                }
            } else {
                // splitValues =
                // filterQuotes(stmt.getInvokeExpr().getUseBoxes().get(0).getValue().toString()).split(splitter.getSplitter());
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
        return Arrays.hashCode(new Object[] {super.hashCode(), callSite, violatedConstraint});
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;

        ConstraintError other = (ConstraintError) obj;
        if (callSite == null) {
            if (other.getCallSiteWithExtractedValue() != null) return false;
        } else if (!callSite.equals(other.getCallSiteWithExtractedValue())) {
            return false;
        }

        if (violatedConstraint == null) {
            if (other.getViolatedConstraint() != null) return false;
        } else if (!violatedConstraint.equals(other.getViolatedConstraint())) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "ConstraintError: " + toErrorMarkerString();
    }
}
