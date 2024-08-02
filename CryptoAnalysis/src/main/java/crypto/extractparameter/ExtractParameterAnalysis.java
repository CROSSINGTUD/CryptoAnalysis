package crypto.extractparameter;

import boomerang.ForwardQuery;
import boomerang.scene.AllocVal;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.DeclaredMethod;
import boomerang.scene.Statement;
import boomerang.scene.Type;
import boomerang.scene.Val;
import crypto.analysis.CryptoScanner;
import crypto.rules.CrySLMethod;
import crypto.rules.CrySLRule;
import crypto.utils.MatcherUtils;

import java.util.Collection;
import java.util.HashSet;

public class ExtractParameterAnalysis {

    private final CryptoScanner scanner;
    private final Collection<Statement> collectedCalls;
    private final CrySLRule rule;
    private final Collection<ExtractParameterQuery> queries;
    private final Collection<CallSiteWithExtractedValue> collectedValues;

    public ExtractParameterAnalysis(CryptoScanner scanner, Collection<Statement> collectedCalls, CrySLRule rule) {
        this.scanner = scanner;
        this.collectedCalls = collectedCalls;
        this.rule = rule;

        queries = new HashSet<>();
        collectedValues = new HashSet<>();
    }

    public void run() {
        for (Statement statement : collectedCalls) {
            if (!statement.containsInvokeExpr()) {
                continue;
            }

            DeclaredMethod declaredMethod = statement.getInvokeExpr().getMethod();
            Collection<CrySLMethod> methods = MatcherUtils.getMatchingCryslMethodsToDeclaredMethod(rule, declaredMethod);

            for (CrySLMethod method : methods) {
                injectQueryAtCallSite(statement, method);
            }
        }

        for (ExtractParameterQuery query : queries) {
            scanner.getAnalysisReporter().beforeTriggeringBoomerangQuery(query);
            query.solve();
            scanner.getAnalysisReporter().afterTriggeringBoomerangQuery(query);
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

        Collection<Statement> predecessors = statement.getMethod().getControlFlowGraph().getPredsOf(statement);
        for (Statement pred : predecessors) {
            ControlFlowGraph.Edge edge = new ControlFlowGraph.Edge(pred, statement);

            ExtractParameterQuery query = new ExtractParameterQuery(scanner, edge, parameter, index);
            query.addListener(results -> {
                CallSiteWithParamIndex callSiteWithParam = new CallSiteWithParamIndex(statement, parameter, index, varNameInSpec);
                Collection<Type> types = results.getPropagationType();

                if (results.getAllocationSites().isEmpty()) {
                    ExtractedValue zeroVal = new ExtractedValue(Val.zero(), statement, types);

                    CallSiteWithExtractedValue callSite = new CallSiteWithExtractedValue(callSiteWithParam, zeroVal);
                    collectedValues.add(callSite);
                    return;
                }

                for (ForwardQuery forwardQuery : results.getAllocationSites().keySet()) {
                    Val val = forwardQuery.var();

                    ExtractedValue extractedValue;
                    if (val instanceof AllocVal) {
                        AllocVal allocVal = (AllocVal) val;
                        extractedValue = new ExtractedValue(allocVal.getAllocVal(), forwardQuery.cfgEdge().getStart(), types);
                    } else {
                        extractedValue = new ExtractedValue(val, forwardQuery.cfgEdge().getStart(), types);
                    }

                    CallSiteWithExtractedValue callSite = new CallSiteWithExtractedValue(callSiteWithParam, extractedValue);
                    collectedValues.add(callSite);
                }
            });
            queries.add(query);
        }
    }


    public Collection<CallSiteWithExtractedValue> getExtractedValues() {
        return collectedValues;
    }
}
