package cbc_aes_example;

import java.security.SecureRandom;
import java.util.Arrays;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class Main {

	public static void main(String...args) throws DataLengthException, InvalidCipherTextException {
		CBCAESBouncyCastle bc = new CBCAESBouncyCastle();
		bc.setKey(new byte[15]);
		bc.encrypt("test".getBytes());
		bc.decrypt("test".getBytes());
	}
 }