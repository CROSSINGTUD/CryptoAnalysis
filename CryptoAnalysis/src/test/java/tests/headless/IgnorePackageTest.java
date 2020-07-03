package tests.headless;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

import crypto.HeadlessCryptoScanner;
import crypto.exceptions.CryptoAnalysisException;

public class IgnorePackageTest extends AbstractHeadlessTest{

	@Test (expected = CryptoAnalysisException.class)
	public void ignorePackageExample() throws IOException {
		String mavenProjectPath = new File("../CryptoAnalysisTargets/IgnorePackageExample").getAbsolutePath();
		MavenProject mavenProject = createAndCompile(mavenProjectPath);
		HeadlessCryptoScanner scanner = createScanner(mavenProject);
		setIGNORE_PACKAGE(true);
		File ignorePackage = new File("../CryptoAnalysisTargets/IgnorePackages.txt");
		setIgnorePackages(Files.readAllLines(Paths.get(ignorePackage.getAbsolutePath()), StandardCharsets.UTF_8));
		scanner.exec();
		/*
		* Ignored the 2 IncompleteOperationError in MessageDigestExample.MessageDigestExample.Main class as it specified in 
		* IgnorePackages.txt
		*/
	}

}
