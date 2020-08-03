package tests.headless;

import java.io.File;

import org.junit.Test;

import crypto.HeadlessCryptoScanner;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.ForbiddenMethodError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import tests.headless.FindingsType.FalsePositives;

/**
 * @author Enri Ozuni
 */
public class BragaCryptoGoodusesTest extends AbstractHeadlessTest {
	/**
	 * The following headless tests are deducted from the Braga et al. paper which
	 * benchmarks several static analyzer tools against several hundred test
	 * projects containing various Cryptographic providers of the JCA framework. For
	 * the creation of these headless tests, various projects from Braga paper were
	 * considered and used for testing the provider detection functionality. The
	 * test projects of the paper can be found in the link below:
	 * 
	 * https://bitbucket.org/alexmbraga/cryptogooduses/src/master/
	 */

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/cai/alwaysDefineCSP/
	@Test
	public void alwaysDefineCSPExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/alwaysDefineCSP")
				.getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.DefinedProvider1: void main(java.lang.String[])>", IncompleteOperationError.class, 3);
		// DefinedProvider2 not included as test due to being similar to DefinedProvider1 above
		setErrorsCount("<example.DefinedProvider3: void main(java.lang.String[])>", ConstraintError.class, 1);
		// The whole test cases based on the below link projects were not included due to being similar to the test above
		// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/br/avoidStatisticPRNG/
		// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/br/avoidFixedPredictableSeed/
		setErrorsCount("<example.DefinedProvider4: void main(java.lang.String[])>", IncompleteOperationError.class, 6);
		setErrorsCount("<example.DefinedProvider5: void main(java.lang.String[])>", IncompleteOperationError.class, 3);
		setErrorsCount("<example.DefinedProvider6: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.DefinedProvider6: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.DefinedProvider7: void main(java.lang.String[])>", IncompleteOperationError.class, 4);
		setErrorsCount("<example.DefinedProvider7: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<example.DefinedProvider7: void main(java.lang.String[])>", TypestateError.class, 2);

		scanner.exec();
		assertErrors();
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/cib/avoidCodingErros/
	@Test
	public void avoidCodingErrosExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidCodingErros")
				.getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.PBEwLargeCountAndRandomSalt: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.PBEwLargeCountAndRandomSalt: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.PBEwLargeCountAndRandomSalt: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<example.PBEwLargeCountAndRandomSalt: void main(java.lang.String[])>", ForbiddenMethodError.class, 1);
		setErrorsCount("<example.DoNotSaveToString: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.GenerateRandomIV: void main(java.lang.String[])>", IncompleteOperationError.class, 4);
		setErrorsCount("<example.GenerateRandomIV: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<example.GenerateRandomIV: void main(java.lang.String[])>", TypestateError.class, 2);

		scanner.exec();
		assertErrors();
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkm/avoidConstantPwdPBE/
	@Test
	public void avoidConstantPwdPBEExamples() {
		String mavenProjectPath = new File(
				"../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidConstantPwdPBE").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.PBEwParameterPassword: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.PBEwParameterPassword: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<example.PBEwParameterPassword: void main(java.lang.String[])>", ForbiddenMethodError.class, 1);
		setErrorsCount("<example.PBEwParameterPassword: void main(java.lang.String[])>", ConstraintError.class, 1);

		scanner.exec();
		assertErrors();
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkc/enc/avoidDeterministicRSA/
	@Test
	public void avoidDeterministicRSAExamples() {
		String mavenProjectPath = new File(
				"../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidDeterministicRSA").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.UseOAEPForRSA: void main(java.lang.String[])>", IncompleteOperationError.class, 4);
		setErrorsCount("<example.UseOAEPForRSA: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<example.UseOAEPForRSA: void main(java.lang.String[])>", TypestateError.class, 2);
		// UsePKCS1ForRSA not included as test due to being similar to UseOAEPForRSA above
		// Also tests from the following projects were not included due to being similar to project the test above is based
		// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkc/ecc/securecurves/
		// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pkc/ecc/insecurecurves/
		
		scanner.exec();
		assertErrors();
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkm/avoidHardcodedKeys/
	@Test
	public void avoidHardcodedKeysExamples() {
		String mavenProjectPath = new File(
				"../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidHardcodedKeys").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.UseDynamicKeyFor3DES: void main(java.lang.String[])>", ConstraintError.class, 3);
		setErrorsCount("<example.UseDynamicKeyFor3DES: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.UseDynamicKeyFor3DES: void main(java.lang.String[])>", TypestateError.class, 1);
		// UseDynamicKeyForAES not included as test due to being similar to UseDynamicKeyFor3DES
		setErrorsCount("<example.UseDynamicKeyforMAC1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);

		scanner.exec();
		assertErrors();
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkm/avoidImproperKeyLen/
	@Test
	public void avoidImproperKeyLenExamples() {
		String mavenProjectPath = new File(
				"../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidImproperKeyLen").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_1: void main(java.lang.String[])>", TypestateError.class, 1);
		// SecureConfig128bitsRSA_3072x384_1, SecureConfig128bitsRSA_4096x512_1 tests not included as being similar to above test
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_2: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_2: void main(java.lang.String[])>", TypestateError.class, 1);
		// SecureConfig128bitsRSA_3072x384_2, SecureConfig128bitsRSA_4096x512_2 tests not included as being similar to above test
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_1: void main(java.lang.String[])>", ConstraintError.class, 1);
		// SecureConfig192bitsRSA_7680x512_1 test not included due to being similar to SecureConfig192bitsRSA_7680x384_1 above

		scanner.exec();
		assertErrors();
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pdf/avoidInsecureDefaults/
	@Test
	public void avoidInsecureDefaultsExamples() {
		String mavenProjectPath = new File(
				"../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidInsecureDefaults").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.UseQualifiedNameForPBE1: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<example.UseQualifiedNameForPBE1: void main(java.lang.String[])>", ForbiddenMethodError.class, 1);
		setErrorsCount("<example.UseQualifiedNameForPBE1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.UseQualifiedNameForPBE1: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.UseExplicitMode1: void main(java.lang.String[])>", IncompleteOperationError.class, 4);
		setErrorsCount("<example.UseExplicitMode1: void main(java.lang.String[])>", RequiredPredicateError.class, 3);
		setErrorsCount("<example.UseExplicitMode1: void main(java.lang.String[])>", TypestateError.class, 2);
		setErrorsCount("<example.UseExplicitPadding1: void main(java.lang.String[])>", IncompleteOperationError.class, 4);
		setErrorsCount("<example.UseExplicitPadding1: void main(java.lang.String[])>", RequiredPredicateError.class, 3);
		setErrorsCount("<example.UseExplicitPadding1: void main(java.lang.String[])>", TypestateError.class, 2);
		setErrorsCount("<example.UseQualifiedNameForRSAOAEP: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.UseQualifiedNameForRSAOAEP: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.UseQualifiedParamsForRSAOAEP: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.UseQualifiedParamsForRSAOAEP: void main(java.lang.String[])>", RequiredPredicateError.class, 1);

		scanner.exec();
		assertErrors();
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/wc/avoidInsecureHash/
	@Test
	public void avoidInsecureHashExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidInsecureHash")
				.getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.UseSHA2_1: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		// UseSHA3_2 test not included due to being similar to the test above
		setErrorsCount("<example.UseSHA2_2: void main(java.lang.String[])>", IncompleteOperationError.class, 4);
		setErrorsCount("<example.UseSHA2_2: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.UseSHA3_1: void main(java.lang.String[])>", ConstraintError.class, 4);
		setErrorsCount("<example.UseSHA3_1: void main(java.lang.String[])>", IncompleteOperationError.class, 4);

		scanner.exec();
		assertErrors();
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkc/enc/avoidInsecurePadding/
	@Test
	public void avoidInsecurePaddingExamples() {
		String mavenProjectPath = new File(
				"../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidInsecurePadding").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.OAEP_2048x256_1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.OAEP_2048x256_1: void main(java.lang.String[])>", TypestateError.class, 1);
		// OAEP_2048x384_1, OAEP_2048x512_1 tests not included due to being similar to above test
		setErrorsCount("<example.OAEP_2048x256_2: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.OAEP_2048x256_2: void main(java.lang.String[])>", TypestateError.class, 1);
		// OAEP_2048x384_2, OAEP_2048x512_2 tests not included due to being similar to above test
		
		scanner.exec();
		assertErrors();
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkc/sign/avoidInsecurePadding/
	@Test
	public void avoidInsecurePaddingSignExamples() {
		String mavenProjectPath = new File(
				"../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidInsecurePaddingSign").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.PSSwSHA256Signature: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.PSSwSHA256Signature: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.PSSwSHA384Signature: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.PSSwSHA384Signature: void main(java.lang.String[])>", ConstraintError.class, 1);
		// PSSwSHA512Signature test not included due to being similar to above test

		scanner.exec();
		assertErrors();
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/wc/avoidInsecureSymEnc/
	@Test
	public void avoidInsecureSymEncExamples() {
		String mavenProjectPath = new File(
				"../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidInsecureSymEnc").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.UseAEADwAES_GCM: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.UseAES_CTR: void main(java.lang.String[])>", TypestateError.class, 2);
		setErrorsCount("<example.UseAES_CTR: void main(java.lang.String[])>", RequiredPredicateError.class, 5);

		scanner.exec();
		assertErrors();
	}

	// This test case corresponds to the following projects in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkm/avoidKeyReuseInStreams/
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pdf/avoidSideChannels/
	@Test
	public void avoidKeyReuseInStreamsExamples() {
		String mavenProjectPath = new File(
				"../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidKeyReuseInStreams").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.DoNotReuseKeyStreamCipher1: void main(java.lang.String[])>", RequiredPredicateError.class, 3);
		setErrorsCount("<example.DoNotReuseKeyStreamCipher1: void main(java.lang.String[])>", TypestateError.class, 1);
		// DoNotReuseKeyStreamCipher2, DoNotReuseKeyStreamCipher4, DoNotReuseKeyStreamCipher5 are similar to test above	
		setErrorsCount("<example.DoNotReuseKeyStreamCipher3: void main(java.lang.String[])>", TypestateError.class, 1);

		scanner.exec();
		assertErrors();
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/icv/completeValidation/
	@Test
	public void completeValidationExamples() {
		String mavenProjectPath = new File(
				"../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/completeValidation").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.SSLClientCertPathCRLValidation: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		setErrorsCount("<example.SSLClientCompleteValidation: void main(java.lang.String[])>", IncompleteOperationError.class, 1);

		scanner.exec();
		assertErrors();
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkc/ka/DHandECDH/
	@Test
	public void DHandECDHExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/DHandECDH")
				.getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.NonAuthenticatedEphemeralECDH_128: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<example.NonAuthenticatedEphemeralECDH_128: void main(java.lang.String[])>", RequiredPredicateError.class, 4);
		setErrorsCount("<example.NonAuthenticatedEphemeralECDH_192: void main(java.lang.String[])>", ConstraintError.class, 4);
		setErrorsCount("<example.NonAuthenticatedEphemeralECDH_192: void main(java.lang.String[])>", RequiredPredicateError.class, 4);
		// NonAuthenticatedEphemeralECDH_256 test not included due to being similar to above test
		setErrorsCount("<example.NonAuthenticatedDH_2048: void main(java.lang.String[])>", RequiredPredicateError.class, 6);
		setErrorsCount("<example.NonAuthenticatedEphemeralDH_2048: void main(java.lang.String[])>", RequiredPredicateError.class, 4);

		scanner.exec();
		assertErrors();
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkc/sign/digSignDSAandECDSA/
	@Test
	public void digSignDSAandECDSAExamples() {
		String mavenProjectPath = new File(
				"../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/digSignDSAandECDSA").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.BC_128bits_DSA3072xSHA256: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<example.BC_128bits_DSA3072xSHA256: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.RandomMessageNonceECDSA: void main(java.lang.String[])>", RequiredPredicateError.class, 5);
		setErrorsCount("<example.RandomMessageNonceECDSA: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.SUN_112bits_ECDSA224wSHA224: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<example.SUN_112bits_ECDSA224wSHA224: void main(java.lang.String[])>", ConstraintError.class, 3);
		// SUN_192bits_ECDSA384wSHA384, SUN_256bits_ECDSA571wSHA512 tests not included due to being similar to test above
		
		scanner.exec();
		assertErrors();
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkc/sign/digSignRSA/
	@Test
	public void digSignRSAExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/digSignRSA")
				.getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.PKCS1_112bitsSign2048xSHA256_1: void main(java.lang.String[])>", TypestateError.class, 1);
		// PKCS1_112bitsSign2048xSHA256_2, PKCS1_128bitsSign3072xSHA256_1, PKCS1_128bitsSign3072xSHA256_2 are similar to above test
		setErrorsCount("<example.PKCS1_192bitsSign7680xSHA384_1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.PKCS1_192bitsSign7680xSHA384_1: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<example.PKCS1_192bitsSign7680xSHA384_1: void main(java.lang.String[])>", ConstraintError.class, 1);
		// PKCS1_192bitsSign7680xSHA384_2 test not included due to being similar to above test
		setErrorsCount("<example.PSS_112bitsSign2048xSHA256_1: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.PSS_112bitsSign2048xSHA256_1: void main(java.lang.String[])>", TypestateError.class, 1);
		// PSS_128bitsSign3072xSHA256_1 not included due to being similar to above test
		setErrorsCount("<example.PSS_112bitsSign2048xSHA256_2: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.PSS_112bitsSign2048xSHA256_2: void main(java.lang.String[])>", TypestateError.class, 1);
		// PSS_128bitsSign3072xSHA256_2 not included due to being similar to above test
		setErrorsCount("<example.PSS_192bitsSign7680xSHA384_1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.PSS_192bitsSign7680xSHA384_1: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<example.PSS_192bitsSign7680xSHA384_1: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<example.PSS_192bitsSign7680xSHA384_2: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.PSS_192bitsSign7680xSHA384_2: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<example.PSS_192bitsSign7680xSHA384_2: void main(java.lang.String[])>", ConstraintError.class, 2);

		scanner.exec();
		assertErrors();
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/cib/doNotPrintSecrets/
	@Test
	public void doNotPrintSecretsExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/doNotPrintSecrets")
				.getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.DoNotPrintECDHPrivKey1: void main(java.lang.String[])>", ConstraintError.class, 4);
		setErrorsCount("<example.DoNotPrintECDHSecret1: void main(java.lang.String[])>", ConstraintError.class, 4);
		setErrorsCount("<example.DoNotPrintECDSAPrivKey1: void main(java.lang.String[])>", ConstraintError.class, 3);
		setErrorsCount("<example.DoNotPrintECDSAPrivKey1: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<example.DoNotPrintPrivKey1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.DoNotPrintPrivKey1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.DoNotPrintSecKey1: void main(java.lang.String[])>", TypestateError.class, 1);

		scanner.exec();
		assertErrors();
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pdf/encryptThenHashOrMAC/
	@Test
	public void encryptThenHashOrMACExamples() {
		String mavenProjectPath = new File(
				"../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/encryptThenHashOrMAC").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.EncryptThenHashCiphertextAndIV: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.EncryptThenHashCiphertextAndIV: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.EncryptThenMacCiphertextAndIV: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.EncryptThenMacCiphertextAndIV: void main(java.lang.String[])>", RequiredPredicateError.class, 1);

		scanner.exec();
		assertErrors();
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/ivm/randomIV/
	@Test
	public void randomIVExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/randomIV")
				.getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.UseRandomIVsForCBC: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<example.UseRandomIVsForCBC: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<example.UseRandomIVsForCBC: void main(java.lang.String[])>", TypestateError.class, 2);
		setErrorsCount("<example.UseRandomIVsForCFB: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.UseRandomIVsForCFB: void main(java.lang.String[])>", IncompleteOperationError.class, 4);
		setErrorsCount("<example.UseRandomIVsForCFB: void main(java.lang.String[])>", TypestateError.class, 2);
		setErrorsCount("<example.UseRandomIVsForCFB128: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.UseRandomIVsForCFB128: void main(java.lang.String[])>", IncompleteOperationError.class, 4);
		setErrorsCount("<example.UseRandomIVsForCFB128: void main(java.lang.String[])>", TypestateError.class, 2);
		setErrorsCount("<example.UseRandomIVsForCFB128: void main(java.lang.String[])>", ConstraintError.class, 2);

		scanner.exec();
		assertErrors();
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkc/enc/secureConfigsRSA/
	@Test
	public void secureConfigsRSAExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/secureConfigsRSA")
				.getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_1: void main(java.lang.String[])>", TypestateError.class, 1);
		// example.SecureConfig128bitsRSA_3072x384_1, SecureConfig128bitsRSA_4096x512_1 tests are similar to above test
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_2: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_2: void main(java.lang.String[])>", TypestateError.class, 1);
		// SecureConfig128bitsRSA_3072x384_2, SecureConfig128bitsRSA_4096x512_2 tests are similar to above test
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_1: void main(java.lang.String[])>", ConstraintError.class, 1);
		// SecureConfig192bitsRSA_7680x512_1 test not included due to being similar to the test above
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_2: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_2: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_2: void main(java.lang.String[])>", ConstraintError.class, 1);
		// SecureConfig192bitsRSA_7680x512_2 test not included due to being similar to the test above
		
		scanner.exec();
		assertErrors();
	}

	// This test case corresponds to the following projects in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pdf/secureStreamCipher/
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/wc/avoidInsecureMAC/
	@Test
	public void secureStreamCipherExamples() {
		String mavenProjectPath = new File(
				"../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/secureStreamCipher").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.UseMacWithMaleableStream: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.UseMacWithMaleableStream: void main(java.lang.String[])>", TypestateError.class, 1);

		scanner.exec();
		assertErrors();
	}

}
