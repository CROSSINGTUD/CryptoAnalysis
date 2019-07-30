package example.brokenhash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class BrokenHashABICase5 {
    public static final String DEFAULT_CRYPTO = "SHA1";
    private static char[] CRYPTO;
    private static char[] crypto;
    public static void main (String [] args) throws NoSuchAlgorithmException {
        String str = "abcdef";
        go2();
        go3();
        go(str);
    }

    private static void go2(){
        CRYPTO = DEFAULT_CRYPTO.toCharArray();
    }
    private static void go3(){
        crypto = CRYPTO;
    }

    public static void go (String str) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(String.valueOf(crypto));
        md.update(str.getBytes());
        System.out.println(md.digest());
    }
}
