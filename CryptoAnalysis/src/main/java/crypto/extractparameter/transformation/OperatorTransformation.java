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
import boomerang.scope.Statement;
import boomerang.scope.Type;
import boomerang.scope.Val;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import crypto.extractparameter.scope.IntType;
import crypto.extractparameter.scope.IntVal;

/**
 * Class for transformations that do not belong to invoke expressions. This class expects an assign
 * statement where the right operation is a value that can be transformed.
 */
public class OperatorTransformation extends Transformation {

    protected OperatorTransformation(FrameworkScope frameworkScope, BoomerangOptions options) {
        super(frameworkScope, options);
    }

    public static boolean isOperatorTransformation(Val val) {
        if (val.isLengthExpr()) {
            return true;
        }

        return false;
    }

    @Override
    protected Multimap<Val, Type> evaluateExpression(Statement statement, Signature signature) {
        return HashMultimap.create();
    }

    protected Multimap<Val, Type> evaluateOperator(Statement statement, Val val) {
        if (val.isLengthExpr()) {
            return evaluateLengthOperator(statement, val);
        }

        return HashMultimap.create();
    }

    /**
     * Evaluates and transforms assign statements where the right side is a length expression for
     * arrays, e.g. for
     *
     * <pre>{@code
     * String[] arr = new String[] {"H", "i"};
     * int length = arr.length;
     * }</pre>
     *
     * the transformation extracts the value 2.
     *
     * @param statement the assign statement
     * @param lengthExpr the length expression
     * @return all possible length values with the type 'int'
     */
    private Multimap<Val, Type> evaluateLengthOperator(Statement statement, Val lengthExpr) {
        Val lengthOp = lengthExpr.getLengthOp();

        Multimap<AllocVal, Type> allocSites = computeAllocSites(statement, lengthOp);
        Multimap<Val, Type> extractedSites = extractAllocValues(allocSites);

        Multimap<Val, Type> result = HashMultimap.create();
        for (Val extractedSite : extractedSites.keySet()) {
            if (extractedSite.isArrayAllocationVal()) {
                Val arrLength = extractedSite.getArrayAllocationSize();

                result.put(arrLength, new IntType());
            } else if (extractedSite.isStringConstant()) {
                int stringLength = extractedSite.getStringValue().length();
                IntVal intVal = new IntVal(stringLength, statement.getMethod());

                result.put(intVal, intVal.getType());
            }
        }

        return result;
    }
}
