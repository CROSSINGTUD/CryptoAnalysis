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

public class SootJava9ConfigurationTest extends AbstractHeadlessTest {

	
	@Test
	public void testJava9ClasspathProject() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/Java9ClasspathExample").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, Ruleset.JavaCryptographicArchitecture);
		
		setErrorsCount("<ConstraintErrorExample: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<ConstraintErrorExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		
		scanner.exec();
	  	assertErrors();
	}
	
	@Test
	public void testJava8Project() {
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
	public void testJava9ModularProject() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/Java9ModuleExample").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, Ruleset.JavaCryptographicArchitecture);
		
		setErrorsCount("<org.demo.jpms.MainClass: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<org.demo.jpms.MainClass: void main(java.lang.String[])>", IncompleteOperationError.class, 1);

		scanner.exec();
	  	assertErrors();
	}
}
