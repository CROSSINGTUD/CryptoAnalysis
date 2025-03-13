package scanner.targets;

import crypto.analysis.errors.IncompleteOperationError;
import de.fraunhofer.iem.scanner.HeadlessJavaScanner;
import java.io.File;
import org.junit.Test;
import scanner.setup.AbstractHeadlessTest;
import scanner.setup.ErrorSpecification;
import scanner.setup.MavenProject;

public class MessageDigestExampleTest extends AbstractHeadlessTest {

    @Test
    public void loadMessageDigestExample() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/MessageDigestExample").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "MessageDigestExample.MessageDigestExample.Main", "getSHA256", 1)
                        .withTPs(IncompleteOperationError.class, 2)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }
}
