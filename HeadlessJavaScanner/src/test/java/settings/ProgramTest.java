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
import de.fraunhofer.iem.scanner.HeadlessJavaScanner;
import de.fraunhofer.iem.scanner.ScannerSettings;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProgramTest {

    private static final String EXAMPLE_APP_PATH = "path/to/app";
    private static final String EXAMPLE_RULES_DIR = "path/to/rules";

    @Test
    public void testMinimalApplication() {
        HeadlessJavaScanner scanner = new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);

        Assertions.assertEquals(scanner.getApplicationPath(), EXAMPLE_APP_PATH);
        Assertions.assertEquals(scanner.getRulesetPath(), EXAMPLE_RULES_DIR);
        Assertions.assertEquals(
                scanner.getCallGraphAlgorithm(), ScannerSettings.CallGraphAlgorithm.CHA);
        Assertions.assertEquals(scanner.getReportFormats(), Collections.emptySet());
        Assertions.assertFalse(scanner.isVisualization());
    }

    @Test
    public void testFramework() {
        HeadlessJavaScanner sootScanner =
                new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
        sootScanner.setFramework(ScannerSettings.Framework.SOOT);
        Assertions.assertEquals(sootScanner.getFramework(), ScannerSettings.Framework.SOOT);

        HeadlessJavaScanner sootUpScanner =
                new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
        sootUpScanner.setFramework(ScannerSettings.Framework.SOOT_UP);
        Assertions.assertEquals(sootUpScanner.getFramework(), ScannerSettings.Framework.SOOT_UP);

        HeadlessJavaScanner opalScanner =
                new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
        opalScanner.setFramework(ScannerSettings.Framework.OPAL);
        Assertions.assertEquals(opalScanner.getFramework(), ScannerSettings.Framework.OPAL);
    }

    @Test
    public void testCallGraph() {
        HeadlessJavaScanner chaScanner =
                new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
        chaScanner.setCallGraphAlgorithm(ScannerSettings.CallGraphAlgorithm.CHA);
        Assertions.assertEquals(
                chaScanner.getCallGraphAlgorithm(), ScannerSettings.CallGraphAlgorithm.CHA);

        HeadlessJavaScanner sparkScanner =
                new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
        sparkScanner.setCallGraphAlgorithm(ScannerSettings.CallGraphAlgorithm.SPARK);
        Assertions.assertEquals(
                sparkScanner.getCallGraphAlgorithm(), ScannerSettings.CallGraphAlgorithm.SPARK);

        HeadlessJavaScanner sparkLibScanner =
                new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
        sparkLibScanner.setCallGraphAlgorithm(ScannerSettings.CallGraphAlgorithm.SPARK_LIB);
        Assertions.assertEquals(
                sparkLibScanner.getCallGraphAlgorithm(),
                ScannerSettings.CallGraphAlgorithm.SPARK_LIB);
    }

    @Test
    public void testSootPath() {
        String sootPath = "path/to/soot";
        HeadlessJavaScanner scanner = new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
        scanner.setSootClassPath(sootPath);

        Assertions.assertEquals(scanner.getSootClassPath(), sootPath);
    }

    @Test
    public void testReportPath() {
        String reportPath = "path/to/report";
        HeadlessJavaScanner scanner = new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
        scanner.setReportDirectory(reportPath);

        Assertions.assertEquals(scanner.getReportDirectory(), reportPath);
    }

    @Test
    public void testReportFormat() {
        HeadlessJavaScanner cmdScanner =
                new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
        cmdScanner.setReportFormats(Reporter.ReportFormat.CMD);
        Assertions.assertEquals(cmdScanner.getReportFormats(), Set.of(Reporter.ReportFormat.CMD));

        HeadlessJavaScanner txtScanner =
                new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
        txtScanner.setReportFormats(Reporter.ReportFormat.TXT);
        Assertions.assertEquals(txtScanner.getReportFormats(), Set.of(Reporter.ReportFormat.TXT));

        HeadlessJavaScanner sarifScanner =
                new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
        sarifScanner.setReportFormats(Reporter.ReportFormat.SARIF);
        Assertions.assertEquals(
                sarifScanner.getReportFormats(), Set.of(Reporter.ReportFormat.SARIF));

        HeadlessJavaScanner csvScanner =
                new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
        csvScanner.setReportFormats(Reporter.ReportFormat.CSV);
        Assertions.assertEquals(csvScanner.getReportFormats(), Set.of(Reporter.ReportFormat.CSV));

        HeadlessJavaScanner csvSummaryScanner =
                new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
        csvSummaryScanner.setReportFormats(Reporter.ReportFormat.CSV_SUMMARY);
        Assertions.assertEquals(
                csvSummaryScanner.getReportFormats(), Set.of(Reporter.ReportFormat.CSV_SUMMARY));

        HeadlessJavaScanner annotationScanner =
                new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
        annotationScanner.setReportFormats(Reporter.ReportFormat.GITHUB_ANNOTATION);
        Assertions.assertEquals(
                annotationScanner.getReportFormats(),
                Set.of(Reporter.ReportFormat.GITHUB_ANNOTATION));

        HeadlessJavaScanner multipleFormatsScanner =
                new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
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
        HeadlessJavaScanner scanner = new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
        scanner.setVisualization(true);
        Assertions.assertTrue(scanner.isVisualization());
    }

    @Test
    public void testIgnoreSections() {
        HeadlessJavaScanner scanner = new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
        scanner.setIgnoredSections(
                Arrays.asList("example.class", "example.class.method", "example.*"));
        Assertions.assertEquals(
                scanner.getIgnoredSections(),
                Set.of("example.class", "example.class.method", "example.*"));
    }

    @Test
    public void testTimeout() {
        int timeout = 5000;
        HeadlessJavaScanner scanner = new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
        scanner.setTimeout(timeout);

        Assertions.assertEquals(scanner.getTimeout(), timeout);
    }
}
