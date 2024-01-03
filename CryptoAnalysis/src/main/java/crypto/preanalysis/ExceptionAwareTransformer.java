package crypto.preanalysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import crypto.constraints.ExceptionConstraint;
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
import soot.Unit;
import soot.UnitPatchingChain;
import soot.jimple.NullConstant;
import soot.jimple.Stmt;
import soot.jimple.internal.JEqExpr;
import soot.jimple.internal.JIfStmt;

/**
 * This transformer adds a branch after each statement, that may throw an
 * Exception, to the handler of that Exception.
 * The exceptions that a statement may throw are declared in the CrySLRule.
 */
public class ExceptionAwareTransformer extends BodyTransformer {

	public static void setup(final List<CrySLRule> rules) {
		final String phaseName = "jap.etr";
		PackManager.v().getPack("jap").remove(phaseName);
		PackManager.v().getPack("jap").add(new Transform(phaseName, new ExceptionAwareTransformer(rules)));
		PhaseOptions.v().setPhaseOption(phaseName, "on");
	}

	private final Collection<CrySLRule> rules;
	private final Map<SootMethod, SootMethod> lookupCache = new HashMap<>();

	public ExceptionAwareTransformer(final Collection<CrySLRule> rules) {
		this.rules = rules;
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
			SootClass declaringClass = called.getDeclaringClass();
			Optional<CrySLRule> rule = getRuleForClass(declaringClass);

			if (!rule.isPresent()) {
				return;
			}

			Multimap<SootMethod, SootClass> exceptions = getExceptionsForRule(rule.get());

			lookup(called, exceptions).ifPresent(declared -> {
				for (final SootClass exception : exceptions.get(declared))
					ExceptionConstraint.getTrap(body, unit, exception)
							.ifPresent(trap -> addBranch(units, unit, trap.getHandlerUnit()));
			});
		});
	}

	private Optional<CrySLRule> getRuleForClass(SootClass sootClass) {
		for (CrySLRule rule : rules) {
			if (rule.getClassName().equals(sootClass.getName())) {
				return Optional.of(rule);
			}
		}
		return Optional.empty();
	}

	private Multimap<SootMethod, SootClass> getExceptionsForRule(CrySLRule rule) {
		Multimap<SootMethod, SootClass> exceptions = HashMultimap.create();

		rule.getConstraints().stream()
				.filter(constraint -> constraint instanceof CrySLExceptionConstraint)
				.map(constraint -> (CrySLExceptionConstraint) constraint)
				.forEach(constraint -> CrySLMethodToSootMethod.v().convert(constraint.getMethod()).stream()
						.forEach(
								method -> exceptions.put(method, Scene.v().getSootClass(constraint.getException().getException()))));

		return exceptions;
	}

	private void addBranch(final UnitPatchingChain units, final Unit after, final Unit to) {
		final JEqExpr condition = new JEqExpr(NullConstant.v(), NullConstant.v());
		units.insertOnEdge(new JIfStmt(condition, to), after, null);
	}

	private Optional<SootMethod> lookup(final SootMethod called, Multimap<SootMethod, SootClass> exceptions) {
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
