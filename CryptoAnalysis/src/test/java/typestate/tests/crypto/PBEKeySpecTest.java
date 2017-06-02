package typestate.tests.crypto;

import java.io.File;
import java.security.NoSuchAlgorithmException;

import javax.crypto.spec.PBEKeySpec;

import org.junit.Test;

import test.IDEALCrossingTestingFramework;

public class PBEKeySpecTest  extends IDEALCrossingTestingFramework {

	@Override
	protected File getCryptSLFile() {
		return new File("PBEKeySpec.cryptslbin");
	}

	@Test
	public void PBEKeySpecTest1() throws NoSuchAlgorithmException {
		PBEKeySpec pbe = new PBEKeySpec(new char[]{});
		//TODO No matching constructor in specification:
		//We only match for this constructor: javax.crypto.spec.PBEKeySpec(char[] pw,byte[] salt,int it,AnyType _,)
		Benchmark.assertState(pbe, -1);
	}

	@Test
	public void PBEKeySpecTest2() throws NoSuchAlgorithmException {
		PBEKeySpec pbe = new PBEKeySpec(new char[]{}, new byte[1], 1000);
		//TODO No matching constructor in specification:
		//We only match for this constructor: javax.crypto.spec.PBEKeySpec(char[] pw,byte[] salt,int it,AnyType _,)
		Benchmark.assertState(pbe, -1);
	}

	@Test
	public void PBEKeySpecTest3() throws NoSuchAlgorithmException {
		PBEKeySpec pbe = new PBEKeySpec(new char[]{}, new byte[1], 1000, 128);
		Benchmark.assertState(pbe, 0);
	}

	@Test
	public void PBEKeySpecTest4() throws NoSuchAlgorithmException {
		PBEKeySpec pbe = new PBEKeySpec(new char[]{}, new byte[1], 1000, 128);
		pbe.clearPassword();
		Benchmark.assertState(pbe, 1);
	}

	@Test
	public void PBEKeySpecTest5() throws NoSuchAlgorithmException {
		PBEKeySpec pbe = new PBEKeySpec(new char[]{});
		//TODO No matching constructor in specification:
		//We only match for this constructor: javax.crypto.spec.PBEKeySpec(char[] pw,byte[] salt,int it,AnyType _,)
		pbe.clearPassword();
		Benchmark.assertState(pbe, -1);
	}

	@Test
	public void PBEKeySpecTest6() throws NoSuchAlgorithmException {
		PBEKeySpec pbe = new PBEKeySpec(new char[]{}, new byte[1], 1000);
		//TODO No matching constructor in specification:
		//We only match for this constructor: javax.crypto.spec.PBEKeySpec(char[] pw,byte[] salt,int it,AnyType _,)
		pbe.clearPassword();
		Benchmark.assertState(pbe, -1);
	}
	
}
