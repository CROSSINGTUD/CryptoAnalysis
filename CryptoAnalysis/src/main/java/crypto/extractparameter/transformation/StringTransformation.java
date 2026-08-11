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
import de.fraunhofer.iem.cryptoanalysis.handler.FrameworkHandler;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StringTransformation extends AbstractTransformation implements ITransformation {

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

    private final MethodWrapper CONCAT =
            new MethodWrapper("java.lang.String", "concat", "java.lang.String", List.of("java.lang.String"));

    private final MethodWrapper STARTS_WITH =
            new MethodWrapper("java.lang.String", "startsWith", "boolean", List.of("java.lang.String"));

    private final MethodWrapper ENDS_WITH =
            new MethodWrapper("java.lang.String", "endsWith", "boolean", List.of("java.lang.String"));

    private final MethodWrapper TRIM =
            new MethodWrapper("java.lang.String", "trim", "java.lang.String");

    private final MethodWrapper SUBSTRING_ONE_PARAM =
            new MethodWrapper("java.lang.String", "substring", "java.lang.String", List.of("int"));
    private final MethodWrapper SUBSTRING_TWO_PARAMS =
            new MethodWrapper("java.lang.String", "substring", "java.lang.String", List.of("int", "int"));

    private final MethodWrapper CHAR_AT =
            new MethodWrapper("java.lang.String", "charAt", "char", List.of("int"));

    private final MethodWrapper INDEX_OF =
            new MethodWrapper("java.lang.String", "indexOf", "int", List.of("java.lang.String"));

    public StringTransformation(FrameworkHandler frameworkHandler) {
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

        if (calledMethod.equals(CONCAT)) {
            Val base = invokeExpr.getBase();
            Val arg = invokeExpr.getArg(0);
            return Set.of(base, arg);
        }

        if (Set.of(STARTS_WITH, ENDS_WITH).contains(calledMethod)) {
            Val base = invokeExpr.getBase();
            Val pattern = invokeExpr.getArg(0);
            return Set.of(base, pattern);
        }

        if (calledMethod.equals(TRIM)) {
            Val base = invokeExpr.getBase();
            return Set.of(base);
        }

        if (Set.of(SUBSTRING_ONE_PARAM, SUBSTRING_TWO_PARAMS).contains(calledMethod)) {
            Val base = invokeExpr.getBase();
            Val beginIndex = invokeExpr.getArg(0);

            if (calledMethod.equals(SUBSTRING_TWO_PARAMS)) {
                Val endIndex = invokeExpr.getArg(1);
                return Set.of(base, beginIndex, endIndex);
            } else {
                return Set.of(base, beginIndex);
            }
        }

        if (calledMethod.equals(CHAR_AT)) {
            Val base = invokeExpr.getBase();
            Val index = invokeExpr.getArg(0);
            return Set.of(base, index);
        }

        if (calledMethod.equals(INDEX_OF)) {
            Val base = invokeExpr.getBase();
            Val searchStr = invokeExpr.getArg(0);
            return Set.of(base, searchStr);
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

        if (methodWrapper.equals(CONCAT)) {
            return evaluateConcat(allocVal, graph, transformation);
        }

        if (Set.of(STARTS_WITH, ENDS_WITH).contains(methodWrapper)) {
            return evaluateStartsEndsWith(allocVal, graph, transformation);
        }

        if (methodWrapper.equals(TRIM)) {
            return evaluateTrim(allocVal, graph, transformation);
        }

        if (Set.of(SUBSTRING_ONE_PARAM, SUBSTRING_TWO_PARAMS).contains(methodWrapper)) {
            return evaluateSubstring(allocVal, graph, transformation);
        }

        if (methodWrapper.equals(CHAR_AT)) {
            return evaluateCharAt(allocVal, graph, transformation);
        }

        if (methodWrapper.equals(INDEX_OF)) {
            return evaluateIndexOf(allocVal, graph, transformation);
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

                Val stringVal =
                        frameworkHandler.createStringConstant(
                                transformedString, statement.getMethod());
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

                Val stringVal =
                        frameworkHandler.createStringConstant(
                                transformedString, statement.getMethod());
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
                        Val stringVal =
                                frameworkHandler.createStringConstant(
                                        transformedString, statement.getMethod());
                        TransformedValue value =
                                new TransformedValue(stringVal, statement, knownValues);

                        transformedValues.add(value);
                    }
                }
            }
        }

        return transformedValues;
    }

    private Collection<TransformedValue> evaluateConcat(
            AllocVal allocVal, AllocationSiteGraph graph, TransformationHandler transformation) {
        Statement statement = allocVal.getAllocStatement();
        InvokeExpr invokeExpr = statement.getInvokeExpr();

        Val base = invokeExpr.getBase();
        Val arg = invokeExpr.getArg(0);

        Collection<AllocVal> baseAllocSites = graph.getAllocSites(base);
        Collection<AllocVal> argAllocSites = graph.getAllocSites(arg);

        Collection<TransformedValue> extractedBaseValues = new HashSet<>();
        for (AllocVal baseAllocSite : baseAllocSites) {
            Collection<TransformedValue> values = transformation.transformAllocationSite(baseAllocSite, graph);
            extractedBaseValues.addAll(values);
        }

        Collection<TransformedValue> extractedArgValues = new HashSet<>();
        for (AllocVal argAllocSite : argAllocSites) {
            Collection<TransformedValue> values = transformation.transformAllocationSite(argAllocSite, graph);
            extractedArgValues.addAll(values);
        }

        Collection<TransformedValue> transformedValues = new HashSet<>();
        for (TransformedValue extractedBase : extractedBaseValues) {
            for (TransformedValue extractedArg : extractedArgValues) {
                Collection<TransformedValue> knownValues = new HashSet<>();
                Collection<TransformedValue> unknownValues = new HashSet<>();

                if (extractedBase.getTransformedVal().isStringConstant()) {
                    knownValues.add(extractedBase);
                } else {
                    unknownValues.add(extractedBase);
                }

                if (extractedArg.getTransformedVal().isStringConstant()) {
                    knownValues.add(extractedArg);
                } else {
                    unknownValues.add(extractedArg);
                }

                if (!unknownValues.isEmpty()) {
                    // At least one variable is not a String -> Cannot evaluate 's.concat(arg)'
                    TransformedValue value = new TransformedValue(
                            allocVal.getAllocVal(), statement, knownValues, unknownValues);
                    transformedValues.add(value);
                } else {
                    // Evaluate 's.concat(arg)'
                    String baseString = extractedBase.getTransformedVal().getStringValue();
                    String argString = extractedArg.getTransformedVal().getStringValue();
                    String transformedString = baseString + argString;

                    Val stringVal = frameworkHandler.createStringConstant(
                            transformedString, statement.getMethod());
                    TransformedValue value = new TransformedValue(stringVal, statement, knownValues);
                    transformedValues.add(value);
                }
            }
        }

        return transformedValues;
    }

    private Collection<TransformedValue> evaluateStartsEndsWith(
            AllocVal allocVal, AllocationSiteGraph graph, TransformationHandler transformation) {
        Statement statement = allocVal.getAllocStatement();
        InvokeExpr invokeExpr = statement.getInvokeExpr();
        MethodWrapper methodWrapper = invokeExpr.getDeclaredMethod().toMethodWrapper();

        Val base = invokeExpr.getBase();
        Val pattern = invokeExpr.getArg(0);

        Collection<AllocVal> baseAllocSites = graph.getAllocSites(base);
        Collection<AllocVal> patternAllocSites = graph.getAllocSites(pattern);

        Collection<TransformedValue> extractedBaseValues = new HashSet<>();
        for (AllocVal baseAllocSite : baseAllocSites) {
            Collection<TransformedValue> values = transformation.transformAllocationSite(baseAllocSite, graph);
            extractedBaseValues.addAll(values);
        }

        Collection<TransformedValue> extractedPatternValues = new HashSet<>();
        for (AllocVal patternAllocSite : patternAllocSites) {
            Collection<TransformedValue> values = transformation.transformAllocationSite(patternAllocSite, graph);
            extractedPatternValues.addAll(values);
        }

        Collection<TransformedValue> transformedValues = new HashSet<>();
        for (TransformedValue extractedBase : extractedBaseValues) {
            for (TransformedValue extractedPattern : extractedPatternValues) {
                Collection<TransformedValue> knownValues = new HashSet<>();
                Collection<TransformedValue> unknownValues = new HashSet<>();

                if (extractedBase.getTransformedVal().isStringConstant()) {
                    knownValues.add(extractedBase);
                } else {
                    unknownValues.add(extractedBase);
                }

                if (extractedPattern.getTransformedVal().isStringConstant()) {
                    knownValues.add(extractedPattern);
                } else {
                    unknownValues.add(extractedPattern);
                }

                if (!unknownValues.isEmpty()) {
                    // At least one variable is not a String -> Cannot evaluate 's.startsWith/endsWith(pattern)'
                    TransformedValue value = new TransformedValue(
                            allocVal.getAllocVal(), statement, knownValues, unknownValues);
                    transformedValues.add(value);
                } else {
                    // Evaluate 's.startsWith(pattern)' or 's.endsWith(pattern)'
                    String baseString = extractedBase.getTransformedVal().getStringValue();
                    String patternString = extractedPattern.getTransformedVal().getStringValue();

                    boolean result = methodWrapper.equals(ENDS_WITH) ?
                            baseString.endsWith(patternString) : baseString.startsWith(patternString);

                    int booleanAsInt = result ? 1 : 0;
                    Val intVal = frameworkHandler.createIntConstant(booleanAsInt, statement.getMethod());
                    TransformedValue value = new TransformedValue(intVal, statement, knownValues);
                    transformedValues.add(value);
                }
            }
        }

        return transformedValues;
    }

    private Collection<TransformedValue> evaluateTrim(
            AllocVal allocVal, AllocationSiteGraph graph, TransformationHandler transformation) {
        Statement statement = allocVal.getAllocStatement();
        Val base = statement.getInvokeExpr().getBase();
        Collection<AllocVal> allocSites = graph.getAllocSites(base);

        Collection<TransformedValue> extractedValues = new HashSet<>();
        for (AllocVal allocSite : allocSites) {
            Collection<TransformedValue> values = transformation.transformAllocationSite(allocSite, graph);
            extractedValues.addAll(values);
        }

        Collection<TransformedValue> transformedValues = new HashSet<>();
        for (TransformedValue value : extractedValues) {
            Val val = value.getTransformedVal();

            if (val.isStringConstant()) {
                String baseString = val.getStringValue();
                String transformedString = baseString.trim();

                Val stringVal = frameworkHandler.createStringConstant(
                        transformedString, statement.getMethod());
                TransformedValue transVal = new TransformedValue(stringVal, statement, value);
                transformedValues.add(transVal);
            } else {
                TransformedValue transVal = new TransformedValue(
                        allocVal.getAllocVal(),
                        statement,
                        Collections.emptySet(),
                        Collections.singleton(value));
                transformedValues.add(transVal);
            }
        }
        return transformedValues;
    }
    private Collection<TransformedValue> evaluateSubstring(
            AllocVal allocVal, AllocationSiteGraph graph, TransformationHandler transformation) {
        Statement statement = allocVal.getAllocStatement();
        InvokeExpr invokeExpr = statement.getInvokeExpr();
        MethodWrapper methodWrapper = invokeExpr.getDeclaredMethod().toMethodWrapper();

        Val base = invokeExpr.getBase();
        Val beginIndex = invokeExpr.getArg(0);

        Collection<AllocVal> baseAllocSites = graph.getAllocSites(base);
        Collection<AllocVal> beginIndexAllocSites = graph.getAllocSites(beginIndex);

        Collection<TransformedValue> extractedBaseValues = new HashSet<>();
        for (AllocVal baseAllocSite : baseAllocSites) {
            Collection<TransformedValue> values = transformation.transformAllocationSite(baseAllocSite, graph);
            extractedBaseValues.addAll(values);
        }

        Collection<TransformedValue> extractedBeginIndexValues = new HashSet<>();
        for (AllocVal beginIndexAllocSite : beginIndexAllocSites) {
            Collection<TransformedValue> values = transformation.transformAllocationSite(beginIndexAllocSite, graph);
            extractedBeginIndexValues.addAll(values);
        }

        Collection<TransformedValue> transformedValues = new HashSet<>();

        if (methodWrapper.equals(SUBSTRING_TWO_PARAMS)) {
            // Handle substring(beginIndex, endIndex)
            Val endIndex = invokeExpr.getArg(1);
            Collection<AllocVal> endIndexAllocSites = graph.getAllocSites(endIndex);

            Collection<TransformedValue> extractedEndIndexValues = new HashSet<>();
            for (AllocVal endIndexAllocSite : endIndexAllocSites) {
                Collection<TransformedValue> values = transformation.transformAllocationSite(endIndexAllocSite, graph);
                extractedEndIndexValues.addAll(values);
            }

            for (TransformedValue extractedBase : extractedBaseValues) {
                for (TransformedValue extractedBeginIndex : extractedBeginIndexValues) {
                    for (TransformedValue extractedEndIndex : extractedEndIndexValues) {
                        Collection<TransformedValue> knownValues = new HashSet<>();
                        Collection<TransformedValue> unknownValues = new HashSet<>();

                        if (extractedBase.getTransformedVal().isStringConstant()) {
                            knownValues.add(extractedBase);
                        } else {
                            unknownValues.add(extractedBase);
                        }

                        if (extractedBeginIndex.getTransformedVal().isIntConstant()) {
                            knownValues.add(extractedBeginIndex);
                        } else {
                            unknownValues.add(extractedBeginIndex);
                        }

                        if (extractedEndIndex.getTransformedVal().isIntConstant()) {
                            knownValues.add(extractedEndIndex);
                        } else {
                            unknownValues.add(extractedEndIndex);
                        }

                        if (!unknownValues.isEmpty()) {
                            TransformedValue value = new TransformedValue(
                                    allocVal.getAllocVal(), statement, knownValues, unknownValues);
                            transformedValues.add(value);
                        } else {
                            // Evaluate substring(beginIndex, endIndex)
                            String baseString = extractedBase.getTransformedVal().getStringValue();
                            int beginIdx = extractedBeginIndex.getTransformedVal().getIntValue();
                            int endIdx = extractedEndIndex.getTransformedVal().getIntValue();

                            try {
                                String transformedString = baseString.substring(beginIdx, endIdx);
                                Val stringVal = frameworkHandler.createStringConstant(
                                        transformedString, statement.getMethod());
                                TransformedValue value = new TransformedValue(stringVal, statement, knownValues);
                                transformedValues.add(value);
                            } catch (IndexOutOfBoundsException e) {
                                // Handle bounds error - return unknown
                                TransformedValue value = new TransformedValue(
                                        allocVal.getAllocVal(), statement, knownValues, unknownValues);
                                transformedValues.add(value);
                            }
                        }
                    }
                }
            }
        } else {
            // Handle substring(beginIndex)
            for (TransformedValue extractedBase : extractedBaseValues) {
                for (TransformedValue extractedBeginIndex : extractedBeginIndexValues) {
                    Collection<TransformedValue> knownValues = new HashSet<>();
                    Collection<TransformedValue> unknownValues = new HashSet<>();

                    if (extractedBase.getTransformedVal().isStringConstant()) {
                        knownValues.add(extractedBase);
                    } else {
                        unknownValues.add(extractedBase);
                    }

                    if (extractedBeginIndex.getTransformedVal().isIntConstant()) {
                        knownValues.add(extractedBeginIndex);
                    } else {
                        unknownValues.add(extractedBeginIndex);
                    }

                    if (!unknownValues.isEmpty()) {
                        TransformedValue value = new TransformedValue(
                                allocVal.getAllocVal(), statement, knownValues, unknownValues);
                        transformedValues.add(value);
                    } else {
                        // Evaluate substring(beginIndex)
                        String baseString = extractedBase.getTransformedVal().getStringValue();
                        int beginIdx = extractedBeginIndex.getTransformedVal().getIntValue();

                        try {
                            String transformedString = baseString.substring(beginIdx);
                            Val stringVal = frameworkHandler.createStringConstant(
                                    transformedString, statement.getMethod());
                            TransformedValue value = new TransformedValue(stringVal, statement, knownValues);
                            transformedValues.add(value);
                        } catch (IndexOutOfBoundsException e) {
                            // Handle bounds error - return unknown
                            TransformedValue value = new TransformedValue(
                                    allocVal.getAllocVal(), statement, knownValues, unknownValues);
                            transformedValues.add(value);
                        }
                    }
                }
            }
        }

        return transformedValues;
    }

    private Collection<TransformedValue> evaluateCharAt(
            AllocVal allocVal, AllocationSiteGraph graph, TransformationHandler transformation) {
        Statement statement = allocVal.getAllocStatement();
        InvokeExpr invokeExpr = statement.getInvokeExpr();

        Val base = invokeExpr.getBase();
        Val index = invokeExpr.getArg(0);

        Collection<AllocVal> baseAllocSites = graph.getAllocSites(base);
        Collection<AllocVal> indexAllocSites = graph.getAllocSites(index);

        Collection<TransformedValue> extractedBaseValues = new HashSet<>();
        for (AllocVal baseAllocSite : baseAllocSites) {
            Collection<TransformedValue> values = transformation.transformAllocationSite(baseAllocSite, graph);
            extractedBaseValues.addAll(values);
        }

        Collection<TransformedValue> extractedIndexValues = new HashSet<>();
        for (AllocVal indexAllocSite : indexAllocSites) {
            Collection<TransformedValue> values = transformation.transformAllocationSite(indexAllocSite, graph);
            extractedIndexValues.addAll(values);
        }

        Collection<TransformedValue> transformedValues = new HashSet<>();
        for (TransformedValue extractedBase : extractedBaseValues) {
            for (TransformedValue extractedIndex : extractedIndexValues) {
                Collection<TransformedValue> knownValues = new HashSet<>();
                Collection<TransformedValue> unknownValues = new HashSet<>();

                if (extractedBase.getTransformedVal().isStringConstant()) {
                    knownValues.add(extractedBase);
                } else {
                    unknownValues.add(extractedBase);
                }

                if (extractedIndex.getTransformedVal().isIntConstant()) {
                    knownValues.add(extractedIndex);
                } else {
                    unknownValues.add(extractedIndex);
                }

                if (!unknownValues.isEmpty()) {
                    TransformedValue value = new TransformedValue(
                            allocVal.getAllocVal(), statement, knownValues, unknownValues);
                    transformedValues.add(value);
                } else {
                    // Evaluate charAt
                    String baseString = extractedBase.getTransformedVal().getStringValue();
                    int idx = extractedIndex.getTransformedVal().getIntValue();

                    try {
                        char ch = baseString.charAt(idx);
                        // Since FrameworkHandler doesn't have createCharConstant, convert to string
                        String charAsString = String.valueOf(ch);
                        Val stringVal = frameworkHandler.createStringConstant(charAsString, statement.getMethod());
                        TransformedValue value = new TransformedValue(stringVal, statement, knownValues);
                        transformedValues.add(value);
                    } catch (IndexOutOfBoundsException e) {
                        TransformedValue value = new TransformedValue(
                                allocVal.getAllocVal(), statement, knownValues, unknownValues);
                        transformedValues.add(value);
                    }
                }
            }
        }

        return transformedValues;
    }

    private Collection<TransformedValue> evaluateIndexOf(
            AllocVal allocVal, AllocationSiteGraph graph, TransformationHandler transformation) {
        Statement statement = allocVal.getAllocStatement();
        InvokeExpr invokeExpr = statement.getInvokeExpr();

        Val base = invokeExpr.getBase();
        Val searchStr = invokeExpr.getArg(0);

        Collection<AllocVal> baseAllocSites = graph.getAllocSites(base);
        Collection<AllocVal> searchStrAllocSites = graph.getAllocSites(searchStr);

        Collection<TransformedValue> extractedBaseValues = new HashSet<>();
        for (AllocVal baseAllocSite : baseAllocSites) {
            Collection<TransformedValue> values = transformation.transformAllocationSite(baseAllocSite, graph);
            extractedBaseValues.addAll(values);
        }

        Collection<TransformedValue> extractedSearchValues = new HashSet<>();
        for (AllocVal searchAllocSite : searchStrAllocSites) {
            Collection<TransformedValue> values = transformation.transformAllocationSite(searchAllocSite, graph);
            extractedSearchValues.addAll(values);
        }

        Collection<TransformedValue> transformedValues = new HashSet<>();
        for (TransformedValue extractedBase : extractedBaseValues) {
            for (TransformedValue extractedSearch : extractedSearchValues) {
                Collection<TransformedValue> knownValues = new HashSet<>();
                Collection<TransformedValue> unknownValues = new HashSet<>();

                if (extractedBase.getTransformedVal().isStringConstant()) {
                    knownValues.add(extractedBase);
                } else {
                    unknownValues.add(extractedBase);
                }

                if (extractedSearch.getTransformedVal().isStringConstant()) {
                    knownValues.add(extractedSearch);
                } else {
                    unknownValues.add(extractedSearch);
                }

                if (!unknownValues.isEmpty()) {
                    TransformedValue value = new TransformedValue(
                            allocVal.getAllocVal(), statement, knownValues, unknownValues);
                    transformedValues.add(value);
                } else {
                    // Evaluate indexOf
                    String baseString = extractedBase.getTransformedVal().getStringValue();
                    String searchString = extractedSearch.getTransformedVal().getStringValue();

                    int result = baseString.indexOf(searchString);
                    Val intVal = frameworkHandler.createIntConstant(result, statement.getMethod());
                    TransformedValue value = new TransformedValue(intVal, statement, knownValues);
                    transformedValues.add(value);
                }
            }
        }

        return transformedValues;
    }
}
