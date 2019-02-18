package tests.pattern.bc;

import java.io.File;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class PaddingTest extends UsagePatternTestingFramework {

	private static final byte[] tData   = Hex.decode("355F697E8B868B65B25A04E18D782AFA");

	@Override
	protected String getSootClassPath() {
		// TODO Auto-generated method stub
		String sootCp = super.getSootClassPath();

		sootCp += File.pathSeparator + "/Users/rakshitkr/.m2/repository/org/bouncycastle/bcprov-jdk15on/1.60/bcprov-jdk15on-1.60.jar";

		return sootCp; 
	}

	@Test
	public void testPKCS7Padding() throws DataLengthException, IllegalStateException, InvalidCipherTextException {

		BlockCipher engine = new DESEngine();
		PKCS7Padding padding = new PKCS7Padding();
		PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(engine, padding);
		KeyParameter key = new KeyParameter(Hex.decode("0011223344556677"));
		
		byte[]  out = new byte[tData.length + 8];
        byte[]  dec = new byte[tData.length];
		
		cipher.init(true, key);
		int len = cipher.processBytes(tData, 0, tData.length, out, 0);
		len += cipher.doFinal(out, len);
		
		cipher.init(false, key);
		int decLen = cipher.processBytes(out, 0, len, dec, 0);
		decLen += cipher.doFinal(dec, decLen);
		
		Assertions.hasEnsuredPredicate(engine);
		Assertions.hasEnsuredPredicate(padding);
		Assertions.hasEnsuredPredicate(key);
		Assertions.hasEnsuredPredicate(dec);
		
		Assertions.mustBeInAcceptingState(cipher);
	}
}
