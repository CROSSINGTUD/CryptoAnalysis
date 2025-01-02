package tests.error.constraints;

import org.junit.Test;
import test.TestConstants;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class ValueConstraintTest extends UsagePatternTestingFramework {

    @Override
    protected String getRulesetPath() {
        return TestConstants.RULES_TEST_DIR + "constraints";
    }

    @Test
    public void testAllConstraintsSatisfied() {
        // All 4 value constraints are satisfied
        ValueConstraint constraint = new ValueConstraint("AES");
        constraint.operation1(20);
        constraint.operation2("CES", 10);

        Assertions.evaluatedConstraints(constraint, 7);
        Assertions.satisfiedConstraints(constraint, 4);
        Assertions.violatedConstraints(constraint, 0);
        Assertions.notRelevantConstraints(constraint, 3);
        Assertions.constraintErrors(constraint, 0);

        Assertions.impreciseValueExtractionErrors(0);
    }

    @Test
    public void testSomeConstraintsSatisfied() {
        // 2 constraints are satisfied, 2 constraints are not relevant
        ValueConstraint constraint = new ValueConstraint("AES");
        constraint.operation1(10);

        Assertions.evaluatedConstraints(constraint, 7);
        Assertions.satisfiedConstraints(constraint, 2);
        Assertions.violatedConstraints(constraint, 0);
        Assertions.notRelevantConstraints(constraint, 5);
        Assertions.constraintErrors(constraint, 0);

        Assertions.impreciseValueExtractionErrors(0);
    }

    @Test
    public void testAllConstraintsViolated() {
        // All 4 value constraints are violated
        ValueConstraint constraint = new ValueConstraint("DES");
        constraint.operation1(200);
        constraint.operation2("EES", 100);

        Assertions.evaluatedConstraints(constraint, 7);
        Assertions.satisfiedConstraints(constraint, 0);
        Assertions.violatedConstraints(constraint, 4);
        Assertions.notRelevantConstraints(constraint, 3);
        Assertions.constraintErrors(constraint, 4);

        Assertions.impreciseValueExtractionErrors(0);
    }

    @Test
    public void testSomeConstraintsViolated() {
        // 2 constraints are violated, 2 constraints are not violated
        ValueConstraint constraint = new ValueConstraint("DES");
        constraint.operation1(100);

        Assertions.evaluatedConstraints(constraint, 7);
        Assertions.satisfiedConstraints(constraint, 0);
        Assertions.violatedConstraints(constraint, 2);
        Assertions.notRelevantConstraints(constraint, 5);
        Assertions.constraintErrors(constraint, 2);

        Assertions.impreciseValueExtractionErrors(0);
    }

    @Test
    public void testCombineAllResults() {
        // 1 constraint is satisfied, 1 is ignored, 2 are violated
        ValueConstraint constraint = new ValueConstraint("DES");
        constraint.operation2("CES", 100);

        Assertions.evaluatedConstraints(constraint, 7);
        Assertions.satisfiedConstraints(constraint, 1);
        Assertions.violatedConstraints(constraint, 2);
        Assertions.notRelevantConstraints(constraint, 4);
        Assertions.constraintErrors(constraint, 2);

        Assertions.impreciseValueExtractionErrors(0);
    }

    @Test
    public void testUnknownValue() {
        ValueConstraint constraint = new ValueConstraint("DES");

        // Unknown values are treated as violated constraints
        int i = (int) (Math.random() * 10);
        constraint.operation2("CES", i);

        Assertions.evaluatedConstraints(constraint, 7);
        Assertions.satisfiedConstraints(constraint, 1);
        Assertions.violatedConstraints(constraint, 2);
        Assertions.notRelevantConstraints(constraint, 4);
        Assertions.constraintErrors(constraint, 1);

        Assertions.impreciseValueExtractionErrors(1);
    }

    @Test
    public void testTransformationSatisfied() {
        ValueConstraint constraint = new ValueConstraint("AES");
        constraint.operation3("AES/CBC/X");

        Assertions.evaluatedConstraints(constraint, 7);
        Assertions.satisfiedConstraints(constraint, 4);
        Assertions.violatedConstraints(constraint, 0);
        Assertions.notRelevantConstraints(constraint, 3);
        Assertions.constraintErrors(constraint, 0);

        Assertions.impreciseValueExtractionErrors(0);
    }

    @Test
    public void testTransformationPartiallyViolated() {
        ValueConstraint algConstraint = new ValueConstraint("AES");
        algConstraint.operation3("DES/CBC/X");

        Assertions.evaluatedConstraints(algConstraint, 7);
        Assertions.satisfiedConstraints(algConstraint, 3);
        Assertions.violatedConstraints(algConstraint, 1);
        Assertions.notRelevantConstraints(algConstraint, 3);
        Assertions.constraintErrors(algConstraint, 1);

        ValueConstraint modeConstraint = new ValueConstraint("AES");
        modeConstraint.operation3("AES/DBD/X");

        Assertions.evaluatedConstraints(modeConstraint, 7);
        Assertions.satisfiedConstraints(modeConstraint, 3);
        Assertions.violatedConstraints(modeConstraint, 1);
        Assertions.notRelevantConstraints(modeConstraint, 3);
        Assertions.constraintErrors(modeConstraint, 1);

        ValueConstraint padConstraint = new ValueConstraint("AES");
        padConstraint.operation3("AES/CBC/W");

        Assertions.evaluatedConstraints(padConstraint, 7);
        Assertions.satisfiedConstraints(padConstraint, 3);
        Assertions.violatedConstraints(padConstraint, 1);
        Assertions.notRelevantConstraints(padConstraint, 3);
        Assertions.constraintErrors(padConstraint, 1);

        Assertions.impreciseValueExtractionErrors(0);
    }
}
