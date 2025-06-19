/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.typestate;

import boomerang.options.DefaultAllocationSite;
import boomerang.scope.AllocVal;
import boomerang.scope.DataFlowScope;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import java.util.Optional;

public class TypestateAllocationSite extends DefaultAllocationSite {

    private final ForwardSeedQuery query;
    private final DataFlowScope dataFlowScope;

    public TypestateAllocationSite(ForwardSeedQuery query, DataFlowScope dataFlowScope) {
        this.query = query;
        this.dataFlowScope = dataFlowScope;
    }

    @Override
    public Optional<AllocVal> getAllocationSite(Method method, Statement stmt, Val fact) {
        Statement statement = query.cfgEdge().getStart();
        Val var = query.getAllocVal().getDelegate();

        // Make sure that we always return the original allocation site
        if (stmt.equals(statement)) {
            if (stmt.isAssignStmt()) {
                Val leftOp = stmt.getLeftOp();
                Val rightOp = stmt.getRightOp();
                AllocVal allocVal = new AllocVal(leftOp, stmt, rightOp);

                return Optional.of(allocVal);
            } else {
                AllocVal allocVal = new AllocVal(var, stmt, var);

                return Optional.of(allocVal);
            }
        }

        // Include allocation sites from a non-project function call
        if (stmt.isAssignStmt() && stmt.containsInvokeExpr()) {
            Val leftOp = stmt.getLeftOp();
            Val rightOp = stmt.getRightOp();

            if (leftOp.equals(fact)) {
                InvokeExpr invokeExpr = stmt.getInvokeExpr();
                
                if (dataFlowScope.isExcluded(invokeExpr.getDeclaredMethod())) {
                    AllocVal allocVal = new AllocVal(leftOp, stmt, rightOp);

                    return Optional.of(allocVal);
                }
            }
        }

        return super.getAllocationSite(method, stmt, fact);
    }
}
