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
import boomerang.scope.DataFlowScope;
import boomerang.scope.DeclaredMethod;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import crypto.extractparameter.transformation.ITransformation;
import de.fraunhofer.iem.cryptoanalysis.handler.FrameworkHandler;
import java.util.Collection;
import java.util.Optional;

public class ExtractParameterAllocationSite implements IAllocationSite {

    private final FrameworkHandler frameworkHandler;
    private final DataFlowScope dataFlowScope;
    private final Collection<ITransformation> transformations;

    public ExtractParameterAllocationSite(
            FrameworkHandler frameworkHandler,
            DataFlowScope dataFlowScope,
            Collection<ITransformation> transformations) {
        this.frameworkHandler = frameworkHandler;
        this.dataFlowScope = dataFlowScope;
        this.transformations = transformations;
    }

    @Override
    public Optional<AllocVal> getAllocationSite(Method method, Statement statement, Val fact) {
        /* Constructors are not assignments; they are simple invoke statements. Therefore, we
         * have to check if we have a corresponding transformation separately.
         */
        if (!statement.isAssignStmt()
                && statement.containsInvokeExpr()
                && isTransformationStatement(statement)) {
            InvokeExpr invokeExpr = statement.getInvokeExpr();
            DeclaredMethod declaredMethod = invokeExpr.getDeclaredMethod();

            if (declaredMethod.isConstructor()) {
                Val base = invokeExpr.getBase();

                if (base.equals(fact)) {
                    AllocVal allocVal = new AllocVal(base, statement, base);
                    return Optional.of(allocVal);
                }
            }
        }

        if (!statement.isAssignStmt()) {
            return Optional.empty();
        }

        Val leftOp = statement.getLeftOp();
        Val rightOp = statement.getRightOp();

        if (!leftOp.equals(fact)) {
            return Optional.empty();
        }

        if (isTransformationStatement(statement)) {
            AllocVal allocVal = new AllocVal(leftOp, statement, rightOp);

            return Optional.of(allocVal);
        }

        /* If the allocation site is an invoke expression, we do not consider it as an
         * allocation site if the method is actually analyzed, e.g.
         *
         * // Return System.getProperty as allocation site because we do not analyze getProperty()
         * String s = System.getProperty();
         * queryFor(s);
         *
         * // Do not return getId(..) because we consider its dataflow
         * String s1 = "AllocSite";
         * String s2 = getId(s1);
         * queryFor(s2);
         */
        if (statement.containsInvokeExpr()) {
            InvokeExpr invokeExpr = statement.getInvokeExpr();

            if (isTransformationStatement(statement)) {
                AllocVal allocVal = new AllocVal(leftOp, statement, rightOp);

                return Optional.of(allocVal);
            }

            if (dataFlowScope.isExcluded(invokeExpr.getDeclaredMethod())) {
                AllocVal allocVal = new AllocVal(leftOp, statement, rightOp);

                return Optional.of(allocVal);
            }
        }

        /* If we have a cast expression, we consider the actual operand as allocation site, e.g.
         * for 'int i = (int) 65000' we return '65000'
         */
        if (rightOp.isCast()) {
            Val castOp = rightOp.getCastOp();

            if (isAllocationVal(castOp)) {
                AllocVal allocVal = new AllocVal(leftOp, statement, castOp);

                return Optional.of(allocVal);
            }
        }

        if (frameworkHandler.isBinaryExpr(rightOp)) {
            AllocVal allocVal = new AllocVal(leftOp, statement, rightOp);
            return Optional.of(allocVal);
        }

        if (isAllocationVal(rightOp)) {
            AllocVal allocVal = new AllocVal(leftOp, statement, rightOp);
            return Optional.of(allocVal);
        }

        return Optional.empty();
    }

    private boolean isTransformationStatement(Statement statement) {
        for (ITransformation transformation : transformations) {
            if (!transformation.computeRequiredValues(statement).isEmpty()) {
                return true;
            }
        }

        return false;
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

        // length expressions: var = arr.length
        if (val.isLengthExpr()) {
            return true;
        }

        // new expressions: var = new java.lang.Object
        return val.isNewExpr();
    }
}
