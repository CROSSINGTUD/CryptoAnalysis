package crypto.analysis.errors;

public interface ErrorVisitor {
	public void visit(ConstraintError constraintError);
	public void visit(ForbiddenMethodError abstractError);
	public void visit(IncompleteOperationError incompleteOperationError);
	public void visit(TypestateError typestateError);
	public void visit(RequiredPredicateError predicateError);
}
