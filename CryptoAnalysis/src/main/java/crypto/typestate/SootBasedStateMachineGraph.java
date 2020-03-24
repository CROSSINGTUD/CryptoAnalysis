package crypto.typestate;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import boomerang.jimple.Statement;
import crypto.rules.CrySLMethod;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.rules.TransitionEdge;
import soot.SootMethod;
import typestate.TransitionFunction;
import typestate.finiteautomata.MatcherTransition;
import typestate.finiteautomata.MatcherTransition.Parameter;
import typestate.finiteautomata.MatcherTransition.Type;
import typestate.finiteautomata.State;

public class SootBasedStateMachineGraph {

	private Set<MatcherTransition> transition = new HashSet<>();
	private Collection<SootMethod> edgeLabelMethods = Sets.newHashSet();
	
	private final StateMachineGraph stateMachineGraph;
	private Multimap<State, SootMethod> outTransitions = HashMultimap.create();
	private Collection<SootMethod> initialTransitonLabel;
	private List<CrySLMethod> crySLinitialTransitionLabel;
	private LabeledMatcherTransition initialTransiton;

	public SootBasedStateMachineGraph(StateMachineGraph fsm) {
		this.stateMachineGraph = fsm;
		//TODO #15 we must start the analysis in state stateMachineGraph.getInitialTransition().from();
		for (final TransitionEdge t : stateMachineGraph.getAllTransitions()) {
			WrappedState from = wrappedState(t.from());
			WrappedState to = wrappedState(t.to());
			LabeledMatcherTransition trans = new LabeledMatcherTransition(from, t.getLabel(),
					Parameter.This, to, Type.OnCallOrOnCallToReturn);
			this.addTransition(trans);
			outTransitions.putAll(from, convert(t.getLabel()));
			if(stateMachineGraph.getInitialTransition().equals(t))
				this.initialTransiton = trans;
		}
		crySLinitialTransitionLabel = stateMachineGraph.getInitialTransition().getLabel();
		
		initialTransitonLabel = convert(stateMachineGraph.getInitialTransition().getLabel());
		//All transitions that are not in the state machine 
		for(StateNode t :  this.stateMachineGraph.getNodes()){
			State wrapped = wrappedState(t);
			Collection<SootMethod> remaining = getInvolvedMethods();
			Collection<SootMethod> expected =  this.outTransitions.get(wrapped);
			if(expected != null){
				remaining.removeAll(expected);
				ReportingErrorStateNode repErrorState = new ReportingErrorStateNode(expected);
				this.addTransition(new MatcherTransition(wrapped, remaining, Parameter.This, new ReportingErrorStateNode(expected), Type.OnCallOrOnCallToReturn));
				//Once an object is in error state, it always remains in the error state.
				ErrorStateNode errorState = new ErrorStateNode();
				this.addTransition(new MatcherTransition(repErrorState, getInvolvedMethods(), Parameter.This, errorState, Type.OnCallOrOnCallToReturn));
			}
		}
		
	}

	

	private WrappedState wrappedState(StateNode t) {
		return new WrappedState(t, stateMachineGraph.getInitialTransition().from().equals(t));
	}



	public Collection<SootMethod> getEdgesOutOf(State n){
		return outTransitions.get(n);
	}
	public void addTransition(MatcherTransition trans) {
		transition.add(trans);
	}

	private Collection<SootMethod> convert(List<CrySLMethod> label) {
		Collection<SootMethod> converted = CrySLMethodToSootMethod.v().convert(label);
		edgeLabelMethods.addAll(converted);
		return converted;
	}


	public Collection<SootMethod> getInvolvedMethods(){
		return Sets.newHashSet(edgeLabelMethods);
	}
	

	public TransitionFunction getInitialWeight(Statement stmt) {
		return new TransitionFunction(initialTransiton,Collections.singleton(stmt));
	}

	public List<MatcherTransition> getAllTransitions() {
		return Lists.newArrayList(transition);
	}

	public Collection<SootMethod> initialTransitonLabel() {
		return Lists.newArrayList(initialTransitonLabel);
	}


	public List<CrySLMethod> getInitialTransition() {
		return crySLinitialTransitionLabel;
	}
}
