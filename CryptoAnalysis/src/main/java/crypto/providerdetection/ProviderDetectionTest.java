package crypto.providerdetection;

public class ProviderDetectionTest {

	public static void main(String[] args) {

		ProviderDetection providerDetection = new ProviderDetection();
		String sootClassPath = providerDetection.getSootClassPath();
		String mainClass = "crypto.providerdetection.ProviderDetectionExample"; 
		providerDetection.setupSoot(sootClassPath, mainClass);
		providerDetection.analyze();
		System.out.println("The provider used is: "+providerDetection.provider);
		System.out.println(providerDetection.sootClassPath);
//		assert providerDetection.provider == "BC";
	}

}
