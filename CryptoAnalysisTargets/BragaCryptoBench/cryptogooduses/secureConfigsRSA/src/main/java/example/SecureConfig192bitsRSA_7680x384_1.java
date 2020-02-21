package example;

import javax.crypto.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.spec.*;
import javax.crypto.spec.PSource;
import org.bouncycastle.jce.provider.*;

public final class SecureConfig192bitsRSA_7680x384_1 {

	public static void main(String args[])
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException,
			IllegalBlockSizeException, NoSuchProviderException, InvalidAlgorithmParameterException {

		Security.addProvider(new BouncyCastleProvider());

		int ksize = 7680;
		int hsize = 384;
		int maxLenBytes = (ksize - 2 * hsize) / 8 - 2;

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");
		kpg.initialize(ksize);
		KeyPair kp = kpg.generateKeyPair();

		MGF1ParameterSpec mgf1ps = MGF1ParameterSpec.SHA384;
		OAEPParameterSpec OAEPps = new OAEPParameterSpec("SHA-384", "MGF1", mgf1ps, PSource.PSpecified.DEFAULT);
		Cipher c = Cipher.getInstance("RSA/None/OAEPPadding", "BC");

		Key pubk = kp.getPublic();
		c.init(Cipher.ENCRYPT_MODE, pubk, OAEPps);
		byte[] pt1 = "demo text".substring(0, maxLenBytes).getBytes();
		byte[] ct = c.doFinal(pt1);

		Key privk = kp.getPrivate();
		c.init(Cipher.DECRYPT_MODE, privk, OAEPps);
		byte[] pt2 = c.doFinal(ct);

	}

}
