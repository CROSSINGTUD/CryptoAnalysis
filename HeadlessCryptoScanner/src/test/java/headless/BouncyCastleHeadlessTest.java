package headless;

import crypto.analysis.errors.ForbiddenMethodError;
import crypto.analysis.errors.HardCodedError;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.NeverTypeOfError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import de.fraunhofer.iem.scanner.HeadlessCryptoScanner;
import headless.FindingsType.TruePositives;
import org.junit.Test;

import java.io.File;

public class BouncyCastleHeadlessTest extends AbstractHeadlessTest {
	
	@Test
	public void testBCMacExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BCMacExamples").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, BOUNCY_CASTLE_RULESET_PATH);
		
		setErrorsCount("<animamea.AmAESCrypto: byte[] getMACOne(byte[])>", ForbiddenMethodError.class, 1);
		setErrorsCount("<animamea.AmAESCrypto: byte[] getMACTwo(byte[],byte[])>", ForbiddenMethodError.class, 1);
		setErrorsCount("<animamea.AmAESCrypto: byte[] getMACTwo(byte[],byte[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<animamea.AmAESCrypto: void <init>()>", RequiredPredicateError.class, 1);
		
		setErrorsCount("<gwt_crypto.GMacTest: void performTestOne()>", ForbiddenMethodError.class, 1);
		setErrorsCount("<gwt_crypto.GMacTest: void performTestOne()>", NeverTypeOfError.class, 1);
		setErrorsCount("<gwt_crypto.GMacTest: void performTestOne()>", RequiredPredicateError.class, 4);
		setErrorsCount("<gwt_crypto.GMacTest: void performTestOne()>", IncompleteOperationError.class, 1);
		setErrorsCount("<gwt_crypto.SkeinMacTest: void performTestOne()>", RequiredPredicateError.class, 1);
		
		setErrorsCount("<bunkr.PBKDF2Descriptor: int calculateRounds(int)>", TypestateError.class, 2);
		setErrorsCount("<bunkr.PBKDF2Descriptor: int calculateRounds(int)>", IncompleteOperationError.class, 1);
		
		setErrorsCount("<pattern.MacTest: void testMac1()>", RequiredPredicateError.class, 1);
		setErrorsCount("<pattern.MacTest: void testMac2()>", RequiredPredicateError.class, 3);

		scanner.run();
	  	assertErrors(scanner.getCollectedErrors());
	}
	
	@Test
	public void testBCSymmetricCipherExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BCSymmetricCipherExamples").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, BOUNCY_CASTLE_RULESET_PATH);
		
		setErrorsCount("<gcm_aes_example.GCMAESBouncyCastle: byte[] processing(byte[],boolean)>", RequiredPredicateError.class, 3);
		setErrorsCount("<gcm_aes_example.GCMAESBouncyCastle: byte[] processingCorrect(byte[],boolean)>", RequiredPredicateError.class, 0);
		
		setErrorsCount("<cbc_aes_example.CBCAESBouncyCastle: void setKey(byte[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<cbc_aes_example.CBCAESBouncyCastle: byte[] processing(byte[],boolean)>", RequiredPredicateError.class, 1);

		scanner.run();
	  	assertErrors(scanner.getCollectedErrors());
	}
	
	@Test
	public void testBCAsymmetricCipherExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BCAsymmetricCipherExamples").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, BOUNCY_CASTLE_RULESET_PATH);
		
		setErrorsCount(TypestateError.class, new TruePositives(1), "<rsa_misuse.RSATest: java.lang.String Encrypt(byte[],org.bouncycastle.crypto.params.AsymmetricKeyParameter)>");
		setErrorsCount(RequiredPredicateError.class, new TruePositives(1), "<rsa_misuse.RSATest: java.lang.String Decrypt(java.lang.String,org.bouncycastle.crypto.params.AsymmetricKeyParameter)>");

		// These two errors occur because AsymmetricCipherKeyPair ensures the predicate only after the constructor call
		setErrorsCount(RequiredPredicateError.class, new TruePositives(1), "<rsa_nomisuse.RSATest: java.lang.String Encrypt(byte[],org.bouncycastle.crypto.params.AsymmetricKeyParameter)>");
		setErrorsCount(RequiredPredicateError.class, new TruePositives(1), "<rsa_nomisuse.RSATest: java.lang.String Decrypt(java.lang.String,org.bouncycastle.crypto.params.AsymmetricKeyParameter)>");
		
		setErrorsCount(TypestateError.class, new TruePositives(1), "<crypto.RSAEngineTest: void testEncryptTwo()>");
		setErrorsCount(TypestateError.class, new TruePositives(1), "<crypto.RSAEngineTest: void testDecryptTwo(byte[])>");

		// Since version 3.0.0: Predicates with same name in the same statement are distinguished
		setErrorsCount(RequiredPredicateError.class, new TruePositives(3), "<crypto.RSAEngineTest: void testDecryptOne(byte[])>");
		setErrorsCount(RequiredPredicateError.class, new TruePositives(2), "<crypto.RSAEngineTest: void testDecryptTwo(byte[])>");
		setErrorsCount(RequiredPredicateError.class, new TruePositives(2), "<params.RSAPrivateCrtKeyParametersTest: void testOne()>");
		
		setErrorsCount(TypestateError.class, new TruePositives(1), "<generators.RSAKeyPairGeneratorTest: void testThree()>");
		setErrorsCount(IncompleteOperationError.class, new TruePositives(1), "<generators.RSAKeyPairGeneratorTest: void testFour()>");
		setErrorsCount(RequiredPredicateError.class, new TruePositives(1),"<generators.RSAKeyPairGeneratorTest: void testTwo()>");
		setErrorsCount(RequiredPredicateError.class, new TruePositives(1),"<params.ParametersWithRandomTest: void testTwo()>");
		
		scanner.run();
	  	assertErrors(scanner.getCollectedErrors());
	}
	
	@Test
	public void testBCDigestExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BCDigestExamples").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, BOUNCY_CASTLE_RULESET_PATH);
		
		setErrorsCount("<pattern.DigestTest: void digestWithoutUpdate()>", TypestateError.class, 1);
		setErrorsCount("<pattern.DigestTest: void digestWithMultipleUpdates()>", TypestateError.class, 1);
		setErrorsCount("<pattern.DigestTest: void digestWithMultipleUpdates()>", ImpreciseValueExtractionError.class, 1);
		
		setErrorsCount("<inflatable_donkey.KeyBlobCurve25519Unwrap: java.util.Optional unwrapAES(byte[],byte[])>", ForbiddenMethodError.class, 1);
		setErrorsCount("<inflatable_donkey.KeyBlobCurve25519Unwrap: java.util.Optional unwrapAES(byte[],byte[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<inflatable_donkey.KeyBlobCurve25519Unwrap: byte[] wrapAES(byte[],byte[])>", ForbiddenMethodError.class, 1);
		setErrorsCount("<inflatable_donkey.KeyBlobCurve25519Unwrap: byte[] wrapAES(byte[],byte[])>", RequiredPredicateError.class, 1);
		
		setErrorsCount("<fabric_api_archieve.Crypto: byte[] sign(byte[],byte[])>", HardCodedError.class, 1);
		setErrorsCount("<fabric_api_archieve.Crypto: byte[] sign(byte[],byte[])>", RequiredPredicateError.class, 1);
		
		setErrorsCount("<pluotsorbet.BouncyCastleSHA256: void TestSHA256DigestOne()>", TypestateError.class, 2);
		setErrorsCount("<pluotsorbet.BouncyCastleSHA256: void testSHA256DigestTwo()>", TypestateError.class, 4);
		setErrorsCount("<pluotsorbet.BouncyCastleSHA256: void testSHA256DigestTwo()>", ImpreciseValueExtractionError.class, 1);

		setErrorsCount("<ipack.JPAKEExample: java.math.BigInteger deriveSessionKey(java.math.BigInteger)>", ImpreciseValueExtractionError.class, 1);
		
		scanner.run();
	  	assertErrors(scanner.getCollectedErrors());
	}

	@Test
	public void testBCSignerExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BCSignerExamples").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, BOUNCY_CASTLE_RULESET_PATH);
		
		setErrorsCount("<gwt_crypto.ISO9796SignerTest: void doShortPartialTest()>", IncompleteOperationError.class, 1);
		setErrorsCount("<gwt_crypto.ISO9796SignerTest: void doFullMessageTest()>", HardCodedError.class, 1);
		setErrorsCount("<gwt_crypto.ISO9796SignerTest: void doFullMessageTest()>", IncompleteOperationError.class, 1);
		setErrorsCount("<gwt_crypto.PSSBlindTest: void testSig(int,org.bouncycastle.crypto.params.RSAKeyParameters,org.bouncycastle.crypto.params.RSAKeyParameters,byte[],byte[],byte[])>", IncompleteOperationError.class, 1);
		setErrorsCount("<gwt_crypto.PSSBlindTest: void testSig(int,org.bouncycastle.crypto.params.RSAKeyParameters,org.bouncycastle.crypto.params.RSAKeyParameters,byte[],byte[],byte[])>", RequiredPredicateError.class, 3);
		setErrorsCount("<gwt_crypto.PSSTest: void testSig(int,org.bouncycastle.crypto.params.RSAKeyParameters,org.bouncycastle.crypto.params.RSAKeyParameters,byte[],byte[],byte[])>", IncompleteOperationError.class, 1);
		setErrorsCount("<gwt_crypto.PSSTest: void testSig(int,org.bouncycastle.crypto.params.RSAKeyParameters,org.bouncycastle.crypto.params.RSAKeyParameters,byte[],byte[],byte[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<gwt_crypto.X931SignerTest: void shouldPassSignatureTestOne()>", IncompleteOperationError.class, 1);
		setErrorsCount("<gwt_crypto.X931SignerTest: void shouldPassSignatureTestTwo()>", RequiredPredicateError.class, 2);
		setErrorsCount("<gwt_crypto.X931SignerTest: void shouldPassSignatureTestTwo()>", IncompleteOperationError.class, 2);
		
		setErrorsCount("<iso9796_signer_verifier.Main: byte[] sign()>", IncompleteOperationError.class, 1);
		setErrorsCount("<iso9796_signer_verifier.Main: byte[] sign()>", ImpreciseValueExtractionError.class, 1);
		
		setErrorsCount("<diqube.TicketSignatureService: void signTicket()>", RequiredPredicateError.class, 1);
		setErrorsCount("<diqube.TicketSignatureService: boolean isValidTicketSignature(byte[])>", RequiredPredicateError.class, 1);
		
		setErrorsCount("<bop_bitcoin_client.ECKeyPair: bop_bitcoin_client.ECKeyPair createNew(boolean)>", RequiredPredicateError.class, 3);
		setErrorsCount("<bop_bitcoin_client.ECKeyPair: byte[] sign(byte[])>", RequiredPredicateError.class, 1);
		
		setErrorsCount("<pattern.SignerTest: void testSignerGenerate()>", RequiredPredicateError.class, 3);
		setErrorsCount("<pattern.SignerTest: void testSignerVerify()>", RequiredPredicateError.class, 3);
		
		scanner.run();
	  	assertErrors(scanner.getCollectedErrors());
	}
	
	@Test
	public void testBCEllipticCurveExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BCEllipticCurveExamples").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, BOUNCY_CASTLE_RULESET_PATH);
		
		setErrorsCount(new ErrorSpecification.Builder("<crypto.ECElGamalEncryptorTest: void testOne()>")
				.withTPs(RequiredPredicateError.class, 1)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<crypto.ECElGamalEncryptorTest: void testTwo()>")
				.withTPs(TypestateError.class, 1)
				.build());
		
		setErrorsCount(new ErrorSpecification.Builder("<crypto.ECElGamalDecryptorTest: void testOne(java.lang.String)>")
				.withTPs(RequiredPredicateError.class, 1)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<crypto.ECElGamalDecryptorTest: void testTwo(java.lang.String)>")
				.withTPs(RequiredPredicateError.class, 1)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<crypto.ECElGamalDecryptorTest: void testThree()>")
				.withTPs(RequiredPredicateError.class, 1)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<crypto.ECElGamalDecryptorTest: void testFour()>")
				.withTPs(TypestateError.class, 1)
				.build());
		
		setErrorsCount(new ErrorSpecification.Builder("<params.ECPublicKeyParametersTest: void testOne(java.lang.String)>")
				.withTPs(RequiredPredicateError.class, 2)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<params.ECPrivateKeyParametersTest: void testOne(java.lang.String)>")
				.withTPs(RequiredPredicateError.class, 2)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<params.ECPrivateKeyParametersTest: void testOne(java.lang.String)>")
				.withTPs(HardCodedError.class, 1)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<params.ECPrivateKeyParametersTest: void testTwo(java.lang.String)>")
				.withTPs(HardCodedError.class, 1)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<params.ParametersWithRandomTest: void testOne(java.lang.String)>")
				.withTPs(RequiredPredicateError.class, 3)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<params.ParametersWithRandomTest: void testThree(java.lang.String)>")
				.withTPs(RequiredPredicateError.class, 4)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<params.ECDomainParametersTest: void testThree(java.lang.String)>")
				.withTPs(RequiredPredicateError.class, 1)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<params.ECKeyGenerationParametersTest: void testTwo(java.lang.String)>")
				.withTPs(RequiredPredicateError.class, 1)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<params.ECKeyGenerationParametersTest: void testThree(java.lang.String)>")
				.withTPs(RequiredPredicateError.class, 1)
				.build());
		
		setErrorsCount(new ErrorSpecification.Builder("<transforms.ECFixedTransformTest: void testFive(java.lang.String)>")
				.withTPs(TypestateError.class, 1)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<transforms.ECFixedTransformTest: void testTwo(java.lang.String)>")
				.withTPs(RequiredPredicateError.class, 3)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<transforms.ECFixedTransformTest: void testFour(java.lang.String)>")
				.withTPs(RequiredPredicateError.class, 3)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<transforms.ECFixedTransformTest: void testThree(java.lang.String)>")
				.withTPs(RequiredPredicateError.class, 2)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<transforms.ECFixedTransformTest: void testOne(java.lang.String)>")
				.withTPs(RequiredPredicateError.class, 3)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<transforms.ECFixedTransformTest: void testSix(java.lang.String)>")
				.withTPs(RequiredPredicateError.class, 3)
				.withTPs(IncompleteOperationError.class, 1)
				.build());
		
		setErrorsCount(new ErrorSpecification.Builder("<transforms.ECNewPublicKeyTransformTest: void testThree(java.lang.String)>")
				.withTPs(TypestateError.class, 1)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<transforms.ECNewPublicKeyTransformTest: void testFour(java.lang.String)>")
				.withTPs(IncompleteOperationError.class, 1)
				.withTPs(RequiredPredicateError.class, 4)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<transforms.ECNewPublicKeyTransformTest: void testFive(java.lang.String)>")
				.withTPs(RequiredPredicateError.class, 4)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<transforms.ECNewPublicKeyTransformTest: void testTwo(java.lang.String)>")
				.withTPs(RequiredPredicateError.class, 4)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<transforms.ECNewPublicKeyTransformTest: void testOne(java.lang.String)>")
				.withTPs(RequiredPredicateError.class, 3)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<transforms.ECNewPublicKeyTransformTest: void testSix(java.lang.String)>")
				.withTPs(RequiredPredicateError.class, 2)
				.build());
		
		setErrorsCount(new ErrorSpecification.Builder("<transforms.ECNewRandomessTransformTest: void testThree(java.lang.String)>")
				.withTPs(TypestateError.class, 1)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<transforms.ECNewRandomessTransformTest: void testFour(java.lang.String)>")
				.withTPs(IncompleteOperationError.class, 1)
				.withTPs(RequiredPredicateError.class, 4)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<transforms.ECNewRandomessTransformTest: void testFive(java.lang.String)>")
				.withTPs(RequiredPredicateError.class, 4)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<transforms.ECNewRandomessTransformTest: void testSix(java.lang.String)>")
				.withTPs(RequiredPredicateError.class, 2)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<transforms.ECNewRandomessTransformTest: void testTwo(java.lang.String)>")
				.withTPs(RequiredPredicateError.class, 4)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<transforms.ECNewRandomessTransformTest: void testOne(java.lang.String)>")
				.withTPs(RequiredPredicateError.class, 3)
				.build());
		
		setErrorsCount(new ErrorSpecification.Builder("<generators.ECKeyPairGeneratorTest: void testTwo(java.lang.String)>")
				.withTPs(TypestateError.class, 1)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<generators.ECKeyPairGeneratorTest: void testThree(java.lang.String)>")
				.withTPs(IncompleteOperationError.class, 1)
				.build());

		setErrorsCount(new ErrorSpecification.Builder("<constants.Constants: void <clinit>()>")
				.withTPs(HardCodedError.class, 1)
				.build());

		scanner.run();
	  	assertErrors(scanner.getCollectedErrors());
	}
}
