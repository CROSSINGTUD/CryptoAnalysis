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
import crypto.constraints.EvaluableConstraint;
import java.util.Collection;

/**
 * Represents the violation of a {@link BinaryConstraint}
 *
 * @param constraint the violated constraint
 */
public record ViolatedBinaryConstraint(BinaryConstraint constraint) implements ViolatedConstraint {

    @Override
    public String getErrorMessage() {
        return getSimplifiedMessage(0);
    }

    @Override
    public String getSimplifiedMessage(int depth) {
        StringBuilder sb = new StringBuilder();
        addMessageForConstraint(sb, constraint, depth);

        return sb.toString();
    }

    private void addMessageForConstraint(
            StringBuilder sb, EvaluableConstraint constraint, int depth) {
        if (constraint instanceof BinaryConstraint binaryConstraint) {
            addMessageForBinaryConstraint(sb, binaryConstraint, depth);
        } else {
            addMessageForLeafConstraint(sb, constraint, depth);
        }
    }

    private void addMessageForBinaryConstraint(
            StringBuilder sb, BinaryConstraint constraint, int depth) {
        EvaluableConstraint leftConstraint = constraint.getLeftConstraint();
        EvaluableConstraint rightConstraint = constraint.getRightConstraint();

        sb.append("\n")
                .append("\t".repeat(depth))
                .append("|- Constraint \"")
                .append(constraint)
                .append("\" evaluates to <")
                .append(constraint.isSatisfied())
                .append(">:");

        sb.append("\n")
                .append("\t".repeat(depth + 1))
                .append("|- The left side \"")
                .append(leftConstraint)
                .append("\" evaluates to <")
                .append(leftConstraint.isSatisfied())
                .append(">:");
        addMessageForConstraint(sb, leftConstraint, depth + 2);

        sb.append("\n")
                .append("\t".repeat(depth + 1))
                .append("|- The right side \"")
                .append(rightConstraint)
                .append("\" evaluates to <")
                .append(rightConstraint.isSatisfied())
                .append(">:");
        addMessageForConstraint(sb, rightConstraint, depth + 2);
    }

    private void addMessageForLeafConstraint(
            StringBuilder sb, EvaluableConstraint constraint, int depth) {
        if (constraint.isSatisfied()) {
            Collection<SatisfiedConstraint> satisfiedConstraints =
                    constraint.getSatisfiedConstraints();

            for (SatisfiedConstraint cons : satisfiedConstraints) {
                sb.append(cons.getSimplifiedMessage(depth));
            }
        }

        if (constraint.isViolated()) {
            Collection<ViolatedConstraint> violatedConstraints =
                    constraint.getViolatedConstraints();

            for (ViolatedConstraint cons : violatedConstraints) {
                sb.append(cons.getSimplifiedMessage(depth));
            }

            Collection<ImpreciseValueConstraint> impreciseConstraints =
                    constraint.getImpreciseConstraints();
            for (ImpreciseValueConstraint cons : impreciseConstraints) {
                sb.append(cons.getSimplifiedMessage(depth));
            }
        }
    }
}
