/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.extractparameter;

import boomerang.scope.Statement;
import boomerang.scope.Type;
import boomerang.scope.Val;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public class TransformedValue {

    private final Val transformedVal;
    private final Statement statement;
    private final Collection<TransformedValue> knownValues;
    private final Collection<TransformedValue> unknownValues;
    private Collection<Type> trackedTypes;

    public TransformedValue(Val transformedVal, Statement statement) {
        this(transformedVal, statement, Collections.emptySet(), Collections.emptySet());
    }

    public TransformedValue(Val transformedVal, Statement statement, TransformedValue origin) {
        this(transformedVal, statement, Collections.singleton(origin), Collections.emptySet());
    }

    public TransformedValue(
            Val transformedVal, Statement statement, Collection<TransformedValue> knownValues) {
        this(transformedVal, statement, knownValues, Collections.emptySet());
    }

    public TransformedValue(
            Val transformedVal,
            Statement statement,
            Collection<TransformedValue> knownValues,
            Collection<TransformedValue> unknownValues) {
        this.transformedVal = transformedVal;
        this.statement = statement;
        this.knownValues = knownValues;
        this.unknownValues = unknownValues;
    }

    public Val getTransformedVal() {
        return transformedVal;
    }

    public Statement getStatement() {
        return statement;
    }

    public Collection<TransformedValue> getKnownValues() {
        return knownValues;
    }

    public Collection<TransformedValue> getUnknownValues() {
        return unknownValues;
    }

    public Collection<Type> getTrackedTypes() {
        if (trackedTypes == null) {
            trackedTypes = new HashSet<>();
            trackedTypes.add(transformedVal.getType());

            Queue<TransformedValue> workList = new LinkedList<>(knownValues);
            while (!workList.isEmpty()) {
                TransformedValue currValue = workList.poll();
                trackedTypes.add(currValue.getTransformedVal().getType());

                workList.addAll(currValue.getKnownValues());
            }
        }

        return trackedTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransformedValue value = (TransformedValue) o;
        return Objects.equals(transformedVal, value.transformedVal)
                && Objects.equals(statement, value.statement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transformedVal, statement);
    }

    @Override
    public String toString() {
        return transformedVal + " @ " + statement;
    }
}
