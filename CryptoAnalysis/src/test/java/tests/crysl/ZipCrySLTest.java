package tests.crysl;

import crypto.rules.CrySLRule;
import crypto.rules.CrySLRuleReader;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.stream.Collectors;

public class ZipCrySLTest
{
    private static final String emptyZipFilePath = "src\\test\\resources\\crySL\\empty.zip";
    private static final String jcaRulesetZipFilePath = "src\\test\\resources\\crySL\\JavaCryptographicArchitecture-1.4-ruleset.zip";
    private static final String multipleRulesetZipFilePath = "src\\test\\resources\\crySL\\Multiple-rulesets.zip";
   
    
    @Test
    public void TestNumberOfRules() {
        File zipFile = new File(jcaRulesetZipFilePath);
        Collection<CrySLRule> rules = CrySLRuleReader.readFromZipFile(zipFile);
        Assert.assertEquals(39, rules.size());
    }

    @Test
    public void TestRulesNotNull() {
        File zipFile = new File(jcaRulesetZipFilePath);
        Collection<CrySLRule> rules = CrySLRuleReader.readFromZipFile(zipFile);
        Collection<CrySLRule> notNullRules = rules.stream().filter(x -> x != null).collect(Collectors.toList());
        Assert.assertEquals(39, notNullRules.size());
    }

    @Test
    public void TestFileNotExists() {
        File zipFile = new File("notExist");
        Collection<CrySLRule> rules = CrySLRuleReader.readFromZipFile(zipFile);
        Assert.assertEquals(0, rules.size());
    }

    @Test
    public void TestFileNoCrypSLFiles() {
        File zipFile = new File(emptyZipFilePath);
        Collection<CrySLRule> rules = CrySLRuleReader.readFromZipFile(zipFile);
        Assert.assertEquals(0, rules.size());
    }
    
    @Test
    public void TestFileContainsMultipleRulesets() {
        File zipFile = new File(multipleRulesetZipFilePath);
        Collection<CrySLRule> rules = CrySLRuleReader.readFromZipFile(zipFile);
        Assert.assertEquals(97, rules.size());
    }

    @Test
    public void TestRunTwiceSameResult() {
        File zipFile = new File(jcaRulesetZipFilePath);
        Collection<CrySLRule> rules = CrySLRuleReader.readFromZipFile(zipFile);
        Assert.assertEquals(39, rules.size());
        rules = CrySLRuleReader.readFromZipFile(zipFile);
        Assert.assertEquals(39, rules.size());
    }

    @Test
    @Ignore
    public void TestPerformanceReducesSignificantlySecondTime() {
        File zipFile = new File(jcaRulesetZipFilePath);
        StopWatch watch = new StopWatch();
        watch.start();
        CrySLRuleReader.readFromZipFile(zipFile);
        watch.stop();
        long firstRun = watch.getTime();
        watch.reset();
        watch.start();
        CrySLRuleReader.readFromZipFile(zipFile);
        watch.stop();
        long secondRun = watch.getTime();
        Assert.assertTrue(secondRun * 100 < firstRun);
        System.out.println("First: " + firstRun + "; Second: " + secondRun);
    }
}