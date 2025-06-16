/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package test.assertions;

import boomerang.scope.Statement;
import boomerang.scope.Val;
import crypto.predicates.AbstractPredicate;
import crypto.predicates.UnEnsuredPredicate;
import java.util.Collection;

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
    public boolean isUnsound() {
        return false;
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
    public String getErrorMessage() {
        if (predName == null) {
            return "Did not expect a predicate for "
                    + val.getVariableName()
                    + " @ "
                    + stmt
                    + " @ line "
                    + stmt.getLineNumber();
        } else {
            return "Did not expect '"
                    + predName
                    + "' ensured on "
                    + val.getVariableName()
                    + " @ "
                    + stmt
                    + " @ line "
                    + stmt.getLineNumber();
        }
    }
}
