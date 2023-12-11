package cai.undefinedCSP;

import java.security.*;
import javax.crypto.*;

public final class UndefinedProvider4 {

	public static void main(String argv[]) {
		try {
			KeyPairGenerator kpg1 = KeyPairGenerator.getInstance("DH");
			KeyAgreement ka1 = KeyAgreement.getInstance("DH");
			KeyFactory kf1 = KeyFactory.getInstance("DH");

			KeyPairGenerator kpg2 = KeyPairGenerator.getInstance("DH");
			KeyAgreement ka2 = KeyAgreement.getInstance("DH");
			KeyFactory kf2 = KeyFactory.getInstance("DH");
		} catch (Exception e) {
		}
	}
}
