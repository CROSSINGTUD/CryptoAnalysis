import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrueNegative {

    public void trueNegative() {
        byte[] pass = new byte[256];
        byte[] salt = new byte[256];

        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);
        secureRandom.nextBytes(pass);

        // convert byte array to char array
        char[] passwd = IntStream
                            .range(0, salt.length)
                            .map(i -> (char) (pass[i]&0xff))
                            .mapToObj(Character::toString)
                            .collect(Collectors.joining())
                            .toCharArray();

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
