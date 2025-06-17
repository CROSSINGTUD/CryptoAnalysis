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

import crypto.reporting.Reporter;
import de.fraunhofer.iem.android.AndroidSettings;
import de.fraunhofer.iem.android.HeadlessAndroidScanner;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProgramTest {

    private static final String EXAMPLE_APK_PATH = "path/to/apk";
    private static final String EXAMPLE_PLATFORM_PATH = "path/to/platform";
    private static final String EXAMPLE_RULES_DIR = "path/to/rules";

    @Test
    public void testMinimalApplication() {
        HeadlessAndroidScanner scanner =
                new HeadlessAndroidScanner(
                        EXAMPLE_APK_PATH, EXAMPLE_PLATFORM_PATH, EXAMPLE_RULES_DIR);

        Assertions.assertEquals(scanner.getApkFile(), EXAMPLE_APK_PATH);
        Assertions.assertEquals(scanner.getPlatformDirectory(), EXAMPLE_PLATFORM_PATH);
        Assertions.assertEquals(scanner.getRulesetPath(), EXAMPLE_RULES_DIR);
        Assertions.assertEquals(
                scanner.getCallGraphAlgorithm(), AndroidSettings.CallGraphAlgorithm.CHA);
    }

    @Test
    public void testCallGraph() {
        HeadlessAndroidScanner chaScanner =
                new HeadlessAndroidScanner(
                        EXAMPLE_APK_PATH, EXAMPLE_PLATFORM_PATH, EXAMPLE_RULES_DIR);
        chaScanner.setCallGraphAlgorithm(AndroidSettings.CallGraphAlgorithm.CHA);
        Assertions.assertEquals(
                chaScanner.getCallGraphAlgorithm(), AndroidSettings.CallGraphAlgorithm.CHA);

        HeadlessAndroidScanner rtaScanner =
                new HeadlessAndroidScanner(
                        EXAMPLE_APK_PATH, EXAMPLE_PLATFORM_PATH, EXAMPLE_RULES_DIR);
        rtaScanner.setCallGraphAlgorithm(AndroidSettings.CallGraphAlgorithm.RTA);
        Assertions.assertEquals(
                rtaScanner.getCallGraphAlgorithm(), AndroidSettings.CallGraphAlgorithm.RTA);

        HeadlessAndroidScanner vtaScanner =
                new HeadlessAndroidScanner(
                        EXAMPLE_APK_PATH, EXAMPLE_PLATFORM_PATH, EXAMPLE_RULES_DIR);
        vtaScanner.setCallGraphAlgorithm(AndroidSettings.CallGraphAlgorithm.VTA);
        Assertions.assertEquals(
                vtaScanner.getCallGraphAlgorithm(), AndroidSettings.CallGraphAlgorithm.VTA);

        HeadlessAndroidScanner sparkScanner =
                new HeadlessAndroidScanner(
                        EXAMPLE_APK_PATH, EXAMPLE_PLATFORM_PATH, EXAMPLE_RULES_DIR);
        sparkScanner.setCallGraphAlgorithm(AndroidSettings.CallGraphAlgorithm.SPARK);
        Assertions.assertEquals(
                sparkScanner.getCallGraphAlgorithm(), AndroidSettings.CallGraphAlgorithm.SPARK);
    }

    @Test
    public void testReportPath() {
        String reportPath = "path/to/report";
        HeadlessAndroidScanner scanner =
                new HeadlessAndroidScanner(
                        EXAMPLE_APK_PATH, EXAMPLE_PLATFORM_PATH, EXAMPLE_RULES_DIR);
        scanner.setReportDirectory(reportPath);

        Assertions.assertEquals(scanner.getReportDirectory(), reportPath);
    }

    @Test
    public void testReportFormat() {
        HeadlessAndroidScanner cmdScanner =
                new HeadlessAndroidScanner(
                        EXAMPLE_APK_PATH, EXAMPLE_PLATFORM_PATH, EXAMPLE_RULES_DIR);
        cmdScanner.setReportFormats(Reporter.ReportFormat.CMD);
        Assertions.assertEquals(cmdScanner.getReportFormats(), Set.of(Reporter.ReportFormat.CMD));

        HeadlessAndroidScanner txtScanner =
                new HeadlessAndroidScanner(
                        EXAMPLE_APK_PATH, EXAMPLE_PLATFORM_PATH, EXAMPLE_RULES_DIR);
        txtScanner.setReportFormats(Reporter.ReportFormat.TXT);
        Assertions.assertEquals(txtScanner.getReportFormats(), Set.of(Reporter.ReportFormat.TXT));

        HeadlessAndroidScanner sarifScanner =
                new HeadlessAndroidScanner(
                        EXAMPLE_APK_PATH, EXAMPLE_PLATFORM_PATH, EXAMPLE_RULES_DIR);
        sarifScanner.setReportFormats(Reporter.ReportFormat.SARIF);
        Assertions.assertEquals(
                sarifScanner.getReportFormats(), Set.of(Reporter.ReportFormat.SARIF));

        HeadlessAndroidScanner csvScanner =
                new HeadlessAndroidScanner(
                        EXAMPLE_APK_PATH, EXAMPLE_PLATFORM_PATH, EXAMPLE_RULES_DIR);
        csvScanner.setReportFormats(Reporter.ReportFormat.CSV);
        Assertions.assertEquals(csvScanner.getReportFormats(), Set.of(Reporter.ReportFormat.CSV));

        HeadlessAndroidScanner csvSummaryScanner =
                new HeadlessAndroidScanner(
                        EXAMPLE_APK_PATH, EXAMPLE_PLATFORM_PATH, EXAMPLE_RULES_DIR);
        csvSummaryScanner.setReportFormats(Reporter.ReportFormat.CSV_SUMMARY);
        Assertions.assertEquals(
                csvSummaryScanner.getReportFormats(), Set.of(Reporter.ReportFormat.CSV_SUMMARY));

        HeadlessAndroidScanner annotationScanner =
                new HeadlessAndroidScanner(
                        EXAMPLE_APK_PATH, EXAMPLE_PLATFORM_PATH, EXAMPLE_RULES_DIR);
        annotationScanner.setReportFormats(Reporter.ReportFormat.GITHUB_ANNOTATION);
        Assertions.assertEquals(
                annotationScanner.getReportFormats(),
                Set.of(Reporter.ReportFormat.GITHUB_ANNOTATION));

        HeadlessAndroidScanner multipleFormatsScanner =
                new HeadlessAndroidScanner(
                        EXAMPLE_APK_PATH, EXAMPLE_PLATFORM_PATH, EXAMPLE_RULES_DIR);
        multipleFormatsScanner.setReportFormats(
                Reporter.ReportFormat.CMD, Reporter.ReportFormat.TXT, Reporter.ReportFormat.CSV);
        Assertions.assertEquals(
                multipleFormatsScanner.getReportFormats(),
                Set.of(
                        Reporter.ReportFormat.CMD,
                        Reporter.ReportFormat.TXT,
                        Reporter.ReportFormat.CSV));
    }

    @Test
    public void testVisualization() {
        HeadlessAndroidScanner scanner =
                new HeadlessAndroidScanner(
                        EXAMPLE_APK_PATH, EXAMPLE_PLATFORM_PATH, EXAMPLE_RULES_DIR);
        scanner.setVisualization(true);
        Assertions.assertTrue(scanner.isVisualization());
    }
}
