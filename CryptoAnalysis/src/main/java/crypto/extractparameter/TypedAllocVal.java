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

import boomerang.scope.AllocVal;
import boomerang.scope.Statement;
import boomerang.scope.Type;
import boomerang.scope.Val;
import java.util.Objects;

public class TypedAllocVal extends AllocVal {

    private final Type type;

    public TypedAllocVal(Val delegate, Statement allocStatement, Val allocationVal, Type type) {
        super(delegate, allocStatement, allocationVal);

        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TypedAllocVal that = (TypedAllocVal) o;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), type);
    }

    @Override
    public String toString() {
        return super.toString() + " with type " + type;
    }
}
