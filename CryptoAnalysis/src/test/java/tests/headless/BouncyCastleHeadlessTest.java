package tests.headless;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

import crypto.HeadlessCryptoScanner;
import crypto.analysis.CrySLRulesetSelector.Ruleset;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import tests.headless.FindingsType.FalseNegatives;
import tests.headless.FindingsType.FalsePositives;
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
		

		setErrorsCount("<gcm_aes_example.GCMAESBouncyCastle: byte[] processing(byte[],boolean)>", RequiredPredicateError.class, 2);
		setErrorsCount("<cbc_aes_example.CBCAESBouncyCastle: void setKey(byte[])>", RequiredPredicateError.class, 1);

		scanner.exec();
	  	assertErrors();
	}
	
	@Test
	@SuppressWarnings("serial")
	public void testBCAsymmetricCipherExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BCAsymmetricCipherExamples").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, Ruleset.BouncyCastle);

		setErrorsCount(RequiredPredicateError.class, new FalsePositives(1, "AsymmetricCipherKeyPair.getPrivate() not specified."), "<rsa_nomisuse.RSATest: java.lang.String Decrypt(java.lang.String,org.bouncycastle.crypto.params.AsymmetricKeyParameter)>");
		setErrorsCount(RequiredPredicateError.class, new FalsePositives(1, "AsymmetricCipherKeyPair.getPrivate() not specified."),"<rsa_misuse.RSATest: java.lang.String Decrypt(java.lang.String,org.bouncycastle.crypto.params.AsymmetricKeyParameter)>");
		setErrorsCount(RequiredPredicateError.class, new FalsePositives(1, "AsymmetricCipherKeyPair.getPublic() not specified"), "<rsa_nomisuse.RSATest: java.lang.String Encrypt(byte[],org.bouncycastle.crypto.params.AsymmetricKeyParameter)>");
		
		setErrorsCount(RequiredPredicateError.class, new TruePositives(1), "<crypto.RSAEngineTest: void testDecryptTwo(byte[])>");
		
		setErrorsCount(TypestateError.class, new TruePositives(1), "<rsa_misuse.RSATest: java.lang.String Encrypt(byte[],org.bouncycastle.crypto.params.AsymmetricKeyParameter)>");
		setErrorsCount(TypestateError.class, new TruePositives(1),"<crypto.RSAEngineTest: void testEncryptTwo()>");
		setErrorsCount(TypestateError.class, new TruePositives(1),"<crypto.RSAEngineTest: void testDecryptTwo(byte[])>");
		
		setErrorsCount(RequiredPredicateError.class, new TruePositives(2), new FalseNegatives(1, "Fifth parameter not randomized! //Related to https://github.com/CROSSINGTUD/CryptoAnalysis/issues/140"),  "<crypto.RSAEngineTest: void testDecryptOne(byte[])>");
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
	@SuppressWarnings("serial")
	public void testBCEllipticCurveExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BCEllipticCurveExamples").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, Ruleset.BouncyCastle);
		
		setErrorsCount(RequiredPredicateError.class, new FalsePositives(1, "https://github.com/CROSSINGTUD/CryptSL/issues/11"), "<crypto.ECElGamalEncryptorTest: void testThree(java.lang.String)>"); 
		setErrorsCount(RequiredPredicateError.class, new FalsePositives(1, "https://github.com/CROSSINGTUD/CryptSL/issues/11"), "<crypto.ECElGamalEncryptorTest: void testFour(java.lang.String)>");
		
		setErrorsCount(RequiredPredicateError.class, new TruePositives(2), "<crypto.ECElGamalEncryptorTest: void testOne()>");
		setErrorsCount(TypestateError.class, new TruePositives(1), "<crypto.ECElGamalEncryptorTest: void testTwo()>");
		
		setErrorsCount(RequiredPredicateError.class,new TruePositives(2), "<params.ECPublicKeyParametersTest: void testOne(java.lang.String)>");
		setErrorsCount(RequiredPredicateError.class, new TruePositives(2),"<params.ECPrivateKeyParametersTest: void testOne(java.lang.String)>");
		setErrorsCount(RequiredPredicateError.class,new TruePositives(3), "<params.ParametersWithRandomTest: void testOne(java.lang.String)>");
		setErrorsCount(RequiredPredicateError.class, new TruePositives(4),"<params.ParametersWithRandomTest: void testThree(java.lang.String)>");
		setErrorsCount(RequiredPredicateError.class, new TruePositives(1),"<params.ECDomainParametersTest: void testThree(java.lang.String)>");
		
		setErrorsCount(RequiredPredicateError.class, new TruePositives(1),"<crypto.ECElGamalDecryptorTest: void testOne(java.lang.String)>");
		setErrorsCount(RequiredPredicateError.class, new TruePositives(1),"<crypto.ECElGamalDecryptorTest: void testTwo(java.lang.String)>");
		setErrorsCount(RequiredPredicateError.class, new TruePositives(1),"<crypto.ECElGamalDecryptorTest: void testThree()>");
		setErrorsCount(TypestateError.class,new TruePositives(1),"<crypto.ECElGamalDecryptorTest: void testFour()>");
		
		setErrorsCount(RequiredPredicateError.class, new TruePositives(2),"<constants.Constants: void <clinit>()>");
		scanner.exec();
	  	assertErrors();
	}
}
