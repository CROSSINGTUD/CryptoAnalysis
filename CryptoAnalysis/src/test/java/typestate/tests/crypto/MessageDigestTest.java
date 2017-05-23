package typestate.tests.crypto;

import java.io.File;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;

import test.IDEALCrossingTestingFramework;

public class MessageDigestTest  extends IDEALCrossingTestingFramework {

	@Override
	protected File getSMGFile() {
		return new File("MessageDigest.smg");
	}

	@Test
	public void MessageDigestTest1() throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		
		Benchmark.assertState(md, 0);
	}
	@Test
	public void MessageDigestTest2() throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.digest(null);
		Benchmark.assertState(md, 1);
	}
	@Test
	public void MessageDigestTest3() throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(ByteBuffer.allocate(1));
		Benchmark.assertState(md, 2);
	}
	@Test
	public void MessageDigestTest4() throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.digest(null);
		md.reset();
		Benchmark.assertState(md, 0);
		//TODO fails because there is no transition out of state 1 with reset()
	}
	@Test
	public void MessageDigestTest5() throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(ByteBuffer.allocate(1));
		md.digest(null);
		Benchmark.assertState(md, 1);
		//TODO fails because there is no transition out of state 2 with digest(byte)
	}
	
}
