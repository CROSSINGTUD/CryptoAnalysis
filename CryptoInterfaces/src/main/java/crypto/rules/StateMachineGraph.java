package crypto.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import typestate.interfaces.FiniteStateMachine;


public final class StateMachineGraph implements FiniteStateMachine<StateNode>, java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final List<StateNode> nodes;
	private final List<TransitionEdge> edges;

	public StateMachineGraph() {
		nodes = new ArrayList<StateNode>();
		edges = new ArrayList<TransitionEdge>();
	}

	public Boolean addEdge(TransitionEdge edge) {
		if (!(nodes.contains(edge.getLeft()) || nodes.contains(edge.getRight()))) {
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

	
	public List<StateNode> getNodes() {
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
	
}
