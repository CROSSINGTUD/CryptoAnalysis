package crypto.analysis.errors;

public interface ErrorVisitor {
	public void visit(ConstraintError constraintError);
	public void visit(ForbiddenMethodError abstractError);
	public void visit(IncompleteOperationError incompleteOperationError);
	public void visit(TypestateError typestateError);
	public void visit(RequiredPredicateError predicateError);
	public void visit(ImpreciseValueExtractionError predicateError);
	public void visit(NeverTypeOfError predicateError);
	public void visit(PredicateContradictionError predicateContradictionError);
	public void visit(HardCodedError hardcodedError);
}
