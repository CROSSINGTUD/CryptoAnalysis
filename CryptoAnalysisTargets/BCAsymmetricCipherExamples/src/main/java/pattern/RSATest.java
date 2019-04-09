package pattern;

import java.math.BigInteger;

import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.util.encoders.Hex;

public class RSATest {
	
	String edgeInput = "ff6f77206973207468652074696d6520666f7220616c6c20676f6f64206d656e";
	BigInteger mod = new BigInteger("b259d2d6e627a768c94be36164c2d9fc79d97aab9253140e5bf17751197731d6f7540d2509e7b9ffee0a70a6e26d56e92d2edd7f85aba85600b69089f35f6bdbf3c298e05842535d9f064e6b0391cb7d306e0a2d20c4dfb4e7b49a9640bdea26c10ad69c3f05007ce2513cee44cfe01998e62b6c3637d3fc0391079b26ee36d5", 16);
    BigInteger pubExp = new BigInteger("11", 16);
    
    RSAKeyParameters pubParameters = new RSAKeyParameters(true, mod, pubExp);
	
	public void testRSAEngineOne() {
		
		RSAEngine eng = new RSAEngine(); // works
//		AsymmetricBlockCipher eng = new RSAEngine(); // doesn't work
        
		eng.init(true, pubParameters);
        
        byte[] data = Hex.decode(edgeInput);
        data = eng.processBlock(data, 0, data.length);  
	}
	
	public void testRSAEngineTwo(int count) {
		RSAEngine engine = new RSAEngine();
		
		engine.init(true, pubParameters);
		long start = System.nanoTime();
		for (int i = 0; i < count; ++i) {
			byte[] message = Hex.decode(edgeInput);
			byte[] signed = engine.processBlock(message, 0, message.length);
		}
	}
	
	public static void main(String...args) {
		RSATest rsaTest = new RSATest();
		rsaTest.testRSAEngineOne();
		rsaTest.testRSAEngineTwo(1);
	}
}
