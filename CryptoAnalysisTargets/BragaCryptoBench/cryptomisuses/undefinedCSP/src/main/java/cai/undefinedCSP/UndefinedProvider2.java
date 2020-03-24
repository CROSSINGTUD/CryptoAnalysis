package cai.undefinedCSP;

import java.security.KeyPairGenerator;
import java.security.Signature;

public final class UndefinedProvider2 {

	public static void main(String[] args) throws Exception {

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
		Signature signer = Signature.getInstance("SHA512WithECDSA");
		Signature verifier = Signature.getInstance("SHA512WithECDSA");

	}
}
