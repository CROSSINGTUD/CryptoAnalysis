package tests.headless;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

import crypto.HeadlessCryptoScanner;

/**
 * @author Enri Ozuni
 */
public class BragaCryptoTest extends AbstractHeadlessTest {
	/**
	 * The following Headless tests are deducted from the Braga et al. paper which benchmarks
	 * several static analyzer tools against several hundred test projects containing various
	 * Cryptographic providers of the JCA framework. For the creation of these Headless tests, 
	 * various projects from CryptoGuard were considered and used for the testing of provider
	 * detection functionality. The test projects of the paper can be found in the link below
	 * and are distunguished on the paper by gooduses and misuses:
	 * https://bitbucket.org/alexmbraga/cryptogooduses/src/master/
	 * https://bitbucket.org/alexmbraga/cryptomisuses/src/master/
	 */
	
//	@Test
//	public void avoidFixedPredictableSeedExamples() {
//		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidFixedPredictableSeed").getAbsolutePath();
//		MavenProject mavenProject = createAndCompile(mavenProjectPath);
//		HeadlessCryptoScanner scanner = createScanner(mavenProject);
//		
//		scanner.exec();
//		assertErrors();
//	}
//	
//	@Test
//	public void avoidStatisticPRNGExamples() {
//		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidStatisticPRNG").getAbsolutePath();
//		MavenProject mavenProject = createAndCompile(mavenProjectPath);
//		HeadlessCryptoScanner scanner = createScanner(mavenProject);
//		
//		scanner.exec();
//		assertErrors();
//	}
	
	@Test
	public void alwaysDefineCSPExamples() {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/alwaysDefineCSP").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		
		scanner.exec();
		assertErrors();
	}
	
}
