package tests.headless;

import java.io.File;

import org.junit.Test;

import crypto.HeadlessCryptoScanner;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.NeverTypeOfError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import tests.headless.FindingsType.FalseNegatives;
import tests.headless.FindingsType.FalsePositives;
import tests.headless.FindingsType.TruePositives;


/**
 * @author Enri Ozuni
 */
public class CryptoGuardTest extends AbstractHeadlessTest {
	
	/**
	 * The following Headless tests are deducted from the CryptoGuard benchmarking tool 
	 * that detects various Cryptographic API misuses. For the creating these Headless 
	 * tests, various projects from CryptoGuard were considered and can be found in the
	 * following link: https://github.com/CryptoGuardOSS/cryptoapi-bench
	 */
	@Test
	public void brokenCryptoExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/CryptoGuardExamples/brokencrypto").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/brokencrypto/BrokenCryptoABICase1.java
		setErrorsCount("<example.brokencrypto.BrokenCryptoABICase1: void doCrypto(java.lang.String)>", ConstraintError.class, 1);
		setErrorsCount("<example.brokencrypto.BrokenCryptoABICase1: void doCrypto(java.lang.String)>", IncompleteOperationError.class, 1);
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/brokencrypto/BrokenCryptoABICase2.java
		setErrorsCount("<example.brokencrypto.BrokenCryptoABICase2: void doCrypto(java.lang.String)>", ConstraintError.class, 2);
		setErrorsCount("<example.brokencrypto.BrokenCryptoABICase2: void doCrypto(java.lang.String)>", IncompleteOperationError.class, 1);
		// ABICase3, ABICase4, ABICase9 not included as tests due to being similar to ABICase1 and ABICase2 above
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/brokencrypto/BrokenCryptoABICase5.java
		setErrorsCount("<example.brokencrypto.BrokenCryptoABICase5: void doCrypto()>", IncompleteOperationError.class, 1);
		setErrorsCount(ConstraintError.class, new TruePositives(1), new FalseNegatives(1, "ConstraintError not caught! //Related to https://github.com/CROSSINGTUD/CryptoAnalysis/issues/163"), "<example.brokencrypto.BrokenCryptoABICase5: void doCrypto()>");
		// ABICase6, -7, -8, -10 not included as tests due to being similar to ABICase5 above
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/brokencrypto/BrokenCryptoBBCase3.java
		setErrorsCount("<example.brokencrypto.BrokenCryptoBBCase3: void go()>", ConstraintError.class, 2);
		setErrorsCount("<example.brokencrypto.BrokenCryptoBBCase3: void go()>", IncompleteOperationError.class, 1);
		// BBCase1, BBCase2, BBCase4, BBCase5 not included as tests due to being similar to BBCase3 above
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/brokencrypto/BrokenCryptoABSCase1.java
		setErrorsCount(ConstraintError.class, new FalseNegatives(1, "ConstraintError not caught! //Related to https://github.com/CROSSINGTUD/CryptoAnalysis/issues/164"), "<example.brokencrypto.BrokenCryptoABSCase1: byte[] encrypt(java.lang.String, java.lang.String)>");
		// ABSCase2, -3, -4, -5 not included as tests due to being similar to ABSCase1 above
		
		// Test cases regarding ecbcrypto project were not added, due to being similar to
		// all the test cases above:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/tree/master/src/main/java/org/cryptoapi/bench/ecbcrypto
		
		scanner.exec();
		assertErrors();
	}
	
	@Test
	public void brokenHashExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/CryptoGuardExamples/brokenhash").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/brokenhash/BrokenHashABICase1.java
		setErrorsCount("<example.brokenhash.BrokenHashABICase1: void go(java.lang.String,java.lang.String)>", ConstraintError.class, 1);
		// ABICase2, -3, -4 not included as tests due to being similar to ABICase1 above
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/brokenhash/BrokenHashABICase5.java
		setErrorsCount(ConstraintError.class, new FalseNegatives(1, "ConstraintError not caught! //Related to https://github.com/CROSSINGTUD/CryptoAnalysis/issues/163"), "<example.brokenhash.BrokenHashABICase5: void go(java.lang.String)>");
		// ABICase6, -7, -8 not included as tests due to being similar to ABICase5 above
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/brokenhash/BrokenHashBBCase2.java
		setErrorsCount("<example.brokenhash.BrokenHashBBCase2: void main(java.lang.String[])>", ConstraintError.class, 1);
		// BBCase1, -3, -4 not included due to being similar to BBCase2 above
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/brokenhash/BrokenHashABSCase1.java
		setErrorsCount(ConstraintError.class, new FalseNegatives(1, "ConstraintError not caught! //Related to https://github.com/CROSSINGTUD/CryptoAnalysis/issues/164"), "<example.brokenhash.BrokenHashABSCase1: void encrypt(java.lang.String, java.lang.String)>");		
		// All other ABS cases not included due to being similar to the ABSCase1 above
		
		scanner.exec();
		assertErrors();
	}
	
	@Test
	public void insecureAsymmetricCryptoExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/CryptoGuardExamples/insecureasymmetriccrypto").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/insecureasymmetriccrypto/InsecureAsymmetricCipherABICase1.java
		setErrorsCount("<example.insecureasymmetriccrypto.InsecureAsymmetricCipherABICase1: void go(java.security.KeyPairGenerator,java.security.KeyPair)>", IncompleteOperationError.class, 2);
		setErrorsCount("<example.insecureasymmetriccrypto.InsecureAsymmetricCipherABICase1: void main(java.lang.String[])>", TypestateError.class, 1);
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/insecureasymmetriccrypto/InsecureAsymmetricCipherABICase2.java
		setErrorsCount("<example.insecureasymmetriccrypto.InsecureAsymmetricCipherABICase2: void go(java.security.KeyPairGenerator,java.security.KeyPair)>", IncompleteOperationError.class, 2);
		setErrorsCount(ConstraintError.class, new TruePositives(1), new FalseNegatives(1, "ConstraintError not properly caught! //Related to https://github.com/CROSSINGTUD/CryptoAnalysis/issues/163"), "<example.insecureasymmetriccrypto.InsecureAsymmetricCipherABICase2: void main(java.lang.String[])>");
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/insecureasymmetriccrypto/InsecureAsymmetricCipherBB Case1.java
		setErrorsCount("<example.insecureasymmetriccrypto.InsecureAsymmetricCipherBBCase1: void go()>", IncompleteOperationError.class, 2);
		setErrorsCount("<example.insecureasymmetriccrypto.InsecureAsymmetricCipherBBCase1: void go()>", ConstraintError.class, 1);
				
		scanner.exec();
		assertErrors();
	}
	
	
	@Test
	public void pbeIterationExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/CryptoGuardExamples/pbeiteration").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/pbeiteration/LessThan1000IterationPBEABICase1.java
		setErrorsCount("<example.pbeiteration.LessThan1000IterationPBEABICase1: void key2(int)>", ConstraintError.class, 1);
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/pbeiteration/LessThan1000IterationPBEBBCase1.java
		setErrorsCount("<example.pbeiteration.LessThan1000IterationPBEBBCase1: void key2()>", ConstraintError.class, 1);
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/pbeiteration/LessThan1000IterationPBEABHCase1.java
		setErrorsCount(ConstraintError.class, new FalseNegatives(1, "ConstraintError not caught! //Related to https://github.com/CROSSINGTUD/CryptoAnalysis/issues/165"), "<example.pbeiteration.LessThan1000IterationPBEABHCase1: void key2()>");		
				
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/pbeiteration/LessThan1000IterationPBEABICase2.java
		setErrorsCount(ConstraintError.class, new TruePositives(1), new FalseNegatives(1, "ConstraintError not properly caught! //Related to https://github.com/CROSSINGTUD/CryptoAnalysis/issues/163"), "<example.pbeiteration.LessThan1000IterationPBEABICase2: void key2()>");		
				
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/pbeiteration/LessThan1000IterationPBEABSCase1.java
		setErrorsCount(ConstraintError.class, new FalseNegatives(1, "ConstraintError not caught! //Related to https://github.com/CROSSINGTUD/CryptoAnalysis/issues/164"), "<example.pbeiteration.LessThan1000IterationPBEABSCase1: encrypt(int)>");		
				
		scanner.exec();
		assertErrors();
	}
	
	@Test
	public void predictableCryptographicKeyExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/CryptoGuardExamples/predictablecryptographickey").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/predictablecryptographickey/PredictableCryptographicKeyABHCase1.java
		setErrorsCount("<example.predictablecryptographickey.PredictableCryptographicKeyABHCase1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/predictablecryptographickey/PredictableCryptographicKeyABICase1.java
		setErrorsCount("<example.predictablecryptographickey.PredictableCryptographicKeyABICase1: void go(java.lang.String)>", RequiredPredicateError.class, 1);
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/predictablecryptographickey/PredictableCryptographicKeyBBCase1.java
		setErrorsCount("<example.predictablecryptographickey.PredictableCryptographicKeyBBCase1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
			
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/predictablecryptographickey/PredictableCryptographicKeyABHCase2.java
		setErrorsCount(RequiredPredicateError.class, new TruePositives(1), new FalseNegatives(1, "RequiredPredicateError not properly caught! //Related to https://github.com/CROSSINGTUD/CryptoAnalysis/issues/165"), "<example.predictablecryptographickey.PredictableCryptographicKeyABHCase2: void main(java.lang.String[])>");		
				
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/predictablecryptographickey/PredictableCryptographicKeyABICase2.java
		setErrorsCount(RequiredPredicateError.class, new TruePositives(1), new FalseNegatives(1, "RequiredPredicateError not properly caught! //Related to https://github.com/CROSSINGTUD/CryptoAnalysis/issues/163"), "<example.predictablecryptographickey.PredictableCryptographicKeyABICase2: void go()>");		
				
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/predictablecryptographickey/PredictableCryptographicKeyABSCase1.java
		setErrorsCount(RequiredPredicateError.class, new TruePositives(1), new FalseNegatives(1, "RequiredPredicateError not properly caught! //Related to https://github.com/CROSSINGTUD/CryptoAnalysis/issues/164"), "<example.predictablecryptographickey.Crypto: byte[] encrypt(java.lang.String,java.lang.String)>");		
		setErrorsCount("<example.predictablecryptographickey.PredictableCryptographicKeyABSCase1: void <init>()>", IncompleteOperationError.class, 1);
	
		scanner.exec();
		assertErrors();
	}
	
	@Test
	public void predictableKeyStorePasswordExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/CryptoGuardExamples/predictablekeystorepassword").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/predictablekeystorepassword/PredictableKeyStorePasswordABICase1.java
		setErrorsCount("<example.predictablekeystorepassword.PredictableKeyStorePasswordABICase1: void go(java.lang.String)>", NeverTypeOfError.class, 1);
			
		// ABH1, ABI2, ABS1, BB1 are other similar test cases that were not included
		// All test cases in this project produce FP regarding NeverTypeOfError 
		// misuse, as is explained in the link below:
		// https://github.com/CROSSINGTUD/CryptoAnalysis/issues/166
		
		scanner.exec();
		assertErrors();
	}
	
	@Test
	public void predictablePBEPasswordExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/CryptoGuardExamples/predictablepbepassword").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/predictablepbepassword/PredictablePBEPasswordBBCase2.java
		setErrorsCount("<example.predictablepbepassword.PredictablePBEPasswordBBCase2: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/predictablepbepassword/PredictablePBEPasswordABHCase2.java
		setErrorsCount(NeverTypeOfError.class, new FalsePositives(1, "NeverTypeOfError is a FP! //Related to https://github.com/CROSSINGTUD/CryptoAnalysis/issues/165"), "<example.predictablepbepassword.PredictablePBEPasswordABHCase2: void key(java.lang.String)>");		
		setErrorsCount("<example.predictablepbepassword.PredictablePBEPasswordABHCase2: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/predictablepbepassword/PredictablePBEPasswordABSCase1.java
		setErrorsCount("<example.predictablepbepassword.PredictablePBEPasswordABSCase1: void <init>()>", IncompleteOperationError.class, 1);
		setErrorsCount(NeverTypeOfError.class, new FalsePositives(1, "NeverTypeOfError is a FP! //Related to https://github.com/CROSSINGTUD/CryptoAnalysis/issues/164"), "<example.predictablepbepassword.CryptoPredictablePBE: void encrypt(java.lang.String)>");		

		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/predictablepbepassword/PredictablePBEPasswordABICase1.java
		setErrorsCount(NeverTypeOfError.class, new FalsePositives(1, "NeverTypeOfError is a FP! //Related to https://github.com/CROSSINGTUD/CryptoAnalysis/issues/166"), "<example.predictablepbepassword.PredictablePBEPasswordABICase1: void key(java.lang.String)>");		
		setErrorsCount("<example.predictablepbepassword.PredictablePBEPasswordABICase1: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		// ABHCase1, BBCase1 are similar to the case above
		
		scanner.exec();
		assertErrors();
	}
	
	@Test
	public void staticInitializationVectorExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/CryptoGuardExamples/staticinitializationvector").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/staticinitializationvector/StaticInitializationVectorABHCase1.java
		setErrorsCount("<example.staticinitializationvector.StaticInitializationVectorABHCase1: void go()>", IncompleteOperationError.class, 1);
		setErrorsCount("<example.staticinitializationvector.StaticInitializationVectorABHCase1: void go()>", RequiredPredicateError.class, 1);
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/staticinitializationvector/StaticInitializationVectorABHCase2.java
		setErrorsCount("<example.staticinitializationvector.StaticInitializationVectorABHCase2: void go()>", IncompleteOperationError.class, 1);
		setErrorsCount("<example.staticinitializationvector.StaticInitializationVectorABHCase2: void go()>", RequiredPredicateError.class, 1);
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/staticinitializationvector/StaticInitializationVectorABICase1.java
		setErrorsCount("<example.staticinitializationvector.StaticInitializationVectorABICase1: void go(javax.crypto.spec.IvParameterSpec)>", IncompleteOperationError.class, 1);
		setErrorsCount("<example.staticinitializationvector.StaticInitializationVectorABICase1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/staticinitializationvector/StaticInitializationVectorABICase2.java
		setErrorsCount("<example.staticinitializationvector.StaticInitializationVectorABICase2: void go(javax.crypto.spec.IvParameterSpec)>", IncompleteOperationError.class, 1);
		setErrorsCount("<example.staticinitializationvector.StaticInitializationVectorABICase2: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/staticinitializationvector/StaticInitializationVectorBBCase1.java
		setErrorsCount("<example.staticinitializationvector.StaticInitializationVectorBBCase1: void go()>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.staticinitializationvector.StaticInitializationVectorBBCase1: void go()>", IncompleteOperationError.class, 1);
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/staticinitializationvector/StaticInitializationVectorABSCase1.java
		setErrorsCount("<example.staticinitializationvector.CryptoStaticIV1: void encrypt(javax.crypto.spec.IvParameterSpec)>", IncompleteOperationError.class, 1);
		setErrorsCount("<example.staticinitializationvector.StaticInitializationVectorABSCase1: void <init>()>", RequiredPredicateError.class, 1);
		
		scanner.exec();
		assertErrors();
	}
	
	@Test
	public void staticSaltsExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/CryptoGuardExamples/staticsalts").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/staticsalts/StaticSaltsABHCase1.java
		setErrorsCount("<example.staticsalts.StaticSaltsABHCase1: void key2()>", ConstraintError.class, 1);
		setErrorsCount("<example.staticsalts.StaticSaltsABHCase1: void key2()>", RequiredPredicateError.class, 1);
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/staticsalts/StaticSaltsABICase2.java
		setErrorsCount("<example.staticsalts.StaticSaltsABICase2: void key2(int)>", ConstraintError.class, 1);
		setErrorsCount("<example.staticsalts.StaticSaltsABICase2: void key2(int)>", RequiredPredicateError.class, 1);
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/staticsalts/StaticSaltsBBCase1.java
		setErrorsCount("<example.staticsalts.StaticSaltsBBCase1: void key2()>", ConstraintError.class, 1);
		setErrorsCount("<example.staticsalts.StaticSaltsBBCase1: void key2()>", RequiredPredicateError.class, 1);
		// ABICase1 is similar to the examples above	
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/staticsalts/StaticSaltsABSCase1.java
		setErrorsCount("<example.staticsalts.CryptoStaticSalt1: void encrypt(byte[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.staticsalts.CryptoStaticSalt1: void encrypt(byte[])>", ConstraintError.class, 1);
				
		scanner.exec();
		assertErrors();
	}
}
