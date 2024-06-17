package tests.headless;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import crypto.HeadlessCryptoScanner;
import crypto.analysis.AnalysisSettings.ReportFormat;

public class ReportFormatTest extends AbstractHeadlessTest{

	private static final String rootPath = "cognicrypt-output/";
	private static final String txtReportPath = rootPath + "CryptoAnalysis-Report.txt";
	private static final String csvReportPath = rootPath + "CryptoAnalysis-Report.csv";
	private static final String csvSummaryReportPath = rootPath + "CryptoAnalysis-Report-Summary.csv";
	private static final String sarifReportPath = rootPath + "CryptoAnalysis-Report.json";
	
	@Test
	public void testTXTReportCreation() {
		File report = new File(txtReportPath);
		if (report.exists()) {
			report.delete();
		}
		String mavenProjectPath = new File("../CryptoAnalysisTargets/ReportFormatExample").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		setReportFormat(ReportFormat.TXT);
		setVISUALIZATION(true);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		scanner.exec();
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
		setReportFormat(ReportFormat.CSV);
		setVISUALIZATION(true);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		scanner.exec();
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
		setReportFormat(ReportFormat.CSV_SUMMARY);
		setVISUALIZATION(true);
		
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		scanner.exec();
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
		setReportFormat(ReportFormat.SARIF);
		setVISUALIZATION(true);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		scanner.exec();
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
		
		setReportFormat(ReportFormat.CMD, ReportFormat.TXT, ReportFormat.CSV, ReportFormat.CSV_SUMMARY, ReportFormat.SARIF);
		setVISUALIZATION(true);
		
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		scanner.exec();
		
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
