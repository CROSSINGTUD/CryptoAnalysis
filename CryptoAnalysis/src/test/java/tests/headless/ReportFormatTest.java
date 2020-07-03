package tests.headless;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import crypto.HeadlessCryptoScanner;

public class ReportFormatTest extends AbstractHeadlessTest{

	@Test
	public void CSVReportCreationTest() {
		File report = new File("cognicrypt-output/CryptoAnalysis-Report.csv");
		if(report.exists()) {
			report.delete();
		}
		String mavenProjectPath = new File("../CryptoAnalysisTargets/ReportFormatExample").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		setVISUALIZATION(true);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		scanner.exec();
		Assert.assertEquals(true, report.exists());
	}
	
	
}
