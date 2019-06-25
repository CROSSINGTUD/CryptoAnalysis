package example.staticinitializationvector;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class StaticInitializationVectorABICase2 {
    public static final String DEFAULT_INITIALIZATION = "abcde";
    private static char[] INITIALIZATION;
    private static char[] initialization;
    public void go(IvParameterSpec ivSpec) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        SecretKey key = keyGen.generateKey();
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        cipher.init(Cipher.ENCRYPT_MODE,key,ivSpec);
    }
    private static void go2(){
        INITIALIZATION = DEFAULT_INITIALIZATION.toCharArray();
    }
    private static void go3(){
        initialization = INITIALIZATION;
    }

    public static void main (String [] args) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        StaticInitializationVectorABICase2 siv = new StaticInitializationVectorABICase2();
        go2();
        go3();
        IvParameterSpec ivSpec = new IvParameterSpec(new byte[]{Byte.parseByte(String.valueOf(initialization))});
        siv.go(ivSpec);
    }
}
