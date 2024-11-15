package settings;

import crypto.exceptions.CryptoAnalysisParserException;
import crypto.reporting.Reporter;
import de.fraunhofer.iem.scanner.ScannerSettings;
import de.fraunhofer.iem.scanner.HeadlessJavaScanner;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;

public class CommandLineTest {

    private static final String APP_PATH = "--appPath";
    private static final String EXAMPLE_APP_PATH = "path/to/app";

    private static final String RULES_DIR = "--rulesDir";
    private static final String EXAMPLE_RULES_DIR = "path/to/rules";

    private static final String FRAMEWORK = "--framework";
    private static final String CALL_GRAPH = "--cg";
    private static final String SOOT_PATH = "--sootPath";
    private static final String REPORT_PATH = "--reportPath";
    private static final String REPORT_FORMAT = "--reportFormat";
    private static final String VISUALIZATION = "--visualization";
    private static final String IGNORE_SECTIONS = "--ignoreSections";
    private static final String TIMEOUT = "--timeout";

    @Test
    public void testMinimalApplication() {
        String[] args = new String[]{APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR};
        HeadlessJavaScanner scanner = HeadlessJavaScanner.createFromCLISettings(args);

        Assert.assertEquals(scanner.getApplicationPath(), EXAMPLE_APP_PATH);
        Assert.assertEquals(scanner.getRulesetPath(), EXAMPLE_RULES_DIR);
        Assert.assertEquals(scanner.getCallGraphAlgorithm(), ScannerSettings.CallGraphAlgorithm.CHA);
        Assert.assertEquals(scanner.getReportFormats(), Set.of(Reporter.ReportFormat.CMD));
        Assert.assertFalse(scanner.isVisualization());
    }

    @Test(expected = CryptoAnalysisParserException.class)
    public void testMissingApplicationPath() {
        String[] args = new String[]{RULES_DIR, EXAMPLE_RULES_DIR};
        HeadlessJavaScanner scanner = HeadlessJavaScanner.createFromCLISettings(args);

        Assert.assertEquals(scanner.getRulesetPath(), EXAMPLE_RULES_DIR);
    }

    @Test(expected = CryptoAnalysisParserException.class)
    public void testMissingRulesDir() {
        String[] args = new String[]{APP_PATH, EXAMPLE_APP_PATH};
        HeadlessJavaScanner scanner = HeadlessJavaScanner.createFromCLISettings(args);

        Assert.assertEquals(scanner.getApplicationPath(), APP_PATH);
    }

    @Test
    public void testFramework() {
        String[] sootArgs = new String[]{APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR, FRAMEWORK, "SOOT"};
        HeadlessJavaScanner sootScanner = HeadlessJavaScanner.createFromCLISettings(sootArgs);
        Assert.assertEquals(sootScanner.getFramework(), ScannerSettings.Framework.SOOT);

        String[] sootUpArgs = new String[]{APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR, FRAMEWORK, "SOOT_UP"};
        HeadlessJavaScanner sootUpScanner = HeadlessJavaScanner.createFromCLISettings(sootUpArgs);
        Assert.assertEquals(sootUpScanner.getFramework(), ScannerSettings.Framework.SOOT_UP);

        String[] opalArgs = new String[]{APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR, FRAMEWORK, "OPAL"};
        HeadlessJavaScanner opalScanner = HeadlessJavaScanner.createFromCLISettings(opalArgs);
        Assert.assertEquals(opalScanner.getFramework(), ScannerSettings.Framework.OPAL);
    }

    @Test(expected = CryptoAnalysisParserException.class)
    public void testInvalidFramework() {
        String[] args = new String[]{APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR, FRAMEWORK, "WALA"};
        HeadlessJavaScanner scanner = HeadlessJavaScanner.createFromCLISettings(args);
        Assert.assertEquals(scanner.getFramework(), ScannerSettings.Framework.SOOT);
    }

    @Test
    public void testCallGraph() {
        String[] chaArgs = new String[]{APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR, CALL_GRAPH, "CHA"};
        HeadlessJavaScanner chaScanner = HeadlessJavaScanner.createFromCLISettings(chaArgs);
        Assert.assertEquals(chaScanner.getCallGraphAlgorithm(), ScannerSettings.CallGraphAlgorithm.CHA);

        String[] sparkArgs = new String[]{APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR, CALL_GRAPH, "SPARK"};
        HeadlessJavaScanner sparkScanner = HeadlessJavaScanner.createFromCLISettings(sparkArgs);
        Assert.assertEquals(sparkScanner.getCallGraphAlgorithm(), ScannerSettings.CallGraphAlgorithm.SPARK);

        String[] sparkLibArgs = new String[]{APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR, CALL_GRAPH, "SPARKLIB"};
        HeadlessJavaScanner sparkLibScanner = HeadlessJavaScanner.createFromCLISettings(sparkLibArgs);
        Assert.assertEquals(sparkLibScanner.getCallGraphAlgorithm(), ScannerSettings.CallGraphAlgorithm.SPARK_LIB);
    }

    @Test(expected = CryptoAnalysisParserException.class)
    public void testInvalidCallGraph() {
        String[] args = new String[]{APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR, CALL_GRAPH, "RTA"};
        HeadlessJavaScanner scanner = HeadlessJavaScanner.createFromCLISettings(args);
        Assert.assertEquals(scanner.getCallGraphAlgorithm(), ScannerSettings.CallGraphAlgorithm.CHA);
    }

    @Test
    public void testSootPath() {
        String sootPath = "path/to/soot";
        String[] args = new String[]{APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR, SOOT_PATH, sootPath};
        HeadlessJavaScanner scanner = HeadlessJavaScanner.createFromCLISettings(args);

        Assert.assertEquals(scanner.getSootClassPath(), sootPath);
    }

    @Test
    public void testReportPath() {
        String reportPath = "path/to/report";
        String[] args = new String[]{APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR, REPORT_PATH, reportPath};
        HeadlessJavaScanner scanner = HeadlessJavaScanner.createFromCLISettings(args);

        Assert.assertEquals(scanner.getReportDirectory(), reportPath);
    }

    @Test
    public void testReportFormat() {
        String[] cmdArgs = new String[]{APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR, REPORT_FORMAT, "CMD"};
        HeadlessJavaScanner cmdScanner = HeadlessJavaScanner.createFromCLISettings(cmdArgs);
        Assert.assertEquals(cmdScanner.getReportFormats(), Set.of(Reporter.ReportFormat.CMD));

        String[] txtArgs = new String[]{APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR, REPORT_FORMAT, "TXT"};
        HeadlessJavaScanner txtScanner = HeadlessJavaScanner.createFromCLISettings(txtArgs);
        Assert.assertEquals(txtScanner.getReportFormats(), Set.of(Reporter.ReportFormat.TXT));

        String[] sarifArgs = new String[]{APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR, REPORT_FORMAT, "SARIF"};
        HeadlessJavaScanner sarifScanner = HeadlessJavaScanner.createFromCLISettings(sarifArgs);
        Assert.assertEquals(sarifScanner.getReportFormats(), Set.of(Reporter.ReportFormat.SARIF));

        String[] csvArgs = new String[]{APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR, REPORT_FORMAT, "CSV"};
        HeadlessJavaScanner csvScanner = HeadlessJavaScanner.createFromCLISettings(csvArgs);
        Assert.assertEquals(csvScanner.getReportFormats(), Set.of(Reporter.ReportFormat.CSV));

        String[] csvSummaryArgs = new String[]{APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR, REPORT_FORMAT, "CSV_SUMMARY"};
        HeadlessJavaScanner csvSummaryScanner = HeadlessJavaScanner.createFromCLISettings(csvSummaryArgs);
        Assert.assertEquals(csvSummaryScanner.getReportFormats(), Set.of(Reporter.ReportFormat.CSV_SUMMARY));

        String[] annotationArgs = new String[]{APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR, REPORT_FORMAT, "GITHUB_ANNOTATION"};
        HeadlessJavaScanner annotationScanner = HeadlessJavaScanner.createFromCLISettings(annotationArgs);
        Assert.assertEquals(annotationScanner.getReportFormats(), Set.of(Reporter.ReportFormat.GITHUB_ANNOTATION));

        String[] multipleArgs = new String[]{APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR, REPORT_FORMAT, "CMD,TXT,CSV"};
        HeadlessJavaScanner multipleFormatsScanner = HeadlessJavaScanner.createFromCLISettings(multipleArgs);
        Assert.assertEquals(multipleFormatsScanner.getReportFormats(), Set.of(Reporter.ReportFormat.CMD, Reporter.ReportFormat.TXT, Reporter.ReportFormat.CSV));
    }

    @Test(expected = CryptoAnalysisParserException.class)
    public void testInvalidReportFormat() {
        String[] args = new String[]{APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR, REPORT_FORMAT, "CMD,XTX"};
        HeadlessJavaScanner scanner = HeadlessJavaScanner.createFromCLISettings(args);
        Assert.assertEquals(scanner.getReportFormats(), Set.of(Reporter.ReportFormat.CMD));
    }

    @Test
    public void testVisualization() {
        String[] args = new String[]{APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR, VISUALIZATION};
        HeadlessJavaScanner scanner = HeadlessJavaScanner.createFromCLISettings(args);
        Assert.assertTrue(scanner.isVisualization());
    }

    @Test
    public void testIgnoreSections() {
        String pathToFile = "src/test/resources/ignoreSections.txt";
        String[] args = new String[]{APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR, IGNORE_SECTIONS, pathToFile};
        HeadlessJavaScanner scanner = HeadlessJavaScanner.createFromCLISettings(args);
        Assert.assertEquals(scanner.getIgnoredSections(), List.of("example.class", "example.class.method", "example.*"));
    }

    @Test
    public void testTimeout() {
        int timeout = 5000;
        String[] args = new String[]{APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR, TIMEOUT, String.valueOf(timeout)};
        HeadlessJavaScanner scanner = HeadlessJavaScanner.createFromCLISettings(args);
        Assert.assertEquals(scanner.getTimeout(), timeout);
    }

    @Test(expected = CryptoAnalysisParserException.class)
    public void testInvalidTimeout() {
        int timeout = -10;
        String[] args = new String[]{APP_PATH, EXAMPLE_APP_PATH, RULES_DIR, EXAMPLE_RULES_DIR, TIMEOUT, String.valueOf(timeout)};
        HeadlessJavaScanner scanner = HeadlessJavaScanner.createFromCLISettings(args);
        Assert.assertEquals(scanner.getTimeout(), timeout);
    }
}
