package animamea;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.paddings.ISO7816d4Padding;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Hex;

@SuppressWarnings("deprecation")
public class AmAESCrypto {
	
	private KeyParameter keyP = new KeyParameter(Hex.decode("cb41f1706cde09651203c2d0efbaddf847a0d315cb2e53ff8bac41da0002672e"));
	private byte[] sscBytes = Hex.decode("d3090c72");
	public static int blockSize = 16;

	public byte[] getMACOne(byte[] data) {

		byte[] n = new byte[sscBytes.length + data.length];
		System.arraycopy(sscBytes, 0, n, 0, sscBytes.length);
		System.arraycopy(data, 0, n, sscBytes.length, data.length);
		n = addPadding(n);

		BlockCipher cipher = new AESFastEngine();
		Mac mac = new CMac(cipher, 64);

		mac.init(keyP);
		mac.update(n, 0, n.length);
		byte[] out = new byte[mac.getMacSize()];

		mac.doFinal(out, 0);

		return out;
	}
	
	public byte[] getMACTwo(byte[] key, byte[] data) {
		BlockCipher cipher = new AESFastEngine();
		Mac mac = new CMac(cipher, 64); // TODO Padding der Daten
		KeyParameter keyP = new KeyParameter(key);
		mac.init(keyP);

		mac.update(data, 0, data.length);

		byte[] out = new byte[8];

		mac.doFinal(out, 0);

		return out;
	}
	
	public byte[] addPadding(byte[] data) {

		int len = data.length;
		int nLen = ((len / blockSize) + 1) * blockSize;
		byte[] n = new byte[nLen];
		System.arraycopy(data, 0, n, 0, data.length);
		new ISO7816d4Padding().addPadding(n, len);
		return n;
	}
}
