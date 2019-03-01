package tests.providerdetection;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import crypto.providerdetection.ProviderDetection;

public class ProviderDetectionTests {
	
	//checks if provider of type `java.security.Provider` is detected when given as a variable
	@Test
	public void providerDetectionTest1() {
		ProviderDetection providerDetection = new ProviderDetection();
		String sootClassPath = providerDetection.getSootClassPath();
		String mainClass = "tests.providerdetection.ProviderDetectionExample1"; 
		providerDetection.setupSoot(sootClassPath, mainClass);
		providerDetection.analyze();
		
		String expected = "BC";
		String actual = providerDetection.provider;
		assertEquals(expected, actual);
	}
	
	//checks if provider of type `java.security.Provider` is detected when given directly
	@Test
	public void providerDetectionTest2() {
		ProviderDetection providerDetection = new ProviderDetection();
		String sootClassPath = providerDetection.getSootClassPath();
		String mainClass = "tests.providerdetection.ProviderDetectionExample2"; 
		providerDetection.setupSoot(sootClassPath, mainClass);
		providerDetection.analyze();
		
		String expected = "BC";
		String actual = providerDetection.provider;
		assertEquals(expected, actual);
	}
	

	//checks if rules are correctly extracted, when provider is of type `java.security.Provider`,
	//is given as a variable, and the rules for that provider exist
	@Test
	public void providerDetectionTest3() {
		ProviderDetection providerDetection = new ProviderDetection();
		String sootClassPath = providerDetection.getSootClassPath();
		String mainClass = "tests.providerdetection.ProviderDetectionExample1"; 
		providerDetection.setupSoot(sootClassPath, mainClass);
		providerDetection.analyze();
		
		int expected = 2;
		int actual = providerDetection.rules.size();
		assertEquals(expected, actual);
	}
	
	//checks if rules are correctly extracted, when provider is of type `java.security.Provider`,
	//is given directly, and the rules for that provider exist
	@Test
	public void providerDetectionTest4() {
		ProviderDetection providerDetection = new ProviderDetection();
		String sootClassPath = providerDetection.getSootClassPath();
		String mainClass = "tests.providerdetection.ProviderDetectionExample2"; 
		providerDetection.setupSoot(sootClassPath, mainClass);
		providerDetection.analyze();
		
		int expected = 2;
		int actual = providerDetection.rules.size();
		assertEquals(expected, actual);
	}
	
	//checks if rules are correctly extracted, when provider is of type `java.security.Provider`,
	//is given as a variable, and the rules for that provider do not exist => so it takes Default rules
	@Test
	public void providerDetectionTest5() {
		ProviderDetection providerDetection = new ProviderDetection();
		String sootClassPath = providerDetection.getSootClassPath();
		String mainClass = "tests.providerdetection.ProviderDetectionExample3"; 
		providerDetection.setupSoot(sootClassPath, mainClass);
		providerDetection.analyze();
		
		int expected = 44;
		int actual = providerDetection.rules.size();
		assertEquals(expected, actual);
	}
	
	//checks if rules are correctly extracted, when provider is of type `java.security.Provider`,
	//is given directly, and the rules for that provider do not exist => so it takes Default rules
	@Test
	public void providerDetectionTest6() {
		ProviderDetection providerDetection = new ProviderDetection();
		String sootClassPath = providerDetection.getSootClassPath();
		String mainClass = "tests.providerdetection.ProviderDetectionExample4"; 
		providerDetection.setupSoot(sootClassPath, mainClass);
		providerDetection.analyze();
		
		int expected = 44;
		int actual = providerDetection.rules.size();
		assertEquals(expected, actual);
	}
	
	//checks if provider of type `java.lang.String` is detected when given as a variable
	@Test
	public void providerDetectionTest7() {
		ProviderDetection providerDetection = new ProviderDetection();
		String sootClassPath = providerDetection.getSootClassPath();
		String mainClass = "tests.providerdetection.ProviderDetectionExample5"; 
		providerDetection.setupSoot(sootClassPath, mainClass);
		providerDetection.analyze();
		
		String expected = "BC";
		String actual = providerDetection.provider;
		assertEquals(expected, actual);
	}
	
	//checks if provider of type `java.lang.String` is detected when given directly
	@Test
	public void providerDetectionTest8() {
		ProviderDetection providerDetection = new ProviderDetection();
		String sootClassPath = providerDetection.getSootClassPath();
		String mainClass = "tests.providerdetection.ProviderDetectionExample6"; 
		providerDetection.setupSoot(sootClassPath, mainClass);
		providerDetection.analyze();
		
		String expected = "BC";
		String actual = providerDetection.provider;
		assertEquals(expected, actual);
	}
	
	//checks if rules are correctly extracted, when provider is of type `java.lang.String`,
	//is given as a variable, and the rules for that provider exist
	@Test
	public void providerDetectionTest9() {
		ProviderDetection providerDetection = new ProviderDetection();
		String sootClassPath = providerDetection.getSootClassPath();
		String mainClass = "tests.providerdetection.ProviderDetectionExample5"; 
		providerDetection.setupSoot(sootClassPath, mainClass);
		providerDetection.analyze();
		
		int expected = 2;
		int actual = providerDetection.rules.size();
		assertEquals(expected, actual);
	}
	
	//checks if rules are correctly extracted, when provider is of type `java.lang.String`,
	//is given directly, and the rules for that provider exist
	@Test
	public void providerDetectionTest10() {
		ProviderDetection providerDetection = new ProviderDetection();
		String sootClassPath = providerDetection.getSootClassPath();
		String mainClass = "tests.providerdetection.ProviderDetectionExample6"; 
		providerDetection.setupSoot(sootClassPath, mainClass);
		providerDetection.analyze();
		
		int expected = 2;
		int actual = providerDetection.rules.size();
		assertEquals(expected, actual);
	}
	
	//checks if rules are correctly extracted, when provider is of type `java.lang.String`,
	//is given as a variable, and the rules for that provider do not exist => so it takes Default rules
	@Test
	public void providerDetectionTest11() {
		ProviderDetection providerDetection = new ProviderDetection();
		String sootClassPath = providerDetection.getSootClassPath();
		String mainClass = "tests.providerdetection.ProviderDetectionExample7"; 
		providerDetection.setupSoot(sootClassPath, mainClass);
		providerDetection.analyze();
		
		int expected = 44;
		int actual = providerDetection.rules.size();
		assertEquals(expected, actual);
	}
	
	//checks if rules are correctly extracted, when provider is of type `java.lang.String`,
	//is given directly, and the rules for that provider do not exist => so it takes Default rules
	@Test
	public void providerDetectionTest12() {
		ProviderDetection providerDetection = new ProviderDetection();
		String sootClassPath = providerDetection.getSootClassPath();
		String mainClass = "tests.providerdetection.ProviderDetectionExample8"; 
		providerDetection.setupSoot(sootClassPath, mainClass);
		providerDetection.analyze();
		
		int expected = 44;
		int actual = providerDetection.rules.size();
		assertEquals(expected, actual);
	}
	
}
