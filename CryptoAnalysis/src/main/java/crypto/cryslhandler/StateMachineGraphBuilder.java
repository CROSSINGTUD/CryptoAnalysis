package crypto.cryslhandler;

import com.google.common.collect.Sets;
import crypto.rules.CrySLMethod;
import crypto.rules.FiniteStateMachine;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import de.darmstadt.tu.crossing.crySL.Event;
import de.darmstadt.tu.crossing.crySL.Order;
import de.darmstadt.tu.crossing.crySL.OrderOperator;
import de.darmstadt.tu.crossing.crySL.Primary;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * This class will build a {@link FiniteStateMachine} for a given ORDER expression from crysl rules.
 * @author marvinvogel
 *
 */
public class StateMachineGraphBuilder {
	
	private final Order order;
	private final StateMachineGraph result;
	private final Collection<Event> events;
	private final Collection<CrySLMethod> allMethods = Sets.newHashSet();
	
	public static StateMachineGraph buildSMG(final Order order, final Collection<Event> events) {
		return (new StateMachineGraphBuilder(order, events))
				.buildSMG();
	}

	protected StateMachineGraphBuilder(final Order order, final Collection<Event> events) {
		this.order = order;
		this.events = events;
		this.result = new StateMachineGraph();
	}

	protected StateMachineGraph buildSMG() {
		StateNode initialNode = new StateNode("-1", true, false);
		this.result.addNode(initialNode);
		SubStateMachine subSmg = buildSubSMG(this.order, Collections.singleton(initialNode));
		subSmg.getEndNodes().parallelStream().forEach(StateNode::makeAccepting);
		return this.result;
	}

	/**
	 * Helper class to store a {@link Collection} of endNodes and startNodes.
	 */
	private static class SubStateMachine {

		private final Collection<StateNode> startNodes;
		private final Collection<StateNode> endNodes;

		public SubStateMachine(final StateNode startNode, final StateNode endNode) {
			this(Collections.singleton(startNode), Collections.singleton(endNode));
		}

		public SubStateMachine(final Collection<StateNode> startNodes, final Collection<StateNode> endNodes) {
			this.startNodes = startNodes;
			this.endNodes = endNodes;
		}

		public Collection<StateNode> getStartNodes() {
			return this.startNodes;
		}

		public Collection<StateNode> getEndNodes() {
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
	 *         -----------
	 *
	 *         Based on this definition, the method will build the StateMachine from
	 *         the Order section.
	 *
	 *         This is done by recursively building a sub-StateMachine and
	 *         connecting
	 *         it with given start Nodes and returned end Nodes according to the
	 *         specific OrderOperator.
	 *
	 *         Therefore, consider the following cases
	 *
	 *         1. Order instanceof Primary:
	 *         In this case, we create a new Node and add an Transistion for the
	 *         Event
	 *         from each start Node to the newly created Node.
	 *         Finally, we return a SubStateMachine where the newly created node is
	 *         the
	 *         start and end Node.
	 *
	 *         2. OrderOperator == SEQUENCE:
	 *         The left-side should occur before the right-side.
	 *         We therefore recursively build the sub-StateMachine of the left-side
	 *         with the given start Nodes saving the returned end Nodes.
	 *         We then build the sub-StateMachine of the right-side giving it the
	 *         left-side's end Nodes as start Nodes.
	 *         Finally, we return the startNodes of the left-side's start Nodes as
	 *         our
	 *         start Nodes and the end Nodes of the right-side's sub-StateMachine
	 *         as our end Nodes.
	 *
	 *         3. OrderOperator == ALTERNATIVE:
	 *         Either the left-side or the right-side should occur.
	 *         We therefore build both sub-StateMachines with our start Nodes as
	 *         start
	 *         Nodes.
	 *         Finally, we return the aggregate of both start Nodes as our
	 *         startNodes and
	 *         the aggregates of both end Nodes as our end Nodes.
	 *
	 *         4. OrderOperator == ZERO_OR_ONE:
	 *         The Event can occur or be skipped.
	 *         We therefore build the sub-StateMachine (only the left-side is
	 *         present)
	 *         with our start Nodes as start Nodes.
	 *         Finally, we return the returned start Nodes as our start
	 *         Nodes and the returned Nodes end Nodes (event occurs) and our start
	 *         nodes (event skipped) as end Nodes.
	 *
	 *         5. OrderOperator == ONE_OR_MORE:
	 *         The Event can occur once or multiple times.
	 *         We therefore build the sub-StateMachine (only the left-side is
	 *         present)
	 *         with our start Nodes as start Nodes and save the returned end Nodes.
	 *         We then duplicate the transitions from each given start Node to the
	 *         start
	 *         node of the Sub-StateMachine, but not with the given start Node as
	 *         origin,
	 *         but each end Node of the Sub-StateMachine, this creates the desired
	 *         loop.
	 *         Finally, we return the returned start and end Nodes.
	 *
	 *         5. OrderOperator == ZERO_OR_MORE:
	 *         This can be seen as (Order)*?.
	 *         We therefore proceed as in ONE_OR_MORE but additionally return
	 *         the given start Nodes as end Nodes aswell as in ZERO_OR_ONE.
	 *
	 */

	private SubStateMachine buildSubSMG(final Order order, final Collection<StateNode> startNodes) {

		if (order == null) {
			// This is the case if the ORDER section was omitted.
			// It implies, that any method may be called in any sequence.
			// We therefore create a Node and a transition from all startNodes
			// to this node and a loop from the node to itself.
			StateNode node = this.result.createNewNode();
			Collection<CrySLMethod> label = new HashSet<>(retrieveAllMethodsFromEvents());
			
			for (StateNode startNode : startNodes) {
				this.result.createNewEdge(label, startNode, node);
			}
			
			this.result.createNewEdge(label, node, node);
			return new SubStateMachine(Collections.singleton(node), startNodes);
		}

		if (order instanceof Primary) {
			Event event = ((Primary) order).getEvent();
			StateNode node = this.result.createNewNode();
			Collection<CrySLMethod> label = CrySLReaderUtils.resolveEventToCryslMethods(event);
			
			for (StateNode startNode : startNodes) {
				this.result.createNewEdge(label, startNode, node);
			}
			
			return new SubStateMachine(node, node);
		}

		Collection<StateNode> end = Sets.newHashSet();
		Collection<StateNode> start = Sets.newHashSet();

		final SubStateMachine left;
		final SubStateMachine right;

		switch (order.getOp()) {
			case SEQUENCE:
				left = buildSubSMG(order.getLeft(), startNodes);
				right = buildSubSMG(order.getRight(), left.getEndNodes());
				start.addAll(left.getStartNodes());
				end.addAll(right.getEndNodes());
				break;
			case ALTERNATIVE:
				left = buildSubSMG(order.getLeft(), startNodes);
				right = buildSubSMG(order.getRight(), startNodes);
				start.addAll(left.getStartNodes());
				start.addAll(right.getStartNodes());
				end.addAll(left.getEndNodes());
				end.addAll(right.getEndNodes());
				// TODO For some reason, this part removes loops in accepting states
				// reduce all end nodes without outgoing edges to one end node
//				Set<StateNode> endNodesWithOutgoingEdges = this.result.getEdges().parallelStream()
//						.map(edge -> edge.from()).filter(node -> end.contains(node)).collect(Collectors.toSet());
//				if (endNodesWithOutgoingEdges.size() < end.size() - 1) {
//					end.removeAll(endNodesWithOutgoingEdges);
//					StateNode aggrNode = this.result.aggregateNodesToOneNode(end, end.iterator().next());
//					end.clear();
//					end.add(aggrNode);
//					end.addAll(endNodesWithOutgoingEdges);
//				}
				break;
			case ONE_OR_MORE:
			case ZERO_OR_MORE:
			case ZERO_OR_ONE:
				left = buildSubSMG(order.getLeft(), startNodes);
				start.addAll(left.getStartNodes());
				end.addAll(left.getEndNodes());
				if (order.getOp() == OrderOperator.ZERO_OR_MORE || order.getOp() == OrderOperator.ONE_OR_MORE) {
					startNodes.stream()
							.map(this.result::getAllOutgoingEdges).flatMap(Collection::stream)
							.filter(edge -> left.getStartNodes().contains(edge.getRight()))
							.forEach(edge -> left.getEndNodes().stream()
									.forEach(endNode -> this.result.createNewEdge(edge.getLabel(), endNode, edge.getRight())));
				}
				if (order.getOp() == OrderOperator.ZERO_OR_MORE || order.getOp() == OrderOperator.ZERO_OR_ONE) {
					end.addAll(startNodes);
				}
				break;
		}
		return new SubStateMachine(start, end);
	}

	private Collection<CrySLMethod> retrieveAllMethodsFromEvents() {
		if (this.allMethods.isEmpty())
			this.allMethods.addAll(CrySLReaderUtils.resolveEventsToCryslMethods(this.events));
		return this.allMethods;
	}

}
