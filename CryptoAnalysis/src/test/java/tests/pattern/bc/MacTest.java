package tests.pattern.bc;

import java.io.File;
import java.security.SecureRandom;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class MacTest extends UsagePatternTestingFramework {
	
	@Override
	protected String getSootClassPath() {
		// TODO Auto-generated method stub
		String sootCp = super.getSootClassPath();
		
		sootCp += File.pathSeparator + "/Users/rakshitkr/.m2/repository/org/bouncycastle/bcprov-jdk15on/1.60/bcprov-jdk15on-1.60.jar";
		
		return sootCp; 
	}
	
	@Test
	public void testMac1() {
		
		byte[] keyBytes = Hex.decode("0123456789abcdef");
		byte[] input1 = Hex.decode("37363534333231204e6f77206973207468652074696d6520666f7220");
		KeyParameter key = new KeyParameter(keyBytes);
		BlockCipher engine = new DESEngine();
        
		Mac mac = new CBCBlockCipherMac(engine);
		
		mac.init(key);
        
		mac.update(input1, 0, input1.length);
        
		byte[]  out = new byte[4];
        mac.doFinal(out, 0);
        
        Assertions.hasEnsuredPredicate(key);
        Assertions.hasEnsuredPredicate(engine);
        Assertions.hasEnsuredPredicate(mac);
        
        Assertions.mustBeInAcceptingState(mac);
	}
	
	@Test
	public void testMac2() {
		
		byte[] keyBytes = Hex.decode("0123456789abcdef");
		byte[] input2 = Hex.decode("3736353433323120");
		byte[]   ivBytes = Hex.decode("1234567890abcdef");
		KeyParameter key = new KeyParameter(keyBytes);
		BlockCipher engine = new DESEngine();
		PKCS7Padding padding = new PKCS7Padding();
		
		CBCBlockCipherMac mac = new CBCBlockCipherMac(engine, padding);
		
		ParametersWithIV param = new ParametersWithIV(key, ivBytes);
        mac.init(param);
        
        mac.update(input2, 0, input2.length);
        
        //missing doFinal call
        
        Assertions.hasEnsuredPredicate(engine);
        Assertions.hasEnsuredPredicate(key);
        Assertions.hasEnsuredPredicate(padding);
        Assertions.hasEnsuredPredicate(param);
        Assertions.notHasEnsuredPredicate(mac);
        
        Assertions.mustNotBeInAcceptingState(mac);
	}
}
