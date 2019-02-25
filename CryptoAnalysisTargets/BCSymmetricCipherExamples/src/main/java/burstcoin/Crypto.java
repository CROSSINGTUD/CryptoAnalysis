package burstcoin;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

public class Crypto {
	
	public static byte[] aesDecrypt(byte[] ivCiphertext, byte[] myPrivateKey, byte[] theirPublicKey, byte[] nonce) {
		try {
			if (ivCiphertext.length < 16 || ivCiphertext.length % 16 != 0) {
				throw new InvalidCipherTextException("invalid ciphertext");
			}
			byte[] iv = Arrays.copyOfRange(ivCiphertext, 0, 16);
			byte[] ciphertext = Arrays.copyOfRange(ivCiphertext, 16, ivCiphertext.length);

			byte[] key = Hex.decode("0123456789abcdef");
			PaddedBufferedBlockCipher aes = new PaddedBufferedBlockCipher(new CBCBlockCipher(
					new AESEngine()));
			CipherParameters ivAndKey = new ParametersWithIV(new KeyParameter(key), iv);
			aes.init(false, ivAndKey);
			byte[] output = new byte[aes.getOutputSize(ciphertext.length)];
			int plaintextLength = aes.processBytes(ciphertext, 0, ciphertext.length, output, 0);
			plaintextLength += aes.doFinal(output, plaintextLength);
			byte[] result = new byte[plaintextLength];
			System.arraycopy(output, 0, result, 0, result.length);
			return result;
		} catch (InvalidCipherTextException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static byte[] aesEncrypt(byte[] plaintext, byte[] myPrivateKey, byte[] theirPublicKey, byte[] nonce) {
        try {
            byte[] key = Hex.decode("0123456789abcdef");
            byte[] iv = new byte[16];
            PaddedBufferedBlockCipher aes = new PaddedBufferedBlockCipher(new CBCBlockCipher(
                    new AESEngine()));
            CipherParameters ivAndKey = new ParametersWithIV(new KeyParameter(key), iv);
            aes.init(true, ivAndKey);
            byte[] output = new byte[aes.getOutputSize(plaintext.length)];
            int ciphertextLength = aes.processBytes(plaintext, 0, plaintext.length, output, 0);
            ciphertextLength += aes.doFinal(output, ciphertextLength);
            byte[] result = new byte[iv.length + ciphertextLength];
            System.arraycopy(iv, 0, result, 0, iv.length);
            System.arraycopy(output, 0, result, iv.length, ciphertextLength);
            return result;
        } catch (InvalidCipherTextException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}

