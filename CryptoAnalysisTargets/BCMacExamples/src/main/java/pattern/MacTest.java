package pattern;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.encoders.Hex;

public class MacTest {
	
	public void testMac1() {
		
		byte[] keyBytes = Hex.decode("0123456789abcdef");
		byte[] input1 = Hex.decode("37363534333231204e6f77206973207468652074696d6520666f7220");
		KeyParameter key = new KeyParameter(keyBytes);
		BlockCipher cipher = new DESEngine();
        
//		CBCBlockCipherMac mac = new CBCBlockCipherMac(cipher); //works
		Mac mac = new CBCBlockCipherMac(cipher); //doesn't work
		
		mac.init(key);
        
		mac.update(input1, 0, input1.length);
        
		byte[]  out = new byte[4];
        mac.doFinal(out, 0);
	}
	
	public void testMac2() {
		
		byte[] keyBytes = Hex.decode("0123456789abcdef");
		byte[] input2 = Hex.decode("3736353433323120");
		byte[]   ivBytes = Hex.decode("1234567890abcdef");
		KeyParameter key = new KeyParameter(keyBytes);
		BlockCipher cipher = new DESEngine();
		PKCS7Padding padding = new PKCS7Padding();
		
		CBCBlockCipherMac mac = new CBCBlockCipherMac(cipher, padding);
		
		ParametersWithIV param = new ParametersWithIV(key, ivBytes);
        mac.init(param);
        
        mac.update(input2, 0, input2.length);
        
        //missing doFinal call
	}
}
