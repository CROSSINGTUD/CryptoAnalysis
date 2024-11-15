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
import scanner.setup.ErrorSpecification;
import scanner.setup.MavenProject;
import scanner.setup.AbstractHeadlessTest;

import java.io.File;

/**
 * The following Headless tests are deducted from the CryptoGuard benchmarking tool
 * that detects various Cryptographic API misuses. For the creating these Headless
 * tests, various projects from CryptoGuard were considered and can be found in the
 * following link:
 *
 * @author Enri Ozuni
 * @see <a href="https://github.com/CryptoGuardOSS/cryptoapi-bench">https://github.com/CryptoGuardOSS/cryptoapi-bench</a>
 */
public class CryptoGuardTest extends AbstractHeadlessTest {

    @Test
    public void brokenCryptoExamples() {
        String mavenProjectPath = new File("../CryptoAnalysisTargets/CryptoGuardExamples/brokencrypto").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/brokencrypto/BrokenCryptoABICase1.java
        addErrorSpecification(new ErrorSpecification.Builder("example.brokencrypto.BrokenCryptoABICase1", "doCrypto", 1)
                .withTPs(ConstraintError.class, 1)
                .withTPs(IncompleteOperationError.class, 1)
                .build());

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/brokencrypto/BrokenCryptoABICase2.java
        addErrorSpecification(new ErrorSpecification.Builder("example.brokencrypto.BrokenCryptoABICase2", "doCrypto", 1)
                .withTPs(ConstraintError.class, 2)
                .withTPs(RequiredPredicateError.class, 2)
                .withTPs(IncompleteOperationError.class, 1)
                .build());
        // ABICase3, ABICase4, ABICase9 not included as tests due to being similar to ABICase1 and ABICase2 above

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/brokencrypto/BrokenCryptoABICase5.java
        addErrorSpecification(new ErrorSpecification.Builder("example.brokencrypto.BrokenCryptoABICase5", "doCrypto", 0)
                .withTPs(ConstraintError.class, 1)
                .withTPs(RequiredPredicateError.class, 2)
                .withTPs(IncompleteOperationError.class, 1)
                .withTPs(ImpreciseValueExtractionError.class, 1)
                .build());
        // ABICase6, -7, -8, -10 not included as tests due to being similar to ABICase5 above

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/brokencrypto/BrokenCryptoBBCase3.java
        addErrorSpecification(new ErrorSpecification.Builder("example.brokencrypto.BrokenCryptoBBCase3", "go", 0)
                .withTPs(ConstraintError.class, 2)
                .withTPs(RequiredPredicateError.class, 2)
                .withTPs(IncompleteOperationError.class, 1)
                .build());
        // BBCase1, BBCase2, BBCase4, BBCase5 not included as tests due to being similar to BBCase3 above

        // Test cases regarding ecbcrypto project were not added, due to being similar to
        // all the test cases above:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/tree/master/src/main/java/org/cryptoapi/bench/ecbcrypto

        scanner.run();
        assertErrors(scanner.getCollectedErrors());
    }

    @Test
    public void brokenHashExamples() {
        String mavenProjectPath = new File("../CryptoAnalysisTargets/CryptoGuardExamples/brokenhash").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/brokenhash/BrokenHashABICase1.java
        addErrorSpecification(new ErrorSpecification.Builder("example.brokenhash.BrokenHashABICase1", "go", 2)
                .withTPs(ConstraintError.class, 1)
                .build());
        // ABICase2, -3, -4 not included as tests due to being similar to ABICase1 above

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/brokenhash/BrokenHashABICase5.java
        addErrorSpecification(new ErrorSpecification.Builder("example.brokenhash.BrokenHashABICase5", "go", 1)
                .withTPs(ImpreciseValueExtractionError.class, 1)
                .build());
        // ABICase6, -7, -8 not included as tests due to being similar to ABICase5 above

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/brokenhash/BrokenHashBBCase2.java
        addErrorSpecification(new ErrorSpecification.Builder("example.brokenhash.BrokenHashBBCase2", "main", 1)
                .withTPs(ConstraintError.class, 1)
                .build());
        // BBCase1, -3, -4 not included due to being similar to BBCase2 above

        scanner.run();
        assertErrors(scanner.getCollectedErrors());
    }

    @Test
    public void ecbCryptoExamples() {
        String mavenProjectPath = new File("../CryptoAnalysisTargets/CryptoGuardExamples/ecbcrypto").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/ecbcrypto/EcbInSymmCryptoABICase1.java
        addErrorSpecification(new ErrorSpecification.Builder("example.ecbcrypto.EcbInSymmCryptoABICase1", "go", 1)
                .withTPs(ConstraintError.class, 1)
                .withTPs(IncompleteOperationError.class, 1)
                .build());
        // ABICase3 not included as tests due to being similar to ABICase1 above

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/ecbcrypto/EcbInSymmCryptoABICase2.java
        addErrorSpecification(new ErrorSpecification.Builder("example.ecbcrypto.EcbInSymmCryptoABICase2", "go", 0)
                .withTPs(IncompleteOperationError.class, 1)
                .withTPs(ImpreciseValueExtractionError.class, 1)
                .build());
        // ABICase3 not included as tests due to being similar to ABICase2 above

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/ecbcrypto/EcbInSymmCryptoBBCase1.java
        addErrorSpecification(new ErrorSpecification.Builder("example.ecbcrypto.EcbInSymmCryptoBBCase1", "go", 0)
                .withTPs(ConstraintError.class, 1)
                .withTPs(IncompleteOperationError.class, 1)
                .build());

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/ecbcrypto/EcbInSymmCryptoCorrected.java
        addErrorSpecification(new ErrorSpecification.Builder("example.ecbcrypto.EcbInSymmCryptoCorrected", "go", 0)
                .withTPs(ConstraintError.class, 1)
                .withTPs(IncompleteOperationError.class, 1)
                .build());

        scanner.run();
        assertErrors(scanner.getCollectedErrors());
    }

    @Test
    public void insecureAsymmetricCryptoExamples() {
        String mavenProjectPath = new File("../CryptoAnalysisTargets/CryptoGuardExamples/insecureasymmetriccrypto").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/insecureasymmetriccrypto/InsecureAsymmetricCipherABICase1.java
        addErrorSpecification(new ErrorSpecification.Builder("example.insecureasymmetriccrypto.InsecureAsymmetricCipherABICase1", "main", 1)
                .withTPs(TypestateError.class, 1)
                .withTPs(RequiredPredicateError.class, 1)
                .build());
        addErrorSpecification(new ErrorSpecification.Builder("example.insecureasymmetriccrypto.InsecureAsymmetricCipherABICase1", "go", 2)
                .withTPs(IncompleteOperationError.class, 2)
                .withTPs(RequiredPredicateError.class, 4)
                .build());

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/insecureasymmetriccrypto/InsecureAsymmetricCipherABICase2.java
        addErrorSpecification(new ErrorSpecification.Builder("example.insecureasymmetriccrypto.InsecureAsymmetricCipherABICase2", "main", 1)
                .withTPs(RequiredPredicateError.class, 1)
                .withTPs(ImpreciseValueExtractionError.class, 1)
                .build());
        addErrorSpecification(new ErrorSpecification.Builder("example.insecureasymmetriccrypto.InsecureAsymmetricCipherABICase2", "go", 2)
                .withTPs(IncompleteOperationError.class, 2)
                .withTPs(RequiredPredicateError.class, 4)
                .build());
        // In the case above, misuse is caught correctly, but the keysize is reported to be 0
        // and not 1024, as it really is. This is caused because of the structure of the project
        // as explained in the issue: https://github.com/CROSSINGTUD/CryptoAnalysis/issues/163

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/insecureasymmetriccrypto/InsecureAsymmetricCipherBB Case1.java
        addErrorSpecification(new ErrorSpecification.Builder("example.insecureasymmetriccrypto.InsecureAsymmetricCipherBBCase1", "go", 0)
                .withTPs(ConstraintError.class, 1)
                .withTPs(IncompleteOperationError.class, 2)
                .withTPs(RequiredPredicateError.class, 5)
                .build());

        scanner.run();
        assertErrors(scanner.getCollectedErrors());
    }

    @Test
    public void pbeIterationExamples() {
        String mavenProjectPath = new File("../CryptoAnalysisTargets/CryptoGuardExamples/pbeiteration").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/pbeiteration/LessThan1000IterationPBEABICase1.java
        addErrorSpecification(new ErrorSpecification.Builder("example.pbeiteration.LessThan1000IterationPBEABICase1", "key2", 1)
                .withTPs(ConstraintError.class, 1)
                .build());

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/pbeiteration/LessThan1000IterationPBEBBCase1.java
        addErrorSpecification(new ErrorSpecification.Builder("example.pbeiteration.LessThan1000IterationPBEBBCase1", "key2", 0)
                .withTPs(ConstraintError.class, 1)
                .build());

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/pbeiteration/LessThan1000IterationPBEABHCase1.java
        addErrorSpecification(new ErrorSpecification.Builder("example.pbeiteration.LessThan1000IterationPBEABHCase1", "key2", 0)
                .withTPs(ImpreciseValueExtractionError.class, 1)
                .build());

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/pbeiteration/LessThan1000IterationPBEABICase2.java
        addErrorSpecification(new ErrorSpecification.Builder("example.pbeiteration.LessThan1000IterationPBEABICase2", "key2", 0)
                .withTPs(ImpreciseValueExtractionError.class, 1)
                .build());
        // In the case above, misuse is caught correctly, but the count is reported to be 0
        // and not 20, as it really is. This is caused because of the structure of the project
        // as explained in the issue: https://github.com/CROSSINGTUD/CryptoAnalysis/issues/163

        scanner.run();
        assertErrors(scanner.getCollectedErrors());
    }

    @Test
    public void predictableCryptographicKeyExamples() {
        String mavenProjectPath = new File("../CryptoAnalysisTargets/CryptoGuardExamples/predictablecryptographickey").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/predictablecryptographickey/PredictableCryptographicKeyABHCase1.java
        addErrorSpecification(new ErrorSpecification.Builder("example.predictablecryptographickey.PredictableCryptographicKeyABHCase1", "main", 1)
                .withTPs(RequiredPredicateError.class, 1)
                .build());

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/predictablecryptographickey/PredictableCryptographicKeyABICase1.java
        addErrorSpecification(new ErrorSpecification.Builder("example.predictablecryptographickey.PredictableCryptographicKeyABICase1", "go", 1)
                .withTPs(RequiredPredicateError.class, 1)
                .build());

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/predictablecryptographickey/PredictableCryptographicKeyBBCase1.java
        addErrorSpecification(new ErrorSpecification.Builder("example.predictablecryptographickey.PredictableCryptographicKeyBBCase1", "main", 1)
                .withTPs(RequiredPredicateError.class, 1)
                .build());

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/predictablecryptographickey/PredictableCryptographicKeyABHCase2.java
        addErrorSpecification(new ErrorSpecification.Builder("example.predictablecryptographickey.PredictableCryptographicKeyABHCase2", "main", 1)
                .withTPs(RequiredPredicateError.class, 1)
                .build());

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/predictablecryptographickey/PredictableCryptographicKeyABICase2.java
        addErrorSpecification(new ErrorSpecification.Builder("example.predictablecryptographickey.PredictableCryptographicKeyABICase2", "go", 0)
                .withTPs(RequiredPredicateError.class, 1)
                .build());

        scanner.run();
        assertErrors(scanner.getCollectedErrors());
    }

    @Test
    public void predictableKeyStorePasswordExamples() {
        String mavenProjectPath = new File("../CryptoAnalysisTargets/CryptoGuardExamples/predictablekeystorepassword").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/predictablekeystorepassword/PredictableKeyStorePasswordABICase1.java
        addErrorSpecification(new ErrorSpecification.Builder("example.predictablekeystorepassword.PredictableKeyStorePasswordABICase1", "go", 1)
                .withTPs(HardCodedError.class, 1)
                .withTPs(NeverTypeOfError.class, 1)
                .build());
        // ABH1, ABI2, BB1 are other similar test cases that were not included

        scanner.run();
        assertErrors(scanner.getCollectedErrors());
    }

    @Test
    public void predictablePBEPasswordExamples() {
        String mavenProjectPath = new File("../CryptoAnalysisTargets/CryptoGuardExamples/predictablepbepassword").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/predictablepbepassword/PredictablePBEPasswordBBCase2.java
        addErrorSpecification(new ErrorSpecification.Builder("example.predictablepbepassword.PredictablePBEPasswordBBCase2", "main", 1)
                .withTPs(IncompleteOperationError.class, 1)
                .build());
        addErrorSpecification(new ErrorSpecification.Builder("example.predictablepbepassword.PredictablePBEPasswordBBCase2", "key", 0)
                .withTPs(HardCodedError.class, 1)
                .build());

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/predictablepbepassword/PredictablePBEPasswordABHCase2.java
        addErrorSpecification(new ErrorSpecification.Builder("example.predictablepbepassword.PredictablePBEPasswordABHCase2", "main", 1)
                .withTPs(IncompleteOperationError.class, 1)
                .build());
        addErrorSpecification(new ErrorSpecification.Builder("example.predictablepbepassword.PredictablePBEPasswordABHCase2", "key", 1)
                .withTPs(NeverTypeOfError.class, 1)
                .withTPs(HardCodedError.class, 1)
                .build());

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/predictablepbepassword/PredictablePBEPasswordABICase1.java
        addErrorSpecification(new ErrorSpecification.Builder("example.predictablepbepassword.PredictablePBEPasswordABICase1", "main", 1)
                .withTPs(IncompleteOperationError.class, 1)
                .build());
        addErrorSpecification(new ErrorSpecification.Builder("example.predictablepbepassword.PredictablePBEPasswordABICase1", "key", 1)
                .withTPs(NeverTypeOfError.class, 1)
                .withTPs(HardCodedError.class, 1)
                .build());
        // ABHCase1, BBCase1 are similar to the case above

        scanner.run();
        assertErrors(scanner.getCollectedErrors());
    }

    // TODO
    // Headless test cases regarding the CryptoGuard projects `predictableseeds`
    // were not added due to the misuses not being caught by the analysis.
    // The projects are CryptoGuard projects are found in the link below:
    // https://github.com/CryptoGuardOSS/cryptoapi-bench/tree/master/src/main/java/org/cryptoapi/bench/predictableseeds
    // For the misuse an issue is opened and can be found in the link below:
    // https://github.com/CROSSINGTUD/CryptoAnalysis/issues/140

    @Test
    public void staticInitializationVectorExamples() {
        String mavenProjectPath = new File("../CryptoAnalysisTargets/CryptoGuardExamples/staticinitializationvector").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/staticinitializationvector/StaticInitializationVectorABHCase1.java
        addErrorSpecification(new ErrorSpecification.Builder("example.staticinitializationvector.StaticInitializationVectorABHCase1", "go", 0)
                .withTPs(ConstraintError.class, 1)
                .withTPs(RequiredPredicateError.class, 1)
                .withTPs(IncompleteOperationError.class, 1)
                .build());

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/staticinitializationvector/StaticInitializationVectorABHCase2.java
        addErrorSpecification(new ErrorSpecification.Builder("example.staticinitializationvector.StaticInitializationVectorABHCase2", "go", 0)
                .withTPs(ConstraintError.class, 1)
                .withTPs(RequiredPredicateError.class, 1)
                .withTPs(IncompleteOperationError.class, 1)
                .build());

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/staticinitializationvector/StaticInitializationVectorABICase1.java
        addErrorSpecification(new ErrorSpecification.Builder("example.staticinitializationvector.StaticInitializationVectorABICase1", "go", 1)
                .withTPs(ConstraintError.class, 1)
                .withTPs(IncompleteOperationError.class, 1)
                .build());
        addErrorSpecification(new ErrorSpecification.Builder("example.staticinitializationvector.StaticInitializationVectorABICase1", "main", 1)
                .withTPs(RequiredPredicateError.class, 1)
                .build());

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/staticinitializationvector/StaticInitializationVectorABICase2.java
        addErrorSpecification(new ErrorSpecification.Builder("example.staticinitializationvector.StaticInitializationVectorABICase2", "go", 1)
                .withTPs(ConstraintError.class, 1)
                .withTPs(IncompleteOperationError.class, 1)
                .build());
        addErrorSpecification(new ErrorSpecification.Builder("example.staticinitializationvector.StaticInitializationVectorABICase2", "main", 1)
                .withTPs(RequiredPredicateError.class, 1)
                .build());

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/staticinitializationvector/StaticInitializationVectorBBCase1.java
        addErrorSpecification(new ErrorSpecification.Builder("example.staticinitializationvector.StaticInitializationVectorBBCase1", "go", 0)
                .withTPs(ConstraintError.class, 1)
                .withTPs(RequiredPredicateError.class, 1)
                .withTPs(IncompleteOperationError.class, 1)
                .build());

        scanner.run();
        assertErrors(scanner.getCollectedErrors());
    }

    @Test
    public void staticSaltsExamples() {
        String mavenProjectPath = new File("../CryptoAnalysisTargets/CryptoGuardExamples/staticsalts").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/staticsalts/StaticSaltsABHCase1.java
        addErrorSpecification(new ErrorSpecification.Builder("example.staticsalts.StaticSaltsABHCase1", "key2", 0)
                .withTPs(ConstraintError.class, 1)
                .withTPs(RequiredPredicateError.class, 1)
                .build());

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/staticsalts/StaticSaltsABICase2.java
        addErrorSpecification(new ErrorSpecification.Builder("example.staticsalts.StaticSaltsABICase2", "key2", 1)
                .withTPs(ConstraintError.class, 1)
                .withTPs(RequiredPredicateError.class, 1)
                .build());

        // This test case corresponds to the following project in CryptoGuard:
        // https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/staticsalts/StaticSaltsBBCase1.java
        addErrorSpecification(new ErrorSpecification.Builder("example.staticsalts.StaticSaltsBBCase1", "key2", 0)
                .withTPs(ConstraintError.class, 1)
                .withTPs(RequiredPredicateError.class, 1)
                .build());
        // ABICase1 is similar to the examples above

        scanner.run();
        assertErrors(scanner.getCollectedErrors());
    }
}
