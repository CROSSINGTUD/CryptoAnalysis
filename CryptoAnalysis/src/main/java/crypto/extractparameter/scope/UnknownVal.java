/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.extractparameter.scope;

import boomerang.scope.ControlFlowGraph;
import boomerang.scope.IArrayRef;
import boomerang.scope.Method;
import boomerang.scope.Type;
import boomerang.scope.Val;

public class UnknownVal extends Val {

    private static UnknownVal instance;

    private UnknownVal() {}

    public static UnknownVal getInstance() {
        if (instance == null) {
            instance = new UnknownVal();
        }

        return instance;
    }

    @Override
    public Type getType() {
        return UnknownType.getInstance();
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public boolean isNewExpr() {
        return false;
    }

    @Override
    public Type getNewExprType() {
        throw new RuntimeException("Unknown Val is not a new expression");
    }

    @Override
    public Val asUnbalanced(ControlFlowGraph.Edge edge) {
        throw new RuntimeException("Unknown Val should not be propagated");
    }

    @Override
    public boolean isLocal() {
        return false;
    }

    @Override
    public boolean isArrayAllocationVal() {
        return false;
    }

    @Override
    public Val getArrayAllocationSize() {
        throw new RuntimeException("Unknown Val is not an array allocation val");
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public boolean isStringConstant() {
        return false;
    }

    @Override
    public String getStringValue() {
        throw new RuntimeException("Unknown Val is not a String constant");
    }

    @Override
    public boolean isCast() {
        return false;
    }

    @Override
    public Val getCastOp() {
        throw new RuntimeException("Unknown Val is not a cast expression");
    }

    @Override
    public boolean isArrayRef() {
        return false;
    }

    @Override
    public boolean isInstanceOfExpr() {
        return false;
    }

    @Override
    public Val getInstanceOfOp() {
        throw new RuntimeException("Unknown Val is not an instanceOf expression");
    }

    @Override
    public boolean isLengthExpr() {
        return false;
    }

    @Override
    public Val getLengthOp() {
        throw new RuntimeException("Unknown Val is not a length expression");
    }

    @Override
    public boolean isIntConstant() {
        return false;
    }

    @Override
    public boolean isClassConstant() {
        return false;
    }

    @Override
    public Type getClassConstantType() {
        throw new RuntimeException("Unknown Val is not a class constant");
    }

    @Override
    public Val withNewMethod(Method method) {
        throw new RuntimeException("Unknown Val should not be propagated");
    }

    @Override
    public boolean isLongConstant() {
        return false;
    }

    @Override
    public int getIntValue() {
        throw new RuntimeException("Unknown Val is not an int constant");
    }

    @Override
    public long getLongValue() {
        throw new RuntimeException("Unknown Val is not a long constant");
    }

    @Override
    public IArrayRef getArrayBase() {
        throw new RuntimeException("Unknown Val has no array base");
    }

    @Override
    public String getVariableName() {
        return "<unknown>";
    }

    @Override
    public String toString() {
        return getVariableName();
    }
}
