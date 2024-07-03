import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;

public class TruePositive {

    public void truePositive() {
        char[] passwd = {'t','h','i','s'};
        byte[] salt = new byte[256];

        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);

        byte[] key = getKey(passwd, salt, 10000, 256);
    }

    public static byte[] getKey(char[] pass, byte[] salt, int iterations, int size) {
        // generate a key via a PBEKeySpec
        try{
            PBEKeySpec spec = new PBEKeySpec(pass, salt, iterations, size);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] key = skf.generateSecret(spec).getEncoded();
            spec.clearPassword();
            return key;
        } catch (Exception e) {
        }
        return null;
    }
}
