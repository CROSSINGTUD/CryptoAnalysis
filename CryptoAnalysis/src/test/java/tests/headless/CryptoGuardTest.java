package tests.headless;

import java.io.File;

import org.junit.Test;

import crypto.HeadlessCryptoScanner;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;


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
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/brokencrypto/BrokenCryptoABICase2.java
		setErrorsCount("<example.brokencrypto.BrokenCryptoBBCase3: void go()>", ConstraintError.class, 2);
		setErrorsCount("<example.brokencrypto.BrokenCryptoBBCase3: void go()>", IncompleteOperationError.class, 1);
		
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
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/brokenhash/BrokenHashBBCase2.java
		setErrorsCount("<example.brokenhash.BrokenHashBBCase2: void main(java.lang.String[])>", ConstraintError.class, 1);
				
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
				
		scanner.exec();
		assertErrors();
	}
	
	@Test
	public void predictableCryptographicKeyExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/CryptoGuardExamples/predictablecryptographickey").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/pbeiteration/LessThan1000IterationPBEABICase1.java
		setErrorsCount("<example.predictablecryptographickey.PredictableCryptographicKeyABHCase1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/pbeiteration/LessThan1000IterationPBEBBCase1.java
		setErrorsCount("<example.predictablecryptographickey.PredictableCryptographicKeyABICase1: void go(java.lang.String)>", RequiredPredicateError.class, 1);
		
		// This test case corresponds to the following project in CryptoGuard:
		// https://github.com/CryptoGuardOSS/cryptoapi-bench/blob/master/src/main/java/org/cryptoapi/bench/pbeiteration/LessThan1000IterationPBEBBCase1.java
		setErrorsCount("<example.predictablecryptographickey.PredictableCryptographicKeyBBCase1: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
				
		scanner.exec();
		assertErrors();
	}
	
	
}
