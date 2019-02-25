package pattern;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.AESLightEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.encoders.Hex;

public class AESTest {

	private static final byte[] tData   = Hex.decode("355F697E8B868B65B25A04E18D782AFA");

	public void testAESEngineDefault() throws DataLengthException, IllegalStateException, InvalidCipherTextException {

		BlockCipher engine = new AESEngine();
		BlockCipher mode = new CBCBlockCipher(engine);
		BufferedBlockCipher cipher =  new BufferedBlockCipher(mode);
		KeyParameter kp = new KeyParameter(Hex.decode("5F060D3716B345C253F6749ABAC10917"));
		ParametersWithIV params = new ParametersWithIV(kp, new byte[16]);

		cipher.init(true, params);

		byte[] out = new byte[cipher.getOutputSize(tData.length)];

		int len = cipher.processBytes(tData, 0, tData.length, out, 0);

		len += cipher.doFinal(out, len);

	}

	public void testAESEngineWithoutFinal() {

		BlockCipher engine = new AESEngine();
		BlockCipher mode = new CBCBlockCipher(engine);
		BufferedBlockCipher cipher =  new BufferedBlockCipher(mode);
		KeyParameter kp = new KeyParameter(Hex.decode("5F060D3716B345C253F6749ABAC10917"));
		ParametersWithIV params = new ParametersWithIV(kp, new byte[16]);

		cipher.init(true, params);

		byte[] out = new byte[cipher.getOutputSize(tData.length)];

		int len = cipher.processBytes(tData, 0, tData.length, out, 0);

		// no final method
	}

	public void testAESLightEngine1() throws Exception {

		BlockCipher engine = new AESLightEngine();
		KeyParameter kp = new KeyParameter(Hex.decode("5F060D3716B345C253F6749ABAC10917"));

		engine.init(true, kp);
	}

	public void testAESLightEngine2() throws Exception {

		AESLightEngine engine = new AESLightEngine(); //works
		//		BlockCipher engine = new AESLightEngine(); //doesn't work

		//no init

		byte[] buf = new byte[128];

		engine.processBlock(buf, 0, buf, 0);
	}

	public void testAESLightEngineUsage1() throws Exception {

		BlockCipher engine = new AESLightEngine();
		KeyParameter kp = new KeyParameter(Hex.decode("5F060D3716B345C253F6749ABAC10917"));

		engine.init(true, kp);

		byte[] correctBuf = new byte[engine.getBlockSize()];
		byte[] shortBuf = new byte[correctBuf.length / 2];

		engine.processBlock(shortBuf, 0, correctBuf, 0);	//failed short input check
		engine.processBlock(correctBuf, 0, shortBuf, 0);	//failed short output check
		engine.processBlock(correctBuf, 0, correctBuf, 0);		
	}

	public void testAESLightEngineUsage2() throws Exception {

		BlockCipher engine = new AESLightEngine();
		byte[] dudKey = new byte[6];	//failed key length check
		KeyParameter kp = new KeyParameter(dudKey);
		engine.init(true, kp);
	}

	public void testAESLightEngineWithIV() throws Exception {

		BlockCipher engine = new AESLightEngine();
		byte[] iv = new byte[16];
		ParametersWithIV kp = new ParametersWithIV(null, iv);	//failed parameter check
		engine.init(true, kp);	
	}

	public void testBufferedBlockCipherWithAESLightEngine() throws Exception {

		BlockCipher engine = new AESLightEngine();
		BufferedBlockCipher cipher =  new BufferedBlockCipher(engine);
		KeyParameter kp = new KeyParameter(Hex.decode("5F060D3716B345C253F6749ABAC10917"));

		cipher.init(true, kp);

		byte[] out = new byte[cipher.getOutputSize(tData.length)];

		int len = cipher.processBytes(tData, 0, tData.length, out, 0);

		cipher.doFinal(out, len);
	}

	public void testAESEngineWithPadding() throws DataLengthException, IllegalStateException, InvalidCipherTextException {
		AESEngine engine = new AESEngine();
		CBCBlockCipher mode = new CBCBlockCipher(engine);
		PKCS7Padding padding = new PKCS7Padding();
		PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(mode, padding);
		KeyParameter key = new KeyParameter(Hex.decode("0011223344556677"));
		
		cipher.init(true, key);
		
		byte[]  out = new byte[tData.length + 8];
		byte[]  dec = new byte[tData.length];
		
		int len = cipher.processBytes(tData, 0, tData.length, out, 0);

		len += cipher.doFinal(out, len);

		cipher.init(false, key);

		int decLen = cipher.processBytes(out, 0, len, dec, 0);

		decLen += cipher.doFinal(dec, decLen);
	}
}
