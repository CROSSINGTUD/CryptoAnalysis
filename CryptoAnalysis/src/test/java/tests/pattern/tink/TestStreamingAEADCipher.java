package tests.pattern.tink;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.security.GeneralSecurityException;

import org.junit.Ignore;
import org.junit.Test;

import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.StreamingAead;
import com.google.crypto.tink.proto.HashType;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.streamingaead.StreamingAeadFactory;
import com.google.crypto.tink.streamingaead.StreamingAeadKeyTemplates;

import crypto.analysis.CrySLRulesetSelector.Ruleset;
import test.assertions.Assertions;

@Ignore
public class TestStreamingAEADCipher extends TestTinkPrimitives {

	@Override
	protected Ruleset getRuleSet() {
		return Ruleset.Tink;
	}
	@Test
	public void generateNewAES128_CTR_HMAC_SHA256_4KBKeySet() throws GeneralSecurityException {
		KeyTemplate kt = StreamingAeadKeyTemplates.createAesCtrHmacStreamingKeyTemplate(16, HashType.SHA256, 16, HashType.SHA256, 32, 4096);
		KeysetHandle ksh = KeysetHandle.generateNew(kt);
		Assertions.hasEnsuredPredicate(kt);
		Assertions.mustBeInAcceptingState(kt);
		Assertions.mustBeInAcceptingState(ksh);		
	}
	
	@Test
	public void generateNewAES256_CTR_HMAC_SHA256_4KBKeySet() throws GeneralSecurityException {
		KeyTemplate kt = StreamingAeadKeyTemplates.createAesCtrHmacStreamingKeyTemplate(32, HashType.SHA256, 32, HashType.SHA256, 32, 4096);
		KeysetHandle ksh = KeysetHandle.generateNew(kt);
		Assertions.hasEnsuredPredicate(kt);
		Assertions.mustBeInAcceptingState(kt);
		Assertions.mustBeInAcceptingState(ksh);		
	}
	
	@Test
	public void generateNewAES128_GCM_HKDF_4KBKeySet() throws GeneralSecurityException {
		KeyTemplate kt = StreamingAeadKeyTemplates.createAesGcmHkdfStreamingKeyTemplate(16, HashType.SHA256, 16, 4096);
		KeysetHandle ksh = KeysetHandle.generateNew(kt);
		Assertions.hasEnsuredPredicate(kt);
		Assertions.mustBeInAcceptingState(kt);
		Assertions.mustBeInAcceptingState(ksh);		
	}
	
	@Test
	public void generateNewAES256_GCM_HKDF_4KBKeySet() throws GeneralSecurityException {
		KeyTemplate kt = StreamingAeadKeyTemplates.createAesGcmHkdfStreamingKeyTemplate(32, HashType.SHA256, 32, 4096);
		KeysetHandle ksh = KeysetHandle.generateNew(kt);
		Assertions.hasEnsuredPredicate(kt);
		Assertions.mustBeInAcceptingState(kt);
		Assertions.mustBeInAcceptingState(ksh);		
	}
	
		
	@Test
	public void generateNewInvalidKeySet() throws GeneralSecurityException {
		KeyTemplate kt = null;
		Assertions.notHasEnsuredPredicate(kt);
		Assertions.mustNotBeInAcceptingState(kt);
	}
	
	
	@Test
	public void encryptUsingAES128_CTR_HMAC_SHA256_4KB() throws GeneralSecurityException {
		KeyTemplate kt = StreamingAeadKeyTemplates.createAesCtrHmacStreamingKeyTemplate(16, HashType.SHA256, 16, HashType.SHA256, 32, 4096);
		KeysetHandle ksh = KeysetHandle.generateNew(kt);
		
		Assertions.hasEnsuredPredicate(kt); 
		try(FileChannel destination = new FileOutputStream("file.tx").getChannel();)  {
			StreamingAead saead = StreamingAeadFactory.getPrimitive(ksh);
			WritableByteChannel out = saead.newEncryptingChannel(destination, "crysl".getBytes());
			Assertions.hasEnsuredPredicate(saead);
			Assertions.mustBeInAcceptingState(saead);
		}
		catch(IOException e) {
			e.printStackTrace();
		}				
  	}
}
