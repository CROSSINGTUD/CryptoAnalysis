package tests.pattern.tink;

import java.security.GeneralSecurityException;

import org.junit.Test;

import com.google.crypto.tink.aead.AeadKeyTemplates;
import com.google.crypto.tink.proto.KeyTemplate;

import crypto.analysis.CrySLRulesetSelector.Ruleset;
import test.assertions.Assertions;

public class TestAeadKeyTemplates extends TestTinkPrimitives {

	@Override
	protected Ruleset getRuleSet() {
		return Ruleset.Tink;
	}
	
	@Test
	public void generateNewAES128GCMKeySet() throws GeneralSecurityException {
		KeyTemplate kt = AeadKeyTemplates.createAesGcmKeyTemplate(16);
		Assertions.hasEnsuredPredicate(kt);
		Assertions.mustBeInAcceptingState(kt);
	}
	
	@Test
	public void generateNewAES128GCMKeySet_static() throws GeneralSecurityException {
		KeyTemplate kt = AeadKeyTemplates.AES128_GCM;
		Assertions.hasEnsuredPredicate(kt);
		Assertions.mustBeInAcceptingState(kt);
	}
}
