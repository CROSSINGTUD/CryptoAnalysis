package scanner.targets;

import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.HardCodedError;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.NeverTypeOfError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import de.fraunhofer.iem.scanner.HeadlessJavaScanner;
import org.junit.Test;
import scanner.setup.AbstractHeadlessTest;
import scanner.setup.ErrorSpecification;
import scanner.setup.MavenProject;

import java.io.File;

public class StaticAnalysisDemoTest extends AbstractHeadlessTest {

    @Test
    public void cogniCryptDemoExamples() {
        String mavenProjectPath = new File("../CryptoAnalysisTargets/CogniCryptDemoExample").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(new ErrorSpecification.Builder("example.ConstraintErrorExample", "main", 1)
                .withTPs(ConstraintError.class, 1)
                .withTPs(IncompleteOperationError.class, 1)
                .build());

        addErrorSpecification(new ErrorSpecification.Builder("example.PredicateMissingExample", "main", 1)
                .withTPs(ConstraintError.class, 2)
                .withTPs(RequiredPredicateError.class, 2)
                .build());

        addErrorSpecification(new ErrorSpecification.Builder("example.IncompleteOperationErrorExample", "main", 1)
                .withTPs(IncompleteOperationError.class, 1)
                .build());

        addErrorSpecification(new ErrorSpecification.Builder("example.TypestateErrorExample", "main", 1)
                .withTPs(TypestateError.class, 1)
                .build());

        addErrorSpecification(new ErrorSpecification.Builder("example.fixed.ConstraintErrorExample", "main", 1)
                .withTPs(ConstraintError.class, 1)
                .withTPs(IncompleteOperationError.class, 1)
                .build());

        addErrorSpecification(new ErrorSpecification.Builder("example.fixed.PredicateMissingExample", "main", 1)
                .withTPs(ConstraintError.class, 1)
                .build());

        scanner.run();
        assertErrors(scanner.getCollectedErrors());
    }

    @Test
    public void cryptoMisuseExampleProject() {
        String mavenProjectPath = new File("../CryptoAnalysisTargets/CryptoMisuseExamples").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(new ErrorSpecification.Builder("main.Msg", "sign", 1)
                .withTPs(ConstraintError.class, 1)
                .withTPs(RequiredPredicateError.class, 1)
                .build());
        addErrorSpecification(new ErrorSpecification.Builder("main.Msg", "getPrivateKey", 0)
                .withTPs(ConstraintError.class, 1)
                .withTPs(RequiredPredicateError.class, 2)
                .build());

        addErrorSpecification(new ErrorSpecification.Builder("main.Msg", "encryptAlgFromField", 0)
                .withTPs(ConstraintError.class, 1)
                .withTPs(IncompleteOperationError.class, 1)
                .build());

        addErrorSpecification(new ErrorSpecification.Builder("main.Msg", "encrypt", 0)
				.withTPs(ConstraintError.class, 1)
				.withTPs(IncompleteOperationError.class, 1)
				.build());

        addErrorSpecification(new ErrorSpecification.Builder("main.Msg", "encryptAlgFromVar", 0)
				.withTPs(ConstraintError.class, 1)
				.withTPs(IncompleteOperationError.class, 1)
				.build());

        addErrorSpecification(new ErrorSpecification.Builder("main.Encrypt", "incorrectBigInteger", 0)
				.withTPs(ConstraintError.class, 2)
				.withTPs(RequiredPredicateError.class, 1)
				.build());

        addErrorSpecification(new ErrorSpecification.Builder("main.Encrypt", "incorrect", 0)
				.withTPs(ConstraintError.class, 1)
				.withTPs(RequiredPredicateError.class, 2)
				.build());

        scanner.run();
        assertErrors(scanner.getCollectedErrors());
    }

    @Test
    public void glassfishExample() {
        String mavenProjectPath = new File("../CryptoAnalysisTargets/glassfish-embedded").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(new ErrorSpecification.Builder("org.glassfish.grizzly.config.ssl.CustomClass", "init", 2)
                .withTPs(ConstraintError.class, 1)
                .withTPs(RequiredPredicateError.class, 1)
                .build());

        addErrorSpecification(new ErrorSpecification.Builder("org.glassfish.grizzly.config.ssl.JSSESocketFactory", "getStore", 3)
                .withTPs(HardCodedError.class, 1)
                .withTPs(NeverTypeOfError.class, 1)
                .withTPs(ImpreciseValueExtractionError.class, 1)
                .build());

        scanner.run();
        assertErrors(scanner.getCollectedErrors());
    }

    @Test
    public void oracleExample() {
        String mavenProjectPath = new File("../CryptoAnalysisTargets/OracleExample").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(new ErrorSpecification.Builder("main.Main", "main", 1)
                .withTPs(ConstraintError.class, 2)
                .withTPs(TypestateError.class, 1)
                .withTPs(NeverTypeOfError.class, 1)
                .withTPs(RequiredPredicateError.class, 3)
                .build());

        addErrorSpecification(new ErrorSpecification.Builder("main.Main", "keyStoreExample", 0)
                .withTPs(ConstraintError.class, 1)
                .withTPs(NeverTypeOfError.class, 1)
                .withTPs(HardCodedError.class, 1)
                .build());

        addErrorSpecification(new ErrorSpecification.Builder("main.Main", "cipherUsageExample", 0)
                .withTPs(ConstraintError.class, 1)
                .build());

        addErrorSpecification(new ErrorSpecification.Builder("main.Main", "use", 1)
                .withTPs(TypestateError.class, 1)
                .build());

        addErrorSpecification(new ErrorSpecification.Builder("Crypto.PWHasher", "verifyPWHash", 2)
                .withFPs(RequiredPredicateError.class, 3, "This is a spurious finding. What happens here?")
                .build());

        addErrorSpecification(new ErrorSpecification.Builder("main.Main", "incorrectKeyForWrongCipher", 0)
                .withTPs(ConstraintError.class, 1)
                .build());

        addErrorSpecification(new ErrorSpecification.Builder("main.Main", "useWrongDoFinal", 0)
                .withTPs(ConstraintError.class, 1)
                .withTPs(TypestateError.class, 1)
                .build());

        addErrorSpecification(new ErrorSpecification.Builder("main.Main", "useCorrectDoFinal", 0)
                .withTPs(ConstraintError.class, 1)
                .build());

        addErrorSpecification(new ErrorSpecification.Builder("main.Main", "useNoDoFinal", 0)
                .withTPs(ConstraintError.class, 1)
                .withTPs(IncompleteOperationError.class, 1)
                .build());

        addErrorSpecification(new ErrorSpecification.Builder("main.Main", "useDoFinalInLoop", 0)
                .withTPs(ConstraintError.class, 1)
                .withTPs(IncompleteOperationError.class, 1)
                .build());

        addErrorSpecification(new ErrorSpecification.Builder("main.Main", "interproceduralTypestate", 0)
                .withTPs(ConstraintError.class, 1)
                .build());

        scanner.run();
        assertErrors(scanner.getCollectedErrors());
    }

    @Test
    public void predicateInstanceOfExample() {
        String mavenProjectPath = new File("../CryptoAnalysisTargets/PredicateInstanceOfExample").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(new ErrorSpecification.Builder("crypto.CipherExample", "cipherExampleOne", 0)
                .withTPs(ConstraintError.class, 1)
                .build());
        addErrorSpecification(new ErrorSpecification.Builder("crypto.CipherExample", "cipherExampleTwo", 0)
                .withTPs(ConstraintError.class, 1)
                .build());

        scanner.run();
        assertErrors(scanner.getCollectedErrors());
    }

    @Test
    public void hardCodedExample() {
        String mavenProjectPath = new File("../CryptoAnalysisTargets/HardcodedTestExamples/").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(new ErrorSpecification.Builder("TruePositive", "getKey", 4)
                .withTPs(HardCodedError.class, 1)
                .withTPs(RequiredPredicateError.class, 2)
                .build());

        addErrorSpecification(new ErrorSpecification.Builder("TrueNegative", "getKey", 4)
                .withTPs(HardCodedError.class, 0)
                .withTPs(RequiredPredicateError.class, 0)
                .build());

        scanner.run();
        assertErrors(scanner.getCollectedErrors());
    }

    @Test
    public void sslExample() {
        String mavenProjectPath = new File("../CryptoAnalysisTargets/SSLMisuseExample").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        scanner.run();
        assertErrors(scanner.getCollectedErrors());
    }
}
