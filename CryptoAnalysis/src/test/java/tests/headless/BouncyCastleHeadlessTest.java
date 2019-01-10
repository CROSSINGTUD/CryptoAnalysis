package tests.headless;

import java.io.File;

import org.junit.Test;

import crypto.HeadlessCryptoScanner;
import crypto.analysis.errors.*;
import crypto.analysis.errors.TypestateError;
import test.IDEALCrossingTestingFramework;

public class BouncyCastleHeadlessTest extends AbstractHeadlessTest {

	@Test
	public void test() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BouncyCastleDemoExample").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, IDEALCrossingTestingFramework.RESOURCE_PATH);
		
		setErrorsCount("<example.misuse.TypeStateErrorExample: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.misuse.RequiredPredicateError: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		
		// false positives
		setErrorsCount("<example.misuse.TypeStateErrorExample: void main(java.lang.String[])>", ImpreciseValueExtractionError.class, 3);

		// false negatives
		setErrorsCount("<example.misuse.RequiredPredicateError: void useModeInsideAnotherMode()>", RequiredPredicateError.class, 1);
		
		scanner.exec();
	  	assertErrors();
	}	
}
