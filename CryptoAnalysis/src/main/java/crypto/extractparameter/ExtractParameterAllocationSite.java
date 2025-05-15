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

import boomerang.options.IAllocationSite;
import boomerang.scope.AllocVal;
import boomerang.scope.DeclaredMethod;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Type;
import boomerang.scope.Val;
import crypto.extractparameter.transformation.OperatorTransformation;
import crypto.extractparameter.transformation.Transformation;
import java.util.Optional;

public class ExtractParameterAllocationSite implements IAllocationSite {

    @Override
    public Optional<AllocVal> getAllocationSite(Method m, Statement stmt, Val fact) {
        /* Constructors are not assignments; they are simple invoke statements. Therefore, we
         * have to check if we have a corresponding transformation separately.
         */
        if (Transformation.isTransformationExpression(stmt)) {
            InvokeExpr invokeExpr = stmt.getInvokeExpr();
            DeclaredMethod declaredMethod = invokeExpr.getDeclaredMethod();

            if (declaredMethod.isConstructor()) {
                Val base = invokeExpr.getBase();

                if (base.equals(fact)) {
                    AllocVal allocVal = new AllocVal(base, stmt, base);
                    return Optional.of(allocVal);
                }
            }
        }

        if (!stmt.isAssignStmt()) {
            return Optional.empty();
        }

        Val leftOp = stmt.getLeftOp();
        Val rightOp = stmt.getRightOp();

        if (!leftOp.equals(fact)) {
            return Optional.empty();
        }

        if (stmt.containsInvokeExpr()) {
            /* If we have an invoke expression, we check if it corresponds to an
             * implemented transformation
             */
            if (Transformation.isTransformationExpression(stmt)) {
                AllocVal allocVal = new AllocVal(leftOp, stmt, rightOp);

                return Optional.of(allocVal);
            }
        } else {
            if (OperatorTransformation.isOperatorTransformation(rightOp)) {
                AllocVal allocVal = new AllocVal(leftOp, stmt, rightOp);

                return Optional.of(allocVal);
            }

            /* Extract cast value from cast expressions, e.g.
             * int i = (int) 65000 -> AllocVal: 65000
             */
            if (rightOp.isCast()) {
                Val castOp = rightOp.getCastOp();

                if (isAllocationVal(castOp)) {
                    return Optional.of(new AllocVal(leftOp, stmt, castOp));
                }
            }

            /* Strings are initialized in two steps, where we need the second one:
             * r0 = new java.lang.String;
             * r0 = "value";
             */
            if (rightOp.isNewExpr()) {
                Type type = rightOp.getNewExprType();

                if (type.toString().equals("java.lang.String")) {
                    return Optional.empty();
                }
            }

            // Basic values: constants, array allocations and null
            if (isAllocationVal(rightOp)) {
                return Optional.of(new AllocVal(leftOp, stmt, rightOp));
            }
        }

        return Optional.empty();
    }

    private boolean isAllocationVal(Val val) {
        // Constants: var = <constant>
        if (val.isConstant()) {
            return true;
        }

        // null: var = null
        if (val.isNull()) {
            return true;
        }

        // arrays: var = new arr[..]
        if (val.isArrayAllocationVal()) {
            return true;
        }

        return val.isNewExpr();
    }
}
