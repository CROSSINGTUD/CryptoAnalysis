package pkm.constantKey;

import javax.crypto.spec.SecretKeySpec;

public class HardCodedKey {

	public static void main(String[] args) {
		String a = "Key Part 1";
		String b = "Key Part 2";
		b = a + b;
		byte[] keyBytes = b.getBytes();
		SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
	}
}
