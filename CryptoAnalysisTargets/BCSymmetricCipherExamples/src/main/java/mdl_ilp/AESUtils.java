package mdl_ilp;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.params.KeyParameter;

public class AESUtils {
	/**
	 * AES [FIPS 197] SHALL be used in CMAC-mode [SP 800-38B] with a MAC length of 8 bytes.
	 *
	 * @param data the data to MAC
	 * @param key the key to use
	 * @return the 8 byte MAC of the data
	 */
	public static byte[] performCBC8(byte[] data, byte[] key) {

	    // mac size in bits (64 bits = 8 bytes)
	    final Mac cbc8 = new CMac(new AESEngine(), 64);
	    CipherParameters params = new KeyParameter(key);
	    cbc8.init(params);

	    byte[] result = new byte[8];
	    cbc8.update(data, 0, data.length);
	    cbc8.doFinal(result, 0);

	    return result;
	}
}
