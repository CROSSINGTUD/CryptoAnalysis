package crypto.typestate;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import crypto.rules.CrySLMethod;
import heros.utilities.DefaultValueMap;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;

public class CrySLMethodToSootMethod {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CrySLMethodToSootMethod.class);
	
	private static CrySLMethodToSootMethod instance;
	private DefaultValueMap<CrySLMethod, Collection<SootMethod>> descriptorToSootMethod = new DefaultValueMap<CrySLMethod, Collection<SootMethod>>() {
		@Override
		protected Collection<SootMethod> createItem(CrySLMethod key) {
			Collection<SootMethod> res = Sets.newHashSet();
			try{
				res = _convert(key);
			} catch(Exception e){
				LOGGER.error("Failed to convert method "  + key);
			}
			for (SootMethod m : res) {
				sootMethodToDescriptor.put(m, key);
			}
			return res;
		}
	};
	private Multimap<SootMethod, CrySLMethod> sootMethodToDescriptor = HashMultimap.create();

	public Collection<CrySLMethod> convert(SootMethod m) {
		return sootMethodToDescriptor.get(m);
	}

	public Collection<SootMethod> convert(CrySLMethod label) {
		return descriptorToSootMethod.getOrCreate(label);
	}

	private Collection<SootMethod> _convert(CrySLMethod label) {
		Set<SootMethod> res = Sets.newHashSet();
		String methodName = label.getMethodName();
		String declaringClass = getDeclaringClass(methodName);
//		Scene.v().forceResolve(declaringClass, SootClass.BODIES);
		if (!Scene.v().containsClass(declaringClass)){
			return res;
		}
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
					if (parametersMatch(label.getParameters(), m.getParameterTypes())){
						res.add(m);
					}
				}
			}
		}
		if(res.isEmpty()){
			LOGGER.warn("Couldn't find any method for CrySLMethod: " + label);
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
		for (Type t : parameterTypes) {
			if (parameters.get(i).getValue().equals("AnyType"))
				continue;
			if (!t.toString().equals(parameters.get(i).getValue())) {
				return false;
			}
			i++;
		}
		return true;
	}

	private String getMethodNameWithoutDeclaringClass(String desc) {
		return desc.substring(desc.lastIndexOf(".") + 1);
	}

	public Collection<SootMethod> convert(List<CrySLMethod> list) {
		Set<SootMethod> res = Sets.newHashSet();
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
