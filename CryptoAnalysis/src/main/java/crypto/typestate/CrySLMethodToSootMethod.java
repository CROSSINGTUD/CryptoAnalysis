package crypto.typestate;

import boomerang.scene.DeclaredMethod;
import boomerang.scene.Method;
import boomerang.scene.Val;
import boomerang.scene.WrappedClass;
import boomerang.scene.jimple.JimpleDeclaredMethod;
import boomerang.scene.jimple.JimpleInvokeExpr;
import boomerang.scene.jimple.JimpleMethod;
import boomerang.scene.jimple.JimpleWrappedClass;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import crypto.rules.CrySLMethod;
import heros.utilities.DefaultValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.jimple.InvokeExpr;
import soot.jimple.JimpleBody;
import soot.jimple.internal.JSpecialInvokeExpr;
import soot.jimple.internal.JStaticInvokeExpr;
import soot.jimple.internal.JVirtualInvokeExpr;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class CrySLMethodToSootMethod {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CrySLMethodToSootMethod.class);
	
	private static CrySLMethodToSootMethod instance;
	private DefaultValueMap<CrySLMethod, Collection<Method>> descriptorToSootMethod = new DefaultValueMap<CrySLMethod, Collection<Method>>() {
		@Override
		protected Collection<Method> createItem(CrySLMethod key) {
			Collection<Method> res = Sets.newHashSet();
			try{
				res = _convert(key);
			} catch(Exception e){
				LOGGER.error("Failed to convert method "  + key);
			}
			for (Method m : res) {
				sootMethodToDescriptor.put(m, key);
			}
			return res;
		}
	};
	private Multimap<Method, CrySLMethod> sootMethodToDescriptor = HashMultimap.create();

	public Collection<CrySLMethod> convert(Method m) {
		return sootMethodToDescriptor.get(m);
	}

	public Collection<Method> convert(CrySLMethod label) {
		return descriptorToSootMethod.getOrCreate(label);
	}

	private Collection<Method> _convert(CrySLMethod label) {
		Set<Method> res = Sets.newHashSet();
		String methodName = label.getMethodName();
		String declaringClass = getDeclaringClass(methodName);
		if (!Scene.v().containsClass(declaringClass)){
			return res;
		}
		Scene.v().forceResolve(declaringClass, SootClass.BODIES);
		SootClass sootClass = Scene.v().getSootClass(declaringClass);
		List<SootClass> classes = Lists.newArrayList(sootClass);
		String methodNameWithoutDeclaringClass = getMethodNameWithoutDeclaringClass(methodName);
		if (methodNameWithoutDeclaringClass.equals(sootClass.getShortName())) {
			//Constructors are only searched from within the actual class itself
			methodNameWithoutDeclaringClass = "<init>";
		} else {
			//For all other EVENTS, any call of the hierarchy matches.
			classes.addAll(getFullHierarchyOf(sootClass));
		}
		int noOfParams = label.getParameters().size(); 
		for(SootClass c : classes) {
			for (SootMethod m : c.getMethods()) {
				if (m.getName().equals(methodNameWithoutDeclaringClass) && m.getParameterCount() == noOfParams) {
					if (parametersMatch(label.getParameters(), m.getParameterTypes())) {
						// TODO Refactor
						//res.add(JimpleMethod.of(m));

						/*InvokeExpr invokeExpr;
						if (m.isStatic()) {
							invokeExpr = new JStaticInvokeExpr(m.makeRef(), );
						} else if (m.isConstructor()) {
							invokeExpr = new JSpecialInvokeExpr(, );
						} else {
							invokeExpr = new JVirtualInvokeExpr()
						}
						JimpleInvokeExpr jimpleInvokeExpr = new JimpleInvokeExpr(, m);
						DeclaredMethod declaredMethod = jimpleInvokeExpr.getMethod();*/

						// Set the class defining the target method to phantom to avoid analyzing the method,
						// if it is defined in a superclass
						// c.setPhantomClass();
					}
				}
			}
		}
		return res;
	}

    private Collection<? extends SootClass> getFullHierarchyOf(SootClass sootClass) {
        LinkedList<SootClass> worklist = Lists.newLinkedList();
        Set<SootClass> visited = Sets.newHashSet();
        worklist.add(sootClass);
        visited.add(sootClass);
        while (!worklist.isEmpty()) {
            SootClass first = worklist.pop();
            Set<SootClass> hierarchy = Sets.newHashSet();
            hierarchy.addAll(first.getInterfaces());
            if (first.isInterface()) {
                hierarchy.addAll(Scene.v().getActiveHierarchy().getSuperinterfacesOf(first));
            } else {
                hierarchy.addAll(Scene.v().getActiveHierarchy().getSuperclassesOf(first));
            }
            for (SootClass h : hierarchy) {
                if (visited.add(h)) {
                    worklist.add(h);
                }
            }
        }
        return visited;
    }

	private boolean parametersMatch(List<Entry<String, String>> parameters, List<Type> parameterTypes) {
		int i = 0;
		for (Type parameter : parameterTypes) {
			if (parameters.get(i).getValue().equals("AnyType"))
				continue;
			
			// Soot does not track generic types, so we are required to remove <...> from the parameter
			String adaptedParameter = parameters.get(i).getValue().replaceAll("[<].*?[>]", "");
			if (!parameter.toString().equals(adaptedParameter)) {
				return false;
			}
			i++;
		}
		return true;
	}

	private String getMethodNameWithoutDeclaringClass(String desc) {
		return desc.substring(desc.lastIndexOf(".") + 1);
	}

	public Collection<Method> convert(List<CrySLMethod> list) {
		Set<Method> res = Sets.newHashSet();
		for (CrySLMethod l : list)
			res.addAll(convert(l));
		return res;
	}

	private String getDeclaringClass(String label) {
		try {
			if (Scene.v().containsClass(label))
				return label;
		} catch (RuntimeException e) {

		}
		return label.substring(0, label.lastIndexOf("."));
	}

	public static CrySLMethodToSootMethod v() {
		if (instance == null)
			instance = new CrySLMethodToSootMethod();
		return instance;
	}

	public static void reset() {
		instance = null;
	}
}
