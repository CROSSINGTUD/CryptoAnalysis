/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.typestate;

import boomerang.Query;
import boomerang.scope.AllocVal;
import boomerang.scope.AnalysisScope;
import boomerang.scope.ControlFlowGraph;
import boomerang.scope.DeclaredMethod;
import boomerang.scope.FrameworkScope;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import crypto.utils.MatcherUtils;
import crysl.rule.CrySLMethod;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;

public class TypestateAnalysisScope extends AnalysisScope {

    private final FrameworkScope frameworkScope;
    private final Map<String, RuleTransitions> ruleTransitions;

    public TypestateAnalysisScope(
            FrameworkScope frameworkScope, Map<String, RuleTransitions> ruleTransitions) {
        super(frameworkScope.getCallGraph());

        this.ruleTransitions = ruleTransitions;
        this.frameworkScope = frameworkScope;
    }

    @Override
    protected Collection<? extends Query> generate(ControlFlowGraph.Edge edge) {
        Statement statement = edge.getStart();
        Collection<ForwardSeedQuery> discoveredSeeds = new LinkedHashSet<>();

        // Check if method should not be analyzed TODO Move this to AnalysisScope
        if (frameworkScope.getDataFlowScope().isExcluded(statement.getMethod())) {
            return discoveredSeeds;
        }

        // Check for seeds that originate from new expressions
        Collection<ForwardSeedQuery> newExprSeeds = computeSeedsFromNewExpressions(edge, statement);
        discoveredSeeds.addAll(newExprSeeds);

        // Check for seeds that originate from function calls
        Collection<ForwardSeedQuery> returnSeeds = computeSeedsFromFactory(edge, statement);
        discoveredSeeds.addAll(returnSeeds);

        return discoveredSeeds;
    }

    private Collection<ForwardSeedQuery> computeSeedsFromNewExpressions(
            ControlFlowGraph.Edge edge, Statement statement) {
        Collection<ForwardSeedQuery> newExprSeeds = new LinkedHashSet<>();

        if (!statement.isAssignStmt()) {
            return newExprSeeds;
        }

        if (statement.containsInvokeExpr()) {
            return newExprSeeds;
        }

        Val leftOp = statement.getLeftOp();
        Val rightOp = statement.getRightOp();

        if (rightOp.isNewExpr()) {
            String newExprType = rightOp.getNewExprType().toString();

            if (ruleTransitions.containsKey(newExprType)) {
                RuleTransitions rule = ruleTransitions.get(newExprType);
                AllocVal allocVal = new AllocVal(leftOp, statement, rightOp);

                ForwardSeedQuery seedQuery =
                        ForwardSeedQuery.makeQueryWithSpecification(edge, allocVal, rule);
                newExprSeeds.add(seedQuery);
            }
        }

        return newExprSeeds;
    }

    private Collection<ForwardSeedQuery> computeSeedsFromFactory(
            ControlFlowGraph.Edge edge, Statement statement) {
        Collection<ForwardSeedQuery> returnSeeds = new LinkedHashSet<>();

        if (!statement.isAssignStmt() || !statement.containsInvokeExpr()) {
            return returnSeeds;
        }

        InvokeExpr invokeExpr = statement.getInvokeExpr();
        if (!invokeExpr.isStaticInvokeExpr()) {
            return returnSeeds;
        }

        DeclaredMethod declaredMethod = invokeExpr.getDeclaredMethod();

        String classType = declaredMethod.getDeclaringClass().getFullyQualifiedName();
        Optional<ForwardSeedQuery> classQuery = computeSeedsForType(edge, statement, classType);
        if (classQuery.isPresent()) {
            returnSeeds.add(classQuery.get());

            return returnSeeds;
        }

        String returnType = declaredMethod.getReturnType().toString();
        Optional<ForwardSeedQuery> returnQuery = computeSeedsForType(edge, statement, returnType);
        if (returnQuery.isPresent()) {
            returnSeeds.add(returnQuery.get());

            return returnSeeds;
        }

        return returnSeeds;
    }

    private Optional<ForwardSeedQuery> computeSeedsForType(
            ControlFlowGraph.Edge edge, Statement statement, String type) {
        Val leftOp = statement.getLeftOp();
        Val rightOp = statement.getRightOp();
        AllocVal allocVal = new AllocVal(leftOp, statement, rightOp);

        DeclaredMethod declaredMethod = statement.getInvokeExpr().getDeclaredMethod();
        if (ruleTransitions.containsKey(type)) {
            RuleTransitions rule = ruleTransitions.get(type);

            Collection<CrySLMethod> methods =
                    MatcherUtils.getMatchingCryslMethodsToDeclaredMethod(
                            rule.getRule().getEvents(), declaredMethod);
            if (!methods.isEmpty()) {
                ForwardSeedQuery query =
                        ForwardSeedQuery.makeQueryWithSpecification(edge, allocVal, rule);

                return Optional.of(query);
            }
        }

        return Optional.empty();
    }
}
