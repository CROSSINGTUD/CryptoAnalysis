package tests.pattern;

import java.io.File;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
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

public class AESTest extends UsagePatternTestingFramework{

	private static final byte[] tData   = Hex.decode("AAFE47EE82411A2BF3F6752AE8D7831138F041560631B114F3F6752AE8D7831138F041560631B1145A01020304050607");
	
	
	@Override
	protected String getSootClassPath() {
		// TODO Auto-generated method stub
		String sootCp = super.getSootClassPath();
		
		sootCp += File.pathSeparator + "/Users/rakshitkr/.m2/repository/org/bouncycastle/bcprov-jdk15on/1.60/bcprov-jdk15on-1.60.jar";
		
		System.out.println(sootCp);
		return sootCp; 
	}

	@Test
	public void testNullCBC1() throws DataLengthException, IllegalStateException, InvalidCipherTextException {
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
        Assertions.hasEnsuredPredicate(params);
        Assertions.mustBeInAcceptingState(cipher);
	}

	@Test
	public void testNullCBC2() {
		BlockCipher engine = new AESEngine(); //BlockCipher or AESEngine?
		BlockCipher mode = new CBCBlockCipher(engine);
		Assertions.extValue(0);
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
	public void testAESLightEngine1() throws Exception { //modified based on CipherTest
		BlockCipher engine = new AESLightEngine();	//here the reference type is different from instantiation
		KeyParameter kp = new KeyParameter(Hex.decode("5F060D3716B345C253F6749ABAC10917"));
		byte[]      buf = new byte[128];
		engine.init(true, kp);
		engine.processBlock(buf, 0, buf, 0);
		Assertions.mustBeInAcceptingState(engine);
//		Assertions.hasEnsuredPredicate(buf);
//		Assertions.hasEnsuredPredicate(engine);
	}
	
	@Test
	public void testAESLightEngine2() throws Exception { //modified based on BlockCipherVectorTest & BlockCipherMonteCarloTest
		BlockCipher engine = new AESLightEngine();
//		BlockCipher mode = new CBCBlockCipher(engine);
		BufferedBlockCipher cipher =  new BufferedBlockCipher(engine);	//here the reference variable is same type as instantiation
		KeyParameter kp = new KeyParameter(Hex.decode("5F060D3716B345C253F6749ABAC10917"));
//		ParametersWithIV params = new ParametersWithIV(kp, new byte[16]);
		cipher.init(true, kp);
		byte[] out = new byte[cipher.getOutputSize(tData.length)];

        int len = cipher.processBytes(tData, 0, tData.length, out, 0);
        cipher.doFinal(out, len);
        Assertions.mustBeInAcceptingState(cipher);
        Assertions.mustBeInAcceptingState(engine);
		Assertions.hasEnsuredPredicate(engine);
//		Assertions.hasEnsuredPredicate(mode);
//		Assertions.hasEnsuredPredicate(out);
	}
	
	
	// to be moved to different test class
	@Test
	public void testCipher() throws GeneralSecurityException {
		Cipher c = Cipher.getInstance("AES/CBC/PKCS7Padding");
		c.init(1, (KeyGenerator.getInstance("AES")).generateKey());
		byte[] input = "jdsljflsd".getBytes();
		c.update(input);
		byte[] out = c.doFinal();
		Assertions.mustBeInAcceptingState(c);
//		Assertions.hasEnsuredPredicate(out);
	}
}
