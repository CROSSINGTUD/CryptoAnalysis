package tests.headless.bugfixes;

import java.io.File;

import org.junit.Test;

import crypto.HeadlessCryptoScanner;
import crypto.analysis.errors.ConstraintError;
import tests.headless.AbstractHeadlessTest;
import tests.headless.MavenProject;

/**
 * Refers to https://github.com/CROSSINGTUD/CryptoAnalysis/issues/270
 */
public class NullpointerPredicateForFields extends AbstractHeadlessTest {
	@Test
	public void issue270() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/Bugfixes/issue270").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.Launcher: void <init>()>", ConstraintError.class, 1);

		// Must not throw NullPointerException in ConstraintSolver:init()!
		scanner.exec();
		assertErrors();
	}
}
