package tests.crysl;

import crypto.cryslhandler.RulesetReader;
import crypto.rules.CrySLRule;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;

public class RulesetReaderTest {
	
    private static final String emptyZipFilePath = "src/test/resources/crySL/empty.zip";
    private static final String jcaRulesetZipFilePath = "src/test/resources/crySL/JavaCryptographicArchitecture-3.0.1-ruleset.zip";
    private static final String junkRuleSet = "src/test/resources/crySL/rulesetWithJunk.zip";

    @Test
    public void testJunkThrows() throws IOException {
        RulesetReader reader = new RulesetReader();
        Collection<CrySLRule> rules = reader.readRulesFromPath(junkRuleSet);

        Assert.assertEquals(48, rules.size());
    }
    
    @Test
    public void testNumberOfRules() throws IOException {
        RulesetReader reader = new RulesetReader();
        Collection<CrySLRule> rules = reader.readRulesFromPath(jcaRulesetZipFilePath);

        Assert.assertEquals(49, rules.size());
    }

    @Test
    public void testRulesZipFile() throws IOException {
        RulesetReader reader = new RulesetReader();
        Collection<CrySLRule> rules = reader.readRulesFromZipArchive(jcaRulesetZipFilePath);

        Assert.assertEquals(49, rules.size());
    }

    @Test(expected = IOException.class)
    public void testFileNotExists() throws IOException {
        RulesetReader reader = new RulesetReader();
        Collection<CrySLRule> rules = reader.readRulesFromPath("notExist");
        Assert.assertEquals(0, rules.size());
    }

    @Test
    public void testFileNoCrySLFiles() throws IOException{
        RulesetReader reader = new RulesetReader();
        Collection<CrySLRule> rules = reader.readRulesFromPath(emptyZipFilePath);

        Assert.assertEquals(0, rules.size());
    }

    @Test
    public void testRunTwiceSameResult() throws IOException {
        RulesetReader reader = new RulesetReader();
        Collection<CrySLRule> rules = reader.readRulesFromPath(jcaRulesetZipFilePath);
        Assert.assertEquals(49, rules.size());

        rules = reader.readRulesFromPath(jcaRulesetZipFilePath);
        Assert.assertEquals(49, rules.size());
    }

    @Test
    @Ignore
    public void TestPerformanceReducesSignificantlySecondTime() throws IOException {
        StopWatch watch = new StopWatch();
        watch.start();
        RulesetReader reader = new RulesetReader();
        reader.readRulesFromZipArchive(jcaRulesetZipFilePath);
        watch.stop();
        long firstRun = watch.getTime();
        watch.reset();
        watch.start();
        reader.readRulesFromZipArchive(jcaRulesetZipFilePath);
        watch.stop();
        long secondRun = watch.getTime();
        Assert.assertTrue(secondRun * 100 < firstRun);
        System.out.println("First: " + firstRun + "; Second: " + secondRun);
    }
}