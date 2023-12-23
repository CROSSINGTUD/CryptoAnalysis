package crypto.constraints;

import crypto.rules.CrySLConstraint;
import crypto.rules.CrySLConstraint.LogOps;

class BinaryConstraint extends EvaluableConstraint {

	public BinaryConstraint(CrySLConstraint origin, ConstraintSolver context) {
		super(origin, context);
	}

	@Override
	public void evaluate() {
		CrySLConstraint binaryConstraint = (CrySLConstraint) origin;
		EvaluableConstraint left = EvaluableConstraint.getInstance(binaryConstraint.getLeft(), context);
		EvaluableConstraint right = EvaluableConstraint.getInstance(binaryConstraint.getRight(), context);
		left.evaluate();
		LogOps ops = binaryConstraint.getOperator();

		if (ops.equals(LogOps.implies)) {
			if (left.hasErrors()) {
				return;
			} else {
				right.evaluate();
				errors.addAll(right.getErrors());
				return;
			}
		} else if (ops.equals(LogOps.or)) {
			right.evaluate();
			errors.addAll(left.getErrors());
			errors.addAll(right.getErrors());
			return;
		} else if (ops.equals(LogOps.and)) {
			if (left.hasErrors()) {
				errors.addAll(left.getErrors());
				return;
			} else {
				right.evaluate();
				errors.addAll(right.getErrors());
				return;
			}
		} else if (ops.equals(LogOps.eq)) {
			right.evaluate();
			if ((left.hasErrors() && right.hasErrors()) || (!left.hasErrors() && !right.hasErrors())) {
				return;
			} else {
				errors.addAll(right.getErrors());
				return;
			}
		}
		errors.addAll(left.getErrors());
	}

}
