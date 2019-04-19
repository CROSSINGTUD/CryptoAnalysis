package tests.headless;

import java.io.File;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.junit.Ignore;
import org.junit.Test;

import crypto.HeadlessCryptoScanner;
import crypto.analysis.Constants.Ruleset;
import crypto.analysis.errors.*;
import test.IDEALCrossingTestingFramework;

public class BouncyCastleHeadlessTest extends AbstractHeadlessTest {
	@Ignore
	@Test
	public void testBCMacExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BCMacExamples").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, IDEALCrossingTestingFramework.RESOURCE_PATH, Ruleset.BouncyCastle);
		
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
		HeadlessCryptoScanner scanner = createScanner(mavenProject, IDEALCrossingTestingFramework.RESOURCE_PATH, Ruleset.BouncyCastle);
		

		setErrorsCount("<gcm_aes_example.GCMAESBouncyCastle: byte[] processing(byte[],boolean)>", RequiredPredicateError.class, 2);
		setErrorsCount("<cbc_aes_example.CBCAESBouncyCastle: void setKey(byte[])>", RequiredPredicateError.class, 1);
		
		//False Positive because a single SecureRandom object may not call nextBytes(...) twice. Correct the rule!
//		setErrorsCount("<cbc_aes_example.CBCAESBouncyCastle: byte[] processing(byte[],boolean)>", TypestateError.class, 1);
				

		scanner.exec();
	  	assertErrors();
	}
	
	@Test
	public void testBCAsymmetricCipherExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BCAsymmetricCipherExamples").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, IDEALCrossingTestingFramework.RESOURCE_PATH, Ruleset.BouncyCastle);
		
		setErrorsCount("<rsa_misuse.RSATest: java.lang.String Encrypt(byte[], org.bouncycastle.crypto.params.AsymmetricKeyParameter)>", TypestateError.class, 1);
		
		scanner.exec();
	  	assertErrors();
	}
	
	@Ignore
	@Test
	public void testBCDigestExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BCDigestExamples").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, IDEALCrossingTestingFramework.RESOURCE_PATH, Ruleset.BouncyCastle);
		
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
		HeadlessCryptoScanner scanner = createScanner(mavenProject, IDEALCrossingTestingFramework.RESOURCE_PATH, Ruleset.BouncyCastle);
		
		scanner.exec();
	  	assertErrors();
	}
}
