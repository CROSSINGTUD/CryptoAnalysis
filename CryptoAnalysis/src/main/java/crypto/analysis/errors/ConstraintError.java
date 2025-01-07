package crypto.analysis.errors;

import boomerang.scene.Statement;
import crypto.analysis.IAnalysisSeed;
import crypto.constraints.EvaluableConstraint;
import crypto.constraints.IViolatedConstraint;
import crysl.rule.CrySLRule;
import java.util.Objects;

public class ConstraintError extends AbstractConstraintsError {

    private final EvaluableConstraint evaluableConstraint;
    private final IViolatedConstraint violatedConstraint;

    /**
     * Constructs a ConstraintError for a violated constraint
     *
     * @param seed the seed with the violated predicate
     * @param statement the statement of the violation
     * @param rule the rule containing the violated predicate
     * @param evaluableConstraint the evaluated constraint
     * @param violatedConstraint the violated constraint
     */
    public ConstraintError(
            IAnalysisSeed seed,
            Statement statement,
            CrySLRule rule,
            EvaluableConstraint evaluableConstraint,
            IViolatedConstraint violatedConstraint) {
        super(seed, statement, rule);

        this.evaluableConstraint = evaluableConstraint;
        this.violatedConstraint = violatedConstraint;
    }

    public IViolatedConstraint getViolatedConstraint() {
        return violatedConstraint;
    }

    @Override
    public String toErrorMarkerString() {
        return "Constraint "
                + evaluableConstraint
                + " on object "
                + getSeed().getFact()
                + " is violated due to the following reasons:";
    }

    /*private String evaluateBrokenConstraint(final ISLConstraint constraint) {
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
    }*/

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), violatedConstraint);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && obj instanceof ConstraintError other
                && Objects.equals(violatedConstraint, other.violatedConstraint);
    }

    @Override
    public String toString() {
        return "ConstraintError: " + toErrorMarkerString();
    }
}
