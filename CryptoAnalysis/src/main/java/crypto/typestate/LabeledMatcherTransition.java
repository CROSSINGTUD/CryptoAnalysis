package crypto.typestate;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import crypto.cryslhandler.CrySLReaderUtils;
import crypto.rules.CrySLMethod;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import typestate.finiteautomata.MatcherTransition;
import typestate.finiteautomata.State;

public class LabeledMatcherTransition extends MatcherTransition {

	public static LabeledMatcherTransition getTransition(State from, Collection<CrySLMethod> label, Parameter param,
			State to, Type type) {
		Multimap<CrySLMethod, SootMethod> resolvedLabel = HashMultimap.create(label.size(), 1);
		for (CrySLMethod method : label)
			resolvedLabel.putAll(method, CrySLMethodToSootMethod.v().convert(method));
		return new LabeledMatcherTransition(from, resolvedLabel, param, to, type);
	}

	public static MatcherTransition getErrorTransition(State from, Collection<SootMethod> matchingMethods,
			Parameter param, State to, Type type) {
		return new LabeledMatcherTransition(from, matchingMethods, param, to, type);
	}

	/**
	 * Match the called method against a declared method and checker whether
	 * the called method could actually be the declared one.
	 * 
	 * @param called	the called method
	 * @param declared	the declared method
	 * 
	 * @return true, if called and declared method match
	 */
	public static boolean matches(SootMethod called, SootMethod declared) {
		// Name is equal
		if (!called.getName().equals(declared.getName()))
			return false;
		// declaring class is or is superinterface/superclass of actual class
		if (!isSubtype(called.getDeclaringClass(), declared.getDeclaringClass()))
			return false;
		// Number of Parameters are equal
		if (!(called.getParameterCount() == declared.getParameterCount()))
			return false;
		// Parameters are equal
		if (!called.getParameterTypes().equals(declared.getParameterTypes()))
			return false;
		// nice, declared is the declared version of called
		return true;
	}

	/**
	 * Returns whether parent is a super type of child, i.e. if they
	 * are the same, child implements or extends parent transitively.
	 * 
	 * @param child		the child to check
	 * @param parent	the parent to check against
	 * 
	 * @return true, if parent is a super type of child
	 */
	public static boolean isSubtype(SootClass child, SootClass parent) {
		if (child.equals(parent))
			return true;

		if (child.isInterface()) {
			return parent.isInterface() &&
					Scene.v().getActiveHierarchy().isInterfaceSubinterfaceOf(child, parent);
		}
		return Scene.v().getActiveHierarchy().isClassSubclassOf(child, parent)
				|| child.getInterfaces().contains(parent);
	}

	private final Multimap<CrySLMethod, SootMethod> label;
	private final CrySLMethod NO_METHOD = new CrySLMethod("", Collections.emptyList(),
			CrySLReaderUtils.resolveObject(null));

	private LabeledMatcherTransition(State from, Collection<SootMethod> matchingMethods, Parameter param, State to,
			Type type) {
		super(from, matchingMethods, param, to, type);
		this.label = HashMultimap.create(1, matchingMethods.size());
		this.label.putAll(NO_METHOD, matchingMethods);
	}

	private LabeledMatcherTransition(State from, Multimap<CrySLMethod, SootMethod> label, Parameter param, State to,
			Type type) {
		super(from, label.values(), param, to, type);
		this.label = label;
	}

	/**
	 * The matches method of {@link MatcherTransition} matches Methods taken
	 * from some {@link soot.jimple.InvokeExpr}'s.
	 * The method getDeclaringClass() will return the object's class they are
	 * called on not the actual declaring class.
	 *
	 * Thus, if the class under spec does not declare the method,
	 * {@link CrySLMethodToSootMethod} won't find a matching method with the
	 * same declaring class and the label will not contain the method.
	 *
	 * We therefore check if there is a matching Method if the
	 * {@link MatcherTransition} returns false.
	 *
	 * The state machine is per Class, so every method will have the same
	 * declaring class and it is correct to return true if it matches the
	 * method of *some* super-type.
	 *
	 * @see typestate.finiteautomata.MatcherTransition#matches(soot.SootMethod)
	 */
	@Override
	public boolean matches(SootMethod method) {
		for (SootMethod m : this.label.values())
			if (matches(method, m))
				return true;
		return false;
	}

	/**
	 * Return the {@link CrySLMethod}'s that match the given method.
	 * As the method is taken from a statement, we need to apply the mathcing logic
	 * defined here, to get the {@link CrySLMethod}s that were resolved to the
	 * matching {@link SootMethod}s.
	 *
	 * @param method	the given method
	 * @return The {@link CrySLMethod}'s matching the given soot method.
	 */
	public Optional<CrySLMethod> getMatching(SootMethod method) {
		for (Map.Entry<CrySLMethod, SootMethod> m : this.label.entries())
			if (matches(method, m.getValue()))
				return Optional.of(m.getKey());
		return Optional.empty();
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
