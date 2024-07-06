package tests.headless;

import crypto.HeadlessCryptoScanner;
import crypto.analysis.errors.CallToError;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.ForbiddenMethodError;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import org.junit.Ignore;
import org.junit.Test;
import tests.headless.FindingsType.FalsePositives;

import java.io.File;

/**
 * The following headless tests are deducted from the Braga et al. paper which
 * benchmarks several static analyzer tools against several hundred test
 * projects containing various Cryptographic providers of the JCA framework. For
 * the creation of these headless tests, various projects from Braga paper were
 * considered and used for testing the provider detection functionality. The
 * test projects of the paper can be found in the link below:
 * <a href="https://bitbucket.org/alexmbraga/cryptogooduses/src/master/">BragaCryptoGoodUses</a>
 *
 * @author Enri Ozuni
 */
public class BragaCryptoGoodUsesTest extends AbstractHeadlessTest {

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/cai/alwaysDefineCSP/
	@Test
	public void alwaysDefineCSPExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/alwaysDefineCSP")
				.getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.DefinedProvider1: void main(java.lang.String[])>", IncompleteOperationError.class, 3);
		setErrorsCount("<example.DefinedProvider2: void main(java.lang.String[])>", IncompleteOperationError.class, 3);
		setErrorsCount("<example.DefinedProvider2: void main(java.lang.String[])>", ConstraintError.class, 0);
		setErrorsCount("<example.DefinedProvider3: void main(java.lang.String[])>", ConstraintError.class, 0);
		setErrorsCount("<example.DefinedProvider3: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.DefinedProvider4: void main(java.lang.String[])>", IncompleteOperationError.class, 4);
		setErrorsCount("<example.DefinedProvider5: void main(java.lang.String[])>", IncompleteOperationError.class, 3);
		setErrorsCount("<example.DefinedProvider6: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.DefinedProvider6: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.DefinedProvider7: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.DefinedProvider7: void main(java.lang.String[])>", IncompleteOperationError.class, 2);
		setErrorsCount("<example.DefinedProvider7: void main(java.lang.String[])>", RequiredPredicateError.class, 5);

		scanner.run();
		assertErrors(scanner.getErrorCollection());
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/cib/avoidCodingErros/
	@Test
	public void avoidCodingErrorsExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidCodingErrors")
				.getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.PBEwLargeCountAndRandomSalt: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.PBEwLargeCountAndRandomSalt: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.PBEwLargeCountAndRandomSalt: void main(java.lang.String[])>", RequiredPredicateError.class, 4);
		setErrorsCount("<example.PBEwLargeCountAndRandomSalt: void main(java.lang.String[])>", ForbiddenMethodError.class, 1);
		setErrorsCount("<example.PBEwLargeCountAndRandomSalt: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		setErrorsCount("<example.DoNotSaveToString: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.DoNotSaveToString: void main(java.lang.String[])>", CallToError.class, 1);
		setErrorsCount("<example.GenerateRandomIV: void main(java.lang.String[])>", IncompleteOperationError.class, 2);
		setErrorsCount("<example.GenerateRandomIV: void main(java.lang.String[])>", RequiredPredicateError.class, 0);
		setErrorsCount("<example.GenerateRandomIV: void main(java.lang.String[])>", TypestateError.class, 2);
		setErrorsCount("<example.GenerateRandomIV: void main(java.lang.String[])>", CallToError.class, 1);

		scanner.run();
		assertErrors(scanner.getErrorCollection());
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
		setErrorsCount("<example.PBEwParameterPassword: void main(java.lang.String[])>", RequiredPredicateError.class, 4);
		setErrorsCount("<example.PBEwParameterPassword: void main(java.lang.String[])>", ForbiddenMethodError.class, 1);
		setErrorsCount("<example.PBEwParameterPassword: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.PBEwParameterPassword: void main(java.lang.String[])>", IncompleteOperationError.class, 1);

		scanner.run();
		assertErrors(scanner.getErrorCollection());
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkc/enc/avoidDeterministicRSA/
	@Test
	public void avoidDeterministicRSAExamples() {
		String mavenProjectPath = new File(
				"../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidDeterministicRSA").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		// positive test case
		setErrorsCount("<example.UseOAEPForRSA: void positiveTestCase()>", IncompleteOperationError.class, 2);
		setErrorsCount("<example.UseOAEPForRSA: void positiveTestCase()>", RequiredPredicateError.class, 0);
		setErrorsCount("<example.UseOAEPForRSA: void positiveTestCase()>", ConstraintError.class, 2);
		setErrorsCount("<example.UseOAEPForRSA: void positiveTestCase()>", TypestateError.class, 0);
		
		// negative test case
		setErrorsCount("<example.UseOAEPForRSA: void negativeTestCase()>", IncompleteOperationError.class, 2);
		setErrorsCount("<example.UseOAEPForRSA: void negativeTestCase()>", RequiredPredicateError.class, 5);
		setErrorsCount("<example.UseOAEPForRSA: void negativeTestCase()>", ConstraintError.class, 3);
		setErrorsCount("<example.UseOAEPForRSA: void negativeTestCase()>", TypestateError.class, 0);
		
		// positive test case
		setErrorsCount("<example.UsePKCS1ForRSA: void positiveTestCase()>", IncompleteOperationError.class, 2);
		setErrorsCount("<example.UsePKCS1ForRSA: void positiveTestCase()>", RequiredPredicateError.class, 0);
		setErrorsCount("<example.UsePKCS1ForRSA: void positiveTestCase()>", ConstraintError.class, 2);
		setErrorsCount("<example.UsePKCS1ForRSA: void positiveTestCase()>", TypestateError.class, 0);

		// negative test case
		setErrorsCount("<example.UsePKCS1ForRSA: void negativeTestCase()>", IncompleteOperationError.class, 2);
		setErrorsCount("<example.UsePKCS1ForRSA: void negativeTestCase()>", RequiredPredicateError.class, 5);
		setErrorsCount("<example.UsePKCS1ForRSA: void negativeTestCase()>", ConstraintError.class, 3);
		setErrorsCount("<example.UsePKCS1ForRSA: void negativeTestCase()>", TypestateError.class, 0);
		
		scanner.run();
		assertErrors(scanner.getErrorCollection());
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/br/avoidFixedPredictableSeed/
	@Test
	public void avoidFixedPredictableSeedExamples() {
		String mavenProjectPath = new File(
				"../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidFixedPredictableSeed").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<example.DoNotUseWeakSeed1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		
		scanner.run();
		assertErrors(scanner.getErrorCollection());
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
		setErrorsCount("<example.UseDynamicKeyFor3DES: void main(java.lang.String[])>", RequiredPredicateError.class, 4);
		setErrorsCount("<example.UseDynamicKeyFor3DES: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.UseDynamicKeyForAES: void main(java.lang.String[])>", TypestateError.class, 1);
		
		setErrorsCount("<example.UseDynamicKeyforMAC1: void main(java.lang.String[])>", RequiredPredicateError.class, 2);

		setErrorsCount("<example.UseDynamicKeyforMAC2: void main(java.lang.String[])>", TypestateError.class, 1);

		scanner.run();
		assertErrors(scanner.getErrorCollection());
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkm/avoidImproperKeyLen/
	@Test
	public void avoidImproperKeyLenExamples() {
		String mavenProjectPath = new File(
				"../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidImproperKeyLen").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		// positive test case
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_1: void positiveTestCase()>", ConstraintError.class, 1);
		// This is correct because there are two different 'SHA-256' variables, i.e. the pred is ensured on the first but not on the second one
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_1: void positiveTestCase()>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_1: void positiveTestCase()>", TypestateError.class, 1);
				
		// negative test case
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_1: void negativeTestCase()>", ConstraintError.class, 2);
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_1: void negativeTestCase()>", RequiredPredicateError.class, 7);
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_1: void negativeTestCase()>", TypestateError.class, 1);
		
		// positive test case
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_2: void positiveTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_2: void positiveTestCase()>", RequiredPredicateError.class, 0);
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_2: void positiveTestCase()>", TypestateError.class, 1);
				
		// negative test case
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_2: void negativeTestCase()>", ConstraintError.class, 2);
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_2: void negativeTestCase()>", RequiredPredicateError.class, 5);
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_2: void negativeTestCase()>", TypestateError.class, 1);
		
		// positive test case
		setErrorsCount("<example.SecureConfig128bitsRSA_3072x384_1: void positiveTestCase()>", ConstraintError.class, 1);
		// This is correct because there are two different 'SHA-256' variables, i.e. the pred is ensured on the first but not on the second one
		setErrorsCount("<example.SecureConfig128bitsRSA_3072x384_1: void positiveTestCase()>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig128bitsRSA_3072x384_1: void positiveTestCase()>", TypestateError.class, 1);

		// negative test case
		setErrorsCount("<example.SecureConfig128bitsRSA_3072x384_1: void negativeTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.SecureConfig128bitsRSA_3072x384_1: void negativeTestCase()>", RequiredPredicateError.class, 2);
		setErrorsCount("<example.SecureConfig128bitsRSA_3072x384_1: void negativeTestCase()>", TypestateError.class, 1);

		setErrorsCount("<example.SecureConfig128bitsRSA_3072x384_2: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.SecureConfig128bitsRSA_3072x384_2: void main(java.lang.String[])>", RequiredPredicateError.class, 0);
		setErrorsCount("<example.SecureConfig128bitsRSA_3072x384_2: void main(java.lang.String[])>", TypestateError.class, 1);
		
		// positive test case
		setErrorsCount("<example.SecureConfig128bitsRSA_4096x512_1: void positiveTestCase()>", ConstraintError.class, 1);
		// This is correct because there are two different 'SHA-256' variables, i.e. the pred is ensured on the first but not on the second one
		setErrorsCount("<example.SecureConfig128bitsRSA_4096x512_1: void positiveTestCase()>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig128bitsRSA_4096x512_1: void positiveTestCase()>", TypestateError.class, 1);
		
		// negative test case
		setErrorsCount("<example.SecureConfig128bitsRSA_4096x512_1: void negativeTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.SecureConfig128bitsRSA_4096x512_1: void negativeTestCase()>", RequiredPredicateError.class, 2);
		setErrorsCount("<example.SecureConfig128bitsRSA_4096x512_1: void negativeTestCase()>", TypestateError.class, 1);

		setErrorsCount("<example.SecureConfig128bitsRSA_4096x512_2: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.SecureConfig128bitsRSA_4096x512_2: void main(java.lang.String[])>", RequiredPredicateError.class, 0);
		setErrorsCount("<example.SecureConfig128bitsRSA_4096x512_2: void main(java.lang.String[])>", TypestateError.class, 1);
		
		// positive test case
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_1: void positiveTestCase()>", ConstraintError.class, 1);
		// This is correct because there are two different 'SHA-256' variables, i.e. the pred is ensured on the first but not on the second one
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_1: void positiveTestCase()>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_1: void positiveTestCase()>", TypestateError.class, 1);
		
		// negative test case
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_1: void negativeTestCase()>", ConstraintError.class, 2);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_1: void negativeTestCase()>", RequiredPredicateError.class, 7);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_1: void negativeTestCase()>", TypestateError.class, 1);
		
		// positive test case
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x512_1: void positiveTestCase()>", ConstraintError.class, 1);
		// This is correct because there are two different 'SHA-256' variables, i.e. the pred is ensured on the first but not on the second one
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x512_1: void positiveTestCase()>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x512_1: void positiveTestCase()>", TypestateError.class, 1);
				
		// negative test case
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x512_1: void negativeTestCase()>", ConstraintError.class, 2);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x512_1: void negativeTestCase()>", RequiredPredicateError.class, 7);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x512_1: void negativeTestCase()>", TypestateError.class, 1);

		scanner.run();
		assertErrors(scanner.getErrorCollection());
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pdf/avoidInsecureDefaults/
	@Test
	public void avoidInsecureDefaultsExamples() {
		String mavenProjectPath = new File(
				"../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidInsecureDefaults").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.UseQualifiedNameForPBE1: void main(java.lang.String[])>", RequiredPredicateError.class, 4);
		setErrorsCount("<example.UseQualifiedNameForPBE1: void main(java.lang.String[])>", ForbiddenMethodError.class, 1);
		setErrorsCount("<example.UseQualifiedNameForPBE1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.UseQualifiedNameForPBE1: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.UseQualifiedNameForPBE1: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		setErrorsCount("<example.UseExplicitMode1: void main(java.lang.String[])>", ConstraintError.class, 6);
		setErrorsCount("<example.UseExplicitMode1: void main(java.lang.String[])>", IncompleteOperationError.class, 3);
		setErrorsCount("<example.UseExplicitMode1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.UseExplicitMode1: void main(java.lang.String[])>", TypestateError.class, 2);
		setErrorsCount("<example.UseExplicitPadding1: void main(java.lang.String[])>", IncompleteOperationError.class, 3);
		setErrorsCount("<example.UseExplicitPadding1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.UseExplicitPadding1: void main(java.lang.String[])>", TypestateError.class, 2);
		setErrorsCount("<example.UseExplicitPadding1: void main(java.lang.String[])>", ImpreciseValueExtractionError.class, 2);
		
		// positive test case
		setErrorsCount("<example.UseQualifiedNameForRSAOAEP: void positiveTestCase()>", TypestateError.class, 1);
		setErrorsCount("<example.UseQualifiedNameForRSAOAEP: void positiveTestCase()>", RequiredPredicateError.class, 0);
		setErrorsCount("<example.UseQualifiedNameForRSAOAEP: void positiveTestCase()>", ConstraintError.class, 1);

		// negative test case
		setErrorsCount("<example.UseQualifiedNameForRSAOAEP: void negativeTestCase()>", TypestateError.class, 1);
		setErrorsCount("<example.UseQualifiedNameForRSAOAEP: void negativeTestCase()>", RequiredPredicateError.class, 5);
		setErrorsCount("<example.UseQualifiedNameForRSAOAEP: void negativeTestCase()>", ConstraintError.class, 2);
		
		// positive test case
		setErrorsCount("<example.UseQualifiedParamsForRSAOAEP: void positiveTestCase()>", TypestateError.class, 1);
		setErrorsCount("<example.UseQualifiedParamsForRSAOAEP: void positiveTestCase()>", RequiredPredicateError.class, 2);
		setErrorsCount("<example.UseQualifiedParamsForRSAOAEP: void positiveTestCase()>", ConstraintError.class, 2);
		
		// negative test case
		setErrorsCount("<example.UseQualifiedParamsForRSAOAEP: void negativeTestCase()>", TypestateError.class, 1);
		setErrorsCount("<example.UseQualifiedParamsForRSAOAEP: void negativeTestCase()>", RequiredPredicateError.class, 7);
		setErrorsCount("<example.UseQualifiedParamsForRSAOAEP: void negativeTestCase()>", ConstraintError.class, 2);

		scanner.run();
		assertErrors(scanner.getErrorCollection());
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/wc/avoidInsecureHash/
	@Test
	public void avoidInsecureHashExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidInsecureHash")
				.getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.UseSHA2_1: void main(java.lang.String[])>", IncompleteOperationError.class, 0);
		setErrorsCount("<example.UseSHA2_2: void main(java.lang.String[])>", IncompleteOperationError.class, 0);
		setErrorsCount("<example.UseSHA2_2: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.UseSHA3_1: void main(java.lang.String[])>", ConstraintError.class, 4);
		setErrorsCount("<example.UseSHA3_1: void main(java.lang.String[])>", IncompleteOperationError.class, 0);
		setErrorsCount("<example.UseSHA3_2: void main(java.lang.String[])>", IncompleteOperationError.class, 0);

		scanner.run();
		assertErrors(scanner.getErrorCollection());
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/wc/avoidInsecureMAC/
	@Test
	public void avoidInsecureMACExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidInsecureMAC")
				.getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		scanner.run();
		assertErrors(scanner.getErrorCollection());
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkc/enc/avoidInsecurePadding/
	@Test
	public void avoidInsecurePaddingExamples() {
		String mavenProjectPath = new File(
				"../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidInsecurePadding").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		// positive test case
		setErrorsCount("<example.OAEP_2048x256_1: void positiveTestCase()>", ConstraintError.class, 1);
		// This is correct because there are two different 'SHA-256' variables, i.e. the pred is ensured on the first but not on the second one
		setErrorsCount("<example.OAEP_2048x256_1: void positiveTestCase()>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.OAEP_2048x256_1: void positiveTestCase()>", TypestateError.class, 1);

		// negative test case
		setErrorsCount("<example.OAEP_2048x256_1: void negativeTestCase()>", ConstraintError.class, 2);
		setErrorsCount("<example.OAEP_2048x256_1: void negativeTestCase()>", RequiredPredicateError.class, 7);
		setErrorsCount("<example.OAEP_2048x256_1: void negativeTestCase()>", TypestateError.class, 1);
		
		// positive test case
		setErrorsCount("<example.OAEP_2048x256_2: void positiveTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.OAEP_2048x256_2: void positiveTestCase()>", RequiredPredicateError.class, 0);
		setErrorsCount("<example.OAEP_2048x256_2: void positiveTestCase()>", TypestateError.class, 1);

		// negative test case
		setErrorsCount("<example.OAEP_2048x256_2: void negativeTestCase()>", ConstraintError.class, 2);
		setErrorsCount("<example.OAEP_2048x256_2: void negativeTestCase()>", RequiredPredicateError.class, 5);
		setErrorsCount("<example.OAEP_2048x256_2: void negativeTestCase()>", TypestateError.class, 1);
		
		// positive test case
		setErrorsCount("<example.OAEP_2048x384_1: void positiveTestCase()>", ConstraintError.class, 1);
		// This is correct because there are two different 'SHA-256' variables, i.e. the pred is ensured on the first but not on the second one
		setErrorsCount("<example.OAEP_2048x384_1: void positiveTestCase()>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.OAEP_2048x384_1: void positiveTestCase()>", TypestateError.class, 1);

		// negative test case
		setErrorsCount("<example.OAEP_2048x384_1: void negativeTestCase()>", ConstraintError.class, 2);
		setErrorsCount("<example.OAEP_2048x384_1: void negativeTestCase()>", RequiredPredicateError.class, 7);
		setErrorsCount("<example.OAEP_2048x384_1: void negativeTestCase()>", TypestateError.class, 1);

		// positive test case
		setErrorsCount("<example.OAEP_2048x384_2: void positiveTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.OAEP_2048x384_2: void positiveTestCase()>", RequiredPredicateError.class, 0);
		setErrorsCount("<example.OAEP_2048x384_2: void positiveTestCase()>", TypestateError.class, 1);

		// negative test case
		setErrorsCount("<example.OAEP_2048x384_2: void negativeTestCase()>", ConstraintError.class, 2);
		setErrorsCount("<example.OAEP_2048x384_2: void negativeTestCase()>", RequiredPredicateError.class, 5);
		setErrorsCount("<example.OAEP_2048x384_2: void negativeTestCase()>", TypestateError.class, 1);

		// positive test case
		setErrorsCount("<example.OAEP_2048x512_1: void positiveTestCase()>", ConstraintError.class, 1);
		// This is correct because there are two different 'SHA-256' variables, i.e. the pred is ensured on the first but not on the second one
		setErrorsCount("<example.OAEP_2048x512_1: void positiveTestCase()>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.OAEP_2048x512_1: void positiveTestCase()>", TypestateError.class, 1);

		// negative test case
		setErrorsCount("<example.OAEP_2048x512_1: void negativeTestCase()>", ConstraintError.class, 2);
		setErrorsCount("<example.OAEP_2048x512_1: void negativeTestCase()>", RequiredPredicateError.class, 7);
		setErrorsCount("<example.OAEP_2048x512_1: void negativeTestCase()>", TypestateError.class, 1);

		// positive test case
		setErrorsCount("<example.OAEP_2048x512_2: void positiveTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.OAEP_2048x512_2: void positiveTestCase()>", RequiredPredicateError.class, 0);
		setErrorsCount("<example.OAEP_2048x512_2: void positiveTestCase()>", TypestateError.class, 1);

		// negative test case
		setErrorsCount("<example.OAEP_2048x512_2: void negativeTestCase()>", ConstraintError.class, 2);
		setErrorsCount("<example.OAEP_2048x512_2: void negativeTestCase()>", RequiredPredicateError.class, 5);
		setErrorsCount("<example.OAEP_2048x512_2: void negativeTestCase()>", TypestateError.class, 1);

		scanner.run();
		assertErrors(scanner.getErrorCollection());
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkc/sign/avoidInsecurePadding/
	@Test
	public void avoidInsecurePaddingSignExamples() {
		String mavenProjectPath = new File(
				"../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidInsecurePaddingSign").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		// positive test case
		setErrorsCount("<example.PSSwSHA256Signature: void positiveTestCase()>", TypestateError.class, 1);
		setErrorsCount("<example.PSSwSHA256Signature: void positiveTestCase()>", ConstraintError.class, 1);
		
		// negative test case
		setErrorsCount("<example.PSSwSHA256Signature: void negativeTestCase()>", TypestateError.class, 1);
		setErrorsCount("<example.PSSwSHA256Signature: void negativeTestCase()>", ConstraintError.class, 2);
		setErrorsCount("<example.PSSwSHA256Signature: void negativeTestCase()>", RequiredPredicateError.class, 5);

		// positive test case
		setErrorsCount("<example.PSSwSHA384Signature: void positiveTestCase()>", TypestateError.class, 1);
		setErrorsCount("<example.PSSwSHA384Signature: void positiveTestCase()>", ConstraintError.class, 1);
		
		// negative test case
		setErrorsCount("<example.PSSwSHA384Signature: void negativeTestCase()>", TypestateError.class, 1);
		setErrorsCount("<example.PSSwSHA384Signature: void negativeTestCase()>", ConstraintError.class, 2);
		setErrorsCount("<example.PSSwSHA384Signature: void negativeTestCase()>", RequiredPredicateError.class, 5);

		// positive test case
		setErrorsCount("<example.PSSwSHA512Signature: void positiveTestCase()>", TypestateError.class, 1);
		setErrorsCount("<example.PSSwSHA512Signature: void positiveTestCase()>", ConstraintError.class, 1);
		
		// negative test case
		setErrorsCount("<example.PSSwSHA512Signature: void negativeTestCase()>", TypestateError.class, 1);
		setErrorsCount("<example.PSSwSHA512Signature: void negativeTestCase()>", ConstraintError.class, 2);
		setErrorsCount("<example.PSSwSHA512Signature: void negativeTestCase()>", RequiredPredicateError.class, 5);

		scanner.run();
		assertErrors(scanner.getErrorCollection());
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
		setErrorsCount("<example.UseAES_CTR: void main(java.lang.String[])>", RequiredPredicateError.class, 6);

		scanner.run();
		assertErrors(scanner.getErrorCollection());
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkm/avoidKeyReuseInStreams/
	@Test
	public void avoidKeyReuseInStreamsExamples() {
		String mavenProjectPath = new File(
				"../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidKeyReuseInStreams").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.DoNotReuseKeyStreamCipher1: void main(java.lang.String[])>", RequiredPredicateError.class, 4);
		setErrorsCount("<example.DoNotReuseKeyStreamCipher1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.DoNotReuseKeyStreamCipher1: void main(java.lang.String[])>", CallToError.class, 1);

		setErrorsCount("<example.DoNotReuseKeyStreamCipher2: void main(java.lang.String[])>", RequiredPredicateError.class, 4);
		setErrorsCount("<example.DoNotReuseKeyStreamCipher2: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.DoNotReuseKeyStreamCipher2: void main(java.lang.String[])>", CallToError.class, 1);

		setErrorsCount("<example.DoNotReuseKeyStreamCipher3: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.DoNotReuseKeyStreamCipher3: void main(java.lang.String[])>", CallToError.class, 1);

		setErrorsCount("<example.DoNotReuseKeyStreamCipher4: void main(java.lang.String[])>", RequiredPredicateError.class, 4);
		setErrorsCount("<example.DoNotReuseKeyStreamCipher4: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.DoNotReuseKeyStreamCipher4: void main(java.lang.String[])>", CallToError.class, 1);

		setErrorsCount("<example.DoNotReuseKeyStreamCipher5: void main(java.lang.String[])>", RequiredPredicateError.class, 4);
		setErrorsCount("<example.DoNotReuseKeyStreamCipher5: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.DoNotReuseKeyStreamCipher5: void main(java.lang.String[])>", CallToError.class, 1);

		scanner.run();
		assertErrors(scanner.getErrorCollection());
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pdf/avoidSideChannels/
	@Test
	public void avoidSideChannelsExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidSideChannels")
				.getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		scanner.run();
		assertErrors(scanner.getErrorCollection());
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/br/avoidStatisticPRNG/
	@Test
	public void avoidStatisticPRNGExamples() {
		String mavenProjectPath = new File(
				"../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidStatisticPRNG").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		scanner.run();
		assertErrors(scanner.getErrorCollection());
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/icv/completeValidation/
	@Ignore("Boomerang cannot finish query computation ")
	@Test
	public void completeValidationExamples() {
		String mavenProjectPath = new File(
				"../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/completeValidation").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.SSLClientCertPathCRLValidation: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		setErrorsCount("<example.SSLClientCompleteValidation: void main(java.lang.String[])>", IncompleteOperationError.class, 1);

		scanner.run();
		assertErrors(scanner.getErrorCollection());
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkc/ka/DHandECDH/
	@Test
	public void DHandECDHExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/DHandECDH")
				.getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.NonAuthenticatedEphemeralECDH_128: void main(java.lang.String[])>", ConstraintError.class, 0);
		setErrorsCount("<example.NonAuthenticatedEphemeralECDH_128: void main(java.lang.String[])>", RequiredPredicateError.class, 0);
		
		setErrorsCount("<example.NonAuthenticatedEphemeralECDH_192: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<example.NonAuthenticatedEphemeralECDH_192: void main(java.lang.String[])>", RequiredPredicateError.class, 16);
		
		setErrorsCount("<example.NonAuthenticatedEphemeralECDH_256: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<example.NonAuthenticatedEphemeralECDH_256: void main(java.lang.String[])>", RequiredPredicateError.class, 16);

		setErrorsCount("<example.NonAuthenticatedEphemeralDH_2048: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<example.NonAuthenticatedEphemeralDH_2048: void main(java.lang.String[])>", RequiredPredicateError.class, 16);
		
		// positive test case
		setErrorsCount("<example.NonAuthenticatedDH_2048: void positiveTestCase()>", ConstraintError.class, 0);
		setErrorsCount("<example.NonAuthenticatedDH_2048: void positiveTestCase()>", RequiredPredicateError.class, 18);
		setErrorsCount("<example.NonAuthenticatedDH_2048: void positiveTestCase()>", IncompleteOperationError.class, 1);

		// negative test case
		setErrorsCount("<example.NonAuthenticatedDH_2048: void negativeTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.NonAuthenticatedDH_2048: void negativeTestCase()>", RequiredPredicateError.class, 18);
		setErrorsCount("<example.NonAuthenticatedDH_2048: void negativeTestCase()>", IncompleteOperationError.class, 1);
		
		scanner.run();
		assertErrors(scanner.getErrorCollection());
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkc/sign/digSignDSAandECDSA/
	@Test
	public void digSignDSAandECDSAExamples() {
		String mavenProjectPath = new File(
				"../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/digSignDSAandECDSA").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.BC_128bits_DSA3072xSHA256: void main(java.lang.String[])>", RequiredPredicateError.class, 0);
		setErrorsCount("<example.BC_128bits_DSA3072xSHA256: void main(java.lang.String[])>", ConstraintError.class, 0);
		
		setErrorsCount("<example.BC_ECDSAprime192: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<example.BC_ECDSAprime192: void main(java.lang.String[])>", RequiredPredicateError.class, 5);
		setErrorsCount("<example.BC_ECDSAprime239: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<example.BC_ECDSAprime239: void main(java.lang.String[])>", RequiredPredicateError.class, 5);
		setErrorsCount("<example.BC_ECDSAprime256: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<example.BC_ECDSAprime256: void main(java.lang.String[])>", RequiredPredicateError.class, 5);

		// TODO False positives
		setErrorsCount(RequiredPredicateError.class, new FalsePositives(2, "setSeed is correctly called (cf. https://github.com/CROSSINGTUD/CryptoAnalysis/issues/295"), "<example.RandomMessageNonceECDSA: void main(java.lang.String[])>");
		
		// positive test case
		setErrorsCount("<example.SUN_112bits_DSA2048wSHA256: void positiveTestCase()>", ConstraintError.class, 0);

		// negative test case
		setErrorsCount("<example.SUN_112bits_DSA2048wSHA256: void negativeTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.SUN_112bits_DSA2048wSHA256: void negativeTestCase()>", RequiredPredicateError.class, 5);
		
		setErrorsCount("<example.SUN_112bits_ECDSA224wSHA224: void main(java.lang.String[])>", RequiredPredicateError.class, 5);
		setErrorsCount("<example.SUN_112bits_ECDSA224wSHA224: void main(java.lang.String[])>", ConstraintError.class, 3);
		setErrorsCount("<example.SUN_192bits_ECDSA384wSHA384: void main(java.lang.String[])>", RequiredPredicateError.class, 5);
		setErrorsCount("<example.SUN_192bits_ECDSA384wSHA384: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.SUN_256bits_ECDSA571wSHA512: void main(java.lang.String[])>", RequiredPredicateError.class, 5);
		setErrorsCount("<example.SUN_256bits_ECDSA571wSHA512: void main(java.lang.String[])>", ConstraintError.class, 1);

		scanner.run();
		assertErrors(scanner.getErrorCollection());
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkc/sign/digSignRSA/
	@Test
	public void digSignRSAExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/digSignRSA")
				.getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		// positive test case
		setErrorsCount("<example.PKCS1_112bitsSign2048xSHA256_1: void positiveTestCase()>", TypestateError.class, 1);

		// negative test case
		setErrorsCount("<example.PKCS1_112bitsSign2048xSHA256_1: void negativeTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.PKCS1_112bitsSign2048xSHA256_1: void negativeTestCase()>", RequiredPredicateError.class, 5);
		setErrorsCount("<example.PKCS1_112bitsSign2048xSHA256_1: void negativeTestCase()>", TypestateError.class, 1);
		
		// positive test case
		setErrorsCount("<example.PKCS1_112bitsSign2048xSHA256_2: void positiveTestCase()>", TypestateError.class, 1);

		// negative test case
		setErrorsCount("<example.PKCS1_112bitsSign2048xSHA256_2: void negativeTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.PKCS1_112bitsSign2048xSHA256_2: void negativeTestCase()>", RequiredPredicateError.class, 5);
		setErrorsCount("<example.PKCS1_112bitsSign2048xSHA256_2: void negativeTestCase()>", TypestateError.class, 1);
		
		setErrorsCount("<example.PKCS1_128bitsSign3072xSHA256_1: void main(java.lang.String[])>", TypestateError.class, 1);
		
		setErrorsCount("<example.PKCS1_128bitsSign3072xSHA256_2: void main(java.lang.String[])>", TypestateError.class, 1);
		
		setErrorsCount("<example.PKCS1_192bitsSign7680xSHA384_1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.PKCS1_192bitsSign7680xSHA384_1: void main(java.lang.String[])>", RequiredPredicateError.class, 5);
		setErrorsCount("<example.PKCS1_192bitsSign7680xSHA384_1: void main(java.lang.String[])>", ConstraintError.class, 1);
		
		setErrorsCount("<example.PKCS1_192bitsSign7680xSHA384_2: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.PKCS1_192bitsSign7680xSHA384_2: void main(java.lang.String[])>", RequiredPredicateError.class, 5);
		setErrorsCount("<example.PKCS1_192bitsSign7680xSHA384_2: void main(java.lang.String[])>", ConstraintError.class, 1);
		
		// positive test case
		setErrorsCount("<example.PSS_112bitsSign2048xSHA256_1: void positiveTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.PSS_112bitsSign2048xSHA256_1: void positiveTestCase()>", TypestateError.class, 1);

		// negative test case
		setErrorsCount("<example.PSS_112bitsSign2048xSHA256_1: void negativeTestCase()>", ConstraintError.class, 2);
		setErrorsCount("<example.PSS_112bitsSign2048xSHA256_1: void negativeTestCase()>", RequiredPredicateError.class, 5);
		setErrorsCount("<example.PSS_112bitsSign2048xSHA256_1: void negativeTestCase()>", TypestateError.class, 1);
		
		// positive test case
		setErrorsCount("<example.PSS_112bitsSign2048xSHA256_2: void positiveTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.PSS_112bitsSign2048xSHA256_2: void positiveTestCase()>", TypestateError.class, 1);

		// negative test case
		setErrorsCount("<example.PSS_112bitsSign2048xSHA256_2: void negativeTestCase()>", ConstraintError.class, 2);
		setErrorsCount("<example.PSS_112bitsSign2048xSHA256_2: void negativeTestCase()>", RequiredPredicateError.class, 5);
		setErrorsCount("<example.PSS_112bitsSign2048xSHA256_2: void negativeTestCase()>", TypestateError.class, 1);
		
		setErrorsCount("<example.PSS_128bitsSign3072xSHA256_1: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.PSS_128bitsSign3072xSHA256_1: void main(java.lang.String[])>", TypestateError.class, 1);
		
		setErrorsCount("<example.PSS_128bitsSign3072xSHA256_2: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.PSS_128bitsSign3072xSHA256_2: void main(java.lang.String[])>", TypestateError.class, 1);
		
		setErrorsCount("<example.PSS_192bitsSign7680xSHA384_1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.PSS_192bitsSign7680xSHA384_1: void main(java.lang.String[])>", RequiredPredicateError.class, 5);
		setErrorsCount("<example.PSS_192bitsSign7680xSHA384_1: void main(java.lang.String[])>", ConstraintError.class, 2);
		
		setErrorsCount("<example.PSS_192bitsSign7680xSHA384_2: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.PSS_192bitsSign7680xSHA384_2: void main(java.lang.String[])>", RequiredPredicateError.class, 5);
		setErrorsCount("<example.PSS_192bitsSign7680xSHA384_2: void main(java.lang.String[])>", ConstraintError.class, 2);

		scanner.run();
		assertErrors(scanner.getErrorCollection());
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/cib/doNotPrintSecrets/
	@Test
	public void doNotPrintSecretsExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/doNotPrintSecrets")
				.getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.DoNotPrintECDHPrivKey1: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<example.DoNotPrintECDHPrivKey1: void main(java.lang.String[])>", RequiredPredicateError.class, 16);
		
		setErrorsCount("<example.DoNotPrintECDHSecret1: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<example.DoNotPrintECDHSecret1: void main(java.lang.String[])>", RequiredPredicateError.class, 16);
		
		setErrorsCount("<example.DoNotPrintECDSAPrivKey1: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.DoNotPrintECDSAPrivKey1: void main(java.lang.String[])>", RequiredPredicateError.class, 5);
		
		// positive test case
		setErrorsCount("<example.DoNotPrintPrivKey1: void positiveTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.DoNotPrintPrivKey1: void positiveTestCase()>", RequiredPredicateError.class, 0);
		setErrorsCount("<example.DoNotPrintPrivKey1: void positiveTestCase()>", TypestateError.class, 1);

		// negative test case
		setErrorsCount("<example.DoNotPrintPrivKey1: void negativeTestCase()>", ConstraintError.class, 2);
		setErrorsCount("<example.DoNotPrintPrivKey1: void negativeTestCase()>", RequiredPredicateError.class, 5);
		setErrorsCount("<example.DoNotPrintPrivKey1: void negativeTestCase()>", TypestateError.class, 1);
		
		setErrorsCount("<example.DoNotPrintSecKey1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.DoNotPrintSecKey1: void main(java.lang.String[])>", CallToError.class, 1);

		setErrorsCount("<example.DoNotPrintDHSecret1: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<example.DoNotPrintDHSecret1: void main(java.lang.String[])>", RequiredPredicateError.class, 16);

		setErrorsCount("<example.DoNotPrintDHPrivKey1: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<example.DoNotPrintDHPrivKey1: void main(java.lang.String[])>", RequiredPredicateError.class, 16);

		scanner.run();
		assertErrors(scanner.getErrorCollection());
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
		setErrorsCount("<example.EncryptThenMacCiphertextAndIV: void main(java.lang.String[])>", TypestateError.class, 2);
		setErrorsCount("<example.EncryptThenMacCiphertextAndIV: void main(java.lang.String[])>", RequiredPredicateError.class, 3);

		scanner.run();
		assertErrors(scanner.getErrorCollection());
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
		setErrorsCount("<example.UseRandomIVsForCFB: void main(java.lang.String[])>", IncompleteOperationError.class, 3);
		setErrorsCount("<example.UseRandomIVsForCFB: void main(java.lang.String[])>", TypestateError.class, 2);
		setErrorsCount("<example.UseRandomIVsForCFB128: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.UseRandomIVsForCFB128: void main(java.lang.String[])>", IncompleteOperationError.class, 3);
		setErrorsCount("<example.UseRandomIVsForCFB128: void main(java.lang.String[])>", TypestateError.class, 2);
		setErrorsCount("<example.UseRandomIVsForCFB128: void main(java.lang.String[])>", ConstraintError.class, 2);

		scanner.run();
		assertErrors(scanner.getErrorCollection());
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkc/enc/secureConfigsRSA/
	@Test
	public void secureConfigsRSAExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/secureConfigsRSA")
				.getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		// positive test case
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_1: void positiveTestCase()>", ConstraintError.class, 1);
		// This is correct because there are two different 'SHA-256' variables, i.e. the pred is ensured on the first but not on the second one
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_1: void positiveTestCase()>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_1: void positiveTestCase()>", TypestateError.class, 1);
		
		// negative test case
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_1: void negativeTestCase()>", ConstraintError.class, 2);
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_1: void negativeTestCase()>", RequiredPredicateError.class, 7);
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_1: void negativeTestCase()>", TypestateError.class, 1);
		
		// positive test case
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_2: void positiveTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_2: void positiveTestCase()>", RequiredPredicateError.class, 0);
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_2: void positiveTestCase()>", TypestateError.class, 1);
		
		// negative test case
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_2: void negativeTestCase()>", ConstraintError.class, 2);
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_2: void negativeTestCase()>", RequiredPredicateError.class, 5);
		setErrorsCount("<example.SecureConfig112bitsRSA_2048x256_2: void negativeTestCase()>", TypestateError.class, 1);
		
		// positive test case
		setErrorsCount("<example.SecureConfig128bitsRSA_3072x384_1: void positiveTestCase()>", ConstraintError.class, 1);
		// This is correct because there are two different 'SHA-256' variables, i.e. the pred is ensured on the first but not on the second one
		setErrorsCount("<example.SecureConfig128bitsRSA_3072x384_1: void positiveTestCase()>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig128bitsRSA_3072x384_1: void positiveTestCase()>", TypestateError.class, 1);

		// negative test case
		setErrorsCount("<example.SecureConfig128bitsRSA_3072x384_1: void negativeTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.SecureConfig128bitsRSA_3072x384_1: void negativeTestCase()>", RequiredPredicateError.class, 2);
		setErrorsCount("<example.SecureConfig128bitsRSA_3072x384_1: void negativeTestCase()>", TypestateError.class, 1);

		setErrorsCount("<example.SecureConfig128bitsRSA_3072x384_2: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.SecureConfig128bitsRSA_3072x384_2: void main(java.lang.String[])>", RequiredPredicateError.class, 0);
		setErrorsCount("<example.SecureConfig128bitsRSA_3072x384_2: void main(java.lang.String[])>", TypestateError.class, 1);
		
		// positive test case
		setErrorsCount("<example.SecureConfig128bitsRSA_4096x512_1: void positiveTestCase()>", ConstraintError.class, 1);
		// This is correct because there are two different 'SHA-256' variables, i.e. the pred is ensured on the first but not on the second one
		setErrorsCount("<example.SecureConfig128bitsRSA_4096x512_1: void positiveTestCase()>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig128bitsRSA_4096x512_1: void positiveTestCase()>", TypestateError.class, 1);
		
		// negative test case
		setErrorsCount("<example.SecureConfig128bitsRSA_4096x512_1: void negativeTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.SecureConfig128bitsRSA_4096x512_1: void negativeTestCase()>", RequiredPredicateError.class, 2);
		setErrorsCount("<example.SecureConfig128bitsRSA_4096x512_1: void negativeTestCase()>", TypestateError.class, 1);

		setErrorsCount("<example.SecureConfig128bitsRSA_4096x512_2: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.SecureConfig128bitsRSA_4096x512_2: void main(java.lang.String[])>", RequiredPredicateError.class, 0);
		setErrorsCount("<example.SecureConfig128bitsRSA_4096x512_2: void main(java.lang.String[])>", TypestateError.class, 1);
		
		// positive test case
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_1: void positiveTestCase()>", ConstraintError.class, 1);
		// This is correct because there are two different 'SHA-256' variables, i.e. the pred is ensured on the first but not on the second one
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_1: void positiveTestCase()>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_1: void positiveTestCase()>", TypestateError.class, 1);
		
		// negative test case
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_1: void negativeTestCase()>", ConstraintError.class, 2);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_1: void negativeTestCase()>", RequiredPredicateError.class, 7);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_1: void negativeTestCase()>", TypestateError.class, 1);
		
		// positive test case
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_2: void positiveTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_2: void positiveTestCase()>", RequiredPredicateError.class, 0);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_2: void positiveTestCase()>", TypestateError.class, 1);
		
		// negative test case
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_2: void negativeTestCase()>", ConstraintError.class, 2);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_2: void negativeTestCase()>", RequiredPredicateError.class, 5);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x384_2: void negativeTestCase()>", TypestateError.class, 1);
		
		// positive test case
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x512_1: void positiveTestCase()>", ConstraintError.class, 1);
		// This is correct because there are two different 'SHA-256' variables, i.e. the pred is ensured on the first but not on the second one
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x512_1: void positiveTestCase()>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x512_1: void positiveTestCase()>", TypestateError.class, 1);
		
		// negative test case
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x512_1: void negativeTestCase()>", ConstraintError.class, 2);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x512_1: void negativeTestCase()>", RequiredPredicateError.class, 7);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x512_1: void negativeTestCase()>", TypestateError.class, 1);
		
		// positive test case
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x512_2: void positiveTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x512_2: void positiveTestCase()>", RequiredPredicateError.class, 0);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x512_2: void positiveTestCase()>", TypestateError.class, 1);

		// negative test case
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x512_2: void negativeTestCase()>", ConstraintError.class, 2);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x512_2: void negativeTestCase()>", RequiredPredicateError.class, 5);
		setErrorsCount("<example.SecureConfig192bitsRSA_7680x512_2: void negativeTestCase()>", TypestateError.class, 1);

		scanner.run();
		assertErrors(scanner.getErrorCollection());
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkc/ecc/securecurves/
	@Test
	public void securecurvesExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/securecurves")
				.getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<example.SecureCurve_secp192k1: void negativeTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.SecureCurve_secp192k1: void negativeTestCase()>", RequiredPredicateError.class, 2);
		
		setErrorsCount("<example.SecureCurve_secp192r1: void negativeTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.SecureCurve_secp192r1: void negativeTestCase()>", RequiredPredicateError.class, 2);
		
		setErrorsCount("<example.SecureCurve_secp224k1: void negativeTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.SecureCurve_secp224k1: void negativeTestCase()>", RequiredPredicateError.class, 2);
		
		setErrorsCount("<example.SecureCurve_secp224r1: void main(java.lang.String[])>", ConstraintError.class, 0);
		setErrorsCount("<example.SecureCurve_secp224r1: void main(java.lang.String[])>", RequiredPredicateError.class, 0);
		
		setErrorsCount("<example.SecureCurve_secp256k1: void negativeTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.SecureCurve_secp256k1: void negativeTestCase()>", RequiredPredicateError.class, 2);
		
		setErrorsCount("<example.SecureCurve_secp256r1: void main(java.lang.String[])>", ConstraintError.class, 0);
		setErrorsCount("<example.SecureCurve_secp256r1: void main(java.lang.String[])>", RequiredPredicateError.class, 0);

		setErrorsCount("<example.SecureCurve_secp384r1: void main(java.lang.String[])>", ConstraintError.class, 0);
		setErrorsCount("<example.SecureCurve_secp384r1: void main(java.lang.String[])>", RequiredPredicateError.class, 0);

		setErrorsCount("<example.SecureCurve_secp521r1: void main(java.lang.String[])>", ConstraintError.class, 0);
		setErrorsCount("<example.SecureCurve_secp521r1: void main(java.lang.String[])>", RequiredPredicateError.class, 0);

		setErrorsCount("<example.SecureCurve_sect163k1: void negativeTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.SecureCurve_sect163k1: void negativeTestCase()>", RequiredPredicateError.class, 2);

		setErrorsCount("<example.SecureCurve_sect163r1: void negativeTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.SecureCurve_sect163r1: void negativeTestCase()>", RequiredPredicateError.class, 2);

		setErrorsCount("<example.SecureCurve_sect163r2: void negativeTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.SecureCurve_sect163r2: void negativeTestCase()>", RequiredPredicateError.class, 2);

		setErrorsCount("<example.SecureCurve_sect233k1: void negativeTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.SecureCurve_sect233k1: void negativeTestCase()>", RequiredPredicateError.class, 2);

		setErrorsCount("<example.SecureCurve_sect233r1: void negativeTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.SecureCurve_sect233r1: void negativeTestCase()>", RequiredPredicateError.class, 2);

		setErrorsCount("<example.SecureCurve_sect239k1: void negativeTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.SecureCurve_sect239k1: void negativeTestCase()>", RequiredPredicateError.class, 2);

		setErrorsCount("<example.SecureCurve_sect283k1: void negativeTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.SecureCurve_sect283k1: void negativeTestCase()>", RequiredPredicateError.class, 2);

		setErrorsCount("<example.SecureCurve_sect283r1: void negativeTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.SecureCurve_sect283r1: void negativeTestCase()>", RequiredPredicateError.class, 2);

		setErrorsCount("<example.SecureCurve_sect409k1: void negativeTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.SecureCurve_sect409k1: void negativeTestCase()>", RequiredPredicateError.class, 2);

		setErrorsCount("<example.SecureCurve_sect409r1: void negativeTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.SecureCurve_sect409r1: void negativeTestCase()>", RequiredPredicateError.class, 2);

		setErrorsCount("<example.SecureCurve_sect571k1: void negativeTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.SecureCurve_sect571k1: void negativeTestCase()>", RequiredPredicateError.class, 2);

		setErrorsCount("<example.SecureCurve_sect571r1: void negativeTestCase()>", ConstraintError.class, 1);
		setErrorsCount("<example.SecureCurve_sect571r1: void negativeTestCase()>", RequiredPredicateError.class, 2);

		scanner.run();
		assertErrors(scanner.getErrorCollection());
	}

	// This test case corresponds to the following project in BragaCryptoBench:
	// https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pdf/secureStreamCipher/
	@Test
	public void secureStreamCipherExamples() {
		String mavenProjectPath = new File(
				"../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/secureStreamCipher").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.UseMacWithMaleableStream: void main(java.lang.String[])>", RequiredPredicateError.class, 3);
		setErrorsCount("<example.UseMacWithMaleableStream: void main(java.lang.String[])>", TypestateError.class, 2);

		scanner.run();
		assertErrors(scanner.getErrorCollection());
	}

}
