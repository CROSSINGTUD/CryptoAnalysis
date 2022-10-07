package crypto.cryslhandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import crypto.rules.CrySLMethod;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import de.darmstadt.tu.crossing.crySL.Event;
import de.darmstadt.tu.crossing.crySL.Order;
import de.darmstadt.tu.crossing.crySL.OrderOperator;
import de.darmstadt.tu.crossing.crySL.Primary;

/**
 * This class will build a {@FiniteStateMachine} for a given ORDER expression from crysl rules.
 * @author marvinvogel
 *
 */
public class StateMachineGraphBuilder {
	
	private final Order order;
	private final StateMachineGraph result;
	private final List<Event> events;
	private final Set<CrySLMethod> allMethods = Sets.newHashSet();
	
	public static StateMachineGraph buildSMG(final Order order, final List<Event> events) {
		return (new StateMachineGraphBuilder(order, events))
				.buildSMG();
	}

	protected StateMachineGraphBuilder(final Order order, final List<Event> events) {
		this.order = order;
		this.events = events;
		this.result = new StateMachineGraph();
	}

	protected StateMachineGraph buildSMG() {
		StateNode initialNode = new StateNode("-1", true, true);
		this.result.addNode(initialNode);
		SubStateMachine subSmg = buildSubSMG(this.order, Collections.singleton(initialNode));
		subSmg.getEndNodes().parallelStream().forEach(StateNode::makeAccepting);
		return this.result;
	}


	/**
	 * Helper class to store a {@link Set} of endNodes and startNodes.
	 */
	private static class SubStateMachine {

		private final Set<StateNode> startNodes;
		private final Set<StateNode> endNodes;

		public SubStateMachine(final StateNode startNode, final StateNode endNode) {
			this(Collections.singleton(startNode), Collections.singleton(endNode));
		}

		public SubStateMachine(final Set<StateNode> startNodes, final Set<StateNode> endNodes) {
			this.startNodes = Collections.unmodifiableSet(startNodes);
			this.endNodes = Collections.unmodifiableSet(endNodes);
		}

		public Set<StateNode> getStartNodes() {
			return this.startNodes;
		}
		public Set<StateNode> getEndNodes() {
			return this.endNodes;
		}
	}
	
	/**
	 * This method builds a {@link SubStateMachine} equivalent to the given {@link Order}.
	 *
	 * It is called recursively For a collection of start nodes,
	 * which must already be added to the {@link StateMachineGraph} result.
	 * 
	 * @param order The CrySL {@link Order} Expression to build the {@link SubStateMachine} for.
	 * @param startNodes Set of nodes used as the startNodes of this {@link SubStateMachine}.
	 * @return the {@link SubStateMachine} representing this {@link Order} Expression
	 *
	 * Having a look at the Crysl Xtext definition for the Order section, we have
	 * -----------
	 * <code>
	 * Sequence returns Order:
	 * 	Alternative ({Order.left=current} op=SequenceOperator right=Alternative)*
	 * ;
	 * 
	 * enum SequenceOperator returns OrderOperator:
	 * 	SEQUENCE = ','
	 * ;
	 * 
	 * Alternative returns Order:
	 * 	Cardinality ({Order.left=current} op=AlternativeOperator right=Cardinality)*
	 * ;
	 * 
	 * enum AlternativeOperator returns OrderOperator:
	 * 	ALTERNATIVE = '|'
	 * ;
	 * 
	 * Cardinality returns Order:
	 * 	Primary ({Order.left=current} op = CardinalityOperator)?
	 * ;
	 * 
	 * enum CardinalityOperator returns OrderOperator:
	 * 	ZERO_OR_MORE = '*' | ONE_OR_MORE = '+' | ZERO_OR_ONE = '?'
	 * ;
	 * 
	 * Primary returns Order:
	 * 	{Primary} event = [Event]
	 * 	| '(' Order ')'
	 * ;
	 * </code>
	 * -----------
	 *
	 * Based on this definition, the method will build the StateMachine from
	 * the Order section.
	 *
	 * This is done by recursively building a sub-StateMachine and connecting
	 * it with given start Nodes and returned end Nodes according to the specific OrderOperator.
	 *
	 * Therefore, consider the following cases
	 *
	 * 1. Order instanceof Primary:
	 * 	In this case, we create a new Node and add an Transistion for the Event
	 * 	from each start Node to the newly created Node.
	 * 	Finally, we return a SubStateMachine where the newly created node is the
	 * 	start and end Node.
	 *
	 * 2. OrderOperator == SEQUENCE:
	 * 	The left-side should occur before the right-side.
	 * 	We therefore recursively build the sub-StateMachine of the left-side
	 * 	with the given start Nodes saving the returned end Nodes.
	 * 	We then build the sub-StateMachine of the right-side giving it the
	 * 	left-side's end Nodes as start Nodes.
	 * 	Finally, we return the startNodes of the left-side's start Nodes as our
	 * 	start Nodes and the end Nodes of the right-side's sub-StateMachine
	 * 	as our end Nodes.
	 *
	 * 3. OrderOperator == ALTERNATIVE:
	 * 	Either the left-side or the right-side should occur.
	 * 	We therefore build both sub-StateMachines with our start Nodes as start
	 * 	Nodes.
	 * 	Finally, we return the aggregate of both start Nodes as our startNodes and
	 * 	the aggregates of both end Nodes as our end Nodes.
	 *
	 * 4. OrderOperator == ZERO_OR_ONE:
	 * 	The Event can occur or be skipped.
	 * 	We therefore build the sub-StateMachine (only the left-side is present)
	 * 	with our start Nodes as start Nodes.
	 * 	Finally, we return the returned start Nodes as our start
	 * 	Nodes and the returned Nodes end Nodes (event occurs) and our start
	 * 	nodes (event skipped) as end Nodes.
	 *
	 * 5. OrderOperator == ONE_OR_MORE:
	 * 	The Event can occur once or multiple times.
	 * 	We therefore build the sub-StateMachine (only the left-side is present)
	 * 	with our start Nodes as start Nodes and save the returned end Nodes.
	 * 	We then duplicate the transitions from each given start Node to the start
	 * 	node of the Sub-StateMachine, but not with the given start Node as origin,
	 * 	but each end Node of the Sub-StateMachine, this creates the desired loop.
	 * 	Finally, we return the returned start and end Nodes.
	 *
	 * 5. OrderOperator == ZERO_OR_MORE:
	 * 	This can be seen as (Order)*?.
	 * 	We therefore proceed as in ONE_OR_MORE but additionally return
	 * 	the given start Nodes as end Nodes aswell as in ZERO_OR_ONE.
	 *
	 */

	private SubStateMachine buildSubSMG(final Order order, final Set<StateNode> startNodes) {

		if(order == null) {
			// This is the case if the ORDER section was ommited.
			// It implies, that any method may be called in any sequence.
			// We therefore create a Node and a Transistion from all startNodes
			// to this node and a loop from the node to itself.
			StateNode node = this.result.createNewNode();
			List<CrySLMethod> label = new ArrayList<>(retrieveAllMethodsFromEvents());
			for(StateNode startNode: startNodes)
				this.result.createNewEdge(label, startNode, node);
			this.result.createNewEdge(label, node, node);
			return new SubStateMachine(node, node);
		}

		if(order instanceof Primary) {
			Event event = ((Primary) order).getEvent();
			StateNode node = this.result.createNewNode();
			List<CrySLMethod> label =
				CrySLReaderUtils.resolveEventToCryslMethods(event);
			for(StateNode startNode: startNodes)
				this.result.createNewEdge(label, startNode, node);
			return new SubStateMachine(node, node);
		}

		Set<StateNode> end = Sets.newHashSet();
		Set<StateNode> start = Sets.newHashSet();

		final SubStateMachine left;
		final SubStateMachine right;

		switch(order.getOp()) {
			case SEQUENCE:
				left = buildSubSMG(order.getLeft(), startNodes);
				right = buildSubSMG(order.getRight(), left.getEndNodes());
				start.addAll(left.getStartNodes());
				start.addAll(right.getStartNodes());
				end.addAll(left.getEndNodes());
				end.addAll(right.getEndNodes());
				break;
			case ALTERNATIVE:
					left = buildSubSMG(order.getLeft(), startNodes);
					right = buildSubSMG(order.getRight(), startNodes);
					// reduce all end nodes without outgoing edges to one end node
					Set<StateNode> endNodesWithOutgoingEdges = this.result.getEdges().parallelStream()
						.map(edge -> edge.from()).filter(node -> end.contains(node)).collect(Collectors.toSet());
					if(endNodesWithOutgoingEdges.size() < end.size()-1) {
						end.removeAll(endNodesWithOutgoingEdges);
						StateNode aggrNode = this.result.aggregateNodesToOneNode(end, end.iterator().next());
						end.clear();
						end.add(aggrNode);
						end.addAll(endNodesWithOutgoingEdges);
					}
				break;
			case ONE_OR_MORE:
			case ZERO_OR_MORE:
			case ZERO_OR_ONE:
				left = buildSubSMG(order.getLeft(), startNodes);
				start.addAll(left.getStartNodes());
				end.addAll(left.getEndNodes());
				if(order.getOp() == OrderOperator.ZERO_OR_ONE || order.getOp() == OrderOperator.ONE_OR_MORE) {
						startNodes.stream()
							.map(this.result::getAllOutgoingEdges).flatMap(Set::stream)
							.filter(edge -> left.getStartNodes().contains(edge.getRight()))
							.forEach(edge -> left.getEndNodes().stream()
									.map(endNode -> this.result.createNewEdge(edge.getLabel(), endNode, edge.getRight()))
							);
				}
				if(order.getOp() == OrderOperator.ZERO_OR_MORE || order.getOp() == OrderOperator.ZERO_OR_ONE) {
						end.addAll(startNodes);
				}
				break;
		}
		return new SubStateMachine(start, end);
	}
	
	private Set<CrySLMethod> retrieveAllMethodsFromEvents(){
		if(this.allMethods.isEmpty())
			this.allMethods.addAll(CrySLReaderUtils.resolveEventsToCryslMethods(this.events));
		return this.allMethods;
	}
	
}
