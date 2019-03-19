package lastpass;

import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

public class Main {

	public static byte[] EncryptAes256(byte[] data, byte[] encryptionKey)
	{
		try {
			KeyParameter keyParam = new KeyParameter(encryptionKey);
			BlockCipherPadding padding = new PKCS7Padding();
			BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(
					new CBCBlockCipher(new AESEngine()), padding);
			cipher.reset();
			cipher.init(true, keyParam);
			byte[] buffer = new byte[cipher.getOutputSize(data.length)];
			int len = cipher.processBytes(data, 0, data.length, buffer, 0);
			len += cipher.doFinal(buffer, len);
			return Arrays.copyOfRange(buffer, 0, len);
		} catch (Exception e) {
			throw new RuntimeException("decrypt error in SimpleAesManaged", e);
		}
	}

	public String decrypt(byte[] encrypted, byte[] encryptionKey) {
		String plain;
		try
		{
			KeyParameter keyParam = new KeyParameter(encryptionKey);
			byte[] iv = Arrays.copyOfRange(encrypted, 0, 16);
			CipherParameters params = new ParametersWithIV(keyParam, iv);
			BlockCipherPadding padding = new PKCS7Padding();
			BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(
					new CBCBlockCipher(new AESEngine()), padding);
			cipher.reset();
			cipher.init(false, params);
			byte[] buffer = new byte[cipher.getOutputSize(encrypted.length)];
			int len = cipher.processBytes(encrypted, 0, encrypted.length, buffer, 0);
			len += cipher.doFinal(buffer, len);
			byte[] out = Arrays.copyOfRange(buffer, 0, len);
			plain = new String(out, "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException("decrypt error in SimpleAesManaged", e);
		}
		return plain;
	}
}
