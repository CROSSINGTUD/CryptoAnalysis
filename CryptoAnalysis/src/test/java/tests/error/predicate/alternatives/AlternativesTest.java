package tests.error.predicate.alternatives;

import org.junit.Test;
import test.TestConstants;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class AlternativesTest extends UsagePatternTestingFramework {

    @Override
    protected String getRulesetPath() {
        return TestConstants.RULES_TEST_DIR + "alternatives";
    }

    @Test
    public void testPositiveSingleAlternative() {
        Source source1 = new Source();
        source1.ensurePredOnThis();
        Assertions.hasEnsuredPredicate(source1);

        Target target1 = new Target();
        target1.requireSource(source1);
        Assertions.hasGeneratedPredicate(target1);

        String string = "test";
        Source source2 = new Source();
        source2.ensurePredOnString(string);
        Assertions.hasGeneratedPredicate(source2);

        Target target2 = new Target();
        target2.requireString(string);
        Assertions.hasGeneratedPredicate(target2);

        Assertions.predicateErrors(0);
    }

    @Test
    public void testNegativeSingleAlternative() {
        Source source1 = new Source();
        Assertions.notHasEnsuredPredicate(source1);

        Target target1 = new Target();
        target1.requireSource(source1);
        Assertions.hasNotGeneratedPredicate(target1);

        String string = "test";
        Source source2 = new Source();
        Assertions.hasNotGeneratedPredicate(source2);

        Target target2 = new Target();
        target2.requireString(string);
        Assertions.hasNotGeneratedPredicate(target2);

        Assertions.predicateErrors(0);
    }

    @Test
    public void testAlternativeWithoutStatement() {
        Source source = new Source();
        Assertions.notHasEnsuredPredicate(source);

        Target target = new Target();
        target.requireSource(source);
        target.requireString("test");

        // Alternatives belong to the same predicate -> one error
        Assertions.predicateErrors(1);
    }

    @Test
    public void testAlternativeWithSingleStatement() {
        Source source1 = new Source();
        source1.ensurePredOnThis();
        Assertions.hasEnsuredPredicate(source1);

        Target target1 = new Target();
        target1.requireSource(source1);
        target1.requireString("test");

        String string = "test";
        Source source2 = new Source();
        source2.ensurePredOnString(string);
        Assertions.hasEnsuredPredicate(source2);

        Target target2 = new Target();
        target2.requireSource(source2);
        target2.requireString(string);

        Assertions.predicateErrors(0);
    }

    @Test
    public void testAlternativeWithMultipleStatements() {
        String string = "test";
        Source source = new Source();
        source.ensurePredOnThis();
        source.ensurePredOnString(string);
        Assertions.hasEnsuredPredicate(source);

        Target target = new Target();
        target.requireSource(source);
        target.requireString(string);

        Assertions.predicateErrors(0);
    }

    @Test
    public void testNegativeAlternativesAtSameStatement() {
        Source source = new Source();
        Assertions.notHasEnsuredPredicate(source);

        Target target = new Target();
        target.requireSourceAndString(source, "test");

        Assertions.predicateErrors(1);
    }

    @Test
    public void testPositiveAlternativesAtSameStatement() {
        Source source1 = new Source();
        source1.ensurePredOnThis();
        Assertions.hasEnsuredPredicate(source1);

        Target target1 = new Target();
        target1.requireSourceAndString(source1, "test");

        Assertions.predicateErrors(0);
    }
}
