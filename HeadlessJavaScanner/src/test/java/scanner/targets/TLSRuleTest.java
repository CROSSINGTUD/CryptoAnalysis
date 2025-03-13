package scanner.targets;

import de.fraunhofer.iem.scanner.HeadlessJavaScanner;
import java.io.File;
import org.junit.Ignore;
import org.junit.Test;
import scanner.setup.AbstractHeadlessTest;
import scanner.setup.MavenProject;

public class TLSRuleTest extends AbstractHeadlessTest {
    @Ignore
    @Test
    public void secureFileTransmitter() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/SecureFileTransmitter").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }
}
