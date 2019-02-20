package tests.headless;

import java.io.File;

import org.junit.Test;

import crypto.HeadlessCryptoScanner;
import crypto.analysis.errors.*;
import test.IDEALCrossingTestingFramework;

public class BouncyCastleHeadlessTest extends AbstractHeadlessTest {

	@Test
	public void testDemo() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BouncyCastleDemoExample").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, IDEALCrossingTestingFramework.RESOURCE_PATH);
		
		setErrorsCount("<example.misuse.TypeStateErrorExample: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.misuse.IncompleteOperationErrorExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);		
		
		// Ignored
		setErrorsCount("<example.misuse.TypeStateErrorExample: void main(java.lang.String[])>", ImpreciseValueExtractionError.class, 3);				
		setErrorsCount("<example.misuse.RequiredPredicateErrorExample: void useModeInsideAnotherMode()>", ImpreciseValueExtractionError.class, 3);
		setErrorsCount("<example.misuse.RequiredPredicateErrorExample: void main(java.lang.String[])>", ImpreciseValueExtractionError.class, 3);
		setErrorsCount("<example.nomisuse.TypeStateErrorExample: void main(java.lang.String[])>", ImpreciseValueExtractionError.class, 3);
		setErrorsCount("<example.nomisuse.IncompleteOperationErrorExample: void main(java.lang.String[])>", ImpreciseValueExtractionError.class, 3);
		setErrorsCount("<example.nomisuse.RequiredPredicateErrorExample: void main(java.lang.String[])>", ImpreciseValueExtractionError.class, 3);
		
		scanner.exec();
	  	assertErrors();
	}	
	
	@Test
	public void testAES() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BouncyCastleAESExamples").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, IDEALCrossingTestingFramework.RESOURCE_PATH);
		
		setErrorsCount("<pattern.AESTest: void testAESEngine2()>", IncompleteOperationError.class, 1);
		setErrorsCount("<pattern.AESTest: void testAESLightEngine2()>", TypestateError.class, 1);
		
		// Ignored
		setErrorsCount("<pattern.AESTest: void testAESEngine1()>", ImpreciseValueExtractionError.class, 2);
		setErrorsCount("<pattern.AESTest: void testAESEngine2()>", ImpreciseValueExtractionError.class, 1);
		setErrorsCount("<pattern.AESTest: void testMonteCarloAndVector()>", ImpreciseValueExtractionError.class, 2);

		scanner.exec();
	  	assertErrors();
	}
	
	@Test
	public void testRSA() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BouncyCastleRSAExamples").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, IDEALCrossingTestingFramework.RESOURCE_PATH);
		
		scanner.exec();
	  	assertErrors();
	}
	
	@Test
	public void testMac() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BouncyCastleMacExamples").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, IDEALCrossingTestingFramework.RESOURCE_PATH);
		
		setErrorsCount("<pattern.MacTest: void testMac2()>", IncompleteOperationError.class, 1);
		
		// Ignored
		setErrorsCount("<pattern.MacTest: void testMac1()>", ImpreciseValueExtractionError.class, 1);
		
		scanner.exec();
	  	assertErrors();
	}
	
	@Test
	public void testDigest() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BouncyCastleDigestExamples").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, IDEALCrossingTestingFramework.RESOURCE_PATH);
		
		setErrorsCount("<DigestTest: void digestWithoutUpdate()>", TypestateError.class, 1);
		
		// False Positives due to issue #129
		setErrorsCount("<DigestTest: void digestDefaultUsage()>", TypestateError.class, 1);
		setErrorsCount("<DigestTest: void digestWithMultipleUpdates()>", IncompleteOperationError.class, 1);
		setErrorsCount("<DigestTest: void multipleDigests()>", IncompleteOperationError.class, 2);
		setErrorsCount("<DigestTest: void digestWithReset()>", TypestateError.class, 1);
		
		scanner.exec();
	  	assertErrors();
	}
	
	@Test
	public void testSigner() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BouncyCastleSignerExamples").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, IDEALCrossingTestingFramework.RESOURCE_PATH);
		
		scanner.exec();
	  	assertErrors();
	}
}
