package crypto.rules;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

public final class StateMachineGraph implements FiniteStateMachine<StateNode> {

    private StateNode startNode;
    private final Collection<StateNode> nodes;
    private final Collection<TransitionEdge> edges;
    private final Collection<TransitionEdge> initialEdges;
    private int nodeNameCounter = 0;

    public StateMachineGraph() {
        nodes = new HashSet<>();
        edges = new ArrayList<>();
        initialEdges = new ArrayList<>();
    }

    public StateNode createNewNode() {
        StateNode node = new StateNode(String.valueOf(this.nodeNameCounter++), false, false);
        this.nodes.add(node);
        return node;
    }

    public boolean createNewEdge(Collection<CrySLMethod> methods, StateNode left, StateNode right) {
        return this.addEdge(new TransitionEdge(methods, left, right));
    }

    private Boolean addEdge(TransitionEdge edge) {
        final StateNode right = edge.getRight();
        final StateNode left = edge.getLeft();
        if (!(nodes.parallelStream().anyMatch(e -> e.equals(left))
                || nodes.parallelStream().anyMatch(e -> e.equals(right)))) {
            return false;
        }
        if (edges.contains(edge)) {
            return false;
        }
        edges.add(edge);

        if (left.isInitialState()) {
            initialEdges.add(edge);
        }

        return true;
    }

    public void wrapUpCreation() {
        getAcceptingStates().parallelStream()
                .forEach(
                        e -> {
                            e.setHopsToAccepting(0);
                            updateHops(e);
                        });
    }

    public Collection<TransitionEdge> getAllOutgoingEdges(StateNode node) {
        return edges.parallelStream()
                .filter(edge -> edge.from().equals(node))
                .collect(Collectors.toSet());
    }

    public void addAllOutgoingEdgesFromOneNodeToOtherNodes(
            StateNode node, Collection<StateNode> otherNodes) {
        Collection<TransitionEdge> edgesFromNode =
                edges.parallelStream()
                        .filter(e -> node.equals(e.from()))
                        .collect(Collectors.toList());
        otherNodes.forEach(
                otherNode ->
                        edgesFromNode.forEach(
                                edge ->
                                        this.createNewEdge(
                                                edge.getLabel(), otherNode, edge.getLeft())));
    }

    public StateNode aggregateNodesToOneNode(Collection<StateNode> endNodes, StateNode newNode) {
        this.aggregateNodesToOtherNodes(endNodes, Lists.newArrayList(newNode));
        return newNode;
    }

    public Collection<StateNode> aggregateNodesToOtherNodes(
            Collection<StateNode> nodesToAggr, Collection<StateNode> startNodes) {
        Collection<TransitionEdge> edgesToAnyAggrNode =
                edges.parallelStream()
                        .filter(e -> nodesToAggr.contains(e.to()))
                        .collect(Collectors.toList());
        // Add new edges to newNode instead of Aggr Node
        startNodes.forEach(
                node ->
                        edgesToAnyAggrNode.forEach(
                                edgeToAggrNode ->
                                        this.createNewEdge(
                                                edgeToAggrNode.getLabel(),
                                                edgeToAggrNode.getLeft(),
                                                node)));
        nodesToAggr.removeAll(startNodes);
        removeNodesWithAllEdges(nodesToAggr);
        return startNodes;
    }

    private void removeNodesWithAllEdges(Collection<StateNode> nodesToRemove) {
        nodesToRemove.forEach(this::removeNodeWithAllEdges);
    }

    private void removeNodeWithAllEdges(StateNode node) {
        removeAllEdgesHavingNode(node);
        nodes.remove(node);
    }

    private void removeAllEdgesHavingNode(StateNode node) {
        Collection<TransitionEdge> filteredEdges =
                edges.parallelStream()
                        .filter(e -> node.equals(e.to()) || node.equals(e.from()))
                        .collect(Collectors.toList());
        edges.removeAll(filteredEdges);
    }

    private void updateHops(StateNode node) {
        int newPath = node.getHopsToAccepting() + 1;
        getAllTransitions().parallelStream()
                .forEach(
                        e -> {
                            StateNode theNewRight = e.getLeft();
                            if (e.getRight().equals(node)
                                    && theNewRight.getHopsToAccepting() > newPath) {
                                theNewRight.setHopsToAccepting(newPath);
                                updateHops(theNewRight);
                            }
                        });
    }

    public Boolean addNode(StateNode node) {
        if (node.isInitialState()) {
            this.startNode = node;
        }
        return nodes.parallelStream().anyMatch(n -> n.getName().equals(node.getName()))
                ? false
                : nodes.add(node);
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

    public Collection<StateNode> getNodes() {
        return nodes;
    }

    public StateNode getStartNode() {
        return startNode;
    }

    public Collection<TransitionEdge> getEdges() {
        return edges;
    }

    public TransitionEdge getInitialTransition() {
        throw new UnsupportedOperationException(
                "There is no single initial transition. Use getInitialTransitions()");
    }

    public Collection<TransitionEdge> getInitialTransitions() {
        return initialEdges;
    }

    public Collection<StateNode> getAcceptingStates() {
        return nodes.parallelStream().filter(StateNode::getAccepting).collect(Collectors.toList());
    }

    public Collection<TransitionEdge> getAllTransitions() {
        return getEdges();
    }
}
