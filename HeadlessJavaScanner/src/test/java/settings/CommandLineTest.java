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
import de.fraunhofer.iem.scanner.HeadlessJavaScanner;
import de.fraunhofer.iem.scanner.ScannerSettings;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CommandLineTest {

    private static final String APP_PATH = "--appPath";
    private static final String EXAMPLE_APP_PATH = "path/to/app";

    private static final String RULES_DIR = "--rulesDir";
    private static final String EXAMPLE_RULES_DIR = "path/to/rules";

    private static final String FRAMEWORK = "--framework";
    private static final String CALL_GRAPH = "--cg";
    private static final String ADD_CLASS_PATH = "--addClassPath";
    private static final String REPORT_PATH = "--reportPath";
    private static final String REPORT_FORMAT = "--reportFormat";
    private static final String VISUALIZATION = "--visualization";
    private static final String IGNORE_SECTIONS = "--ignoreSections";
    private static final String TIMEOUT = "--timeout";

    @Test
    public void testMinimalApplication() {
        String[] args = new String[] {APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR};
        HeadlessJavaScanner scanner = HeadlessJavaScanner.createFromCLISettings(args);

        Assertions.assertEquals(scanner.getApplicationPath(), EXAMPLE_APP_PATH);
        Assertions.assertEquals(scanner.getRulesetPath(), EXAMPLE_RULES_DIR);
        Assertions.assertEquals(
                scanner.getCallGraphAlgorithm(), ScannerSettings.CallGraphAlgorithm.CHA);
        Assertions.assertEquals(scanner.getReportFormats(), Set.of(Reporter.ReportFormat.CMD));
        Assertions.assertFalse(scanner.isVisualization());
    }

    @Test
    public void testMissingApplicationPath() {
        Assertions.assertThrows(
                CryptoAnalysisParserException.class,
                () -> {
                    String[] args = new String[] {RULES_DIR, EXAMPLE_RULES_DIR};
                    HeadlessJavaScanner scanner = HeadlessJavaScanner.createFromCLISettings(args);

                    Assertions.assertEquals(scanner.getRulesetPath(), EXAMPLE_RULES_DIR);
                });
    }

    @Test
    public void testMissingRulesDir() {
        Assertions.assertThrows(
                CryptoAnalysisParserException.class,
                () -> {
                    String[] args = new String[] {APP_PATH, EXAMPLE_APP_PATH};
                    HeadlessJavaScanner scanner = HeadlessJavaScanner.createFromCLISettings(args);

                    Assertions.assertEquals(scanner.getApplicationPath(), APP_PATH);
                });
    }

    @Test
    public void testFramework() {
        String[] sootArgs =
                new String[] {
                    APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR, FRAMEWORK, "SOOT"
                };
        HeadlessJavaScanner sootScanner = HeadlessJavaScanner.createFromCLISettings(sootArgs);
        Assertions.assertEquals(sootScanner.getFramework(), ScannerSettings.Framework.SOOT);

        String[] sootUpArgs =
                new String[] {
                    APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR, FRAMEWORK, "SOOT_UP"
                };
        HeadlessJavaScanner sootUpScanner = HeadlessJavaScanner.createFromCLISettings(sootUpArgs);
        Assertions.assertEquals(sootUpScanner.getFramework(), ScannerSettings.Framework.SOOT_UP);

        String[] opalArgs =
                new String[] {
                    APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR, FRAMEWORK, "OPAL"
                };
        HeadlessJavaScanner opalScanner = HeadlessJavaScanner.createFromCLISettings(opalArgs);
        Assertions.assertEquals(opalScanner.getFramework(), ScannerSettings.Framework.OPAL);
    }

    @Test
    public void testInvalidFramework() {
        Assertions.assertThrows(
                CryptoAnalysisParserException.class,
                () -> {
                    String[] args =
                            new String[] {
                                APP_PATH,
                                EXAMPLE_APP_PATH,
                                RULES_DIR,
                                EXAMPLE_RULES_DIR,
                                FRAMEWORK,
                                "WALA"
                            };
                    HeadlessJavaScanner scanner = HeadlessJavaScanner.createFromCLISettings(args);
                    Assertions.assertEquals(scanner.getFramework(), ScannerSettings.Framework.SOOT);
                });
    }

    @Test
    public void testCallGraph() {
        String[] chaArgs =
                new String[] {
                    APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR, CALL_GRAPH, "CHA"
                };
        HeadlessJavaScanner chaScanner = HeadlessJavaScanner.createFromCLISettings(chaArgs);
        Assertions.assertEquals(
                chaScanner.getCallGraphAlgorithm(), ScannerSettings.CallGraphAlgorithm.CHA);

        String[] sparkArgs =
                new String[] {
                    APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR, CALL_GRAPH, "SPARK"
                };
        HeadlessJavaScanner sparkScanner = HeadlessJavaScanner.createFromCLISettings(sparkArgs);
        Assertions.assertEquals(
                sparkScanner.getCallGraphAlgorithm(), ScannerSettings.CallGraphAlgorithm.SPARK);

        String[] sparkLibArgs =
                new String[] {
                    APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR, CALL_GRAPH, "SPARKLIB"
                };
        HeadlessJavaScanner sparkLibScanner =
                HeadlessJavaScanner.createFromCLISettings(sparkLibArgs);
        Assertions.assertEquals(
                sparkLibScanner.getCallGraphAlgorithm(),
                ScannerSettings.CallGraphAlgorithm.SPARK_LIB);
    }

    @Test
    public void testInvalidCallGraph() {
        Assertions.assertThrows(
                CryptoAnalysisParserException.class,
                () -> {
                    String[] args =
                            new String[] {
                                APP_PATH,
                                EXAMPLE_APP_PATH,
                                RULES_DIR,
                                EXAMPLE_RULES_DIR,
                                CALL_GRAPH,
                                "RTA"
                            };
                    HeadlessJavaScanner scanner = HeadlessJavaScanner.createFromCLISettings(args);
                    Assertions.assertEquals(
                            scanner.getCallGraphAlgorithm(),
                            ScannerSettings.CallGraphAlgorithm.CHA);
                });
    }

    @Test
    public void testAddClassPath() {
        String classPath = "path/to/class";
        String[] args =
                new String[] {
                    APP_PATH,
                    EXAMPLE_APP_PATH,
                    RULES_DIR,
                    EXAMPLE_RULES_DIR,
                    ADD_CLASS_PATH,
                    classPath
                };
        HeadlessJavaScanner scanner = HeadlessJavaScanner.createFromCLISettings(args);

        Assertions.assertEquals(scanner.getAddClassPath(), classPath);
    }

    @Test
    public void testReportPath() {
        String reportPath = "path/to/report";
        String[] args =
                new String[] {
                    APP_PATH,
                    EXAMPLE_APP_PATH,
                    RULES_DIR,
                    EXAMPLE_RULES_DIR,
                    REPORT_PATH,
                    reportPath
                };
        HeadlessJavaScanner scanner = HeadlessJavaScanner.createFromCLISettings(args);

        Assertions.assertEquals(scanner.getReportDirectory(), reportPath);
    }

    @Test
    public void testReportFormat() {
        String[] cmdArgs =
                new String[] {
                    APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR, REPORT_FORMAT, "CMD"
                };
        HeadlessJavaScanner cmdScanner = HeadlessJavaScanner.createFromCLISettings(cmdArgs);
        Assertions.assertEquals(cmdScanner.getReportFormats(), Set.of(Reporter.ReportFormat.CMD));

        String[] txtArgs =
                new String[] {
                    APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR, REPORT_FORMAT, "TXT"
                };
        HeadlessJavaScanner txtScanner = HeadlessJavaScanner.createFromCLISettings(txtArgs);
        Assertions.assertEquals(txtScanner.getReportFormats(), Set.of(Reporter.ReportFormat.TXT));

        String[] sarifArgs =
                new String[] {
                    APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR, REPORT_FORMAT, "SARIF"
                };
        HeadlessJavaScanner sarifScanner = HeadlessJavaScanner.createFromCLISettings(sarifArgs);
        Assertions.assertEquals(
                sarifScanner.getReportFormats(), Set.of(Reporter.ReportFormat.SARIF));

        String[] csvArgs =
                new String[] {
                    APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR, REPORT_FORMAT, "CSV"
                };
        HeadlessJavaScanner csvScanner = HeadlessJavaScanner.createFromCLISettings(csvArgs);
        Assertions.assertEquals(csvScanner.getReportFormats(), Set.of(Reporter.ReportFormat.CSV));

        String[] csvSummaryArgs =
                new String[] {
                    APP_PATH,
                    EXAMPLE_APP_PATH,
                    RULES_DIR,
                    EXAMPLE_RULES_DIR,
                    REPORT_FORMAT,
                    "CSV_SUMMARY"
                };
        HeadlessJavaScanner csvSummaryScanner =
                HeadlessJavaScanner.createFromCLISettings(csvSummaryArgs);
        Assertions.assertEquals(
                csvSummaryScanner.getReportFormats(), Set.of(Reporter.ReportFormat.CSV_SUMMARY));

        String[] annotationArgs =
                new String[] {
                    APP_PATH,
                    EXAMPLE_APP_PATH,
                    RULES_DIR,
                    EXAMPLE_RULES_DIR,
                    REPORT_FORMAT,
                    "GITHUB_ANNOTATION"
                };
        HeadlessJavaScanner annotationScanner =
                HeadlessJavaScanner.createFromCLISettings(annotationArgs);
        Assertions.assertEquals(
                annotationScanner.getReportFormats(),
                Set.of(Reporter.ReportFormat.GITHUB_ANNOTATION));

        String[] multipleArgs =
                new String[] {
                    APP_PATH,
                    EXAMPLE_APP_PATH,
                    RULES_DIR,
                    EXAMPLE_RULES_DIR,
                    REPORT_FORMAT,
                    "CMD,TXT,CSV"
                };
        HeadlessJavaScanner multipleFormatsScanner =
                HeadlessJavaScanner.createFromCLISettings(multipleArgs);
        Assertions.assertEquals(
                multipleFormatsScanner.getReportFormats(),
                Set.of(
                        Reporter.ReportFormat.CMD,
                        Reporter.ReportFormat.TXT,
                        Reporter.ReportFormat.CSV));
    }

    @Test
    public void testInvalidReportFormat() {
        Assertions.assertThrows(
                CryptoAnalysisParserException.class,
                () -> {
                    String[] args =
                            new String[] {
                                APP_PATH,
                                EXAMPLE_APP_PATH,
                                RULES_DIR,
                                EXAMPLE_RULES_DIR,
                                REPORT_FORMAT,
                                "CMD,XTX"
                            };
                    HeadlessJavaScanner scanner = HeadlessJavaScanner.createFromCLISettings(args);
                    Assertions.assertEquals(
                            scanner.getReportFormats(), Set.of(Reporter.ReportFormat.CMD));
                });
    }

    @Test
    public void testVisualization() {
        String reportPath = "path/to/report";
        String[] args =
                new String[] {
                    APP_PATH,
                    EXAMPLE_APP_PATH,
                    RULES_DIR,
                    EXAMPLE_RULES_DIR,
                    VISUALIZATION,
                    REPORT_PATH,
                    reportPath
                };
        HeadlessJavaScanner scanner = HeadlessJavaScanner.createFromCLISettings(args);
        Assertions.assertTrue(scanner.isVisualization());
    }

    @Test
    public void testInvalidVisualization() {
        Assertions.assertThrows(
                CryptoAnalysisParserException.class,
                () -> {
                    String[] args =
                            new String[] {
                                APP_PATH,
                                EXAMPLE_APP_PATH,
                                RULES_DIR,
                                EXAMPLE_RULES_DIR,
                                VISUALIZATION
                            };
                    HeadlessJavaScanner scanner = HeadlessJavaScanner.createFromCLISettings(args);
                    Assertions.assertTrue(scanner.isVisualization());
                });
    }

    @Test
    public void testIgnoreSections() {
        String pathToFile = "src/test/resources/ignoreSections.txt";
        String[] args =
                new String[] {
                    APP_PATH,
                    EXAMPLE_APP_PATH,
                    RULES_DIR,
                    EXAMPLE_RULES_DIR,
                    IGNORE_SECTIONS,
                    pathToFile
                };
        HeadlessJavaScanner scanner = HeadlessJavaScanner.createFromCLISettings(args);
        Assertions.assertEquals(
                scanner.getIgnoredSections(),
                List.of("example.class", "example.class.method", "example.*"));
    }

    @Test
    public void testTimeout() {
        int timeout = 5000;
        String[] args =
                new String[] {
                    APP_PATH,
                    EXAMPLE_APP_PATH,
                    RULES_DIR,
                    EXAMPLE_RULES_DIR,
                    TIMEOUT,
                    String.valueOf(timeout)
                };
        HeadlessJavaScanner scanner = HeadlessJavaScanner.createFromCLISettings(args);
        Assertions.assertEquals(scanner.getTimeout(), timeout);
    }

    @Test
    public void testInvalidTimeout() {
        Assertions.assertThrows(
                CryptoAnalysisParserException.class,
                () -> {
                    int timeout = -10;
                    String[] args =
                            new String[] {
                                APP_PATH,
                                EXAMPLE_APP_PATH,
                                RULES_DIR,
                                EXAMPLE_RULES_DIR,
                                TIMEOUT,
                                String.valueOf(timeout)
                            };
                    HeadlessJavaScanner scanner = HeadlessJavaScanner.createFromCLISettings(args);
                    Assertions.assertEquals(scanner.getTimeout(), timeout);
                });
    }
}
