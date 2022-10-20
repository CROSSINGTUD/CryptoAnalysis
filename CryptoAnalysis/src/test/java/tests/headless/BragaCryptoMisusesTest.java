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
import tests.headless.FindingsType.FalsePositives;

/**
 * @author Enri Ozuni
 */
public class BragaCryptoMisusesTest extends AbstractHeadlessTest {
	/**
	 * The following headless tests are deducted from the Braga et al. paper which benchmarks
	 * several static analyzer tools against several hundred test projects containing various
	 * Cryptographic providers of the JCA framework. For the creation of these headless tests,
	 * various projects from Braga paper were considered and used for testing the provider
	 * detection functionality. The test projects of the paper can be found in the link below:
	 *
	 * https://bitbucket.org/alexmbraga/cryptomisuses/src/master/
	 */

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
		setErrorsCount("<cib.buggyIVgen.BuggyIVGen1: void main(java.lang.String[])>", RequiredPredicateError.class, 7);
		setErrorsCount("<cib.buggyIVgen.BuggyIVGen1: void main(java.lang.String[])>", IncompleteOperationError.class, 4);
		setErrorsCount("<cib.buggyIVgen.BuggyIVGen2: void main(java.lang.String[])>", TypestateError.class, 2);
		setErrorsCount("<cib.buggyIVgen.BuggyIVGen2: void main(java.lang.String[])>", RequiredPredicateError.class, 7);
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
		setErrorsCount("<ivm.constantIV.FixedIV1: void main(java.lang.String[])>", RequiredPredicateError.class, 7);
		setErrorsCount("<ivm.constantIV.FixedIV2: void main(java.lang.String[])>", IncompleteOperationError.class, 4);
		setErrorsCount("<ivm.constantIV.FixedIV2: void main(java.lang.String[])>", TypestateError.class, 2);
		setErrorsCount("<ivm.constantIV.FixedIV2: void main(java.lang.String[])>", RequiredPredicateError.class, 7);
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
		setErrorsCount("<pkc.enc.deterministicCrypto.DeterministicEncryptionRSA: void main(java.lang.String[])>", TypestateError.class, 0);
		setErrorsCount("<pkc.enc.deterministicCrypto.DeterministicEncryptionRSA1: void main(java.lang.String[])>", ConstraintError.class, 3);
		setErrorsCount("<pkc.enc.deterministicCrypto.DeterministicEncryptionRSA1: void main(java.lang.String[])>", IncompleteOperationError.class, 4);
		setErrorsCount("<pkc.enc.deterministicCrypto.DeterministicEncryptionRSA1: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<pkc.enc.deterministicCrypto.DeterministicEncryptionRSA1: void main(java.lang.String[])>", TypestateError.class, 0);
		setErrorsCount("<pkc.enc.deterministicCrypto.DeterministicEncryptionRSA2: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<pkc.enc.deterministicCrypto.DeterministicEncryptionRSA2: void main(java.lang.String[])>", IncompleteOperationError.class, 4);
		setErrorsCount("<pkc.enc.deterministicCrypto.DeterministicEncryptionRSA2: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<pkc.enc.deterministicCrypto.DeterministicEncryptionRSA2: void main(java.lang.String[])>", TypestateError.class, 0);
		setErrorsCount("<pkc.enc.deterministicCrypto.DeterministicEncryptionRSA3: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<pkc.enc.deterministicCrypto.DeterministicEncryptionRSA3: void main(java.lang.String[])>", IncompleteOperationError.class, 4);
		setErrorsCount("<pkc.enc.deterministicCrypto.DeterministicEncryptionRSA3: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<pkc.enc.deterministicCrypto.DeterministicEncryptionRSA3: void main(java.lang.String[])>", TypestateError.class, 0);

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
		setErrorsCount("<wc.deterministicSymEnc.DeterministicEncryptionAESwECB1: void main(java.lang.String[])>", TypestateError.class, 0);
		setErrorsCount("<wc.deterministicSymEnc.DeterministicEncryptionAESwECB2: void main(java.lang.String[])>", ForbiddenMethodError.class, 1);
		setErrorsCount("<wc.deterministicSymEnc.DeterministicEncryptionAESwECB2: void main(java.lang.String[])>", IncompleteOperationError.class, 4);
		setErrorsCount("<wc.deterministicSymEnc.DeterministicEncryptionAESwECB2: void main(java.lang.String[])>", TypestateError.class, 0);

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
		setErrorsCount("<br.fixedSeed.FixedSeed1: void main(java.lang.String[])>", RequiredPredicateError.class, 7);
		setErrorsCount("<br.fixedSeed.FixedSeed1: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<br.fixedSeed.FixedSeed1: void main(java.lang.String[])>", IncompleteOperationError.class, 6);
		setErrorsCount("<br.fixedSeed.FixedSeed2: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<br.fixedSeed.FixedSeed3: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<br.fixedSeed.FixedSeed4: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
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
		setErrorsCount("<pdf.insecureDefault.InsecureDefaultRSA: void main(java.lang.String[])>", TypestateError.class, 0);

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

		setErrorsCount("<pkc.enc.insecurePadding.InsecurePaddingRSA1: void main(java.lang.String[])>", TypestateError.class, 0);
		setErrorsCount("<pkc.enc.insecurePadding.InsecurePaddingRSA1: void main(java.lang.String[])>", IncompleteOperationError.class, 4);
		setErrorsCount("<pkc.enc.insecurePadding.InsecurePaddingRSA1: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<pkc.enc.insecurePadding.InsecurePaddingRSA1: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<pkc.enc.insecurePadding.InsecurePaddingRSA2: void main(java.lang.String[])>", TypestateError.class, 0);
		setErrorsCount("<pkc.enc.insecurePadding.InsecurePaddingRSA2: void main(java.lang.String[])>", IncompleteOperationError.class, 4);
		setErrorsCount("<pkc.enc.insecurePadding.InsecurePaddingRSA2: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<pkc.enc.insecurePadding.InsecurePaddingRSA3: void main(java.lang.String[])>", TypestateError.class, 0);
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
		setErrorsCount("<pkc.ka.issuesDHandECDH.NonAuthenticatedDH_512: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<pkc.ka.issuesDHandECDH.NonAuthenticatedDH_1024: void main(java.lang.String[])>", RequiredPredicateError.class, 6);
		setErrorsCount(IncompleteOperationError.class, new FalsePositives(1, ""), "<pkc.ka.issuesDHandECDH.NonAuthenticatedDH_1024: void main(java.lang.String[])>");

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
		setErrorsCount("<cib.printPrivSecKey.PrintECDHPrivKey1: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<cib.printPrivSecKey.PrintECDHSecret1: void main(java.lang.String[])>", ConstraintError.class, 4);
		setErrorsCount("<cib.printPrivSecKey.PrintECDHSecret1: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<cib.printPrivSecKey.PrintECDSAPrivKey1: void main(java.lang.String[])>", ConstraintError.class, 3);
		setErrorsCount("<cib.printPrivSecKey.PrintECDSAPrivKey1: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<cib.printPrivSecKey.PrintPrivKey1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<cib.printPrivSecKey.PrintPrivKey1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<cib.printPrivSecKey.PrintSecKey1: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<cib.printPrivSecKey.PrintDHSecret1: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<cib.printPrivSecKey.PrintDHPrivKey1: void main(java.lang.String[])>", RequiredPredicateError.class, 2);


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
	@Ignore
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

}
