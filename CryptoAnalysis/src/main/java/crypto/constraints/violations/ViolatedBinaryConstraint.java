/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.constraints.violations;

import crypto.constraints.BinaryConstraint;
import crypto.constraints.ComparisonConstraint;
import crypto.constraints.EvaluableConstraint;
import crypto.constraints.PredefinedPredicateConstraint;
import crypto.constraints.ValueConstraint;
import crypto.extractparameter.TransformedValue;
import java.util.Collection;

/**
 * Represents the violation of a {@link BinaryConstraint}
 *
 * @param constraint the violated constraint
 */
public record ViolatedBinaryConstraint(BinaryConstraint constraint) implements IViolatedConstraint {

    @Override
    public String getErrorMessage() {
        StringBuilder sb = new StringBuilder();
        addMessageForConstraint(sb, constraint, 0);

        return sb.toString();
    }

    private void addMessageForConstraint(
            StringBuilder sb, EvaluableConstraint constraint, int depth) {
        if (constraint instanceof ValueConstraint valueConstraint) {
            addMessageForValueConstraint(sb, valueConstraint, depth);
        } else if (constraint instanceof ComparisonConstraint comparisonConstraint) {
            addMessageForComparisonConstraint(sb, comparisonConstraint, depth);
        } else if (constraint instanceof BinaryConstraint binaryConstraint) {
            addMessageForBinaryConstraint(sb, binaryConstraint, depth);
        } else if (constraint instanceof PredefinedPredicateConstraint predicateConstraint) {

        }
    }

    private void addMessageForValueConstraint(
            StringBuilder sb, ValueConstraint constraint, int depth) {
        if (constraint.isSatisfied()) {
            Collection<TransformedValue> satisfiedValues = constraint.getSatisfiedValues();

            for (TransformedValue value : satisfiedValues) {
                sb.append("\n")
                        .append("\t".repeat(depth))
                        .append("|- Value \"")
                        .append(constraint.getVarName())
                        .append("\" satisfies the constraint @ ")
                        .append(value.getStatement())
                        .append(" @ line ")
                        .append(value.getStatement().getLineNumber());
            }
        } else if (constraint.isViolated()) {
            sb.append("\n").append("\t".repeat(depth));
            Collection<TransformedValue> violatedValues = constraint.getViolatingValues();

            if (violatedValues.isEmpty()) {
                sb.append("|- Could not extract a value for \"")
                        .append(constraint.getVarName())
                        .append("\n");
            }

            for (TransformedValue value : violatedValues) {
                sb.append("|- Value \"")
                        .append(constraint.getVarName())
                        .append("\" violates the constraint @ ")
                        .append(value.getStatement())
                        .append(" @ line ")
                        .append(value.getStatement().getLineNumber());
            }
        }
    }

    private void addMessageForComparisonConstraint(
            StringBuilder sb, ComparisonConstraint constraint, int depth) {
        // TODO Improve error message
    }

    private void addMessageForBinaryConstraint(
            StringBuilder sb, BinaryConstraint constraint, int depth) {
        EvaluableConstraint leftConstraint = constraint.getLeftConstraint();
        EvaluableConstraint rightConstraint = constraint.getRightConstraint();

        sb.append("\n")
                .append("\t".repeat(depth))
                .append("|- The left side \"")
                .append(leftConstraint)
                .append("\" evaluates to ")
                .append(leftConstraint.isSatisfied())
                .append(" due to the following reason:");
        addMessageForConstraint(sb, leftConstraint, depth + 1);

        sb.append("\n")
                .append("\t".repeat(depth))
                .append("|- The right side \"")
                .append(rightConstraint)
                .append("\" evaluates to ")
                .append(rightConstraint.isSatisfied())
                .append(" due to the following reason:");
        addMessageForConstraint(sb, rightConstraint, depth + 1);
    }

    private void addMessageForPredicateConstraint(
            StringBuilder sb, PredefinedPredicateConstraint constraint, int depth) {
        // TODO Improve error message
    }
}
