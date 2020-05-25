package tests.headless;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;
import crypto.HeadlessCryptoScanner;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.ForbiddenMethodError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import tests.headless.FindingsType.FalseNegatives;
import tests.headless.FindingsType.FalsePositives;
import tests.headless.FindingsType.TruePositives;

/**
 * @author Enri Ozuni
 */
public class BragaCryptoTest extends AbstractHeadlessTest {
	/**
	 * The following headless tests are deducted from the Braga et al. paper which benchmarks
	 * several static analyzer tools against several hundred test projects containing various
	 * Cryptographic providers of the JCA framework. For the creation of these headless tests, 
	 * various projects from Braga paper were considered and used for testing the provider
	 * detection functionality. The test projects of the paper can be found in the link below
	 * and are distinguished on the paper by good uses and misuses:
	 * 
	 * https://bitbucket.org/alexmbraga/cryptogooduses/src/master/
	 * https://bitbucket.org/alexmbraga/cryptomisuses/src/master/
	 */
	
	
	// Start of headless tests from good uses projects
	// --------------------------------------------------------------------------------------------------------------------------------------------
	
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/cai/alwaysDefineCSP/		
	@Test
	public void alwaysDefineCSPExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/alwaysDefineCSP").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<example.DefinedProvider1: void main(java.lang.String[])>", IncompleteOperationError.class, 3);
		setErrorsCount("<example.DefinedProvider2: void main(java.lang.String[])>", IncompleteOperationError.class, 3);
		setErrorsCount("<example.DefinedProvider2: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<example.DefinedProvider3: void main(java.lang.String[])>", ConstraintError.class, 1);
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
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidCodingErros").getAbsolutePath();
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
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidConstantPwdPBE").getAbsolutePath();
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
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidDeterministicRSA").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<example.UseOAEPForRSA: void main(java.lang.String[])>", IncompleteOperationError.class, 4);
		setErrorsCount("<example.UseOAEPForRSA: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<example.UseOAEPForRSA: void main(java.lang.String[])>", TypestateError.class, 2);
		setErrorsCount("<example.UsePKCS1ForRSA: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<example.UsePKCS1ForRSA: void main(java.lang.String[])>", TypestateError.class, 2);
		setErrorsCount("<example.UsePKCS1ForRSA: void main(java.lang.String[])>", IncompleteOperationError.class, 4);

		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/br/avoidFixedPredictableSeed/
	@Test
	public void avoidFixedPredictableSeedExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidFixedPredictableSeed").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		setErrorsCount(RequiredPredicateError.class, new FalsePositives(1, ""), "<example.DoNotUseWeakSeed1: void main(java.lang.String[])>");
		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkm/avoidHardcodedKeys/
	@Test
	public void avoidHardcodedKeysExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidHardcodedKeys").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<example.UseDynamicKeyFor3DES: void main(java.lang.String[])>", ConstraintError.class, 3);
		setErrorsCount("<example.UseDynamicKeyFor3DES: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.UseDynamicKeyFor3DES: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.UseDynamicKeyForAES: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.UseDynamicKeyforMAC1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);

		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkm/avoidImproperKeyLen/
	@Test
	public void avoidImproperKeyLenExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidImproperKeyLen").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_2: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_2: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.SecureConfig128bitsRSA_3072x384_1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig128bitsRSA_3072x384_1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.SecureConfig128bitsRSA_3072x384_2: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig128bitsRSA_3072x384_2: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.SecureConfig128bitsRSA_4096x512_1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig128bitsRSA_4096x512_1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.SecureConfig128bitsRSA_4096x512_2: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig128bitsRSA_4096x512_2: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_1: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x512_1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x512_1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x512_1: void main(java.lang.String[])>", ConstraintError.class, 1);
		
		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pdf/avoidInsecureDefaults/
	@Test
	public void avoidInsecureDefaultsExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidInsecureDefaults").getAbsolutePath();
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
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidInsecureHash").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<example.UseSHA2_1: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		setErrorsCount("<example.UseSHA2_2: void main(java.lang.String[])>", IncompleteOperationError.class, 4);
		setErrorsCount("<example.UseSHA2_2: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.UseSHA3_1: void main(java.lang.String[])>", ConstraintError.class, 4);
		setErrorsCount("<example.UseSHA3_1: void main(java.lang.String[])>", IncompleteOperationError.class, 4);
		setErrorsCount("<example.UseSHA3_2: void main(java.lang.String[])>", IncompleteOperationError.class, 1);

		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/wc/avoidInsecureMAC/
	@Test
	public void avoidInsecureMACExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidInsecureMAC").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkc/enc/avoidInsecurePadding/
	@Test
	public void avoidInsecurePaddingExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidInsecurePadding").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<example.OAEP_2048x256_1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.OAEP_2048x256_1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.OAEP_2048x256_2: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.OAEP_2048x256_2: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.OAEP_2048x384_1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.OAEP_2048x384_1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.OAEP_2048x384_2: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.OAEP_2048x384_2: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.OAEP_2048x512_1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.OAEP_2048x512_1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.OAEP_2048x512_2: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.OAEP_2048x512_2: void main(java.lang.String[])>", TypestateError.class, 1);

		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkc/sign/avoidInsecurePadding/
	@Test
	public void avoidInsecurePaddingSignExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidInsecurePaddingSign").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<example.PSSwSHA256Signature: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.PSSwSHA256Signature: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.PSSwSHA384Signature: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.PSSwSHA384Signature: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.PSSwSHA512Signature: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.PSSwSHA512Signature: void main(java.lang.String[])>", ConstraintError.class, 1);

		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/wc/avoidInsecureSymEnc/
	@Test
	public void avoidInsecureSymEncExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidInsecureSymEnc").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<example.UseAEADwAES_GCM: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.UseAES_CTR: void main(java.lang.String[])>", TypestateError.class, 2);
		setErrorsCount("<example.UseAES_CTR: void main(java.lang.String[])>", RequiredPredicateError.class, 5);

		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkm/avoidKeyReuseInStreams/
	@Test
	public void avoidKeyReuseInStreamsExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidKeyReuseInStreams").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<example.DoNotReuseKeyStreamCipher1: void main(java.lang.String[])>", RequiredPredicateError.class, 3);
		setErrorsCount("<example.DoNotReuseKeyStreamCipher1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.DoNotReuseKeyStreamCipher2: void main(java.lang.String[])>", RequiredPredicateError.class, 3);
		setErrorsCount("<example.DoNotReuseKeyStreamCipher2: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.DoNotReuseKeyStreamCipher3: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.DoNotReuseKeyStreamCipher4: void main(java.lang.String[])>", RequiredPredicateError.class, 3);
		setErrorsCount("<example.DoNotReuseKeyStreamCipher4: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.DoNotReuseKeyStreamCipher5: void main(java.lang.String[])>", RequiredPredicateError.class, 3);
		setErrorsCount("<example.DoNotReuseKeyStreamCipher5: void main(java.lang.String[])>", TypestateError.class, 1);

		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pdf/avoidSideChannels/
	@Test
	public void avoidSideChannelsExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidSideChannels").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/br/avoidStatisticPRNG/
	@Test
	public void avoidStatisticPRNGExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidStatisticPRNG").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/icv/completeValidation/
	@Test
	public void completeValidationExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/completeValidation").getAbsolutePath();
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
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/DHandECDH").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<example.NonAuthenticatedEphemeralECDH_128: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<example.NonAuthenticatedEphemeralECDH_128: void main(java.lang.String[])>", RequiredPredicateError.class, 4);
		setErrorsCount("<example.NonAuthenticatedEphemeralECDH_192: void main(java.lang.String[])>", ConstraintError.class, 4);
		setErrorsCount("<example.NonAuthenticatedEphemeralECDH_192: void main(java.lang.String[])>", RequiredPredicateError.class, 4);
		setErrorsCount("<example.NonAuthenticatedEphemeralECDH_256: void main(java.lang.String[])>", ConstraintError.class, 4);
		setErrorsCount("<example.NonAuthenticatedEphemeralECDH_256: void main(java.lang.String[])>", RequiredPredicateError.class, 4);
		setErrorsCount("<example.NonAuthenticatedDH_2048: void main(java.lang.String[])>", RequiredPredicateError.class, 6);
		setErrorsCount("<example.NonAuthenticatedEphemeralDH_2048: void main(java.lang.String[])>", RequiredPredicateError.class, 4);
		
		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkc/sign/digSignDSAandECDSA/
	@Test
	public void digSignDSAandECDSAExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/digSignDSAandECDSA").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<example.BC_128bits_DSA3072xSHA256: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<example.BC_128bits_DSA3072xSHA256: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.RandomMessageNonceECDSA: void main(java.lang.String[])>", RequiredPredicateError.class, 5);
		setErrorsCount("<example.RandomMessageNonceECDSA: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.SUN_112bits_ECDSA224wSHA224: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<example.SUN_112bits_ECDSA224wSHA224: void main(java.lang.String[])>", ConstraintError.class, 3);
		setErrorsCount("<example.SUN_192bits_ECDSA384wSHA384: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<example.SUN_192bits_ECDSA384wSHA384: void main(java.lang.String[])>", ConstraintError.class, 3);
		setErrorsCount("<example.SUN_256bits_ECDSA571wSHA512: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<example.SUN_256bits_ECDSA571wSHA512: void main(java.lang.String[])>", ConstraintError.class, 3);
						
		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkc/sign/digSignRSA/
	@Test
	public void digSignRSAExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/digSignRSA").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<example.PKCS1_112bitsSign2048xSHA256_1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.PKCS1_112bitsSign2048xSHA256_2: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.PKCS1_128bitsSign3072xSHA256_1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.PKCS1_128bitsSign3072xSHA256_2: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.PKCS1_192bitsSign7680xSHA384_1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.PKCS1_192bitsSign7680xSHA384_1: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<example.PKCS1_192bitsSign7680xSHA384_1: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.PKCS1_192bitsSign7680xSHA384_2: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.PKCS1_192bitsSign7680xSHA384_2: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<example.PKCS1_192bitsSign7680xSHA384_2: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.PSS_112bitsSign2048xSHA256_1: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.PSS_112bitsSign2048xSHA256_1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.PSS_112bitsSign2048xSHA256_2: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.PSS_112bitsSign2048xSHA256_2: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.PSS_128bitsSign3072xSHA256_1: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.PSS_128bitsSign3072xSHA256_1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.PSS_128bitsSign3072xSHA256_2: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.PSS_128bitsSign3072xSHA256_2: void main(java.lang.String[])>", TypestateError.class, 1);
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
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/doNotPrintSecrets").getAbsolutePath();
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
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/encryptThenHashOrMAC").getAbsolutePath();
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
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/randomIV").getAbsolutePath();
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
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/secureConfigsRSA").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_1: void main(java.lang.String[])>", TypestateError.class, 1);				
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_2: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_2: void main(java.lang.String[])>", TypestateError.class, 1);				
		setErrorsCount("<example.SecureConfig128bitsRSA_3072x384_1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig128bitsRSA_3072x384_1: void main(java.lang.String[])>", TypestateError.class, 1);				
		setErrorsCount("<example.SecureConfig128bitsRSA_3072x384_2: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig128bitsRSA_3072x384_2: void main(java.lang.String[])>", TypestateError.class, 1);				
		setErrorsCount("<example.SecureConfig128bitsRSA_4096x512_1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig128bitsRSA_4096x512_1: void main(java.lang.String[])>", TypestateError.class, 1);				
		setErrorsCount("<example.SecureConfig128bitsRSA_4096x512_2: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig128bitsRSA_4096x512_2: void main(java.lang.String[])>", TypestateError.class, 1);				
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_1: void main(java.lang.String[])>", TypestateError.class, 1);				
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_1: void main(java.lang.String[])>", ConstraintError.class, 1);				
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_2: void main(java.lang.String[])>", TypestateError.class, 1);				
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_2: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_2: void main(java.lang.String[])>", ConstraintError.class, 1);				
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x512_1: void main(java.lang.String[])>", TypestateError.class, 1);				
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x512_1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x512_1: void main(java.lang.String[])>", ConstraintError.class, 1);				
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x512_2: void main(java.lang.String[])>", TypestateError.class, 1);				
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x512_2: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x512_2: void main(java.lang.String[])>", ConstraintError.class, 1);				
																				
		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkc/ecc/securecurves/
	@Test
	public void securecurvesExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/securecurves").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
								
		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pdf/secureStreamCipher/
	@Test
	public void secureStreamCipherExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/secureStreamCipher").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<example.UseMacWithMaleableStream: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.UseMacWithMaleableStream: void main(java.lang.String[])>", TypestateError.class, 1);

		scanner.exec();
		assertErrors();
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------
	// End of headless tests from good uses projects
	
	
	// Start of headless tests from misuses projects
	// --------------------------------------------------------------------------------------------------------------------------------------------
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/wc/brokenInsecureHash/
	@Test
	public void brokenInsecureHashExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/brokenInsecureHash").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<wc.brokenInsecureHash.InsecureHashes1: void main(java.lang.String[])>", IncompleteOperationError.class, 3);
		setErrorsCount("<wc.brokenInsecureHash.InsecureHashes1: void main(java.lang.String[])>", ConstraintError.class, 3);
		setErrorsCount("<wc.brokenInsecureHash.InsecureHashes2: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		setErrorsCount("<wc.brokenInsecureHash.InsecureHashes3: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		setErrorsCount("<wc.brokenInsecureHash.InsecureHashes3: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<wc.brokenInsecureHash.InsecureHashes4: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		setErrorsCount("<wc.brokenInsecureHash.InsecureHashes4: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<wc.brokenInsecureHash.InsecureHashes5: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		setErrorsCount("<wc.brokenInsecureHash.InsecureHashes5: void main(java.lang.String[])>", ConstraintError.class, 1);

		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/wc/brokenInsecureMAC/
	@Test
	public void brokenInsecureMACExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/brokenInsecureMAC").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<wc.brokenInsecureMAC.InsecureMAC1: void main(java.lang.String[])>", ConstraintError.class, 4);
		setErrorsCount("<wc.brokenInsecureMAC.InsecureMAC1: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<wc.brokenInsecureMAC.InsecureMAC3: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<wc.brokenInsecureMAC.InsecureMAC3: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<wc.brokenInsecureMAC.InsecureMAC4: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<wc.brokenInsecureMAC.InsecureMAC4: void main(java.lang.String[])>", RequiredPredicateError.class, 1);

		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/icv/brokenSSLorTLS/
	@Test
	public void brokenSSLorTLSExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/brokenSSLorTLS").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<icv.brokenSSLorTLS.SSLctxNoKeyMgmtNoTrustMgmt1: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<icv.brokenSSLorTLS.SSLctxNoKeyMgmtNoTrustMgmt1: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<icv.brokenSSLorTLS.SSLctxNoKeyMgmtNoTrustMgmt2: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<icv.brokenSSLorTLS.SSLctxNoKeyMgmtNoTrustMgmt2: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<icv.brokenSSLorTLS.SSLctxNoKeyMgmtNoTrustMgmt3: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<icv.brokenSSLorTLS.SSLctxNoKeyMgmtNoTrustMgmt3: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<icv.brokenSSLorTLS.SSLctxNoKeyMgmtNoTrustMgmt4: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<icv.brokenSSLorTLS.SSLctxNoKeyMgmtNoTrustMgmt4: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<icv.brokenSSLorTLS.SSLctxNoKeyMgmtNoTrustMgmt5: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<icv.brokenSSLorTLS.SSLctxNoKeyMgmtNoTrustMgmt5: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<icv.brokenSSLorTLS.SSLctxNoKeyMgmtNoTrustMgmt6: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<icv.brokenSSLorTLS.SSLctxNoKeyMgmtNoTrustMgmt6: void main(java.lang.String[])>", ConstraintError.class, 1);

		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/cib/buggyIVgen/
	@Test
	public void buggyIVgenExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/buggyIVgen").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<cib.buggyIVgen.BuggyIVGen1: void main(java.lang.String[])>", TypestateError.class, 2);
		setErrorsCount("<cib.buggyIVgen.BuggyIVGen1: void main(java.lang.String[])>", RequiredPredicateError.class, 5);
		setErrorsCount("<cib.buggyIVgen.BuggyIVGen1: void main(java.lang.String[])>", IncompleteOperationError.class, 4);
		setErrorsCount("<cib.buggyIVgen.BuggyIVGen2: void main(java.lang.String[])>", TypestateError.class, 2);
		setErrorsCount("<cib.buggyIVgen.BuggyIVGen2: void main(java.lang.String[])>", RequiredPredicateError.class, 5);
		setErrorsCount("<cib.buggyIVgen.BuggyIVGen2: void main(java.lang.String[])>", IncompleteOperationError.class, 4);

		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/ivm/constantIV/
	@Test
	public void constantIVExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/constantIV").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<ivm.constantIV.FixedIV1: void main(java.lang.String[])>", IncompleteOperationError.class, 4);
		setErrorsCount("<ivm.constantIV.FixedIV1: void main(java.lang.String[])>", TypestateError.class, 2);
		setErrorsCount("<ivm.constantIV.FixedIV1: void main(java.lang.String[])>", RequiredPredicateError.class, 5);
		setErrorsCount("<ivm.constantIV.FixedIV2: void main(java.lang.String[])>", IncompleteOperationError.class, 4);
		setErrorsCount("<ivm.constantIV.FixedIV2: void main(java.lang.String[])>", TypestateError.class, 2);
		setErrorsCount("<ivm.constantIV.FixedIV2: void main(java.lang.String[])>", RequiredPredicateError.class, 5);
		setErrorsCount("<ivm.constantIV.SimpleIVConstant: void main(java.lang.String[])>", RequiredPredicateError.class, 1);

		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pkm/constantKey/
	@Test
	public void constantKeyExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/constantKey").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<pkm.constantKey.ConstantKey3DES: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<pkm.constantKey.ConstantKey3DES: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<pkm.constantKey.ConstantKey3DES: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pkm.constantKey.ConstantKeyAES1: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<pkm.constantKey.ConstantKeyAES1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pkm.constantKey.ConstantKeyAES2: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<pkm.constantKey.ConstantKeyAES2: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pkm.constantKey.ConstantKeyAES3: void main(java.lang.String[])>", RequiredPredicateError.class, 3);
		setErrorsCount("<pkm.constantKey.ConstantKeyAES3: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pkm.constantKey.HardCodedKey: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<pkm.constantKey.ConstantKeyforMAC: void main(java.lang.String[])>", RequiredPredicateError.class, 2);

		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pkm/constPwd4PBE/
	@Test
	public void constPwd4PBEExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/constPwd4PBE").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<pkm.constPwd4PBE.ConstPassword4PBE1: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<pkm.constPwd4PBE.ConstPassword4PBE1: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<pkm.constPwd4PBE.ConstPassword4PBE1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pkm.constPwd4PBE.ConstPassword4PBE1: void main(java.lang.String[])>", ForbiddenMethodError.class, 1);
		setErrorsCount("<pkm.constPwd4PBE.ConstPassword4PBE2: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<pkm.constPwd4PBE.ConstPassword4PBE2: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<pkm.constPwd4PBE.ConstPassword4PBE2: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pkm.constPwd4PBE.ConstPassword4PBE2: void main(java.lang.String[])>", ForbiddenMethodError.class, 1);

		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/wc/customCrypto/
	@Test
	public void customCryptoExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/customCrypto").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<wc.customCrypto.Manual3DES: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<wc.customCrypto.Manual3DES: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<wc.customCrypto.Manual3DES: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<wc.customCrypto.RawSignatureRSA: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<wc.customCrypto.RawSignatureRSA: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<wc.customCrypto.RawSignatureRSAwHash: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<wc.customCrypto.RawSignatureRSAwHash: void main(java.lang.String[])>", TypestateError.class, 1);
		
		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pkc/enc/deterministicCrypto/
	@Test
	public void deterministicCryptoExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/deterministicCrypto").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<pkc.enc.deterministicCrypto.DeterministicEncryptionRSA: void main(java.lang.String[])>", ForbiddenMethodError.class, 1);
		setErrorsCount("<pkc.enc.deterministicCrypto.DeterministicEncryptionRSA: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<pkc.enc.deterministicCrypto.DeterministicEncryptionRSA: void main(java.lang.String[])>", IncompleteOperationError.class, 4);
		setErrorsCount("<pkc.enc.deterministicCrypto.DeterministicEncryptionRSA: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<pkc.enc.deterministicCrypto.DeterministicEncryptionRSA: void main(java.lang.String[])>", TypestateError.class, 2);
		setErrorsCount("<pkc.enc.deterministicCrypto.DeterministicEncryptionRSA1: void main(java.lang.String[])>", ConstraintError.class, 3);
		setErrorsCount("<pkc.enc.deterministicCrypto.DeterministicEncryptionRSA1: void main(java.lang.String[])>", IncompleteOperationError.class, 4);
		setErrorsCount("<pkc.enc.deterministicCrypto.DeterministicEncryptionRSA1: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<pkc.enc.deterministicCrypto.DeterministicEncryptionRSA1: void main(java.lang.String[])>", TypestateError.class, 2);
		setErrorsCount("<pkc.enc.deterministicCrypto.DeterministicEncryptionRSA2: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<pkc.enc.deterministicCrypto.DeterministicEncryptionRSA2: void main(java.lang.String[])>", IncompleteOperationError.class, 4);
		setErrorsCount("<pkc.enc.deterministicCrypto.DeterministicEncryptionRSA2: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<pkc.enc.deterministicCrypto.DeterministicEncryptionRSA2: void main(java.lang.String[])>", TypestateError.class, 2);
		setErrorsCount("<pkc.enc.deterministicCrypto.DeterministicEncryptionRSA3: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<pkc.enc.deterministicCrypto.DeterministicEncryptionRSA3: void main(java.lang.String[])>", IncompleteOperationError.class, 4);
		setErrorsCount("<pkc.enc.deterministicCrypto.DeterministicEncryptionRSA3: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<pkc.enc.deterministicCrypto.DeterministicEncryptionRSA3: void main(java.lang.String[])>", TypestateError.class, 2);

		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/wc/deterministicSymEnc/
	@Test
	public void deterministicSymEncExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/deterministicSymEnc").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<wc.deterministicSymEnc.DeterministicEncryptionAESwECB1: void main(java.lang.String[])>", ConstraintError.class, 6);
		setErrorsCount("<wc.deterministicSymEnc.DeterministicEncryptionAESwECB1: void main(java.lang.String[])>", IncompleteOperationError.class, 12);
		setErrorsCount("<wc.deterministicSymEnc.DeterministicEncryptionAESwECB1: void main(java.lang.String[])>", TypestateError.class, 6);
		setErrorsCount("<wc.deterministicSymEnc.DeterministicEncryptionAESwECB2: void main(java.lang.String[])>", ForbiddenMethodError.class, 1);
		setErrorsCount("<wc.deterministicSymEnc.DeterministicEncryptionAESwECB2: void main(java.lang.String[])>", IncompleteOperationError.class, 4);
		setErrorsCount("<wc.deterministicSymEnc.DeterministicEncryptionAESwECB2: void main(java.lang.String[])>", TypestateError.class, 2);
	
		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/br/fixedSeed/
	@Test
	public void fixedSeedExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/fixedSeed").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<br.fixedSeed.FixedSeed1: void main(java.lang.String[])>", TypestateError.class, 4);
		setErrorsCount("<br.fixedSeed.FixedSeed1: void main(java.lang.String[])>", RequiredPredicateError.class, 6);
		setErrorsCount("<br.fixedSeed.FixedSeed1: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<br.fixedSeed.FixedSeed1: void main(java.lang.String[])>", IncompleteOperationError.class, 6);

		setErrorsCount("<br.fixedSeed.FixedSeed3: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<br.fixedSeed.FixedSeed4: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pkm/ImproperKeyLen/
	@Test
	public void improperKeyLenExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/ImproperKeyLen").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<pkm.ImproperKeyLen.ImproperKeySizeRSA1: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<pkm.ImproperKeyLen.ImproperKeySizeRSA2: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<pkm.ImproperKeyLen.ImproperKeySizeRSA3: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<pkm.ImproperKeyLen.ImproperKeySizeRSA4: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<pkm.ImproperKeyLen.ImproperKeySizeRSA5: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<pkm.ImproperKeyLen.ImproperKeySizeRSA6: void main(java.lang.String[])>", ConstraintError.class, 1);

		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/icv/incompleteValidation/
	@Test
	public void incompleteValidationExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/incompleteValidation").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<icv.incompleteValidation.ValidateCertChainButNoCRL: void main(java.lang.String[])>", IncompleteOperationError.class, 1);

		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pdf/insecureComboHashEnc/
	@Test
	public void insecureComboHashEncExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/insecureComboHashEnc").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<pdf.insecureComboHashEnc.ManualComboEncryptAndHash: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<pdf.insecureComboHashEnc.ManualComboEncryptAndHash: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pdf.insecureComboHashEnc.ManualComboEncryptThenHash1: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<pdf.insecureComboHashEnc.ManualComboEncryptThenHash1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pdf.insecureComboHashEnc.ManualComboEncryptThenHash2: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<pdf.insecureComboHashEnc.ManualComboEncryptThenHash2: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pdf.insecureComboHashEnc.ManualComboHashThenEncrypt: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<pdf.insecureComboHashEnc.ManualComboHashThenEncrypt: void main(java.lang.String[])>", TypestateError.class, 1);

		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pdf/insecureComboMacEnc/
	@Test
	public void insecureComboMacEncExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/insecureComboMacEnc").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<pdf.insecureComboMacEnc.ManualComboEncryptAndMAC: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<pdf.insecureComboMacEnc.ManualComboEncryptAndMAC: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pdf.insecureComboMacEnc.ManualComboEncryptThenMAC1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<pdf.insecureComboMacEnc.ManualComboEncryptThenMAC1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pdf.insecureComboMacEnc.ManualComboEncryptThenMAC2: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<pdf.insecureComboMacEnc.ManualComboEncryptThenMAC2: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pdf.insecureComboMacEnc.ManualComboMACThenEncrypt: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<pdf.insecureComboMacEnc.ManualComboMACThenEncrypt: void main(java.lang.String[])>", TypestateError.class, 1);

		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pkc/ecc/insecurecurves/
	@Test
	public void insecurecurvesExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/insecurecurves").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pdf/insecureDefault/
	@Test
	public void insecureDefaultExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/insecureDefault").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<pdf.insecureDefault.InsecureDefaultPBE: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<pdf.insecureDefault.InsecureDefaultPBE: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<pdf.insecureDefault.InsecureDefaultPBE: void main(java.lang.String[])>", ForbiddenMethodError.class, 1);
		setErrorsCount("<pdf.insecureDefault.InsecureDefault3DES: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<pdf.insecureDefault.InsecureDefault3DES: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<pdf.insecureDefault.InsecureDefault3DES: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pdf.insecureDefault.InsecureDefaultAES: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pdf.insecureDefault.InsecureDefaultAES: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<pdf.insecureDefault.InsecureDefaultOAEP: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<pdf.insecureDefault.InsecureDefaultOAEP: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<pdf.insecureDefault.InsecureDefaultOAEP: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pdf.insecureDefault.InsecureDefaultRSA: void main(java.lang.String[])>", IncompleteOperationError.class, 4);
		setErrorsCount("<pdf.insecureDefault.InsecureDefaultRSA: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<pdf.insecureDefault.InsecureDefaultRSA: void main(java.lang.String[])>", TypestateError.class, 2);
			
		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pkc/enc/insecurePadding/
	@Test
	public void insecurePaddingExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/insecurePadding").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<pkc.enc.insecurePadding.InsecurePaddingRSA1: void main(java.lang.String[])>", TypestateError.class, 2);
		setErrorsCount("<pkc.enc.insecurePadding.InsecurePaddingRSA1: void main(java.lang.String[])>", IncompleteOperationError.class, 4);
		setErrorsCount("<pkc.enc.insecurePadding.InsecurePaddingRSA1: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<pkc.enc.insecurePadding.InsecurePaddingRSA1: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<pkc.enc.insecurePadding.InsecurePaddingRSA2: void main(java.lang.String[])>", TypestateError.class, 2);
		setErrorsCount("<pkc.enc.insecurePadding.InsecurePaddingRSA2: void main(java.lang.String[])>", IncompleteOperationError.class, 4);
		setErrorsCount("<pkc.enc.insecurePadding.InsecurePaddingRSA2: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<pkc.enc.insecurePadding.InsecurePaddingRSA3: void main(java.lang.String[])>", TypestateError.class, 2);
		setErrorsCount("<pkc.enc.insecurePadding.InsecurePaddingRSA3: void main(java.lang.String[])>", IncompleteOperationError.class, 4);
		setErrorsCount("<pkc.enc.insecurePadding.InsecurePaddingRSA3: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
			
		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pkc/sign/insecurePadding/
	@Test
	public void insecurePaddingSignExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/insecurePaddingSign").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<pkc.sign.insecurePaddingSign.PKCS1Signature: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pkc.sign.insecurePaddingSign.PKCS1Signature: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<pkc.sign.insecurePaddingSign.PKCS1Signature1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pkc.sign.insecurePaddingSign.PKCS1Signature2: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pkc.sign.insecurePaddingSign.PSSwSHA1Signature: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pkc.sign.insecurePaddingSign.PSSwSHA1Signature: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<pkc.sign.insecurePaddingSign.PSSwSHA1Signature1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pkc.sign.insecurePaddingSign.PSSwSHA1Signature1: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<pkc.sign.insecurePaddingSign.PSSwSHA1Signature2: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pkc.sign.insecurePaddingSign.PSSwSHA1Signature2: void main(java.lang.String[])>", ConstraintError.class, 1);
			
		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pdf/insecureStreamCipher/
	@Test
	public void insecureStreamCipherExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/insecureStreamCipher").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<pdf.insecureStreamCipher.ConfusingBlockAndStream: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pdf.insecureStreamCipher.ConfusingBlockAndStream: void main(java.lang.String[])>", RequiredPredicateError.class, 5);
		setErrorsCount("<pdf.insecureStreamCipher.MalealableStreamCipher: void main(java.lang.String[])>", IncompleteOperationError.class, 3);
		setErrorsCount("<pdf.insecureStreamCipher.MalealableStreamCipher: void main(java.lang.String[])>", RequiredPredicateError.class, 4);
		setErrorsCount("<pdf.insecureStreamCipher.MalealableStreamCipher: void main(java.lang.String[])>", TypestateError.class, 2);

		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pkc/ka/issuesDHandECDH/
	@Test
	public void issuesDHandECDHExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/issuesDHandECDH").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<pkc.ka.issuesDHandECDH.NonAuthenticatedEphemeralDH_1024: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<pkc.ka.issuesDHandECDH.NonAuthenticatedEphemeralDH_1024: void main(java.lang.String[])>", RequiredPredicateError.class, 4);
		setErrorsCount("<pkc.ka.issuesDHandECDH.NonAuthenticatedEphemeralDH_512: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<pkc.ka.issuesDHandECDH.NonAuthenticatedEphemeralDH_512: void main(java.lang.String[])>", RequiredPredicateError.class, 4);
		setErrorsCount("<pkc.ka.issuesDHandECDH.NonAuthenticatedEphemeralECDH_112: void main(java.lang.String[])>", ConstraintError.class, 4);
		setErrorsCount("<pkc.ka.issuesDHandECDH.NonAuthenticatedEphemeralECDH_112: void main(java.lang.String[])>", RequiredPredicateError.class, 4);
		setErrorsCount("<pkc.ka.issuesDHandECDH.NonAuthenticatedEphemeralECDH_80: void main(java.lang.String[])>", ConstraintError.class, 4);
		setErrorsCount("<pkc.ka.issuesDHandECDH.NonAuthenticatedEphemeralECDH_80: void main(java.lang.String[])>", RequiredPredicateError.class, 4);
		setErrorsCount("<pkc.ka.issuesDHandECDH.NonAuthenticatedDH_512: void main(java.lang.String[])>", RequiredPredicateError.class, 6);
		setErrorsCount("<pkc.ka.issuesDHandECDH.NonAuthenticatedDH_1024: void main(java.lang.String[])>", RequiredPredicateError.class, 6);

		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pkm/keyReuseInStreamCipher/
	@Test
	public void keyReuseInStreamCipherExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/keyReuseInStreamCipher").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<pkm.keyReuseInStreamCipher.KeyReuseStreamCipher1: void main(java.lang.String[])>", RequiredPredicateError.class, 5);
		setErrorsCount("<pkm.keyReuseInStreamCipher.KeyReuseStreamCipher1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pkm.keyReuseInStreamCipher.KeyReuseStreamCipher2: void main(java.lang.String[])>", RequiredPredicateError.class, 5);
		setErrorsCount("<pkm.keyReuseInStreamCipher.KeyReuseStreamCipher2: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pkm.keyReuseInStreamCipher.KeyReuseStreamCipher3: void main(java.lang.String[])>", RequiredPredicateError.class, 5);
		setErrorsCount("<pkm.keyReuseInStreamCipher.KeyReuseStreamCipher3: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pkm.keyReuseInStreamCipher.KeyReuseStreamCipher4: void main(java.lang.String[])>", RequiredPredicateError.class, 6);
		setErrorsCount("<pkm.keyReuseInStreamCipher.KeyReuseStreamCipher4: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pkm.keyReuseInStreamCipher.KeyReuseStreamCipher5: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<pkm.keyReuseInStreamCipher.KeyReuseStreamCipher5: void main(java.lang.String[])>", TypestateError.class, 1);

		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/ivm/nonceReuse/
	@Test
	public void nonceReuseExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/nonceReuse").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<ivm.nonceReuse.NonceReuse1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<ivm.nonceReuse.NonceReuse1: void main(java.lang.String[])>", RequiredPredicateError.class, 2);

		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/cib/nullcipher/
	@Test
	public void nullcipherExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/nullcipher").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/cib/paramsPBE/
	@Test
	public void paramsPBEExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/paramsPBE").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<cib.paramsPBE.PBEwConstSalt1: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<cib.paramsPBE.PBEwConstSalt1: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<cib.paramsPBE.PBEwConstSalt1: void main(java.lang.String[])>", ForbiddenMethodError.class, 1);
		setErrorsCount("<cib.paramsPBE.PBEwConstSalt1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<cib.paramsPBE.PBEwSmallCount1: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<cib.paramsPBE.PBEwSmallCount1: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<cib.paramsPBE.PBEwSmallCount1: void main(java.lang.String[])>", ForbiddenMethodError.class, 1);
		setErrorsCount("<cib.paramsPBE.PBEwSmallCount1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<cib.paramsPBE.PBEwSmallSalt: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<cib.paramsPBE.PBEwSmallSalt: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<cib.paramsPBE.PBEwSmallSalt: void main(java.lang.String[])>", ForbiddenMethodError.class, 1);
		setErrorsCount("<cib.paramsPBE.PBEwSmallSalt: void main(java.lang.String[])>", TypestateError.class, 1);

		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/br/predictableSeed/
	@Test
	public void predictableSeedExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/predictableSeed").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<br.predictableSeed.ReusedSeed: void main(java.lang.String[])>", TypestateError.class, 0);
		setErrorsCount("<br.predictableSeed.ReusedSeed: void main(java.lang.String[])>", RequiredPredicateError.class, 4);
		
		setErrorsCount("<br.predictableSeed.LowEntropySeed1: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<br.predictableSeed.LowEntropySeed2: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<br.predictableSeed.LowEntropySeed3: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<br.predictableSeed.LowEntropySeed4: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/cib/printPrivSecKey/
	@Test
	public void printPrivSecKeyExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/printPrivSecKey").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<cib.printPrivSecKey.PrintECDHPrivKey1: void main(java.lang.String[])>", ConstraintError.class, 4);
		setErrorsCount("<cib.printPrivSecKey.PrintECDHSecret1: void main(java.lang.String[])>", ConstraintError.class, 4);
		setErrorsCount("<cib.printPrivSecKey.PrintECDSAPrivKey1: void main(java.lang.String[])>", ConstraintError.class, 3);
		setErrorsCount("<cib.printPrivSecKey.PrintECDSAPrivKey1: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<cib.printPrivSecKey.PrintPrivKey1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<cib.printPrivSecKey.PrintPrivKey1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<cib.printPrivSecKey.PrintSecKey1: void main(java.lang.String[])>", TypestateError.class, 1);

		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/wc/riskyInsecureCrypto/
	@Test
	public void riskyInsecureCryptoExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/riskyInsecureCrypto").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<wc.riskyInsecureCrypto.InsecureCryptoPBE: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<wc.riskyInsecureCrypto.InsecureCryptoPBE: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<wc.riskyInsecureCrypto.InsecureCryptoPBE: void main(java.lang.String[])>", ForbiddenMethodError.class, 1);
		setErrorsCount("<wc.riskyInsecureCrypto.InsecureCrypto3DES: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<wc.riskyInsecureCrypto.InsecureCrypto3DES: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<wc.riskyInsecureCrypto.InsecureCrypto3DES: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<wc.riskyInsecureCrypto.InsecureCryptoBlowfish: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<wc.riskyInsecureCrypto.InsecureCryptoBlowfish: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<wc.riskyInsecureCrypto.InsecureCryptoBlowfish: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<wc.riskyInsecureCrypto.InsecureCryptoDES: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<wc.riskyInsecureCrypto.InsecureCryptoDES: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<wc.riskyInsecureCrypto.InsecureCryptoDES: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<wc.riskyInsecureCrypto.InsecureCryptoDES_StreamCipher: void main(java.lang.String[])>", TypestateError.class, 5);
		setErrorsCount("<wc.riskyInsecureCrypto.InsecureCryptoDES_StreamCipher: void main(java.lang.String[])>", ConstraintError.class, 5);
		setErrorsCount("<wc.riskyInsecureCrypto.InsecureCryptoDES_StreamCipher: void main(java.lang.String[])>", RequiredPredicateError.class, 4);
		setErrorsCount("<wc.riskyInsecureCrypto.InsecureCryptoRC4_StreamCipher: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<wc.riskyInsecureCrypto.InsecureCryptoRC4_StreamCipher: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<wc.riskyInsecureCrypto.InsecureCryptoRC4_StreamCipher: void main(java.lang.String[])>", RequiredPredicateError.class, 1);

		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pdf/sideChannelAttacks/
	@Test
	public void sideChannelAttacksExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/sideChannelAttacks").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<pdf.sideChannelAttacks.HashVerificationVariableTime: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<pdf.sideChannelAttacks.PaddingOracle: boolean oracle(byte[],byte[])>", ConstraintError.class, 1);
		setErrorsCount("<pdf.sideChannelAttacks.PaddingOracle: boolean oracle(byte[],byte[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<pdf.sideChannelAttacks.PaddingOracle: byte[] encripta()>", RequiredPredicateError.class, 3);
		setErrorsCount("<pdf.sideChannelAttacks.PaddingOracle: byte[] encripta()>", ConstraintError.class, 1);
		setErrorsCount("<pdf.sideChannelAttacks.PaddingOracle: void <clinit>()>", RequiredPredicateError.class, 1);

		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/ivm/staticCounterCTR/
	@Test
	public void staticCounterCTRExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/staticCounterCTR").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<ivm.staticCounterCTR.StaticCounterCTR1: void main(java.lang.String[])>", RequiredPredicateError.class, 5);
		setErrorsCount("<ivm.staticCounterCTR.StaticCounterCTR1: void main(java.lang.String[])>", TypestateError.class, 2);

		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/br/statisticPRNG/
	@Test
	public void statisticPRNGExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/statisticPRNG").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/cai/undefinedCSP/
	@Test
	@Ignore
	public void undefinedCSPExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/undefinedCSP").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<cai.undefinedCSP.UndefinedProvider1: void main(java.lang.String[])>", IncompleteOperationError.class, 3);
		setErrorsCount("<cai.undefinedCSP.UndefinedProvider2: void main(java.lang.String[])>", IncompleteOperationError.class, 3);
		setErrorsCount("<cai.undefinedCSP.UndefinedProvider2: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount(RequiredPredicateError.class, new FalsePositives(1, ""), "<cai.undefinedCSP.UndefinedProvider3: void main(java.lang.String[])>");
		setErrorsCount("<cai.undefinedCSP.UndefinedProvider4: void main(java.lang.String[])>", IncompleteOperationError.class, 2);
		setErrorsCount("<cai.undefinedCSP.UndefinedProvider5: void main(java.lang.String[])>", IncompleteOperationError.class, 3);
		setErrorsCount("<cai.undefinedCSP.UndefinedProvider6: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<cai.undefinedCSP.UndefinedProvider6: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<cai.undefinedCSP.UndefinedProvider7: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<cai.undefinedCSP.UndefinedProvider7: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<cai.undefinedCSP.UndefinedProvider7: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<cai.undefinedCSP.UndefinedProvider8: void main(java.lang.String[])>", TypestateError.class, 2);
		setErrorsCount("<cai.undefinedCSP.UndefinedProvider8: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<cai.undefinedCSP.UndefinedProvider8: void main(java.lang.String[])>", IncompleteOperationError.class, 4);

		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pkc/enc/weakConfigsRSA/
	@Test
	public void weakConfigsRSAExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/weakConfigsRSA").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<pkc.enc.weakConfigsRSA.ImproperConfigRSA_1024x160_1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pkc.enc.weakConfigsRSA.ImproperConfigRSA_1024x160_1: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<pkc.enc.weakConfigsRSA.ImproperConfigRSA_1024x160_1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<pkc.enc.weakConfigsRSA.ImproperConfigRSA_1024x256_1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pkc.enc.weakConfigsRSA.ImproperConfigRSA_1024x256_1: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<pkc.enc.weakConfigsRSA.ImproperConfigRSA_1024x256_1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<pkc.enc.weakConfigsRSA.ImproperConfigRSA_1024x384_1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pkc.enc.weakConfigsRSA.ImproperConfigRSA_1024x384_1: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<pkc.enc.weakConfigsRSA.ImproperConfigRSA_1024x384_1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<pkc.enc.weakConfigsRSA.ImproperConfigRSA_2048x160_1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pkc.enc.weakConfigsRSA.ImproperConfigRSA_2048x160_1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<pkc.enc.weakConfigsRSA.ImproperConfigRSA_3072x160_1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pkc.enc.weakConfigsRSA.ImproperConfigRSA_3072x160_1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<pkc.enc.weakConfigsRSA.ImproperConfigRSA_384x160_1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pkc.enc.weakConfigsRSA.ImproperConfigRSA_384x160_1: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<pkc.enc.weakConfigsRSA.ImproperConfigRSA_384x160_1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<pkc.enc.weakConfigsRSA.ImproperConfigRSA_4096x160_1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pkc.enc.weakConfigsRSA.ImproperConfigRSA_4096x160_1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<pkc.enc.weakConfigsRSA.ImproperConfigRSA_512x160_1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pkc.enc.weakConfigsRSA.ImproperConfigRSA_512x160_1: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<pkc.enc.weakConfigsRSA.ImproperConfigRSA_512x160_1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<pkc.enc.weakConfigsRSA.ImproperConfigRSA_768x160_1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pkc.enc.weakConfigsRSA.ImproperConfigRSA_768x160_1: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<pkc.enc.weakConfigsRSA.ImproperConfigRSA_768x160_1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<pkc.enc.weakConfigsRSA.ImproperConfigRSA_768x256_1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pkc.enc.weakConfigsRSA.ImproperConfigRSA_768x256_1: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<pkc.enc.weakConfigsRSA.ImproperConfigRSA_768x256_1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
			
		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pkc/sign/weakSignatureECDSA/
	@Test
	public void weakSignatureECDSAExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/weakSignatureECDSA").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<pkc.sign.weakSignatureECDSA.RepeatedMessageNonceECDSA_1: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<pkc.sign.weakSignatureECDSA.RepeatedMessageNonceECDSA_2: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<pkc.sign.weakSignatureECDSA.RepeatedMessageNonceECDSA_3: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<pkc.sign.weakSignatureECDSA.RepeatedMessageNonceECDSA_4: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<pkc.sign.weakSignatureECDSA.SUN_80bits_ECDSA112wNONE1: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<pkc.sign.weakSignatureECDSA.SUN_80bits_ECDSA112wNONE1: void main(java.lang.String[])>", ConstraintError.class, 5);
		setErrorsCount("<pkc.sign.weakSignatureECDSA.SUN_80bits_ECDSA112wNONE2: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<pkc.sign.weakSignatureECDSA.SUN_80bits_ECDSA112wNONE2: void main(java.lang.String[])>", ConstraintError.class, 3);
		setErrorsCount("<pkc.sign.weakSignatureECDSA.SUN_80bits_ECDSA112wSHA1: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<pkc.sign.weakSignatureECDSA.SUN_80bits_ECDSA112wSHA1: void main(java.lang.String[])>", ConstraintError.class, 3);
		
		scanner.exec();
		assertErrors();
	}
	
	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptomisuses/src/master/pkc/sign/weakSignatureRSA/
	@Test
	public void weakSignatureRSAExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptomisuses/weakSignatureRSA").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<pkc.sign.weakSignatureRSA.PKCS1Sign1024xSHA1_1: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<pkc.sign.weakSignatureRSA.PKCS1Sign1024xSHA1_1: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<pkc.sign.weakSignatureRSA.PKCS1Sign1024xSHA1_1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pkc.sign.weakSignatureRSA.PKCS1Sign1024xSHA256_2: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<pkc.sign.weakSignatureRSA.PKCS1Sign1024xSHA256_2: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<pkc.sign.weakSignatureRSA.PKCS1Sign1024xSHA256_2: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pkc.sign.weakSignatureRSA.PSSw1024xSHA1_1: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<pkc.sign.weakSignatureRSA.PSSw1024xSHA1_1: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<pkc.sign.weakSignatureRSA.PSSw1024xSHA1_1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<pkc.sign.weakSignatureRSA.PSSw1024xSHA256_2: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<pkc.sign.weakSignatureRSA.PSSw1024xSHA256_2: void main(java.lang.String[])>", TypestateError.class, 1);
		
		scanner.exec();
		assertErrors();
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------
	// End of headless tests from misuses projects
	
}
