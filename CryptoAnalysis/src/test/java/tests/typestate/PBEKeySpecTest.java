package tests.typestate;

import java.io.File;
import java.security.NoSuchAlgorithmException;

import javax.crypto.spec.PBEKeySpec;

import org.junit.Test;

import test.IDEALCrossingTestingFramework;
import test.assertions.Assertions;

public class PBEKeySpecTest  extends IDEALCrossingTestingFramework {

	@Override
	protected File getCryptSLFile() {
		return new File("PBEKeySpec.cryptslbin");
	}

	@Test
	public void PBEKeySpecTest1() throws NoSuchAlgorithmException {
		PBEKeySpec pbe = new PBEKeySpec(new char[]{}, new byte[1], 1000, 128);
		Assertions.assertState(pbe, 0);
	}

	@Test
	public void PBEKeySpecTest2() throws NoSuchAlgorithmException {
		PBEKeySpec pbe = new PBEKeySpec(new char[]{}, new byte[1], 1000, 128);
		pbe.clearPassword();
		Assertions.assertState(pbe, 1);
	}

	
}
