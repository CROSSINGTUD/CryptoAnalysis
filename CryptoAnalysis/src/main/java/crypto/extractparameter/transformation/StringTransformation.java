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

import boomerang.options.BoomerangOptions;
import boomerang.scope.AllocVal;
import boomerang.scope.FrameworkScope;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Statement;
import boomerang.scope.Type;
import boomerang.scope.Val;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import crypto.extractparameter.scope.StringVal;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/** Class for all transformations from the class {@link java.lang.String}. */
public class StringTransformation extends Transformation {

    private static final Signature TO_CHAR_ARRAY =
            new Signature("java.lang.String", "char[]", "toCharArray");
    private static final Signature GET_BYTES =
            new Signature("java.lang.String", "byte[]", "getBytes");
    private static final Signature GET_BYTES_WITH_PARAM =
            new Signature("java.lang.String", "byte[]", "getBytes", List.of("java.lang.String"));
    private static final Signature TO_UPPER_CASE =
            new Signature("java.lang.String", "java.lang.String", "toUpperCase");
    private static final Signature TO_UPPER_CASE_WITH_PARAM =
            new Signature(
                    "java.lang.String",
                    "java.lang.String",
                    "toUpperCase",
                    List.of("java.util.Locale"));
    private static final Signature TO_LOWER_CASE =
            new Signature("java.lang.String", "java.lang.String", "toLowerCase");
    private static final Signature TO_LOWER_CASE_WITH_PARAM =
            new Signature(
                    "java.lang.String",
                    "java.lang.String",
                    "toLowerCase",
                    List.of("java.util.Locale"));
    private static final Signature REPLACE =
            new Signature(
                    "java.lang.String",
                    "java.lang.String",
                    "replace",
                    List.of("java.lang.CharSequence", "java.lang.CharSequence"));

    /** Signatures for methods where the base is the corresponding transformed value */
    private static final Collection<Signature> STRING_BASE =
            Set.of(
                    TO_CHAR_ARRAY,
                    GET_BYTES,
                    GET_BYTES_WITH_PARAM,
                    TO_UPPER_CASE,
                    TO_UPPER_CASE_WITH_PARAM,
                    TO_LOWER_CASE,
                    TO_LOWER_CASE_WITH_PARAM);

    private static final Collection<Signature> STRING_SIGNATURES =
            Set.of(
                    TO_CHAR_ARRAY,
                    GET_BYTES,
                    GET_BYTES_WITH_PARAM,
                    TO_UPPER_CASE,
                    TO_UPPER_CASE_WITH_PARAM,
                    TO_LOWER_CASE,
                    TO_LOWER_CASE_WITH_PARAM,
                    REPLACE);

    protected static boolean isStringTransformation(Signature signature) {
        return STRING_SIGNATURES.contains(signature);
    }

    protected StringTransformation(FrameworkScope frameworkScope, BoomerangOptions options) {
        super(frameworkScope, options);
    }

    @Override
    protected Multimap<Val, Type> evaluateExpression(Statement statement, Signature signature) {
        if (STRING_BASE.contains(signature)) {
            return evaluateStringAsBase(statement);
        }

        if (signature.equals(REPLACE)) {
            return evaluateReplaceCharSequence(statement);
        }

        return HashMultimap.create();
    }

    /**
     * Evaluate and transform all String expressions where only the base is relevant, e.g. in
     *
     * <pre>{@code
     * String s = "test";
     * char[] c = s.toCharArray();
     * }</pre>
     *
     * the call to 's.toCharArray()' requires only the value of the base 's'.
     *
     * @param statement the statement with a corresponding invoked expression
     * @return all possible values for the base and their propagated types
     */
    private Multimap<Val, Type> evaluateStringAsBase(Statement statement) {
        InvokeExpr invokeExpr = statement.getInvokeExpr();
        Val base = invokeExpr.getBase();

        Multimap<AllocVal, Type> allocSites = computeAllocSites(statement, base);

        Multimap<Val, Type> result = HashMultimap.create();
        for (AllocVal allocSite : allocSites.keySet()) {
            Multimap<Val, Type> transformedAllocSites =
                    Transformation.transformAllocationSite(allocSite, frameworkScope, options);

            for (Val transformedVal : transformedAllocSites.keySet()) {
                if (!transformedVal.isStringConstant()) {
                    continue;
                }

                Collection<Type> types = transformedAllocSites.get(transformedVal);
                types.add(transformedVal.getType());

                result.putAll(transformedVal, types);
            }
        }

        return result;
    }

    /**
     * Evaluates and transforms the expression s.replace(a, b). This includes all possible
     * combinations that can be extracted for s, a and b.
     *
     * @param statement the statement with the replace expression
     * @return all possible results and their types
     */
    private Multimap<Val, Type> evaluateReplaceCharSequence(Statement statement) {
        InvokeExpr invokeExpr = statement.getInvokeExpr();

        Val base = invokeExpr.getBase();
        Val param1 = invokeExpr.getArg(0);
        Val param2 = invokeExpr.getArg(1);

        Multimap<AllocVal, Type> allocBases = computeAllocSites(statement, base);
        Multimap<AllocVal, Type> allocParams1 = computeAllocSites(statement, param1);
        Multimap<AllocVal, Type> allocParams2 = computeAllocSites(statement, param2);

        Multimap<Val, Type> extractedBases = extractAllocValues(allocBases);
        Multimap<Val, Type> extractedParams1 = extractAllocValues(allocParams1);
        Multimap<Val, Type> extractedParams2 = extractAllocValues(allocParams2);

        Multimap<Val, Type> result = HashMultimap.create();
        for (Val extractedBase : extractedBases.keySet()) {
            for (Val extractedParam1 : extractedParams1.keySet()) {
                for (Val extractedParam2 : extractedParams2.keySet()) {
                    if (!extractedBase.isStringConstant()
                            || !extractedParam1.isStringConstant()
                            || !extractedParam2.isStringConstant()) {
                        continue;
                    }

                    String stringBase = extractedBase.getStringValue();
                    String stringParam1 = extractedParam1.getStringValue();
                    String stringParam2 = extractedParam2.getStringValue();

                    String transformedString = stringBase.replace(stringParam1, stringParam2);
                    StringVal stringVal = new StringVal(transformedString, statement.getMethod());

                    Collection<Type> types = extractedBases.get(extractedBase);
                    types.add(stringVal.getType());

                    result.putAll(stringVal, types);
                }
            }
        }

        return result;
    }
}
