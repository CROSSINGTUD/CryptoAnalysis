package tests.headless;

import crypto.HeadlessCryptoScanner;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.HardCodedError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.NeverTypeOfError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import org.junit.Test;

import java.io.File;

public class ReportedIssueTest extends AbstractHeadlessTest {

	@Test
	public void reportedIssues() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/ReportedIssues").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<issueseeds.Main: void main(java.lang.String[])>", RequiredPredicateError.class, 1);

		setErrorsCount("<issue227.WrappedHasher: byte[] hash()>", TypestateError.class, 0);

		setErrorsCount("<issue208.Issue208WithSingleEntryPoint: void encryptImpl()>", RequiredPredicateError.class, 0);
		setErrorsCount("<issue208.Issue208WithMultipleEntryPoints: void encryptImpl()>", RequiredPredicateError.class, 1);
		
		setErrorsCount("<issue81.Encryption: byte[] encrypt(byte[],javax.crypto.SecretKey)>", ConstraintError.class, 1);
		setErrorsCount("<issue81.Encryption: byte[] encrypt(byte[],javax.crypto.SecretKey)>", RequiredPredicateError.class, 1);
		setErrorsCount("<issue81.Encryption: javax.crypto.SecretKey generateKey(java.lang.String)>", IncompleteOperationError.class, 1);
		setErrorsCount("<issue81.Encryption: javax.crypto.SecretKey generateKey(java.lang.String)>", RequiredPredicateError.class, 4);
		// TODO toCharArray() is not currently not considered when evaluating NeverTypeOfErrors
		setErrorsCount("<issue81.Encryption: javax.crypto.SecretKey generateKey(java.lang.String)>", NeverTypeOfError.class, 0);
		setErrorsCount("<issue81.Encryption: javax.crypto.SecretKey generateKey(java.lang.String)>", ConstraintError.class, 1);
		// TODO toCharArray() is not currently not considered when evaluating HardCodedErrors
		setErrorsCount("<issue81.Encryption: javax.crypto.SecretKey generateKey(java.lang.String)>", HardCodedError.class, 0);
		setErrorsCount("<issue81.Main: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		// TODO toCharArray() is not currently not considered when evaluating NeverTypeOfErrors
		setErrorsCount("<issue81.Main: void main(java.lang.String[])>", NeverTypeOfError.class, 0);
		// TODO toCharArray() is not currently not considered when evaluating HardCodedErrors
		setErrorsCount("<issue81.Main: void main(java.lang.String[])>", HardCodedError.class, 0);

		setErrorsCount("<issuecognicrypt210.CogniCryptSecretKeySpec: void main(java.lang.String[])>", ConstraintError.class, 0);
		setErrorsCount("<issuecognicrypt210.CogniCryptSecretKeySpec: void main(java.lang.String[])>", RequiredPredicateError.class, 3);
		setErrorsCount("<issuecognicrypt210.CogniCryptSecretKeySpec: void main(java.lang.String[])>", HardCodedError.class, 1);

		setErrorsCount("<issue70.ClientProtocolDecoder: byte[] decryptAES(byte[])>", ConstraintError.class, 1);
		setErrorsCount("<issue70.ClientProtocolDecoder: byte[] decryptAES(byte[])>", RequiredPredicateError.class, 3);

		setErrorsCount("<issue69.Issue69: void encryptByPublicKey(java.lang.String)>", IncompleteOperationError.class, 1);
		setErrorsCount("<issue69.Issue69: void encryptByPublicKey(java.lang.String)>", RequiredPredicateError.class, 4);

		// TODO toCharArray() is not currently not considered when evaluating NeverTypeOfErrors
		setErrorsCount("<issue68.AESCryptor: byte[] getKey(java.lang.String)>", NeverTypeOfError.class, 0);
		setErrorsCount("<issue68.AESCryptor: byte[] getKey(java.lang.String)>", RequiredPredicateError.class, 3);
		setErrorsCount("<issue68.AESCryptor: byte[] getKey(java.lang.String)>", IncompleteOperationError.class, 1);
		// TODO toCharArray() is not currently not considered when evaluating HardCodedErrors
		setErrorsCount("<issue68.AESCryptor: byte[] getKey(java.lang.String)>", HardCodedError.class, 0);
		setErrorsCount("<issue68.AESCryptor: javax.crypto.SecretKeyFactory getFactory()>", ConstraintError.class, 1);
		setErrorsCount("<issue68.AESCryptor: byte[] encryptImpl(byte[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<issue68.AESCryptor: void <init>(byte[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<issue68.AESCryptor: void <init>(byte[])>", ConstraintError.class, 2);
		setErrorsCount("<issue68.AESCryptor: byte[] decryptImpl(byte[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<issue68.AESCryptor: byte[] decryptImpl(byte[])>", TypestateError.class, 0);
		setErrorsCount("<issue68.simplified.field.AESCryptor: byte[] encryptImpl(byte[])>", RequiredPredicateError.class, 0);

		setErrorsCount("<issue49.Main: java.security.PrivateKey getPrivateKey()>", ConstraintError.class, 1);
		setErrorsCount("<issue49.Main: java.security.PrivateKey getPrivateKey()>", RequiredPredicateError.class, 2);
		setErrorsCount("<issue49.Main: byte[] sign(java.lang.String)>", RequiredPredicateError.class, 1);

		setErrorsCount("<issue103.Main: void main(java.lang.String[])>", RequiredPredicateError.class, 4);

		setErrorsCount("<issue137.Program: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<issue137.Program: void main(java.lang.String[])>", IncompleteOperationError.class, 1);

		scanner.run();
		assertErrors(scanner.getErrorCollection());
	}

	@Test
	public void issue271Test() {
		// Related to https://github.com/CROSSINGTUD/CryptoAnalysis/issues/271
		String mavenProjectPath = new File("../CryptoAnalysisTargets/KotlinExamples/Issue271").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.Issue271Java: void testFail(java.lang.String)>", IncompleteOperationError.class, 0);
		setErrorsCount("<example.Issue271Java: void testOk(java.lang.String)>", IncompleteOperationError.class, 0);

		setErrorsCount("<example.Issue271Kotlin: void testFail(java.lang.String)>", IncompleteOperationError.class, 0);
		setErrorsCount("<example.Issue271Kotlin: void testOk(java.lang.String)>", IncompleteOperationError.class, 0);

		scanner.run();
		assertErrors(scanner.getErrorCollection());
	}
}
