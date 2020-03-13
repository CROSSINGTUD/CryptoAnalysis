package tests.providerdetection;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import crypto.providerdetection.ProviderDetection;

public class ProviderDetectionTests {
	
	// Checks if provider of type `java.security.Provider` is detected when given as a variable
	@Test
	public void providerDetectionTest1() {
		ProviderDetectionTestingFramework providerDetection = new ProviderDetectionTestingFramework();
		String sootClassPath = providerDetection.getSootClassPath();
		String mainClass = "tests.providerdetection.ProviderDetectionExample1"; 
		providerDetection.setupSoot(sootClassPath, mainClass);
		providerDetection.analyze();
		
		String expected = "BouncyCastle-JCA";
		String actual = providerDetection.getProvider();
		assertEquals(expected, actual);
	}
	
	// Checks if provider of type `java.security.Provider` is detected when given directly
	@Test
	public void providerDetectionTest2() {
		ProviderDetectionTestingFramework providerDetection = new ProviderDetectionTestingFramework();
		String sootClassPath = providerDetection.getSootClassPath();
		String mainClass = "tests.providerdetection.ProviderDetectionExample2"; 
		providerDetection.setupSoot(sootClassPath, mainClass);
		providerDetection.analyze();
		
		String expected = "BouncyCastle-JCA";
		String actual = providerDetection.getProvider();
		assertEquals(expected, actual);
	}
	

	// Checks if rules are correctly extracted, when provider is of type `java.security.Provider`,
	// is given as a variable, and the rules for that provider exist
	@Test
	public void providerDetectionTest3() {
		ProviderDetectionTestingFramework providerDetection = new ProviderDetectionTestingFramework();
		String sootClassPath = providerDetection.getSootClassPath();
		String mainClass = "tests.providerdetection.ProviderDetectionExample1"; 
		providerDetection.setupSoot(sootClassPath, mainClass);
		providerDetection.analyze();
		
		String rulesDirectory = providerDetection.getRulesDirectory();
		assertEquals(true, rulesDirectory.endsWith("BouncyCastle-JCA"));
	}
	
	
	// Checks if rules are correctly extracted, when provider is of type `java.security.Provider`,
	// is given directly, and the rules for that provider exist
	@Test
	public void providerDetectionTest4() {
		ProviderDetectionTestingFramework providerDetection = new ProviderDetectionTestingFramework();
		String sootClassPath = providerDetection.getSootClassPath();
		String mainClass = "tests.providerdetection.ProviderDetectionExample2"; 
		providerDetection.setupSoot(sootClassPath, mainClass);
		providerDetection.analyze();
		
		String rulesDirectory = providerDetection.getRulesDirectory();
		assertEquals(true, rulesDirectory.endsWith("BouncyCastle-JCA"));
	}
	
	// Checks if rules are correctly extracted, when provider is of type `java.security.Provider`,
	// is given as a variable, and the rules for that provider exist
	@Test
	public void providerDetectionTest5() {
		ProviderDetectionTestingFramework providerDetection = new ProviderDetectionTestingFramework();
		String sootClassPath = providerDetection.getSootClassPath();
		String mainClass = "tests.providerdetection.ProviderDetectionExample3"; 
		providerDetection.setupSoot(sootClassPath, mainClass);
		providerDetection.analyze();
		
		String rulesDirectory = providerDetection.getRulesDirectory();
		assertEquals(true, rulesDirectory.endsWith("BouncyCastle-JCA"));
	}
	
	// Checks if rules are correctly extracted, when provider is of type `java.security.Provider`,
	// is given directly, and the rules for that provider exist
	@Test
	public void providerDetectionTest6() {
		ProviderDetectionTestingFramework providerDetection = new ProviderDetectionTestingFramework();
		String sootClassPath = providerDetection.getSootClassPath();
		String mainClass = "tests.providerdetection.ProviderDetectionExample4"; 
		providerDetection.setupSoot(sootClassPath, mainClass);
		providerDetection.analyze();
		
		String rulesDirectory = providerDetection.getRulesDirectory();
		assertEquals(true, rulesDirectory.endsWith("BouncyCastle-JCA"));
	}
	
	// Checks if provider of type `java.lang.String` is detected when given as a variable
	@Test
	public void providerDetectionTest7() {
		ProviderDetectionTestingFramework providerDetection = new ProviderDetectionTestingFramework();
		String sootClassPath = providerDetection.getSootClassPath();
		String mainClass = "tests.providerdetection.ProviderDetectionExample5"; 
		providerDetection.setupSoot(sootClassPath, mainClass);
		providerDetection.analyze();
		
		String expected = "BouncyCastle-JCA";
		String actual = providerDetection.getProvider();
		assertEquals(expected, actual);
	}
	
	// Checks if provider of type `java.lang.String` is detected when given directly
	@Test
	public void providerDetectionTest8() {
		ProviderDetectionTestingFramework providerDetection = new ProviderDetectionTestingFramework();
		String sootClassPath = providerDetection.getSootClassPath();
		String mainClass = "tests.providerdetection.ProviderDetectionExample6"; 
		providerDetection.setupSoot(sootClassPath, mainClass);
		providerDetection.analyze();
		
		String expected = "BouncyCastle-JCA";
		String actual = providerDetection.getProvider();
		assertEquals(expected, actual);
	}
	
	// Checks if rules are correctly extracted, when provider is of type `java.lang.String`,
	// is given as a variable, and the rules for that provider exist
	@Test
	public void providerDetectionTest9() {
		ProviderDetectionTestingFramework providerDetection = new ProviderDetectionTestingFramework();
		String sootClassPath = providerDetection.getSootClassPath();
		String mainClass = "tests.providerdetection.ProviderDetectionExample5"; 
		providerDetection.setupSoot(sootClassPath, mainClass);
		providerDetection.analyze();
		
		String rulesDirectory = providerDetection.getRulesDirectory();
		assertEquals(true, rulesDirectory.endsWith("BouncyCastle-JCA"));
	}
	
	// Checks if rules are correctly extracted, when provider is of type `java.lang.String`,
	// is given directly, and the rules for that provider exist
	@Test
	public void providerDetectionTest10() {
		ProviderDetectionTestingFramework providerDetection = new ProviderDetectionTestingFramework();
		String sootClassPath = providerDetection.getSootClassPath();
		String mainClass = "tests.providerdetection.ProviderDetectionExample6"; 
		providerDetection.setupSoot(sootClassPath, mainClass);
		providerDetection.analyze();
		
		String rulesDirectory = providerDetection.getRulesDirectory();
		assertEquals(true, rulesDirectory.endsWith("BouncyCastle-JCA"));
	}
	
	// Checks if rules are correctly extracted, when provider is of type `java.lang.String`,
	// is given as a variable, and the rules for that provider exist
	@Test
	public void providerDetectionTest11() {
		ProviderDetectionTestingFramework providerDetection = new ProviderDetectionTestingFramework();
		String sootClassPath = providerDetection.getSootClassPath();
		String mainClass = "tests.providerdetection.ProviderDetectionExample7"; 
		providerDetection.setupSoot(sootClassPath, mainClass);
		providerDetection.analyze();
		
		String rulesDirectory = providerDetection.getRulesDirectory();
		assertEquals(true, rulesDirectory.endsWith("BouncyCastle-JCA"));
	}
	
	// Checks if rules are correctly extracted, when provider is of type `java.lang.String`,
	// is given directly, and the rules for that provider exist
	@Test
	public void providerDetectionTest12() {
		ProviderDetectionTestingFramework providerDetection = new ProviderDetectionTestingFramework();
		String sootClassPath = providerDetection.getSootClassPath();
		String mainClass = "tests.providerdetection.ProviderDetectionExample8"; 
		providerDetection.setupSoot(sootClassPath, mainClass);
		providerDetection.analyze();
		
		String rulesDirectory = providerDetection.getRulesDirectory();
		assertEquals(true, rulesDirectory.endsWith("BouncyCastle-JCA"));
	}
	
	// Checks if the default ruleset is chosen when provider of type `java.security.Provider`
	// flows through TERNARY operators
	@Test
	public void providerDetectionTest13() {
		ProviderDetectionTestingFramework providerDetection = new ProviderDetectionTestingFramework();
		String sootClassPath = providerDetection.getSootClassPath();
		String mainClass = "tests.providerdetection.ProviderDetectionExample9"; 
		providerDetection.setupSoot(sootClassPath, mainClass);
		providerDetection.analyze();
		
		String rulesDirectory = providerDetection.getRulesDirectory();
		assertEquals(true, rulesDirectory.endsWith("JavaCryptographicArchitecture"));
	}
	
	// Checks if the default ruleset is chosen when provider of type `java.security.Provider`
	// flows through IF-ELSE statements
	@Test
	public void providerDetectionTest14() {
		ProviderDetectionTestingFramework providerDetection = new ProviderDetectionTestingFramework();
		String sootClassPath = providerDetection.getSootClassPath();
		String mainClass = "tests.providerdetection.ProviderDetectionExample10"; 
		providerDetection.setupSoot(sootClassPath, mainClass);
		providerDetection.analyze();
		
		String rulesDirectory = providerDetection.getRulesDirectory();
		assertEquals(true, rulesDirectory.endsWith("JavaCryptographicArchitecture"));
	}
	
	// Checks if the default ruleset is chosen when provider of type `java.security.Provider`
	// flows through SWITCH statements
	@Test
	public void providerDetectionTest15() {
		ProviderDetectionTestingFramework providerDetection = new ProviderDetectionTestingFramework();
		String sootClassPath = providerDetection.getSootClassPath();
		String mainClass = "tests.providerdetection.ProviderDetectionExample11"; 
		providerDetection.setupSoot(sootClassPath, mainClass);
		providerDetection.analyze();
		
		String rulesDirectory = providerDetection.getRulesDirectory();
		assertEquals(true, rulesDirectory.endsWith("JavaCryptographicArchitecture"));
	}
	
	// Checks if the default ruleset is chosen when provider of type `java.lang.String`
	// flows through TERNARY operators
	@Test
	public void providerDetectionTest16() {
		ProviderDetectionTestingFramework providerDetection = new ProviderDetectionTestingFramework();
		String sootClassPath = providerDetection.getSootClassPath();
		String mainClass = "tests.providerdetection.ProviderDetectionExample12"; 
		providerDetection.setupSoot(sootClassPath, mainClass);
		providerDetection.analyze();
		
		String rulesDirectory = providerDetection.getRulesDirectory();
		assertEquals(true, rulesDirectory.endsWith("JavaCryptographicArchitecture"));
	}
	
	// Checks if the default ruleset is chosen when provider of type `java.lang.String`
	// flows through IF-ELSE statements
	@Test
	public void providerDetectionTest17() {
		ProviderDetectionTestingFramework providerDetection = new ProviderDetectionTestingFramework();
		String sootClassPath = providerDetection.getSootClassPath();
		String mainClass = "tests.providerdetection.ProviderDetectionExample13"; 
		providerDetection.setupSoot(sootClassPath, mainClass);
		providerDetection.analyze();
		
		String rulesDirectory = providerDetection.getRulesDirectory();
		assertEquals(true, rulesDirectory.endsWith("JavaCryptographicArchitecture"));
	}
	
	// Checks if the default ruleset is chosen when provider of type `java.lang.String`
	// flows through SWITCH statements
	@Test
	public void providerDetectionTest18() {
		ProviderDetectionTestingFramework providerDetection = new ProviderDetectionTestingFramework();
		String sootClassPath = providerDetection.getSootClassPath();
		String mainClass = "tests.providerdetection.ProviderDetectionExample14"; 
		providerDetection.setupSoot(sootClassPath, mainClass);
		providerDetection.analyze();
		
		String rulesDirectory = providerDetection.getRulesDirectory();
		assertEquals(true, rulesDirectory.endsWith("JavaCryptographicArchitecture"));
	}
	
}
