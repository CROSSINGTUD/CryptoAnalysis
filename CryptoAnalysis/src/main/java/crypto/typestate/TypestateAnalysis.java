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
import boomerang.WeightedForwardQuery;
import boomerang.debugger.Debugger;
import boomerang.options.BoomerangOptions;
import boomerang.results.ForwardBoomerangResults;
import boomerang.scope.ControlFlowGraph;
import boomerang.scope.FrameworkScope;
import boomerang.scope.Val;
import boomerang.solver.Strategies;
import crypto.definition.Definitions;
import crysl.rule.CrySLRule;
import ideal.IDEALAnalysis;
import ideal.IDEALAnalysisDefinition;
import ideal.IDEALResultHandler;
import ideal.IDEALSeedSolver;
import ideal.StoreIDEALResultHandler;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import sync.pds.solver.WeightFunctions;
import typestate.TransitionFunction;

public class TypestateAnalysis {

    private final Definitions.TypestateDefinition definition;
    private final TypestateAnalysisScope analysisScope;
    private final StoreIDEALResultHandler<TransitionFunction> resultHandler;

    public TypestateAnalysis(Definitions.TypestateDefinition definition) {
        this.definition = definition;

        Map<String, RuleTransitions> transitions = new HashMap<>();
        for (CrySLRule rule : definition.rules()) {
            transitions.put(rule.getClassName(), RuleTransitions.of(rule));
        }

        analysisScope = new TypestateAnalysisScope(definition.frameworkScope(), transitions);
        resultHandler = new StoreIDEALResultHandler<>();
    }

    public Collection<IdealResult> runTypestateAnalysis() {
        // Compute all seeds in the program
        Collection<Query> seeds = analysisScope.computeSeeds();

        Collection<IdealResult> results = new LinkedHashSet<>();
        for (Query seed : seeds) {
            if (seed instanceof ForwardSeedQuery query) {
                ForwardBoomerangResults<TransitionFunction> result =
                        runTypestateAnalysisForSeed(query);
                results.add(new IdealResult(query, result));
            }
        }

        return results;
    }

    public ForwardBoomerangResults<TransitionFunction> runTypestateAnalysisForSeed(
            ForwardSeedQuery query) {
        // Initialize typestate function
        Collection<LabeledMatcherTransition> transitions = query.getAllTransitions();
        TypestateFunction typestateFunction = new TypestateFunction(transitions);

        // Initialize and run IDEal
        IDEALAnalysisDefinition<TransitionFunction> idealDefinition =
                getIdealAnalysisDefinition(query, typestateFunction);
        IDEALAnalysis<TransitionFunction> idealAnalysis = new IDEALAnalysis<>(idealDefinition);
        return idealAnalysis.run(query);
    }

    private IDEALAnalysisDefinition<TransitionFunction> getIdealAnalysisDefinition(
            ForwardSeedQuery seedQuery, TypestateFunction typestateFunction) {
        return new IDEALAnalysisDefinition<>() {
            @Override
            public Collection<WeightedForwardQuery<TransitionFunction>> generate(
                    ControlFlowGraph.Edge stmt) {
                return typestateFunction.generateSeed(stmt);
            }

            @Override
            public WeightFunctions<
                            ControlFlowGraph.Edge, Val, ControlFlowGraph.Edge, TransitionFunction>
                    weightFunctions() {
                return typestateFunction;
            }

            @Override
            public Debugger<TransitionFunction> debugger(
                    IDEALSeedSolver<TransitionFunction> idealSeedSolver) {
                return new Debugger<>();
            }

            @Override
            public FrameworkScope getFrameworkFactory() {
                return definition.frameworkScope();
            }

            @Override
            public IDEALResultHandler<TransitionFunction> getResultHandler() {
                return resultHandler;
            }

            @Override
            public BoomerangOptions boomerangOptions() {
                return BoomerangOptions.builder()
                        .withAllocationSite(
                                new TypestateAllocationSite(
                                        seedQuery, definition.frameworkScope().getDataFlowScope()))
                        .withStaticFieldStrategy(Strategies.StaticFieldStrategy.FLOW_SENSITIVE)
                        .withAnalysisTimeout(definition.timeout())
                        .enableAllowMultipleQueries(true)
                        .build();
            }
        };
    }

    public Map<ForwardSeedQuery, ForwardBoomerangResults<TransitionFunction>> getResults() {
        Map<ForwardSeedQuery, ForwardBoomerangResults<TransitionFunction>> results =
                new HashMap<>();

        for (Map.Entry<
                        WeightedForwardQuery<TransitionFunction>,
                        ForwardBoomerangResults<TransitionFunction>>
                entry : resultHandler.getResults().entrySet()) {
            if (entry.getKey() instanceof ForwardSeedQuery forwardSeedQuery) {
                results.put(forwardSeedQuery, entry.getValue());
            }
        }
        return results;
    }
}
