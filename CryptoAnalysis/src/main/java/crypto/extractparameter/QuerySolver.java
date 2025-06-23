/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.extractparameter;

import boomerang.BackwardQuery;
import boomerang.Boomerang;
import boomerang.ForwardQuery;
import boomerang.options.BoomerangOptions;
import boomerang.results.BackwardBoomerangResults;
import boomerang.scope.AllocVal;
import boomerang.scope.ControlFlowGraph;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import crypto.definition.Definitions;
import crypto.extractparameter.transformation.ITransformation;
import crypto.extractparameter.transformation.TransformationHandler;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import wpds.impl.NoWeight;

public class QuerySolver {

    private final Definitions.QuerySolverDefinition definition;
    private final TransformationHandler transformation;
    private final BoomerangOptions options;

    public QuerySolver(Definitions.QuerySolverDefinition definition) {
        this.definition = definition;
        this.transformation = new TransformationHandler(definition.scope().getFrameworkHandler());
        this.options =
                BoomerangOptions.builder()
                        .withAllocationSite(
                                new ExtractParameterAllocationSite(
                                        definition.scope().getFrameworkHandler(),
                                        definition.scope().asFrameworkScope().getDataFlowScope(),
                                        transformation.getTransformations()))
                        .withAnalysisTimeout(definition.timeout())
                        .withSparsificationStrategy(definition.strategy())
                        .build();
    }

    public ParameterWithExtractedValues solveQuery(ExtractParameterInfo info) {
        AllocationSiteGraph graph = computeAllocationSiteGraph(info.param(), info.statement());
        Collection<TransformedValue> transformedValues =
                transformation.computeTransformedValues(graph);

        // If no allocation site has been found, add the zero value to indicate it
        if (transformedValues.isEmpty()) {
            TransformedValue zeroVal =
                    new TransformedValue(UnknownVal.getInstance(), info.statement());

            return new ParameterWithExtractedValues(
                    info.statement(),
                    info.param(),
                    info.index(),
                    info.varNameInSpec(),
                    Collections.singleton(zeroVal));
        }

        return new ParameterWithExtractedValues(
                info.statement(),
                info.param(),
                info.index(),
                info.varNameInSpec(),
                transformedValues);
    }

    private AllocationSiteGraph computeAllocationSiteGraph(Val sourceVal, Statement statement) {
        Collection<AllocVal> initialAllocSites = computeAllocationSites(sourceVal, statement);

        AllocationSiteGraph graph = new AllocationSiteGraph(sourceVal);
        graph.addValToAllocSiteEdge(sourceVal, initialAllocSites);

        Queue<AllocVal> workList = new LinkedList<>(initialAllocSites);
        while (!workList.isEmpty()) {
            AllocVal allocVal = workList.poll();
            Statement allocStmt = allocVal.getAllocStatement();

            for (ITransformation transformation : transformation.getTransformations()) {
                Collection<Val> requiredValues = transformation.computeRequiredValues(allocStmt);
                graph.addAllocSiteToValEdge(allocVal, requiredValues);

                for (Val val : requiredValues) {
                    Collection<AllocVal> allocSites = computeAllocationSites(val, allocStmt);
                    graph.addValToAllocSiteEdge(val, allocSites);

                    workList.addAll(allocSites);
                }
            }
        }

        return graph;
    }

    private Collection<AllocVal> computeAllocationSites(Val val, Statement statement) {
        Collection<AllocVal> result = new HashSet<>();

        for (Statement pred : statement.getMethod().getControlFlowGraph().getPredsOf(statement)) {
            ControlFlowGraph.Edge edge = new ControlFlowGraph.Edge(pred, statement);
            BackwardQuery query = BackwardQuery.make(edge, val);

            definition.reporter().beforeTriggeringBoomerangQuery(query);
            Boomerang boomerang = new Boomerang(definition.scope().asFrameworkScope(), options);
            BackwardBoomerangResults<NoWeight> results = boomerang.solve(query);
            definition.reporter().afterTriggeringBoomerangQuery(query);

            definition.reporter().extractedBoomerangResults(query, results);
            if (results.isTimedout()) {
                definition
                        .reporter()
                        .onExtractParameterAnalysisTimeout(
                                query.var(), query.cfgEdge().getTarget());
            }

            for (ForwardQuery allocSite : results.getAllocationSites().keySet()) {
                result.add(allocSite.getAllocVal());
            }
        }

        return result;
    }
}
