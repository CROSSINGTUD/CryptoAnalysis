package example;

import java.security.*;
import java.security.spec.*;
import java.util.Arrays;
import javax.crypto.*;
import javax.crypto.spec.*;
import javax.crypto.interfaces.*;

public final class NonAuthenticatedDH_2048 {

	public static void main(String argv[]) {
		try {
			AlgorithmParameterGenerator apg = AlgorithmParameterGenerator.getInstance("DH", "SunJCE");
			apg.init(2048);
			AlgorithmParameters p = apg.generateParameters();
			DHParameterSpec dhps = (DHParameterSpec) p.getParameterSpec(DHParameterSpec.class);

			KeyPairGenerator kpg1 = KeyPairGenerator.getInstance("DH", "SunJCE");
			kpg1.initialize(dhps);
			KeyPair kp1 = kpg1.generateKeyPair();

			KeyAgreement ka1 = KeyAgreement.getInstance("DH", "SunJCE");
			ka1.init(kp1.getPrivate());

			byte[] pubKey1 = kp1.getPublic().getEncoded();

			KeyFactory kf1 = KeyFactory.getInstance("DH", "SunJCE");
			X509EncodedKeySpec x509ks = new X509EncodedKeySpec(pubKey1);
			PublicKey apk1 = kf1.generatePublic(x509ks);

			DHParameterSpec dhps2 = ((DHPublicKey) apk1).getParams();

			KeyPairGenerator kpg2 = KeyPairGenerator.getInstance("DH", "SunJCE");
			kpg2.initialize(dhps2);
			KeyPair kp2 = kpg2.generateKeyPair();

			KeyAgreement ka2 = KeyAgreement.getInstance("DH", "SunJCE");
			ka2.init(kp2.getPrivate());

			byte[] pubKey2 = kp2.getPublic().getEncoded();

			KeyFactory kf2 = KeyFactory.getInstance("DH", "SunJCE");
			x509ks = new X509EncodedKeySpec(pubKey2);
			PublicKey apk2 = kf2.generatePublic(x509ks);
			ka1.doPhase(apk2, true);
			byte[] secretKey1 = ka1.generateSecret();

			ka2.doPhase(apk1, true);
			byte[] secretKey2 = ka2.generateSecret();

			if (!Arrays.equals(secretKey1, secretKey2)) {
				throw new Exception("Shared secrets differ");
			}

		} catch (Exception e) {}
	}
}
