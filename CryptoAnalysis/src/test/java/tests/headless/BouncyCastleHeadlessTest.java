package tests.headless;

import java.io.File;
import java.util.ArrayList;

import org.junit.Ignore;
import org.junit.Test;

import crypto.HeadlessCryptoScanner;
import crypto.analysis.Constants.Ruleset;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
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

		scanner.exec();
	  	assertErrors();
	}
	
	@Ignore
	@Test
	public void testBCAsymmetricCipherExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BCAsymmetricCipherExamples").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, IDEALCrossingTestingFramework.RESOURCE_PATH, Ruleset.BouncyCastle);
		
		setErrorsCount("<pattern.MacTest: void testMac2()>", IncompleteOperationError.class, 1);
		
		// Ignored
		setErrorsCount("<pattern.MacTest: void testMac1()>", ImpreciseValueExtractionError.class, 1);
		
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
	
	@Test
	@SuppressWarnings("serial")
	public void testBCEllipticCurveExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BCEllipticCurveExamples").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, IDEALCrossingTestingFramework.RESOURCE_PATH, Ruleset.BouncyCastle);
		setErrorsCount("<crypto.ECElGamalEncryptorTest: void testOne()>", RequiredPredicateError.class, new ArrayList<findingType>(){
			{
				add(findingType.TRUE_POSITIVE);
				add(findingType.TRUE_POSITIVE);
			}
		});
		setErrorsCount("<crypto.ECElGamalEncryptorTest: void testTwo()>", TypestateError.class, new ArrayList<findingType>(){
			{
				add(findingType.TRUE_POSITIVE);
			}
		});
		setErrorsCount("<crypto.ECElGamalEncryptorTest: void testThree(java.lang.String)>", RequiredPredicateError.class, new ArrayList<findingType>() {
			{
				add(findingType.FALSE_POSITIVE);
			}
		}); //issue29:Crypto-API-Rules
		setErrorsCount("<crypto.ECElGamalEncryptorTest: void testFour(java.lang.String)>", RequiredPredicateError.class, new ArrayList<findingType>() {
			{
				add(findingType.FALSE_POSITIVE);
			}
		}); //issue29:Crypto-API-Rules
		
		setErrorsCount("<params.ECPublicKeyParametersTest: void testOne(java.lang.String)>", RequiredPredicateError.class, new ArrayList<findingType>() {
			{
				add(findingType.TRUE_POSITIVE);
				add(findingType.TRUE_POSITIVE);
			}
		});
		setErrorsCount("<params.ECPrivateKeyParametersTest: void testOne(java.lang.String)>", RequiredPredicateError.class, new ArrayList<findingType>() {
			{
				add(findingType.TRUE_POSITIVE);
				add(findingType.TRUE_POSITIVE);
			}
		});
		setErrorsCount("<params.ParametersWithRandomTest: void testOne(java.lang.String)>", RequiredPredicateError.class, new ArrayList<findingType>() {
			{
				add(findingType.TRUE_POSITIVE);
				add(findingType.TRUE_POSITIVE);
				add(findingType.TRUE_POSITIVE);
			}
		});
		setErrorsCount("<params.ParametersWithRandomTest: void testThree(java.lang.String)>", RequiredPredicateError.class, new ArrayList<findingType>() {
			{
				add(findingType.TRUE_POSITIVE);
				add(findingType.TRUE_POSITIVE);
				add(findingType.TRUE_POSITIVE);
				add(findingType.TRUE_POSITIVE);
			}
		});
		setErrorsCount("<params.ECDomainParametersTest: void testThree(java.lang.String)>", RequiredPredicateError.class, new ArrayList<findingType>() {
			{
				add(findingType.TRUE_POSITIVE);
			}
		});
		
		setErrorsCount("<crypto.ECElGamalDecryptorTest: void testOne(java.lang.String)>", RequiredPredicateError.class, new ArrayList<findingType>() {
			{
				add(findingType.TRUE_POSITIVE);
			}
		});
		setErrorsCount("<crypto.ECElGamalDecryptorTest: void testTwo(java.lang.String)>", RequiredPredicateError.class, new ArrayList<findingType>() {
			{
				add(findingType.TRUE_POSITIVE);
			}
		});
		setErrorsCount("<crypto.ECElGamalDecryptorTest: void testThree()>", RequiredPredicateError.class, new ArrayList<findingType>() {
			{
				add(findingType.TRUE_POSITIVE);
			}
		});
		setErrorsCount("<crypto.ECElGamalDecryptorTest: void testFour()>", TypestateError.class, new ArrayList<findingType>() {
			{
				add(findingType.TRUE_POSITIVE);
			}
		});
		
		setErrorsCount("<constants.Constants: void <clinit>()>", RequiredPredicateError.class, new ArrayList<findingType>() {
			{
				add(findingType.TRUE_POSITIVE);
				add(findingType.TRUE_POSITIVE);
			}
		});
		scanner.exec();
	  	assertErrors();
	}
}
