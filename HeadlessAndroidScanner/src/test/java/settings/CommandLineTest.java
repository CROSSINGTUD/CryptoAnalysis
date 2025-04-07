/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package settings;

import crypto.exceptions.CryptoAnalysisParserException;
import crypto.reporting.Reporter;
import de.fraunhofer.iem.android.HeadlessAndroidScanner;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

public class CommandLineTest {

    private static final String APK_PATH = "--apkFile";
    private static final String EXAMPLE_APK_PATH = "path/to/apk";

    private static final String PLATFORM_PATH = "--platformDirectory";
    private static final String EXAMPLE_PLATFORM_PATH = "path/to/platform";

    private static final String RULES_DIR = "--rulesDir";
    private static final String EXAMPLE_RULES_DIR = "path/to/rules";

    private static final String REPORT_PATH = "--reportPath";
    private static final String REPORT_FORMAT = "--reportFormat";

    private static final String VISUALIZATION = "--visualization";

    @Test
    public void testMinimalApplication() {
        String[] args =
                new String[] {
                    APK_PATH,
                    EXAMPLE_APK_PATH,
                    PLATFORM_PATH,
                    EXAMPLE_PLATFORM_PATH,
                    RULES_DIR,
                    EXAMPLE_RULES_DIR
                };
        HeadlessAndroidScanner scanner = HeadlessAndroidScanner.createFromCLISettings(args);

        Assert.assertEquals(scanner.getApkFile(), EXAMPLE_APK_PATH);
        Assert.assertEquals(scanner.getPlatformDirectory(), EXAMPLE_PLATFORM_PATH);
        Assert.assertEquals(scanner.getRulesetPath(), EXAMPLE_RULES_DIR);
    }

    @Test(expected = CryptoAnalysisParserException.class)
    public void testMissingApkPath() {
        String[] args =
                new String[] {PLATFORM_PATH, EXAMPLE_PLATFORM_PATH, RULES_DIR, EXAMPLE_RULES_DIR};
        HeadlessAndroidScanner scanner = HeadlessAndroidScanner.createFromCLISettings(args);

        Assert.assertEquals(scanner.getPlatformDirectory(), EXAMPLE_PLATFORM_PATH);
        Assert.assertEquals(scanner.getRulesetPath(), EXAMPLE_RULES_DIR);
    }

    @Test(expected = CryptoAnalysisParserException.class)
    public void testMissingPlatformPath() {
        String[] args = new String[] {APK_PATH, EXAMPLE_APK_PATH, RULES_DIR, EXAMPLE_RULES_DIR};
        HeadlessAndroidScanner scanner = HeadlessAndroidScanner.createFromCLISettings(args);

        Assert.assertEquals(scanner.getApkFile(), EXAMPLE_APK_PATH);
        Assert.assertEquals(scanner.getRulesetPath(), EXAMPLE_RULES_DIR);
    }

    @Test(expected = CryptoAnalysisParserException.class)
    public void testMissingRulesDir() {
        String[] args =
                new String[] {APK_PATH, EXAMPLE_APK_PATH, PLATFORM_PATH, EXAMPLE_PLATFORM_PATH};
        HeadlessAndroidScanner scanner = HeadlessAndroidScanner.createFromCLISettings(args);

        Assert.assertEquals(scanner.getApkFile(), EXAMPLE_APK_PATH);
        Assert.assertEquals(scanner.getPlatformDirectory(), EXAMPLE_PLATFORM_PATH);
    }

    @Test
    public void testReportPath() {
        String reportPath = "path/to/report";
        String[] args =
                new String[] {
                    APK_PATH,
                    EXAMPLE_APK_PATH,
                    PLATFORM_PATH,
                    EXAMPLE_PLATFORM_PATH,
                    RULES_DIR,
                    EXAMPLE_RULES_DIR,
                    REPORT_PATH,
                    reportPath
                };
        HeadlessAndroidScanner scanner = HeadlessAndroidScanner.createFromCLISettings(args);

        Assert.assertEquals(scanner.getReportDirectory(), reportPath);
    }

    @Test
    public void testReportFormat() {
        String[] cmdArgs =
                new String[] {
                    APK_PATH,
                    EXAMPLE_APK_PATH,
                    PLATFORM_PATH,
                    EXAMPLE_PLATFORM_PATH,
                    RULES_DIR,
                    EXAMPLE_RULES_DIR,
                    REPORT_FORMAT,
                    "CMD"
                };
        HeadlessAndroidScanner cmdScanner = HeadlessAndroidScanner.createFromCLISettings(cmdArgs);
        Assert.assertEquals(cmdScanner.getReportFormats(), Set.of(Reporter.ReportFormat.CMD));

        String[] txtArgs =
                new String[] {
                    APK_PATH,
                    EXAMPLE_APK_PATH,
                    PLATFORM_PATH,
                    EXAMPLE_PLATFORM_PATH,
                    RULES_DIR,
                    EXAMPLE_RULES_DIR,
                    REPORT_FORMAT,
                    "TXT"
                };
        HeadlessAndroidScanner txtScanner = HeadlessAndroidScanner.createFromCLISettings(txtArgs);
        Assert.assertEquals(txtScanner.getReportFormats(), Set.of(Reporter.ReportFormat.TXT));

        String[] sarifArgs =
                new String[] {
                    APK_PATH,
                    EXAMPLE_APK_PATH,
                    PLATFORM_PATH,
                    EXAMPLE_PLATFORM_PATH,
                    RULES_DIR,
                    EXAMPLE_RULES_DIR,
                    REPORT_FORMAT,
                    "SARIF"
                };
        HeadlessAndroidScanner sarifScanner =
                HeadlessAndroidScanner.createFromCLISettings(sarifArgs);
        Assert.assertEquals(sarifScanner.getReportFormats(), Set.of(Reporter.ReportFormat.SARIF));

        String[] csvArgs =
                new String[] {
                    APK_PATH,
                    EXAMPLE_APK_PATH,
                    PLATFORM_PATH,
                    EXAMPLE_PLATFORM_PATH,
                    RULES_DIR,
                    EXAMPLE_RULES_DIR,
                    REPORT_FORMAT,
                    "CSV"
                };
        HeadlessAndroidScanner csvScanner = HeadlessAndroidScanner.createFromCLISettings(csvArgs);
        Assert.assertEquals(csvScanner.getReportFormats(), Set.of(Reporter.ReportFormat.CSV));

        String[] csvSummaryArgs =
                new String[] {
                    APK_PATH,
                    EXAMPLE_APK_PATH,
                    PLATFORM_PATH,
                    EXAMPLE_PLATFORM_PATH,
                    RULES_DIR,
                    EXAMPLE_RULES_DIR,
                    REPORT_FORMAT,
                    "CSV_SUMMARY"
                };
        HeadlessAndroidScanner csvSummaryScanner =
                HeadlessAndroidScanner.createFromCLISettings(csvSummaryArgs);
        Assert.assertEquals(
                csvSummaryScanner.getReportFormats(), Set.of(Reporter.ReportFormat.CSV_SUMMARY));

        String[] annotationArgs =
                new String[] {
                    APK_PATH,
                    EXAMPLE_APK_PATH,
                    PLATFORM_PATH,
                    EXAMPLE_PLATFORM_PATH,
                    RULES_DIR,
                    EXAMPLE_RULES_DIR,
                    REPORT_FORMAT,
                    "GITHUB_ANNOTATION"
                };
        HeadlessAndroidScanner annotationScanner =
                HeadlessAndroidScanner.createFromCLISettings(annotationArgs);
        Assert.assertEquals(
                annotationScanner.getReportFormats(),
                Set.of(Reporter.ReportFormat.GITHUB_ANNOTATION));

        String[] multipleArgs =
                new String[] {
                    APK_PATH,
                    EXAMPLE_APK_PATH,
                    PLATFORM_PATH,
                    EXAMPLE_PLATFORM_PATH,
                    RULES_DIR,
                    EXAMPLE_RULES_DIR,
                    REPORT_FORMAT,
                    "CMD,TXT,CSV"
                };
        HeadlessAndroidScanner multipleFormatsScanner =
                HeadlessAndroidScanner.createFromCLISettings(multipleArgs);
        Assert.assertEquals(
                multipleFormatsScanner.getReportFormats(),
                Set.of(
                        Reporter.ReportFormat.CMD,
                        Reporter.ReportFormat.TXT,
                        Reporter.ReportFormat.CSV));
    }

    @Test
    public void testVisualization() {
        String reportPath = "path/to/report";
        String[] args =
                new String[] {
                    APK_PATH,
                    EXAMPLE_APK_PATH,
                    PLATFORM_PATH,
                    EXAMPLE_PLATFORM_PATH,
                    RULES_DIR,
                    EXAMPLE_RULES_DIR,
                    VISUALIZATION,
                    REPORT_PATH,
                    reportPath
                };
        HeadlessAndroidScanner scanner = HeadlessAndroidScanner.createFromCLISettings(args);
        Assert.assertTrue(scanner.isVisualization());
    }

    @Test(expected = CryptoAnalysisParserException.class)
    public void testInvalidVisualization() {
        String[] args =
                new String[] {
                    APK_PATH,
                    EXAMPLE_APK_PATH,
                    PLATFORM_PATH,
                    EXAMPLE_PLATFORM_PATH,
                    RULES_DIR,
                    EXAMPLE_RULES_DIR,
                    VISUALIZATION,
                };
        HeadlessAndroidScanner scanner = HeadlessAndroidScanner.createFromCLISettings(args);
        Assert.assertTrue(scanner.isVisualization());
    }
}
