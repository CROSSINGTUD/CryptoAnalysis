package tests.headless;

import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import crypto.HeadlessCryptoScanner;
import crypto.analysis.CryptoScannerSettings.ReportFormat;

public class ReportFormatTest extends AbstractHeadlessTest{

	private static final String txtReportPath = "cognicrypt-output/CryptoAnalysis-Report.txt";
	private static final String csvReportPath = "cognicrypt-output/CryptoAnalysis-Report.csv";
	private static final String sarifReportPath = "cognicrypt-output/CryptoAnalysis-Report.json";
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
	
}
