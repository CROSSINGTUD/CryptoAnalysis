package tests.error.typestate;

import crypto.typestate.ErrorStateNode;
import crypto.typestate.ReportingErrorStateNode;
import org.junit.Test;
import test.TestConstants;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class TypestateTest extends UsagePatternTestingFramework {

    @Override
    protected String getRulesetPath() {
        return TestConstants.RULES_TEST_DIR + "typestate";
    }

    @Test
    public void correctLinearCallSequenceTest() {
        TypestateLinear typestate = new TypestateLinear();
        Assertions.assertState(typestate, "0");
        typestate.operation1();
        Assertions.assertState(typestate, "1");
        typestate.operation2();
        Assertions.assertState(typestate, "2");
        typestate.operation3();
        Assertions.assertState(typestate, "3");

        Assertions.mustBeInAcceptingState(typestate);
        Assertions.typestateErrors(typestate, 0);
    }

    @Test
    public void missingLinearCallTest() {
        TypestateLinear typestate1 = new TypestateLinear();
        Assertions.assertState(typestate1, "0");
        // typestate1.operation1();
        typestate1.operation2();
        Assertions.assertState(typestate1, ReportingErrorStateNode.LABEL);
        typestate1.operation3();
        Assertions.assertState(typestate1, ErrorStateNode.LABEL);

        Assertions.mustNotBeInAcceptingState(typestate1);
        Assertions.typestateErrors(typestate1, 1);

        TypestateLinear typestate2 = new TypestateLinear();
        Assertions.assertState(typestate2, "0");
        typestate2.operation1();
        Assertions.assertState(typestate2, "1");
        // typestate2.operation2();
        typestate2.operation3();
        Assertions.assertState(typestate2, ReportingErrorStateNode.LABEL);

        Assertions.mustNotBeInAcceptingState(typestate2);
        Assertions.typestateErrors(typestate2, 1);
    }

    @Test
    public void wrongInitialLinearCallTest() {
        TypestateLinear typestate = new TypestateLinear("notAllowed");
        Assertions.assertState(typestate, ReportingErrorStateNode.LABEL);
        typestate.operation1();
        Assertions.assertState(typestate, ErrorStateNode.LABEL);
        typestate.operation2();
        Assertions.assertState(typestate, ErrorStateNode.LABEL);
        typestate.operation3();
        Assertions.assertState(typestate, ErrorStateNode.LABEL);

        Assertions.mustNotBeInAcceptingState(typestate);
        Assertions.typestateErrors(typestate, 1);
    }

    @Test
    public void optionalCallSequenceTest() {
        TypestateOptional typestate1 = new TypestateOptional();
        Assertions.assertState(typestate1, "0");
        typestate1.operation1();
        Assertions.assertState(typestate1, "1");
        typestate1.operation2();
        Assertions.assertState(typestate1, "2");
        typestate1.operation3();
        Assertions.assertState(typestate1, "3");

        Assertions.mustBeInAcceptingState(typestate1);
        Assertions.typestateErrors(typestate1, 0);

        TypestateOptional typestate2 = new TypestateOptional();
        Assertions.assertState(typestate2, "0");
        typestate2.operation1();
        Assertions.assertState(typestate2, "1");
        typestate2.operation3();
        Assertions.assertState(typestate2, "3");

        Assertions.mustBeInAcceptingState(typestate2);
        Assertions.typestateErrors(typestate2, 0);
    }

    @Test
    public void branchingCallSequenceTest() {
        TypestateLinear typestate1 = new TypestateLinear();
        Assertions.assertState(typestate1, "0");
        typestate1.operation1();
        Assertions.assertState(typestate1, "1");

        if (Math.random() > 0.5) {
            typestate1.operation2();
            Assertions.assertState(typestate1, "2");
        }

        typestate1.operation3();
        // Dataflow without taking the branch leads to the error state
        Assertions.assertState(typestate1, ReportingErrorStateNode.LABEL);
        Assertions.assertState(typestate1, "3");

        Assertions.mayBeInAcceptingState(typestate1);
        Assertions.typestateErrors(typestate1, 1);

        TypestateOptional typestate2 = new TypestateOptional();
        Assertions.assertState(typestate2, "0");
        typestate2.operation1();
        Assertions.assertState(typestate2, "1");

        if (Math.random() > 0.5) {
            typestate2.operation2(); // No error because it is optional
            Assertions.assertState(typestate2, "2");
        }

        typestate2.operation3();
        Assertions.assertState(typestate2, "3");

        Assertions.mustBeInAcceptingState(typestate2);
        Assertions.typestateErrors(typestate2, 0);
    }

    @Test
    public void correctMultipleStaticInitializerTest() {
        TypestateStatic typestate1 = TypestateStatic.createTypestate();
        Assertions.assertState(typestate1, "0");
        typestate1.operation1();
        Assertions.assertState(typestate1, "1");

        Assertions.mustBeInAcceptingState(typestate1);
        Assertions.typestateErrors(typestate1, 0);

        TypestateStatic typestate2 = TypestateStatic.createTypestate("correct");
        Assertions.assertState(typestate2, "2");
        typestate2.operation2();
        Assertions.assertState(typestate2, "3");

        Assertions.mustBeInAcceptingState(typestate2);
        Assertions.typestateErrors(typestate2, 0);
    }

    @Test
    public void wrongMultipleStaticInitializerTest() {
        TypestateStatic typestate1 = TypestateStatic.createTypestate();
        Assertions.assertState(typestate1, "0");
        typestate1.operation2();
        Assertions.assertState(typestate1, ReportingErrorStateNode.LABEL);

        Assertions.mustNotBeInAcceptingState(typestate1);
        Assertions.typestateErrors(typestate1, 1);

        TypestateStatic typestate2 = TypestateStatic.createTypestate("incorrect");
        Assertions.assertState(typestate2, "2");
        typestate2.operation1();
        Assertions.assertState(typestate2, ReportingErrorStateNode.LABEL);

        Assertions.mustNotBeInAcceptingState(typestate2);
        Assertions.typestateErrors(typestate2, 1);

        // Using createTypestate(_, _) is wrong
        TypestateStatic typestate3 = TypestateStatic.createTypestate("unknown", -1);
        Assertions.assertState(typestate1, ReportingErrorStateNode.LABEL);
        Assertions.mustNotBeInAcceptingState(typestate3);
        Assertions.typestateErrors(typestate3, 1);
    }

    @Test
    public void linearAliasingTest() {
        TypestateLinear typestate = new TypestateLinear();
        Assertions.assertState(typestate, "0");
        @SuppressWarnings("redundent")
        TypestateLinear typestate2 = typestate;
        Assertions.assertState(typestate2, "0");

        typestate.operation1();
        Assertions.assertState(typestate, "1");
        Assertions.assertState(typestate2, "1");
        typestate2.operation2();
        Assertions.assertState(typestate, "2");
        Assertions.assertState(typestate2, "2");
        typestate.operation3();
        Assertions.assertState(typestate, "3");
        Assertions.assertState(typestate2, "3");

        Assertions.mustBeInAcceptingState(typestate);
        Assertions.mustBeInAcceptingState(typestate2);
        Assertions.typestateErrors(typestate, 0);
    }

    @Test
    public void linearBranchAliasingTest() {
        TypestateLinear typestate = new TypestateLinear();
        Assertions.assertState(typestate, "0");
        TypestateLinear typestate2 = new TypestateLinear();
        Assertions.assertState(typestate2, "0");

        if (Math.random() > 0.5) {
            typestate2 = typestate;
            Assertions.assertState(typestate2, "0");
        }

        typestate.operation1();
        Assertions.assertState(typestate, "1");
        typestate2.operation2(); // Alias only may exist
        Assertions.assertState(typestate2, "1");
        Assertions.assertState(typestate2, "2");
        typestate.operation3();
        Assertions.assertState(typestate, "3");
        Assertions.assertState(typestate, ReportingErrorStateNode.LABEL);
        Assertions.assertState(typestate2, "3");
        Assertions.assertState(typestate2, ReportingErrorStateNode.LABEL);

        Assertions.mayBeInAcceptingState(typestate);
        Assertions.mayBeInAcceptingState(typestate2);
        Assertions.typestateErrors(typestate, 1);
        Assertions.typestateErrors(typestate2, 2);
    }
}
