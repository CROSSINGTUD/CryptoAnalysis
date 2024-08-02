package headless;

import java.io.File;

import de.fraunhofer.iem.scanner.HeadlessJavaScanner;
import org.junit.Ignore;
import org.junit.Test;

public class TLSRuleTest extends AbstractHeadlessTest{
	@Ignore
	@Test
	public void secureFileTransmitter() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/SecureFileTransmitter").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessJavaScanner scanner = createScanner(mavenProject);
	
		scanner.run();
		assertErrors(scanner.getCollectedErrors());
	}
}
