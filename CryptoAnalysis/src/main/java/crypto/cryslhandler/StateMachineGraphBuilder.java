package crypto.cryslhandler;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import crypto.exceptions.CryptoAnalysisParserException;
import crypto.rules.CrySLMethod;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.rules.TransitionEdge;
import de.darmstadt.tu.crossing.crySL.Event;
import de.darmstadt.tu.crossing.crySL.Expression;
import de.darmstadt.tu.crossing.crySL.RequiredBlock;

/**
 * This class will build a {@FiniteStateMachine} for a given ORDER expression from crysl rules.
 * @author marvinvogel
 *
 */
public class StateMachineGraphBuilder {
	
	private final Expression order;
	private final StateMachineGraph result;
	private final Set<CrySLMethod> allMethods;
	
	public StateMachineGraphBuilder(final Expression order, final RequiredBlock events) {
		this.order = order;
		this.allMethods = retrieveAllMethodsFromEVENTSBlock(events);
		this.result = new StateMachineGraph();
	}
	
	private Set<CrySLMethod> retrieveAllMethodsFromEVENTSBlock(RequiredBlock events){
		return events.getReq_event().parallelStream().flatMap(event -> CryslReaderUtils.resolveAggregateToMethodeNames(event).stream()).collect(Collectors.toSet());
	}
	
	/**
	 * This method will build the state machine. It is called recursively.
	 * For a collection of start nodes, which must already be added to the @attr result (the StateMachineGraph), 
	 * it will append nodes and edges to result according to the given order and return all accepting end nodes.
	 * 
	 * @param order the order expression from crysl.
	 * @param startNodes nodes from which the order can start.
	 * @param ignoreElementOp if true, it will ignore elementop (elementop=('+' | '?' | '*')). Default is false.
	 * @return all accepting nodes, according to the order
	 */
	private Set<StateNode> parseOrderAndGetEndStates(Expression order, Collection<StateNode> startNodes) {
		/* Having a look at the Crysl Xtext definition for the Order section, we have
		 * -----------
		 *
		 * Order returns Expression:
		 * 	SimpleOrder ({Order.left=current} orderop=',' right=SimpleOrder)* | '*';
		 * 
		 * SimpleOrder returns Expression:
		 * 	Primary ({SimpleOrder.left=current} orderop='|' right=Primary)*;
		 * 
		 * Primary returns Expression:
		 * 	(orderEv+=[Event] elementop=('+' | '?' | '*')?) | ('(' Order ')' elementop=('+' | '?' | '*')?);
		 * 
		 * -----------
		 * Based on this definition, the method will parse the Order section.
		 * In detail, we seperate Order, SimpleOrder and orderEv from elementop.
		 * To simplify this documentation, we create a synonym "ROOTS" for Order, SimpleOrder and orderEv.
		 * As you see in the definition the elementop can only be defined for any ROOT.
		 * Further, the recursion happens within ROOT elements.
		 * With this method, we will recursively get all end nodes for a given set of start nodes for one ROOT.
		 * After retrieving all end nodes, it then applies the logic of the element operator.
		 * In detail: 
		 * 
		 * Given start nodes, we fist append ROOT's nodes and edges according to the order and will retrieve it's end nodes.
		 * Therefore, we have three cases:
		 * (Event): Event should be the next transition in graph. 
		 * 			We add edges from each start to a new node with Event as label.
		 * 			The new node is our end node.
		 * (,):		Left side should be called before right side.
		 * 			We recursively call this method with order.leftSide and retrieve end nodes.
		 * 			We recursively call this method with order.rightSide use the end nodes from order.leftSide as start nodes.
		 * 			We retrieve our final end nodes from the call with order.rightSide.
		 * (|):		Left side or right side should be called.
		 * 			We recursively call this method with order.leftSide and order.rightSide, with same start nodes.
		 * 			We retrieve our final end nodes by joining both return collections.
		 * 			To minimize the number of nodes, we aggregate all end nodes without outgoing edges to one node.
		 * 
		 * Having start and end nodes, we can then modify the graph such that it applies the elementop.
		 * Therefore, we have three cases:
		 * ()+: 	We have to create a loop, according the path in order on the end node.
		 * 			We calculate all new edges generated on the start nodes.
		 * 			These edges where generated according to the current order.
		 * 			We simply create new edges from each end node to each target nodes of these edges.
		 * ()?: 	All start nodes are also end nodes.
		 * ()*:		This is simply considered as (()+)?
		 */
		
		// store outgoing edges from one start node, to be able to calculate new created outgoing edges after the recursive call.
		StateNode oneStartNode = startNodes.iterator().next(); 
		Set<TransitionEdge> initialEdgesOnOneStartNode = this.result.getAllOutgoingEdges(oneStartNode);
		final Set<StateNode> endNodes = Sets.newHashSet();
		// first create nodes end edges according to ROOT's
		if(order.getOrderop() == null) {
			// This must be of type Primary
			// That is actually the end of recursion or the deepest level.
			StateNode endNode = this.result.createNewNode();
			endNodes.add(endNode);
			final List<CrySLMethod> label;
			if(!order.getOrderEv().isEmpty()) {
				// Event as label
				label = CryslReaderUtils.resolveAggregateToMethodeNames(order.getOrderEv().get(0));
			} else {
				// this corresponds to '*' in Order
				// any method is allowed to call
				label = Lists.newArrayList(allMethods);
			}
			// add edges from each start node to the new end node
			for(StateNode startNode: startNodes) {
				this.result.createNewEdge(label, startNode, endNode);
			}
		} else if(order.getOrderop().equals(",")) {
			// This must be of type Order
			endNodes.addAll(parseOrderAndGetEndStates(order.getRight(), parseOrderAndGetEndStates(order.getLeft(), startNodes)));
		} else if(order.getOrderop().equals("|")) {
			// This must by of type SimpleOrder
			endNodes.addAll(parseOrderAndGetEndStates(order.getLeft(), startNodes));
			endNodes.addAll(parseOrderAndGetEndStates(order.getRight(), startNodes));
			// reduce all end nodes without outgoing edges to one end node
			Set<StateNode> endNodesWithOutgoingEdges = this.result.getEdges().parallelStream().map(edge -> edge.from()).filter(node -> endNodes.contains(node)).collect(Collectors.toSet());
			if(endNodesWithOutgoingEdges.size() < endNodes.size()-1) {
				endNodes.removeAll(endNodesWithOutgoingEdges);
				StateNode aggrNode = this.result.aggregateNodesToOneNode(endNodes, endNodes.iterator().next());
				endNodes.clear();
				endNodes.add(aggrNode);
				endNodes.addAll(endNodesWithOutgoingEdges);
			}
			
		}
		
		// modify graph such that it applies elementop.
		String elementop = order.getElementop();
		if(elementop != null) {
			if(!elementop.equals("?")) {
				// elementop is "+" or "*"
				Set<TransitionEdge> newOutgoingEdgesOnStartNodes = this.result.getAllOutgoingEdges(oneStartNode);
				newOutgoingEdgesOnStartNodes.removeAll(initialEdgesOnOneStartNode);
				for(TransitionEdge newEdge: newOutgoingEdgesOnStartNodes) {
					endNodes.forEach(endNode -> this.result.createNewEdge(newEdge.getLabel(), endNode, newEdge.getRight()));	
				}
			}
			if(!elementop.equals("+")) {
				// elementop is "?" or "*"
				endNodes.addAll(startNodes);
			}
		}
		return endNodes;
	}
	
	public StateMachineGraph buildSMG() {
		StateNode initialNode = new StateNode("-1", true, true);
		this.result.addNode(initialNode);
		if (this.order != null) {
			Set<StateNode> acceptingNodes = parseOrderAndGetEndStates(this.order, Sets.newHashSet(this.result.getNodes()));
			acceptingNodes.parallelStream().forEach(node -> node.setAccepting(true));
		}
		if(this.result.getAllTransitions().isEmpty()) {
			// to have an initial transition
			this.result.createNewEdge(Lists.newArrayList(), initialNode, initialNode);
		}
		return this.result;
	}	
}