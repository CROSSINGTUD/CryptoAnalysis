package tests.pattern.tink;

import java.security.GeneralSecurityException;

import org.junit.Ignore;
import org.junit.Test;

import com.google.crypto.tink.HybridDecrypt;
import com.google.crypto.tink.HybridEncrypt;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadKeyTemplates;
import com.google.crypto.tink.hybrid.HybridDecryptFactory;
import com.google.crypto.tink.hybrid.HybridEncryptFactory;
import com.google.crypto.tink.hybrid.HybridKeyTemplates;
import com.google.crypto.tink.proto.EcPointFormat;
import com.google.crypto.tink.proto.EllipticCurveType;
import com.google.crypto.tink.proto.HashType;
import com.google.crypto.tink.proto.KeyTemplate;

import crypto.analysis.CrySLRulesetSelector.Ruleset;
import test.assertions.Assertions;

@Ignore
public class TestHybridEncryption extends TestTinkPrimitives {

	@Override
	protected Ruleset getRuleSet() {
		return Ruleset.Tink;
	}
	@Test
	public void generateNewECIES_P256_HKDF_HMAC_SHA256_AES128_GCMKeySet() throws GeneralSecurityException {
		KeyTemplate kt = HybridKeyTemplates.createEciesAeadHkdfKeyTemplate(EllipticCurveType.NIST_P256,
		          HashType.SHA256,
		          EcPointFormat.UNCOMPRESSED,
		          AeadKeyTemplates.AES128_GCM,
		          new byte[0]);
		          
		KeysetHandle ksh = KeysetHandle.generateNew(kt);
		Assertions.hasEnsuredPredicate(kt);
		Assertions.mustBeInAcceptingState(kt);
		Assertions.mustBeInAcceptingState(ksh);		
	}
	
	@Test
	public void generateNewECIES_P256_HKDF_HMAC_SHA256_AES128_CTR_HMAC_SHA256KeySet() throws GeneralSecurityException {
		KeyTemplate kt = HybridKeyTemplates.createEciesAeadHkdfKeyTemplate(
		          EllipticCurveType.NIST_P256,
		          HashType.SHA256,
		          EcPointFormat.UNCOMPRESSED,
		          AeadKeyTemplates.AES128_CTR_HMAC_SHA256,
		          new byte[0]);
		          
		KeysetHandle ksh = KeysetHandle.generateNew(kt);
		Assertions.hasEnsuredPredicate(kt);
		Assertions.mustBeInAcceptingState(kt);
		Assertions.mustBeInAcceptingState(ksh);		
	}
	
	@Test
	public void generateInvalidKey() {
		KeyTemplate kt = null;
		Assertions.notHasEnsuredPredicate(kt);
	}
	
	
	@Test
	public void encryptUsingECIES_P256_HKDF_HMAC_SHA256_AES128_CTR_HMAC_SHA256KeySet() throws GeneralSecurityException {
		KeyTemplate kt = HybridKeyTemplates.createEciesAeadHkdfKeyTemplate(
		          EllipticCurveType.NIST_P256,
		          HashType.SHA256,
		          EcPointFormat.UNCOMPRESSED,
		          AeadKeyTemplates.AES128_CTR_HMAC_SHA256,
		          new byte[0]);
		          
		KeysetHandle ksh = KeysetHandle.generateNew(kt);
		KeysetHandle publicKsh = ksh.getPublicKeysetHandle();
		
		HybridEncrypt cipher = HybridEncryptFactory.getPrimitive(publicKsh);
		
		byte[] cipherText = cipher.encrypt("just an hybrid encryption test".getBytes(), "".getBytes());
		
		Assertions.hasEnsuredPredicate(kt);
		Assertions.hasEnsuredPredicate(publicKsh);
		Assertions.hasEnsuredPredicate(cipher);
		Assertions.mustBeInAcceptingState(kt);
		Assertions.mustBeInAcceptingState(ksh);	
		Assertions.mustBeInAcceptingState(publicKsh);
	}
	
	@Test
	public void decryptUsingECIES_P256_HKDF_HMAC_SHA256_AES128_CTR_HMAC_SHA256KeySet() throws GeneralSecurityException {
		KeyTemplate kt = HybridKeyTemplates.createEciesAeadHkdfKeyTemplate(
		          EllipticCurveType.NIST_P256,
		          HashType.SHA256,
		          EcPointFormat.UNCOMPRESSED,
		          AeadKeyTemplates.AES128_CTR_HMAC_SHA256,
		          new byte[0]);
		          
		KeysetHandle ksh = KeysetHandle.generateNew(kt);
		
		HybridDecrypt cipher = HybridDecryptFactory.getPrimitive(ksh);
		
		byte[] cipherText = cipher.decrypt("mxvw d sodlq whaw iru whvwlqj".getBytes(), "".getBytes());
		
		Assertions.hasEnsuredPredicate(kt);
		Assertions.hasEnsuredPredicate(ksh);
		Assertions.hasEnsuredPredicate(cipher);
		Assertions.mustBeInAcceptingState(kt);
		Assertions.mustBeInAcceptingState(ksh);
	}
	

}
