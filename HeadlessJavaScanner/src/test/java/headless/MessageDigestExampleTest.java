package headless;

import crypto.analysis.errors.IncompleteOperationError;
import de.fraunhofer.iem.scanner.HeadlessCryptoScanner;
import org.junit.Test;

import java.io.File;

public class MessageDigestExampleTest extends AbstractHeadlessTest{

	@Test
	public void loadMessageDigestExample() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/MessageDigestExample").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<MessageDigestExample.MessageDigestExample.Main: java.lang.String getSHA256(java.io.InputStream)>", IncompleteOperationError.class, 2);

		scanner.run();
		assertErrors(scanner.getCollectedErrors());
	}

}
