package crypto.typestate;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import crypto.rules.CryptSLMethod;
import heros.utilities.DefaultValueMap;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;

public class CryptSLMethodToSootMethod {
	private static CryptSLMethodToSootMethod instance;
	private DefaultValueMap<CryptSLMethod, Collection<SootMethod>> descriptorToSootMethod = new DefaultValueMap<CryptSLMethod, Collection<SootMethod>>() {
		@Override
		protected Collection<SootMethod> createItem(CryptSLMethod key) {
			Collection<SootMethod> res = Sets.newHashSet();
			try{
				res = _convert(key);
			} catch(Exception e){
				System.err.println("Failed to convert method "  + key);
			}
			for (SootMethod m : res) {
				sootMethodToDescriptor.put(m, key);
			}
			return res;
		}
	};
	private Multimap<SootMethod, CryptSLMethod> sootMethodToDescriptor = HashMultimap.create();

	public Collection<CryptSLMethod> convert(SootMethod m) {
		return sootMethodToDescriptor.get(m);
	}

	public Collection<SootMethod> convert(CryptSLMethod label) {
		return descriptorToSootMethod.getOrCreate(label);
	}

	private Collection<SootMethod> _convert(CryptSLMethod label) {
		Set<SootMethod> res = Sets.newHashSet();
		String methodName = label.getMethodName();
		String declaringClass = getDeclaringClass(methodName);
//		Scene.v().forceResolve(declaringClass, SootClass.BODIES);
		if (!Scene.v().containsClass(declaringClass)){
			return res;
		}
		SootClass sootClass = Scene.v().getSootClass(declaringClass);
		String methodNameWithoutDeclaringClass = getMethodNameWithoutDeclaringClass(methodName);
		if (methodNameWithoutDeclaringClass.equals(sootClass.getShortName()))
			methodNameWithoutDeclaringClass = "<init>";
		int noOfParams = label.getParameters().size(); 
		for (SootMethod m : sootClass.getMethods()) {
			if (m.getName().equals(methodNameWithoutDeclaringClass) && m.getParameterCount() == noOfParams) {
				if (parametersMatch(label.getParameters(), m.getParameterTypes())){
					res.add(m);
				}
			}
		}
		if(res.isEmpty()){
			System.out.println("Warning: Couldn't find any method for CryptSLMethod: " + label);
		}
		return res;
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

	public Collection<SootMethod> convert(List<CryptSLMethod> list) {
		Set<SootMethod> res = Sets.newHashSet();
		for (CryptSLMethod l : list)
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

	public static CryptSLMethodToSootMethod v() {
		if (instance == null)
			instance = new CryptSLMethodToSootMethod();
		return instance;
	}

	public static void reset() {
		instance = null;
	}
}
