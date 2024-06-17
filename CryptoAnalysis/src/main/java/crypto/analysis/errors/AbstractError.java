package crypto.analysis.errors;

import boomerang.scene.Statement;
import crypto.rules.CrySLRule;
import soot.jimple.IfStmt;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractError implements IError {

	private final Statement errorStmt;
	private final CrySLRule rule;
	private final String outerMethod;
	private final String invokeMethod;
	private final String declaringClass;

	private final Set<AbstractError> causedByErrors; // preceding
	private final Set<AbstractError> willCauseErrors; // subsequent

	public AbstractError(Statement errorStmt, CrySLRule rule) {
		this.errorStmt = errorStmt;
		this.rule = rule;
		this.outerMethod = errorStmt.getMethod().getName();
		this.declaringClass = errorStmt.getMethod().getDeclaringClass().getName();

		this.causedByErrors = new HashSet<>();
		this.willCauseErrors = new HashSet<>();

		if(errorStmt.containsInvokeExpr()) {
			this.invokeMethod = errorStmt.getInvokeExpr().getMethod().getName();
		} else if(errorStmt.isReturnStmt()) {
			this.invokeMethod = errorStmt.toString();
		} else if (errorStmt.isIfStmt()) {
			// TODO getCondition not accessible
			this.invokeMethod = ((IfStmt) errorStmt).getCondition().toString();
		} else {
			this.invokeMethod = errorStmt.getLeftOp().getVariableName();
		}
	}

	public abstract String toErrorMarkerString();

	public Statement getErrorStatement() {
		return errorStmt;
	}

	public CrySLRule getRule() {
		return rule;
	}

	public int getLineNumber() {
		return errorStmt.getStartLineNumber();
	}

	public void addCausingError(AbstractError parent) {
		causedByErrors.add(parent);
	}

	public void addCausingError(Collection<AbstractError> parents) {
		causedByErrors.addAll(parents);
	}

	public void addSubsequentError(AbstractError subsequentError) {
		willCauseErrors.add(subsequentError);
	}

	public Set<AbstractError> getSubsequentErrors(){
		return this.willCauseErrors;
	}

	public Set<AbstractError> getRootErrors(){
		return this.causedByErrors;
	}


	public String toString() {
		return toErrorMarkerString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((declaringClass == null) ? 0 : declaringClass.hashCode());
		result = prime * result + ((invokeMethod == null) ? 0 : invokeMethod.hashCode());
		result = prime * result + ((outerMethod == null) ? 0 : outerMethod.hashCode());
		result = prime * result + ((rule == null) ? 0 : rule.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractError other = (AbstractError) obj;
		if (declaringClass == null) {
			if (other.declaringClass != null)
				return false;
		} else if (!declaringClass.equals(other.declaringClass))
			return false;
		if (invokeMethod == null) {
			if (other.invokeMethod != null)
				return false;
		} else if (!invokeMethod.equals(other.invokeMethod))
			return false;
		if (outerMethod == null) {
			if (other.outerMethod != null)
				return false;
		} else if (!outerMethod.equals(other.outerMethod))
			return false;
		if (rule == null) {
			if (other.rule != null)
				return false;
		} else if (!rule.equals(other.rule)) {
			return false;
		} else if (!errorStmt.equals(other.getErrorStatement())) {
			return false;
		}
		return true;
	}
}
