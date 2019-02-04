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
		setErrorsCount("<example.misuse.RequiredPredicateError: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		
		// false negatives
		setErrorsCount("<example.misuse.RequiredPredicateError: void useModeInsideAnotherMode()>", RequiredPredicateError.class, 1);
		
		// false positives
		setErrorsCount("<example.misuse.TypeStateErrorExample: void main(java.lang.String[])>", ImpreciseValueExtractionError.class, 3);				
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
		
		scanner.exec();
	  	assertErrors();
	}
}
