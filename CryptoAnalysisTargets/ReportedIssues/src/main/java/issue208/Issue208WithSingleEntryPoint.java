package issue208;

import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;

public class Issue208WithSingleEntryPoint {

    private final SecureRandom secureRandom = new SecureRandom();

    private static final int IV_LENGTH = 32;

    private void encryptImpl() {
        byte[] iv = new byte[IV_LENGTH];
        secureRandom.nextBytes(iv);

        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
    }

    public static void main(String[] args) {
        // Method 'main' is the single entry point -> Instantiate SecureRandom seed and
        // use it in 'encryptImpl'
        Issue208WithSingleEntryPoint issue208 = new Issue208WithSingleEntryPoint();
        issue208.encryptImpl();
    }
}
