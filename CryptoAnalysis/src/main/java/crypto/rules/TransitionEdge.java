package crypto.rules;
import java.util.List;

import crypto.interfaces.Transition;

public class TransitionEdge implements Transition<StateNode>, java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private StateNode left = null;
	private StateNode right = null;
	private List<CrySLMethod> methods = null;

	public TransitionEdge(List<CrySLMethod> _methods, StateNode _left, StateNode _right) {
		left = _left;
		right = _right;
		methods = _methods;
	}

	public StateNode getLeft() {
		return left;
	}

	public StateNode getRight() {
		return right;
	}

	public List<CrySLMethod> getLabel() {
		return methods;
	}

	public String toString() {
		StringBuilder edgeSB = new StringBuilder();
		edgeSB.append("Left: ");
		edgeSB.append(this.left.getName());
		edgeSB.append(" ====");
		edgeSB.append(methods);
		edgeSB.append("====> Right:");
		edgeSB.append(this.right.getName());
		return edgeSB.toString();
	}

	public StateNode from() {
		return left;
	}

	public StateNode to() {
		return right;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((methods == null) ? 0 : methods.hashCode());
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransitionEdge other = (TransitionEdge) obj;
		if (methods == null) {
			if (other.methods != null)
				return false;
		} else if (!methods.equals(other.methods))
			return false;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		return true;
	}
	
}
