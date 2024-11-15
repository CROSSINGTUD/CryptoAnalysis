package crypto.rules;

import java.util.Collection;
import java.util.List;

public class CrySLCondPredicate extends CrySLPredicate {

    private final Collection<StateNode> conditionalNodes;

    public CrySLCondPredicate(
            ICrySLPredicateParameter baseObj,
            String name,
            List<ICrySLPredicateParameter> parameters,
            Boolean negated,
            Collection<StateNode> nodes) {
        this(baseObj, name, parameters, negated, nodes, null);
    }

    public CrySLCondPredicate(
            ICrySLPredicateParameter baseObj,
            String name,
            List<ICrySLPredicateParameter> parameters,
            Boolean negated,
            Collection<StateNode> nodes,
            ISLConstraint constraint) {
        super(baseObj, name, parameters, negated, constraint);
        this.conditionalNodes = nodes;
    }

    /**
     * @return the conditionalMethods
     */
    public Collection<StateNode> getConditionalMethods() {
        return conditionalNodes;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;

        if (!(obj instanceof CrySLCondPredicate)) return false;

        CrySLCondPredicate other = (CrySLCondPredicate) obj;
        if (!getConditionalMethods().equals(other.getConditionalMethods())) return false;

        return true;
    }

    public String toString() {
        return super.toString() + " after " + conditionalNodes;
    }
}
