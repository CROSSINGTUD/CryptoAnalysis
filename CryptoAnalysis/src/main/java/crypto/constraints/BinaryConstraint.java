/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.constraints;

import boomerang.scope.Statement;
import com.google.common.collect.Multimap;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.errors.AbstractConstraintsError;
import crypto.analysis.errors.ConstraintError;
import crypto.constraints.violations.ViolatedBinaryConstraint;
import crypto.constraints.violations.ViolatedConstraint;
import crypto.extractparameter.ParameterWithExtractedValues;
import crysl.rule.CrySLConstraint;
import java.util.Collection;
import java.util.HashSet;

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
    public CrySLConstraint getConstraint() {
        return constraint;
    }

    @Override
    public EvaluationResult evaluate() {
        EvaluationResult leftResult = leftConstraint.evaluate();
        EvaluationResult rightResult = rightConstraint.evaluate();

        Collection<Statement> violatingStatements = new HashSet<>();

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

                // Constraint is not satisfied -> Only consider right sides for implications
                for (AbstractConstraintsError error : rightConstraint.getErrors()) {
                    violatingStatements.add(error.getErrorStatement());
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

                // Constraint is not satisfied
                if (leftResult == EvaluationResult.ConstraintIsNotSatisfied) {
                    for (AbstractConstraintsError error : leftConstraint.getErrors()) {
                        violatingStatements.add(error.getErrorStatement());
                    }
                }

                if (rightResult == EvaluationResult.ConstraintIsNotSatisfied) {
                    for (AbstractConstraintsError error : rightConstraint.getErrors()) {
                        violatingStatements.add(error.getErrorStatement());
                    }
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

                // Constraint is not satisfied
                if (leftResult == EvaluationResult.ConstraintIsNotSatisfied) {
                    for (AbstractConstraintsError error : leftConstraint.getErrors()) {
                        violatingStatements.add(error.getErrorStatement());
                    }
                }

                if (rightResult == EvaluationResult.ConstraintIsNotSatisfied) {
                    for (AbstractConstraintsError error : rightConstraint.getErrors()) {
                        violatingStatements.add(error.getErrorStatement());
                    }
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

        for (Statement statement : violatingStatements) {
            ViolatedConstraint violatedConstraint = new ViolatedBinaryConstraint(this);
            ConstraintError error =
                    new ConstraintError(
                            seed, statement, seed.getSpecification(), this, violatedConstraint);
            errors.add(error);
        }

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
