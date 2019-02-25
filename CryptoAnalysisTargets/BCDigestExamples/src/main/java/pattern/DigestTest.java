package pattern;


import java.io.File;
import java.io.UnsupportedEncodingException;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;

public class DigestTest {
	
	public void digestDefaultUsage() throws UnsupportedEncodingException {
		SHA256Digest digest = new SHA256Digest();
		final byte[] input = "input".getBytes("UTF-8");
		digest.update(input, 0, input.length);
		byte[] resBuf = new byte[digest.getDigestSize()];
		digest.doFinal(resBuf, 0);
	}
	
	public void digestWithMultipleUpdates() throws UnsupportedEncodingException {
		Digest digest = new SHA256Digest();
		final String[] input = { "input1", "input2", "input3", "input4" };
		int i = 0;
		while (i < input.length) {
			digest.update(input[i].getBytes("UTF-8"), 0, input[i].length());
		}
		byte[] resBuf = new byte[digest.getDigestSize()];
		digest.doFinal(resBuf, 0);
	}
	
	public void digestWithoutUpdate() throws UnsupportedEncodingException {
		SHA256Digest digest = new SHA256Digest();
		final byte[] input = "input".getBytes("UTF-8");
		byte[] resBuf = new byte[digest.getDigestSize()]; 
		digest.doFinal(resBuf, 0);
	}

	public void multipleDigests() throws UnsupportedEncodingException {
		Digest digest = new SHA256Digest();
		final byte[] input1 = "input1".getBytes("UTF-8");
		final byte[] input2 = "input2".getBytes("UTF-8");
		digest.update(input1, 0, input1.length);
		byte[] resBuf1 = new byte[digest.getDigestSize()]; 
		digest.doFinal(resBuf1, 0);

		digest = new SHA512Digest();
		digest.update(input2, 0, input2.length);
		byte[] resBuf2 = new byte[digest.getDigestSize()]; 
		digest.doFinal(resBuf2, 0);
	}

	public void digestWithReset() throws UnsupportedEncodingException {
		SHA256Digest digest = new SHA256Digest();
		final byte[] input = "input".getBytes("UTF-8");
		final byte[] input2 = "input2".getBytes("UTF-8");
		digest.update(input, 0, input.length);
		byte[] resBuf = new byte[digest.getDigestSize()];
		digest.doFinal(resBuf, 0);
		digest.reset();
		digest.update(input2, 0, input2.length);
		digest.doFinal(resBuf, 0);
	}

	
}
