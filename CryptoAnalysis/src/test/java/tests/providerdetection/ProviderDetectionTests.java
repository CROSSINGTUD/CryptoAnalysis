package tests.providerdetection;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import crypto.providerdetection.ProviderDetection;

public class ProviderDetectionTests {
	
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
	
}
