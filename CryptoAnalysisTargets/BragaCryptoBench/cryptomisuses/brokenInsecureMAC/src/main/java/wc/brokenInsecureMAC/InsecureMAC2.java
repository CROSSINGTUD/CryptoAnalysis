package wc.brokenInsecureMAC;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import javax.crypto.*;
import javax.crypto.spec.*;

public final class InsecureMAC2 {

	static Object[] macs = { "HMACSHA1", "HMACMD5" };

	public static void main(String[] args) {

		for (int i = 0; i < macs.length; i++) {
			try {
				KeyGenerator kg = KeyGenerator.getInstance(macs[i].toString(), "SunJCE");
				SecretKey sk = kg.generateKey();
				Mac mac = Mac.getInstance(macs[i].toString(), "SunJCE");
				mac.init(sk);
				String msg = "demo msg";
				mac.update(msg.getBytes());
				byte[] result = mac.doFinal();
				byte[] key2 = sk.getEncoded();
				SecretKeySpec ks = new SecretKeySpec(key2, macs[i].toString());
				Mac mac2 = Mac.getInstance(macs[i].toString(), "SunJCE");
				mac2.init(ks);
				mac2.update(msg.getBytes());
				byte[] result2 = mac2.doFinal();
			} catch (NoSuchAlgorithmException | InvalidKeyException | IllegalStateException
					| NoSuchProviderException e) {
			}
		}
	}
}
