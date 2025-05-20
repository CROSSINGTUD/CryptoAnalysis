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

public class LongVal extends Val {

    private final long value;

    public LongVal(long value, Method method) {
        this(value, method, null);
    }

    private LongVal(long value, Method method, ControlFlowGraph.Edge edge) {
        super(method, edge);

        this.value = value;
    }

    @Override
    public Type getType() {
        return new LongType();
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
        throw new RuntimeException("Long constant is not a new expression");
    }

    @Override
    public Val asUnbalanced(ControlFlowGraph.Edge stmt) {
        return new LongVal(value, m, stmt);
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
        throw new RuntimeException("Long constant is not an array allocation expression");
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
        throw new RuntimeException("Long constant is not a String constant");
    }

    @Override
    public boolean isCast() {
        return false;
    }

    @Override
    public Val getCastOp() {
        throw new RuntimeException("Long constant is not a cast expression");
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
        throw new RuntimeException("Long constant is not an instanceOf expression");
    }

    @Override
    public boolean isLengthExpr() {
        return false;
    }

    @Override
    public Val getLengthOp() {
        throw new RuntimeException("Long constant is not a length expression");
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
        throw new RuntimeException("Long constant is not a class constant");
    }

    @Override
    public Val withNewMethod(Method callee) {
        return new LongVal(value, callee);
    }

    @Override
    public boolean isLongConstant() {
        return true;
    }

    @Override
    public int getIntValue() {
        throw new RuntimeException("Long constant is not an integer");
    }

    @Override
    public long getLongValue() {
        return value;
    }

    @Override
    public IArrayRef getArrayBase() {
        throw new RuntimeException("Long constant has no array base");
    }

    @Override
    public String getVariableName() {
        return "Long constant: " + value;
    }
}
