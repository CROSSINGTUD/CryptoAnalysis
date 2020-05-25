package tests.headless;

import java.io.File;

import org.junit.Test;

import crypto.HeadlessCryptoScanner;
import crypto.analysis.errors.IncompleteOperationError;

public class MessageDigestExampleTest extends AbstractHeadlessTest{

	@Test
	public void loadMessageDigestExample() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/MessageDigestExample").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		//false positive
		setErrorsCount("<MessageDigestExample.MessageDigestExample.Main: java.lang.String getSHA256(java.io.InputStream)>", IncompleteOperationError.class, 2);
		
		scanner.exec();
		assertErrors();
	}

}
