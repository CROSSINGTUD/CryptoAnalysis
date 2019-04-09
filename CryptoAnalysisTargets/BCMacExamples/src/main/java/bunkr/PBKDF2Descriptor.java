package bunkr;

import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.macs.HMac;

public class PBKDF2Descriptor {
	
	public static final int MINIMUM_PBKD2_ITERS = 4096;
	
	public static int calculateRounds(int milliseconds)
	{
	    HMac mac = new HMac(new SHA256Digest());
	    byte[] state = new byte[mac.getMacSize()];
	    long startTime = System.currentTimeMillis();
	    int pbkdf2Iterations = 0;
	    while((System.currentTimeMillis() - startTime) < milliseconds)
	    {
	        mac.update(state, 0, state.length);
	        mac.doFinal(state, 0);
	        pbkdf2Iterations++;
	    }
	    pbkdf2Iterations = Math.max(pbkdf2Iterations, PBKDF2Descriptor.MINIMUM_PBKD2_ITERS);
	    return pbkdf2Iterations;
	}
}
