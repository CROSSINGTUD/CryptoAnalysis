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
import crypto.extractparameter.scope.StringVal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StringTransformation implements ITransformation {

    private final MethodWrapper TO_CHAR_ARRAY =
            new MethodWrapper("java.lang.String", "toCharArray", "char[]");
    private final MethodWrapper GET_BYTES =
            new MethodWrapper("java.lang.String", "getBytes", "byte[]");
    private final MethodWrapper GET_BYTES_WITH_PARAM =
            new MethodWrapper(
                    "java.lang.String", "getBytes", "byte[]", List.of("java.lang.String"));

    private final MethodWrapper TO_UPPER_CASE =
            new MethodWrapper("java.lang.String", "toUpperCase", "java.lang.String");
    private final MethodWrapper TO_UPPER_CASE_WITH_PARAM =
            new MethodWrapper(
                    "java.lang.String",
                    "toUpperCase",
                    "java.lang.String",
                    List.of("java.util.Locale"));

    private final MethodWrapper TO_LOWER_CASE =
            new MethodWrapper("java.lang.String", "toLowerCase", "java.lang.String");
    private final MethodWrapper TO_LOWER_CASE_WITH_PARAM =
            new MethodWrapper(
                    "java.lang.String",
                    "toLowerCase",
                    "java.lang.String",
                    List.of("java.util.Locale"));

    private final MethodWrapper REPLACE =
            new MethodWrapper(
                    "java.lang.String",
                    "replace",
                    "java.lang.String",
                    List.of("java.lang.CharSequence", "java.lang.CharSequence"));

    @Override
    public Collection<Val> computeRequiredValues(Statement statement) {
        if (!statement.containsInvokeExpr()) {
            return Collections.emptySet();
        }

        InvokeExpr invokeExpr = statement.getInvokeExpr();
        DeclaredMethod declaredMethod = invokeExpr.getDeclaredMethod();
        MethodWrapper calledMethod = declaredMethod.toMethodWrapper();

        if (Set.of(TO_CHAR_ARRAY, GET_BYTES, GET_BYTES_WITH_PARAM).contains(calledMethod)) {
            Val base = invokeExpr.getBase();

            return Set.of(base);
        }

        if (Set.of(TO_UPPER_CASE, TO_UPPER_CASE_WITH_PARAM, TO_LOWER_CASE, TO_LOWER_CASE_WITH_PARAM)
                .contains(calledMethod)) {
            Val base = invokeExpr.getBase();

            return Set.of(base);
        }

        if (calledMethod.equals(REPLACE)) {
            Val base = invokeExpr.getBase();
            Val arg1 = invokeExpr.getArg(0);
            Val arg2 = invokeExpr.getArg(1);

            return Set.of(base, arg1, arg2);
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

        if (Set.of(TO_CHAR_ARRAY, GET_BYTES, GET_BYTES_WITH_PARAM).contains(methodWrapper)) {
            return evaluateStringBase(allocVal, graph, transformation);
        }

        if (Set.of(TO_UPPER_CASE, TO_UPPER_CASE_WITH_PARAM).contains(methodWrapper)) {
            return evaluateToUpperCase(allocVal, graph, transformation);
        }

        if (Set.of(TO_LOWER_CASE, TO_LOWER_CASE_WITH_PARAM).contains(methodWrapper)) {
            return evaluateToLowerCase(allocVal, graph, transformation);
        }

        if (methodWrapper.equals(REPLACE)) {
            return evaluateReplace(allocVal, graph, transformation);
        }

        return Collections.emptySet();
    }

    private Collection<TransformedValue> evaluateStringBase(
            AllocVal allocVal, AllocationSiteGraph graph, TransformationHandler transformation) {
        Statement statement = allocVal.getAllocStatement();
        Val base = statement.getInvokeExpr().getBase();
        Collection<AllocVal> allocSites = graph.getAllocSites(base);

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

    private Collection<TransformedValue> evaluateToUpperCase(
            AllocVal allocVal, AllocationSiteGraph graph, TransformationHandler transformation) {
        Statement statement = allocVal.getAllocStatement();
        Val base = statement.getInvokeExpr().getBase();
        Collection<AllocVal> allocSites = graph.getAllocSites(base);

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
                String baseString = val.getStringValue();
                String transformedString = baseString.toUpperCase();

                StringVal stringVal = new StringVal(transformedString, statement.getMethod());
                TransformedValue transVal = new TransformedValue(stringVal, statement, value);

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

    private Collection<TransformedValue> evaluateToLowerCase(
            AllocVal allocVal, AllocationSiteGraph graph, TransformationHandler transformation) {
        Statement statement = allocVal.getAllocStatement();
        Val base = statement.getInvokeExpr().getBase();
        Collection<AllocVal> allocSites = graph.getAllocSites(base);

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
                String baseString = val.getStringValue();
                String transformedString = baseString.toLowerCase();

                StringVal stringVal = new StringVal(transformedString, statement.getMethod());
                TransformedValue transVal = new TransformedValue(stringVal, statement, value);

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

    private Collection<TransformedValue> evaluateReplace(
            AllocVal allocVal, AllocationSiteGraph graph, TransformationHandler transformation) {
        Statement statement = allocVal.getAllocStatement();
        InvokeExpr invokeExpr = statement.getInvokeExpr();

        Val base = invokeExpr.getBase();
        Val arg1 = invokeExpr.getArg(0);
        Val arg2 = invokeExpr.getArg(1);

        Collection<AllocVal> baseAllocSites = graph.getAllocSites(base);
        Collection<AllocVal> arg1AllocSites = graph.getAllocSites(arg1);
        Collection<AllocVal> arg2AllocSites = graph.getAllocSites(arg2);

        Collection<TransformedValue> extractedBaseValues = new HashSet<>();
        for (AllocVal baseAllocSite : baseAllocSites) {
            Collection<TransformedValue> values =
                    transformation.transformAllocationSite(baseAllocSite, graph);
            extractedBaseValues.addAll(values);
        }

        Collection<TransformedValue> extractedArg1AllocSites = new HashSet<>();
        for (AllocVal arg1AllocSite : arg1AllocSites) {
            Collection<TransformedValue> values =
                    transformation.transformAllocationSite(arg1AllocSite, graph);
            extractedArg1AllocSites.addAll(values);
        }

        Collection<TransformedValue> extractedArg2AllocSites = new HashSet<>();
        for (AllocVal arg2AllocSite : arg2AllocSites) {
            Collection<TransformedValue> values =
                    transformation.transformAllocationSite(arg2AllocSite, graph);
            extractedArg2AllocSites.addAll(values);
        }

        Collection<TransformedValue> transformedValues = new HashSet<>();
        for (TransformedValue extractedBase : extractedBaseValues) {
            for (TransformedValue extractedArg1 : extractedArg1AllocSites) {
                for (TransformedValue extractedArg2 : extractedArg2AllocSites) {

                    Collection<TransformedValue> knownValues = new HashSet<>();
                    Collection<TransformedValue> unknownValues = new HashSet<>();

                    if (extractedBase.getTransformedVal().isStringConstant()) {
                        knownValues.add(extractedBase);
                    } else {
                        unknownValues.add(extractedBase);
                    }

                    if (extractedArg1.getTransformedVal().isStringConstant()) {
                        knownValues.add(extractedArg1);
                    } else {
                        unknownValues.add(extractedArg1);
                    }

                    if (extractedArg2.getTransformedVal().isStringConstant()) {
                        knownValues.add(extractedArg2);
                    } else {
                        unknownValues.add(extractedArg2);
                    }

                    if (!unknownValues.isEmpty()) {
                        // At least one variable is not a String -> Cannot evaluate 's.replace(a,
                        // b)'
                        TransformedValue value =
                                new TransformedValue(
                                        allocVal.getAllocVal(),
                                        statement,
                                        knownValues,
                                        unknownValues);
                        transformedValues.add(value);
                    } else {
                        // Evaluate 's.replace(a, b)'
                        String baseString = extractedBase.getTransformedVal().getStringValue();
                        String arg1String = extractedArg1.getTransformedVal().getStringValue();
                        String arg2String = extractedArg2.getTransformedVal().getStringValue();

                        String transformedString = baseString.replace(arg1String, arg2String);
                        StringVal stringVal =
                                new StringVal(transformedString, statement.getMethod());
                        TransformedValue value =
                                new TransformedValue(stringVal, statement, knownValues);

                        transformedValues.add(value);
                    }
                }
            }
        }

        return transformedValues;
    }
}
