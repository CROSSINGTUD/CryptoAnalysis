package wc.brokenInsecureMAC;

import javax.crypto.*;
import java.security.*;
import javax.crypto.spec.*;

public final class InsecureMAC1 {

	public static void main(String[] args) {

		try {
			KeyGenerator kg = KeyGenerator.getInstance("HMACMD5", "SunJCE");
			SecretKey sk = kg.generateKey();
			Mac mac = Mac.getInstance("HMACMD5", "SunJCE");
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

			kg = KeyGenerator.getInstance("HMACSHA1", "SunJCE");
			sk = kg.generateKey();
			mac = Mac.getInstance("HMACSHA1", "SunJCE");
			mac.init(sk);
			msg = "demo msg 2";
			mac.update(msg.getBytes());
			result = mac.doFinal();
			key2 = sk.getEncoded();
			ks = new SecretKeySpec(key2, "HMACSHA1");
			mac2 = Mac.getInstance("HMACSHA1", "SunJCE");
			mac2.init(ks);
			mac2.update(msg.getBytes());
			result2 = mac2.doFinal();

		} catch (NoSuchAlgorithmException | InvalidKeyException | IllegalStateException | NoSuchProviderException e) {
		}
	}
}
