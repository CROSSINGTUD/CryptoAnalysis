package tests.crysl;

import java.io.File;
import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import crypto.exceptions.CryptoAnalysisException;
import crypto.rules.CrySLRule;
import crypto.rules.CrySLRuleReader;

public class ZipCrySLTest
{
    private static final String emptyZipFilePath = "src/test/resources/crySL/empty.zip";
    private static final String jcaRulesetZipFilePath = "src/test/resources/crySL/JavaCryptographicArchitecture-1.5.1-ruleset.zip";
    private static final String multipleRulesetZipFilePath = "src/test/resources/crySL/Multiple-rulesets.zip";
    private static final String junkRuleSet = "src/test/resources/crySL/rulesetWithJunk.zip";


    @Test(expected = CryptoAnalysisException.class)
    public void TestJunkThrows() throws CryptoAnalysisException {
        File zipFile = new File(junkRuleSet);
        Collection<CrySLRule> rules = CrySLRuleReader.readFromZipFile(zipFile);
    }
    
    @Test
    public void TestNumberOfRules() throws CryptoAnalysisException {
        File zipFile = new File(jcaRulesetZipFilePath);
        Collection<CrySLRule> rules = null;
		rules = CrySLRuleReader.readFromZipFile(zipFile);
        Assert.assertEquals(46, rules.size());
    }

    @Test
    public void TestRulesNotNull() throws CryptoAnalysisException {
        File zipFile = new File(jcaRulesetZipFilePath);
        Collection<CrySLRule> rules = CrySLRuleReader.readFromZipFile(zipFile);
        Collection<CrySLRule> notNullRules = rules.stream().filter(x -> x != null).collect(Collectors.toList());
        Assert.assertEquals(46, notNullRules.size());
    }

    @Test(expected = CryptoAnalysisException.class)
    public void TestFileNotExists() throws CryptoAnalysisException {
        File zipFile = new File("notExist");
        Collection<CrySLRule> rules = CrySLRuleReader.readFromZipFile(zipFile);
        Assert.assertEquals(0, rules.size());
    }

    @Test
    public void TestFileNoCrypSLFiles() throws CryptoAnalysisException {
        File zipFile = new File(emptyZipFilePath);
        Collection<CrySLRule> rules = CrySLRuleReader.readFromZipFile(zipFile);
        Assert.assertEquals(0, rules.size());
    }
    
    @Test
    public void TestFileContainsMultipleRulesets() throws CryptoAnalysisException {
        File zipFile = new File(multipleRulesetZipFilePath);
        Collection<CrySLRule> rules = CrySLRuleReader.readFromZipFile(zipFile);
        Assert.assertEquals(102, rules.size());
    }

    @Test
    public void TestRunTwiceSameResult() throws CryptoAnalysisException {
        File zipFile = new File(jcaRulesetZipFilePath);
        Collection<CrySLRule> rules = CrySLRuleReader.readFromZipFile(zipFile);
        Assert.assertEquals(46, rules.size());
        rules = CrySLRuleReader.readFromZipFile(zipFile);
        Assert.assertEquals(46, rules.size());
    }

    @Test
    @Ignore
    public void TestPerformanceReducesSignificantlySecondTime() throws CryptoAnalysisException {
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