package crypto.constraints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import boomerang.scene.ControlFlowGraph;
import crypto.analysis.errors.UncaughtExceptionError;
import crypto.rules.CrySLExceptionConstraint;
import crypto.typestate.CrySLMethodToSootMethod;
import crypto.typestate.LabeledMatcherTransition;
import soot.Body;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Trap;
import soot.Unit;
import soot.UnitBox;
import soot.jimple.Stmt;

public class ExceptionConstraint extends EvaluableConstraint {

	private final Set<SootMethod> method;
	private final SootClass exception;

	public ExceptionConstraint(CrySLExceptionConstraint cons, ConstraintSolver context) {
		super(cons, context);
		this.method = new HashSet<>(CrySLMethodToSootMethod.v().convert(cons.getMethod()));
		this.exception = Scene.v().getSootClass(cons.getException().getException());
	}

	/**
	 * Evaluates this contraint, by checking if the excepiton if caught for every
	 * invokation of the method.
	 */
	@Override
	public void evaluate() {
		for (ControlFlowGraph.Edge call : context.getCollectedCalls()) {
			evaluate(call);
		}
	}

	/**
	 * Checks if a) the method that is called is the same as the method of
	 * this constraint and b) if the specified exception is caught.
	 * 
	 * @param call	the called statement
	 */
	public void evaluate(ControlFlowGraph.Edge call) {
		try {
			Stmt stmt = call.getUnit().get();
			if (!isSameMethod(stmt.getInvokeExpr().getMethod()))
				return;
			if (!getTrap(call.getMethod().getActiveBody(), stmt, this.exception).isPresent())
				errors.add(new UncaughtExceptionError(call, context.getClassSpec().getRule(), this.exception));
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
	public static Optional<Trap> getTrap(final Body body, final Unit unit, final SootClass exception) {
		for (final Trap trap : body.getTraps())
			if (ExceptionConstraint.isCaughtAs(trap.getException(), exception))
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
	public static boolean isCaughtAs(SootClass catchClause, SootClass exception) {
		return LabeledMatcherTransition.isSubtype(exception, catchClause);
	}

	/**
	 * @param method The method to compare againts.
	 * @return Wheter the methods represented in this constraint match the given
	 *         method.
	 */
	public boolean isSameMethod(SootMethod method) {
		return this.method.stream().anyMatch(declared -> LabeledMatcherTransition.matches(method, declared));
	}
}
