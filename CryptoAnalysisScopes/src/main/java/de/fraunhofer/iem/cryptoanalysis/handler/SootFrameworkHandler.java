/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package de.fraunhofer.iem.cryptoanalysis.handler;

import boomerang.scope.Method;
import boomerang.scope.Val;
import boomerang.scope.soot.jimple.JimpleMethod;
import boomerang.scope.soot.jimple.JimpleVal;
import org.jspecify.annotations.NonNull;
import soot.Value;
import soot.jimple.BinopExpr;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.StringConstant;

public class SootFrameworkHandler implements FrameworkHandler {

    @Override
    public Val createIntConstant(int value, Method method) {
        if (method instanceof JimpleMethod jimpleMethod) {
            return new JimpleVal(IntConstant.v(value), jimpleMethod);
        }

        throw new RuntimeException("Cannot create int constant without JimpleMethod");
    }

    @Override
    public Val createLongConstant(@NonNull long value, @NonNull Method method) {
        if (method instanceof JimpleMethod jimpleMethod) {
            return new JimpleVal(LongConstant.v(value), jimpleMethod);
        }

        throw new RuntimeException("Cannot create long constant without JimpleMethod");
    }

    @Override
    public Val createStringConstant(@NonNull String value, @NonNull Method method) {
        if (method instanceof JimpleMethod jimpleMethod) {
            return new JimpleVal(StringConstant.v(value), jimpleMethod);
        }

        throw new RuntimeException("Cannot create String constant without JimpleMethod");
    }

    @Override
    public boolean isBinaryExpr(@NonNull Val val) {
        if (val instanceof JimpleVal jimpleVal) {
            Value value = jimpleVal.getDelegate();

            return value instanceof BinopExpr;
        }

        return false;
    }
}
