package tests.headless;

import java.io.File;

import org.junit.Test;

import crypto.HeadlessCryptoScanner;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;

public class IgnorePackageTest extends AbstractHeadlessTest{

	@Test
	public void loadMessageDigestExample() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/CogniCryptDemoExample").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		//false positive
		setErrorsCount("<example.ConstraintErrorExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		setErrorsCount("<example.ConstraintErrorExample: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.IncompleOperationErrorExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		setErrorsCount("<example.PredicateMissingExample: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.PredicateMissingExample: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.TypestateErrorExample: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.fixed.ConstraintErrorExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		scanner.exec();
		assertErrors();
	}

}
