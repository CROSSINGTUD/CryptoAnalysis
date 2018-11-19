package tests.pattern.tink;

import java.security.GeneralSecurityException;

import org.junit.Test;

import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.Mac;
import com.google.crypto.tink.mac.MacFactory;
import com.google.crypto.tink.mac.MacKeyTemplates;
import com.google.crypto.tink.proto.KeyTemplate;

import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class TestMAC extends UsagePatternTestingFramework {
	
	private static final String PLAIN_TEXT = "This is just a sample text"; 
	
	@Test
	public void generateNewHMACSHA256_128BitTag() throws GeneralSecurityException {
		KeyTemplate kt = MacKeyTemplates.HMAC_SHA256_128BITTAG;
		KeysetHandle ksh = KeysetHandle.generateNew(kt);
		Assertions.mustBeInAcceptingState(kt);
		Assertions.mustBeInAcceptingState(ksh);
	}

	@Test
	public void generateNewHMACSHA256_256BitTag() throws GeneralSecurityException {
		KeyTemplate kt = MacKeyTemplates.HMAC_SHA256_256BITTAG;
		KeysetHandle ksh = KeysetHandle.generateNew(kt);
		Assertions.mustBeInAcceptingState(kt);
		Assertions.mustBeInAcceptingState(ksh);
	}
	
	@Test
	public void testGenerateMAC() throws GeneralSecurityException {
		KeyTemplate kt = MacKeyTemplates.HMAC_SHA256_256BITTAG;
		KeysetHandle ksh = KeysetHandle.generateNew(kt);
		
		Mac mac = MacFactory.getPrimitive(ksh);
		
		final byte[] data = PLAIN_TEXT.getBytes();
		final byte[] tag = mac.computeMac(data);
		
		mac.verifyMac(tag, data);
		
		Assertions.hasEnsuredPredicate(tag);
		Assertions.hasEnsuredPredicate(data);
		Assertions.mustBeInAcceptingState(mac);
	}
	
//	@Test
//	public void testInvalidKeyTemplate() throws GeneralSecurityException {
//		KeyTemplate kt = null;
//		KeysetHandle ksh = KeysetHandle.generateNew(kt);
//		
//		Assertions.mustNotBeInAcceptingState(ksh);
//	}

}
