package scanner.targets;

import crypto.analysis.errors.AlternativeReqPredicateError;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.ForbiddenMethodError;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import de.fraunhofer.iem.scanner.HeadlessJavaScanner;
import java.io.File;
import org.junit.Ignore;
import org.junit.Test;
import scanner.setup.AbstractHeadlessTest;
import scanner.setup.ErrorSpecification;
import scanner.setup.MavenProject;

/**
 * The following headless tests are deducted from the Braga et al. paper which benchmarks several
 * static analyzer tools against several hundred test projects containing various Cryptographic
 * providers of the JCA framework. For the creation of these headless tests, various projects from
 * Braga paper were considered and used for testing the provider detection functionality. The test
 * projects of the paper can be found in the link below: <a
 * href="https://bitbucket.org/alexmbraga/cryptomisuses/src/master/">BragaCryptoMisuses</a>
 *
 * @author Enri Ozuni
 */
public class BragaCryptoMisusesTest extends AbstractHeadlessTest {

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/wc/brokenInsecureHash/
    @Test
    public void brokenInsecureHashExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/brokenInsecureHash")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("wc.brokenInsecureHash.InsecureHashes1", "main", 1)
                        .withTPs(ConstraintError.class, 3)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("wc.brokenInsecureHash.InsecureHashes2", "main", 1)
                        .withTPs(IncompleteOperationError.class, 1)
                        .withTPs(ImpreciseValueExtractionError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("wc.brokenInsecureHash.InsecureHashes3", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("wc.brokenInsecureHash.InsecureHashes4", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("wc.brokenInsecureHash.InsecureHashes5", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/wc/brokenInsecureMAC/
    @Test
    public void brokenInsecureMACExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/brokenInsecureMAC")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("wc.brokenInsecureMAC.InsecureMAC1", "main", 1)
                        .withTPs(ConstraintError.class, 8)
                        .withTPs(RequiredPredicateError.class, 8)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("wc.brokenInsecureMAC.InsecureMAC2", "main", 1)
                        .withTPs(ImpreciseValueExtractionError.class, 4)
                        .withTPs(RequiredPredicateError.class, 4)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("wc.brokenInsecureMAC.InsecureMAC3", "main", 1)
                        .withTPs(ConstraintError.class, 4)
                        .withTPs(RequiredPredicateError.class, 4)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("wc.brokenInsecureMAC.InsecureMAC4", "main", 1)
                        .withTPs(ConstraintError.class, 4)
                        .withTPs(RequiredPredicateError.class, 4)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/icv/brokenSSLorTLS/
    @Test
    public void brokenSSLorTLSExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/brokenSSLorTLS")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "icv.brokenSSLorTLS.SSLctxNoKeyMgmtNoTrustMgmt1", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "icv.brokenSSLorTLS.SSLctxNoKeyMgmtNoTrustMgmt2", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "icv.brokenSSLorTLS.SSLctxNoKeyMgmtNoTrustMgmt3", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "icv.brokenSSLorTLS.SSLctxNoKeyMgmtNoTrustMgmt4", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "icv.brokenSSLorTLS.SSLctxNoKeyMgmtNoTrustMgmt5", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "icv.brokenSSLorTLS.SSLctxNoKeyMgmtNoTrustMgmt6", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/cib/buggyIVgen/
    @Test
    public void buggyIVGenExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/buggyIVgen")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("cib.buggyIVgen.BuggyIVGen1", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(TypestateError.class, 2)
                        .withTPs(RequiredPredicateError.class, 2)
                        .withTPs(IncompleteOperationError.class, 2)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("cib.buggyIVgen.BuggyIVGen2", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(TypestateError.class, 2)
                        .withTPs(RequiredPredicateError.class, 2)
                        .withTPs(IncompleteOperationError.class, 2)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/ivm/constantIV/
    @Test
    public void constantIVExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/constantIV")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("ivm.constantIV.FixedIV1", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(IncompleteOperationError.class, 2)
                        .withTPs(TypestateError.class, 2)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("ivm.constantIV.FixedIV2", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(IncompleteOperationError.class, 2)
                        .withTPs(TypestateError.class, 2)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("ivm.constantIV.SimpleIVConstant", "main", 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pkm/constantKey/
    @Test
    public void constantKeyExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/constantKey")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("pkm.constantKey.ConstantKey3DES", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 2)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("pkm.constantKey.ConstantKeyAES1", "main", 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("pkm.constantKey.ConstantKeyAES2", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("pkm.constantKey.ConstantKeyAES3", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("pkm.constantKey.HardCodedKey", "main", 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("pkm.constantKey.ConstantKeyforMAC", "main", 1)
                        .withTPs(RequiredPredicateError.class, 4)
                        .withTPs(TypestateError.class, 1)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pkm/constPwd4PBE/
    @Test
    public void constPwd4PBEExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/constPwd4PBE")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("pkm.constPwd4PBE.ConstPassword4PBE1", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 2)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 2)
                        .withTPs(ForbiddenMethodError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("pkm.constPwd4PBE.ConstPassword4PBE2", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 2)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 2)
                        .withTPs(ForbiddenMethodError.class, 1)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/wc/customCrypto/
    @Test
    public void customCryptoExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/customCrypto")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("wc.customCrypto.Manual3DES", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 2)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "wc.customCrypto.RawSignatureRSA", "positiveTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "wc.customCrypto.RawSignatureRSA", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(TypestateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("wc.customCrypto.RawSignatureRSAwHash", "main", 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pkc/enc/deterministicCrypto/
    @Test
    public void deterministicCryptoExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/deterministicCrypto")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.enc.deterministicCrypto.DeterministicEncryptionRSA", "main", 1)
                        .withTPs(ConstraintError.class, 3)
                        .withTPs(RequiredPredicateError.class, 3)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(IncompleteOperationError.class, 2)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.enc.deterministicCrypto.DeterministicEncryptionRSA1",
                                "main",
                                1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 3)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(IncompleteOperationError.class, 2)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.enc.deterministicCrypto.DeterministicEncryptionRSA2",
                                "main",
                                1)
                        .withTPs(ConstraintError.class, 3)
                        .withTPs(RequiredPredicateError.class, 3)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(IncompleteOperationError.class, 2)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.enc.deterministicCrypto.DeterministicEncryptionRSA3",
                                "main",
                                1)
                        .withTPs(ConstraintError.class, 3)
                        .withTPs(RequiredPredicateError.class, 3)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(IncompleteOperationError.class, 2)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/wc/deterministicSymEnc/
    @Test
    public void deterministicSymEncExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/deterministicSymEnc")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "wc.deterministicSymEnc.DeterministicEncryptionAESwECB1", "main", 1)
                        .withTPs(ConstraintError.class, 6)
                        .withTPs(IncompleteOperationError.class, 6)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "wc.deterministicSymEnc.DeterministicEncryptionAESwECB2", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(IncompleteOperationError.class, 2)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/br/fixedSeed/
    @Test
    public void fixedSeedExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/fixedSeed")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("br.fixedSeed.FixedSeed1", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 6)
                        .withTPs(TypestateError.class, 4)
                        .withTPs(IncompleteOperationError.class, 4)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("br.fixedSeed.FixedSeed2", "main", 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("br.fixedSeed.FixedSeed3", "main", 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("br.fixedSeed.FixedSeed4", "main", 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pkm/ImproperKeyLen/
    @Test
    public void improperKeyLenExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/ImproperKeyLen")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("pkm.ImproperKeyLen.ImproperKeySizeRSA1", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("pkm.ImproperKeyLen.ImproperKeySizeRSA2", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("pkm.ImproperKeyLen.ImproperKeySizeRSA3", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("pkm.ImproperKeyLen.ImproperKeySizeRSA4", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("pkm.ImproperKeyLen.ImproperKeySizeRSA5", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("pkm.ImproperKeyLen.ImproperKeySizeRSA6", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/icv/incompleteValidation/
    @Ignore("Boomerang is causing an Assertion error")
    @Test
    public void incompleteValidationExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/incompleteValidation")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "icv.incompleteValidation.ValidateCertChainButNoCRL", "name", 1)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pdf/insecureComboHashEnc/
    @Test
    public void insecureComboHashEncExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/insecureComboHashEnc")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pdf.insecureComboHashEnc.ManualComboEncryptAndHash", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pdf.insecureComboHashEnc.ManualComboEncryptThenHash1", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pdf.insecureComboHashEnc.ManualComboEncryptThenHash2", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pdf.insecureComboHashEnc.ManualComboHashThenEncrypt", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pdf/insecureComboMacEnc/
    @Test
    public void insecureComboMacEncExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/insecureComboMacEnc")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pdf.insecureComboMacEnc.ManualComboEncryptAndMAC", "main", 1)
                        .withTPs(RequiredPredicateError.class, 3)
                        .withTPs(TypestateError.class, 2)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pdf.insecureComboMacEnc.ManualComboEncryptThenMAC1", "main", 1)
                        .withTPs(RequiredPredicateError.class, 3)
                        .withTPs(TypestateError.class, 2)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pdf.insecureComboMacEnc.ManualComboEncryptThenMAC2", "main", 1)
                        .withTPs(RequiredPredicateError.class, 3)
                        .withTPs(TypestateError.class, 2)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pdf.insecureComboMacEnc.ManualComboMACThenEncrypt", "main", 1)
                        .withTPs(RequiredPredicateError.class, 3)
                        .withTPs(TypestateError.class, 2)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pkc/ecc/insecurecurves/
    @Test
    public void insecureCurvesExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/insecurecurves")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.ecc.insecurecurves.InsecureCurve_secp112r1",
                                "negativeTestCase",
                                0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.ecc.insecurecurves.InsecureCurve_secp128r1",
                                "negativeTestCase",
                                0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.ecc.insecurecurves.InsecureCurve_secp160k1",
                                "negativeTestCase",
                                0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.ecc.insecurecurves.InsecureCurve_secp160r1",
                                "negativeTestCase",
                                0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.ecc.insecurecurves.InsecureCurve_sect113r1",
                                "negativeTestCase",
                                0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.ecc.insecurecurves.InsecureCurve_sect131r1",
                                "negativeTestCase",
                                0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.ecc.insecurecurves.InsecureCurve_sect193r1",
                                "negativeTestCase",
                                0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.ecc.insecurecurves.InsecureCurveECDH1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pdf/insecureDefault/
    @Test
    public void insecureDefaultExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/insecureDefault")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("pdf.insecureDefault.InsecureDefaultPBE", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(ForbiddenMethodError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("pdf.insecureDefault.InsecureDefault3DES", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("pdf.insecureDefault.InsecureDefaultAES", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pdf.insecureDefault.InsecureDefaultOAEP", "positiveTestCase", 0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pdf.insecureDefault.InsecureDefaultOAEP", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 3)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pdf.insecureDefault.InsecureDefaultRSA", "positiveTestCase", 0)
                        .withTPs(IncompleteOperationError.class, 2)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pdf.insecureDefault.InsecureDefaultRSA", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 3)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(IncompleteOperationError.class, 2)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pkc/enc/insecurePadding/
    @Test
    public void insecurePaddingExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/insecurePadding")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.enc.insecurePadding.InsecurePaddingRSA1",
                                "positiveTestCase",
                                0)
                        .withTPs(IncompleteOperationError.class, 2)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.enc.insecurePadding.InsecurePaddingRSA1",
                                "negativeTestCase",
                                0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 3)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(IncompleteOperationError.class, 2)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.enc.insecurePadding.InsecurePaddingRSA2",
                                "positiveTestCase",
                                0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(IncompleteOperationError.class, 2)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.enc.insecurePadding.InsecurePaddingRSA2",
                                "negativeTestCase",
                                0)
                        .withTPs(ConstraintError.class, 3)
                        .withTPs(RequiredPredicateError.class, 3)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(IncompleteOperationError.class, 2)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.enc.insecurePadding.InsecurePaddingRSA3",
                                "positiveTestCase",
                                0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(IncompleteOperationError.class, 2)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.enc.insecurePadding.InsecurePaddingRSA3",
                                "negativeTestCase",
                                0)
                        .withTPs(ConstraintError.class, 3)
                        .withTPs(RequiredPredicateError.class, 3)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(IncompleteOperationError.class, 2)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pkc/sign/insecurePadding/
    @Test
    public void insecurePaddingSignExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/insecurePaddingSign")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.sign.insecurePaddingSign.PKCS1Signature",
                                "positiveTestCase",
                                0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.sign.insecurePaddingSign.PKCS1Signature",
                                "negativeTestCase",
                                0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.sign.insecurePaddingSign.PKCS1Signature1",
                                "positiveTestCase",
                                0)
                        .withNoErrors(ConstraintError.class)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.sign.insecurePaddingSign.PKCS1Signature1",
                                "negativeTestCase",
                                0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.sign.insecurePaddingSign.PKCS1Signature2",
                                "positiveTestCase",
                                0)
                        .withNoErrors(ConstraintError.class)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.sign.insecurePaddingSign.PKCS1Signature2",
                                "negativeTestCase",
                                0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.sign.insecurePaddingSign.PSSwSHA1Signature",
                                "positiveTestCase",
                                0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.sign.insecurePaddingSign.PSSwSHA1Signature",
                                "negativeTestCase",
                                0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.sign.insecurePaddingSign.PSSwSHA1Signature1",
                                "positiveTestCase",
                                0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.sign.insecurePaddingSign.PSSwSHA1Signature1",
                                "negativeTestCase",
                                0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.sign.insecurePaddingSign.PSSwSHA1Signature2",
                                "positiveTestCase",
                                0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.sign.insecurePaddingSign.PSSwSHA1Signature2",
                                "negativeTestCase",
                                0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(TypestateError.class, 1)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pdf/insecureStreamCipher/
    @Test
    public void insecureStreamCipherExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/insecureStreamCipher")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pdf.insecureStreamCipher.ConfusingBlockAndStream", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pdf.insecureStreamCipher.MalealableStreamCipher", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 3)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 2)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pkc/ka/issuesDHandECDH/
    @Test
    public void issuesDHandECDHExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/issuesDHandECDH")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.ka.issuesDHandECDH.NonAuthenticatedEphemeralDH_1024",
                                "positiveTestCase",
                                0)
                        .withNoErrors(ConstraintError.class)
                        .withNoErrors(RequiredPredicateError.class)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.ka.issuesDHandECDH.NonAuthenticatedEphemeralDH_1024",
                                "negativeTestCase",
                                0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 16)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.ka.issuesDHandECDH.NonAuthenticatedEphemeralDH_512", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 16)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.ka.issuesDHandECDH.NonAuthenticatedEphemeralECDH_112",
                                "main",
                                1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 16)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.ka.issuesDHandECDH.NonAuthenticatedEphemeralECDH_80",
                                "main",
                                1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 16)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.ka.issuesDHandECDH.NonAuthenticatedDH_512", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 18)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.ka.issuesDHandECDH.NonAuthenticatedDH_1024",
                                "positiveTestCase",
                                0)
                        .withTPs(RequiredPredicateError.class, 18)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.ka.issuesDHandECDH.NonAuthenticatedDH_1024",
                                "negativeTestCase",
                                0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 18)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pkm/keyReuseInStreamCipher/
    @Test
    public void keyReuseInStreamCipherExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/keyReuseInStreamCipher")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkm.keyReuseInStreamCipher.KeyReuseStreamCipher1", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkm.keyReuseInStreamCipher.KeyReuseStreamCipher2", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkm.keyReuseInStreamCipher.KeyReuseStreamCipher3", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkm.keyReuseInStreamCipher.KeyReuseStreamCipher4", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 6)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkm.keyReuseInStreamCipher.KeyReuseStreamCipher5", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/ivm/nonceReuse/
    @Test
    public void nonceReuseExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/nonceReuse")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("ivm.nonceReuse.NonceReuse1", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/cib/nullcipher/
    @Test
    public void nullCipherExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/nullcipher")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/cib/paramsPBE/
    @Test
    public void paramsPBEExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/paramsPBE")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("cib.paramsPBE.PBEwConstSalt1", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(ForbiddenMethodError.class, 1)
                        .withTPs(TypestateError.class, 2)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("cib.paramsPBE.PBEwSmallCount1", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(ForbiddenMethodError.class, 1)
                        .withTPs(TypestateError.class, 2)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("cib.paramsPBE.PBEwSmallSalt", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(ForbiddenMethodError.class, 1)
                        .withTPs(TypestateError.class, 2)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/br/predictableSeed/
    @Test
    public void predictableSeedExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/predictableSeed")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("br.predictableSeed.ReusedSeed", "main", 1)
                        .withTPs(RequiredPredicateError.class, 4)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("br.predictableSeed.LowEntropySeed1", "main", 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("br.predictableSeed.LowEntropySeed2", "main", 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("br.predictableSeed.LowEntropySeed3", "main", 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("br.predictableSeed.LowEntropySeed4", "main", 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/cib/printPrivSecKey/
    @Test
    public void printPrivSecKeyExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/printPrivSecKey")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("cib.printPrivSecKey.PrintECDHPrivKey1", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 16)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("cib.printPrivSecKey.PrintECDHSecret1", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 16)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("cib.printPrivSecKey.PrintECDSAPrivKey1", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 5)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "cib.printPrivSecKey.PrintPrivKey1", "positiveTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "cib.printPrivSecKey.PrintPrivKey1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 3)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("cib.printPrivSecKey.PrintSecKey1", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "cib.printPrivSecKey.PrintDHSecret1", "positiveTestCase", 0)
                        .withNoErrors(ConstraintError.class)
                        .withNoErrors(RequiredPredicateError.class)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "cib.printPrivSecKey.PrintDHSecret1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 16)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "cib.printPrivSecKey.PrintDHPrivKey1", "positiveTestCase", 0)
                        .withNoErrors(ConstraintError.class)
                        .withNoErrors(TypestateError.class)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "cib.printPrivSecKey.PrintDHPrivKey1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 16)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/wc/riskyInsecureCrypto/
    @Test
    public void riskyInsecureCryptoExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/riskyInsecureCrypto")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "wc.riskyInsecureCrypto.InsecureCryptoPBE", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(ForbiddenMethodError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "wc.riskyInsecureCrypto.InsecureCrypto3DES", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "wc.riskyInsecureCrypto.InsecureCryptoBlowfish", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "wc.riskyInsecureCrypto.InsecureCryptoDES", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "wc.riskyInsecureCrypto.InsecureCryptoDES_StreamCipher", "main", 1)
                        .withTPs(ConstraintError.class, 5)
                        .withTPs(RequiredPredicateError.class, 4)
                        .withTPs(AlternativeReqPredicateError.class, 8)
                        .withTPs(TypestateError.class, 5)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "wc.riskyInsecureCrypto.InsecureCryptoRC4_StreamCipher", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pdf/sideChannelAttacks/
    @Test
    public void sideChannelAttacksExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/sideChannelAttacks")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pdf.sideChannelAttacks.HashVerificationVariableTime", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pdf.sideChannelAttacks.MacVerificationVariableTime", "main", 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("pdf.sideChannelAttacks.PaddingOracle", "oracle", 2)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(AlternativeReqPredicateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pdf.sideChannelAttacks.PaddingOracle", "encripta", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(AlternativeReqPredicateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pdf.sideChannelAttacks.PaddingOracle", "<clinit>", 0)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/ivm/staticCounterCTR/
    @Test
    public void staticCounterCTRExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/staticCounterCTR")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("ivm.staticCounterCTR.StaticCounterCTR1", "main", 1)
                        .withTPs(RequiredPredicateError.class, 6)
                        .withTPs(TypestateError.class, 2)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/br/statisticPRNG/
    @Test
    public void statisticPRNGExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/statisticPRNG")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/cai/undefinedCSP/
    @Test
    public void undefinedCSPExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/undefinedCSP")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("cai.undefinedCSP.UndefinedProvider1", "main", 1)
                        .withTPs(IncompleteOperationError.class, 3)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("cai.undefinedCSP.UndefinedProvider2", "main", 1)
                        .withTPs(IncompleteOperationError.class, 3)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("cai.undefinedCSP.UndefinedProvider3", "main", 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("cai.undefinedCSP.UndefinedProvider4", "main", 1)
                        .withTPs(IncompleteOperationError.class, 4)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("cai.undefinedCSP.UndefinedProvider5", "main", 1)
                        .withTPs(IncompleteOperationError.class, 3)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("cai.undefinedCSP.UndefinedProvider6", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("cai.undefinedCSP.UndefinedProvider7", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("cai.undefinedCSP.UndefinedProvider8", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 3)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(IncompleteOperationError.class, 2)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pkc/enc/weakConfigsRSA/
    @Test
    public void weakConfigsRSAExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/weakConfigsRSA")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.enc.weakConfigsRSA.ImproperConfigRSA_384x160_1",
                                "positiveTestCase",
                                0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 4)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.enc.weakConfigsRSA.ImproperConfigRSA_384x160_1",
                                "negativeTestCase",
                                0)
                        .withTPs(ConstraintError.class, 3)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.enc.weakConfigsRSA.ImproperConfigRSA_512x160_1",
                                "positiveTestCase",
                                0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 4)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.enc.weakConfigsRSA.ImproperConfigRSA_512x160_1",
                                "negativeTestCase",
                                0)
                        .withTPs(ConstraintError.class, 3)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.enc.weakConfigsRSA.ImproperConfigRSA_768x160_1",
                                "positiveTestCase",
                                0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 4)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.enc.weakConfigsRSA.ImproperConfigRSA_768x160_1",
                                "negativeTestCase",
                                0)
                        .withTPs(ConstraintError.class, 3)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.enc.weakConfigsRSA.ImproperConfigRSA_768x256_1",
                                "positiveTestCase",
                                0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 4)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.enc.weakConfigsRSA.ImproperConfigRSA_768x256_1",
                                "negativeTestCase",
                                0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.enc.weakConfigsRSA.ImproperConfigRSA_1024x160_1",
                                "positiveTestCase",
                                0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 4)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.enc.weakConfigsRSA.ImproperConfigRSA_1024x160_1",
                                "negativeTestCase",
                                0)
                        .withTPs(ConstraintError.class, 3)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.enc.weakConfigsRSA.ImproperConfigRSA_1024x256_1",
                                "positiveTestCase",
                                0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 4)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.enc.weakConfigsRSA.ImproperConfigRSA_1024x256_1",
                                "negativeTestCase",
                                0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.enc.weakConfigsRSA.ImproperConfigRSA_1024x384_1",
                                "positiveTestCase",
                                0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 4)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.enc.weakConfigsRSA.ImproperConfigRSA_1024x384_1",
                                "negativeTestCase",
                                0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.enc.weakConfigsRSA.ImproperConfigRSA_2048x160_1",
                                "positiveTestCase",
                                0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 4)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.enc.weakConfigsRSA.ImproperConfigRSA_2048x160_1",
                                "negativeTestCase",
                                0)
                        .withTPs(ConstraintError.class, 3)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.enc.weakConfigsRSA.ImproperConfigRSA_3072x160_1",
                                "positiveTestCase",
                                0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.enc.weakConfigsRSA.ImproperConfigRSA_3072x160_1",
                                "negativeTestCase",
                                0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.enc.weakConfigsRSA.ImproperConfigRSA_4096x160_1",
                                "positiveTestCase",
                                0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.enc.weakConfigsRSA.ImproperConfigRSA_4096x160_1",
                                "negativeTestCase",
                                0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pkc/sign/weakSignatureECDSA/
    @Test
    public void weakSignatureECDSAExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/weakSignatureECDSA")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.sign.weakSignatureECDSA.RepeatedMessageNonceECDSA_1",
                                "main",
                                1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.sign.weakSignatureECDSA.RepeatedMessageNonceECDSA_2",
                                "main",
                                1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.sign.weakSignatureECDSA.RepeatedMessageNonceECDSA_3",
                                "main",
                                1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.sign.weakSignatureECDSA.RepeatedMessageNonceECDSA_4",
                                "main",
                                1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.sign.weakSignatureECDSA.SUN_80bits_ECDSA112wNONE1", "main", 1)
                        .withTPs(ConstraintError.class, 5)
                        .withTPs(RequiredPredicateError.class, 5)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.sign.weakSignatureECDSA.SUN_80bits_ECDSA112wNONE2", "main", 1)
                        .withTPs(ConstraintError.class, 3)
                        .withTPs(RequiredPredicateError.class, 5)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.sign.weakSignatureECDSA.SUN_80bits_ECDSA112wSHA1", "main", 1)
                        .withTPs(ConstraintError.class, 3)
                        .withTPs(RequiredPredicateError.class, 5)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pkc/sign/weakSignatureRSA/
    @Test
    public void weakSignatureRSAExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/weakSignatureRSA")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.sign.weakSignatureRSA.PKCS1Sign1024xSHA1_1", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(TypestateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.sign.weakSignatureRSA.PKCS1Sign1024xSHA256_2", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(TypestateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.sign.weakSignatureRSA.PSSw1024xSHA1_1", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.sign.weakSignatureRSA.PSSw1024xSHA256_2",
                                "positiveTestCase",
                                0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pkc.sign.weakSignatureRSA.PSSw1024xSHA256_2",
                                "negativeTestCase",
                                0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(TypestateError.class, 1)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }
}
