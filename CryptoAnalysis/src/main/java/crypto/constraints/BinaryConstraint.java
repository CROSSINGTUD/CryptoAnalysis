package crypto.constraints;

import boomerang.scene.Statement;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.errors.ConstraintError;
import crypto.extractparameter.ParameterWithExtractedValues;
import crysl.rule.CrySLConstraint;
import crysl.rule.ISLConstraint;
import java.util.Collection;

public class BinaryConstraint extends EvaluableConstraint {

    private final CrySLConstraint constraint;
    private final EvaluableConstraint leftConstraint;
    private final EvaluableConstraint rightConstraint;

    protected BinaryConstraint(
            AnalysisSeedWithSpecification seed,
            CrySLConstraint constraint,
            Collection<Statement> statements,
            Collection<ParameterWithExtractedValues> extractedValues) {
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

        if (leftResult == EvaluationResult.ConstraintIsNotRelevant
                && rightResult == EvaluationResult.ConstraintIsNotRelevant) {
            // return EvaluationResult.ConstraintIsNotRelevant;
        }

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

        ConstraintError error = new ConstraintError(this, seed, seed.getSpecification());
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
