package pkc.ka.issuesDHandECDH;

import java.security.*;
import java.security.spec.*;
import java.util.Arrays;
import javax.crypto.*;

public final class NonAuthenticatedEphemeralDH_1024 {

	public static void main(String argv[]) {
		try {
			KeyPairGenerator kpg1 = KeyPairGenerator.getInstance("DH", "SunJCE");
			kpg1.initialize(1024);
			KeyPair kp1 = kpg1.generateKeyPair();
			KeyAgreement ka1 = KeyAgreement.getInstance("DH", "SunJCE");
			ka1.init(kp1.getPrivate());

			byte[] pubKey1 = kp1.getPublic().getEncoded();

			KeyFactory kf1 = KeyFactory.getInstance("DH", "SunJCE");
			X509EncodedKeySpec x509ks = new X509EncodedKeySpec(pubKey1);
			PublicKey apk1 = kf1.generatePublic(x509ks);

			KeyPairGenerator kpg2 = KeyPairGenerator.getInstance("DH", "SunJCE");
			kpg2.initialize(1024);
			KeyPair kp2 = kpg2.generateKeyPair();
			KeyAgreement ka2 = KeyAgreement.getInstance("DH", "SunJCE");
			ka2.init(kp2.getPrivate());

			byte[] pubKey2 = kp2.getPublic().getEncoded();

			KeyFactory kf2 = KeyFactory.getInstance("DH", "SunJCE");
			x509ks = new X509EncodedKeySpec(pubKey2);
			PublicKey apk2 = kf2.generatePublic(x509ks);
			ka1.doPhase(apk2, true);
			byte[] genSec1 = ka1.generateSecret();

			ka2.doPhase(apk1, true);
			byte[] genSec2 = ka2.generateSecret();

			if (!Arrays.equals(genSec1, genSec2)) {
				throw new Exception("Shared secrets differ");
			}
		} catch (Exception e) {
		}
	}
}
