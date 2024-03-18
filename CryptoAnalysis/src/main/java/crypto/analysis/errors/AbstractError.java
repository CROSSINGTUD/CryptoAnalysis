package crypto.analysis.errors;

import boomerang.scene.ControlFlowGraph;
import crypto.rules.CrySLRule;
import soot.jimple.Stmt;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JIfStmt;
import soot.jimple.internal.JReturnStmt;
import soot.jimple.internal.JReturnVoidStmt;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractError implements IError{
	private ControlFlowGraph.Edge errorLocation;
	private CrySLRule rule;
	private final String outerMethod;
	private final String invokeMethod;
	private final String declaringClass;

	private Set<AbstractError> causedByErrors; // preceding
	private Set<AbstractError> willCauseErrors; // subsequent

	public AbstractError(ControlFlowGraph.Edge errorLocation, CrySLRule rule) {
		this.errorLocation = errorLocation;
		this.rule = rule;
		this.outerMethod = errorLocation.getMethod().getSignature();
		this.declaringClass = errorLocation.getMethod().getDeclaringClass().toString();

		this.causedByErrors = new HashSet<>();
		this.willCauseErrors = new HashSet<>();

		Stmt errorStmt = errorLocation.getUnit().get();
		if(errorStmt.containsInvokeExpr()) {
			this.invokeMethod = errorStmt.getInvokeExpr().getMethod().toString();
		} else if(errorStmt instanceof JReturnStmt || errorStmt instanceof JReturnVoidStmt) {
			this.invokeMethod = errorStmt.toString();
		} else if (errorStmt instanceof JIfStmt) {
			this.invokeMethod = ((JIfStmt) errorStmt).getCondition().toString();
		} else {
			this.invokeMethod = ((JAssignStmt) errorStmt).getLeftOp().toString();
		}
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

	public ControlFlowGraph.Edge getErrorLocation() {
		return errorLocation;
	}

	public CrySLRule getRule() {
		return rule;
	}
	public abstract String toErrorMarkerString();

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
		} else if (!errorLocation.equals(other.getErrorLocation())) {
			return false;
		}
		return true;
	}
}
