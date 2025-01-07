package crypto.extractparameter;

import boomerang.Boomerang;
import boomerang.ForwardQuery;
import boomerang.results.BackwardBoomerangResults;
import boomerang.scene.AllocVal;
import boomerang.scene.Statement;
import boomerang.scene.Type;
import boomerang.scene.Val;
import crypto.definition.Definitions;
import crypto.extractparameter.transformation.TransformedAllocVal;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import wpds.impl.Weight;

public class QuerySolver {

    private final Definitions.QuerySolverDefinition definition;

    public QuerySolver(Definitions.QuerySolverDefinition definition) {
        this.definition = definition;
    }

    public ParameterWithExtractedValues solveQuery(ExtractParameterQuery query) {
        Definitions.BoomerangOptionsDefinition optionsDefinition =
                new Definitions.BoomerangOptionsDefinition(
                        definition.callGraph(),
                        definition.dataFlowScope(),
                        definition.timeout(),
                        definition.strategy());
        ExtractParameterOptions options = new ExtractParameterOptions(optionsDefinition);

        Boomerang boomerang =
                new Boomerang(definition.callGraph(), definition.dataFlowScope(), options);

        definition.reporter().beforeTriggeringBoomerangQuery(query);
        BackwardBoomerangResults<Weight.NoWeight> results = boomerang.solve(query);
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
            ExtractParameterQuery query, BackwardBoomerangResults<Weight.NoWeight> results) {

        Collection<Map.Entry<Val, Statement>> extractedParameters = new HashSet<>();

        Collection<ForwardQuery> allocSites = results.getAllocationSites().keySet();
        Collection<ForwardQuery> filteredAllocSites = filterBoomerangResults(allocSites);
        for (ForwardQuery paramQuery : filteredAllocSites) {
            Statement initStatement = paramQuery.cfgEdge().getStart();
            Val val = paramQuery.var();

            if (val instanceof AllocVal allocVal) {
                Map.Entry<Val, Statement> entry =
                        new AbstractMap.SimpleEntry<>(allocVal.getAllocVal(), initStatement);
                extractedParameters.add(entry);
            } else {
                Map.Entry<Val, Statement> entry = new AbstractMap.SimpleEntry<>(val, initStatement);
                extractedParameters.add(entry);
            }
        }

        Collection<Type> types = results.getPropagationType();

        // If no value could be extracted, add the zero value to indicate it
        if (extractedParameters.isEmpty()) {
            ExtractedValue zeroVal =
                    new ExtractedValue(Val.zero(), query.cfgEdge().getTarget(), types);

            return new ParameterWithExtractedValues(
                    query.cfgEdge().getTarget(),
                    query.var(),
                    query.getIndex(),
                    query.getVarNameInSpec(),
                    Collections.singleton(zeroVal));
        }

        Collection<ExtractedValue> extractedValues = new HashSet<>();
        for (Map.Entry<Val, Statement> entry : extractedParameters) {
            // The extracted value may be transformed, i.e. not the propagated type
            Collection<Type> valueTypes = new HashSet<>(types);
            valueTypes.add(entry.getKey().getType());

            ExtractedValue extractedValue =
                    new ExtractedValue(entry.getKey(), entry.getValue(), valueTypes);

            extractedValues.add(extractedValue);
        }

        return new ParameterWithExtractedValues(
                query.cfgEdge().getTarget(),
                query.var(),
                query.getIndex(),
                query.getVarNameInSpec(),
                extractedValues);
    }

    private Collection<ForwardQuery> filterBoomerangResults(
            Collection<ForwardQuery> resultQueries) {
        Collection<ForwardQuery> transformedQueries = new HashSet<>();

        for (ForwardQuery query : resultQueries) {
            Val val = query.var();

            if (val instanceof TransformedAllocVal) {
                transformedQueries.add(query);
            }
        }

        if (transformedQueries.isEmpty()) {
            return resultQueries;
        }

        return transformedQueries;
    }
}
