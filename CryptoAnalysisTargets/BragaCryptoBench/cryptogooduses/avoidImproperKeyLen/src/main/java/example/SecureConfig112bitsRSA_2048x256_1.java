package example;

import javax.crypto.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.spec.*;
import javax.crypto.spec.PSource;
import org.bouncycastle.jce.provider.*;

public final class SecureConfig112bitsRSA_2048x256_1 {

	public static void main(String args[])
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException,
			IllegalBlockSizeException, NoSuchProviderException, InvalidAlgorithmParameterException {

		Security.addProvider(new BouncyCastleProvider());

		int keysize = 2048;
		int hashsize = 256;
		int maxLenBytes = (keysize - 2 * hashsize) / 8 - 2;

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");
		kpg.initialize(keysize);
		KeyPair kp = kpg.generateKeyPair();

		MGF1ParameterSpec mgf1ps = MGF1ParameterSpec.SHA256;
		OAEPParameterSpec OAEPps = new OAEPParameterSpec("SHA-256", "MGF1", mgf1ps, PSource.PSpecified.DEFAULT);
		Cipher c = Cipher.getInstance("RSA/None/OAEPPadding", "BC");

		Key pubk = kp.getPublic();
		c.init(Cipher.ENCRYPT_MODE, pubk, OAEPps);
		byte[] ptA = "This is a test string".substring(0, maxLenBytes).getBytes();
		byte[] ct = c.doFinal(ptA);

		Key privk = kp.getPrivate();
		c.init(Cipher.DECRYPT_MODE, privk, OAEPps);
		byte[] ptB = c.doFinal(ct);

	}

}
