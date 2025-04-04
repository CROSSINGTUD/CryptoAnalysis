/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package test.assertions.states;

import boomerang.scope.Statement;
import boomerang.scope.Val;
import java.util.Collection;
import java.util.Objects;
import test.assertions.Assertion;
import typestate.finiteautomata.State;

public abstract class StateResult implements Assertion {

    protected final Statement statement;
    protected final Val seed;

    public StateResult(Statement statement, Val seed) {
        this.statement = statement;
        this.seed = seed;
    }

    public Statement getStmt() {
        return statement;
    }

    public Val getSeed() {
        return seed;
    }

    @Override
    public boolean isImprecise() {
        return false;
    }

    public abstract void computedStates(Collection<State> states);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StateResult that = (StateResult) o;
        return Objects.equals(statement, that.statement) && Objects.equals(seed, that.seed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statement, seed);
    }
}
