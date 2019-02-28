package tests.providerdetection;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import crypto.providerdetection.ProviderDetection;

public class ProviderDetectionTests {
	
	//checks if Provider extracted is BC, which in this case is
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
	
	//checks if Provider extracted is BC, which in this case is
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
	
	//tests if the rules are correctly extracted when provider is found, which in this case are correctly
	//extracted since the BC folder contains only 2 rules
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
	
	//tests if the rules are correctly extracted when provider is found, which in this case are correctly
	//extracted since the BC folder contains only 2 rules
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
	
	//tests if the rules are correctly extracted when provider is found, which in this case are the no
	//of rules from the Default folder, since the BC folder does not contain the Provider rule
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
	
	//tests if the rules are correctly extracted when provider is found, which in this case are the no
	//of rules from the Default folder, since the BC folder does not contain the Provider rule
	@Test
	public void providerDetectionTest6() {
		ProviderDetection providerDetection = new ProviderDetection();
		String sootClassPath = providerDetection.getSootClassPath();
		String mainClass = "tests.providerdetection.ProviderDetectionExample3"; 
		providerDetection.setupSoot(sootClassPath, mainClass);
		providerDetection.analyze();
		
		int expected = 44;
		int actual = providerDetection.rules.size();
		assertEquals(expected, actual);
	}
	
}
