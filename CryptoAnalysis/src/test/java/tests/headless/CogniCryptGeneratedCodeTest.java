package tests.headless;

import crypto.HeadlessCryptoScanner;
import crypto.analysis.errors.CallToError;
import crypto.analysis.errors.HardCodedError;
import crypto.analysis.errors.RequiredPredicateError;
import org.junit.Test;

import java.io.File;

public class CogniCryptGeneratedCodeTest extends AbstractHeadlessTest {

	@Test
	public void fileEncryptor() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/FileEncryptor").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<Crypto.Enc: byte[] encrypt(byte[],javax.crypto.SecretKey)>", CallToError.class, 1);

		//All the following errors are false positives
		setErrorsCount("<Crypto.KeyDeriv: javax.crypto.SecretKey getKey(char[])>", RequiredPredicateError.class, 3);
		setErrorsCount("<Crypto.KeyDeriv: javax.crypto.SecretKey getKey(char[])>", HardCodedError.class, 1);
		setErrorsCount("<Crypto.Enc: byte[] encrypt(byte[],javax.crypto.SecretKey)>", RequiredPredicateError.class, 1);
		setErrorsCount("<Crypto.Enc: byte[] decrypt(byte[],javax.crypto.SecretKey)>", RequiredPredicateError.class, 2);
		
		scanner.run();
		assertErrors(scanner.getCollectedErrors());
	}

	
	@Test
	public void userAuthenticator() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/UserAuthenticator").getAbsolutePath();
	  	MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<Crypto.PWHasher: java.lang.Boolean verifyPWHash(char[],java.lang.String)>", RequiredPredicateError.class, 3);
		setErrorsCount("<Crypto.PWHasher: java.lang.Boolean verifyPWHash(char[],java.lang.String)>", HardCodedError.class, 0);
		setErrorsCount("<Crypto.PWHasher: java.lang.String createPWHash(char[])>", RequiredPredicateError.class, 0);
		setErrorsCount("<Crypto.PWHasher: java.lang.String createPWHash(char[])>", HardCodedError.class, 0);

		scanner.run();
		assertErrors(scanner.getCollectedErrors());
	}

}
