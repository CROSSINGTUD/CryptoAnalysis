package crypto.typestate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;

import boomerang.accessgraph.AccessGraph;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import heros.utilities.DefaultValueMap;
import soot.Local;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import typestate.TypestateDomainValue;
import typestate.finiteautomata.MatcherStateMachine;
import typestate.finiteautomata.MatcherTransition;
import typestate.finiteautomata.MatcherTransition.Parameter;
import typestate.finiteautomata.MatcherTransition.Type;
import typestate.finiteautomata.Transition;

public class FiniteStateMachineToTypestateChangeFunction extends MatcherStateMachine<StateNode> {

	private StateNode initialState;
	private Collection<SootMethod> initialTransitonLabel;
	private Collection<SootMethod> allMatchedMethods = Sets.newHashSet();
	private DefaultValueMap<String, Collection<SootMethod>> descriptorToSootMethod = new DefaultValueMap<String, Collection<SootMethod>>() {
		@Override
		protected Collection<SootMethod> createItem(String key) {
			return descriptorToSootMethod(key);
		}
	};
	private CryptoTypestateAnaylsisProblem analysisProblem;
	private StateMachineGraph stateMachineGraph;

	public FiniteStateMachineToTypestateChangeFunction(CryptoTypestateAnaylsisProblem analysisProblem) {
		this.analysisProblem = analysisProblem;
		stateMachineGraph = analysisProblem.getStateMachineGraph();
		initialTransitonLabel = completeDescriptorToSootMethod(stateMachineGraph.getInitialTransition().getLabel().getMethodName());
		initialState = stateMachineGraph.getInitialTransition().to();
		for (final typestate.interfaces.Transition<StateNode> t : stateMachineGraph.getAllTransitions()) {
			this.addTransition(new MatcherTransition<StateNode>(t.from(), completeDescriptorToSootMethod(t.getLabel().getMethodName()),
					Parameter.This, t.to(), Type.OnCallToReturn) {
				@Override
				public String toString() {
					return t.getLabel().getMethodName();
				}
			});
		}
	}

	@Override
	public Set<Transition<StateNode>> getCallToReturnTransitionsFor(AccessGraph d1, Unit callSite, AccessGraph d2,
			Unit returnSite, AccessGraph d3) {
		Set<Transition<StateNode>> res = super.getCallToReturnTransitionsFor(d1, callSite, d2, returnSite, d3);
		for (Transition<StateNode> t : res) {
			if (!(t instanceof MatcherTransition))
				continue;
			injectQueryAtCallSite(t.toString(),callSite, d1);
		}
		return res;
	}
	
	private void injectQueryAtCallSite(String descriptor, Unit callSite, AccessGraph context) {
		for(String matchingDescriptor : splitDescriptor(descriptor.toString())){
			for(SootMethod m : descriptorToSootMethod.getOrCreate(matchingDescriptor)){
				if (!(callSite instanceof Stmt))
					continue;
				Stmt stmt = (Stmt) callSite;
				if (!stmt.containsInvokeExpr())
					continue;
				SootMethod method = stmt.getInvokeExpr().getMethod();
				if (!m.equals(method))
					continue;
				{
					int index = 0;
					for(String param : getParameters(matchingDescriptor)){
						if(!param.equals("_")){
							soot.Type parameterType = method.getParameterType(index);
							if(parameterType.toString().equals(param)){
								analysisProblem.addQueryAtCallsite(matchingDescriptor, stmt, index, context);
							}
						}
						index++;
					}
				}
			}
		}
	}

	@Override
	public Collection<AccessGraph> generateSeed(SootMethod method, Unit unit, Collection<SootMethod> optional) {
		Set<AccessGraph> out = new HashSet<>();
		if (!(unit instanceof Stmt) || !((Stmt) unit).containsInvokeExpr())
			return out;
		InvokeExpr invokeExpr = ((Stmt) unit).getInvokeExpr();
		SootMethod calledMethod = invokeExpr.getMethod();
		if (!initialTransitonLabel.contains(calledMethod))
			return out;
		if (calledMethod.isStatic()) {
			AssignStmt stmt = (AssignStmt) unit;
			out.add(new AccessGraph((Local) stmt.getLeftOp(), stmt.getLeftOp().getType()));
		} else if (invokeExpr instanceof InstanceInvokeExpr){
			InstanceInvokeExpr iie = (InstanceInvokeExpr) invokeExpr;
			out.add(new AccessGraph((Local)  iie.getBase(), iie.getBase().getType()));
		}
		injectQueryAtCallSite(stateMachineGraph.getInitialTransition().getLabel().getMethodName(),unit,null);
		return out;
	}

	private Collection<SootMethod> descriptorToSootMethod(String desc) {
		desc = addInitStringIfConstructor(desc);
		Set<SootMethod> res = Sets.newHashSet();
		String methodName = getMethodName(desc);
		String declaringClass = getDeclaringClass(desc);
		int noOfParams = getNumberOfParams(desc);
		SootClass sootClass = Scene.v().getSootClass(declaringClass);
		for (SootMethod m : sootClass.getMethods()) {
			if (m.getName().equals(methodName) && m.getParameterCount() == noOfParams)
				res.add(m);
		}
		allMatchedMethods.addAll(res);
		return res;
	}
	private String addInitStringIfConstructor(String desc) {
		String prefix = desc.substring(0, desc.indexOf("("));
		String suffix = desc.substring( desc.indexOf("("));
		try{
			if(Scene.v().containsClass(prefix))
				return prefix +".<init>"+suffix;
		} catch(RuntimeException e){
			
		}
		return desc;
	}

	private int getNumberOfParams(String desc) {
		return getParameters(desc).length;
	}
	private String getParameterSubString(String desc) {
		return desc.substring(desc.indexOf("(") +1,desc.indexOf(")"));
	}

	private String[] getParameters(String desc) {
		if(getParameterSubString(desc).length() == 0)
			return new String[]{};
		return getParameterSubString(desc).split(",");
	}
	private Collection<SootMethod> completeDescriptorToSootMethod(String desc) {
		Set<SootMethod> res = Sets.newHashSet();
		for(String l : splitDescriptor(desc))
			res.addAll(descriptorToSootMethod(l));
		return res;
	}
	private String[] splitDescriptor(String label) {
		String[] split = label.split(";");
		return split;
	}
	
	

	private String getMethodName(String label) {
		String substring = label.substring(0, label.indexOf("("));
		String[] split = substring.split("\\.");
		return split[split.length - 1];
	}

	private String getDeclaringClass(String label) {
		String substring = label.substring(0, label.indexOf("("));
		return substring.substring(0, substring.lastIndexOf("."));
	}

	@Override
	public TypestateDomainValue<StateNode> getBottomElement() {
		return new TypestateDomainValue<StateNode>(initialState);
	}

	
	public Collection<SootMethod> getAllMatchedMethods(){
		return Sets.newHashSet(allMatchedMethods);
	}
}
