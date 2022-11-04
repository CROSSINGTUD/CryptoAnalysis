package crypto.typestate;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import crypto.rules.CrySLMethod;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import typestate.finiteautomata.MatcherTransition;
import typestate.finiteautomata.State;

public class LabeledMatcherTransition extends MatcherTransition {

	private List<CrySLMethod> label;
	private Collection<SootMethod> matchingMethods;

	public LabeledMatcherTransition(State from, Collection<SootMethod> matchingMethods, Parameter param, State to, Type type) {
		super(from, matchingMethods, param, to, type);
		this.matchingMethods = matchingMethods;
		this.label = Collections.emptyList();
	}

	public LabeledMatcherTransition(State from, List<CrySLMethod> label,  Parameter param, State to, Type type) {
		super(from, CrySLMethodToSootMethod.v().convert(label), param, to, type);
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
		if ("true".equals(System.getProperty("strictMatch")))
			return super.matches(method);
		for (SootMethod m : this.matchingMethods) {
			if (matches(method, m))
				return true;
		}
		return false;
	}

	/**
	 * Match the called method againts a declared method and checker wheter
	 * the called method could actually be the declared one.
	 * It does not matter if the declared method is acually overridden,
	 * since the overriding method would be matched in a later iteration.
	 */
	public boolean matches(SootMethod called, SootMethod declared) {
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

	@Override
	public String toString() {
			return super.toString() + " FUZZY";
	}

	public List<CrySLMethod> label() {
		return label;
	}
}
