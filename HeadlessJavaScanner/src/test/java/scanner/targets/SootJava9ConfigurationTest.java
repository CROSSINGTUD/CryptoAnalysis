package scanner.targets;

import static org.junit.Assume.assumeTrue;

import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import de.fraunhofer.iem.scanner.HeadlessJavaScanner;
import java.io.File;
import org.junit.Before;
import org.junit.Test;
import scanner.setup.AbstractHeadlessTest;
import scanner.setup.ErrorSpecification;
import scanner.setup.MavenProject;

public class SootJava9ConfigurationTest extends AbstractHeadlessTest {

    @Before
    public void checkJavaVersion() {
        assumeTrue(getVersion() >= 9);
    }

    private static int getVersion() {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.")) {
            version = version.substring(2, 3);
        } else {
            int dot = version.indexOf(".");
            if (dot != -1) {
                version = version.substring(0, dot);
            }
        }
        return Integer.parseInt(version);
    }

    @Test
    public void testJava9ClasspathProject() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/Java9ClasspathExample").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("ConstraintErrorExample", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());

        scanner.run();
        assertErrors(scanner.getCollectedErrors());
    }

    @Test
    public void testJava8Project() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/CogniCryptDemoExample").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("example.ConstraintErrorExample", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("example.PredicateMissingExample", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("example.IncompleteOperationErrorExample", "main", 1)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("example.TypestateErrorExample", "main", 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("example.fixed.ConstraintErrorExample", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("example.fixed.PredicateMissingExample", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .build());

        scanner.run();
        assertErrors(scanner.getCollectedErrors());
    }

    @Test
    public void testJava9ModularProject() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/Java9ModuleExample").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("org.demo.jpms.MainClass", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());

        scanner.run();
        assertErrors(scanner.getCollectedErrors());
    }
}
