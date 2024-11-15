package scanner.targets;

import de.fraunhofer.iem.scanner.HeadlessJavaScanner;
import org.junit.Ignore;
import org.junit.Test;
import scanner.setup.MavenProject;
import scanner.setup.AbstractHeadlessTest;

import java.io.File;

public class TLSRuleTest extends AbstractHeadlessTest {
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
