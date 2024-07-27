package headless;

import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.HardCodedError;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.NeverTypeOfError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import de.fraunhofer.iem.scanner.HeadlessCryptoScanner;
import org.junit.Test;

import java.io.File;

public class StaticAnalysisDemoTest extends AbstractHeadlessTest {

	@Test
	public void cogniCryptDemoExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/CogniCryptDemoExample").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<example.ConstraintErrorExample: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.ConstraintErrorExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		setErrorsCount("<example.fixed.ConstraintErrorExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		setErrorsCount("<example.fixed.ConstraintErrorExample: void main(java.lang.String[])>", ConstraintError.class, 1);

		setErrorsCount("<example.PredicateMissingExample: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<example.PredicateMissingExample: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<example.fixed.PredicateMissingExample: void main(java.lang.String[])>", ConstraintError.class, 1);

		setErrorsCount("<example.TypestateErrorExample: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.IncompleteOperationErrorExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		
		scanner.run();
		assertErrors(scanner.getCollectedErrors());
	}
	

	@Test
	public void cryptoMisuseExampleProject() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/CryptoMisuseExamples").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<main.Msg: byte[] sign(java.lang.String)>", ConstraintError.class, 1);
		setErrorsCount("<main.Msg: byte[] sign(java.lang.String)>", RequiredPredicateError.class, 1);
		setErrorsCount("<main.Msg: java.security.PrivateKey getPrivateKey()>", ConstraintError.class, 1);
		setErrorsCount("<main.Msg: java.security.PrivateKey getPrivateKey()>", RequiredPredicateError.class, 2);

		setErrorsCount("<main.Msg: void encryptAlgFromField()>", ConstraintError.class, 1);
		setErrorsCount("<main.Msg: void encryptAlgFromField()>", IncompleteOperationError.class, 1);
		
		setErrorsCount("<main.Msg: void encrypt()>", ConstraintError.class, 1);
		setErrorsCount("<main.Msg: void encrypt()>", IncompleteOperationError.class, 1);
		
		setErrorsCount("<main.Msg: void encryptAlgFromVar()>", ConstraintError.class, 1);
		setErrorsCount("<main.Msg: void encryptAlgFromVar()>", IncompleteOperationError.class, 1);

		setErrorsCount("<main.Encrypt: void incorrectBigInteger()>", ConstraintError.class, 2);
		setErrorsCount("<main.Encrypt: void incorrectBigInteger()>", RequiredPredicateError.class, 1);
		
		setErrorsCount("<main.Encrypt: void incorrect()>", ConstraintError.class, 1);
		setErrorsCount("<main.Encrypt: void incorrect()>", RequiredPredicateError.class, 2);

		scanner.run();
		assertErrors(scanner.getCollectedErrors());
	}
	
	@Test
	public void glassfishExample() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/glassfish-embedded").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount("<org.glassfish.grizzly.config.ssl.CustomClass: void init(javax.crypto.SecretKey,java.lang.String)>", RequiredPredicateError.class, 1);
		setErrorsCount("<org.glassfish.grizzly.config.ssl.CustomClass: void init(javax.crypto.SecretKey,java.lang.String)>", ConstraintError.class, 1);

		setErrorsCount("<org.glassfish.grizzly.config.ssl.JSSESocketFactory: java.security.KeyStore getStore(java.lang.String,java.lang.String,java.lang.String)>", HardCodedError.class, 1);
		setErrorsCount("<org.glassfish.grizzly.config.ssl.JSSESocketFactory: java.security.KeyStore getStore(java.lang.String,java.lang.String,java.lang.String)>", NeverTypeOfError.class, 0);
		setErrorsCount("<org.glassfish.grizzly.config.ssl.JSSESocketFactory: java.security.KeyStore getStore(java.lang.String,java.lang.String,java.lang.String)>", ImpreciseValueExtractionError.class, 1);

		scanner.run();
		assertErrors(scanner.getCollectedErrors());
	}
	

	@Test
	public void oracleExample() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/OracleExample").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<main.Main: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<main.Main: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<main.Main: void main(java.lang.String[])>", RequiredPredicateError.class, 3);
		setErrorsCount("<main.Main: void keyStoreExample()>", ConstraintError.class, 1);
		setErrorsCount("<main.Main: void keyStoreExample()>", NeverTypeOfError.class, 0);

		setErrorsCount("<main.Main: void keyStoreExample()>", HardCodedError.class, 1);
		setErrorsCount("<main.Main: void cipherUsageExample()>", ConstraintError.class, 1);

		setErrorsCount("<main.Main: void use(javax.crypto.Cipher)>", TypestateError.class, 1);


		//TODO this is a spurious finding. What happens here?
		setErrorsCount("<Crypto.PWHasher: java.lang.Boolean verifyPWHash(char[],java.lang.String)>", RequiredPredicateError.class, 3);


		setErrorsCount("<main.Main: void incorrectKeyForWrongCipher()>", ConstraintError.class, 1);
		setErrorsCount("<main.Main: void incorrectKeyForWrongCipher()>", RequiredPredicateError.class, 0);

		setErrorsCount("<main.Main: void useWrongDoFinal()>", TypestateError.class, 1);
		setErrorsCount("<main.Main: void useWrongDoFinal()>", ConstraintError.class, 1);
		setErrorsCount("<main.Main: void useCorrectDoFinal()>", ConstraintError.class, 1);
		setErrorsCount("<main.Main: void useNoDoFinal()>", IncompleteOperationError.class, 1);
		setErrorsCount("<main.Main: void useNoDoFinal()>", ConstraintError.class, 1);
		//TODO: This is wrong.
		setErrorsCount("<main.Main: void useDoFinalInLoop()>", TypestateError.class, 0);
		setErrorsCount("<main.Main: void useDoFinalInLoop()>", IncompleteOperationError.class, 1);
		setErrorsCount("<main.Main: void useDoFinalInLoop()>", ConstraintError.class, 1);


		setErrorsCount("<main.Main: void interproceduralTypestate()>", ConstraintError.class, 1);

		scanner.run();
		assertErrors(scanner.getCollectedErrors());
	}
	
	@Test
	public void predicateInstanceOfExample() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/PredicateInstanceOfExample").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		setErrorsCount(new ErrorSpecification.Builder("<crypto.CipherExample: void cipherExampleOne()>")
				.withTPs(ConstraintError.class, 1)
				.withTPs(RequiredPredicateError.class, 0)
				.build());
		setErrorsCount("<crypto.CipherExample: void cipherExampleTwo()>", ConstraintError.class, 1);
		setErrorsCount("<crypto.CipherExample: void cipherExampleTwo()>", RequiredPredicateError.class, 0);

		scanner.run();
		assertErrors(scanner.getCollectedErrors());
	}

	@Test
	public void hardCodedExample() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/HardcodedTestExamples/").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<TruePositive: byte[] getKey(char[],byte[],int,int)>", HardCodedError.class, 1);
		setErrorsCount("<TruePositive: byte[] getKey(char[],byte[],int,int)>", RequiredPredicateError.class, 2);

		setErrorsCount("<TrueNegative: byte[] getKey(char[],byte[],int,int)>", HardCodedError.class, 0);
		setErrorsCount("<TrueNegative: byte[] getKey(char[],byte[],int,int)>", RequiredPredicateError.class, 0);

		scanner.run();
		assertErrors(scanner.getCollectedErrors());
	}
	
	@Test
	public void sslExample() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/SSLMisuseExample").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		scanner.run();
		assertErrors(scanner.getCollectedErrors());
	}
}
