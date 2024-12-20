package test.assertions;

import boomerang.scene.Statement;
import boomerang.scene.Val;
import crypto.analysis.AbstractPredicate;
import crypto.analysis.UnEnsuredPredicate;
import java.util.Collection;
import test.Assertion;

public class NotHasEnsuredPredicateAssertion implements Assertion {

    private final Statement stmt;
    private final Val val;
    private final String predName;
    private boolean imprecise = false;

    public NotHasEnsuredPredicateAssertion(Statement stmt, Val val) {
        this(stmt, val, null);
    }

    public NotHasEnsuredPredicateAssertion(Statement stmt, Val val, String predName) {
        this.stmt = stmt;
        this.val = val;
        this.predName = predName;
    }

    @Override
    public boolean isSatisfied() {
        return true;
    }

    @Override
    public boolean isImprecise() {
        return imprecise;
    }

    public Statement getStmt() {
        return stmt;
    }

    public void reported(Collection<Val> seed, AbstractPredicate pred) {
        if (!seed.contains(val) || pred instanceof UnEnsuredPredicate) {
            return;
        }

        if (predName == null || pred.getPredicate().getPredName().equals(predName)) {
            imprecise = true;
        }
    }

    @Override
    public String toString() {
        if (predName == null) {
            return "Did not expect a predicate for "
                    + val.getVariableName()
                    + " @ "
                    + stmt
                    + " @ line "
                    + stmt.getStartLineNumber();
        } else {
            return "Did not expect '"
                    + predName
                    + "' ensured on "
                    + val.getVariableName()
                    + " @ "
                    + stmt
                    + " @ line "
                    + stmt.getStartLineNumber();
        }
    }
}
