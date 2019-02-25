package example.misuse;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Hex;

public class IncompleteOperationErrorExample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BlockCipher engine = new AESEngine();
		BlockCipher mode = new CBCBlockCipher(engine);
		BufferedBlockCipher cipher = new BufferedBlockCipher(mode);

		byte[] key = Hex.decode("5F060D3716B345C253F6749ABAC10917");

		// here the cipher is properly initialized but never used for encryption or decryption 
		cipher.init(true, new KeyParameter(key));
	}

}
