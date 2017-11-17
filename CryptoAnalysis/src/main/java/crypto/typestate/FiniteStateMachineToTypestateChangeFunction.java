package crypto.typestate;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import boomerang.accessgraph.AccessGraph;
import boomerang.jimple.Val;
import crypto.rules.CryptSLMethod;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.rules.TransitionEdge;
import soot.Local;
import soot.RefType;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.NewExpr;
import soot.jimple.Stmt;
import typestate.finiteautomata.MatcherTransition;
import typestate.finiteautomata.MatcherTransition.Parameter;
import typestate.finiteautomata.MatcherTransition.Type;
import typestate.finiteautomata.State;
import typestate.finiteautomata.Transition;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;

public class FiniteStateMachineToTypestateChangeFunction extends TypeStateMachineWeightFunctions {

	private State initialState;
	private Collection<SootMethod> initialTransitonLabel;
	private Collection<SootMethod> edgeLabelMethods = Sets.newHashSet();
	private Collection<SootMethod> methodsInvokedOnInstance = Sets.newHashSet();
	
	private CryptoTypestateAnaylsisProblem analysisProblem;
	private StateMachineGraph stateMachineGraph;
	private Multimap<State, SootMethod> outTransitions = HashMultimap.create();
	private Collection<RefType> analyzedType = Sets.newHashSet();

	public FiniteStateMachineToTypestateChangeFunction(CryptoTypestateAnaylsisProblem analysisProblem) {
		this.analysisProblem = analysisProblem;
		stateMachineGraph = analysisProblem.getStateMachine();
		initialTransitonLabel = convert(stateMachineGraph.getInitialTransition().getLabel());
		//TODO #15 we must start the analysis in state stateMachineGraph.getInitialTransition().from();
		initialState = new WrappedState(stateMachineGraph.getInitialTransition().to());
		for (final TransitionEdge t : stateMachineGraph.getAllTransitions()) {
			WrappedState from = new WrappedState(t.from());
			WrappedState to = new WrappedState(t.to());
			this.addTransition(new LabeledMatcherTransition(from, t.getLabel(),
					Parameter.This, to, Type.OnCallToReturn));
			outTransitions.putAll(from, convert(t.getLabel()));
		}
		if(startAtConstructor()){
			List<SootMethod> label = Lists.newLinkedList();
			for(SootMethod m : initialTransitonLabel)
				if(m.isConstructor())
					label.add(m);
			this.addTransition(new MatcherTransition(initialState, label, Parameter.This, initialState, Type.OnCallToReturn));
			outTransitions.putAll(initialState, label);
		}
		//All transitions that are not in the state machine 
		for(StateNode t :  stateMachineGraph.getNodes()){
			State wrapped = new WrappedState(t);
			Collection<SootMethod> remaining = getEdgeLabelMethods();
			Collection<SootMethod> outs =  outTransitions.get(wrapped);
			if(outs == null)
				outs = Sets.newHashSet();
			remaining.removeAll(outs);
			this.addTransition(new MatcherTransition(wrapped, remaining, Parameter.This, ErrorStateNode.v(), Type.OnCallToReturn));
		}
	}

	private boolean startAtConstructor() {
		for(SootMethod m : initialTransitonLabel){
			if(m.isConstructor()){
				analyzedType.add(m.getDeclaringClass().getType());
			}
		}
		return !analyzedType.isEmpty();
	}

	private Collection<SootMethod> convert(List<CryptSLMethod> label) {
		Collection<SootMethod> converted = CryptSLMethodToSootMethod.v().convert(label);
		edgeLabelMethods.addAll(converted);
		return converted;
	}


	private Collection<SootMethod> convert(CryptSLMethod label) {
		Collection<SootMethod> converted = CryptSLMethodToSootMethod.v().convert(label);
		edgeLabelMethods.addAll(converted);
		return converted;
	}
	@Override
	public Set<Transition> getCallToReturnTransitionsFor(AccessGraph d1, Unit callSite, AccessGraph d2,
			Unit returnSite, AccessGraph d3) {
		Set<Transition> res = super.getCallToReturnTransitionsFor(d1, callSite, d2, returnSite, d3);
		for (Transition t : res) {
			if (!(t instanceof LabeledMatcherTransition))
				continue;
			injectQueryAtCallSite(((LabeledMatcherTransition)t).label,callSite, d1);
		}		
		if(callSite instanceof Stmt){
			Stmt stmt = (Stmt) callSite;
			if(stmt.containsInvokeExpr() && stmt.getInvokeExpr() instanceof InstanceInvokeExpr){
				InstanceInvokeExpr e = (InstanceInvokeExpr)stmt.getInvokeExpr();
				if(e.getBase().equals(d2.getBase())){
					analysisProblem.methodInvokedOnInstance(stmt);
				}
			}
		}
		return res;
	}

	public void injectQueryForSeed(Unit u){
        injectQueryAtCallSite(stateMachineGraph.getInitialTransition().getLabel(),u);
	}
	
	private void injectQueryAtCallSite(List<CryptSLMethod> list, Unit callSite) {
		for(CryptSLMethod matchingDescriptor : list){
			for(SootMethod m : convert(matchingDescriptor)){
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
					for(Entry<String, String> param : matchingDescriptor.getParameters()){
						if(!param.getKey().equals("_")){
							soot.Type parameterType = method.getParameterType(index);
							if(parameterType.toString().equals(param.getValue())){
								analysisProblem.addQueryAtCallsite(param.getKey(), stmt, index);
							}
						}
						index++;
					}
				}
			}
		}
	}

	@Override
	public Collection<Val> generateSeed(SootMethod method, Unit unit, Collection<SootMethod> optional) {
		Set<Val> out = new HashSet<>();
		if(startAtConstructor()){
			if(unit instanceof AssignStmt){
				AssignStmt as = (AssignStmt) unit;
				if(as.getRightOp() instanceof NewExpr){
					NewExpr newExpr = (NewExpr) as.getRightOp();
					if(analyzedType.contains(newExpr.getType())){
						AssignStmt stmt = (AssignStmt) unit;
						out.add(new Val(stmt.getLeftOp(), method));
					}
				}
			}
		}
		if (!(unit instanceof Stmt) || !((Stmt) unit).containsInvokeExpr())
			return out;
		InvokeExpr invokeExpr = ((Stmt) unit).getInvokeExpr();
		SootMethod calledMethod = invokeExpr.getMethod();
		if (!initialTransitonLabel.contains(calledMethod) || calledMethod.isConstructor())
			return out;
		if (calledMethod.isStatic()) {
			if(unit instanceof AssignStmt){
				AssignStmt stmt = (AssignStmt) unit;
				out.add(new Val(stmt.getLeftOp(), method));
			}
		} else if (invokeExpr instanceof InstanceInvokeExpr){
			InstanceInvokeExpr iie = (InstanceInvokeExpr) invokeExpr;
			out.add(new Val(iie.getBase(), method));
		}
		return out;
	}

	
	public Collection<SootMethod> getEdgeLabelMethods(){
		return Sets.newHashSet(edgeLabelMethods);
	}

	public Collection<SootMethod> getAllMethodsInvokedOnInstance(){
		return Sets.newHashSet(methodsInvokedOnInstance);
	}
	
	public Collection<SootMethod> getEdgesOutOf(State n){
		return outTransitions.get(n);
	}
	
	private class LabeledMatcherTransition extends MatcherTransition{

		private final List<CryptSLMethod> label;

		public LabeledMatcherTransition(State from, List<CryptSLMethod> label,
				Parameter param, State to,
				Type type) {
			super(from,convert(label), param, to, type);
			this.label = label;
		}}
	
	private class WrappedState implements State{
		private StateNode delegate;

		WrappedState(StateNode delegate){
			this.delegate = delegate;
		}
		@Override
		public boolean isErrorState() {
			return delegate.isErrorState();
		}

		@Override
		public boolean isInitialState() {
			return  delegate.isInitialState();
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((delegate == null) ? 0 : delegate.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			WrappedState other = (WrappedState) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (delegate == null) {
				if (other.delegate != null)
					return false;
			} else if (!delegate.equals(other.delegate))
				return false;
			return true;
		}
		private FiniteStateMachineToTypestateChangeFunction getOuterType() {
			return FiniteStateMachineToTypestateChangeFunction.this;
		}
		
	}
}
