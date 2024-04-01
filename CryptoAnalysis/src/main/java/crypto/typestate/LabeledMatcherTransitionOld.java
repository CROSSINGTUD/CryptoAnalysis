package crypto.typestate;

import boomerang.scene.DeclaredMethod;
import boomerang.scene.Method;
import boomerang.scene.Val;
import boomerang.scene.WrappedClass;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import crypto.cryslhandler.CrySLReaderUtils;
import crypto.rules.CrySLMethod;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import typestate.finiteautomata.MatcherTransition;
import typestate.finiteautomata.State;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class LabeledMatcherTransitionOld extends MatcherTransition {
	public LabeledMatcherTransitionOld(State from, String methodMatcher, Parameter param, State to, Type type) {
		super(from, methodMatcher, param, to, type);
	}

	public static LabeledMatcherTransitionOld getTransition(State from, Collection<CrySLMethod> label, Parameter param,
															State to, MatcherTransition.Type type) {
		/*Multimap<CrySLMethod, Method> resolvedLabel = HashMultimap.create(label.size(), 1);
		for (CrySLMethod method : label)
			resolvedLabel.putAll(method, CrySLMethodToSootMethod.v().convert(method));*/
		return new LabeledMatcherTransitionOld(from, "", param, to, type);
	}

	public static LabeledMatcherTransitionOld getErrorTransition(State from, Collection<CrySLMethod> matchingMethods,
																 Parameter param, State to, Type type) {
		return new LabeledMatcherTransitionOld(from, "", param, to, type);
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
	/*public static boolean matches(Method called, Method declared) {

		// Name is equal
		if (!called.getName().equals(declared.getName()))
			return false;
		// declaring class is or is superinterface/superclass of actual class
		if (!isSubtype(called.getDeclaringClass(), declared.getDeclaringClass()))
			return false;
		// Number of Parameters are equal
		if (called.getParameterLocals().size() != declared.getParameterLocals().size())
			return false;
		// Parameters are equal
		for (int i = 0; i < called.getParameterLocals().size(); i++) {
			Val calledParameter = called.getParameterLocal(i);
			Val declaredParameter = declared.getParameterLocal(i);

			if (!calledParameter.getType().equals(declaredParameter.getType())) {
				return false;
			}
		}
		// nice, declared is the declared version of called
		return true;
	}*/

	/**
	 * Returns whether parent is a super type of child, i.e. if they
	 * are the same, child implements or extends parent transitively.
	 * 
	 * @param childClass		the child to check
	 * @param parentClass	the parent to check against
	 * 
	 * @return true, if parent is a super type of child
	 */
	/*public static boolean isSubtype(WrappedClass childClass, WrappedClass parentClass) {
		SootClass child = (SootClass) childClass.getDelegate();
		SootClass parent = (SootClass) parentClass.getDelegate();

		if (child.equals(parent))
			return true;

		if (child.isInterface()) {
			return parent.isInterface() &&
					Scene.v().getActiveHierarchy().isInterfaceSubinterfaceOf(child, parent);
		}
		return Scene.v().getActiveHierarchy().isClassSubclassOf(child, parent)
				|| child.getInterfaces().contains(parent);
	}

	private final Multimap<CrySLMethod, Method> label;
	private final CrySLMethod NO_METHOD = new CrySLMethod("", Collections.emptyList(),
			CrySLReaderUtils.resolveObject(null));

	private LabeledMatcherTransitionOld(State from, Collection<CrySLMethod> matchingMethods, Parameter param, State to,
										Type type) {
		super(from, "", param, to, type);
		this.label = HashMultimap.create(1, matchingMethods.size());
		//this.label.putAll(NO_METHOD, matchingMethods);
	}

	private LabeledMatcherTransitionOld(State from, Multimap<CrySLMethod, Method> label, Parameter param, State to,
										Type type) {
		super(from, "", param, to, type);
		this.label = label;
	}*/

	/**
	 * The matches method of {@link MatcherTransition} matches Methods taken
	 * from some {@link DeclaredMethod}'s.
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
	 * @see typestate.finiteautomata.MatcherTransition#matches(DeclaredMethod)
	 */
	/*@Override
	public boolean matches(DeclaredMethod declaredMethod) {
		for (Method m : this.label.values()) {

			if (matches(method, m)) {
				return true;
			}
		}
		return false;
	}*/

	/**
	 * Return the {@link CrySLMethod}'s that match the given method.
	 * As the method is taken from a statement, we need to apply the mathcing logic
	 * defined here, to get the {@link CrySLMethod}s that were resolved to the
	 * matching {@link SootMethod}s.
	 *
	 * @param declaredMethod	the given method
	 * @return The {@link CrySLMethod}'s matching the given soot method.
	 */
	/*public Optional<CrySLMethod> getMatching(DeclaredMethod declaredMethod) {
		for (Map.Entry<CrySLMethod, Method> m : this.label.entries()) {
			Method method = CrySLMethodToSootMethod.declaredMethodToJimpleMethod(declaredMethod);

			if (matches(method, m.getValue())) {
				return Optional.of(m.getKey());
			}
		}
		return Optional.empty();
	}

	@Override
	public String toString() {
		return super.toString();
	}*/
}
