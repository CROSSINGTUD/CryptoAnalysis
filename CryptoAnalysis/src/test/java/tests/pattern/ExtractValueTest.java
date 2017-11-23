package tests.pattern;

import java.security.GeneralSecurityException;

import javax.crypto.KeyGenerator;

import org.junit.Test;

import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class ExtractValueTest  extends UsagePatternTestingFramework{
	@Test
	public void testInterproceduralFlow() throws GeneralSecurityException {
		KeyGenerator keygen = KeyGenerator.getInstance(getAES());
		Assertions.extValue(0);
		keygen.init(0);
	}	
	
	@Test
	public void testIntraproceduralFlow() throws GeneralSecurityException {
		String aes = "AES";
		KeyGenerator keygen = KeyGenerator.getInstance(aes);
		Assertions.extValue(0);
		keygen.init(0);
	}
	@Test
	public void testInterproceduralFlowDirect() throws GeneralSecurityException {
		KeyGenerator keygen = KeyGenerator.getInstance(getAESReturn());
		Assertions.extValue(0);
		keygen.init(0);
	}

	private String getAESReturn() {
		return "AES";
	}
	private String getAES() {
		String var = "AES";
		return var;
	}
}
