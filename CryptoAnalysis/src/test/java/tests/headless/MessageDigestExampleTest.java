package tests.headless;

import crypto.HeadlessCryptoScanner;
import crypto.analysis.errors.IncompleteOperationError;
import org.junit.Test;

import java.io.File;

public class MessageDigestExampleTest extends AbstractHeadlessTest{

	@Test
	public void loadMessageDigestExample() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/MessageDigestExample").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<MessageDigestExample.MessageDigestExample.Main: java.lang.String getSHA256(java.io.InputStream)>", IncompleteOperationError.class, 1);

		scanner.run();
		assertErrors(scanner.getErrorCollection());
	}

}
