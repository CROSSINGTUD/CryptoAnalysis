package scanner.targets;

import crypto.analysis.errors.ForbiddenMethodError;
import crypto.analysis.errors.HardCodedError;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.NeverTypeOfError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import de.fraunhofer.iem.scanner.HeadlessJavaScanner;
import java.io.File;
import org.junit.Test;
import scanner.setup.AbstractHeadlessTest;
import scanner.setup.ErrorSpecification;
import scanner.setup.MavenProject;

public class BouncyCastleHeadlessTest extends AbstractHeadlessTest {

    @Test
    public void testBCMacExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/BCMacExamples").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject, BOUNCY_CASTLE_RULESET_PATH);

        addErrorSpecification(
                new ErrorSpecification.Builder("animamea.AmAESCrypto", "getMACOne", 1)
                        .withTPs(ForbiddenMethodError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("animamea.AmAESCrypto", "getMACTwo", 2)
                        .withTPs(ForbiddenMethodError.class, 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("animamea.AmAESCrypto", "<init>", 0)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("gwt_crypto.GMacTest", "performTestOne", 0)
                        .withTPs(ForbiddenMethodError.class, 1)
                        .withTPs(NeverTypeOfError.class, 1)
                        .withTPs(RequiredPredicateError.class, 4)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("gwt_crypto.SkeinMacTest", "performTestOne", 0)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("bunkr.PBKDF2Descriptor", "calculateRounds", 1)
                        .withTPs(TypestateError.class, 2)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("pattern.MacTest", "testMac1", 0)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("pattern.MacTest", "testMac2", 0)
                        .withTPs(RequiredPredicateError.class, 3)
                        .build());

        scanner.run();
        assertErrors(scanner.getCollectedErrors());
    }

    @Test
    public void testBCSymmetricCipherExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/BCSymmetricCipherExamples").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject, BOUNCY_CASTLE_RULESET_PATH);

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "gcm_aes_example.GCMAESBouncyCastle", "processing", 2)
                        .withTPs(RequiredPredicateError.class, 3)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "gcm_aes_example.GCMAESBouncyCastle", "processingCorrect", 2)
                        .withTPs(RequiredPredicateError.class, 0)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("cbc_aes_example.CBCAESBouncyCastle", "setKey", 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "cbc_aes_example.CBCAESBouncyCastle", "processing", 2)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());

        scanner.run();
        assertErrors(scanner.getCollectedErrors());
    }

    @Test
    public void testBCAsymmetricCipherExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/BCAsymmetricCipherExamples").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject, BOUNCY_CASTLE_RULESET_PATH);

        addErrorSpecification(
                new ErrorSpecification.Builder("rsa_misuse.RSATest", "Encrypt", 2)
                        .withTPs(TypestateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("rsa_misuse.RSATest", "Decrypt", 2)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());

        // These two errors occur because AsymmetricCipherKeyPair ensures the predicate only after
        // the constructor call
        addErrorSpecification(
                new ErrorSpecification.Builder("rsa_nomisuse.RSATest", "Encrypt", 2)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("rsa_nomisuse.RSATest", "Decrypt", 2)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("crypto.RSAEngineTest", "testEncryptTwo", 0)
                        .withTPs(TypestateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("crypto.RSAEngineTest", "testDecryptOne", 1)
                        .withTPs(RequiredPredicateError.class, 3)
                        .withTPs(ImpreciseValueExtractionError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("crypto.RSAEngineTest", "testDecryptTwo", 1)
                        .withTPs(TypestateError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .withTPs(ImpreciseValueExtractionError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "params.RSAPrivateCrtKeyParametersTest", "testOne", 0)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("generators.RSAKeyPairGeneratorTest", "testTwo", 0)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("params.ParametersWithRandomTest", "testTwo", 0)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("generators.RSAKeyPairGeneratorTest", "testThree", 0)
                        .withTPs(TypestateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("generators.RSAKeyPairGeneratorTest", "testFour", 0)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());

        scanner.run();
        assertErrors(scanner.getCollectedErrors());
    }

    @Test
    public void testBCDigestExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/BCDigestExamples").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject, BOUNCY_CASTLE_RULESET_PATH);

        addErrorSpecification(
                new ErrorSpecification.Builder("pattern.DigestTest", "digestWithoutUpdate", 0)
                        .withTPs(TypestateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("pattern.DigestTest", "digestWithMultipleUpdates", 0)
                        .withTPs(TypestateError.class, 1)
                        .withTPs(ImpreciseValueExtractionError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "inflatable_donkey.KeyBlobCurve25519Unwrap", "unwrapAES", 2)
                        .withTPs(ForbiddenMethodError.class, 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "inflatable_donkey.KeyBlobCurve25519Unwrap", "wrapAES", 2)
                        .withTPs(ForbiddenMethodError.class, 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("fabric_api_archieve.Crypto", "sign", 2)
                        .withTPs(HardCodedError.class, 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pluotsorbet.BouncyCastleSHA256", "TestSHA256DigestOne", 0)
                        .withTPs(TypestateError.class, 2)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "pluotsorbet.BouncyCastleSHA256", "testSHA256DigestTwo", 0)
                        .withTPs(TypestateError.class, 4)
                        .withTPs(ImpreciseValueExtractionError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("ipack.JPAKEExample", "deriveSessionKey", 1)
                        .withTPs(ImpreciseValueExtractionError.class, 1)
                        .build());

        scanner.run();
        assertErrors(scanner.getCollectedErrors());
    }

    @Test
    public void testBCSignerExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/BCSignerExamples").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject, BOUNCY_CASTLE_RULESET_PATH);

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "gwt_crypto.ISO9796SignerTest", "doShortPartialTest", 0)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "gwt_crypto.ISO9796SignerTest", "doFullMessageTest", 0)
                        .withTPs(HardCodedError.class, 1)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("gwt_crypto.PSSBlindTest", "testSig", 6)
                        .withTPs(IncompleteOperationError.class, 1)
                        .withTPs(RequiredPredicateError.class, 3)
                        .withTPs(ImpreciseValueExtractionError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("gwt_crypto.PSSTest", "testSig", 6)
                        .withTPs(IncompleteOperationError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "gwt_crypto.X931SignerTest", "shouldPassSignatureTestOne", 0)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "gwt_crypto.X931SignerTest", "shouldPassSignatureTestTwo", 0)
                        .withTPs(IncompleteOperationError.class, 2)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("iso9796_signer_verifier.Main", "sign", 0)
                        .withTPs(IncompleteOperationError.class, 1)
                        .withTPs(ImpreciseValueExtractionError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("diqube.TicketSignatureService", "signTicket", 0)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "diqube.TicketSignatureService", "isValidTicketSignature", 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(ImpreciseValueExtractionError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("bop_bitcoin_client.ECKeyPair", "createNew", 1)
                        .withTPs(RequiredPredicateError.class, 3)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("bop_bitcoin_client.ECKeyPair", "sign", 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("pattern.SignerTest", "testSignerGenerate", 0)
                        .withTPs(RequiredPredicateError.class, 3)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("pattern.SignerTest", "testSignerVerify", 0)
                        .withTPs(RequiredPredicateError.class, 3)
                        .build());

        scanner.run();
        assertErrors(scanner.getCollectedErrors());
    }

    @Test
    public void testBCEllipticCurveExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/BCEllipticCurveExamples").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject, BOUNCY_CASTLE_RULESET_PATH);

        addErrorSpecification(
                new ErrorSpecification.Builder("crypto.ECElGamalEncryptorTest", "testOne", 0)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("crypto.ECElGamalEncryptorTest", "testTwo", 0)
                        .withTPs(TypestateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("crypto.ECElGamalDecryptorTest", "testOne", 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("crypto.ECElGamalDecryptorTest", "testTwo", 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("crypto.ECElGamalDecryptorTest", "testThree", 0)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("crypto.ECElGamalDecryptorTest", "testFour", 0)
                        .withTPs(TypestateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("params.ECPublicKeyParametersTest", "testOne", 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("params.ECPrivateKeyParametersTest", "testOne", 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .withTPs(HardCodedError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("params.ECPrivateKeyParametersTest", "testTwo", 1)
                        .withTPs(HardCodedError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("params.ParametersWithRandomTest", "testOne", 1)
                        .withTPs(RequiredPredicateError.class, 3)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("params.ParametersWithRandomTest", "testThree", 1)
                        .withTPs(RequiredPredicateError.class, 4)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("params.ECDomainParametersTest", "testThree", 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("params.ECKeyGenerationParametersTest", "testTwo", 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "params.ECKeyGenerationParametersTest", "testThree", 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("transforms.ECFixedTransformTest", "testOne", 1)
                        .withTPs(RequiredPredicateError.class, 3)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("transforms.ECFixedTransformTest", "testTwo", 1)
                        .withTPs(RequiredPredicateError.class, 3)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("transforms.ECFixedTransformTest", "testThree", 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("transforms.ECFixedTransformTest", "testFour", 1)
                        .withTPs(RequiredPredicateError.class, 3)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("transforms.ECFixedTransformTest", "testFive", 1)
                        .withTPs(TypestateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("transforms.ECFixedTransformTest", "testSix", 1)
                        .withTPs(RequiredPredicateError.class, 3)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "transforms.ECNewPublicKeyTransformTest", "testOne", 1)
                        .withTPs(RequiredPredicateError.class, 3)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "transforms.ECNewPublicKeyTransformTest", "testTwo", 1)
                        .withTPs(RequiredPredicateError.class, 4)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "transforms.ECNewPublicKeyTransformTest", "testThree", 1)
                        .withTPs(TypestateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "transforms.ECNewPublicKeyTransformTest", "testFour", 1)
                        .withTPs(IncompleteOperationError.class, 1)
                        .withTPs(RequiredPredicateError.class, 4)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "transforms.ECNewPublicKeyTransformTest", "testFive", 1)
                        .withTPs(RequiredPredicateError.class, 4)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "transforms.ECNewPublicKeyTransformTest", "testSix", 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "transforms.ECNewRandomessTransformTest", "testOne", 1)
                        .withTPs(RequiredPredicateError.class, 3)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "transforms.ECNewRandomessTransformTest", "testTwo", 1)
                        .withTPs(RequiredPredicateError.class, 4)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "transforms.ECNewRandomessTransformTest", "testThree", 1)
                        .withTPs(TypestateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "transforms.ECNewRandomessTransformTest", "testFour", 1)
                        .withTPs(IncompleteOperationError.class, 1)
                        .withTPs(RequiredPredicateError.class, 4)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "transforms.ECNewRandomessTransformTest", "testFive", 1)
                        .withTPs(RequiredPredicateError.class, 4)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "transforms.ECNewRandomessTransformTest", "testSix", 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("generators.ECKeyPairGeneratorTest", "testTwo", 1)
                        .withTPs(TypestateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("generators.ECKeyPairGeneratorTest", "testThree", 1)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("constants.Constants", "<clinit>", 0)
                        .withTPs(HardCodedError.class, 1)
                        .build());

        scanner.run();
        assertErrors(scanner.getCollectedErrors());
    }
}
