package crypto.providerdetection;

public class ProviderDetectionTest {

	public static void main(String[] args) {

		ProviderDetection providerDetection = new ProviderDetection();
		String sootClassPath = providerDetection.getMainSootClassPath();
		String mainClass = "crypto.providerdetection.ProviderDetectionExample"; 
		providerDetection.setupSoot(sootClassPath, mainClass);
		providerDetection.analyze();
		System.out.println("The provider used is: "+providerDetection.getProvider());
		System.out.println("The rules directory is: "+providerDetection.getRulesDirectory());
	}

}
