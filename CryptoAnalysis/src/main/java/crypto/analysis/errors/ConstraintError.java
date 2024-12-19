package crypto.analysis.errors;

import boomerang.scene.Statement;
import boomerang.scene.Type;
import boomerang.scene.Val;
import com.google.common.base.CharMatcher;
import crypto.analysis.IAnalysisSeed;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crysl.rule.CrySLArithmeticConstraint;
import crysl.rule.CrySLComparisonConstraint;
import crysl.rule.CrySLConstraint;
import crysl.rule.CrySLRule;
import crysl.rule.CrySLSplitter;
import crysl.rule.CrySLValueConstraint;
import crysl.rule.ISLConstraint;
import java.util.Collection;
import java.util.Objects;

public class ConstraintError extends AbstractConstraintsError {

    private final CallSiteWithExtractedValue callSite;
    private final ISLConstraint violatedConstraint;

    public ConstraintError(
            IAnalysisSeed seed,
            CallSiteWithExtractedValue cs,
            CrySLRule rule,
            ISLConstraint constraint) {
        super(seed, cs.callSiteWithParam().statement(), rule);

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
        } else if (constraint instanceof CrySLArithmeticConstraint brokenArithConstraint) {
            msg.append(brokenArithConstraint.getLeft());
            msg.append(" ");
            msg.append(brokenArithConstraint.getOperator());
            msg.append(" ");
            msg.append(brokenArithConstraint.getRight());
        } else if (constraint instanceof CrySLComparisonConstraint brokenCompCons) {
            msg.append(" Variable ");
            msg.append(brokenCompCons.getLeft().getLeft().getName());
            msg.append(" must be ");
            msg.append(evaluateCompOp(brokenCompCons.getOperator()));
            msg.append(" ");
            msg.append(brokenCompCons.getRight().getLeft().getName());
        } else if (constraint instanceof CrySLConstraint crySLConstraint) {
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

    private String evaluateCompOp(CrySLComparisonConstraint.CompOp operator) {
        return switch (operator) {
            case ge -> "at least";
            case g -> "greater than";
            case l -> "lesser than";
            case le -> "at most";
            case eq -> "equal to";
            case neq -> "not equal to";
        };
    }

    private String evaluateValueConstraint(final CrySLValueConstraint brokenConstraint) {
        StringBuilder msg = new StringBuilder();
        msg.append(" should be any of ");
        CrySLSplitter splitter = brokenConstraint.getVar().getSplitter();
        if (splitter != null) {
            Statement stmt = callSite.callSiteWithParam().statement();
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
        return Objects.hash(super.hashCode(), callSite, violatedConstraint);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && obj instanceof ConstraintError other
                && Objects.equals(callSite, other.getCallSiteWithExtractedValue())
                && Objects.equals(violatedConstraint, other.getViolatedConstraint());
    }

    @Override
    public String toString() {
        return "ConstraintError: " + toErrorMarkerString();
    }
}
