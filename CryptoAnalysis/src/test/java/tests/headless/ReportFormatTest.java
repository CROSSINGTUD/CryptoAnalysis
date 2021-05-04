package tests.headless;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import crypto.HeadlessCryptoScanner;
import crypto.analysis.CryptoScannerSettings.ReportFormat;

public class ReportFormatTest extends AbstractHeadlessTest{

	private static final String rootPath = "cognicrypt-output/";
	private static final String txtReportPath = rootPath+"CryptoAnalysis-Report.txt";
	private static final String csvReportPath = rootPath+"CryptoAnalysis-Report.csv";
	private static final String sarifReportPath = rootPath+"CryptoAnalysis-Report.json";
	
	@Test
	public void TXTReportCreationTest() {
		File report = new File(txtReportPath);
		if(report.exists()) {
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
	public void CSVReportCreationTest() {
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
	public void SARIFReportCreationTest() {
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
	
	@After
	public void tearDown() {
		try {
			FileUtils.deleteDirectory(new File(rootPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
