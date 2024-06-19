package tests.headless;

import static org.junit.Assume.assumeTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import crypto.HeadlessCryptoScanner;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;

public class SootJava9ConfigurationTest extends AbstractHeadlessTest {

	@Before
	public void checkJavaVersion() {
		assumeTrue(getVersion() >= 9);
	}

	private static int getVersion() {
		String version = System.getProperty("java.version");
		if(version.startsWith("1.")) {
			version = version.substring(2, 3);
		} else {
			int dot = version.indexOf(".");
			if(dot != -1) { version = version.substring(0, dot); }
		} return Integer.parseInt(version);
	}

	@Test
	public void testJava9ClasspathProject() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/Java9ClasspathExample").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<ConstraintErrorExample: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<ConstraintErrorExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);

		scanner.run();
		assertErrors(scanner.getErrorCollection());
	}

	@Test
	public void testJava8Project() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/CogniCryptDemoExample").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.PredicateMissingExample: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		setErrorsCount("<example.PredicateMissingExample: void main(java.lang.String[])>", ConstraintError.class, 2);
		setErrorsCount("<example.IncompleteOperationErrorExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		setErrorsCount("<example.ConstraintErrorExample: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.ConstraintErrorExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		setErrorsCount("<example.TypestateErrorExample: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<example.fixed.ConstraintErrorExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		setErrorsCount("<example.fixed.ConstraintErrorExample: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.fixed.PredicateMissingExample: void main(java.lang.String[])>", ConstraintError.class, 1);

		scanner.run();
		assertErrors(scanner.getErrorCollection());
	}

	@Test
	public void testJava9ModularProject() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/Java9ModuleExample").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<org.demo.jpms.MainClass: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<org.demo.jpms.MainClass: void main(java.lang.String[])>", IncompleteOperationError.class, 1);

		scanner.run();
		assertErrors(scanner.getErrorCollection());
	}
}
