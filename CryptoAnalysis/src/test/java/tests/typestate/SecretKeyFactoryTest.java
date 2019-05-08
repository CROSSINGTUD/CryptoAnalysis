package tests.typestate;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;
import test.IDEALCrossingTestingFramework;
import test.assertions.Assertions;

public class SecretKeyFactoryTest extends IDEALCrossingTestingFramework{

	@Override
	protected Ruleset getRuleset() {
		return Ruleset.JavaCryptographicArchitecture;
	}
	
	@Override
	protected String getRulename() {
		return "SecretKeyFactory";
	}
	
	@Test
	public void testSecretKeyFactory1() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException {
		final PBEKeySpec pbekeyspec = new PBEKeySpec(null,null, 65000, 128);
		final SecretKeyFactory secFac = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		secFac.generateSecret(pbekeyspec);
		Assertions.assertState(secFac, 1);
	}
}
