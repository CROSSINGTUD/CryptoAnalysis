package tests.pattern;

import java.security.GeneralSecurityException;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.PBEKeySpec;

import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class ExtractValueTest  extends UsagePatternTestingFramework{

	@Override
	protected Ruleset getRuleSet() {
		return Ruleset.JavaCryptographicArchitecture;
	}
	@Test
	public void testInterproceduralStringFlow() throws GeneralSecurityException {
		KeyGenerator keygen = KeyGenerator.getInstance(getAES());
		Assertions.extValue(0);
		keygen.init(0);
	}	
	
	@Test
	public void charArrayExtractionTest(){
		char[] v = new char[] {'p'};
		final PBEKeySpec pbekeyspec = new PBEKeySpec(v, null, 65000, 128);
		Assertions.extValue(0);
	} 
	@Test
	public void testIntraproceduralStringFlow() throws GeneralSecurityException {
		String aes = "AES";
		KeyGenerator keygen = KeyGenerator.getInstance(aes);
		Assertions.extValue(0);
		keygen.init(0);
	}
	@Test
	public void testInterproceduralStringFlowDirect() throws GeneralSecurityException {
		KeyGenerator keygen = KeyGenerator.getInstance(getAESReturn());
		Assertions.extValue(0);
		keygen.init(0);
	}

	@Test
	public void testIntraproceduralIntFlowDirect() throws GeneralSecurityException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		int val = 0;
		keygen.init(val);
		Assertions.extValue(0);
	}
	

	@Test
	public void testIntraproceduralNativeNoCalleeIntFlow() throws GeneralSecurityException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		int val = noCallee();
		keygen.init(val);
		Assertions.extValue(0);
	}
	private String getAESReturn() {
		int x = 222;
		return "AES";
	}
	private String getAES() {
		String var = "AES";
		return var;
	}
	private static native int noCallee();
}
