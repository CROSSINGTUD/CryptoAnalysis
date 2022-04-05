package crypto.cryslhandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

import crypto.rules.CrySLMethod;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.rules.TransitionEdge;
import de.darmstadt.tu.crossing.crySL.Event;
import de.darmstadt.tu.crossing.crySL.Expression;

/**
 * This class will build a {@FiniteStateMachine} for a given ORDER expression from crysl rules.
 * @author marvinvogel
 *
 */
public class NewStateMachineGraphBuilder {
	
	private final Expression order;
	private final StateMachineGraph result = new StateMachineGraph();
	private int nodeNameCounter = 0;

	public NewStateMachineGraphBuilder(final Expression order) {
		this.order = order;
		this.result.addNode(new StateNode("-1", true, true));
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
	private List<StateNode> parseOrderAndGetEndStates(Expression order, Collection<StateNode> startNodes, boolean ignoreElementOp) {
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
		 * As you see in the definition the elementop can be defined for any ROOT.
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
		 * 
		 * Having start and end nodes, we can then modify the graph such that it applies the elementop.
		 * Therefore, we have three cases:
		 * ()*:		End nodes will be aggregated to the start nodes. 
		 * 			In detail, for each transition to end nodes, a transition to each start node is created.
		 * 			The end notes will be removed from the graph and new end nodes are the start nodes.
		 * ()?: 	All start nodes are also end nodes.
		 * ()+: 	This creates a loop on the end nodes. In detail, we will recursively call this method with same order,
		 * 			but with end nodes as start nodes.
		 * 			We set the ignoreElementOp flag, to not end in infinity loop.
		 * 			Then we apply same as on ()*.
		 */
		List<StateNode> endNodes = Lists.newArrayList();
		if(order.getOrderop() == null) {
			// This must by of type Primary
			// (orderEv+=[Event] elementop=('+' | '?' | '*')?) | ('(' Order ')' elementop=('+' | '?' | '*')?);
			if(!order.getOrderEv().isEmpty()) {
				// That is actually the end of recursion or the deepest level.
				// 
				StateNode endNode = this.getNewNode();
				this.result.addNode(endNode);
				endNodes.add(endNode);
				parseEvent(order.getOrderEv().get(0), startNodes, endNode);
			} else {
				//parser error: expected to have entries in orderEv
			}
		} else if(order.getOrderop().equals(",")) {
			// This must by of type Order
			// SimpleOrder ({Order.left=current} orderop=',' right=SimpleOrder)* | '*';
			List<StateNode> leftEndNodes =  parseOrderAndGetEndStates(order.getLeft(), startNodes, false);
			endNodes = parseOrderAndGetEndStates(order.getRight(), leftEndNodes, false);
		} else if(order.getOrderop().equals("|")) {
			// This must by of type SimpleOrder
			// Primary ({SimpleOrder.left=current} orderop='|' right=Primary)*;
			List<StateNode> leftEndNodes =  parseOrderAndGetEndStates(order.getLeft(), startNodes, false);
			endNodes = parseOrderAndGetEndStates(order.getRight(), startNodes, false);
			endNodes.addAll(leftEndNodes);
		}
		
		// modify graph such that it applies elementop.
		String elementop = order.getElementop();
		if(!(elementop == null || ignoreElementOp)) {
			if(elementop.equals("+")) {
				List<StateNode> endNotesToAggr = parseOrderAndGetEndStates(order, endNodes, true);
				this.result.aggregateNodestoOtherNodes(endNotesToAggr, endNodes);
			} else if(elementop.equals("*")){
				endNodes = Lists.newArrayList(this.result.aggregateNodestoOtherNodes(endNodes, startNodes));
			} else if(elementop.equals("?")) {
				endNodes.addAll(startNodes);
			}
		}
		return endNodes;
	}
	
	private void parseEvent(Event event, Collection<StateNode> startNodes, StateNode endNode) {
		for(StateNode startState: startNodes) {
			parseEvent(event, startState, endNode);
		}
	}
	
	private void parseEvent(Event event, StateNode startNode, StateNode endNode) {
		final List<CrySLMethod> label = CryslReaderUtils.resolveAggregateToMethodeNames(event);
		this.result.addEdge(new TransitionEdge(label, startNode, endNode));
	}
	
	private StateNode getNewNode() {
		return new StateNode(String.valueOf(this.nodeNameCounter++), false, false);
	}

	public StateMachineGraph buildSMG() {
		StateNode initialNode = null;
		for(StateNode s: this.result.getNodes()) {
			initialNode = s;
		}
		if (this.order != null) {
			List<StateNode> acceptingNodes = parseOrderAndGetEndStates(this.order, this.result.getNodes(), false);
			acceptingNodes.parallelStream().forEach(node -> node.setAccepting(true));
		}
		if(this.result.getAllTransitions().isEmpty()) {
			this.result.addEdge(new TransitionEdge(new ArrayList<CrySLMethod>(), initialNode, initialNode));
		}
		return this.result;
	}
	
}
