package issue49;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import org.bouncycastle.pqc.jcajce.spec.KyberParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.DilithiumParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Main {
	public byte[] sign(String data) throws Exception {

		Security.addProvider(new BouncyCastleProvider());

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("Kyber", "BC");
		kpg.initialize(KyberParameterSpec.kyber768, new SecureRandom());
		KeyPair kyberKeyPair = kpg.generateKeyPair();

		KeyPairGenerator sigGen = KeyPairGenerator.getInstance("Dilithium", "BC");
		sigGen.initialize(DilithiumParameterSpec.dilithium3, new SecureRandom());
		KeyPair dilithiumKeyPair = sigGen.generateKeyPair();

		Signature pqSig = Signature.getInstance("Dilithium", "BC");
		pqSig.initSign(dilithiumKeyPair.getPrivate());
		pqSig.update("message".getBytes());

		return pqSig.sign();
	}

	private PrivateKey getPrivateKey() throws NoSuchAlgorithmException {
		KeyPairGenerator gen = KeyPairGenerator.getInstance("AES");
		gen.initialize(1024);
		KeyPair keyPair = gen.generateKeyPair();
		return keyPair.getPrivate();
	}
}
