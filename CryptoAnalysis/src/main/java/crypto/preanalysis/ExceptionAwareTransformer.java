package crypto.preanalysis;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import crypto.constraints.ExceptionConstraint;
import crypto.rules.CrySLExceptionConstraint;
import crypto.rules.CrySLRule;
import crypto.typestate.CrySLMethodToSootMethod;
import crypto.typestate.LabeledMatcherTransition;
import soot.Body;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.UnitPatchingChain;
import soot.jimple.NullConstant;
import soot.jimple.Stmt;
import soot.jimple.internal.JEqExpr;
import soot.jimple.internal.JIfStmt;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This transformer adds a branch after each statement, that may throw an
 * Exception, to the handler of that Exception.
 * The exceptions that a statement may throw are declared in the CrySLRule.
 */
public class ExceptionAwareTransformer extends PreTransformer {

	private final SootClass spec;

	private final Multimap<SootMethod, SootClass> exceptions;

	private final Map<SootMethod, SootMethod> lookupCache = new HashMap<>();

	public ExceptionAwareTransformer(final CrySLRule rule) {
		this.exceptions = HashMultimap.create();
		this.spec = Scene.v().getSootClass(rule.getClassName());
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

			if (!called.getDeclaringClass().equals(this.spec))
				return;

			lookup(called).ifPresent(declared -> {
				for (final SootClass exception : exceptions.get(declared))
					ExceptionConstraint.getTrap(body, unit, exception)
							.ifPresent(trap -> addBranch(units, unit, trap.getHandlerUnit()));
			});
		});
	}

	private void addBranch(final UnitPatchingChain units, final Unit after, final Unit to) {
		final JEqExpr condition = new JEqExpr(NullConstant.v(), NullConstant.v());
		units.insertOnEdge(new JIfStmt(condition, to), after, null);
	}

	private Optional<SootMethod> lookup(final SootMethod called) {
		if (lookupCache.containsKey(called))
			return Optional.of(lookupCache.get(called));
		for (final SootMethod declared : exceptions.keySet()) {
			if (LabeledMatcherTransition.matches(called, declared)) {
				lookupCache.put(called, declared);
				return Optional.of(declared);
			}
		}
		return Optional.empty();
	}

}
