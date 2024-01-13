package tests.headless;

import crypto.HeadlessCryptoScanner;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IgnoreSectionsTest extends AbstractHeadlessTest {

	@Test
	public void ignoreNoPackages() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/IgnorePackagesExample").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		List<String> ignoredSections = Collections.emptyList();
		setIgnoredSections(ignoredSections);

		// No sections are ignored, i.e. all errors are be reported
		setErrorsCount("<example.ConstraintErrorExample: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.ConstraintErrorExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		setErrorsCount("<example.IncompleteOperationErrorExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		setErrorsCount("<example.PredicateMissingExample: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.PredicateMissingExample: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.TypestateErrorExample: void main(java.lang.String[])>", TypestateError.class, 1);

		scanner.exec();
		assertErrors();
	}

	@Test
	public void ignoreMethodsExample() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/IgnorePackagesExample").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		List<String> ignoredMethods = Arrays.asList(
				"example.PredicateMissingExample.main",
				"example.TypestateErrorExample.main"
		);
		setIgnoredSections(ignoredMethods);

		setErrorsCount("<example.ConstraintErrorExample: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.ConstraintErrorExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		setErrorsCount("<example.IncompleteOperationErrorExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);

		// Errors are not reported because methods 'main' are ignored in these classes
		setErrorsCount("<example.PredicateMissingExample: void main(java.lang.String[])>", ConstraintError.class, 0);
		setErrorsCount("<example.PredicateMissingExample: void main(java.lang.String[])>", RequiredPredicateError.class, 0);
		setErrorsCount("<example.TypestateErrorExample: void main(java.lang.String[])>", TypestateError.class, 0);

		scanner.exec();
		assertErrors();
	}

	@Test
	public void ignoreClassesExample() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/IgnorePackagesExample").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		List<String> ignoredClasses = Arrays.asList(
				"example.ConstraintErrorExample",
				"example.IncompleteOperationErrorExample"
		);
		setIgnoredSections(ignoredClasses);

		// Errors are not reported because classes 'ConstraintErrorExample' and 'IncompleteOperationErrorExample' are ignored
		setErrorsCount("<example.ConstraintErrorExample: void main(java.lang.String[])>", ConstraintError.class, 0);
		setErrorsCount("<example.ConstraintErrorExample: void main(java.lang.String[])>", IncompleteOperationError.class, 0);
		setErrorsCount("<example.IncompleteOperationErrorExample: void main(java.lang.String[])>", IncompleteOperationError.class, 0);

		setErrorsCount("<example.PredicateMissingExample: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.PredicateMissingExample: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.TypestateErrorExample: void main(java.lang.String[])>", TypestateError.class, 1);

		scanner.exec();
		assertErrors();
	}

	@Test
	public void ignoreWildcardExample() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/IgnorePackagesExample").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);

		List<String> ignoredWildcards = Collections.singletonList(
                "example.*"
        );
		setIgnoredSections(ignoredWildcards);

		// No errors are reported because the package 'example' is ignored
		setErrorsCount("<example.ConstraintErrorExample: void main(java.lang.String[])>", ConstraintError.class, 0);
		setErrorsCount("<example.ConstraintErrorExample: void main(java.lang.String[])>", IncompleteOperationError.class, 0);
		setErrorsCount("<example.IncompleteOperationErrorExample: void main(java.lang.String[])>", IncompleteOperationError.class, 0);
		setErrorsCount("<example.PredicateMissingExample: void main(java.lang.String[])>", ConstraintError.class, 0);
		setErrorsCount("<example.PredicateMissingExample: void main(java.lang.String[])>", RequiredPredicateError.class, 0);
		setErrorsCount("<example.TypestateErrorExample: void main(java.lang.String[])>", TypestateError.class, 0);

		scanner.exec();
		assertErrors();
	}

}
