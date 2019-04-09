package inflatable_donkey;

import java.util.Optional;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.engines.RFC3394WrapEngine;
import org.bouncycastle.crypto.params.KeyParameter;

@SuppressWarnings("deprecation")
public class KeyBlobCurve25519Unwrap {
	public static Optional<byte[]> curve25519Unwrap(
			byte[] myPublicKey,
			byte[] myPrivateKey,
			byte[] otherPublicKey,
			byte[] wrappedKey) {

		SHA256Digest sha256 = new SHA256Digest();

		byte[] shared = new byte[32];

		// Stripped down NIST SP 800-56A KDF.
		byte[] counter = new byte[]{0x00, 0x00, 0x00, 0x01};
		byte[] hash = new byte[sha256.getDigestSize()];

		sha256.reset();
		sha256.update(counter, 0, counter.length);
		sha256.update(shared, 0, shared.length);
		sha256.update(otherPublicKey, 0, otherPublicKey.length);
		sha256.update(myPublicKey, 0, myPublicKey.length);
		sha256.doFinal(hash, 0);

		return unwrapAES(hash, wrappedKey);
	}

	public static Optional<byte[]> unwrapAES(byte[] keyEncryptionKey, byte[] wrappedKey) {
		try {
			RFC3394WrapEngine engine = new RFC3394WrapEngine(new AESFastEngine());
			engine.init(false, new KeyParameter(keyEncryptionKey));
			return Optional.of(engine.unwrap(wrappedKey, 0, wrappedKey.length));

		} catch (InvalidCipherTextException ex) {
			return Optional.empty();
		}
	}

	public static byte[] wrapAES(byte[] keyEncryptionKey, byte[] unwrappedKey) {
		RFC3394WrapEngine engine = new RFC3394WrapEngine(new AESFastEngine());
		engine.init(true, new KeyParameter(keyEncryptionKey));
		return engine.wrap(unwrappedKey, 0, unwrappedKey.length);
	}


}
