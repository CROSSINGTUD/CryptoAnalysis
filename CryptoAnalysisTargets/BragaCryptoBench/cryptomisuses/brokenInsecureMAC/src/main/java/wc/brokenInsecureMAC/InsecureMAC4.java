package wc.brokenInsecureMAC;

import javax.crypto.*;
import java.security.*;
import javax.crypto.spec.*;

public final class InsecureMAC4 {

	public static void main(String[] args) {

		try {
			KeyGenerator kg = KeyGenerator.getInstance("HMACSHA1", "SunJCE");
			SecretKey sk = kg.generateKey();
			Mac mac = Mac.getInstance("HMACSHA1", "SunJCE");
			mac.init(sk);
			String msg = "demo msg";
			mac.update(msg.getBytes());
			byte[] result = mac.doFinal();
			byte[] key2 = sk.getEncoded();
			SecretKeySpec ks = new SecretKeySpec(key2, "HMACMD5");
			Mac mac2 = Mac.getInstance("HMACMD5", "SunJCE");
			mac2.init(ks);
			mac2.update(msg.getBytes());
			byte[] result2 = mac2.doFinal();

		} catch (NoSuchAlgorithmException | InvalidKeyException | IllegalStateException | NoSuchProviderException e) {
		}
	}
}
