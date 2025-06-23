/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.extractparameter.transformation;

import boomerang.scope.AllocVal;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import crypto.extractparameter.AllocationSiteGraph;
import crypto.extractparameter.TransformedValue;
import de.fraunhofer.iem.cryptoanalysis.handler.FrameworkHandler;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class OperatorTransformation extends AbstractTransformation implements ITransformation {

    public OperatorTransformation(FrameworkHandler frameworkHandler) {
        super(frameworkHandler);
    }

    @Override
    public Collection<Val> computeRequiredValues(Statement statement) {
        if (statement.isAssignStmt()) {
            Val rightOp = statement.getRightOp();

            if (rightOp.isLengthExpr()) {
                Val lengthOp = rightOp.getLengthOp();

                return Collections.singleton(lengthOp);
            }
        }

        return Collections.emptySet();
    }

    @Override
    public Collection<TransformedValue> transformAllocationSite(
            AllocVal allocVal, AllocationSiteGraph graph, TransformationHandler transformation) {
        Statement statement = allocVal.getAllocStatement();
        if (statement.isAssignStmt()) {
            Val rightOp = statement.getRightOp();

            if (rightOp.isLengthExpr()) {
                return evaluateLengthExpr(allocVal, rightOp, graph, transformation);
            }
        }

        return Collections.emptySet();
    }

    private Collection<TransformedValue> evaluateLengthExpr(
            AllocVal allocVal,
            Val lengthExpr,
            AllocationSiteGraph graph,
            TransformationHandler transformation) {
        Val lengthOp = lengthExpr.getLengthOp();
        Collection<AllocVal> allocSites = graph.getAllocSites(lengthOp);

        Collection<TransformedValue> extractedValues = new HashSet<>();
        for (AllocVal allocSite : allocSites) {
            Collection<TransformedValue> values =
                    transformation.transformAllocationSite(allocSite, graph);
            extractedValues.addAll(values);
        }

        Collection<TransformedValue> transformedValues = new HashSet<>();
        for (TransformedValue value : extractedValues) {
            Val val = value.getTransformedVal();

            if (val.isArrayAllocationVal()) {
                Val arrLength = val.getArrayAllocationSize();

                TransformedValue transVal =
                        new TransformedValue(arrLength, allocVal.getAllocStatement(), value);
                transformedValues.add(transVal);
            } else if (val.isStringConstant()) {
                int stringLength = val.getStringValue().length();
                Method method = allocVal.getAllocStatement().getMethod();
                Val intVal = frameworkHandler.createIntConstant(stringLength, method);

                TransformedValue transVal =
                        new TransformedValue(intVal, allocVal.getAllocStatement(), value);
                transformedValues.add(transVal);
            } else {
                TransformedValue transVal =
                        new TransformedValue(
                                allocVal.getAllocVal(),
                                allocVal.getAllocStatement(),
                                Collections.emptySet(),
                                Collections.singleton(value));
                transformedValues.add(transVal);
            }
        }

        return transformedValues;
    }
}
