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
import boomerang.scope.opal.tac.OpalMethod;
import boomerang.scope.opal.tac.OpalVal;
import boomerang.scope.opal.transformation.TacLocal;
import org.jspecify.annotations.NonNull;
import org.opalj.tac.BinaryExpr$;
import org.opalj.tac.Expr;
import org.opalj.tac.IntConst;
import org.opalj.tac.LongConst;
import org.opalj.tac.StringConst;

public class OpalFrameworkHandler implements FrameworkHandler {

    private static final int PC = Integer.MIN_VALUE;

    @Override
    public Val createIntConstant(@NonNull int value, @NonNull Method method) {
        if (method instanceof OpalMethod opalMethod) {
            return OpalVal.createUnsafe(new IntConst(PC, value), opalMethod);
        }

        throw new RuntimeException("Cannot create int constant without OpalMethod");
    }

    @Override
    public Val createLongConstant(@NonNull long value, @NonNull Method method) {
        if (method instanceof OpalMethod opalMethod) {
            return OpalVal.createUnsafe(new LongConst(PC, value), opalMethod);
        }

        throw new RuntimeException("Cannot create long constant without OpalMethod");
    }

    @Override
    public Val createStringConstant(@NonNull String value, @NonNull Method method) {
        if (method instanceof OpalMethod opalMethod) {
            return OpalVal.createUnsafe(new StringConst(PC, value), opalMethod);
        }

        throw new RuntimeException("Cannot create String constant without OpalMethod");
    }

    @Override
    public boolean isBinaryExpr(@NonNull Val val) {
        if (val instanceof OpalVal opalVal) {
            Expr<TacLocal> value = opalVal.delegate();

            return value.astID() == BinaryExpr$.MODULE$.ASTID();
        }

        return false;
    }
}
