package crypto.typestate;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import crypto.rules.StatementLabel;
import heros.utilities.DefaultValueMap;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;

public class StatementLabelToSootMethod {
	private static StatementLabelToSootMethod instance;
	private DefaultValueMap<StatementLabel, Collection<SootMethod>> descriptorToSootMethod = new DefaultValueMap<StatementLabel, Collection<SootMethod>>() {
		@Override
		protected Collection<SootMethod> createItem(StatementLabel key) {
			return _convert(key);
		}
	};
	
	public Collection<SootMethod> convert(StatementLabel label){
		return descriptorToSootMethod.getOrCreate(label);
	}
	private Collection<SootMethod> _convert(StatementLabel label) {
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

	public Collection<SootMethod> convert(List<StatementLabel> list) {
		Set<SootMethod> res = Sets.newHashSet();
		for(StatementLabel l : list)
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
}
