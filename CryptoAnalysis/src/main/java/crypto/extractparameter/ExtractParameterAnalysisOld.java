package crypto.extractparameter;

import boomerang.ForwardQuery;
import boomerang.scene.AllocVal;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.DeclaredMethod;
import boomerang.scene.Statement;
import boomerang.scene.Type;
import boomerang.scene.Val;
import crypto.extractparameter.transformation.TransformedAllocVal;
import crypto.utils.MatcherUtils;
import crysl.rule.CrySLMethod;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class ExtractParameterAnalysisOld {

    private final Collection<ExtractParameterQueryOld> queries;
    private final Collection<CallSiteWithExtractedValue> collectedValues;
    private final ExtractParameterDefinition definition;

    public ExtractParameterAnalysisOld(ExtractParameterDefinition definition) {
        this.definition = definition;

        queries = new HashSet<>();
        collectedValues = new HashSet<>();
    }

    public void run() {
        for (Statement statement : definition.getCollectedCalls()) {
            if (!statement.containsInvokeExpr()) {
                continue;
            }

            DeclaredMethod declaredMethod = statement.getInvokeExpr().getMethod();
            Collection<CrySLMethod> methods =
                    MatcherUtils.getMatchingCryslMethodsToDeclaredMethod(
                            definition.getRule(), declaredMethod);

            for (CrySLMethod method : methods) {
                injectQueryAtCallSite(statement, method);
            }
        }

        for (ExtractParameterQueryOld query : queries) {
            definition.getAnalysisReporter().beforeTriggeringBoomerangQuery(query);
            query.solve();
            definition.getAnalysisReporter().afterTriggeringBoomerangQuery(query);
        }
    }

    private void injectQueryAtCallSite(Statement statement, CrySLMethod method) {
        for (int i = 0; i < method.getParameters().size(); i++) {
            String parameter = method.getParameters().get(i).getKey();

            addQueryAtCallSite(statement, parameter, i);
        }
    }

    private void addQueryAtCallSite(Statement statement, String varNameInSpec, int index) {
        Val parameter = statement.getInvokeExpr().getArg(index);

        Collection<Statement> predecessors =
                statement.getMethod().getControlFlowGraph().getPredsOf(statement);
        for (Statement pred : predecessors) {
            ControlFlowGraph.Edge edge = new ControlFlowGraph.Edge(pred, statement);

            ExtractParameterQueryOld query =
                    new ExtractParameterQueryOld(definition, edge, parameter, index);
            query.addListener(
                    results -> {
                        Collection<Map.Entry<Val, Statement>> extractedParameters = new HashSet<>();

                        Collection<ForwardQuery> filteredQueries =
                                filterBoomerangResults(results.getAllocationSites().keySet());
                        for (ForwardQuery paramQuery : filteredQueries) {
                            Val val = paramQuery.var();

                            if (val instanceof AllocVal allocVal) {
                                Map.Entry<Val, Statement> entry =
                                        new AbstractMap.SimpleEntry<>(
                                                allocVal.getAllocVal(),
                                                paramQuery.cfgEdge().getStart());
                                extractedParameters.add(entry);
                            } else {
                                Map.Entry<Val, Statement> entry =
                                        new AbstractMap.SimpleEntry<>(
                                                val, paramQuery.cfgEdge().getStart());
                                extractedParameters.add(entry);
                            }
                        }

                        CallSiteWithParamIndex callSiteWithParam =
                                new CallSiteWithParamIndex(statement, index, varNameInSpec);
                        Collection<Type> types = results.getPropagationType();

                        // If no value could be extracted, add the zero value to indicate it
                        if (extractedParameters.isEmpty()) {
                            ExtractedValue zeroVal =
                                    new ExtractedValue(Val.zero(), statement, types);

                            CallSiteWithExtractedValue callSite =
                                    new CallSiteWithExtractedValue(callSiteWithParam, zeroVal);
                            collectedValues.add(callSite);
                            return;
                        }

                        for (Map.Entry<Val, Statement> entry : extractedParameters) {
                            // The extracted value may be transformed, i.e. not the propagated type
                            types.add(entry.getKey().getType());

                            ExtractedValue extractedValue =
                                    new ExtractedValue(entry.getKey(), entry.getValue(), types);

                            CallSiteWithExtractedValue callSite =
                                    new CallSiteWithExtractedValue(
                                            callSiteWithParam, extractedValue);
                            collectedValues.add(callSite);
                        }
                    });
            queries.add(query);
        }
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

    public Collection<CallSiteWithExtractedValue> getExtractedValues() {
        return collectedValues;
    }
}
