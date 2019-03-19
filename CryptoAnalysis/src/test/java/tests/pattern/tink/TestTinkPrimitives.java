package tests.pattern.tink;

import java.io.File;

import org.junit.Ignore;

import test.UsagePatternTestingFramework;

@Ignore
public abstract class TestTinkPrimitives extends UsagePatternTestingFramework {
	@Override
	protected String getSootClassPath() {
		String sootCp = super.getSootClassPath();
		String userHome = System.getProperty("user.home");
		
		sootCp += File.pathSeparator + userHome + "/.m2/repository/com/google/crypto/tink/tink/1.2.0/tink-1.2.0.jar"; 			
		return sootCp;
	}

}
