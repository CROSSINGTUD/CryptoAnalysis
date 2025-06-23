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

import java.lang.reflect.Method;
import java.util.Optional;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

public class TestRunnerInterceptor implements BeforeAllCallback, InvocationInterceptor {

    private final TestRunner testRunner;

    public TestRunnerInterceptor() {
        this.testRunner = new TestRunner();
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        Optional<Class<?>> testClass = context.getTestClass();
        if (testClass.isEmpty()) {
            throw new RuntimeException("Could not load test class");
        }

        Ruleset ruleset = testClass.get().getAnnotation(Ruleset.class);
        if (ruleset == null) {
            throw new RuntimeException(
                    "Test class "
                            + testClass.get().getName()
                            + " is not annotated with "
                            + Ruleset.class.getSimpleName());
        }

        testRunner.initialize(ruleset.value());
    }

    @Override
    public void interceptTestMethod(
            Invocation<Void> invocation,
            ReflectiveInvocationContext<Method> invocationContext,
            ExtensionContext extensionContext)
            throws Throwable {
        String testClassName = invocationContext.getExecutable().getDeclaringClass().getName();
        String testMethodName = invocationContext.getExecutable().getName();

        testRunner.runTest(testClassName, testMethodName);

        try {
            // We have to continue JUnit's lifecycle. However, since some test cases may throw
            // an exception (e.g. we use invalid algorithms as parameters), we have to catch
            // corresponding exceptions. We can ignore these exceptions because the actual test
            // has already been executed and has passed
            invocation.proceed();
        } catch (Throwable ignored) {
        }
    }
}
