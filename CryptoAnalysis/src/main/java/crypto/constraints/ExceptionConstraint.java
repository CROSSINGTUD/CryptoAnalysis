package crypto.constraints;

import boomerang.scene.DeclaredMethod;
import boomerang.scene.Method;
import boomerang.scene.Statement;
import boomerang.scene.WrappedClass;
import boomerang.scene.jimple.JimpleMethod;
import boomerang.scene.jimple.JimpleStatement;
import boomerang.scene.jimple.JimpleWrappedClass;
import crypto.analysis.errors.UncaughtExceptionError;
import crypto.rules.CrySLExceptionConstraint;
import crypto.utils.MatcherUtils;
import soot.Body;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Trap;
import soot.Unit;
import soot.UnitBox;
import soot.jimple.Stmt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ExceptionConstraint extends EvaluableConstraint {

	private final Set<Method> method;
	private final WrappedClass exception;

	public ExceptionConstraint(CrySLExceptionConstraint cons, ConstraintSolver context) {
		super(cons, context);
		//this.method = new HashSet<>(CrySLMethodToSootMethod.v().convert(cons.getMethod()));
		this.method = new HashSet<>();

		SootClass exceptionClass = Scene.v().getSootClass(cons.getException().getException());
		this.exception = new JimpleWrappedClass(exceptionClass);
	}

	/**
	 * Evaluates this contraint, by checking if the excepiton if caught for every
	 * invokation of the method.
	 */
	@Override
	public void evaluate() {
		for (Statement statement : context.getCollectedCalls()) {
			evaluate(statement);
		}
	}

	/**
	 * Checks if a) the method that is called is the same as the method of
	 * this constraint and b) if the specified exception is caught.
	 * 
	 * @param stmt	the called statement
	 */
	public void evaluate(Statement stmt) {
		try {
			DeclaredMethod declaredMethod = stmt.getInvokeExpr().getMethod();
			if (!isSameMethod(declaredMethod))
				return;

			if (!(stmt.getMethod() instanceof JimpleMethod)) {
				return;
			}

			JimpleMethod jimpleMethod = (JimpleMethod) stmt.getMethod();
			SootMethod sootMethod = jimpleMethod.getDelegate();

			if (!(stmt instanceof JimpleStatement)) {
				return;
			}

			JimpleStatement jimpleStatement = (JimpleStatement) stmt;
			Stmt sootStmt = jimpleStatement.getDelegate();

			if (!getTrap(sootMethod.getActiveBody(), sootStmt, this.exception).isPresent())
				errors.add(new UncaughtExceptionError(context.getSeed(), stmt, context.getSpecification(), this.exception));
		} catch (Exception e) {
		}
	}

	/**
	 * Returns whether the `trapped` unit is trapped in the method body.
	 *
	 * @param body	the method's body
	 * @param trap	the trap
	 * @param trapped	the trapped unit
	 * @return Returns whether the `trapped` unit is trapped in the method body.
	 */
	public static boolean trapsUnit(final Body body, final Trap trap, final Unit trapped) {
		boolean begun = false;
		for (final Unit unit : getUnits(body)) {
			if (unit.equals(trap.getEndUnit()))
				break;
			if (unit.equals(trap.getBeginUnit()))
				begun = true;
			if (begun && unit.equals(trapped))
				return true;
		}
		return false;
	}

	/**
	 * Returns the handler, that catches the exception thrown by callee in the method.
	 *
	 * @param body The called Method, throwing the exception.
	 * @param unit Unit where callee is called.
	 * @param exception The called Method, throwing the exception.
	 * @return Returns the handler, that catches the exception thrown by callee in the method.
	 */
	public static Optional<Trap> getTrap(final Body body, final Unit unit, final WrappedClass exception) {
		for (final Trap trap : body.getTraps())
			if (ExceptionConstraint.isCaughtAs(new JimpleWrappedClass(trap.getException()), exception))
				if (trapsUnit(body, trap, unit))
					return Optional.of(trap);
		return Optional.empty();
	}

	/**
	* Returns all units in the method body, excluding exception handlers.
	* 
	* @param body	the method's body
	* 
	* @return units	all collected units
	*/
	public static Collection<Unit> getUnits(Body body) {
		Collection<Unit> units = new ArrayList<>();
		for (Unit item : body.getUnits())
			getAllUnits(item, units);
		return units;
	}

	private static Collection<Unit> getAllUnits(Unit unit, Collection<Unit> units) {
		if (unit == null)
			return units;
		units.add(unit);
		for (UnitBox item : unit.getUnitBoxes())
			getAllUnits(item.getUnit(), units);
		return units;
	}

	/**
	 * Returns wheter a catch clause with the given catchClause, would catch
	 * the given exception.
	 * 
	 * @param catchClause The type of the catch-clause.
	 * @param exception Exception to be caught.
	 * @return Wheter a catch clause with the given catchClause, would catch
	 *         the given exception.
	 */
	public static boolean isCaughtAs(WrappedClass catchClause, WrappedClass exception) {
		return MatcherUtils.isSubtype(exception, catchClause);
	}

	/**
	 * @param method The method to compare againts.
	 * @return Wheter the methods represented in this constraint match the given
	 *         method.
	 */
	public boolean isSameMethod(DeclaredMethod method) {
		// TODO Refactoring
		return false;
		//return this.method.stream().anyMatch(declared -> MatcherUtils.matches(method, declared));
	}
}
