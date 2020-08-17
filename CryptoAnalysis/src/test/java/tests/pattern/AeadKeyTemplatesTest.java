package tests.pattern;

import java.io.File;
import java.security.GeneralSecurityException;

import org.junit.Test;

import com.google.crypto.tink.aead.AeadKeyTemplates;
import com.google.crypto.tink.proto.KeyTemplate;

import crypto.analysis.CrySLRulesetSelector.Ruleset;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class AeadKeyTemplatesTest extends UsagePatternTestingFramework {

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
	
	@Override
	protected String getSootClassPath() {
		String tinkJarPath = new File("src/test/resources/tink-1.2.0.jar").getAbsolutePath();
		return super.getSootClassPath() +File.pathSeparator +tinkJarPath;
	}
}
