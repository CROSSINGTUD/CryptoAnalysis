package crypto.constraints;

import boomerang.scene.Statement;
import com.google.common.collect.Multimap;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.errors.ConstraintError;
import crypto.constraints.violations.IViolatedConstraint;
import crypto.constraints.violations.ViolatedBinaryConstraint;
import crypto.extractparameter.ParameterWithExtractedValues;
import crysl.rule.CrySLConstraint;
import crysl.rule.ISLConstraint;
import java.util.Collection;

/**
 * A binary constraint represents two constraints 'left' and 'right' that are connected by a logical
 * operator. During the evaluation, both sites are evaluated individually and, depending on the
 * operator, evaluated by the basic logic rules. For example, an implication 'A implies B' evaluates
 * to false if A is true and B is false, otherwise it evaluates to true. Currently, we have
 * implications, conjunctions and disjunctions.
 */
public class BinaryConstraint extends EvaluableConstraint {

    private final CrySLConstraint constraint;
    private final EvaluableConstraint leftConstraint;
    private final EvaluableConstraint rightConstraint;

    protected BinaryConstraint(
            AnalysisSeedWithSpecification seed,
            CrySLConstraint constraint,
            Collection<Statement> statements,
            Multimap<Statement, ParameterWithExtractedValues> extractedValues) {
        super(seed, statements, extractedValues);

        this.constraint = constraint;
        this.leftConstraint =
                EvaluableConstraint.getInstance(
                        seed, constraint.getLeft(), statements, extractedValues);
        this.rightConstraint =
                EvaluableConstraint.getInstance(
                        seed, constraint.getRight(), statements, extractedValues);
    }

    @Override
    public ISLConstraint getConstraint() {
        return constraint;
    }

    @Override
    public EvaluationResult evaluate() {
        EvaluationResult leftResult = leftConstraint.evaluate();
        EvaluationResult rightResult = rightConstraint.evaluate();

        switch (constraint.getOperator()) {
            case implies -> {
                if (leftResult == EvaluationResult.ConstraintIsNotSatisfied) {
                    if (rightResult == EvaluationResult.ConstraintIsNotRelevant) {
                        return EvaluationResult.ConstraintIsNotRelevant;
                    }

                    return EvaluationResult.ConstraintIsSatisfied;
                }

                if (leftResult == EvaluationResult.ConstraintIsNotRelevant) {
                    if (rightResult == EvaluationResult.ConstraintIsNotRelevant) {
                        return EvaluationResult.ConstraintIsNotRelevant;
                    }

                    return EvaluationResult.ConstraintIsSatisfied;
                }

                if (leftResult == EvaluationResult.ConstraintIsSatisfied) {
                    if (rightResult == EvaluationResult.ConstraintIsNotRelevant) {
                        return EvaluationResult.ConstraintIsNotRelevant;
                    }

                    if (rightResult == EvaluationResult.ConstraintIsSatisfied) {
                        return EvaluationResult.ConstraintIsSatisfied;
                    }
                }
            }
            case or -> {
                if (leftResult == EvaluationResult.ConstraintIsNotRelevant
                        && rightResult == EvaluationResult.ConstraintIsNotRelevant) {
                    return EvaluationResult.ConstraintIsNotRelevant;
                }

                if (leftResult == EvaluationResult.ConstraintIsSatisfied) {
                    return EvaluationResult.ConstraintIsSatisfied;
                }

                if (rightResult == EvaluationResult.ConstraintIsSatisfied) {
                    return EvaluationResult.ConstraintIsSatisfied;
                }
            }
            case and -> {
                if (leftResult == EvaluationResult.ConstraintIsNotRelevant) {
                    return EvaluationResult.ConstraintIsNotRelevant;
                }

                if (rightResult == EvaluationResult.ConstraintIsNotRelevant) {
                    return EvaluationResult.ConstraintIsNotRelevant;
                }

                if (leftResult == EvaluationResult.ConstraintIsSatisfied
                        && rightResult == EvaluationResult.ConstraintIsSatisfied) {
                    return EvaluationResult.ConstraintIsSatisfied;
                }
            }
            case eq -> {
                if (leftResult == EvaluationResult.ConstraintIsNotRelevant) {
                    return EvaluationResult.ConstraintIsNotRelevant;
                }

                if (rightResult == EvaluationResult.ConstraintIsNotRelevant) {
                    return EvaluationResult.ConstraintIsNotRelevant;
                }

                if (leftResult == EvaluationResult.ConstraintIsSatisfied
                        && rightResult == EvaluationResult.ConstraintIsSatisfied) {
                    return EvaluationResult.ConstraintIsSatisfied;
                }

                if (leftResult == EvaluationResult.ConstraintIsNotSatisfied
                        && rightResult == EvaluationResult.ConstraintIsNotSatisfied) {
                    return EvaluationResult.ConstraintIsSatisfied;
                }
            }
        }

        IViolatedConstraint violatedConstraint = new ViolatedBinaryConstraint(this);
        ConstraintError error =
                new ConstraintError(
                        seed, seed.getOrigin(), seed.getSpecification(), this, violatedConstraint);
        errors.add(error);

        return EvaluationResult.ConstraintIsNotSatisfied;
    }

    public EvaluableConstraint getLeftConstraint() {
        return leftConstraint;
    }

    public EvaluableConstraint getRightConstraint() {
        return rightConstraint;
    }

    @Override
    public String toString() {
        switch (constraint.getOperator()) {
            case implies -> {
                return leftConstraint + " => " + rightConstraint;
            }
            case or -> {
                return leftConstraint + " || " + rightConstraint;
            }
            case and -> {
                return leftConstraint + " && " + rightConstraint;
            }
            case eq -> {
                return leftConstraint + " <=> " + rightConstraint;
            }
            default -> {
                return constraint.toString();
            }
        }
    }
}
