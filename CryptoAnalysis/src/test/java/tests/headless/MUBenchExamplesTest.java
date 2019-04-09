package tests.headless;

import java.io.File;

import org.junit.Test;

import crypto.HeadlessCryptoScanner;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import test.IDEALCrossingTestingFramework;

public class MUBenchExamplesTest extends AbstractHeadlessTest{

	@Test
	public void muBenchExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/MUBenchExamples").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject, IDEALCrossingTestingFramework.RESOURCE_PATH);
		
		setErrorsCount("<example.CipherUsesBlowfishExample: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.CipherUsesBlowfishExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		
		setErrorsCount("<example.CipherUsesBlowfishWithECBModeExample: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.CipherUsesBlowfishWithECBModeExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);

		setErrorsCount("<example.CipherUsesDESExample: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.CipherUsesDESExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		
		setErrorsCount("<example.CipherUsesDSAExample: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.CipherUsesDSAExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		
		setErrorsCount("<example.CipherUsesInvalidKeyExample: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.CipherUsesInvalidKeyExample: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		
		setErrorsCount("<example.CipherUsesJustAESExample: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.CipherUsesJustAESExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		
		setErrorsCount("<example.CipherUsesNonRandomKeyExample: void main(java.lang.String[])>", RequiredPredicateError.class, 2);
		
		setErrorsCount("<example.CipherUsesPBEWithMD5AndDESExample: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.CipherUsesPBEWithMD5AndDESExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		
		setErrorsCount("<example.CipherUsesRSAWithCBCExample: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<example.CipherUsesRSAWithCBCExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		
		setErrorsCount("<example.EmptyArrayUsedForCipherDoFinalExample: void main(java.lang.String[])>", ConstraintError.class, 1);

		setErrorsCount("<example.InitInMacCalledMoreThanOnceExample: void main(java.lang.String[])>", TypestateError.class, 2);
		setErrorsCount("<example.InitInMacCalledMoreThanOnceExample: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		
		
		scanner.exec();
		assertErrors();
	}

}
