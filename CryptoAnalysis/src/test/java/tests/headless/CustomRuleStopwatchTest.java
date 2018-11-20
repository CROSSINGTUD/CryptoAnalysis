package tests.headless;

import java.io.File;

import org.junit.Test;

import crypto.HeadlessCryptoScanner;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.TypestateError;

public class CustomRuleStopwatchTest extends AbstractHeadlessTest{

	@Test
	public void loadExternalRuleAndStopwatchExample() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/StopwatchExample").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		String rulesDir = new File("../CryptoAnalysisTargets/StopwatchExample/rules").getAbsolutePath();
		HeadlessCryptoScanner scanner = createScanner(mavenProject, rulesDir);
		//TODO this is wrong. The state machine does not label the correct accepting states for the state machine.
		setErrorsCount("<main.Main: void correct()>", IncompleteOperationError.class, 2);
		setErrorsCount("<main.Main: void wrong()>", TypestateError.class, 1);
		setErrorsCount("<main.Main: void context(com.google.common.base.Stopwatch)>", TypestateError.class, 1);
		setErrorsCount("<main.Main: void context2(com.google.common.base.Stopwatch)>", TypestateError.class, 1);
		scanner.exec();
		assertErrors();

	}
}
