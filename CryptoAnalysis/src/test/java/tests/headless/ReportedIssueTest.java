package tests.headless;

import java.io.File;
import org.junit.Test;
import crypto.HeadlessCryptoScanner;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.HardCodedError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.NeverTypeOfError;
import crypto.analysis.errors.RequiredPredicateError;

public class ReportedIssueTest extends AbstractHeadlessTest {

	@Test
	public void reportedIssues() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/ReportedIssues").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<issueseeds.Main: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		
		setErrorsCount("<issue81.Encryption: byte[] encrypt(byte[],javax.crypto.SecretKey)>", ConstraintError.class, 1);

		setErrorsCount("<issue81.Encryption: byte[] encrypt(byte[],javax.crypto.SecretKey)>", RequiredPredicateError.class, 1);

		setErrorsCount("<issue81.Encryption: javax.crypto.SecretKey generateKey(java.lang.String)>", IncompleteOperationError.class, 1);
		setErrorsCount("<issue81.Encryption: javax.crypto.SecretKey generateKey(java.lang.String)>", RequiredPredicateError.class, 3);
		setErrorsCount("<issue81.Encryption: javax.crypto.SecretKey generateKey(java.lang.String)>", NeverTypeOfError.class, 1);
		setErrorsCount("<issue81.Encryption: javax.crypto.SecretKey generateKey(java.lang.String)>", ConstraintError.class, 1);
		setErrorsCount("<issue81.Encryption: javax.crypto.SecretKey generateKey(java.lang.String)>", HardCodedError.class, 1);

		setErrorsCount("<issue81.Main: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		setErrorsCount("<issue81.Main: void main(java.lang.String[])>", NeverTypeOfError.class, 1);
		setErrorsCount("<issue81.Main: void main(java.lang.String[])>", HardCodedError.class, 1);

		setErrorsCount("<issuecognicrypt210.CogniCryptSecretKeySpec: void main(java.lang.String[])>", ConstraintError.class, 0);
		setErrorsCount("<issuecognicrypt210.CogniCryptSecretKeySpec: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<issuecognicrypt210.CogniCryptSecretKeySpec: void main(java.lang.String[])>", HardCodedError.class, 1);

		setErrorsCount("<issue70.ClientProtocolDecoder: byte[] decryptAES(byte[])>", ConstraintError.class, 1);
		setErrorsCount("<issue70.ClientProtocolDecoder: byte[] decryptAES(byte[])>", RequiredPredicateError.class, 3);

		setErrorsCount("<issue68.Main: void main(java.lang.String[])>", IncompleteOperationError.class, 2);

		setErrorsCount("<issue68.AESCryptor: byte[] getKey(java.lang.String)>", NeverTypeOfError.class, 1);
		setErrorsCount("<issue68.AESCryptor: byte[] getKey(java.lang.String)>", RequiredPredicateError.class, 2);
		setErrorsCount("<issue68.AESCryptor: byte[] getKey(java.lang.String)>", IncompleteOperationError.class, 1);
		setErrorsCount("<issue68.AESCryptor: byte[] getKey(java.lang.String)>", HardCodedError.class, 1);
		setErrorsCount("<issue68.AESCryptor: javax.crypto.SecretKeyFactory getFactory()>", ConstraintError.class, 1);
		setErrorsCount("<issue68.AESCryptor: byte[] encryptImpl(byte[])>", RequiredPredicateError.class, 1);

		setErrorsCount("<issue68.AESCryptor: void <init>(byte[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<issue68.AESCryptor: byte[] decryptImpl(byte[])>", RequiredPredicateError.class, 2);

		setErrorsCount("<issue49.Main: java.security.PrivateKey getPrivateKey()>", ConstraintError.class, 1);
		setErrorsCount("<issue49.Main: byte[] sign(java.lang.String)>", RequiredPredicateError.class, 1);

		setErrorsCount("<issue103.Main: void main(java.lang.String[])>", RequiredPredicateError.class, 4);

		setErrorsCount("<issue137.Program: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<issue137.Program: void main(java.lang.String[])>", IncompleteOperationError.class, 0);
		scanner.exec();
		assertErrors();
	}
}
