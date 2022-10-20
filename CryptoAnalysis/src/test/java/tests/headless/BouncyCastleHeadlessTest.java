package tests.headless;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

import crypto.HeadlessCryptoScanner;
import crypto.analysis.CrySLRulesetSelector.Ruleset;
import crypto.analysis.errors.HardCodedError;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import tests.headless.FindingsType.FalseNegatives;
import tests.headless.FindingsType.TruePositives;

public class BouncyCastleHeadlessTest extends AbstractHeadlessTest {
	@Ignore
	@Test
	public void testBCMacExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BCMacExamples").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, Ruleset.BouncyCastle);
		
		setErrorsCount("<pattern.AESTest: void testAESEngine2()>", IncompleteOperationError.class, 1);
		setErrorsCount("<pattern.AESTest: void testAESLightEngine2()>", TypestateError.class, 1);
		
		// Ignored
		setErrorsCount("<pattern.AESTest: void testAESEngine1()>", ImpreciseValueExtractionError.class, 2);
		setErrorsCount("<pattern.AESTest: void testAESEngine2()>", ImpreciseValueExtractionError.class, 1);
		setErrorsCount("<pattern.AESTest: void testMonteCarloAndVector()>", ImpreciseValueExtractionError.class, 2);

		scanner.exec();
	  	assertErrors();
	}
	
	@Test
	public void testBCSymmetricCipherExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BCSymmetricCipherExamples").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, Ruleset.BouncyCastle);
		
		setErrorsCount(RequiredPredicateError.class, new TruePositives(2), new FalseNegatives(1, "https://github.com/CROSSINGTUD/CryptoAnalysis/issues/216"), "<gcm_aes_example.GCMAESBouncyCastle: byte[] processing(byte[],boolean)>");
		setErrorsCount("<cbc_aes_example.CBCAESBouncyCastle: void setKey(byte[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<cbc_aes_example.CBCAESBouncyCastle: byte[] processing(byte[],boolean)>", RequiredPredicateError.class, 1);

		scanner.exec();
	  	assertErrors();
	}
	
	@Test
	@SuppressWarnings("serial")
	public void testBCAsymmetricCipherExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BCAsymmetricCipherExamples").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, Ruleset.BouncyCastle);

		setErrorsCount(RequiredPredicateError.class, new TruePositives(1), "<crypto.RSAEngineTest: void testDecryptTwo(byte[])>");
		
		setErrorsCount(TypestateError.class, new TruePositives(1), "<rsa_misuse.RSATest: java.lang.String Encrypt(byte[],org.bouncycastle.crypto.params.AsymmetricKeyParameter)>");
		setErrorsCount(TypestateError.class, new TruePositives(1),"<crypto.RSAEngineTest: void testEncryptTwo()>");
		setErrorsCount(TypestateError.class, new TruePositives(1),"<crypto.RSAEngineTest: void testDecryptTwo(byte[])>");
		
		setErrorsCount(RequiredPredicateError.class, new TruePositives(1), new FalseNegatives(1, "Fifth parameter not randomized! //Related to https://github.com/CROSSINGTUD/CryptoAnalysis/issues/140"),  "<crypto.RSAEngineTest: void testDecryptOne(byte[])>");
		setErrorsCount(TypestateError.class,  new TruePositives(1), "<generators.RSAKeyPairGeneratorTest: void testThree()>");
		setErrorsCount(IncompleteOperationError.class, new TruePositives(1),  "<generators.RSAKeyPairGeneratorTest: void testFour()>");
		setErrorsCount(RequiredPredicateError.class, new TruePositives(1),"<generators.RSAKeyPairGeneratorTest: void testTwo()>");
		setErrorsCount(RequiredPredicateError.class, new TruePositives(1),"<params.ParametersWithRandomTest: void testTwo()>");
		setErrorsCount(RequiredPredicateError.class, new TruePositives(1), "<params.RSAPrivateCrtKeyParametersTest: void testOne()>");
		
		scanner.exec();
	  	assertErrors();
	}
	
	@Ignore
	@Test
	public void testBCDigestExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BCDigestExamples").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, Ruleset.BouncyCastle);
		
		setErrorsCount("<DigestTest: void digestWithoutUpdate()>", TypestateError.class, 1);
		
		// False Positives due to issue #129
		setErrorsCount("<DigestTest: void digestDefaultUsage()>", TypestateError.class, 1);
		setErrorsCount("<DigestTest: void digestWithMultipleUpdates()>", IncompleteOperationError.class, 1);
		setErrorsCount("<DigestTest: void multipleDigests()>", IncompleteOperationError.class, 2);
		setErrorsCount("<DigestTest: void digestWithReset()>", TypestateError.class, 1);
		
		scanner.exec();
	  	assertErrors();
	}
	
	@Ignore
	@Test
	public void testBCSignerExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BCSignerExamples").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, Ruleset.BouncyCastle);
		
		scanner.exec();
	  	assertErrors();
	}
	
	@Test
	public void testBCEllipticCurveExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BCEllipticCurveExamples").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, Ruleset.BouncyCastle);
		
		setErrorsCount(new ErrorSpecification.Builder("<crypto.ECElGamalEncryptorTest: void testOne()>")
				.withFNs(RequiredPredicateError.class, 1, "https://github.com/CROSSINGTUD/CryptoAnalysis/issues/216")
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
				.withFNs(RequiredPredicateError.class, 1, "https://github.com/CROSSINGTUD/CryptoAnalysis/issues/216")
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<params.ECPrivateKeyParametersTest: void testOne(java.lang.String)>")
				.withTPs(HardCodedError.class, 1)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<params.ECPrivateKeyParametersTest: void testTwo(java.lang.String)>")
				.withTPs(HardCodedError.class, 1)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<params.ParametersWithRandomTest: void testOne(java.lang.String)>")
				.withTPs(RequiredPredicateError.class, 2)
				.withFNs(RequiredPredicateError.class, 1, "https://github.com/CROSSINGTUD/CryptoAnalysis/issues/216")
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<params.ParametersWithRandomTest: void testThree(java.lang.String)>")
				.withTPs(RequiredPredicateError.class, 3)
				.withFNs(RequiredPredicateError.class, 1, "https://github.com/CROSSINGTUD/CryptoAnalysis/issues/216")
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
				.withTPs(RequiredPredicateError.class, 2)
				.withTPs(IncompleteOperationError.class, 1)
				.build());
		
		setErrorsCount(new ErrorSpecification.Builder("<transforms.ECNewPublicKeyTransformTest: void testThree(java.lang.String)>")
				.withTPs(TypestateError.class, 1)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<transforms.ECNewPublicKeyTransformTest: void testFour(java.lang.String)>")
				.withTPs(IncompleteOperationError.class, 1)
				.withTPs(RequiredPredicateError.class, 2)
				.withFNs(RequiredPredicateError.class, 1, "https://github.com/CROSSINGTUD/CryptoAnalysis/issues/216")
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<transforms.ECNewPublicKeyTransformTest: void testFive(java.lang.String)>")
				.withTPs(RequiredPredicateError.class, 2)
				.withFNs(RequiredPredicateError.class, 2, "https://github.com/CROSSINGTUD/CryptoAnalysis/issues/216")
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<transforms.ECNewPublicKeyTransformTest: void testTwo(java.lang.String)>")
				.withTPs(RequiredPredicateError.class, 2)
				.withFNs(RequiredPredicateError.class, 2, "https://github.com/CROSSINGTUD/CryptoAnalysis/issues/216")
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<transforms.ECNewPublicKeyTransformTest: void testOne(java.lang.String)>")
				.withTPs(RequiredPredicateError.class, 2)
				.withFNs(RequiredPredicateError.class, 1, "https://github.com/CROSSINGTUD/CryptoAnalysis/issues/216")
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<transforms.ECNewPublicKeyTransformTest: void testSix(java.lang.String)>")
				.withFNs(RequiredPredicateError.class, 2, "https://github.com/CROSSINGTUD/CryptoAnalysis/issues/216")
				.build());
		
		setErrorsCount(new ErrorSpecification.Builder("<transforms.ECNewRandomessTransformTest: void testThree(java.lang.String)>")
				.withTPs(TypestateError.class, 1)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<transforms.ECNewRandomessTransformTest: void testFour(java.lang.String)>")
				.withTPs(IncompleteOperationError.class, 1)
				.withTPs(RequiredPredicateError.class, 2)
				.withFNs(RequiredPredicateError.class, 1, "https://github.com/CROSSINGTUD/CryptoAnalysis/issues/216")
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<transforms.ECNewRandomessTransformTest: void testFive(java.lang.String)>")
				.withTPs(RequiredPredicateError.class, 2)
				.withFNs(RequiredPredicateError.class, 1, "https://github.com/CROSSINGTUD/CryptoAnalysis/issues/216")
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<transforms.ECNewRandomessTransformTest: void testSix(java.lang.String)>")
				.withFNs(RequiredPredicateError.class, 2, "https://github.com/CROSSINGTUD/CryptoAnalysis/issues/216")
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<transforms.ECNewRandomessTransformTest: void testTwo(java.lang.String)>")
				.withTPs(RequiredPredicateError.class, 2)
				.withFNs(RequiredPredicateError.class, 2, "https://github.com/CROSSINGTUD/CryptoAnalysis/issues/216")
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<transforms.ECNewRandomessTransformTest: void testOne(java.lang.String)>")
				.withTPs(RequiredPredicateError.class, 2)
				.withFNs(RequiredPredicateError.class, 1, "https://github.com/CROSSINGTUD/CryptoAnalysis/issues/216")
				.build());
		
		setErrorsCount(new ErrorSpecification.Builder("<generators.ECKeyPairGeneratorTest: void testTwo(java.lang.String)>")
				.withTPs(TypestateError.class, 1)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<generators.ECKeyPairGeneratorTest: void testThree(java.lang.String)>")
				.withTPs(IncompleteOperationError.class, 1)
				.build());
		
		setErrorsCount(new ErrorSpecification.Builder("<constants.Constants: void <clinit>()>")
				.withTPs(RequiredPredicateError.class, 2)
				.build());
		setErrorsCount(new ErrorSpecification.Builder("<constants.Constants: void <clinit>()>")
				.withTPs(HardCodedError.class, 1)
				.build());
		scanner.exec();
	  	assertErrors();
	}
}
