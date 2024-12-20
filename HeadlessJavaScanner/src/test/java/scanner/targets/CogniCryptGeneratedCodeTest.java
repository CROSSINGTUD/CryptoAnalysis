package scanner.targets;

import crypto.analysis.errors.AlternativeReqPredicateError;
import crypto.analysis.errors.CallToError;
import crypto.analysis.errors.HardCodedError;
import crypto.analysis.errors.RequiredPredicateError;
import de.fraunhofer.iem.scanner.HeadlessJavaScanner;
import java.io.File;
import org.junit.Test;
import scanner.setup.AbstractHeadlessTest;
import scanner.setup.ErrorSpecification;
import scanner.setup.MavenProject;

public class CogniCryptGeneratedCodeTest extends AbstractHeadlessTest {

    @Test
    public void fileEncryptor() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/FileEncryptor").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("Crypto.Enc", "encrypt", 2)
                        .withTPs(CallToError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("Crypto.KeyDeriv", "getKey", 1)
                        .withFPs(HardCodedError.class, 1, "Mystery")
                        .withFPs(RequiredPredicateError.class, 3, "Mystery")
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("Crypto.Enc", "encrypt", 2)
                        .withFPs(AlternativeReqPredicateError.class, 1, "Mystery")
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("Crypto.Enc", "decrypt", 2)
                        .withFPs(RequiredPredicateError.class, 1, "Mystery")
                        .withFPs(AlternativeReqPredicateError.class, 1, "Mystery")
                        .build());

        scanner.run();
        assertErrors(scanner.getCollectedErrors());
    }

    @Test
    public void userAuthenticator() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/UserAuthenticator").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("Crypto.PWHasher", "verifyPWHash", 2)
                        .withTPs(RequiredPredicateError.class, 3)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("Crypto.PWHasher", "createPWHash", 1)
                        .withNoErrors(RequiredPredicateError.class)
                        .build());

        scanner.run();
        assertErrors(scanner.getCollectedErrors());
    }
}
