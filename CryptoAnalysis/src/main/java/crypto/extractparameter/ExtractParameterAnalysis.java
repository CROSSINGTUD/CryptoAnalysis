package crypto.extractparameter;

import boomerang.scene.ControlFlowGraph;
import boomerang.scene.DeclaredMethod;
import boomerang.scene.Statement;
import boomerang.scene.Val;
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
                        definition.callGraph(),
                        definition.dataFlowScope(),
                        definition.timeout(),
                        definition.strategy(),
                        definition.reporter());
        this.querySolver = new QuerySolver(querySolverDefinition);
    }

    public Collection<ParameterWithExtractedValues> extractParameters(
            Collection<Statement> statements, CrySLRule rule) {
        Collection<ExtractParameterQuery> queries = computeQueries(statements, rule);

        Collection<ParameterWithExtractedValues> result = new HashSet<>();
        for (ExtractParameterQuery query : queries) {
            ParameterWithExtractedValues extractedValues = solveQuery(query);

            result.add(extractedValues);
        }

        return result;
    }

    public Collection<ExtractParameterQuery> computeQueries(
            Collection<Statement> statements, CrySLRule rule) {
        Collection<ExtractParameterQuery> result = new HashSet<>();

        for (Statement statement : statements) {
            if (!statement.containsInvokeExpr()) {
                continue;
            }

            DeclaredMethod declaredMethod = statement.getInvokeExpr().getMethod();
            Collection<CrySLMethod> methods =
                    MatcherUtils.getMatchingCryslMethodsToDeclaredMethod(rule, declaredMethod);

            for (CrySLMethod method : methods) {
                Collection<ExtractParameterQuery> queries = getQueriesAtCallSite(statement, method);

                result.addAll(queries);
            }
        }

        return result;
    }

    private Collection<ExtractParameterQuery> getQueriesAtCallSite(
            Statement statement, CrySLMethod method) {
        Collection<ExtractParameterQuery> result = new HashSet<>();

        for (int i = 0; i < method.getParameters().size(); i++) {
            String parameter = method.getParameters().get(i).getKey();

            Collection<ExtractParameterQuery> queries =
                    getQueriesForParameter(statement, i, parameter);
            result.addAll(queries);
        }

        return result;
    }

    private Collection<ExtractParameterQuery> getQueriesForParameter(
            Statement statement, int index, String parameter) {
        Collection<ExtractParameterQuery> result = new HashSet<>();

        Val param = statement.getInvokeExpr().getArg(index);

        Collection<Statement> predecessors =
                statement.getMethod().getControlFlowGraph().getPredsOf(statement);
        for (Statement pred : predecessors) {
            ControlFlowGraph.Edge edge = new ControlFlowGraph.Edge(pred, statement);

            ExtractParameterQuery query = new ExtractParameterQuery(edge, param, index, parameter);
            result.add(query);
        }

        return result;
    }

    public ParameterWithExtractedValues solveQuery(ExtractParameterQuery query) {
        return querySolver.solveQuery(query);
    }
}
