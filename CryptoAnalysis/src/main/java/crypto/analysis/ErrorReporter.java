package crypto.analysis;

import soot.Unit;

public interface ErrorReporter {
	public void report(ClassSpecification spec, Unit stmt, Violation details);
	public static interface Violation{}
	public static class TypestateViolation implements Violation{
		
	}
	public static class ForbiddenMethodViolation implements Violation{
	}
	
	public static class ConstraintSystemViolation implements Violation{
	}
}
