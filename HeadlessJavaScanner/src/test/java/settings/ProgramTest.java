package settings;

import crypto.reporting.Reporter;
import de.fraunhofer.iem.scanner.ScannerSettings;
import de.fraunhofer.iem.scanner.HeadlessJavaScanner;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class ProgramTest {

	private static final String EXAMPLE_APP_PATH = "path/to/app";
	private static final String EXAMPLE_RULES_DIR = "path/to/rules";

	@Test
	public void testMinimalApplication() {
		HeadlessJavaScanner scanner = new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);

		Assert.assertEquals(scanner.getApplicationPath(), EXAMPLE_APP_PATH);
		Assert.assertEquals(scanner.getRulesetPath(), EXAMPLE_RULES_DIR);
		Assert.assertEquals(scanner.getCallGraphAlgorithm(), ScannerSettings.CallGraphAlgorithm.CHA);
		Assert.assertEquals(scanner.getReportFormats(), Collections.emptySet());
		Assert.assertFalse(scanner.isVisualization());
	}

	@Test
	public void testFramework() {
		HeadlessJavaScanner sootScanner = new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
		sootScanner.setFramework(ScannerSettings.Framework.SOOT);
		Assert.assertEquals(sootScanner.getFramework(), ScannerSettings.Framework.SOOT);

		HeadlessJavaScanner sootUpScanner = new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
		sootUpScanner.setFramework(ScannerSettings.Framework.SOOT_UP);
		Assert.assertEquals(sootUpScanner.getFramework(), ScannerSettings.Framework.SOOT_UP);

		HeadlessJavaScanner opalScanner = new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
		opalScanner.setFramework(ScannerSettings.Framework.OPAL);
		Assert.assertEquals(opalScanner.getFramework(), ScannerSettings.Framework.OPAL);
	}

	@Test
	public void testCallGraph() {
		HeadlessJavaScanner chaScanner = new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
		chaScanner.setCallGraphAlgorithm(ScannerSettings.CallGraphAlgorithm.CHA);
		Assert.assertEquals(chaScanner.getCallGraphAlgorithm(), ScannerSettings.CallGraphAlgorithm.CHA);

		HeadlessJavaScanner sparkScanner = new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
		sparkScanner.setCallGraphAlgorithm(ScannerSettings.CallGraphAlgorithm.SPARK);
		Assert.assertEquals(sparkScanner.getCallGraphAlgorithm(), ScannerSettings.CallGraphAlgorithm.SPARK);

		HeadlessJavaScanner sparkLibScanner = new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
		sparkLibScanner.setCallGraphAlgorithm(ScannerSettings.CallGraphAlgorithm.SPARK_LIB);
		Assert.assertEquals(sparkLibScanner.getCallGraphAlgorithm(), ScannerSettings.CallGraphAlgorithm.SPARK_LIB);
	}

	@Test
	public void testSootPath() {
		String sootPath = "path/to/soot";
		HeadlessJavaScanner scanner = new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
		scanner.setSootClassPath(sootPath);

		Assert.assertEquals(scanner.getSootClassPath(), sootPath);
	}

	@Test
	public void testReportPath() {
		String reportPath = "path/to/report";
		HeadlessJavaScanner scanner = new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
		scanner.setReportDirectory(reportPath);

		Assert.assertEquals(scanner.getReportDirectory(), reportPath);
	}

	@Test
	public void testReportFormat() {
		HeadlessJavaScanner cmdScanner = new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
		cmdScanner.setReportFormats(Reporter.ReportFormat.CMD);
		Assert.assertEquals(cmdScanner.getReportFormats(), Set.of(Reporter.ReportFormat.CMD));

		HeadlessJavaScanner txtScanner = new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
		txtScanner.setReportFormats(Reporter.ReportFormat.TXT);
		Assert.assertEquals(txtScanner.getReportFormats(), Set.of(Reporter.ReportFormat.TXT));

		HeadlessJavaScanner sarifScanner = new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
		sarifScanner.setReportFormats(Reporter.ReportFormat.SARIF);
		Assert.assertEquals(sarifScanner.getReportFormats(), Set.of(Reporter.ReportFormat.SARIF));

		HeadlessJavaScanner csvScanner = new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
		csvScanner.setReportFormats(Reporter.ReportFormat.CSV);
		Assert.assertEquals(csvScanner.getReportFormats(), Set.of(Reporter.ReportFormat.CSV));

		HeadlessJavaScanner csvSummaryScanner = new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
		csvSummaryScanner.setReportFormats(Reporter.ReportFormat.CSV_SUMMARY);
		Assert.assertEquals(csvSummaryScanner.getReportFormats(), Set.of(Reporter.ReportFormat.CSV_SUMMARY));

		HeadlessJavaScanner annotationScanner = new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
		annotationScanner.setReportFormats(Reporter.ReportFormat.GITHUB_ANNOTATION);
		Assert.assertEquals(annotationScanner.getReportFormats(), Set.of(Reporter.ReportFormat.GITHUB_ANNOTATION));

		HeadlessJavaScanner multipleFormatsScanner = new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
		multipleFormatsScanner.setReportFormats(Reporter.ReportFormat.CMD, Reporter.ReportFormat.TXT, Reporter.ReportFormat.CSV);
		Assert.assertEquals(multipleFormatsScanner.getReportFormats(), Set.of(Reporter.ReportFormat.CMD, Reporter.ReportFormat.TXT, Reporter.ReportFormat.CSV));
	}

	@Test
	public void testVisualization() {
		HeadlessJavaScanner scanner = new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
		scanner.setVisualization(true);
		Assert.assertTrue(scanner.isVisualization());
	}

	@Test
	public void testIgnoreSections() {
		HeadlessJavaScanner scanner = new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
		scanner.setIgnoredSections(Arrays.asList(
				"example.class",
				"example.class.method",
				"example.*"
		));
		Assert.assertEquals(scanner.getIgnoredSections(), Set.of("example.class", "example.class.method", "example.*"));
	}

	@Test
	public void testTimeout() {
		int timeout = 5000;
		HeadlessJavaScanner scanner = new HeadlessJavaScanner(EXAMPLE_APP_PATH, EXAMPLE_RULES_DIR);
		scanner.setTimeout(timeout);

		Assert.assertEquals(scanner.getTimeout(), timeout);
	}
}
