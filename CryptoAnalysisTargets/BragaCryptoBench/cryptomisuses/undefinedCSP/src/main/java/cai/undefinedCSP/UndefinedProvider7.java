package cai.undefinedCSP;

import javax.crypto.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.spec.*;
import javax.crypto.spec.PSource;
import org.bouncycastle.jce.provider.*;

public final class UndefinedProvider7 {

	public static void main(String args[])
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException,
			IllegalBlockSizeException, NoSuchProviderException, InvalidAlgorithmParameterException {

		Security.addProvider(new BouncyCastleProvider());

		int ksize = 512;
		int hsize = 160;
		int maxLenBytes = (ksize - 2 * hsize) / 8 - 2;

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(ksize);
		KeyPair kp = kpg.generateKeyPair();

		MGF1ParameterSpec mgf1ps = MGF1ParameterSpec.SHA1;
		OAEPParameterSpec OAEPps = new OAEPParameterSpec("SHA1", "MGF1", mgf1ps, PSource.PSpecified.DEFAULT);
		Cipher c = Cipher.getInstance("RSA");

		c.init(Cipher.ENCRYPT_MODE, kp.getPublic(), OAEPps);
		byte[] pt = "demo text".substring(0, maxLenBytes).getBytes();
		byte[] ct = c.doFinal(pt);

		c.init(Cipher.DECRYPT_MODE, kp.getPrivate(), OAEPps);
		byte[] pt2 = c.doFinal(ct);

	}

}
