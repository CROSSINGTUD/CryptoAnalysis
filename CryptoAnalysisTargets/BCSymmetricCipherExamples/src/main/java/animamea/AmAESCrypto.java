package animamea;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.params.KeyParameter;

public class AmAESCrypto {
	
	public static int blockSize = 16;

	public byte[] decryptBlock(byte[] key, byte[] z) {
		byte[] s = new byte[blockSize];
		KeyParameter encKey = new KeyParameter(key);
		BlockCipher cipher = new AESFastEngine();
		cipher.init(false, encKey);
		cipher.processBlock(z, 0, s, 0);
		return s;
	}
	
	public byte[] encryptBlock(byte[] key, byte[] z) {
		byte[] s = new byte[blockSize];
		KeyParameter encKey = new KeyParameter(key);
		BlockCipher cipher = new AESFastEngine();
		cipher.init(true, encKey);
		cipher.processBlock(z, 0, s, 0);
		return s;
	}
}
