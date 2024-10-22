package scanner;

import crypto.reporting.Reporter;
import de.fraunhofer.iem.scanner.HeadlessJavaScanner;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

public class ReportFormatTest extends AbstractHeadlessTest{

	private static final String rootPath = "cognicrypt-output/";
	private static final File outputDir = new File(rootPath);
	private static final String txtReportPath = rootPath + "CryptoAnalysis-Report.txt";
	private static final String csvReportPath = rootPath + "CryptoAnalysis-Report.csv";
	private static final String csvSummaryReportPath = rootPath + "CryptoAnalysis-Report-Summary.csv";
	private static final String sarifReportPath = rootPath + "CryptoAnalysis-Report.json";

	@Before
	public void setup() {
		outputDir.mkdir();
	}

	@Test
	public void testTXTReportCreation() {
		File report = new File(txtReportPath);
		if (report.exists()) {
			report.delete();
		}
		String mavenProjectPath = new File("../CryptoAnalysisTargets/ReportFormatExample").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);

		HeadlessJavaScanner scanner = createScanner(mavenProject);
		scanner.setReportDirectory(outputDir.getAbsolutePath());
		scanner.setReportFormats(Reporter.ReportFormat.TXT);
		scanner.run();

		Assert.assertTrue(report.exists());
	}
	
	@Test
	public void testCSVReportCreation() {
		File report = new File(csvReportPath);
		if(report.exists()) {
			report.delete();
		}
		String mavenProjectPath = new File("../CryptoAnalysisTargets/ReportFormatExample").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);

		HeadlessJavaScanner scanner = createScanner(mavenProject);
		scanner.setReportDirectory(outputDir.getAbsolutePath());
		scanner.setReportFormats(Reporter.ReportFormat.CSV);
		scanner.run();

		Assert.assertTrue(report.exists());
	}
	
	@Test
	public void testCSVSummaryCreation() {
		File report = new File(csvSummaryReportPath);
		
		if (report.exists()) {
			report.delete();
		}
		
		String mavenProjectPath = new File("../CryptoAnalysisTargets/ReportFormatExample").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		
		HeadlessJavaScanner scanner = createScanner(mavenProject);
		scanner.setReportDirectory(outputDir.getAbsolutePath());
		scanner.setReportFormats(Reporter.ReportFormat.CSV_SUMMARY);
		scanner.run();

		Assert.assertTrue(report.exists());
	}
	
	@Test
	public void testSARIFReportCreation() {
		File report = new File(sarifReportPath);
		if(report.exists()) {
			report.delete();
		}
		String mavenProjectPath = new File("../CryptoAnalysisTargets/ReportFormatExample").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);

		HeadlessJavaScanner scanner = createScanner(mavenProject);
		scanner.setReportDirectory(outputDir.getAbsolutePath());
		scanner.setReportFormats(Reporter.ReportFormat.SARIF);
		scanner.run();

		Assert.assertTrue(report.exists());
	}
	
	@Test
	public void testMultipleFormatsCreation() {
		File txtReport = new File(txtReportPath);
		
		if (txtReport.exists()) {
			txtReport.delete();
		}
		
		File csvReport = new File(csvReportPath);
		
		if (csvReport.exists()) {
			csvReport.delete();
		}
		
		File csvSummaryReport = new File(csvSummaryReportPath);
		
		if (csvSummaryReport.exists()) {
			csvSummaryReport.delete();
		}
		
		File sarifReport = new File(sarifReportPath);
		
		if (sarifReport.exists()) {
			sarifReport.delete();
		}
		
		String mavenProjectPath = new File("../CryptoAnalysisTargets/ReportFormatExample").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		
		Collection<Reporter.ReportFormat> formats = Arrays.asList(
				Reporter.ReportFormat.CMD,
				Reporter.ReportFormat.TXT,
				Reporter.ReportFormat.CSV,
				Reporter.ReportFormat.CSV_SUMMARY,
				Reporter.ReportFormat.SARIF);
		
		HeadlessJavaScanner scanner = createScanner(mavenProject);
		scanner.setReportDirectory(outputDir.getAbsolutePath());
		scanner.setReportFormats(formats);
		scanner.run();
		
		Assert.assertTrue(txtReport.exists());
		Assert.assertTrue(csvReport.exists());
		Assert.assertTrue(csvSummaryReport.exists());
		Assert.assertTrue(sarifReport.exists());
	}
	
	@After
	public void tearDown() {
		try {
			FileUtils.deleteDirectory(new File(rootPath));
		} catch (IOException e) {
			throw new RuntimeException("Could not delete test directories");
		}
	}
	
}
