package test;

import boomerang.scene.CallGraph;
import boomerang.scene.DataFlowScope;
import boomerang.scene.InvokeExpr;
import boomerang.scene.Method;
import boomerang.scene.Statement;
import boomerang.scene.Val;
import boomerang.scene.jimple.BoomerangPretransformer;
import boomerang.scene.jimple.JimpleMethod;
import boomerang.scene.jimple.SootCallGraph;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import crypto.analysis.CryptoScanner;
import crypto.listener.IErrorListener;
import crypto.listener.IResultsListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import soot.SceneTransformer;
import soot.options.Options;
import test.assertions.Assertions;
import test.assertions.CallToForbiddenMethodAssertion;
import test.assertions.ConstraintErrorCountAssertion;
import test.assertions.ConstraintsEvaluatedAssertion;
import test.assertions.ConstraintsNotRelevantAssertion;
import test.assertions.ConstraintsSatisfiedAssertion;
import test.assertions.ConstraintsViolatedAssertion;
import test.assertions.ExtractedValueAssertion;
import test.assertions.ForbiddenMethodErrorCountAssertion;
import test.assertions.HasEnsuredPredicateAssertion;
import test.assertions.ImpreciseValueExtractionErrorCountAssertion;
import test.assertions.IncompleteOperationErrorCountAssertion;
import test.assertions.NotHasEnsuredPredicateAssertion;
import test.assertions.PredicateContradictionErrorCountAssertion;
import test.assertions.PredicateErrorCountAssertion;
import test.assertions.TypestateErrorCountAssertion;
import test.assertions.states.MayBeInAcceptingStateAssertion;
import test.assertions.states.MustBeInAcceptingStateAssertion;
import test.assertions.states.MustNotBeInAcceptingStateAssertion;
import test.assertions.states.StateAssertion;
import test.core.selfrunning.AbstractTestingFramework;
import test.core.selfrunning.ImprecisionException;

public abstract class UsagePatternTestingFramework extends AbstractTestingFramework {

    private class TestScanner extends CryptoScanner {

        public void init() {
            super.initialize();
        }

        public void run() {
            super.scan();
        }

        @Override
        public String getRulesetPath() {
            return UsagePatternTestingFramework.this.getRulesetPath();
        }

        @Override
        protected CallGraph constructCallGraph() {
            BoomerangPretransformer.v().reset();
            BoomerangPretransformer.v().apply();

            return new SootCallGraph();
        }

        @Override
        protected DataFlowScope createDataFlowScope() {
            return new TestDataFlowScope(super.getRuleset());
        }
    }

    @Override
    protected SceneTransformer createAnalysisTransformer() throws ImprecisionException {

        Options.v().setPhaseOption("jb", "use-original-names:false");
        Options.v().set_keep_line_number(true);

        return new SceneTransformer() {

            protected void internalTransform(String phaseName, Map<String, String> options) {

                TestScanner scanner = new TestScanner();
                scanner.init();

                // Setup test listener
                Collection<Assertion> assertions =
                        extractBenchmarkMethods(
                                JimpleMethod.of(sootTestMethod), scanner.getCallGraph());
                IErrorListener errorListener = new UsagePatternErrorListener(assertions);
                IResultsListener resultsListener = new UsagePatternResultsListener(assertions);

                // Setup scanner
                scanner.addErrorListener(errorListener);
                scanner.addResultsListener(resultsListener);

                scanner.run();

                // Evaluate results
                List<Assertion> unsound = Lists.newLinkedList();
                List<Assertion> imprecise = Lists.newLinkedList();

                for (Assertion r : assertions) {
                    if (!r.isSatisfied()) {
                        unsound.add(r);
                    }
                }

                for (Assertion r : assertions) {
                    if (r.isImprecise()) {
                        imprecise.add(r);
                    }
                }

                StringBuilder errors = new StringBuilder();
                if (!unsound.isEmpty()) {
                    errors.append("\nUnsound results: \n").append(Joiner.on("\n").join(unsound));
                }
                if (!imprecise.isEmpty()) {
                    errors.append("\nImprecise results: \n")
                            .append(Joiner.on("\n").join(imprecise));
                }
                if (!errors.toString().isEmpty()) {
                    Assert.fail(errors.toString());
                }
            }
        };
    }

    protected abstract String getRulesetPath();

    @Override
    public List<String> getIncludeList() {
        return new ArrayList<>();
    }

    @Override
    public List<String> excludedPackages() {
        return new ArrayList<>();
    }

    private Set<Assertion> extractBenchmarkMethods(Method testMethod, CallGraph callGraph) {
        Set<Assertion> results = new HashSet<>();
        extractBenchmarkMethods(testMethod, callGraph, results, new HashSet<>());
        return results;
    }

    private void extractBenchmarkMethods(
            Method method, CallGraph callGraph, Set<Assertion> queries, Set<Method> visited) {
        if (visited.contains(method)) {
            return;
        }
        visited.add(method);

        for (CallGraph.Edge callSite : callGraph.edgesInto(method)) {
            Method callee = callSite.tgt();
            extractBenchmarkMethods(callee, callGraph, queries, visited);
        }

        for (Statement statement : method.getStatements()) {
            if (!statement.containsInvokeExpr()) {
                continue;
            }

            InvokeExpr invokeExpr = statement.getInvokeExpr();

            if (!invokeExpr
                    .getMethod()
                    .getDeclaringClass()
                    .toString()
                    .equals(Assertions.class.getName())) {
                continue;
            }

            String invocationName = invokeExpr.getMethod().getName();

            if (invocationName.startsWith("extValue")) {
                Val param = invokeExpr.getArg(0);
                if (!param.isIntConstant()) {
                    continue;
                }

                for (Statement pred : getPredecessorsNotBenchmark(statement)) {
                    queries.add(new ExtractedValueAssertion(pred, param.getIntValue()));
                }
            }

            if (invocationName.startsWith("callToForbiddenMethod")) {
                for (Statement pred : getPredecessorsNotBenchmark(statement)) {
                    queries.add(new CallToForbiddenMethodAssertion(pred));
                }
            }

            if (invocationName.startsWith("evaluatedConstraints")) {
                Val local = invokeExpr.getArg(0);
                Val count = invokeExpr.getArg(1);

                if (!local.isLocal() || !count.isIntConstant()) {
                    continue;
                }

                queries.add(new ConstraintsEvaluatedAssertion(local, count.getIntValue()));
            }

            if (invocationName.startsWith("satisfiedConstraints")) {
                Val local = invokeExpr.getArg(0);
                Val count = invokeExpr.getArg(1);

                if (!local.isLocal() || !count.isIntConstant()) {
                    continue;
                }

                queries.add(new ConstraintsSatisfiedAssertion(local, count.getIntValue()));
            }

            if (invocationName.startsWith("violatedConstraints")) {
                Val local = invokeExpr.getArg(0);
                Val count = invokeExpr.getArg(1);

                if (!local.isLocal() || !count.isIntConstant()) {
                    continue;
                }

                queries.add(new ConstraintsViolatedAssertion(local, count.getIntValue()));
            }

            if (invocationName.startsWith("notRelevantConstraints")) {
                Val local = invokeExpr.getArg(0);
                Val count = invokeExpr.getArg(1);

                if (!local.isLocal() || !count.isIntConstant()) {
                    continue;
                }

                queries.add(new ConstraintsNotRelevantAssertion(local, count.getIntValue()));
            }

            if (invocationName.startsWith("hasEnsuredPredicate")) {
                Val param = invokeExpr.getArg(0);
                if (!param.isLocal()) {
                    continue;
                }

                if (invokeExpr.getArgs().size() == 2) {
                    // predicate name is passed as parameter
                    Val predNameParam = invokeExpr.getArg(1);
                    if (!predNameParam.isStringConstant()) {
                        continue;
                    }
                    String predName = predNameParam.getStringValue();
                    queries.add(new HasEnsuredPredicateAssertion(statement, param, predName));

                } else {
                    queries.add(new HasEnsuredPredicateAssertion(statement, param));
                }
            }

            if (invocationName.startsWith("notHasEnsuredPredicate")) {
                Val param = invokeExpr.getArg(0);
                if (!param.isLocal()) {
                    continue;
                }

                if (invokeExpr.getArgs().size() == 2) {
                    // predicate name is passed as parameter
                    Val predNameParam = invokeExpr.getArg(1);
                    if (!predNameParam.isStringConstant()) {
                        continue;
                    }
                    String predName = predNameParam.getStringValue();
                    queries.add(new NotHasEnsuredPredicateAssertion(statement, param, predName));
                } else {
                    queries.add(new NotHasEnsuredPredicateAssertion(statement, param));
                }
            }

            if (invocationName.startsWith("mustBeInAcceptingState")) {
                Val param = invokeExpr.getArg(0);
                if (!param.isLocal()) {
                    continue;
                }

                queries.add(new MustBeInAcceptingStateAssertion(statement, param));
            }

            if (invocationName.startsWith("mayBeInAcceptingState")) {
                Val param = invokeExpr.getArg(0);
                if (!param.isLocal()) {
                    continue;
                }

                queries.add(new MayBeInAcceptingStateAssertion(statement, param));
            }

            if (invocationName.startsWith("mustNotBeInAcceptingState")) {
                Val param = invokeExpr.getArg(0);
                if (!param.isLocal()) {
                    continue;
                }

                queries.add(new MustNotBeInAcceptingStateAssertion(statement, param));
            }

            if (invocationName.startsWith("assertState")) {
                Val local = invokeExpr.getArg(0);
                Val stateLabel = invokeExpr.getArg(1);

                if (!local.isLocal()) {
                    continue;
                }

                if (!stateLabel.isStringConstant()) {
                    continue;
                }

                queries.add(new StateAssertion(statement, local, stateLabel.getStringValue()));
            }

            if (invocationName.startsWith("predicateErrors")) {
                Val param = invokeExpr.getArg(0);
                if (!param.isIntConstant()) {
                    continue;
                }
                queries.add(new PredicateErrorCountAssertion(param.getIntValue()));
            }

            if (invocationName.startsWith("predicateContradictionErrors")) {
                Val param = invokeExpr.getArg(0);
                if (!param.isIntConstant()) {
                    continue;
                }
                queries.add(new PredicateContradictionErrorCountAssertion(param.getIntValue()));
            }

            if (invocationName.startsWith("constraintErrors")) {
                Val seed = invokeExpr.getArg(0);
                Val param = invokeExpr.getArg(1);

                if (!seed.isLocal()) {
                    continue;
                }

                if (!param.isIntConstant()) {
                    continue;
                }
                queries.add(new ConstraintErrorCountAssertion(seed, param.getIntValue()));
            }

            if (invocationName.startsWith("typestateErrors")) {
                Val seed = invokeExpr.getArg(0);
                Val param = invokeExpr.getArg(1);

                if (!seed.isLocal()) {
                    continue;
                }

                if (!param.isIntConstant()) {
                    continue;
                }
                queries.add(new TypestateErrorCountAssertion(seed, param.getIntValue()));
            }

            if (invocationName.startsWith("incompleteOperationErrors")) {
                Val param = invokeExpr.getArg(0);
                if (!param.isIntConstant()) {
                    continue;
                }
                queries.add(new IncompleteOperationErrorCountAssertion(param.getIntValue()));
            }

            if (invocationName.startsWith("forbiddenMethodErrors")) {
                Val param = invokeExpr.getArg(0);
                if (!param.isIntConstant()) {
                    continue;
                }
                queries.add(new ForbiddenMethodErrorCountAssertion(param.getIntValue()));
            }

            if (invocationName.startsWith("impreciseValueExtractionErrors")) {
                Val param = invokeExpr.getArg(0);
                if (!param.isIntConstant()) {
                    continue;
                }
                queries.add(new ImpreciseValueExtractionErrorCountAssertion(param.getIntValue()));
            }
        }
    }

    private Set<Statement> getPredecessorsNotBenchmark(Statement stmt) {
        Set<Statement> res = Sets.newHashSet();
        Set<Statement> visited = Sets.newHashSet();
        LinkedList<Statement> workList = Lists.newLinkedList();
        workList.add(stmt);

        while (!workList.isEmpty()) {
            Statement curr = workList.poll();

            if (!visited.add(curr)) {
                continue;
            }

            if (curr.containsInvokeExpr()) {
                String invokedClassName =
                        curr.getInvokeExpr().getMethod().getDeclaringClass().getName();
                String assertionClassName = Assertions.class.getName();

                if (!invokedClassName.equals(assertionClassName)) {
                    res.add(curr);
                    continue;
                }
            }

            Collection<Statement> preds = stmt.getMethod().getControlFlowGraph().getPredsOf(curr);
            workList.addAll(preds);
        }
        return res;
    }
}
