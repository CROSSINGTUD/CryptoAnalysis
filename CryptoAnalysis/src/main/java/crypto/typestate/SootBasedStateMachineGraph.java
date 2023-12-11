package crypto.typestate;

import java.util.ArrayList;
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
import soot.jimple.AssignStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
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
	private Collection<SootMethod> initialTransitionLabels;
	private Collection<CrySLMethod> crySLinitialTransitionLabels;
	private Collection<LabeledMatcherTransition> initialMatcherTransitions;

	public SootBasedStateMachineGraph(StateMachineGraph fsm) {
		this.stateMachineGraph = fsm;
		this.initialTransitionLabels = new ArrayList<>();
		this.crySLinitialTransitionLabels = new ArrayList<>();
		this.initialMatcherTransitions = new ArrayList<>();
		
		Collection<TransitionEdge> initialTransitions = stateMachineGraph.getInitialTransitions();
		
		for (final TransitionEdge t : stateMachineGraph.getAllTransitions()) {
			WrappedState from = wrappedState(t.from());
			WrappedState to = wrappedState(t.to());
			LabeledMatcherTransition trans = LabeledMatcherTransition.getTransition(from, t.getLabel(),
					Parameter.This, to, Type.OnCallOrOnCallToReturn);
			this.addTransition(trans);
			outTransitions.putAll(from, convert(t.getLabel()));
			
			if (initialTransitions.contains(t)) {
				initialMatcherTransitions.add(trans);
			}
		}

		for (TransitionEdge edge : initialTransitions) {
			crySLinitialTransitionLabels.addAll(edge.getLabel());
			initialTransitionLabels.addAll(convert(edge.getLabel()));
		}
		
		// All transitions that are not in the state machine
		for (StateNode t : this.stateMachineGraph.getNodes()) {
			State wrapped = wrappedState(t);
			Collection<SootMethod> remaining = getInvolvedMethods();
			Collection<SootMethod> expected = this.outTransitions.get(wrapped);
			if (expected != null) {
				remaining.removeAll(expected);
				ReportingErrorStateNode repErrorState = new ReportingErrorStateNode(expected);
				this.addTransition(LabeledMatcherTransition.getErrorTransition(wrapped, remaining, Parameter.This,
						new ReportingErrorStateNode(expected), Type.OnCallOrOnCallToReturn));
				// Once an object is in error state, it always remains in the error state.
				ErrorStateNode errorState = new ErrorStateNode();
				this.addTransition(LabeledMatcherTransition.getErrorTransition(repErrorState, getInvolvedMethods(), Parameter.This, errorState,
						Type.OnCallOrOnCallToReturn));
			}
		}
	}

	private WrappedState wrappedState(StateNode t) {
		return new WrappedState(t, stateMachineGraph.getInitialTransition().from().equals(t));
	}

	public Collection<SootMethod> getEdgesOutOf(State n) {
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

	public Collection<SootMethod> getInvolvedMethods() {
		return Sets.newHashSet(edgeLabelMethods);
	}

	public TransitionFunction getInitialWeight(Statement stmt) {
		TransitionFunction defaultTransition = new TransitionFunction(((ArrayList<LabeledMatcherTransition>) initialMatcherTransitions).get(0), Collections.singleton(stmt));
		
		if (!(stmt.getUnit().get() instanceof InvokeStmt) && !(stmt.getUnit().get() instanceof AssignStmt)) {
			return defaultTransition;
		}
		
		for (LabeledMatcherTransition trans : initialMatcherTransitions) {
			if (stmt.getUnit().get() instanceof InvokeStmt) {
				InvokeExpr invokeExpr = stmt.getUnit().get().getInvokeExpr();

				if (trans.getMatching(invokeExpr.getMethod()).isPresent()) {
					return new TransitionFunction(trans, Collections.singleton(stmt));
				}
			} else if (stmt.getUnit().get() instanceof AssignStmt) {
				InvokeExpr invokeExpr = stmt.getUnit().get().getInvokeExpr();
				
				if (trans.getMatching(invokeExpr.getMethod()).isPresent()) {
					return new TransitionFunction(trans, Collections.singleton(stmt));
				}
			}
		}
		return defaultTransition;
	}

	public List<MatcherTransition> getAllTransitions() {
		return Lists.newArrayList(transition);
	}

	public Collection<SootMethod> initialTransitonLabel() {
		return initialTransitionLabels;
	}

	public Collection<CrySLMethod> getInitialTransition() {
		return crySLinitialTransitionLabels;
	}
}
