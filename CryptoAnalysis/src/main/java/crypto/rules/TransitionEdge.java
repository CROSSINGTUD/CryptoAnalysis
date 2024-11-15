package crypto.rules;

import java.util.Collection;

public class TransitionEdge implements Transition<StateNode> {

    private final StateNode left;
    private final StateNode right;
    private final Collection<CrySLMethod> methods;

    public TransitionEdge(Collection<CrySLMethod> _methods, StateNode _left, StateNode _right) {
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

    public Collection<CrySLMethod> getLabel() {
        return methods;
    }

    @Override
    public String toString() {
        return "Left: "
                + this.left.getName()
                + " ===="
                + methods
                + "====> Right:"
                + this.right.getName();
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
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        TransitionEdge other = (TransitionEdge) obj;
        if (methods == null) {
            if (other.methods != null) return false;
        } else if (!methods.equals(other.methods)) return false;
        if (left == null) {
            if (other.left != null) return false;
        } else if (!left.equals(other.left)) return false;
        if (right == null) {
            if (other.right != null) return false;
        } else if (!right.equals(other.right)) return false;
        return true;
    }
}
