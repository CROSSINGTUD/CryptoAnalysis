package issue208;

import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;

public class Issue208WithMultipleEntryPoints {

    private final SecureRandom secureRandom = new SecureRandom();

    private static final int IV_LENGTH = 32;

    private void encryptImpl() {
        byte[] iv = new byte[IV_LENGTH];
        secureRandom.nextBytes(iv);

        // iv has to ensure 'randomized'
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
    }
}
