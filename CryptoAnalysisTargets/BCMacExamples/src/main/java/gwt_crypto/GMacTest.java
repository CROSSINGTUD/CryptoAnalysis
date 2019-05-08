package gwt_crypto;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.macs.GMac;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.encoders.Hex;

public class GMacTest {

	public void performTestOne()
	{
		byte[] key = Hex.decode("11754cd72aec309bf52f7687212e8957");
		byte[] iv = Hex.decode("3c819d9a9bed087615030b65");
		byte[] tag = Hex.decode("250327c674aaf477aef2675748cf6971");
		Mac mac = new GMac(new GCMBlockCipher(new AESFastEngine()), tag.length * 8);
		CipherParameters param = new KeyParameter(key);
		mac.init(new ParametersWithIV(param, iv));
	}
}
