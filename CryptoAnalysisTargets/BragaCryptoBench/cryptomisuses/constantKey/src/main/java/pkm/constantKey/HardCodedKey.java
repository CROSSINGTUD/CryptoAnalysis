package pkm.constantKey;

import javax.crypto.spec.SecretKeySpec;

public class HardCodedKey {

    public static void main(String[] args) {
        String a = "Schluessel Part 1";
        String b = "Schslssuessssssel Part 2";
        b = a + b; //Teil1Teil2
        byte[] keyBytes = b.getBytes();
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
    }
}
