package settings;

import crypto.reporting.Reporter;
import de.fraunhofer.iem.android.HeadlessAndroidScanner;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class ProgramTest {

	private static final String EXAMPLE_APK_PATH = "path/to/apk";
	private static final String EXAMPLE_PLATFORM_PATH = "path/to/platform";
	private static final String EXAMPLE_RULES_DIR = "path/to/rules";

	@Test
	public void testMinimalApplication() {
		HeadlessAndroidScanner scanner = new HeadlessAndroidScanner(EXAMPLE_APK_PATH, EXAMPLE_PLATFORM_PATH, EXAMPLE_RULES_DIR);

		Assert.assertEquals(scanner.getApkFile(), EXAMPLE_APK_PATH);
		Assert.assertEquals(scanner.getPlatformDirectory(), EXAMPLE_PLATFORM_PATH);
		Assert.assertEquals(scanner.getRulesetPath(), EXAMPLE_RULES_DIR);
	}

	@Test
	public void testReportPath() {
		String reportPath = "path/to/report";
		HeadlessAndroidScanner scanner = new HeadlessAndroidScanner(EXAMPLE_APK_PATH, EXAMPLE_PLATFORM_PATH, EXAMPLE_RULES_DIR);
		scanner.setReportDirectory(reportPath);

		Assert.assertEquals(scanner.getReportDirectory(), reportPath);
	}

	@Test
	public void testReportFormat() {
		HeadlessAndroidScanner cmdScanner = new HeadlessAndroidScanner(EXAMPLE_APK_PATH, EXAMPLE_PLATFORM_PATH, EXAMPLE_RULES_DIR);
		cmdScanner.setReportFormats(Reporter.ReportFormat.CMD);
		Assert.assertEquals(cmdScanner.getReportFormats(), Set.of(Reporter.ReportFormat.CMD));

		HeadlessAndroidScanner txtScanner = new HeadlessAndroidScanner(EXAMPLE_APK_PATH, EXAMPLE_PLATFORM_PATH, EXAMPLE_RULES_DIR);
		txtScanner.setReportFormats(Reporter.ReportFormat.TXT);
		Assert.assertEquals(txtScanner.getReportFormats(), Set.of(Reporter.ReportFormat.TXT));

		HeadlessAndroidScanner sarifScanner = new HeadlessAndroidScanner(EXAMPLE_APK_PATH, EXAMPLE_PLATFORM_PATH, EXAMPLE_RULES_DIR);
		sarifScanner.setReportFormats(Reporter.ReportFormat.SARIF);
		Assert.assertEquals(sarifScanner.getReportFormats(), Set.of(Reporter.ReportFormat.SARIF));

		HeadlessAndroidScanner csvScanner = new HeadlessAndroidScanner(EXAMPLE_APK_PATH, EXAMPLE_PLATFORM_PATH, EXAMPLE_RULES_DIR);
		csvScanner.setReportFormats(Reporter.ReportFormat.CSV);
		Assert.assertEquals(csvScanner.getReportFormats(), Set.of(Reporter.ReportFormat.CSV));

		HeadlessAndroidScanner csvSummaryScanner = new HeadlessAndroidScanner(EXAMPLE_APK_PATH, EXAMPLE_PLATFORM_PATH, EXAMPLE_RULES_DIR);
		csvSummaryScanner.setReportFormats(Reporter.ReportFormat.CSV_SUMMARY);
		Assert.assertEquals(csvSummaryScanner.getReportFormats(), Set.of(Reporter.ReportFormat.CSV_SUMMARY));

		HeadlessAndroidScanner annotationScanner = new HeadlessAndroidScanner(EXAMPLE_APK_PATH, EXAMPLE_PLATFORM_PATH, EXAMPLE_RULES_DIR);
		annotationScanner.setReportFormats(Reporter.ReportFormat.GITHUB_ANNOTATION);
		Assert.assertEquals(annotationScanner.getReportFormats(), Set.of(Reporter.ReportFormat.GITHUB_ANNOTATION));

		HeadlessAndroidScanner multipleFormatsScanner = new HeadlessAndroidScanner(EXAMPLE_APK_PATH, EXAMPLE_PLATFORM_PATH, EXAMPLE_RULES_DIR);
		multipleFormatsScanner.setReportFormats(Reporter.ReportFormat.CMD, Reporter.ReportFormat.TXT, Reporter.ReportFormat.CSV);
		Assert.assertEquals(multipleFormatsScanner.getReportFormats(), Set.of(Reporter.ReportFormat.CMD, Reporter.ReportFormat.TXT, Reporter.ReportFormat.CSV));
	}
}
