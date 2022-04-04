package crypto.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
		final StateNode right = edge.getRight();
		final StateNode left = edge.getLeft();
		if (!(nodes.parallelStream().anyMatch(e -> e.equals(left)) || nodes.parallelStream().anyMatch(e -> e.equals(right)))) {
			return false;
		}
		if (edges.contains(edge)) {
			return false;
		}
		edges.add(edge);
		return true;
	}

	public void wrapUpCreation() {
		getAcceptingStates().parallelStream().forEach(e -> {
			e.setHopsToAccepting(0);
			updateHops(e);
		});
	}
	
	public StateNode aggregateNodesToOneNode(List<StateNode> nodesToAggr, StateNode newNode) {
		List<TransitionEdge> edgesToAnyAggrNode = edges.parallelStream().filter(e -> nodesToAggr.contains(e.to())).collect(Collectors.toList());
		// Add new edges to newNode instead of Aggr Node 
		edgesToAnyAggrNode.forEach(edgeToAggrNode -> this.addEdge(new TransitionEdge(edgeToAggrNode.getLabel(), edgeToAggrNode.getLeft(), newNode)));
		// remove Aggr nodes and edges
		nodesToAggr.remove(newNode);
		removeNodesWithAllEdges(nodesToAggr);
		return newNode;
	}
	
	public Collection<StateNode> aggregateNodestoOtherNodes(Collection<StateNode> nodesToAggr, Collection<StateNode> startNodes){
		List<TransitionEdge> edgesToAnyAggrNode = edges.parallelStream().filter(e -> nodesToAggr.contains(e.to())).collect(Collectors.toList());
		// Add new edges to newNode instead of Aggr Node 
		startNodes.forEach(node -> edgesToAnyAggrNode.forEach(edgeToAggrNode -> this.addEdge(new TransitionEdge(edgeToAggrNode.getLabel(), edgeToAggrNode.getLeft(), node))));
		nodesToAggr.removeAll(startNodes);
		removeNodesWithAllEdges(nodesToAggr);
		return startNodes;
	}
	
	private void removeNodesWithAllEdges(Collection<StateNode> nodesToRemove) {
		nodesToRemove.forEach(node -> removeNodeWithAllEdges(node));
	}
	
	private void removeNodeWithAllEdges(StateNode node) {
		removeAllEdgesWithNode(node);
		nodes.remove(node);
	}
	
	private void removeAllEdgesWithNode(StateNode node) {
		List<TransitionEdge> filteredEdges = edges.parallelStream().filter(e -> node.equals(e.to()) || node.equals(e.from())).collect(Collectors.toList());
		edges.removeAll(filteredEdges);
	}

	private void updateHops(StateNode node) {
		int newPath = node.getHopsToAccepting() + 1;
		getAllTransitions().parallelStream().forEach(e -> {
			StateNode theNewRight = e.getLeft();
			if (e.getRight().equals(node) && theNewRight.getHopsToAccepting() > newPath) {
				theNewRight.setHopsToAccepting(newPath);
				updateHops(theNewRight);
			}
		});
	}

	public Boolean addNode(StateNode node) {
		for (StateNode innerNode : nodes) {
			if (innerNode.getName().equals(node.getName())) {
				return false;
			}
		}
		nodes.add(node);
		return true;
	}

	public String toString() {
		StringBuilder graphSB = new StringBuilder();
		for (StateNode node : nodes) {
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
			if (node.getAccepting()) {
				accNodes.add(node);
			}
		}

		return accNodes;
	}

	public Collection<TransitionEdge> getAllTransitions() {
		return getEdges();
	}

	public StateMachineGraph simplify() {
		// TODO #15 Can be removed once
		TransitionEdge initialTrans = getInitialTransition();
		StateNode intialState = initialTrans.from();
		Set<TransitionEdge> merge = new HashSet<>();
		merge.add(initialTrans);
		for (TransitionEdge t : getEdges()) {
			if (!t.equals(initialTrans)) {
				if (t.from().equals(intialState) && t.to().equals(initialTrans.to())) {
					merge.add(t);
				}
			}
		}
		List<CrySLMethod> mergedMethods = new LinkedList<>();
		edges.removeAll(merge);
		for (TransitionEdge e : merge) {
			mergedMethods.addAll(e.getLabel());
		}
		edges.add(0, new TransitionEdge(mergedMethods, intialState, initialTrans.to()));
		return this;
	}

}
