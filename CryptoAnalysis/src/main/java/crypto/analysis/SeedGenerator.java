/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.analysis;

import boomerang.results.ForwardBoomerangResults;
import boomerang.scope.AllocVal;
import boomerang.scope.ControlFlowGraph;
import boomerang.scope.DeclaredMethod;
import boomerang.scope.FrameworkScope;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Statement;
import boomerang.scope.Type;
import boomerang.scope.Val;
import crypto.definition.Definitions;
import crypto.typestate.ForwardSeedQuery;
import crypto.typestate.IdealResult;
import crypto.typestate.RuleTransitions;
import crypto.typestate.TypestateAnalysis;
import crypto.utils.MatcherUtils;
import crysl.rule.CrySLMethod;
import crysl.rule.CrySLPredicate;
import crysl.rule.CrySLRule;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import typestate.TransitionFunction;

public class SeedGenerator {

    private final CryptoScanner scanner;
    private final FrameworkScope frameworkScope;
    private final Map<String, CrySLRule> rules;
    private final TypestateAnalysis typestateAnalysis;

    public SeedGenerator(
            CryptoScanner scanner, FrameworkScope frameworkScope, Collection<CrySLRule> ruleset) {
        this.scanner = scanner;
        this.frameworkScope = frameworkScope;

        this.rules = new HashMap<>();
        for (CrySLRule rule : ruleset) {
            rules.put(rule.getClassName(), rule);
        }

        Definitions.TypestateDefinition definition =
                new Definitions.TypestateDefinition(frameworkScope, ruleset, scanner.getTimeout());
        typestateAnalysis = new TypestateAnalysis(definition);
    }

    public Collection<IAnalysisSeed> computeSeeds() {
        scanner.getAnalysisReporter().beforeTypestateAnalysis();
        Collection<IdealResult> directSeedResults = computeDirectSeeds();
        Collection<IdealResult> indirectSeedResults = computeIndirectSeeds(directSeedResults);
        scanner.getAnalysisReporter().afterTypestateAnalysis();

        Collection<IdealResult> allResults = new LinkedHashSet<>(directSeedResults);
        allResults.addAll(indirectSeedResults);

        return computeSeedsFromResults(allResults);
    }

    private Collection<IdealResult> computeDirectSeeds() {
        return typestateAnalysis.runTypestateAnalysis();
    }

    private Collection<IdealResult> computeIndirectSeeds(Collection<IdealResult> results) {
        Collection<IdealResult> indirectSeedResults = new LinkedHashSet<>();

        Queue<IdealResult> workList = new LinkedList<>(results);
        while (!workList.isEmpty()) {
            IdealResult currRes = workList.poll();

            Collection<ForwardSeedQuery> indirectSeeds = computeIndirectSeedsFromResult(currRes);
            for (ForwardSeedQuery query : indirectSeeds) {
                ForwardBoomerangResults<TransitionFunction> result =
                        typestateAnalysis.runTypestateAnalysisForSeed(query);

                IdealResult seedResult = new IdealResult(query, result);
                indirectSeedResults.add(seedResult);

                if (query.hasSpecification()) {
                    workList.add(seedResult);
                }
            }
        }

        return indirectSeedResults;
    }

    private Collection<ForwardSeedQuery> computeIndirectSeedsFromResult(IdealResult result) {
        Collection<ForwardSeedQuery> seeds = new LinkedHashSet<>();

        ForwardSeedQuery query = result.query();
        CrySLRule rule = query.getRule();
        Collection<ControlFlowGraph.Edge> callsOnObject =
                result.results().getInvokedMethodOnInstance().keySet();

        for (ControlFlowGraph.Edge edge : callsOnObject) {
            if (query.cfgEdge().equals(edge)) {
                continue;
            }

            Optional<ForwardSeedQuery> indirectSeed = computeIndirectSeed(edge);
            indirectSeed.ifPresent(seeds::add);

            Collection<ForwardSeedQuery> predicateSeeds = computePredicateSeeds(edge, rule);
            seeds.addAll(predicateSeeds);
        }

        return seeds;
    }

    private Optional<ForwardSeedQuery> computeIndirectSeed(ControlFlowGraph.Edge edge) {
        Statement statement = edge.getStart();
        if (!statement.containsInvokeExpr() || !statement.isAssignStmt()) {
            return Optional.empty();
        }

        InvokeExpr invokeExpr = statement.getInvokeExpr();
        if (invokeExpr.isStaticInvokeExpr()) {
            return Optional.empty();
        }

        Type returnType = statement.getInvokeExpr().getDeclaredMethod().getReturnType();
        if (rules.containsKey(returnType.toString())) {
            Val leftOp = statement.getLeftOp();
            Val rightOp = statement.getRightOp();
            AllocVal allocVal = new AllocVal(leftOp, statement, rightOp);

            RuleTransitions transitions = RuleTransitions.of(rules.get(returnType.toString()));
            ForwardSeedQuery query =
                    ForwardSeedQuery.makeQueryWithSpecification(edge, allocVal, transitions);

            return Optional.of(query);
        }

        return Optional.empty();
    }

    private Collection<ForwardSeedQuery> computePredicateSeeds(
            ControlFlowGraph.Edge edge, CrySLRule rule) {
        Collection<ForwardSeedQuery> predicateSeeds = new LinkedHashSet<>();

        Statement statement = edge.getStart();
        if (!statement.containsInvokeExpr()) {
            return predicateSeeds;
        }

        InvokeExpr invokeExpr = statement.getInvokeExpr();
        DeclaredMethod declaredMethod = invokeExpr.getDeclaredMethod();
        Collection<CrySLMethod> methods =
                MatcherUtils.getMatchingCryslMethodsToDeclaredMethod(
                        rule.getEvents(), declaredMethod);

        for (CrySLMethod method : methods) {
            Map.Entry<String, String> targetObj = method.getRetObject();
            Type returnType = declaredMethod.getReturnType();

            if (isPredicateRelevantValue(targetObj.getKey(), rule)
                    && isNonExistingRuleType(returnType)) {
                if (statement.isAssignStmt()) {
                    Val leftOp = statement.getLeftOp();
                    Val rightOp = statement.getRightOp();
                    AllocVal fact = new AllocVal(leftOp, statement, rightOp);

                    ForwardSeedQuery query =
                            ForwardSeedQuery.makeQueryWithoutSpecification(edge, fact);
                    predicateSeeds.add(query);
                }
            }

            for (int i = 0; i < method.getParameters().size(); i++) {
                Map.Entry<String, String> param = method.getParameters().get(i);
                Type paramType = declaredMethod.getParameterType(i);

                if (isPredicateRelevantValue(param.getKey(), rule)
                        && isNonExistingRuleType(paramType)) {
                    Val paramVal = invokeExpr.getArg(i);
                    AllocVal fact = new AllocVal(paramVal, statement, paramVal);

                    ForwardSeedQuery query =
                            ForwardSeedQuery.makeQueryWithoutSpecification(edge, fact);
                    predicateSeeds.add(query);
                }
            }
        }

        return predicateSeeds;
    }

    private boolean isPredicateRelevantValue(String value, CrySLRule rule) {
        if (value.equals(CrySLMethod.NO_NAME)) {
            return false;
        }

        for (CrySLPredicate predicate : rule.getPredicates()) {
            // TODO Maybe also compare types?
            if (predicate.getParameters().get(0).getName().equals(value)) {
                return true;
            }
        }

        return false;
    }

    private boolean isNonExistingRuleType(Type type) {
        return !rules.containsKey(type.toString());
    }

    private Collection<IAnalysisSeed> computeSeedsFromResults(Collection<IdealResult> results) {
        Collection<IAnalysisSeed> seeds = new LinkedHashSet<>();

        for (IdealResult result : results) {
            ForwardSeedQuery query = result.query();
            Statement stmt = query.cfgEdge().getStart();
            AllocVal fact = query.getAllocVal();

            IAnalysisSeed seed;
            if (query.hasSpecification()) {
                CrySLRule rule = query.getRule();

                seed =
                        new AnalysisSeedWithSpecification(
                                scanner, stmt, fact, frameworkScope, result.results(), rule);
            } else {
                seed = new AnalysisSeedWithEnsuredPredicate(scanner, stmt, fact, result.results());
            }

            seeds.add(seed);

            if (result.results().isTimedOut()) {
                scanner.getAnalysisReporter().onTypestateAnalysisTimeout(seed);
            }

            scanner.getAnalysisReporter().typestateAnalysisResults(seed, result.results());
        }

        return seeds;
    }
}
