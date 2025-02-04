package crypto.extractparameter;

import boomerang.Boomerang;
import boomerang.ForwardQuery;
import boomerang.results.BackwardBoomerangResults;
import boomerang.scene.AllocVal;
import boomerang.scene.Type;
import boomerang.scene.Val;
import com.google.common.collect.Multimap;
import crypto.definition.Definitions;
import crypto.extractparameter.transformation.Transformation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import wpds.impl.Weight;

public class QuerySolver {

    private final Definitions.QuerySolverDefinition definition;
    private final Definitions.BoomerangOptionsDefinition optionsDefinition;

    public QuerySolver(Definitions.QuerySolverDefinition definition) {
        this.definition = definition;
        this.optionsDefinition =
                new Definitions.BoomerangOptionsDefinition(
                        definition.callGraph(),
                        definition.dataFlowScope(),
                        definition.timeout(),
                        definition.strategy());
    }

    public ParameterWithExtractedValues solveQuery(ExtractParameterQuery query) {
        ExtendedBoomerangOptions options =
                new ExtendedBoomerangOptions(
                        optionsDefinition.timeout(), optionsDefinition.strategy());

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

        Collection<ForwardQuery> allocSites = results.getAllocationSites().keySet();
        Collection<Type> propagatedTypes = results.getPropagationType();

        // If no allocation site has been found, add the zero value to indicate it
        if (allocSites.isEmpty()) {
            return extractedZeroValue(query, propagatedTypes);
        }

        Collection<ExtractedValue> extractedValues = new HashSet<>();
        for (ForwardQuery allocSite : allocSites) {
            Val val = allocSite.var();
            if (val instanceof AllocVal allocVal) {
                Multimap<Val, Type> sites =
                        Transformation.transformAllocationSite(allocVal, optionsDefinition);

                for (Val site : sites.keySet()) {
                    Collection<Type> types = sites.get(site);
                    types.addAll(propagatedTypes);

                    ExtractedValue extractedValue =
                            new ExtractedValue(site, allocVal.getAllocStatement(), types);
                    extractedValues.add(extractedValue);
                }
            }
        }

        if (extractedValues.isEmpty()) {
            return extractedZeroValue(query, propagatedTypes);
        }

        return new ParameterWithExtractedValues(
                query.cfgEdge().getTarget(),
                query.var(),
                query.getIndex(),
                query.getVarNameInSpec(),
                extractedValues);
    }

    private ParameterWithExtractedValues extractedZeroValue(
            ExtractParameterQuery query, Collection<Type> types) {
        ExtractedValue zeroVal = new ExtractedValue(Val.zero(), query.cfgEdge().getTarget(), types);

        return new ParameterWithExtractedValues(
                query.cfgEdge().getTarget(),
                query.var(),
                query.getIndex(),
                query.getVarNameInSpec(),
                Collections.singleton(zeroVal));
    }
}
