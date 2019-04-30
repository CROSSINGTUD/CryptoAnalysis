package tests.typestate;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import org.junit.Ignore;
import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;
import test.IDEALCrossingTestingFramework;
import test.assertions.Assertions;

public class KeyStoreTest extends IDEALCrossingTestingFramework {

	@Override
	protected Ruleset getRuleset() {
		return Ruleset.JavaCryptographicArchitecture;
	}
	
	@Override
	protected String getRulename() {
		return "KeyStore";
	}
	@Ignore
	@Test
	public void correctUsage() throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException, CertificateException, IOException {
		KeyStore keystore = KeyStore.getInstance("PKCS12", "BC");
		keystore.load(null, null);
		keystore.store(null, "".toCharArray());
		Assertions.assertState(keystore, 1);
	}


	@Test
	public void correctUsage1() throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException, CertificateException, IOException {
		KeyStore keystore = KeyStore.getInstance("PKCS12", "BC");
		keystore.load(null, null);
		Assertions.assertState(keystore, 1);
	}
}
