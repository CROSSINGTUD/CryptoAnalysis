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
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BigIntegerTransformation extends AbstractTransformation implements ITransformation {

    private final MethodWrapper BIG_INTEGER_CONSTRUCTOR_STRING =
            new MethodWrapper(
                    "java.math.BigInteger", "<init>", "void", List.of("java.lang.String"));
    private final MethodWrapper BIG_INTEGER_CONSTRUCTOR_STRING_INT =
            new MethodWrapper(
                    "java.math.BigInteger", "<init>", "void", List.of("java.lang.String", "int"));
    private final MethodWrapper BIG_INTEGER_VALUE_OF =
            new MethodWrapper(
                    "java.math.BigInteger", "valueOf", "java.math.BigInteger", List.of("long"));

    public BigIntegerTransformation(FrameworkHandler frameworkHandler) {
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

        if (calledMethod.equals(BIG_INTEGER_CONSTRUCTOR_STRING)) {
            Val arg = invokeExpr.getArg(0);

            return Set.of(arg);
        }

        if (calledMethod.equals(BIG_INTEGER_CONSTRUCTOR_STRING_INT)) {
            Val arg1 = invokeExpr.getArg(0);
            Val arg2 = invokeExpr.getArg(1);

            return Set.of(arg1, arg2);
        }

        if (calledMethod.equals(BIG_INTEGER_VALUE_OF)) {
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

        if (methodWrapper.equals(BIG_INTEGER_CONSTRUCTOR_STRING)) {
            return evaluateBigIntegerConstructorString(allocVal, graph, transformation);
        }

        if (methodWrapper.equals(BIG_INTEGER_CONSTRUCTOR_STRING_INT)) {
            return evaluateBigIntegerConstructorStringInt(allocVal, graph, transformation);
        }

        if (methodWrapper.equals(BIG_INTEGER_VALUE_OF)) {
            return evaluateBigIntegerValueOf(allocVal, graph, transformation);
        }

        return Collections.emptySet();
    }

    private Collection<TransformedValue> evaluateBigIntegerConstructorString(
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

            if (val.isStringConstant()) {
                BigInteger bigInteger = new BigInteger(val.getStringValue());

                // Using Integer.MAX_VALUE is sufficient to model large integers
                int intValue;
                try {
                    intValue = bigInteger.intValueExact();
                } catch (ArithmeticException e) {
                    intValue = Integer.MAX_VALUE;
                }

                Val intVal = frameworkHandler.createIntConstant(intValue, statement.getMethod());
                TransformedValue transVal = new TransformedValue(intVal, statement, value);

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

    private Collection<TransformedValue> evaluateBigIntegerConstructorStringInt(
            AllocVal allocVal, AllocationSiteGraph graph, TransformationHandler handler) {
        Statement statement = allocVal.getAllocStatement();
        Val arg1 = statement.getInvokeExpr().getArg(0);
        Val arg2 = statement.getInvokeExpr().getArg(1);

        Collection<AllocVal> arg1AllocSites = graph.getAllocSites(arg1);
        Collection<AllocVal> arg2AllocSites = graph.getAllocSites(arg2);

        Collection<TransformedValue> extractedArg1Values = new HashSet<>();
        for (AllocVal allocSite : arg1AllocSites) {
            Collection<TransformedValue> values = handler.transformAllocationSite(allocSite, graph);
            extractedArg1Values.addAll(values);
        }

        Collection<TransformedValue> extractedArg2Values = new HashSet<>();
        for (AllocVal allocSite : arg2AllocSites) {
            Collection<TransformedValue> values = handler.transformAllocationSite(allocSite, graph);
            extractedArg2Values.addAll(values);
        }

        Collection<TransformedValue> transformedValues = new HashSet<>();
        for (TransformedValue arg1Value : extractedArg1Values) {
            for (TransformedValue arg2Value : extractedArg2Values) {

                Collection<TransformedValue> knownValues = new HashSet<>();
                Collection<TransformedValue> unknownValues = new HashSet<>();

                if (arg1Value.getTransformedVal().isStringConstant()) {
                    knownValues.add(arg1Value);
                } else {
                    unknownValues.add(arg2Value);
                }

                if (arg2Value.getTransformedVal().isIntConstant()) {
                    knownValues.add(arg2Value);
                } else {
                    unknownValues.add(arg2Value);
                }

                if (!unknownValues.isEmpty()) {
                    // At least one value is not known -> Cannot evaluate 'new BigInteger(String,
                    // int)'
                    TransformedValue value =
                            new TransformedValue(
                                    allocVal.getAllocVal(), statement, knownValues, unknownValues);
                    transformedValues.add(value);
                } else {
                    BigInteger bigInteger =
                            new BigInteger(
                                    arg1Value.getTransformedVal().getStringValue(),
                                    arg2Value.getTransformedVal().getIntValue());

                    // Using Integer.MAX_VALUE is sufficient to model large integers
                    int intValue;
                    try {
                        intValue = bigInteger.intValueExact();
                    } catch (ArithmeticException e) {
                        intValue = Integer.MAX_VALUE;
                    }

                    Val intVal =
                            frameworkHandler.createIntConstant(intValue, statement.getMethod());
                    TransformedValue transVal =
                            new TransformedValue(intVal, statement, knownValues, unknownValues);

                    transformedValues.add(transVal);
                }
            }
        }

        return transformedValues;
    }

    private Collection<TransformedValue> evaluateBigIntegerValueOf(
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

            if (val.isLongConstant()) {
                // Instead of a BigInteger, we continue propagating the long value
                Method method = statement.getMethod();
                Val longVal = frameworkHandler.createLongConstant(val.getLongValue(), method);
                TransformedValue transVal = new TransformedValue(longVal, statement, value);

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
