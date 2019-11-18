package tests.crysl;

import crypto.rules.CryptSLRule;
import crypto.rules.CryptSLRuleReader;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.stream.Collectors;

public class ZipCrySLTest
{
    private static final String ZipFilePath = "src\\test\\resources\\crySL\\CrySL_JCA.zip";

    @Test
    public void TestNumberOfRules() {
        File zipFile = new File(ZipFilePath);
        Collection<CryptSLRule> rules = CryptSLRuleReader.readFromZipFile(zipFile);
        Assert.assertEquals(39, rules.size());
    }

    @Test
    public void TestRulesNotNull() {
        File zipFile = new File(ZipFilePath);
        Collection<CryptSLRule> rules = CryptSLRuleReader.readFromZipFile(zipFile);
        Collection<CryptSLRule> notNullRules = rules.stream().filter(x -> x != null).collect(Collectors.toList());
        Assert.assertEquals(39, notNullRules.size());
    }

    @Test
    public void TestFileNotExists() {
        File zipFile = new File("notExist");
        Collection<CryptSLRule> rules = CryptSLRuleReader.readFromZipFile(zipFile);
        Assert.assertEquals(0, rules.size());
    }

    @Test
    public void TestFileNoCrypSLFiles() {
        File zipFile = new File("src\\test\\resources\\crySL\\empty.zip");
        Collection<CryptSLRule> rules = CryptSLRuleReader.readFromZipFile(zipFile);
        Assert.assertEquals(0, rules.size());
    }

    @Test
    public void TestRunTwiceSameResult() {
        File zipFile = new File(ZipFilePath);
        Collection<CryptSLRule> rules = CryptSLRuleReader.readFromZipFile(zipFile);
        Assert.assertEquals(39, rules.size());
        rules = CryptSLRuleReader.readFromZipFile(zipFile);
        Assert.assertEquals(39, rules.size());
    }

    @Test
    @Ignore
    public void TestPerformanceReducesSignificantlySecondTime() {
        File zipFile = new File(ZipFilePath);
        StopWatch watch = new StopWatch();
        watch.start();
        CryptSLRuleReader.readFromZipFile(zipFile);
        watch.stop();
        long firstRun = watch.getTime();
        watch.reset();
        watch.start();
        CryptSLRuleReader.readFromZipFile(zipFile);
        watch.stop();
        long secondRun = watch.getTime();
        Assert.assertTrue(secondRun * 100 < firstRun);
        System.out.println("First: " + firstRun + "; Second: " + secondRun);
    }
}
