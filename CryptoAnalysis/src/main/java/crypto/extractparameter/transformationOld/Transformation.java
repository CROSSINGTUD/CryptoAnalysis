/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.extractparameter.transformationOld;

import boomerang.BackwardQuery;
import boomerang.Boomerang;
import boomerang.ForwardQuery;
import boomerang.options.BoomerangOptions;
import boomerang.results.BackwardBoomerangResults;
import boomerang.scope.AllocVal;
import boomerang.scope.ControlFlowGraph;
import boomerang.scope.DeclaredMethod;
import boomerang.scope.FrameworkScope;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Statement;
import boomerang.scope.Type;
import boomerang.scope.Val;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import crypto.extractparameter.TypedAllocVal;
import java.util.ArrayList;
import java.util.List;
import wpds.impl.NoWeight;

/**
 * Main class for transforming the allocation sites when using Boomerang to extract relevant
 * parameters. When running Boomerang, one can use the function {@link
 * Transformation#isTransformationExpression(Statement)} to check if there is an implemented
 * transformation for the given statement. Note that transformations require an invoke expression to
 * run. After checking corresponding statements, one can use {@link
 * Transformation#transformAllocationSite(AllocVal, FrameworkScope, BoomerangOptions)} to transform
 * Boomerang's detected allocations sites, if an implementation is available.
 */
public abstract class Transformation {

    protected record Signature(
            String declaringClass, String returnValue, String name, List<String> params) {
        public Signature(String declaringClass, String returnValue, String name) {
            this(declaringClass, returnValue, name, new ArrayList<>());
        }
    }

    public static boolean isTransformationExpression(Statement statement) {
        if (!statement.containsInvokeExpr()) {
            return false;
        }

        Signature signature = invokeExprToSignature(statement.getInvokeExpr());

        if (StringTransformation.isStringTransformation(signature)) {
            return true;
        }

        if (WrapperTransformation.isWrapperTransformation(signature)) {
            return true;
        }

        return false;
    }

    private static Signature invokeExprToSignature(InvokeExpr invokeExpr) {
        DeclaredMethod method = invokeExpr.getDeclaredMethod();

        List<String> params = method.getParameterTypes().stream().map(Object::toString).toList();
        return new Signature(
                method.getDeclaringClass().getFullyQualifiedName(),
                method.getReturnType().toString(),
                method.getName(),
                params);
    }

    public static Multimap<Val, Type> transformAllocationSite(
            AllocVal allocVal, FrameworkScope frameworkScope, BoomerangOptions options) {
        Statement allocStatement = allocVal.getAllocStatement();

        if (!allocStatement.containsInvokeExpr()) {
            Val allocSite = allocVal.getAllocVal();

            // Check for basic transformation operators (e.g. length)
            if (OperatorTransformation.isOperatorTransformation(allocSite)) {
                OperatorTransformation transformation =
                        new OperatorTransformation(frameworkScope, options);

                return transformation.evaluateOperator(allocStatement, allocSite);
            }

            // For direct values (e.g. constants), we take their type
            Multimap<Val, Type> allocMap = HashMultimap.create();
            allocMap.put(allocSite, allocSite.getType());

            return allocMap;
        }

        Signature signature = invokeExprToSignature(allocStatement.getInvokeExpr());
        if (StringTransformation.isStringTransformation(signature)) {
            Transformation transformation = new StringTransformation(frameworkScope, options);

            return transformation.evaluateExpression(allocStatement, signature);
        } else if (WrapperTransformation.isWrapperTransformation(signature)) {
            Transformation transformation = new WrapperTransformation(frameworkScope, options);

            return transformation.evaluateExpression(allocStatement, signature);
        } else if (MiscellaneousTransformation.isMiscellaneousTransformation(signature)) {
            Transformation transformation =
                    new MiscellaneousTransformation(frameworkScope, options);

            return transformation.evaluateExpression(allocStatement, signature);
        }

        return HashMultimap.create();
    }

    protected final FrameworkScope frameworkScope;
    protected final BoomerangOptions options;

    protected Transformation(FrameworkScope frameworkScope, BoomerangOptions options) {
        this.frameworkScope = frameworkScope;
        this.options = options;
    }

    protected Multimap<AllocVal, Type> computeAllocSites(Statement statement, Val val) {
        Multimap<AllocVal, Type> boomerangResults = HashMultimap.create();

        for (Statement pred : statement.getMethod().getControlFlowGraph().getPredsOf(statement)) {
            ControlFlowGraph.Edge edge = new ControlFlowGraph.Edge(pred, statement);

            BackwardQuery query = BackwardQuery.make(edge, val);
            Boomerang boomerang = new Boomerang(frameworkScope, options);
            BackwardBoomerangResults<NoWeight> results = boomerang.solve(query);

            for (ForwardQuery forwardQuery : results.getAllocationSites().keySet()) {
                AllocVal allocVal = forwardQuery.getAllocVal();
                if (allocVal instanceof TypedAllocVal typedAllocVal) {
                    boomerangResults.put(forwardQuery.getAllocVal(), typedAllocVal.getType());
                } else {
                    // TODO Unkwown type
                }
            }
        }

        return boomerangResults;
    }

    protected Multimap<Val, Type> extractAllocValues(Multimap<AllocVal, Type> allocSites) {
        Multimap<Val, Type> result = HashMultimap.create();

        for (AllocVal allocVal : allocSites.keySet()) {
            Multimap<Val, Type> transformedAllocSites =
                    Transformation.transformAllocationSite(allocVal, frameworkScope, options);

            result.putAll(transformedAllocSites);
        }

        return result;
    }

    protected abstract Multimap<Val, Type> evaluateExpression(
            Statement statement, Signature signature);
}
