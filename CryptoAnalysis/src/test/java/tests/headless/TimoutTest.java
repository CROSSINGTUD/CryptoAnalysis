package tests.headless;

import java.io.File;

import org.junit.Test;

import crypto.HeadlessCryptoScanner;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.RequiredPredicateError;
import tests.headless.FindingsType.FalseNegatives;;

public class TimoutTest extends AbstractHeadlessTest{

		@Test
		public void TimeoutExamples() {
			String mavenProjectPath = new File("../CryptoAnalysisTargets/TimeoutExample").getAbsolutePath();
			MavenProject mavenProject = createAndCompile(mavenProjectPath);
			HeadlessCryptoScanner scanner = createScanner(mavenProject);

			setErrorsCount("<pdf.sideChannelAttacks.HashVerificationVariableTime: void main(java.lang.String[])>", ConstraintError.class, 1);
			setErrorsCount("<pdf.sideChannelAttacks.PaddingOracle: boolean oracle(byte[],byte[])>", ConstraintError.class, 1);
			setErrorsCount("<pdf.sideChannelAttacks.PaddingOracle: boolean oracle(byte[],byte[])>", RequiredPredicateError.class, 2);
			setErrorsCount("<pdf.sideChannelAttacks.PaddingOracle: byte[] encripta()>", RequiredPredicateError.class, 3);
			setErrorsCount("<pdf.sideChannelAttacks.PaddingOracle: byte[] encripta()>", ConstraintError.class, 1);
			// seed <clinit> was skipped from analysis because of timeout 
			setErrorsCount(RequiredPredicateError.class, new FalseNegatives(1, ""), "<pdf.sideChannelAttacks.PaddingOracle: void <clinit>()>");
			scanner.exec();
			assertErrors();
		}
}
