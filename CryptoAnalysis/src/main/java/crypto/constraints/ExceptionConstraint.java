package crypto.constraints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import boomerang.jimple.Statement;
import crypto.analysis.errors.UncaughtExceptionError;
import crypto.rules.CrySLExceptionConstraint;
import crypto.typestate.CrySLMethodToSootMethod;
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
	private final Set<SootClass> catchableAs;

	public ExceptionConstraint(CrySLExceptionConstraint cons, ConstraintSolver context) {
		super(cons, context);
		this.method = new HashSet<>(CrySLMethodToSootMethod.v().convert(cons.getMethod()));
		this.exception = Scene.v().getSootClass(cons.getException().getException());
		this.catchableAs = fullHirachy(exception);
	}

	/**
	 * Evaluates this contraint, by checking if for every invokation of
	 * method, all the exception is caught.
	 */
	@Override
	public void evaluate() {
		for (Statement call : context.getCollectedCalls()) {
			evaluate(call);
		}
	}

	/**
	 * Checks if a) the method that is called is the same as the method of
	 * this constraint and b) if the specified exception is caught.
	 */
	public void evaluate(Statement call) {
		try {
			Stmt stmt = call.getUnit().get();
			if (!isSameMethod(stmt.getInvokeExpr().getMethod()))
				return;
			if (!isTrapped(call.getMethod(), stmt))
				errors.add(new UncaughtExceptionError(call, context.getClassSpec().getRule(), this.exception));
		} catch (Exception e) {
		}
	}

	/**
	 * Returns wheter the exception thrown by callee is caught in the method.
	 * 
	 * @param method
	 * @param callee The called Methdo, emitting the
	 * @return Wheter the exception thrown by callee is caught in the method.
	 */
	public boolean isTrapped(SootMethod method, Unit callee) {
		for (Trap trap : method.getActiveBody().getTraps()) {
			if (!isCaughtAs(trap.getException()))
				continue;
			Collection<Unit> trapped = getUnitsBetween(method, trap.getBeginUnit(), trap.getEndUnit());
			if (trapped.contains(callee))
				return true;
		}
		return false;
	}

	/**
	 * Returns all units in a methods body, excluding exception handlers.
	 */
	public static Collection<Unit> getUnits(SootMethod method) {
		Collection<Unit> units = new ArrayList<>();
		for (Unit item : method.getActiveBody().getUnits())
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
	 * Returns all units between end and begin in the method.
	 * 
	 * @param method The method from which to get the units.
	 * @param begin  First unit to include.
	 * @param end    Unit after Last Unit to include.
	 * @return All units in the body of method, between begin (inclusive) and
	 *         end (exclusive).
	 */
	public static Collection<Unit> getUnitsBetween(SootMethod method, Unit begin, Unit end) {
		Collection<Unit> result = new ArrayList<>();
		boolean beginningFound = false;
		for (final Unit unit : getUnits(method)) {
			if (unit.equals(begin))
				beginningFound = true;
			if (!beginningFound)
				continue;
			if (unit.equals(end))
				break;
			else
				result.add(unit);
		}
		return result;
	}

	/**
	 * Returns wheter a catch clause with the given catchClause, would catch
	 * the exception of this constraint.
	 * 
	 * @param catchClause The type of the catch-clause.
	 * @return Wheter a catch clause with the given catchClause, would catch
	 *         the exception of this constraint.
	 */
	public boolean isCaughtAs(SootClass catchClause) {
		return this.catchableAs.contains(catchClause);
	}

	/**
	 * @param method The method to compare againts.
	 * @return Wheter the methods represented in this constraint match the given
	 *         method.
	 */
	public boolean isSameMethod(SootMethod method) {
		return this.method.contains(method);
	}

	/**
	 * Returns the fullHirachy of the given SootClass, including all
	 * implented interfaces, classes and itself.
	 */
	private static Set<SootClass> fullHirachy(SootClass m) {
		return superTypes(m).collect(Collectors.toSet());
	}

	private static Stream<SootClass> superTypes(SootClass m) {
		if (!m.hasSuperclass())
			return Stream.empty();
		Stream<SootClass> self = Stream.of(m);
		Stream<SootClass> supercls = Stream.of(m.getSuperclass());
		Stream<SootClass> interfaces = m.getInterfaces().stream();
		Stream<SootClass> supertypes = Stream.concat(supercls, interfaces)
				.flatMap(ExceptionConstraint::superTypes);
		return Stream.concat(self, supertypes);
	}
}
