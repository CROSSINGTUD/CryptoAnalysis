package example.insecureasymmetriccrypto;

import javax.crypto.*;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class InsecureAsymmetricCipherABICase2 {
    public static final String DEFAULT_KEY_SIZE = "1024";
    private static char[] KEY_SIZE;
    private static char[] keysize;
    public void go(KeyPairGenerator kgp, KeyPair kp) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException {

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, kp.getPublic());

        //encrypting
        String myMessage = new String("Secret Message");
        SealedObject encryptedMessage = new SealedObject(myMessage,cipher);

        //decrypting
        Cipher dec = Cipher.getInstance("RSA");
        dec.init(Cipher.DECRYPT_MODE, kp.getPrivate());

        String message = (String) encryptedMessage.getObject(dec);
        System.out.println(message);
    }
    private static void go2(){
        KEY_SIZE = DEFAULT_KEY_SIZE.toCharArray();
    }
    private static void go3(){
        keysize = KEY_SIZE;
    }

    public static void main (String [] args) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException {
        InsecureAsymmetricCipherABICase2 bc = new InsecureAsymmetricCipherABICase2();
        go2();
        go3();
        KeyPairGenerator kgp = KeyPairGenerator.getInstance("RSA");
        kgp.initialize(Integer.parseInt(String.valueOf(keysize)));
        KeyPair kp = kgp.generateKeyPair();
        bc.go(kgp,kp);
    }

}
