package crypto.typestate;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import crypto.cryslhandler.CrySLReaderUtils;
import crypto.rules.CrySLMethod;
import soot.SootClass;
import soot.SootMethod;
import typestate.finiteautomata.MatcherTransition;
import typestate.finiteautomata.State;

public class LabeledMatcherTransition extends MatcherTransition {

	public static LabeledMatcherTransition getLabeledMatcherTransition(State from, Collection<CrySLMethod> label, Parameter param, State to, Type type) {
		Multimap<CrySLMethod, SootMethod> resolvedLabel = HashMultimap.create(label.size(), 1);
		for (CrySLMethod method : label)
			resolvedLabel.putAll(method, CrySLMethodToSootMethod.v().convert(method));
		return new LabeledMatcherTransition(from, resolvedLabel, param, to, type);
	}

	public static MatcherTransition getMatcherTransition(State from, Collection<SootMethod> matchingMethods, Parameter param, State to, Type type) {
		return new LabeledMatcherTransition(from, matchingMethods, param, to, type);
	}

	/**
	 * Match the called method againts a declared method and checker wheter
	 * the called method could actually be the declared one.
	 * It does not matter if the declared method is acually overridden,
	 * since the overriding method would be matched in a later iteration.
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
	 * Returns whether parent is a supertype of child, i.e. if they
	 * are the same, child implements or extends parent transitivly.
	 */
	public static boolean isSubtype(SootClass child, SootClass parent) {
		try {
			Class<?> x = Class.forName(parent.getName());
			Class<?> y = Class.forName(child.getName());
			return x.isAssignableFrom(y);
		} catch (Throwable e) {
			return false;
		}
	}

	private final Multimap<CrySLMethod, SootMethod> label;
	private final CrySLMethod NO_METHOD = new CrySLMethod("", Collections.emptyList(), CrySLReaderUtils.resolveObject(null));

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
	 * The MatcherTransition is borked, since the passed method has the object on
	 * which it is called as declaringClass, even if it inherits it.
	 * The state machine is per Class, so every method will have the same
	 * declaring class and it is correct to return true if it matches the
	 * method of some supertype.
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

	public Collection<CrySLMethod> label() {
		return this.label.keySet();
	}
}
