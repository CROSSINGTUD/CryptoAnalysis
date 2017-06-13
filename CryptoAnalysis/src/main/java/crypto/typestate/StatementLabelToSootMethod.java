package crypto.typestate;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import crypto.rules.CryptSLMethod;
import heros.utilities.DefaultValueMap;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;

public class StatementLabelToSootMethod {
	private static StatementLabelToSootMethod instance;
	private DefaultValueMap<CryptSLMethod, Collection<SootMethod>> descriptorToSootMethod = new DefaultValueMap<CryptSLMethod, Collection<SootMethod>>() {
		@Override
		protected Collection<SootMethod> createItem(CryptSLMethod key) {
			return _convert(key);
		}
	};
	
	public Collection<SootMethod> convert(CryptSLMethod label){
		return descriptorToSootMethod.getOrCreate(label);
	}
	private Collection<SootMethod> _convert(CryptSLMethod label) {
		String methodName = label.getMethodName();
		String methodNameWithoutDeclaringClass = getMethodNameWithoutDeclaringClass(methodName);
		Set<SootMethod> res = Sets.newHashSet();
		String declaringClass = getDeclaringClass(methodName);
		int noOfParams = label.getParameters().size() - 1; //-1 because List of Parameters contains placeholder for return value.
		SootClass sootClass = Scene.v().getSootClass(declaringClass);
		for (SootMethod m : sootClass.getMethods()) {
			if (m.getName().equals(methodNameWithoutDeclaringClass) && m.getParameterCount() == noOfParams)
				res.add(m);
		}
		return res;
	}
	private String getMethodNameWithoutDeclaringClass(String desc) {
		try{
			if(Scene.v().containsClass(desc))
				return "<init>";
		} catch(RuntimeException e){
			
		}
		return desc.substring(desc.lastIndexOf(".") +1);
	}

	public Collection<SootMethod> convert(List<CryptSLMethod> list) {
		Set<SootMethod> res = Sets.newHashSet();
		for(CryptSLMethod l : list)
			res.addAll(_convert(l));
		return res;
	}
	
	private String getDeclaringClass(String label) {
		try{
			if(Scene.v().containsClass(label))
				return label;
		} catch(RuntimeException e){
			
		}
		return label.substring(0, label.lastIndexOf("."));
	}
	public static StatementLabelToSootMethod v() {
		if(instance == null)
			instance = new StatementLabelToSootMethod();
		return instance;
	}
	public Set<SootMethod> convert(String label) {
		String removedParameters = label.substring(0,label.indexOf("("));
		String methodNameWithoutDeclaringClass = getMethodNameWithoutDeclaringClass(removedParameters);
		Set<SootMethod> res = Sets.newHashSet();
		String declaringClass = getDeclaringClass(removedParameters);
		String[] params = getParameters(label);
		SootClass sootClass = Scene.v().getSootClass(declaringClass);
		for(SootMethod m : sootClass.getMethods()){
			if(m.getName().equals(methodNameWithoutDeclaringClass)){
				if(m.getParameterCount() == params.length){
					boolean paramTypesMatch = true;
					int i = 0;
					for(Type t : m.getParameterTypes()){
						if(!paramsTypeMatch(t, params[i])){
							paramTypesMatch = false;
							break;
						}
						i++;
					}
					if(paramTypesMatch)
						res.add(m);
				}
			}
		}
		return res;
	}
	private boolean paramsTypeMatch(Type t, String string) {
		return t.toString().equals(string);
	}
	private String[] getParameters(String label) {	
		String substring = label.substring(label.indexOf("(")+1, label.indexOf(")"));
		return substring.split(",");
	}
}
