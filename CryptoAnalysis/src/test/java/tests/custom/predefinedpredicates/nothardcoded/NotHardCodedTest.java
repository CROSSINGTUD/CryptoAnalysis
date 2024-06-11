package tests.custom.predefinedpredicates.nothardcoded;

import crypto.analysis.CrySLRulesetSelector;
import org.junit.Test;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

import java.math.BigInteger;
import java.util.Random;
import java.util.UUID;

public class NotHardCodedTest extends UsagePatternTestingFramework {

    @Override
    protected CrySLRulesetSelector.Ruleset getRuleSet() {
        return CrySLRulesetSelector.Ruleset.CustomRules;
    }

    @Override
    protected String getRulesetPath() {
        return "predefinedPredicates";
    }

    @Test
    public void positivePredicateWithIntValueTest() {
        NotHardCoded notHardCoded = new NotHardCoded();

        // Parameter is random
        int value = (int) (Math.random() * 10);
        notHardCoded.operation(value);

        Assertions.notHardCodedErrors(0);
    }

    @Test
    public void negativePredicateWithIntValueTest() {
        NotHardCoded notHardCoded = new NotHardCoded();

        // Parameter is hard coded
        notHardCoded.operation(12345);

        Assertions.notHardCodedErrors(1);
    }

    @Test
    public void positivePredicateWithStringValueTest() {
        NotHardCoded notHardCoded = new NotHardCoded();

        String value = UUID.randomUUID().toString();
        notHardCoded.operation(value);

        Assertions.notHardCodedErrors(0);
    }

    @Test
    public void negativePredicateWithStringValueTest() {
        NotHardCoded notHardCoded = new NotHardCoded();

        String value = "This is hard coded";
        notHardCoded.operation(value);

        Assertions.notHardCodedErrors(1);
    }

    @Test
    public void predicateWithInstanceTest() {
        NotHardCoded notHardCoded = new NotHardCoded();

        // BigInteger's value is random, the instance is hard coded
        BigInteger bigInteger = new BigInteger(8, new Random());
        notHardCoded.operation(bigInteger);

        Assertions.notHardCodedErrors(1);
    }

    @Test
    public void positivePredicateWithArrayTest() {
        NotHardCoded notHardCoded = new NotHardCoded();

        int[] array = new int[3];
        array[0] = (int) (Math.random() * 10);
        array[1] = (int) (Math.random() * 10);
        array[2] = (int) (Math.random() * 10);

        notHardCoded.operation(array);

        Assertions.notHardCodedErrors(0);
    }

    @Test
    public void negativePredicateWithArrayTest() {
        NotHardCoded notHardCoded = new NotHardCoded();

        char[] array = new char[]{'p', 'a', 's', 's', 'w', 'o', 'r', 'd'};
        notHardCoded.operation(array);

        Assertions.notHardCodedErrors(1);
    }
}
