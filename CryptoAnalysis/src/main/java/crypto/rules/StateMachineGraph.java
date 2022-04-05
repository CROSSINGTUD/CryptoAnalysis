package crypto.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import crypto.interfaces.FiniteStateMachine;

public final class StateMachineGraph implements FiniteStateMachine<StateNode>, java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Set<StateNode> nodes;
	private final List<TransitionEdge> edges;
	private int nodeNameCounter = 0;

	public StateMachineGraph() {
		nodes = new HashSet<StateNode>();
		edges = new ArrayList<TransitionEdge>();
	}
	
	public StateNode createNewNode() {
		StateNode node = new StateNode(String.valueOf(this.nodeNameCounter++), false, false);
		this.nodes.add(node);
		return node;
	}
	
	public boolean createNewEdge(List<CrySLMethod> methods, StateNode left, StateNode right) {
		return this.addEdge(new TransitionEdge(methods, left, right));
	}

	private Boolean addEdge(TransitionEdge edge) {
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
		this.aggregateNodestoOtherNodes(nodesToAggr, Lists.newArrayList(newNode));
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
		removeAllEdgesHavingNode(node);
		nodes.remove(node);
	}
	
	private void removeAllEdgesHavingNode(StateNode node) {
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
		return nodes.parallelStream().anyMatch(n -> n.getName().equals(node.getName())) ? false : nodes.add(node);
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
		return nodes.parallelStream().filter(node -> node.getAccepting()).collect(Collectors.toList());
	}

	public Collection<TransitionEdge> getAllTransitions() {
		return getEdges();
	}

}
