package tests.pattern.tink;

import java.security.GeneralSecurityException;

import org.junit.Ignore;
import org.junit.Test;

import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.Mac;
import com.google.crypto.tink.mac.MacFactory;
import com.google.crypto.tink.mac.MacKeyTemplates;
import com.google.crypto.tink.proto.HashType;
import com.google.crypto.tink.proto.KeyTemplate;

import test.assertions.Assertions;

@Ignore
public class TestMAC extends TestTinkPrimitives {
	@Test
	public void generateNewHMACSHA256_128BitTag() throws GeneralSecurityException {
		KeyTemplate kt = MacKeyTemplates.createHmacKeyTemplate(32, 16, HashType.SHA256);
		KeysetHandle ksh = KeysetHandle.generateNew(kt);
		Assertions.mustBeInAcceptingState(kt);
		Assertions.mustBeInAcceptingState(ksh);
	}

	@Test
	public void generateNewHMACSHA256_256BitTag() throws GeneralSecurityException {
		KeyTemplate kt = MacKeyTemplates.createHmacKeyTemplate(32, 32, HashType.SHA256);
		KeysetHandle ksh = KeysetHandle.generateNew(kt);
		Assertions.mustBeInAcceptingState(kt);
		Assertions.mustBeInAcceptingState(ksh);
	}
	
	@Test
	public void testGenerateMAC() throws GeneralSecurityException {
		KeyTemplate kt = MacKeyTemplates.createHmacKeyTemplate(32, 16, HashType.SHA256);
		KeysetHandle ksh = KeysetHandle.generateNew(kt);
		
		Mac mac = MacFactory.getPrimitive(ksh);
		
		final byte[] data =  "This is just a sample text".getBytes(); 
		final byte[] tag = mac.computeMac(data);
		
		mac.verifyMac(tag, data);
		
		Assertions.mustBeInAcceptingState(mac);
	}
}
