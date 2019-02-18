package tests.pattern.bc;

import java.io.File;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.AESLightEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class AESTest extends UsagePatternTestingFramework {

	private static final byte[] tData   = Hex.decode("355F697E8B868B65B25A04E18D782AFA");
	
	@Override
	protected String getSootClassPath() {
		// TODO Auto-generated method stub
		String sootCp = super.getSootClassPath();
		
		sootCp += File.pathSeparator + "/Users/rakshitkr/.m2/repository/org/bouncycastle/bcprov-jdk15on/1.60/bcprov-jdk15on-1.60.jar";

		return sootCp; 
	}
	
	@Test
	public void testAESEngine1() throws DataLengthException, IllegalStateException, InvalidCipherTextException {

		BlockCipher engine = new AESEngine();
		BlockCipher mode = new CBCBlockCipher(engine);
		BufferedBlockCipher cipher =  new BufferedBlockCipher(mode);
		KeyParameter kp = new KeyParameter(Hex.decode("5F060D3716B345C253F6749ABAC10917"));
		ParametersWithIV params = new ParametersWithIV(kp, new byte[16]);

		cipher.init(true, params);

		byte[] out = new byte[cipher.getOutputSize(tData.length)];

		int len = cipher.processBytes(tData, 0, tData.length, out, 0);

		len += cipher.doFinal(out, len);

		Assertions.hasEnsuredPredicate(engine);
		Assertions.hasEnsuredPredicate(mode);
		Assertions.hasEnsuredPredicate(kp);
		Assertions.hasEnsuredPredicate(params);

		Assertions.mustBeInAcceptingState(cipher);
	}

	@Test
	public void testAESEngine2() {

		BlockCipher engine = new AESEngine();
		BlockCipher mode = new CBCBlockCipher(engine);
		BufferedBlockCipher cipher =  new BufferedBlockCipher(mode);
		KeyParameter kp = new KeyParameter(Hex.decode("5F060D3716B345C253F6749ABAC10917"));
		ParametersWithIV params = new ParametersWithIV(kp, new byte[16]);

		cipher.init(true, params);

		byte[] out = new byte[cipher.getOutputSize(tData.length)];

		int len = cipher.processBytes(tData, 0, tData.length, out, 0);

		// no final method

		Assertions.hasEnsuredPredicate(engine);
		Assertions.hasEnsuredPredicate(mode);
		Assertions.hasEnsuredPredicate(params);

		Assertions.mustNotBeInAcceptingState(cipher);
	}

	@Test
	public void testAESLightEngine1() throws Exception {

		BlockCipher engine = new AESLightEngine();
		KeyParameter kp = new KeyParameter(Hex.decode("5F060D3716B345C253F6749ABAC10917"));

		engine.init(true, kp);

		Assertions.hasEnsuredPredicate(kp);
		Assertions.hasEnsuredPredicate(engine);
		
		Assertions.mustBeInAcceptingState(engine);
	}

	@Test
	public void testAESLightEngine2() throws Exception {

		BlockCipher engine = new AESLightEngine();
		
		//no init

		byte[] buf = new byte[128];

		engine.processBlock(buf, 0, buf, 0);

		Assertions.notHasEnsuredPredicate(engine);

		Assertions.mustNotBeInAcceptingState(engine);
	}

	@Test
	public void testAESLightEngine3() throws Exception {

		BlockCipher engine = new AESLightEngine();
		KeyParameter kp = new KeyParameter(Hex.decode("5F060D3716B345C253F6749ABAC10917"));

		engine.init(true, kp);

		byte[] correctBuf = new byte[engine.getBlockSize()];
		byte[] shortBuf = new byte[correctBuf.length / 2];

		engine.processBlock(shortBuf, 0, correctBuf, 0);	//failed short input check
		engine.processBlock(correctBuf, 0, shortBuf, 0);	//failed short output check
		engine.processBlock(correctBuf, 0, correctBuf, 0);

		Assertions.hasEnsuredPredicate(engine);
		Assertions.hasEnsuredPredicate(kp);

		Assertions.mustBeInAcceptingState(engine);		
	}

	@Test
	public void testAESLightEngine4() throws Exception {

		AESLightEngine engine = new AESLightEngine();
		byte[] dudKey = new byte[6];	//failed key length check
		KeyParameter kp = new KeyParameter(dudKey);
		engine.init(true, kp);

		Assertions.hasEnsuredPredicate(kp);
		Assertions.hasEnsuredPredicate(engine);

		Assertions.mustBeInAcceptingState(engine);
	}

	@Test
	public void testAESLightEngine5() throws Exception {

		BlockCipher engine = new AESLightEngine();
		byte[] iv = new byte[16];
		ParametersWithIV kp = new ParametersWithIV(null, iv);	//failed parameter check
		engine.init(true, kp);	

		Assertions.notHasEnsuredPredicate(kp);
		Assertions.notHasEnsuredPredicate(engine);

		Assertions.mustBeInAcceptingState(engine);
	}


	@Test
	public void testMonteCarloAndVector() throws Exception {

		BlockCipher engine = new AESLightEngine();
		BufferedBlockCipher cipher =  new BufferedBlockCipher(engine);
		KeyParameter kp = new KeyParameter(Hex.decode("5F060D3716B345C253F6749ABAC10917"));

		cipher.init(true, kp);

		byte[] out = new byte[cipher.getOutputSize(tData.length)];

		int len = cipher.processBytes(tData, 0, tData.length, out, 0);

		cipher.doFinal(out, len);

		Assertions.hasEnsuredPredicate(engine);
		Assertions.hasEnsuredPredicate(kp);

		Assertions.mustBeInAcceptingState(engine);
		Assertions.mustBeInAcceptingState(cipher);
	}
}
