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
public class MUBenchExamplesTest extends AbstractHeadlessTest{
	
	
	/**
	 * The following Headless tests are deducted from the MUBench benchmarking tool 
	 * that detects various API misuses. For the creating these Headless tests, only 
	 * previous Cryptographic API misuses were considered from the data-set of MUBench,
	 * which contains numerous projects having these kind of API misuses.
	 */
	@Test
	public void muBenchExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/MUBenchExamples").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		/**
		 * The links for each test case redirect to the description of the misuse, 
		 * and also contain the correct usage of the corresponding misuse in the directory
		 * where `misuse.yml` file is contained
		 */
		
		// This test case corresponds to the following project in MUBench having this misuse:
		// https://github.com/akwick/MUBench/blob/master/data/tap-apps/misuses/1/misuse.yml
		setErrorsCount("<example.CipherUsesBlowfishExample: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.CipherUsesBlowfishExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		
		// This test case corresponds to the following project in MUBench having this misuse:
		// https://github.com/akwick/MUBench/blob/master/data/drftpd3-extended/misuses/1/misuse.yml
		setErrorsCount("<example.CipherUsesBlowfishWithECBModeExample: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.CipherUsesBlowfishWithECBModeExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);

		// This test case corresponds to the following project in MUBench having this misuse:
		// https://github.com/akwick/MUBench/blob/master/data/chensun/misuses/1/misuse.yml
		setErrorsCount("<example.CipherUsesDESExample: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.CipherUsesDESExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		
		// This test case corresponds to the following project in MUBench having this misuse:
		// https://github.com/akwick/MUBench/blob/master/data/secure-tcp/misuses/1/misuse.yml
		setErrorsCount("<example.CipherUsesDSAExample: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.CipherUsesDSAExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		
		// This test case corresponds to the following project in MUBench having the this misuse:
		// https://github.com/akwick/MUBench/blob/master/data/corona-old/misuses/1/misuse.yml		
		setErrorsCount("<example.CipherUsesJustAESExample: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.CipherUsesJustAESExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		
		// This test case corresponds to the following project in MUBench having this misuse:
		// https://github.com/akwick/MUBench/blob/master/data/chensun/misuses/2/misuse.yml
		setErrorsCount("<example.CipherUsesNonRandomKeyExample: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		
		// This test case corresponds to the following project in MUBench having this misuse:
		// https://github.com/akwick/MUBench/blob/master/data/minecraft-launcher/misuses/1/misuse.yml
		setErrorsCount("<example.CipherUsesPBEWithMD5AndDESExample: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.CipherUsesPBEWithMD5AndDESExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		
		// This test case corresponds to the following project in MUBench having this misuse:
		// https://github.com/akwick/MUBench/blob/master/data/dalvik/misuses/3/misuse.yml
		// **MUBench hints that using RSA with PKCS1Padding is unsafe, which the Cipher CrySL rule
		//   does not agree, so added the test case of using RSA with CBC mode**
		setErrorsCount("<example.CipherUsesRSAWithCBCExample: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.CipherUsesRSAWithCBCExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		
		// This test case corresponds to the following project in MUBench having this misuse:
		// https://github.com/akwick/MUBench/blob/master/data/pawotag/misuses/1/misuse.yml
		setErrorsCount("<example.EmptyArrayUsedForCipherDoFinalExample: void main(java.lang.String[])>", ConstraintError.class, 1);

		// This test case corresponds to the following project in MUBench having this misuse:
		// https://github.com/akwick/MUBench/blob/master/data/red5-server/misuses/1/misuse.yml
		setErrorsCount("<example.InitInMacCalledMoreThanOnceExample: void main(java.lang.String[])>", TypestateError.class, 2);
		setErrorsCount("<example.InitInMacCalledMoreThanOnceExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		
		
		scanner.exec();
		assertErrors();
	}

}
