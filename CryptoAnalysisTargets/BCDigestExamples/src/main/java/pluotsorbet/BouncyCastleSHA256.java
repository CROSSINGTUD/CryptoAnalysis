package pluotsorbet;

import org.bouncycastle.crypto.digests.SHA256Digest;

public class BouncyCastleSHA256 {
	
	// Try to mimic a popular midlet that commonly does around
    // 170 update calls on 4096 bytes at a time.
    private static final int UPDATES = 170;
	
    public void TestSHA256DigestOne() {
	    byte[] digest = new byte[4096];
	    for (int i = 0; i < digest.length; i++) {
	        digest[i] = (byte)i;
	    }

	    long start = System.currentTimeMillis();
	    for (int i = 0; i < 20; i++) {
	        SHA256Digest digester = new SHA256Digest();
	        byte[] retValue = new byte[digester.getDigestSize()];
	        for (int j = 0; j < UPDATES; j++) {
	            digester.update(digest, 0, digest.length);
	        }
	        digester.doFinal(retValue, 0);
	    }
	    long time = System.currentTimeMillis() - start;
	    System.out.println("BouncyCastleSHA256: " + time);
	}
	
	private static final String[] messages = {
	        "",
	        "a",
	        "abc",
	        "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq",
	        "message digest",
	        "secure hash algorithm"
	    };
	
	public void testSHA256DigestTwo() {
	    SHA256Digest md = new SHA256Digest();
	    byte[] retValue = new byte[md.getDigestSize()];

	    for (int i = 0; i < messages.length; i++) {
	        byte[] bytes = messages[i].getBytes();
	        md.update(bytes, 0, bytes.length);
	        md.doFinal(retValue, 0);
	    }

	    for (int i = 0; i < 1000000; i++) {
	        md.update((byte)'a');
	    }
	    md.doFinal(retValue, 0);
	}
}
