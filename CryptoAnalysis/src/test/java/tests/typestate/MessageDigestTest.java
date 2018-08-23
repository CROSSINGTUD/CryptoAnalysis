package tests.typestate;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.Ignore;
import org.junit.Test;

import test.IDEALCrossingTestingFramework;
import test.assertions.Assertions;

public class MessageDigestTest  extends IDEALCrossingTestingFramework {

	@Override
	protected File getCryptSLFile() {
		return new File("MessageDigest.cryptslbin");
	}

	@Test
	public void MessageDigestTest1() throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		
		Assertions.assertState(md, 0);
	}
	@Test
	public void MessageDigestTest2() throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.digest(null);
		Assertions.assertState(md, 1);
	}
	@Test
	public void MessageDigestTest3() throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(ByteBuffer.allocate(1));
		Assertions.assertState(md, 2);
	}
	@Ignore
	@Test
	public void MessageDigestTest4() throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.digest(null);
		md.reset();
		Assertions.assertState(md, 0);
		//TODO fails because there is no transition out of state 1 with reset()
	}
	@Test
	public void MessageDigestTest5() throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(ByteBuffer.allocate(1));
		md.digest(null);
		Assertions.assertState(md, 1);
		//TODO fails because there is no transition out of state 2 with digest(byte)
	}

	@Test
	public void MessageDigestTest6() throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.digest(null);
		Assertions.assertState(md, 1);
		md = MessageDigest.getInstance("SHA-1");
		md.digest(null);
		Assertions.assertState(md, 1);
	}
	@Test
	public void MessageDigestTest7() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		Assertions.extValue(0);
		final byte[] input = "input".getBytes("UTF-8");
		final byte[] input2 = "input2".getBytes("UTF-8");
		byte[] output = md.digest(input);
		Assertions.assertState(md, 1);
		
		
		md = MessageDigest.getInstance("MD5");
		output = md.digest(input2);
		Assertions.assertState(md, 1);
	}
}
