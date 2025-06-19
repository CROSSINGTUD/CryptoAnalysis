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
import boomerang.scope.DeclaredMethod;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import boomerang.utils.MethodWrapper;
import crypto.extractparameter.AllocationSiteGraph;
import crypto.extractparameter.TransformedValue;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MiscellaneousTransformation implements ITransformation {

    private final MethodWrapper HEX_DECODE =
            new MethodWrapper(
                    "org.bouncycastle.util.encoders.Hex",
                    "decode",
                    "byte[]",
                    List.of("java.lang.String"));

    @Override
    public Collection<Val> computeRequiredValues(Statement statement) {
        if (!statement.containsInvokeExpr()) {
            return Collections.emptySet();
        }

        InvokeExpr invokeExpr = statement.getInvokeExpr();
        DeclaredMethod declaredMethod = invokeExpr.getDeclaredMethod();
        MethodWrapper calledMethod = declaredMethod.toMethodWrapper();

        if (calledMethod.equals(HEX_DECODE)) {
            Val arg = invokeExpr.getArg(0);

            return Set.of(arg);
        }

        return Collections.emptySet();
    }

    @Override
    public Collection<TransformedValue> transformAllocationSite(
            AllocVal allocVal, AllocationSiteGraph graph, TransformationHandler transformation) {
        Statement statement = allocVal.getAllocStatement();
        if (!statement.containsInvokeExpr()) {
            return Collections.emptySet();
        }

        InvokeExpr invokeExpr = statement.getInvokeExpr();
        MethodWrapper methodWrapper = invokeExpr.getDeclaredMethod().toMethodWrapper();

        if (methodWrapper.equals(HEX_DECODE)) {
            return evaluateHexDecode(allocVal, graph, transformation);
        }

        return Collections.emptySet();
    }

    private Collection<TransformedValue> evaluateHexDecode(
            AllocVal allocVal, AllocationSiteGraph graph, TransformationHandler transformation) {
        Statement statement = allocVal.getAllocStatement();
        Val arg = statement.getInvokeExpr().getArg(0);
        Collection<AllocVal> allocSites = graph.getAllocSites(arg);

        Collection<TransformedValue> extractedValues = new HashSet<>();
        for (AllocVal allocSite : allocSites) {
            Collection<TransformedValue> values =
                    transformation.transformAllocationSite(allocSite, graph);
            extractedValues.addAll(values);
        }

        Collection<TransformedValue> transformedValues = new HashSet<>();
        for (TransformedValue value : extractedValues) {
            Val val = value.getTransformedVal();

            if (val.isStringConstant()) {
                TransformedValue transVal = new TransformedValue(val, statement, value);
                transformedValues.add(transVal);
            } else {
                TransformedValue transVal =
                        new TransformedValue(
                                allocVal.getAllocVal(),
                                statement,
                                Collections.emptySet(),
                                Collections.singleton(value));
                transformedValues.add(transVal);
            }
        }

        return transformedValues;
    }
}
