package tests.typestate;

import java.security.GeneralSecurityException;
import java.security.Signature;
import java.security.SignatureException;

import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;
import test.IDEALCrossingTestingFramework;
import test.assertions.Assertions;

public class SignatureTests extends IDEALCrossingTestingFramework {

	@Override
	protected Ruleset getRuleset() {
		return Ruleset.JavaCryptographicArchitecture;
	}
	
	@Override
	protected String getRulename() {
		return "Signature";
	}
	@Test
	public void signatureTest() throws GeneralSecurityException {
		Signature signature = Signature.getInstance("SHA256withRSA");
		signature.initSign(null);
		Assertions.assertState(signature, 1);
		signature.update("test".getBytes());
		Assertions.assertState(signature, 2);
		signature.sign();
		Assertions.assertState(signature, 3);
	}
	@Test
	public void signatureTestWrapped() throws GeneralSecurityException {
		T ex = new T();
		ex.doInit();
		Assertions.assertState(ex.signature, 1);
		ex.doUpate();
		Assertions.assertState(ex.signature, 2);
		ex.doSign();
		Assertions.assertState(ex.signature, 3);
	}

	public static class T {
		public Signature signature;

		private void doInit() throws GeneralSecurityException {
			signature = Signature.getInstance("SHA256withRSA");
			signature.initSign(null);
		}

		public void doSign() throws SignatureException {
			signature.sign();
		}

		private void doUpate() throws GeneralSecurityException {
			signature.update("test".getBytes());
		}
	}
}
