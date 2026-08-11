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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            if (frameworkHandler.isBinaryExpr(rightOp) && isArithmeticExpression(statement)) {
                // Try to extract operands from the binary expression
                Collection<Val> operands = extractArithmeticOperands(rightOp, statement);
                if (!operands.isEmpty()) {
                    return operands;
                }
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
            if (frameworkHandler.isBinaryExpr(rightOp) && isArithmeticExpression(statement)) {
                return evaluateArithmeticExpression(allocVal, rightOp, graph, transformation);
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

    private boolean isArithmeticExpression(Statement statement) {
        String stmtStr = statement.toString();

        // Look for arithmetic patterns but exclude string operations and method calls
        boolean hasArithmetic = stmtStr.contains(" + ") || stmtStr.contains(" - ") ||
                stmtStr.contains(" * ") || stmtStr.contains(" / ") ||
                stmtStr.contains(" % ");

        // Make sure it's not a method call or string operation
        boolean isMethodCall = stmtStr.contains("invoke") || stmtStr.contains(".");
        boolean isStringConcat = stmtStr.contains("\"") && stmtStr.contains(" + ");

        return hasArithmetic && !isMethodCall && !isStringConcat;
    }

    private Collection<Val> extractArithmeticOperands(Val binaryExpr, Statement statement) {
        if (isConstantArithmetic(statement.toString())) {
            return Collections.emptySet();
        }
        return Collections.emptySet();
    }

    private Collection<TransformedValue> evaluateArithmeticExpression(
            AllocVal allocVal, Val binaryExpr, AllocationSiteGraph graph, TransformationHandler transformation) {

        Statement statement = allocVal.getAllocStatement();
        String stmtStr = statement.toString();

        // Handle constant arithmetic first
        if (isConstantArithmetic(stmtStr)) {
            return evaluateConstantArithmetic(allocVal, stmtStr);
        }
        return createUnknownTransformedValue(allocVal);
    }

    private boolean isConstantArithmetic(String statementStr) {
        // Match patterns like: var = number operator number
        Pattern constantPattern = Pattern.compile(".*=\\s*(-?\\d+)\\s*([+\\-*/])\\s*(-?\\d+).*");
        return constantPattern.matcher(statementStr).matches();
    }

    private Collection<TransformedValue> evaluateConstantArithmetic(AllocVal allocVal, String statementStr) {
        try {
            // Extract numbers and operator using regex
            Pattern pattern = Pattern.compile(".*=\\s*(-?\\d+)\\s*([+\\-*/])\\s*(-?\\d+).*");
            Matcher matcher = pattern.matcher(statementStr);

            if (matcher.matches()) {
                int leftValue = Integer.parseInt(matcher.group(1));
                String operator = matcher.group(2);
                int rightValue = Integer.parseInt(matcher.group(3));

                Integer result = performArithmeticOperation(leftValue, rightValue, operator);

                if (result != null) {
                    Method method = allocVal.getAllocStatement().getMethod();
                    Val intVal = frameworkHandler.createIntConstant(result, method);

                    TransformedValue value = new TransformedValue(intVal, allocVal.getAllocStatement(), Collections.emptySet());
                    return Collections.singleton(value);
                }
            }
        } catch (Exception e) {
        }

        return createUnknownTransformedValue(allocVal);
    }

    private Integer performArithmeticOperation(int left, int right, String operator) {
        try {
            switch (operator) {
                case "+":
                    return Math.addExact(left, right);
                case "-":
                    return Math.subtractExact(left, right);
                case "*":
                    return Math.multiplyExact(left, right);
                case "/":
                    if (right == 0) return null;
                    return left / right;
                case "%":
                    if (right == 0) return null;
                    return left % right;
                default:
                    return null;
            }
        } catch (ArithmeticException e) {
            return null;
        }
    }

    private Collection<TransformedValue> createUnknownTransformedValue(AllocVal allocVal) {
        TransformedValue unknownValue = new TransformedValue(
                allocVal.getAllocVal(),
                allocVal.getAllocStatement(),
                Collections.emptySet(),
                Collections.emptySet());
        return Collections.singleton(unknownValue);
    }
}
