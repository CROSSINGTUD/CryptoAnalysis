package crypto.preanalysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import crypto.rules.CrySLExceptionConstraint;
import crypto.rules.CrySLRule;
import crypto.typestate.CrySLMethodToSootMethod;
import crypto.typestate.LabeledMatcherTransition;
import soot.Body;
import soot.BodyTransformer;
import soot.PackManager;
import soot.PhaseOptions;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Trap;
import soot.Unit;
import soot.UnitPatchingChain;
import soot.jimple.NullConstant;
import soot.jimple.Stmt;
import soot.jimple.internal.JEqExpr;
import soot.jimple.internal.JIfStmt;
import soot.util.Chain;

/**
 * This transformer adds a branch after each statement, that may throw an
 * Exception, to the handler of that Exception.
 * The exceptions that a statement may throw are declared in the CrySLRule.
 */
public class ExceptionAwareTransformer extends BodyTransformer {

	public static void setup(final List<CrySLRule> rules) {
		for (final CrySLRule rule : rules) {
			final String phaseName = "jap.etr-" + rule.getClassName();
			PackManager.v().getPack("jap").remove(phaseName);
			PackManager.v().getPack("jap").add(new Transform(phaseName, new ExceptionAwareTransformer(rule)));
			PhaseOptions.v().setPhaseOption(phaseName, "on");
		}
		PackManager.v().runPacks();
	}

	private final Multimap<SootMethod, SootClass> exceptions;

	private final Map<SootMethod, SootMethod> lookupCache = new HashMap<>();

	public ExceptionAwareTransformer(final CrySLRule rule) {
		this.exceptions = HashMultimap.create();

		rule.getConstraints().stream()
				.filter(constraint -> constraint instanceof CrySLExceptionConstraint)
				.map(constraint -> (CrySLExceptionConstraint) constraint)
				.forEach(constraint -> CrySLMethodToSootMethod.v().convert(constraint.getMethod()).stream()
						.forEach(
								method -> exceptions.put(method, Scene.v().getSootClass(constraint.getException().getException()))));
	}

	protected void internalTransform(final Body body, final String phase, final Map<String, String> options) {
		if (body.getMethod().getDeclaringClass().getName().startsWith("java."))
			return;
		if (!body.getMethod().getDeclaringClass().isApplicationClass())
			return;

		final UnitPatchingChain units = body.getUnits();
		units.snapshotIterator().forEachRemaining(unit -> {
			if (!(unit instanceof Stmt))
				return;
			if (!((Stmt) unit).containsInvokeExpr())
				return;

			final SootMethod called = ((Stmt) unit).getInvokeExpr().getMethod();
			lookup(called).ifPresent(declared -> {
				for (final SootClass exception : exceptions.get(declared))
					getTrap(body, unit, exception)
							.ifPresent(trap -> addBranch(units, unit, trap.getHandlerUnit()));
			});
		});
	}

	private void addBranch(final UnitPatchingChain units, final Unit after, final Unit to) {
		final JEqExpr condition = new JEqExpr(NullConstant.v(), NullConstant.v());
		units.insertOnEdge(new JIfStmt(condition, to), after, null);
	}

	private boolean matches(final SootMethod callee, final SootMethod declared) {
		return LabeledMatcherTransition.matches(callee, declared);
	}

	private boolean trapsUnit(final Body body, final Trap trap, final Unit trapped) {
		boolean begun = false;
		for (final Unit unit : body.getUnits()) {
			if (unit.equals(trap.getEndUnit()))
				break;
			if (unit.equals(trap.getBeginUnit()))
				begun = true;
			if (begun && unit.equals(trapped))
				return true;
		}
		return false;
	}

	private Optional<Trap> getTrap(final Body body, final Unit unit, final SootClass exception) {
		final Chain<Trap> traps = body.getTraps();
		for (final Trap trap : traps)
			if (trapsUnit(body, trap, unit))
				return Optional.of(trap);
		return Optional.empty();
	}

	private Optional<SootMethod> lookup(final SootMethod called) {
		if (lookupCache.containsKey(called))
			return Optional.of(lookupCache.get(called));
		for (final SootMethod declared : exceptions.keySet()) {
			if (matches(called, declared)) {
				lookupCache.put(called, declared);
				return Optional.of(declared);
			}
		}
		return Optional.empty();
	}

}
