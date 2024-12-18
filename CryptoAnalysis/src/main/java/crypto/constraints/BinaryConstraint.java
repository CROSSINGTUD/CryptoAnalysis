package crypto.constraints;

import crysl.rule.CrySLConstraint;

class BinaryConstraint extends EvaluableConstraint {

    public BinaryConstraint(CrySLConstraint origin, ConstraintSolver context) {
        super(origin, context);
    }

    @Override
    public void evaluate() {
        CrySLConstraint binaryConstraint = (CrySLConstraint) origin;
        EvaluableConstraint left =
                EvaluableConstraint.getInstance(binaryConstraint.getLeft(), context);
        EvaluableConstraint right =
                EvaluableConstraint.getInstance(binaryConstraint.getRight(), context);
        left.evaluate();
        CrySLConstraint.LogOps ops = binaryConstraint.getOperator();

        if (ops.equals(CrySLConstraint.LogOps.implies)) {
            // Left side of implication is not satisfied => Right side does not need to be satisfied
            if (left.hasErrors()) {
                return;
            }

            right.evaluate();
            errors.addAll(right.getErrors());
        } else if (ops.equals(CrySLConstraint.LogOps.or)) {
            // Constraint is violated if left and right is not satisfied
            right.evaluate();
            errors.addAll(left.getErrors());
            errors.addAll(right.getErrors());
        } else if (ops.equals(CrySLConstraint.LogOps.and)) {
            // Left is not satisfied => AND cannot be satisfied
            if (left.hasErrors()) {
                errors.addAll(left.getErrors());
                return;
            }

            right.evaluate();
            errors.addAll(right.getErrors());
        } else if (ops.equals(CrySLConstraint.LogOps.eq)) {
            right.evaluate();

            // Simple <=> evaluation
            if ((left.hasErrors() && right.hasErrors())
                    || (!left.hasErrors() && !right.hasErrors())) {
                return;
            }
            errors.addAll(right.getErrors());
        } else {
            errors.addAll(left.getErrors());
        }
    }
}
