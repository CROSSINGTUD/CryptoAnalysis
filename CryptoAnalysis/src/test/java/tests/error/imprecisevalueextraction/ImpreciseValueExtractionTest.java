package tests.error.imprecisevalueextraction;

import org.junit.Test;
import test.TestConstants;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class ImpreciseValueExtractionTest extends UsagePatternTestingFramework {

    @Override
    protected String getRulesetPath() {
        return TestConstants.RULES_TEST_DIR + "impreciseValueExtraction";
    }

    @Test
    public void testCouldExtractInteger() {
        ImpreciseValueExtraction extraction = new ImpreciseValueExtraction(10);
        Assertions.extValue(0);

        extraction.extractInteger(100);
        Assertions.extValue(0);

        Assertions.impreciseValueExtractionErrors(0);
    }

    @Test
    public void testCouldNotExtractInteger() {
        int randomValue = (int)(Math.random() * 20);

        // For both statements, the value cannot be extracted
        ImpreciseValueExtraction extraction = new ImpreciseValueExtraction(randomValue);
        extraction.extractInteger(randomValue);

        Assertions.impreciseValueExtractionErrors(2);
    }

    @Test
    public void testCouldExtractString() {
        ImpreciseValueExtraction extraction = new ImpreciseValueExtraction("Value");
        Assertions.extValue(0);

        extraction.extractString("Value");
        Assertions.extValue(0);

        Assertions.impreciseValueExtractionErrors(0);
    }

    @Test
    public void testCouldNotExtractString() {
        char[] charValue = new char[]{'v', 'a', 'l', 'u', 'e'};
        String stringValue = String.valueOf(charValue);

        // For both statements, the value cannot be extracted
        ImpreciseValueExtraction extraction = new ImpreciseValueExtraction(stringValue);
        extraction.extractString(stringValue);

        Assertions.impreciseValueExtractionErrors(2);
    }

    @Test
    @SuppressWarnings("unused")
    public void testCouldExtractConditionalValue() {
        ImpreciseValueExtraction extraction1 = new ImpreciseValueExtraction(100);
        Assertions.extValue(0);

        // Value can be extracted and condition satisfied => required call to 'missingCallTo'
        Assertions.impreciseValueExtractionErrors(0);
        Assertions.callToErrors(1);
    }

    @Test
    @SuppressWarnings("unused")
    public void testCouldNotExtractConditionalValue() {
        int randomValue = (int)(Math.random() * 20);
        ImpreciseValueExtraction extraction2 = new ImpreciseValueExtraction(randomValue);

        // Value cannot be extract => Condition cannot be evaluated and call to 'missingCallTo' not known
        Assertions.impreciseValueExtractionErrors(1);
        Assertions.callToErrors(0);
    }
}
