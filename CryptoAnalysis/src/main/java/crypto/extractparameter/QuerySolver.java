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

import boomerang.Boomerang;
import boomerang.ForwardQuery;
import boomerang.options.BoomerangOptions;
import boomerang.results.BackwardBoomerangResults;
import boomerang.scope.AllocVal;
import boomerang.scope.Type;
import boomerang.scope.Val;
import boomerang.scope.ValCollection;
import com.google.common.collect.Multimap;
import crypto.definition.Definitions;
import crypto.extractparameter.transformation.Transformation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import wpds.impl.NoWeight;

public class QuerySolver {

    private final Definitions.QuerySolverDefinition definition;
    private final BoomerangOptions options;

    public QuerySolver(Definitions.QuerySolverDefinition definition) {
        this.definition = definition;
        this.options =
                BoomerangOptions.builder()
                        .withAllocationSite(
                                new ExtractParameterAllocationSite(
                                        definition.frameworkScope().getDataFlowScope()))
                        .withAnalysisTimeout(definition.timeout())
                        .withSparsificationStrategy(definition.strategy())
                        .enableTrackStaticFieldAtEntryPointToClinit(true)
                        .build();
    }

    public ParameterWithExtractedValues solveQuery(ExtractParameterQuery query) {
        Boomerang boomerang = new Boomerang(definition.frameworkScope(), options);

        definition.reporter().beforeTriggeringBoomerangQuery(query);
        BackwardBoomerangResults<NoWeight> results = boomerang.solve(query);
        definition.reporter().afterTriggeringBoomerangQuery(query);

        definition.reporter().extractedBoomerangResults(query, results);
        if (results.isTimedout()) {
            definition
                    .reporter()
                    .onExtractParameterAnalysisTimeout(query.var(), query.cfgEdge().getTarget());
        }

        return extractValuesFromQueryResult(query, results);
    }

    private ParameterWithExtractedValues extractValuesFromQueryResult(
            ExtractParameterQuery query, BackwardBoomerangResults<NoWeight> results) {

        Collection<ForwardQuery> allocSites = results.getAllocationSites().keySet();

        // If no allocation site has been found, add the zero value to indicate it
        if (allocSites.isEmpty()) {
            return extractedZeroValue(query, Collections.emptySet());
        }

        Collection<ExtractedValue> extractedValues = new HashSet<>();
        Collection<Type> originalTypes = new HashSet<>();
        for (ForwardQuery allocSite : allocSites) {
            Collection<Type> allTypes = new HashSet<>();

            AllocVal allocVal = allocSite.getAllocVal();
            Multimap<Val, Type> sites =
                    Transformation.transformAllocationSite(
                            allocVal, definition.frameworkScope(), options);

            if (allocVal instanceof TypedAllocVal typedAllocVal) {
                originalTypes.add(typedAllocVal.getType());
                allTypes.add(typedAllocVal.getType());
            }

            if (sites.isEmpty()) {
                ExtractedValue extractedValue =
                        new ExtractedValue(
                                allocVal.getAllocVal(), allocVal.getAllocStatement(), allTypes);
                extractedValues.add(extractedValue);
            }

            for (Val site : sites.keySet()) {
                Collection<Type> types = sites.get(site);
                types.addAll(allTypes);

                ExtractedValue extractedValue =
                        new ExtractedValue(site, allocVal.getAllocStatement(), types);
                extractedValues.add(extractedValue);
            }
        }

        return new ParameterWithExtractedValues(
                query.cfgEdge().getTarget(),
                query.var(),
                query.getIndex(),
                query.getVarNameInSpec(),
                extractedValues,
                originalTypes);
    }

    private ParameterWithExtractedValues extractedZeroValue(
            ExtractParameterQuery query, Collection<Type> types) {
        ExtractedValue zeroVal =
                new ExtractedValue(ValCollection.zero(), query.cfgEdge().getTarget(), types);

        return new ParameterWithExtractedValues(
                query.cfgEdge().getTarget(),
                query.var(),
                query.getIndex(),
                query.getVarNameInSpec(),
                Collections.singleton(zeroVal),
                // TODO Unknown type
                Collections.emptySet());
    }
}
