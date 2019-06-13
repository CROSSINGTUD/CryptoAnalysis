package tests.headless;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

import crypto.HeadlessCryptoScanner;
import crypto.analysis.CrySLRulesetSelector.Ruleset;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;

public class DummyHeadlessTest extends AbstractHeadlessTest {

	
	@Test
	public void testOne() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/Dummy9").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, Ruleset.JavaCryptographicArchitecture);
		
		setErrorsCount("<ConstraintErrorExample: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<ConstraintErrorExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		
		scanner.exec();
	  	assertErrors();
	}
	
	@Test
	public void testFour() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/CogniCryptDemoExample").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, Ruleset.JavaCryptographicArchitecture);
		
		setErrorsCount("<example.PredicateMissingExample: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.PredicateMissingExample: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.IncompleOperationErrorExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		setErrorsCount("<example.ConstraintErrorExample: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.ConstraintErrorExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		setErrorsCount("<example.TypestateErrorExample: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.fixed.ConstraintErrorExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);

		scanner.exec();
	  	assertErrors();
	}

	@Test
	public void testTwo() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/Dummy8").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, Ruleset.JavaCryptographicArchitecture);
		
		setErrorsCount("<ConstraintErrorExample: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<ConstraintErrorExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		
		scanner.exec();
	  	assertErrors();
	}
	
	@Test
	public void testThree() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/JPMSExample").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, Ruleset.JavaCryptographicArchitecture);
		
		setErrorsCount("<org.demo.jpms.HelloModularWorld: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<org.demo.jpms.HelloModularWorld: void main(java.lang.String[])>", IncompleteOperationError.class, 1);

		
		scanner.exec();
	  	assertErrors();
	}
}
