package tests.headless;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

import crypto.HeadlessCryptoScanner;

public class TLSRuleTest extends AbstractHeadlessTest{
	@Ignore
	@Test
	public void secureFileTransmitter() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/SecureFileTransmitter").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
	
		scanner.exec();
		assertErrors();
	}
}
