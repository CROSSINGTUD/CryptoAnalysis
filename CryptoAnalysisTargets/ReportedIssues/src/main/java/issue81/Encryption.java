package issue81;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {
      byte[] salt = {15, -12, 94, 0, 12, 3, -65, 73, -1, -84, -35};
    
      private SecretKey generateKey(String password) throws NoSuchAlgorithmException, GeneralSecurityException {
 		  PBEKeySpec pBEKeySpec = new PBEKeySpec(password.toCharArray(), salt, 10000, 256);

		  SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithSHA256");
		  byte[] keyMaterial = secretKeyFactory.generateSecret(pBEKeySpec).getEncoded();

		  SecretKey encryptionKey = new SecretKeySpec(keyMaterial, "AES");
		  //pBEKeySpec.clearPassword();
		  return encryptionKey;
      }
    
    private byte[] encrypt(byte[] plainText, SecretKey encryptionKey) throws GeneralSecurityException, NoSuchPaddingException {
          Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
          cipher.init(Cipher.ENCRYPT_MODE, encryptionKey);
          return cipher.doFinal(plainText);
      }
    
      public byte[] encryptData(byte[] plainText, String password) throws NoSuchAlgorithmException, GeneralSecurityException {
          return encrypt(plainText, generateKey(password));
      }
}