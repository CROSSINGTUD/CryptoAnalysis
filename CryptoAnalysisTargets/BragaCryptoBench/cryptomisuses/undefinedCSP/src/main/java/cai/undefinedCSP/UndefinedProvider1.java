package cai.undefinedCSP;

import java.security.KeyPairGenerator;
import java.security.Signature;

public final class UndefinedProvider1 {

	public static void main(String[] args) throws Exception {

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
		Signature signer = Signature.getInstance("SHA256WithDSA");
		Signature verifier = Signature.getInstance("SHA256WithDSA");

	}
}
