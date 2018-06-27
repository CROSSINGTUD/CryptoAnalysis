package crypto.interfaces;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import crypto.rules.CryptSLMethod;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.rules.TransitionEdge;
import de.darmstadt.tu.crossing.cryptSL.Expression;
import de.darmstadt.tu.crossing.cryptSL.Order;
import de.darmstadt.tu.crossing.cryptSL.SimpleOrder;

public class StateMachineGraphBuilder {

	private final Expression head;
	private final StateMachineGraph result = new StateMachineGraph();
	private int nodeNameCounter = 0;

	public StateMachineGraphBuilder(final Expression order) {
		this.head = order;
		this.result.addNode(new StateNode("-1", true, true));
	}

	private StateNode addRegularEdge(final Expression leaf, final StateNode prevNode, final StateNode nextNode) {
		return addRegularEdge(leaf, prevNode, nextNode, false);
	}

	private StateNode addRegularEdge(final Expression leaf, final StateNode prevNode, final StateNode nextNode, final Boolean isStillAccepting) {
		final List<CryptSLMethod> label = CrySLReaderUtils.resolveAggregateToMethodeNames(leaf.getOrderEv().get(0));
		return addRegularEdge(label, prevNode, nextNode, isStillAccepting);
	}

	private StateNode addRegularEdge(final List<CryptSLMethod> label, final StateNode prevNode, StateNode nextNode, final Boolean isStillAccepting) {
		if (nextNode == null) {
			nextNode = getNewNode();
			this.result.addNode(nextNode);
		}
		if (!isStillAccepting) {
			prevNode.setAccepting(false);
		}
		this.result.addEdge(new TransitionEdge(label, prevNode, nextNode));
		return nextNode;
	}

	public StateMachineGraph buildSMG() {
		StateNode initialNode = null;
		for (final StateNode n : this.result.getNodes()) {
			initialNode = n;
		}
		if (this.head != null) {
			processHead(this.head, 0, HashMultimap.create(), initialNode);
		} else {
			this.result.addEdge(new TransitionEdge(new ArrayList<CryptSLMethod>(), initialNode, initialNode));
		}
		return this.result;
	}

	private StateNode getNewNode() {
		return new StateNode(String.valueOf(this.nodeNameCounter++), false, true);
	}

	private List<TransitionEdge> getOutgoingEdge(final StateNode curNode, final StateNode notTo) {
		final List<TransitionEdge> outgoingEdges = new ArrayList<>();
		for (final TransitionEdge comp : this.result.getAllTransitions()) {
			if (comp.getLeft().equals(curNode) && !(comp.getRight().equals(curNode) || comp.getRight().equals(notTo))) {
				outgoingEdges.add(comp);
			}
		}
		return outgoingEdges;
	}

	private StateNode isGeneric(final String el, final int level, final Multimap<Integer, Entry<String, StateNode>> leftOvers) {
		for (final Entry<String, StateNode> entry : leftOvers.get(level)) {
			if (el.equals(entry.getKey())) {
				return entry.getValue();
			}
		}
		return null;
	}

	private StateNode isOr(final int level, final Multimap<Integer, Entry<String, StateNode>> leftOvers) {
		return isGeneric("|", level, leftOvers);
	}

	private StateNode isQM(final int level, final Multimap<Integer, Entry<String, StateNode>> leftOvers) {
		return isGeneric("?", level, leftOvers);
	}

	private StateNode process(final Expression curLevel, final int level, final Multimap<Integer, Map.Entry<String, StateNode>> leftOvers, StateNode prevNode) {
		final Expression left = curLevel.getLeft();
		final Expression right = curLevel.getRight();
		final String leftElOp = (left != null) ? left.getElementop() : "";
		final String rightElOp = (right != null) ? right.getElementop() : "";
		final String orderOp = curLevel.getOrderop();
		//case 1 = left & right = non-leaf
		//case 2 = left = non-leaf & right = leaf
		//case 3 = left = leaf & right = non-leaf
		//case 4 = left = leaf & right = leaf

		if (left == null && right == null) {
			addRegularEdge(curLevel, prevNode, null);
		} else if ((left instanceof Order || left instanceof SimpleOrder) && (right instanceof Order || right instanceof SimpleOrder)) {
			final StateNode leftPrev = prevNode;
			prevNode = process(left, level + 1, leftOvers, prevNode);

			final StateNode rightPrev = prevNode;
			StateNode returnToNode = null;
			if ("|".equals(orderOp)) {
				leftOvers.put(level + 1, new HashMap.SimpleEntry<>(orderOp, prevNode));
				prevNode = process(right, level + 1, leftOvers, leftPrev);
			} else if ((returnToNode = isOr(level, leftOvers)) != null) {
				prevNode = process(right, level + 1, leftOvers, returnToNode);
			} else {
				prevNode = process(right, level + 1, leftOvers, prevNode);
			}

			if (rightElOp != null && ("+".equals(rightElOp) || "*".equals(rightElOp))) {
				List<TransitionEdge> outgoingEdges = new ArrayList<TransitionEdge>();
				if ("|".equals(orderOp)) {
					List<TransitionEdge> tmpOutgoingEdges = getOutgoingEdge(leftPrev, null);
					for (final TransitionEdge outgoingEdge : tmpOutgoingEdges) {
						if (isReachable(outgoingEdge.to(), prevNode, new ArrayList<StateNode>())) {
							outgoingEdges.addAll(getOutgoingEdge(outgoingEdge.to(), prevNode));
						}
					}
					for (final TransitionEdge outgoingEdge : outgoingEdges) {
						addRegularEdge(outgoingEdge.getLabel(), prevNode, outgoingEdge.from(), true);
					}

				} else {
					outgoingEdges.addAll(getOutgoingEdge(rightPrev, prevNode));
					for (final TransitionEdge outgoingEdge : outgoingEdges) {
						addRegularEdge(outgoingEdge.getLabel(), prevNode, outgoingEdge.to(), true);
					}
				}
			}

			if (leftElOp != null && ("?".equals(leftElOp) || "*".equals(leftElOp))) {
				addRegularEdge(right, leftPrev, prevNode, true);
			}

		} else if ((left instanceof Order || left instanceof SimpleOrder) && !(right instanceof Order || right instanceof SimpleOrder)) {
			prevNode = process(left, level + 1, leftOvers, prevNode);

			prevNode = addRegularEdge(right, prevNode, null);
		} else if (!(left instanceof Order || left instanceof SimpleOrder) && (right instanceof Order || right instanceof SimpleOrder)) {
			StateNode leftPrev = null;
			leftPrev = prevNode;
			prevNode = addRegularEdge(left, prevNode, null);

			if (leftElOp != null && ("+".equals(leftElOp) || "*".equals(leftElOp))) {
				addRegularEdge(left, prevNode, prevNode, true);
			}

			if (rightElOp != null && ("?".equals(rightElOp) || "*".equals(rightElOp))) {
				leftOvers.put(level - 1, new HashMap.SimpleEntry<>(rightElOp, prevNode));
			}
			final StateNode rightPrev = prevNode;
			StateNode returnToNode = null;
			if ("|".equals(orderOp)) {
				leftOvers.put(level + 1, new HashMap.SimpleEntry<>(orderOp, prevNode));
				prevNode = process(right, level + 1, leftOvers, leftPrev);
			} else {
				prevNode = process(right, level + 1, leftOvers, prevNode);
			}

			if (rightElOp != null && ("+".equals(rightElOp) || "*".equals(rightElOp))) {
				final List<TransitionEdge> outgoingEdges = getOutgoingEdge(rightPrev, prevNode);
				for (final TransitionEdge outgoingEdge : outgoingEdges) {
					addRegularEdge(outgoingEdge.getLabel(), prevNode, outgoingEdge.to(), true);
				}
			}

			if (leftElOp != null && ("?".equals(leftElOp) || "*".equals(leftElOp))) {
				addRegularEdge(right, leftPrev, prevNode, true);
			}

		} else if (!(left instanceof Order || left instanceof SimpleOrder) && !(right instanceof Order || right instanceof SimpleOrder)) {
			StateNode leftPrev = null;
			leftPrev = prevNode;
			StateNode returnToNode = isOr(level, leftOvers);

			prevNode = addRegularEdge(left, prevNode, null);

			if (leftElOp != null && ("+".equals(leftElOp) || "*".equals(leftElOp))) {
				addRegularEdge(left, prevNode, prevNode, true);
			}

			if (rightElOp != null && ("?".equals(rightElOp) || "*".equals(rightElOp))) {
				leftOvers.put(level - 1, new HashMap.SimpleEntry<>(rightElOp, prevNode));
			}
			if (returnToNode != null || "|".equals(orderOp)) {
				if ("|".equals(orderOp)) {
					addRegularEdge(right, leftPrev, prevNode);
				}
				if ((returnToNode = isOr(level, leftOvers)) != null) {
					prevNode = addRegularEdge(right, prevNode, returnToNode);
				}
			} else {
				prevNode = addRegularEdge(right, prevNode, null);
			}

			if (rightElOp != null && ("+".equals(rightElOp) || "*".equals(rightElOp))) {
				addRegularEdge(right, prevNode, prevNode, true);
			}

			if (leftElOp != null && ("?".equals(leftElOp) || "*".equals(leftElOp))) {
				addRegularEdge(right, leftPrev, prevNode, true);
			}
		}
		leftOvers.removeAll(level);
		return prevNode;
	}

	private boolean isReachable(StateNode stateNode, StateNode prevNode, List<StateNode> skippable) {
		for (TransitionEdge edge : getOutgoingEdge(stateNode, stateNode)) {
			if (edge.to().equals(prevNode)) {
				return true;
			} else if (!skippable.contains(edge.to())) {
				skippable.add(edge.to());
				return isReachable(edge.to(), prevNode, skippable);
			}
		}
		return false;
	}

	private void processHead(final Expression curLevel, final int level, final Multimap<Integer, Map.Entry<String, StateNode>> leftOvers, StateNode prevNode) {
		final Expression left = curLevel.getLeft();
		final Expression right = curLevel.getRight();
		final String leftElOp = (left != null) ? left.getElementop() : "";
		final String rightElOp = (right != null) ? right.getElementop() : "";
		final String orderOp = curLevel.getOrderop();

		if (left == null && right == null) {
			addRegularEdge(curLevel, prevNode, null);
		} else if ((left instanceof Order || left instanceof SimpleOrder) && (right instanceof Order || right instanceof SimpleOrder)) {
			final StateNode leftPrev = prevNode;
			prevNode = process(left, level + 1, leftOvers, prevNode);
			final StateNode rightPrev = prevNode;
			prevNode = process(right, level + 1, leftOvers, prevNode);

			if ("*".equals(rightElOp) || "?".equals(rightElOp)) {
				setAcceptingState(rightPrev);
			}
			if (rightElOp != null && ("+".equals(rightElOp) || "*".equals(rightElOp))) {
				final String orderop = right.getOrderop();
				List<TransitionEdge> outgoingEdges = null;
				if (orderop != null && "|".equals(orderop)) {
					outgoingEdges = getOutgoingEdge(rightPrev, null);
				} else {
					outgoingEdges = getOutgoingEdge(rightPrev, prevNode);
				}
				for (final TransitionEdge outgoingEdge : outgoingEdges) {
					addRegularEdge(outgoingEdge.getLabel(), prevNode, outgoingEdge.to(), true);
				}
			}

			if (leftElOp != null && ("?".equals(leftElOp) || "*".equals(leftElOp))) {
				addRegularEdge(right, leftPrev, prevNode, true);
			}
		} else if ((left instanceof Order || left instanceof SimpleOrder) && !(right instanceof Order || right instanceof SimpleOrder)) {
			final StateNode leftPrev = prevNode;
			prevNode = process(left, level + 1, leftOvers, prevNode);
			final StateNode rightPrev = prevNode;
			prevNode = addRegularEdge(right, prevNode, null);
			if ("*".equals(rightElOp) || "?".equals(rightElOp)) {
				setAcceptingState(rightPrev);
				if ("?".equals(left.getRight().getElementop()) || "*".equals(left.getRight().getElementop())) {
					final List<TransitionEdge> outgoingEdges = getOutgoingEdge(leftPrev, null);
					for (final TransitionEdge outgoingEdge : outgoingEdges) {
						setAcceptingState(outgoingEdge.to());
					}
				}
			}
			StateNode returnToNode = null;
			if ((returnToNode = isQM(level, leftOvers)) != null) {
				addRegularEdge(right, returnToNode, prevNode, true);
			}
		} else if (!(left instanceof Order || left instanceof SimpleOrder) && (right instanceof Order || right instanceof SimpleOrder)) {
			StateNode leftPrev = null;
			leftPrev = prevNode;
			prevNode = addRegularEdge(left, prevNode, null);

			if (leftElOp != null && ("+".equals(leftElOp) || "*".equals(leftElOp))) {
				addRegularEdge(left, prevNode, prevNode, true);
			}

			final StateNode rightPrev = prevNode;
			StateNode returnToNode = null;
			if (rightElOp != null && ("?".equals(rightElOp) || "*".equals(rightElOp))) {
				setAcceptingState(rightPrev);
			}
			if ("|".equals(orderOp)) {
				setAcceptingState(prevNode);
				prevNode = process(right, level + 1, leftOvers, leftPrev);
			} else if ((returnToNode = isOr(level, leftOvers)) != null) {
				prevNode = process(right, level + 1, leftOvers, returnToNode);
			} else {
				prevNode = process(right, level + 1, leftOvers, prevNode);
			}

			if (rightElOp != null && ("+".equals(rightElOp) || "*".equals(rightElOp))) {
				final List<TransitionEdge> outgoingEdges = getOutgoingEdge(rightPrev, null);
				for (final TransitionEdge outgoingEdge : outgoingEdges) {
					addRegularEdge(outgoingEdge.getLabel(), prevNode, outgoingEdge.to(), true);
				}
			}

			if (leftElOp != null && ("?".equals(leftElOp) || "*".equals(leftElOp))) {
				setAcceptingState(leftPrev);
				final List<TransitionEdge> outgoingEdges = getOutgoingEdge(rightPrev, null);
				for (final TransitionEdge outgoingEdge : outgoingEdges) {
					setAcceptingState(outgoingEdge.to());
					addRegularEdge(outgoingEdge.getLabel(), leftPrev, outgoingEdge.to(), true);
				}
			}
			if (rightElOp != null && ("?".equals(rightElOp) || "*".equals(rightElOp))) {
				setAcceptingState(rightPrev);
			}

		} else if (!(left instanceof Order || left instanceof SimpleOrder) && !(right instanceof Order || right instanceof SimpleOrder)) {
			StateNode leftPrev = null;
			leftPrev = prevNode;
			StateNode returnToNode = isOr(level, leftOvers);

			final boolean leftOptional = "?".equals(leftElOp) || "*".equals(leftElOp);
			prevNode = addRegularEdge(left, prevNode, null, leftOptional);

			if (leftElOp != null && ("+".equals(leftElOp) || "*".equals(leftElOp))) {
				addRegularEdge(left, prevNode, prevNode, true);
			}

			final boolean rightoptional = "?".equals(rightElOp) || "*".equals(rightElOp);
			if (returnToNode != null || "|".equals(orderOp)) {
				if ("|".equals(orderOp)) {
					addRegularEdge(right, leftPrev, prevNode, rightoptional);
				}
				if ((returnToNode = isOr(level, leftOvers)) != null) {
					prevNode = addRegularEdge(right, prevNode, returnToNode, rightoptional);
				}
			} else {
				prevNode = addRegularEdge(right, prevNode, null, rightoptional);
			}

			if (rightElOp != null && ("+".equals(rightElOp) || "*".equals(rightElOp))) {
				addRegularEdge(right, prevNode, prevNode, true);
			}

			if (leftElOp != null && ("?".equals(leftElOp) || "*".equals(leftElOp))) {
				addRegularEdge(right, leftPrev, prevNode, true);
			}
		}
	}

	private void setAcceptingState(final StateNode prevNode) {
		prevNode.setAccepting(true);
	}

}
