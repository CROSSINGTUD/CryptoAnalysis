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
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import boomerang.utils.MethodWrapper;
import crypto.extractparameter.AllocationSiteGraph;
import crypto.extractparameter.TransformedValue;
import de.fraunhofer.iem.cryptoanalysis.handler.FrameworkHandler;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WrapperTransformation extends AbstractTransformation implements ITransformation {

    private final MethodWrapper INTEGER_PARSE_INT =
            new MethodWrapper("java.lang.Integer", "parseInt", "int", List.of("java.lang.String"));

    public WrapperTransformation(FrameworkHandler frameworkHandler) {
        super(frameworkHandler);
    }

    @Override
    public Collection<Val> computeRequiredValues(Statement statement) {
        if (!statement.containsInvokeExpr()) {
            return Collections.emptySet();
        }

        InvokeExpr invokeExpr = statement.getInvokeExpr();
        DeclaredMethod declaredMethod = invokeExpr.getDeclaredMethod();
        MethodWrapper calledMethod = declaredMethod.toMethodWrapper();

        if (calledMethod.equals(INTEGER_PARSE_INT)) {
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

        if (methodWrapper.equals(INTEGER_PARSE_INT)) {
            return evaluateIntegerParseInt(allocVal, graph, transformation);
        }

        return Collections.emptySet();
    }

    private Collection<TransformedValue> evaluateIntegerParseInt(
            AllocVal allocVal, AllocationSiteGraph graph, TransformationHandler handler) {
        Statement statement = allocVal.getAllocStatement();
        Val arg = statement.getInvokeExpr().getArg(0);
        Collection<AllocVal> allocSites = graph.getAllocSites(arg);

        Collection<TransformedValue> extractedValues = new HashSet<>();
        for (AllocVal allocSite : allocSites) {
            Collection<TransformedValue> values = handler.transformAllocationSite(allocSite, graph);
            extractedValues.addAll(values);
        }

        Collection<TransformedValue> transformedValues = new HashSet<>();
        for (TransformedValue value : extractedValues) {
            Val val = value.getTransformedVal();

            if (!val.isStringConstant()) {
                // No String constant -> We cannot evaluate Integer.parseInt and return the
                // extracted value
                TransformedValue transVal =
                        new TransformedValue(
                                allocVal.getAllocVal(),
                                statement,
                                Collections.emptySet(),
                                Collections.singleton(value));
                transformedValues.add(transVal);
            } else {
                // Try to evaluate Integer.parseInt; if not possible, we return the extracted value
                try {
                    int parsedInt = Integer.parseInt(val.getStringValue());
                    Method method = statement.getMethod();

                    Val intVal = frameworkHandler.createIntConstant(parsedInt, method);
                    TransformedValue transVal = new TransformedValue(intVal, statement, value);

                    transformedValues.add(transVal);
                } catch (NumberFormatException ignored) {
                    TransformedValue transVal = new TransformedValue(val, statement, value);
                    transformedValues.add(transVal);
                }
            }
        }

        return transformedValues;
    }
}
