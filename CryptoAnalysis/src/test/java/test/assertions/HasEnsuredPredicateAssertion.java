package test.assertions;

import boomerang.scene.Statement;
import boomerang.scene.Val;
import crypto.analysis.AbstractPredicate;
import crypto.analysis.HiddenPredicate;
import java.util.Collection;
import test.Assertion;

public class HasEnsuredPredicateAssertion implements Assertion {

    private final Statement stmt;
    private final Val val;
    private final String predName;
    private boolean satisfied;

    public HasEnsuredPredicateAssertion(Statement stmt, Val val) {
        this(stmt, val, null);
    }

    public HasEnsuredPredicateAssertion(Statement stmt, Val val, String predName) {
        this.stmt = stmt;
        this.val = val;
        this.predName = predName;
    }

    @Override
    public boolean isSatisfied() {
        return satisfied;
    }

    @Override
    public boolean isImprecise() {
        return false;
    }

    public Statement getStmt() {
        return stmt;
    }

    public void reported(Collection<Val> seed, AbstractPredicate pred) {
        if (!seed.contains(val) || pred instanceof HiddenPredicate) {
            return;
        }

        if (predName == null || pred.getPredicate().getPredName().equals(predName)) {
            satisfied = true;
        }
    }

    @Override
    public String toString() {
        if (predName == null) {
            return "Expected a predicate for " + val.getVariableName() + " @ " + stmt;
        } else {
            return "Expected '" + predName + "' ensured on " + val.getVariableName() + " @ " + stmt;
        }
    }
}
