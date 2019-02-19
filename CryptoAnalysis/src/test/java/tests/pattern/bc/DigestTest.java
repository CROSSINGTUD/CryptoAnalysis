package tests.pattern.bc;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.junit.Test;

import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class DigestTest extends UsagePatternTestingFramework {
	
	@Override
	protected String getSootClassPath() {
		// TODO Auto-generated method stub
		String sootCp = super.getSootClassPath();
		
		sootCp += File.pathSeparator + "/Users/rakshitkr/.m2/repository/org/bouncycastle/bcprov-jdk15on/1.60/bcprov-jdk15on-1.60.jar";

		return sootCp; 
	}
	
	@Test
	public void UsagePatternTest1() throws UnsupportedEncodingException {
		SHA256Digest digest = new SHA256Digest();
		final byte[] input = "input".getBytes("UTF-8");
		digest.update(input, 0, input.length);
		Assertions.mustNotBeInAcceptingState(digest);
		Assertions.notHasEnsuredPredicate(input);
		byte[] resBuf = new byte[digest.getDigestSize()];
		digest.doFinal(resBuf, 0);
	}
	
	@Test
	public void UsagePatternTest2() throws UnsupportedEncodingException {
		SHA256Digest digest = new SHA256Digest();
		final byte[] input = "input".getBytes("UTF-8");
		digest.update(input, 0, input.length);
		byte[] resBuf = new byte[digest.getDigestSize()];
		digest.doFinal(resBuf, 0);
		Assertions.mustBeInAcceptingState(digest);
//		Assertions.hasEnsuredPredicate(resBuf);
	}
	
	@Test
	public void UsagePatternTest3() throws UnsupportedEncodingException {
		Digest digest = new SHA256Digest();
		final String[] input = { "input1", "input2", "input3", "input4" };
		int i = 0;
		while (i < input.length) {
			digest.update(input[i].getBytes("UTF-8"), 0, input[i].length());
		}
		byte[] resBuf = new byte[digest.getDigestSize()];
		digest.doFinal(resBuf, 0);
		Assertions.mustBeInAcceptingState(digest);
		Assertions.hasEnsuredPredicate(resBuf);
	}
	
	@Test
	public void UsagePatternTest4() throws UnsupportedEncodingException {
		Digest digest = new SHA256Digest();
		final byte[] input = "input".getBytes("UTF-8");
		byte[] resBuf = new byte[digest.getDigestSize()]; 
		digest.doFinal(resBuf, 0);
		Assertions.mustNotBeInAcceptingState(digest);
		Assertions.notHasEnsuredPredicate(input);
		Assertions.notHasEnsuredPredicate(resBuf);
	}

	@Test
	public void UsagePatternTest5() throws UnsupportedEncodingException {
		Digest digest = new SHA256Digest();
		final byte[] input1 = "input1".getBytes("UTF-8");
		final byte[] input2 = "input2".getBytes("UTF-8");
		digest.update(input1, 0, input1.length);
		byte[] resBuf1 = new byte[digest.getDigestSize()]; 
		digest.doFinal(resBuf1, 0);
		Assertions.hasEnsuredPredicate(input1);
		Assertions.hasEnsuredPredicate(resBuf1);
		Assertions.mustBeInAcceptingState(digest);

		digest = new SHA512Digest();
		digest.update(input2, 0, input2.length);
		byte[] resBuf2 = new byte[digest.getDigestSize()]; 
		digest.doFinal(resBuf2, 0);
		Assertions.mustBeInAcceptingState(digest);
		Assertions.hasEnsuredPredicate(input2);
		Assertions.hasEnsuredPredicate(resBuf2);
	}

	@Test
	public void UsagePatternTest6() throws UnsupportedEncodingException {
		Digest digest = new SHA256Digest();
		final byte[] input = "input".getBytes("UTF-8");
		final byte[] input2 = "input2".getBytes("UTF-8");
		digest.update(input, 0, input.length);
		byte[] resBuf = new byte[digest.getDigestSize()];
		digest.doFinal(resBuf, 0);
		Assertions.hasEnsuredPredicate(input);
		Assertions.hasEnsuredPredicate(resBuf);
		digest.reset();
		digest.update(input2, 0, input2.length);
		Assertions.mustNotBeInAcceptingState(digest);
		Assertions.notHasEnsuredPredicate(input2);
		digest.doFinal(resBuf, 0);
	}

	
}
