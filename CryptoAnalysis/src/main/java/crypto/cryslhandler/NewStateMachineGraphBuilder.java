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

public class NewStateMachineGraphBuilder {
	
	private final Expression head;
	private final StateMachineGraph result = new StateMachineGraph();
	private int nodeNameCounter = 0;

	public NewStateMachineGraphBuilder(final Expression order) {
		this.head = order;
		this.result.addNode(new StateNode("-1", true, true));
	}
	
	private List<StateNode> parseOrderAndGetEndStates(Expression order, Collection<StateNode> startNodes, boolean ignoreElementOp) {
		List<StateNode> endNodes = Lists.newArrayList();
		if(order.getOrderop() == null) {
			// Primary
			// (orderEv+=[Event] elementop=('+' | '?' | '*')?) | ('(' Order ')' elementop=('+' | '?' | '*')?);
			if(!order.getOrderEv().isEmpty()) {
				StateNode endNode = this.getNewNode();
				this.result.addNode(endNode);
				endNodes.add(endNode);
				parseEvent(order.getOrderEv().get(0), startNodes, endNode);
			}
			else {
				//parser error
			}
		}
		else if(order.getOrderop().equals(",")) {
			// Order
			// SimpleOrder ({Order.left=current} orderop=',' right=SimpleOrder)* | '*';
			List<StateNode> leftEndNodes =  parseOrderAndGetEndStates(order.getLeft(), startNodes, false);
			endNodes = parseOrderAndGetEndStates(order.getRight(), leftEndNodes, false);
		}
		else if(order.getOrderop().equals("|")) {
			// SimpleOrder
			// Primary ({SimpleOrder.left=current} orderop='|' right=Primary)*;
			List<StateNode> leftEndNodes =  parseOrderAndGetEndStates(order.getLeft(), startNodes, false);
			endNodes = parseOrderAndGetEndStates(order.getRight(), startNodes, false);
			endNodes.addAll(leftEndNodes);
		}
		String elementop = order.getElementop();
		if(!(elementop == null || ignoreElementOp)) {
			if(elementop.equals("+")) {
				// start --> end node
				// end node --> end node
				for(StateNode endNode: endNodes) {
					List<StateNode> endNotesToAggr = parseOrderAndGetEndStates(order, Lists.newArrayList(endNode), true);
					this.result.aggregateNodesToOneNode(endNotesToAggr, endNode);
				}
			}
			else if(elementop.equals("*")){
				// start --> start
				// start = end node
				endNodes = Lists.newArrayList(this.result.aggregateNodestoOtherNodes(endNodes, startNodes));
			}
			else if(elementop.equals("?")) {
				// start --> end node
				// start & end node = end nodes
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
		if (this.head != null) {
			List<StateNode> acceptingNodes = parseOrderAndGetEndStates(this.head, this.result.getNodes(), false);
			acceptingNodes.parallelStream().forEach(node -> node.setAccepting(true));
		}
		if(this.result.getAllTransitions().isEmpty()) {
			this.result.addEdge(new TransitionEdge(new ArrayList<CrySLMethod>(), initialNode, initialNode));
		}
		return this.result;
	}
	
}
