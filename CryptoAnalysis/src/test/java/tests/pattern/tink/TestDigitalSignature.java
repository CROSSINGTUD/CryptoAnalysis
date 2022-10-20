package tests.pattern.tink;

import java.security.GeneralSecurityException;

import org.junit.Ignore;
import org.junit.Test;

import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.PublicKeySign;
import com.google.crypto.tink.PublicKeyVerify;
import com.google.crypto.tink.proto.EcdsaSignatureEncoding;
import com.google.crypto.tink.proto.EllipticCurveType;
import com.google.crypto.tink.proto.HashType;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.signature.PublicKeySignFactory;
import com.google.crypto.tink.signature.PublicKeyVerifyFactory;
import com.google.crypto.tink.signature.SignatureKeyTemplates;

import crypto.analysis.CrySLRulesetSelector.Ruleset;
import test.assertions.Assertions;

@SuppressWarnings("deprecation")
@Ignore
public class TestDigitalSignature extends TestTinkPrimitives {

	@Override
	protected Ruleset getRuleSet() {
		return Ruleset.Tink;
	}
	@Test
	public void generateNewECDSA_P256() throws GeneralSecurityException {
		KeyTemplate kt = SignatureKeyTemplates.createEcdsaKeyTemplate(HashType.SHA256, EllipticCurveType.NIST_P256, EcdsaSignatureEncoding.DER, null);
		KeysetHandle ksh = KeysetHandle.generateNew(kt);
		Assertions.mustBeInAcceptingState(kt);
		Assertions.mustBeInAcceptingState(ksh);
	}
	
	@Test
	public void generateNewECDSA_P384() throws GeneralSecurityException {
		KeyTemplate kt = SignatureKeyTemplates.createEcdsaKeyTemplate(HashType.SHA512, EllipticCurveType.NIST_P384, EcdsaSignatureEncoding.DER, null);
		KeysetHandle ksh = KeysetHandle.generateNew(kt);
		Assertions.mustBeInAcceptingState(kt);
		Assertions.mustBeInAcceptingState(ksh);
	}
	
	@Test
	public void generateNewECDSA_P521() throws GeneralSecurityException {
		KeyTemplate kt = SignatureKeyTemplates.createEcdsaKeyTemplate(HashType.SHA512, EllipticCurveType.NIST_P521, EcdsaSignatureEncoding.DER, null);
		KeysetHandle ksh = KeysetHandle.generateNew(kt);
		Assertions.mustBeInAcceptingState(kt);
		Assertions.mustBeInAcceptingState(ksh);
	}
	
	@Test
	public void generateNewECDSA_P256_IEEE_P1363() throws GeneralSecurityException {
		KeyTemplate kt = SignatureKeyTemplates.createEcdsaKeyTemplate(HashType.SHA256, EllipticCurveType.NIST_P256, EcdsaSignatureEncoding.IEEE_P1363, null);
		KeysetHandle ksh = KeysetHandle.generateNew(kt);
		Assertions.mustBeInAcceptingState(kt);
		Assertions.mustBeInAcceptingState(ksh);
	}
	
	@Test
	public void generateNewECDSA_P384_IEEE_P1363() throws GeneralSecurityException {
		KeyTemplate kt = SignatureKeyTemplates.createEcdsaKeyTemplate(HashType.SHA512, EllipticCurveType.NIST_P384, EcdsaSignatureEncoding.IEEE_P1363, null);
		KeysetHandle ksh = KeysetHandle.generateNew(kt);
		Assertions.mustBeInAcceptingState(kt);
		Assertions.mustBeInAcceptingState(ksh);
	}
	
	@Test
	public void generateNewECDSA_P521_IEEE_P1363() throws GeneralSecurityException {
		KeyTemplate kt = SignatureKeyTemplates.createEcdsaKeyTemplate(HashType.SHA512, EllipticCurveType.NIST_P521, EcdsaSignatureEncoding.IEEE_P1363, null);
		KeysetHandle ksh = KeysetHandle.generateNew(kt);
		Assertions.mustBeInAcceptingState(kt);
		Assertions.mustBeInAcceptingState(ksh);
	}
	
	@Test
	public void signUsingECDSA_P256() throws GeneralSecurityException {
		KeyTemplate kt = SignatureKeyTemplates.createEcdsaKeyTemplate(HashType.SHA256, EllipticCurveType.NIST_P256, EcdsaSignatureEncoding.DER, null);;
		KeysetHandle ksh = KeysetHandle.generateNew(kt);
		
		Assertions.mustBeInAcceptingState(kt);
		Assertions.mustBeInAcceptingState(ksh);
		
		@SuppressWarnings("deprecation")
		PublicKeySign pks = PublicKeySignFactory.getPrimitive(ksh);
		
		pks.sign("this is just a test using digital signatures using Google Tink".getBytes());
		
		Assertions.hasEnsuredPredicate(pks);
		Assertions.mustBeInAcceptingState(pks);
	}
	
	@Test
	public void signAndVerifyUsingECDSA_P256() throws GeneralSecurityException {
		KeyTemplate kt = SignatureKeyTemplates.createEcdsaKeyTemplate(HashType.SHA256, EllipticCurveType.NIST_P256, EcdsaSignatureEncoding.DER, null);;
		KeysetHandle ksh = KeysetHandle.generateNew(kt);
		
		Assertions.mustBeInAcceptingState(kt);
		Assertions.mustBeInAcceptingState(ksh);
		
		@SuppressWarnings("deprecation")
		PublicKeySign pks = PublicKeySignFactory.getPrimitive(ksh);
		
		String data = "this is just a test using digital signatures using Google Tink";

		byte[] signature = pks.sign(data.getBytes());
		
		Assertions.hasEnsuredPredicate(pks);
		Assertions.mustBeInAcceptingState(pks);
		
		KeysetHandle publicKsh = ksh.getPublicKeysetHandle();
		Assertions.hasEnsuredPredicate(publicKsh);

		
		@SuppressWarnings("deprecation")
		PublicKeyVerify pkv = PublicKeyVerifyFactory.getPrimitive(publicKsh);
		Assertions.hasEnsuredPredicate(pkv);
		
		pkv.verify(signature, data.getBytes());
	}
}
