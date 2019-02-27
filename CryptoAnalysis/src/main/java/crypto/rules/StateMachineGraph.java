package crypto.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import crypto.interfaces.FiniteStateMachine;


public final class StateMachineGraph implements FiniteStateMachine<StateNode>, java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Set<StateNode> nodes;
	private final List<TransitionEdge> edges;

	public StateMachineGraph() {
		nodes = new HashSet<StateNode>();
		edges = new ArrayList<TransitionEdge>();
	}

	public Boolean addEdge(TransitionEdge edge) {
		final StateNode right =  edge.getRight();
		final StateNode left =  edge.getLeft();
		if (!(nodes.parallelStream().anyMatch(e -> e.equals(left)) || nodes.parallelStream().anyMatch(e -> e.equals(right)))) {
			return false;
		}
		if (edges.contains(edge)) {
			return false;
		}
		edges.add(edge);
		return true;
	}

	public Boolean addNode(StateNode node) {
		for (StateNode innerNode: nodes) {
			if (innerNode.getName().equals(node.getName())) {
				return false;
			};
		}
		nodes.add(node);
		return true;
	}

	public String toString() {
		StringBuilder graphSB = new StringBuilder();
		for (StateNode node: nodes) {
			graphSB.append(node.toString());
			graphSB.append(System.lineSeparator());
		}
		
		for (TransitionEdge te : edges) {
			graphSB.append(te.toString());
			graphSB.append(System.lineSeparator());
		}
		
		return graphSB.toString();
	}

	
	public Set<StateNode> getNodes() {
		return nodes;
	}

	
	public List<TransitionEdge> getEdges() {
		return edges;
	}

	public TransitionEdge getInitialTransition() {
		return edges.get(0);
	}

	public Collection<StateNode> getAcceptingStates() {
		Collection<StateNode> accNodes = new ArrayList<StateNode>();
		for (StateNode node : nodes) {
			if(node.getAccepting()) {
				accNodes.add(node);
			}
		}
		
		return accNodes;
	}

	public Collection<TransitionEdge> getAllTransitions() {
		return getEdges();
	}

	public StateMachineGraph simplify() {
		//TODO #15 Can be removed once 
		TransitionEdge initialTrans = getInitialTransition();
		StateNode intialState = initialTrans.from();
		Set<TransitionEdge> merge = new HashSet<>();
		merge.add(initialTrans);
		for(TransitionEdge t : getEdges()){
			if(!t.equals(initialTrans)){
				if(t.from().equals(intialState) && t.to().equals(initialTrans.to())){
					merge.add(t);
				}
			}
		}
		List<CryptSLMethod> mergedMethods = new LinkedList<>();
		edges.removeAll(merge);
		for(TransitionEdge e: merge){
			mergedMethods.addAll(e.getLabel());
		}
		edges.add(0, new TransitionEdge(mergedMethods, intialState, initialTrans.to()));
		return this;
	}
	
}
