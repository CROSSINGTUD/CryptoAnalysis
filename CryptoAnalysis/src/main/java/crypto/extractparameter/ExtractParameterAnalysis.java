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

import boomerang.scope.DeclaredMethod;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import crypto.definition.Definitions;
import crypto.utils.MatcherUtils;
import crysl.rule.CrySLMethod;
import crysl.rule.CrySLRule;
import java.util.Collection;
import java.util.HashSet;

public class ExtractParameterAnalysis {

    private final QuerySolver querySolver;

    public ExtractParameterAnalysis(Definitions.ExtractParameterDefinition definition) {
        Definitions.QuerySolverDefinition querySolverDefinition =
                new Definitions.QuerySolverDefinition(
                        definition.scope(),
                        definition.timeout(),
                        definition.strategy(),
                        definition.reporter());
        this.querySolver = new QuerySolver(querySolverDefinition);
    }

    public Collection<ParameterWithExtractedValues> extractParameters(
            Collection<Statement> statements, CrySLRule rule) {
        Collection<ExtractParameterInfo> queries = computeQueries(statements, rule);

        Collection<ParameterWithExtractedValues> result = new HashSet<>();
        for (ExtractParameterInfo query : queries) {
            ParameterWithExtractedValues extractedValues = solveQuery(query);

            result.add(extractedValues);
        }

        return result;
    }

    public Collection<ExtractParameterInfo> computeQueries(
            Collection<Statement> statements, CrySLRule rule) {
        Collection<ExtractParameterInfo> result = new HashSet<>();

        for (Statement statement : statements) {
            if (!statement.containsInvokeExpr()) {
                continue;
            }

            DeclaredMethod declaredMethod = statement.getInvokeExpr().getDeclaredMethod();
            Collection<CrySLMethod> methods =
                    MatcherUtils.getMatchingCryslMethodsToDeclaredMethod(
                            rule.getEvents(), declaredMethod);

            for (CrySLMethod method : methods) {
                Collection<ExtractParameterInfo> queries = getQueriesAtCallSite(statement, method);

                result.addAll(queries);
            }
        }

        return result;
    }

    private Collection<ExtractParameterInfo> getQueriesAtCallSite(
            Statement statement, CrySLMethod method) {
        Collection<ExtractParameterInfo> result = new HashSet<>();

        for (int i = 0; i < method.getParameters().size(); i++) {
            String parameter = method.getParameters().get(i).getKey();

            Val param = statement.getInvokeExpr().getArg(i);
            ExtractParameterInfo info = new ExtractParameterInfo(statement, param, i, parameter);
            result.add(info);
        }

        return result;
    }

    public ParameterWithExtractedValues solveQuery(ExtractParameterInfo query) {
        return querySolver.solveQuery(query);
    }
}
