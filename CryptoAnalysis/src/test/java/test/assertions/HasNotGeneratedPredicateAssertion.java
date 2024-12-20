package test.assertions;

import boomerang.scene.Statement;
import boomerang.scene.Val;
import crypto.analysis.AbstractPredicate;
import crypto.analysis.UnEnsuredPredicate;
import java.util.Collection;
import test.Assertion;

public class HasNotGeneratedPredicateAssertion implements Assertion {

    private final Statement statement;
    private final Val val;
    private boolean imprecise = false;

    public HasNotGeneratedPredicateAssertion(Statement statement, Val val) {
        this.statement = statement;
        this.val = val;
    }

    @Override
    public boolean isSatisfied() {
        return true;
    }

    @Override
    public boolean isImprecise() {
        return imprecise;
    }

    public Statement getStatement() {
        return statement;
    }

    public void reported(Collection<Val> seed, AbstractPredicate predicate) {
        if (seed.contains(val) && !(predicate instanceof UnEnsuredPredicate)) {
            imprecise = true;
        }
    }

    @Override
    public String toString() {
        return "Did not expected "
                + val.getVariableName()
                + " to generate a predicate @ "
                + statement
                + " @ line "
                + statement.getStartLineNumber();
    }
}
