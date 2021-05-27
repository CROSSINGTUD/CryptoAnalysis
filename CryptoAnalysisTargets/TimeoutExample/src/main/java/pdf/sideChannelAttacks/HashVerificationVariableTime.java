
package pdf.sideChannelAttacks;

import javax.crypto.*;
import java.security.*;
import org.bouncycastle.jce.provider.*;
import org.bouncycastle.util.Arrays;

public final class HashVerificationVariableTime {

	public static void main(String args[]) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			BadPaddingException, IllegalBlockSizeException, NoSuchProviderException {

		Security.addProvider(new BouncyCastleProvider());

		MessageDigest md = MessageDigest.getInstance("SHA512", "BC");
		boolean ok;
		long t1, t2;
		long t[] = new long[64], tt[] = new long[64], ttt[] = new long[64];
		long tttt[] = new long[64], ttttt[] = new long[64];
		md.reset();
		byte[] hash1 = md.digest("demo text".getBytes());
		for (int j = 0; j < 1; j++) {

			for (int i = 0; i < t.length; i++) {
				md.reset();
				byte[] hash2 = md.digest("demo text".getBytes());
				hash2[i] = (byte) (hash2[i] ^ 0x01);
				t1 = System.nanoTime();
				ok = isEqualToVar(hash2, hash1);
				t2 = System.nanoTime();
				t[i] = t2 - t1;
			}

			for (int i = 0; i < t.length; i++) {
				md.reset();
				byte[] hash2 = md.digest("demo text".getBytes());
				hash2[i] = (byte) (hash2[i] ^ 0x01);
				t1 = System.nanoTime();
				ok = MessageDigest.isEqual(hash2, hash1);
				t2 = System.nanoTime();
				tt[i] = t2 - t1;
			}

			for (int i = 0; i < t.length; i++) {
				md.reset();
				byte[] hash2 = md.digest("demo text".getBytes());
				hash2[i] = (byte) (hash2[i] ^ 0x01);
				t1 = System.nanoTime();
				ok = isEqualToConst(hash2, hash1);
				t2 = System.nanoTime();
				ttt[i] = t2 - t1;
			}

			for (int i = 0; i < t.length; i++) {
				md.reset();
				byte[] hash2 = md.digest("demo text".getBytes());
				hash2[i] = (byte) (hash2[i] ^ 0x01);
				t1 = System.nanoTime();
				ok = Arrays.areEqual(hash2, hash1);
				t2 = System.nanoTime();
				tttt[i] = t2 - t1;
			}

			for (int i = 0; i < t.length; i++) {
				md.reset();
				byte[] hash2 = md.digest("demo text".getBytes());
				hash2[i] = (byte) (hash2[i] ^ 0x01);
				t1 = System.nanoTime();
				ok = Arrays.constantTimeAreEqual(hash2, hash1);
				t2 = System.nanoTime();
				ttttt[i] = t2 - t1;
			}
		}
		md.reset();
		byte[] hash2 = md.digest("demo text".getBytes());
		t1 = System.nanoTime();
		ok = isEqualToConst(hash2, hash1);
		t2 = System.nanoTime();
	}

	static boolean isEqualToVar(byte[] a, byte[] b) {
		boolean igual = true;
		if (a.length != b.length) {
			igual = false;
		} else {
			for (int i = 0; i < a.length; i++) {
				if (a[i] != b[i]) {
					igual = false;
					break;
				}
			}
		}
		return igual;
	}

	static boolean isEqualToConst(byte[] a, byte[] b) {
		boolean igual = true;
		if (a.length != b.length) {
			igual = false;
		} else {
			for (int i = 0; i < a.length; i++) {
				if (a[i] != b[i]) {
					igual = false;
				}
			}
		}
		return igual;
	}
}
