package tests.custom.incompleteoperation;

import org.junit.Test;
import test.TestConstants;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class IncompleteOperationTest extends UsagePatternTestingFramework {

    @Override
    protected String getRulesetPath() {
        return TestConstants.RULES_TEST_DIR + "incompleteOperation";
    }

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

}
