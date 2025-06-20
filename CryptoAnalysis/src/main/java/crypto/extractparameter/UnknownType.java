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

import boomerang.scope.Type;
import boomerang.scope.Val;
import boomerang.scope.WrappedClass;

public class UnknownType implements Type {

    private static UnknownType instance;

    private UnknownType() {}

    public static UnknownType getInstance() {
        if (instance == null) {
            instance = new UnknownType();
        }

        return instance;
    }

    @Override
    public boolean isNullType() {
        return false;
    }

    @Override
    public boolean isRefType() {
        return false;
    }

    @Override
    public boolean isArrayType() {
        return false;
    }

    @Override
    public Type getArrayBaseType() {
        throw new RuntimeException("Unknown type is not an array type");
    }

    @Override
    public WrappedClass getWrappedClass() {
        throw new RuntimeException("Unknown type has no declaring class");
    }

    @Override
    public boolean doesCastFail(Type type, Val val) {
        return true;
    }

    @Override
    public boolean isSubtypeOf(String s) {
        return false;
    }

    @Override
    public boolean isSupertypeOf(String s) {
        return false;
    }

    @Override
    public boolean isBooleanType() {
        return false;
    }

    @Override
    public String toString() {
        return "<UnknownType>";
    }
}
