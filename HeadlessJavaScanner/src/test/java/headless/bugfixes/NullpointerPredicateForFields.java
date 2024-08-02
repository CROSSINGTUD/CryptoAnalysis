package headless.bugfixes;

import java.io.File;

import de.fraunhofer.iem.scanner.HeadlessJavaScanner;
import headless.AbstractHeadlessTest;
import headless.MavenProject;
import org.junit.Test;

import crypto.analysis.errors.ConstraintError;

/**
 * Refers to https://github.com/CROSSINGTUD/CryptoAnalysis/issues/270
 */
public class NullpointerPredicateForFields extends AbstractHeadlessTest {
	@Test
	public void issue270() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/Bugfixes/issue270").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessJavaScanner scanner = createScanner(mavenProject);

		setErrorsCount("<example.Launcher: void <init>()>", ConstraintError.class, 1);

		// Must not throw NullPointerException in ConstraintSolver:init()!
		scanner.run();
		assertErrors(scanner.getCollectedErrors());
	}
}
