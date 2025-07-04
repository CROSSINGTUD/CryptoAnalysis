/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package test;

import boomerang.scope.CallGraph;
import boomerang.scope.DataFlowScope;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import com.google.common.base.Joiner;
import crypto.analysis.CryptoScanner;
import crypto.listener.IErrorListener;
import crypto.listener.IResultsListener;
import crysl.rule.CrySLRule;
import de.fraunhofer.iem.cryptoanalysis.scope.CryptoAnalysisScope;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.assertions.Assertion;
import test.assertions.Assertions;
import test.assertions.CallToForbiddenMethodAssertion;
import test.assertions.ConstraintErrorCountAssertion;
import test.assertions.ConstraintsEvaluatedAssertion;
import test.assertions.ConstraintsNotRelevantAssertion;
import test.assertions.ConstraintsSatisfiedAssertion;
import test.assertions.ConstraintsViolatedAssertion;
import test.assertions.DiscoveredPredicateSeedAssertion;
import test.assertions.DiscoveredRuleSeedAssertion;
import test.assertions.DiscoveredSeedAssertion;
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
import test.framework.OpalTestSetup;
import test.framework.SootTestSetup;
import test.framework.SootUpTestSetup;
import test.framework.TestSetup;

public class TestRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestRunner.class);

    private static final String SOOT = "soot";
    private static final String SOOT_UP = "sootup";
    private static final String OPAL = "opal";

    /** Use this variable to configure the framework when running the tests locally */
    private static final String LOCAL_TEST_FRAMEWORK = SOOT;

    private final TestSetup testSetup;
    private final CryptoScanner scanner;
    private Collection<CrySLRule> rules;

    public TestRunner() {
        this.testSetup = createTestSetup();
        this.scanner = new CryptoScanner();
    }

    private TestSetup createTestSetup() {
        String framework = System.getProperty("testSetup", LOCAL_TEST_FRAMEWORK);

        switch (framework.toLowerCase()) {
            case SOOT -> {
                return new SootTestSetup();
            }
            case SOOT_UP -> {
                return new SootUpTestSetup();
            }
            case OPAL -> {
                return new OpalTestSetup();
            }
            default ->
                    throw new IllegalArgumentException(
                            "Cannot run tests with test setup " + framework);
        }
    }

    public void initialize(String ruleset) {
        String path;
        if (Set.of(TestRules.BOUNCY_CASTLE, TestRules.JCA, TestRules.TINK).contains(ruleset)) {
            path = TestRules.RULES_BASE_DIR + ruleset;
        } else {
            path = TestRules.RULES_TEST_DIR + ruleset;
        }

        rules = scanner.readRules(path);
        LOGGER.info("Using rules from " + path);
    }

    public void runTest(String testClassName, String testMethodName) {
        LOGGER.info("Running test '{}' in class '{}'", testMethodName, testClassName);

        testSetup.initialize(buildClassPath(), testClassName, testMethodName);

        DataFlowScope dataFlowScope = new TestDataFlowScope(rules);
        CryptoAnalysisScope frameworkScope = testSetup.createFrameworkScope(dataFlowScope);
        Method testMethod = testSetup.getTestMethod();

        // Setup test listener
        Collection<Assertion> assertions =
                extractBenchmarkMethods(
                        testMethod, frameworkScope.asFrameworkScope().getCallGraph());
        IErrorListener errorListener = new TestRunnerErrorListener(assertions);
        IResultsListener resultsListener = new TestRunnerResultsListener(assertions);

        scanner.addErrorListener(errorListener);
        scanner.addResultsListener(resultsListener);

        scanner.scan(frameworkScope, rules);
        scanner.reset();

        // Evaluate results
        Collection<Assertion> unsound = new ArrayList<>();
        Collection<Assertion> imprecise = new ArrayList<>();

        for (Assertion r : assertions) {
            if (r.isUnsound()) {
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
            errors.append("\nUnsound results: \n")
                    .append(
                            Joiner.on("\n")
                                    .join(
                                            unsound.stream()
                                                    .map(Assertion::getErrorMessage)
                                                    .toList()));
        }
        if (!imprecise.isEmpty()) {
            errors.append("\nImprecise results: \n")
                    .append(
                            Joiner.on("\n")
                                    .join(
                                            imprecise.stream()
                                                    .map(Assertion::getErrorMessage)
                                                    .toList()));
        }
        if (!errors.toString().isEmpty()) {
            org.junit.jupiter.api.Assertions.fail(errors.toString());
        }
    }

    protected String buildClassPath() {
        String userDir = System.getProperty("user.dir");
        String javaHome = System.getProperty("java.home");
        if (javaHome == null || javaHome.isEmpty()) {
            throw new RuntimeException("Could not get property java.home!");
        }

        return userDir + "/target/test-classes";
    }

    private Collection<Assertion> extractBenchmarkMethods(Method testMethod, CallGraph callGraph) {
        Collection<Assertion> results = new HashSet<>();
        extractBenchmarkMethods(testMethod, callGraph, results, new HashSet<>());
        return results;
    }

    private void extractBenchmarkMethods(
            Method method,
            CallGraph callGraph,
            Collection<Assertion> queries,
            Collection<Method> visited) {
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
                    .getDeclaredMethod()
                    .getDeclaringClass()
                    .getFullyQualifiedName()
                    .equals(Assertions.class.getName())) {
                continue;
            }

            String invocationName = invokeExpr.getDeclaredMethod().getName();

            if (invocationName.startsWith("extValue")) {
                Val param = invokeExpr.getArg(0);
                int countVal = getIntValueFromAssertion(method, param);

                for (Statement pred : getPredecessorsNotBenchmark(statement)) {
                    queries.add(new ExtractedValueAssertion(pred, countVal));
                }
            }

            if (invocationName.startsWith("discoveredSeeds")) {
                Val count = invokeExpr.getArg(0);

                int countVal = getIntValueFromAssertion(method, count);
                queries.add(new DiscoveredSeedAssertion(countVal));
            }

            if (invocationName.startsWith("discoveredRuleSeeds")) {
                Val count = invokeExpr.getArg(0);

                int countVal = getIntValueFromAssertion(method, count);
                queries.add(new DiscoveredRuleSeedAssertion(countVal));
            }

            if (invocationName.startsWith("discoveredPredicateSeeds")) {
                Val count = invokeExpr.getArg(0);

                int countVal = getIntValueFromAssertion(method, count);
                queries.add(new DiscoveredPredicateSeedAssertion(countVal));
            }

            if (invocationName.startsWith("callToForbiddenMethod")) {
                for (Statement pred : getPredecessorsNotBenchmark(statement)) {
                    queries.add(new CallToForbiddenMethodAssertion(pred));
                }
            }

            if (invocationName.startsWith("evaluatedConstraints")) {
                Val local = invokeExpr.getArg(0);
                Val count = invokeExpr.getArg(1);

                if (!local.isLocal()) {
                    throw new RuntimeException("Cannot create assertion @ " + statement);
                }

                int countVal = getIntValueFromAssertion(method, count);
                queries.add(new ConstraintsEvaluatedAssertion(local, countVal));
            }

            if (invocationName.startsWith("satisfiedConstraints")) {
                Val local = invokeExpr.getArg(0);
                Val count = invokeExpr.getArg(1);

                if (!local.isLocal()) {
                    throw new RuntimeException("Cannot create assertion @ " + statement);
                }

                int countVal = getIntValueFromAssertion(method, count);
                queries.add(new ConstraintsSatisfiedAssertion(local, countVal));
            }

            if (invocationName.startsWith("violatedConstraints")) {
                Val local = invokeExpr.getArg(0);
                Val count = invokeExpr.getArg(1);

                if (!local.isLocal()) {
                    throw new RuntimeException("Cannot create assertion @ " + statement);
                }

                int countVal = getIntValueFromAssertion(method, count);
                queries.add(new ConstraintsViolatedAssertion(local, countVal));
            }

            if (invocationName.startsWith("notRelevantConstraints")) {
                Val local = invokeExpr.getArg(0);
                Val count = invokeExpr.getArg(1);

                if (!local.isLocal()) {
                    throw new RuntimeException("Cannot create assertion @ " + statement);
                }

                int countVal = getIntValueFromAssertion(method, count);
                queries.add(new ConstraintsNotRelevantAssertion(local, countVal));
            }

            if (invocationName.startsWith("hasEnsuredPredicate")) {
                Val param = invokeExpr.getArg(0);
                if (!param.isLocal()) {
                    throw new RuntimeException("Cannot create assertion @ " + statement);
                }

                if (invokeExpr.getArgs().size() == 2) {
                    // predicate name is passed as parameter
                    Val predNameParam = invokeExpr.getArg(1);
                    String predName = getStringValueFromAssertion(method, predNameParam);

                    queries.add(new HasEnsuredPredicateAssertion(statement, param, predName));
                } else {
                    queries.add(new HasEnsuredPredicateAssertion(statement, param));
                }
            }

            if (invocationName.startsWith("notHasEnsuredPredicate")) {
                Val param = invokeExpr.getArg(0);
                if (!param.isLocal()) {
                    throw new RuntimeException("Cannot create assertion @ " + statement);
                }

                if (invokeExpr.getArgs().size() == 2) {
                    // predicate name is passed as parameter
                    Val predNameParam = invokeExpr.getArg(1);
                    String predName = getStringValueFromAssertion(method, predNameParam);

                    queries.add(new NotHasEnsuredPredicateAssertion(statement, param, predName));
                } else {
                    queries.add(new NotHasEnsuredPredicateAssertion(statement, param));
                }
            }

            if (invocationName.startsWith("mustBeInAcceptingState")) {
                Val param = invokeExpr.getArg(0);
                if (!param.isLocal()) {
                    throw new RuntimeException("Cannot create assertion @ " + statement);
                }

                queries.add(new MustBeInAcceptingStateAssertion(statement, param));
            }

            if (invocationName.startsWith("mayBeInAcceptingState")) {
                Val param = invokeExpr.getArg(0);
                if (!param.isLocal()) {
                    throw new RuntimeException("Cannot create assertion @ " + statement);
                }

                queries.add(new MayBeInAcceptingStateAssertion(statement, param));
            }

            if (invocationName.startsWith("mustNotBeInAcceptingState")) {
                Val param = invokeExpr.getArg(0);
                if (!param.isLocal()) {
                    throw new RuntimeException("Cannot create assertion @ " + statement);
                }

                queries.add(new MustNotBeInAcceptingStateAssertion(statement, param));
            }

            if (invocationName.startsWith("assertState")) {
                Val local = invokeExpr.getArg(0);
                Val stateLabel = invokeExpr.getArg(1);

                if (!local.isLocal()) {
                    throw new RuntimeException("Cannot create assertion @ " + statement);
                }

                String state = getStringValueFromAssertion(method, stateLabel);
                queries.add(new StateAssertion(statement, local, state));
            }

            if (invocationName.startsWith("predicateErrors")) {
                Val param = invokeExpr.getArg(0);
                int intVal = getIntValueFromAssertion(method, param);

                queries.add(new PredicateErrorCountAssertion(intVal));
            }

            if (invocationName.startsWith("predicateContradictionErrors")) {
                Val param = invokeExpr.getArg(0);
                int intVal = getIntValueFromAssertion(method, param);

                queries.add(new PredicateContradictionErrorCountAssertion(intVal));
            }

            if (invocationName.startsWith("constraintErrors")) {
                Val seed = invokeExpr.getArg(0);
                Val param = invokeExpr.getArg(1);

                if (!seed.isLocal()) {
                    throw new RuntimeException("Cannot create assertion @ " + statement);
                }

                int intVal = getIntValueFromAssertion(method, param);
                queries.add(new ConstraintErrorCountAssertion(seed, intVal));
            }

            if (invocationName.startsWith("typestateErrors")) {
                Val seed = invokeExpr.getArg(0);
                Val param = invokeExpr.getArg(1);

                if (!seed.isLocal()) {
                    throw new RuntimeException("Cannot create assertion @ " + statement);
                }

                int intVal = getIntValueFromAssertion(method, param);
                queries.add(new TypestateErrorCountAssertion(seed, intVal));
            }

            if (invocationName.startsWith("incompleteOperationErrors")) {
                Val param = invokeExpr.getArg(0);
                int intVal = getIntValueFromAssertion(method, param);

                queries.add(new IncompleteOperationErrorCountAssertion(intVal));
            }

            if (invocationName.startsWith("forbiddenMethodErrors")) {
                Val param = invokeExpr.getArg(0);
                int intVal = getIntValueFromAssertion(method, param);

                queries.add(new ForbiddenMethodErrorCountAssertion(intVal));
            }

            if (invocationName.startsWith("impreciseValueExtractionErrors")) {
                Val param = invokeExpr.getArg(0);
                int intVal = getIntValueFromAssertion(method, param);

                queries.add(new ImpreciseValueExtractionErrorCountAssertion(intVal));
            }
        }
    }

    private Collection<Statement> getPredecessorsNotBenchmark(Statement stmt) {
        Collection<Statement> res = new HashSet<>();
        Collection<Statement> visited = new HashSet<>();
        LinkedList<Statement> workList = new LinkedList<>();
        workList.add(stmt);

        while (!workList.isEmpty()) {
            Statement curr = workList.poll();

            if (!visited.add(curr)) {
                continue;
            }

            if (curr.containsInvokeExpr()) {
                String invokedClassName =
                        curr.getInvokeExpr()
                                .getDeclaredMethod()
                                .getDeclaringClass()
                                .getFullyQualifiedName();
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

    private int getIntValueFromAssertion(Method method, Val param) {
        if (param.isIntConstant()) {
            return param.getIntValue();
        }

        for (Statement statement : method.getStatements()) {
            if (statement.isAssignStmt()) {
                Val leftOp = statement.getLeftOp();
                Val rightOp = statement.getRightOp();

                if (leftOp.equals(param)) {
                    if (rightOp.isIntConstant()) {
                        return rightOp.getIntValue();
                    }
                }
            }
        }

        throw new RuntimeException("Unable to find int value for param " + param);
    }

    private String getStringValueFromAssertion(Method method, Val param) {
        if (param.isStringConstant()) {
            return param.getStringValue();
        }

        for (Statement statement : method.getStatements()) {
            if (statement.isAssignStmt()) {
                Val leftOp = statement.getLeftOp();
                Val rightOp = statement.getRightOp();

                if (leftOp.equals(param)) {
                    if (rightOp.isStringConstant()) {
                        return rightOp.getStringValue();
                    }
                }
            }
        }

        throw new RuntimeException("Unable to find String value for param " + param);
    }
}
