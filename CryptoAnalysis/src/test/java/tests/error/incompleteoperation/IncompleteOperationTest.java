/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package tests.error.incompleteoperation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.Ruleset;
import test.TestRules;
import test.TestRunnerInterceptor;
import test.assertions.Assertions;

@ExtendWith(TestRunnerInterceptor.class)
@Ruleset(TestRules.INCOMPLETE_OPERATION)
public class IncompleteOperationTest {

    @Test
    public void testNoIncompleteOperation() {
        Operations operations1 = new Operations();
        operations1.operation1();
        operations1.operation2();
        operations1.operation3();
        operations1.operation4();

        Operations operations2 = new Operations();
        operations2.operation1();
        operations2.operation2();
        operations2.operation3();
        // operation 4 is optional, i.e. no incomplete operation

        Assertions.incompleteOperationErrors(0);
    }

    @Test
    public void testMissingOperation() {
        Operations operations = new Operations();
        operations.operation1();
        operations.operation2();
        // operation3 is missing

        Assertions.incompleteOperationErrors(1);
    }

    @Test
    public void testSingleDataflowPathWithIncompleteOperation() {
        Operations operations = new Operations();
        operations.operation1();
        operations.operation2();

        // Dataflow path without operation3 exists => incomplete operation
        if (Math.random() > 0.5) {
            operations.operation3();
        }

        Assertions.incompleteOperationErrors(1);
    }

    @Test
    public void testMultipleDataflowPathsWithIncompleteOperations() {
        Operations operations = new Operations();
        operations.operation1();

        // On both dataflow paths a call two operation3 is missing => 2 incomplete operations
        if (Math.random() > 0.5) {
            operations.operation2();
        } else {
            operations.operation2();
        }

        Assertions.incompleteOperationErrors(2);
    }

    @Test
    public void testMultipleDataflowPathsWithoutIncompleteOperations() {
        Operations operations = new Operations();
        operations.operation1();

        // Both dataflow path are completed with operation3
        if (Math.random() > 0.5) {
            operations.operation2();
        } else {
            operations.operation2();
        }
        operations.operation3();

        Assertions.incompleteOperationErrors(0);
    }

    @Test
    public void testIncompleteOperationWithLoops() {
        Operations operations1 = new Operations();
        operations1.operation1();
        operations1.operation2();

        // Dataflow path without call to operation3
        while (Math.random() > 0.5) {
            operations1.operation3();
        }

        Operations operations2 = new Operations();
        operations2.operation1();
        operations2.operation2();

        // Dataflow path without call to operation3
        for (int i = 0; i < 1; i++) {
            operations2.operation3();
        }

        Assertions.incompleteOperationErrors(2);
    }

    @Test
    public void testOverwriteSeedConstructor() {
        OverwriteOperation operation = new OverwriteOperation();
        operation.operation1();
        Assertions.assertState(operation, "1");

        // Overwrite the seed and continue with constructor call
        operation = new OverwriteOperation();
        Assertions.assertState(operation, "0");

        Assertions.discoveredSeeds(2);
        Assertions.incompleteOperationErrors(2);
    }

    @Test
    public void testOverwriteSeedStatic() {
        OverwriteOperation operation = OverwriteOperation.getInstance();
        operation.operation1();
        Assertions.assertState(operation, "1");

        // Overwrite the seed with factory method
        operation = OverwriteOperation.getInstance();
        Assertions.assertState(operation, "0");

        Assertions.discoveredSeeds(2);
        Assertions.incompleteOperationErrors(2);
    }
}
