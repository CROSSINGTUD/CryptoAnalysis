package diqube;

import java.nio.ByteBuffer;
import java.util.List;

import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.signers.RSADigestSigner;

public class TicketSignatureService {
	
	/**
	   * Checks if a {@link Ticket} has a valid signature.
	   * 
	   * @param deserializedTicket
	   *          The result of {@link TicketUtil#deserialize(ByteBuffer)} of the serialized {@link Ticket}.
	   * @return true if {@link Ticket} signature is valid.
	   */
	  public boolean isValidTicketSignature(byte[] deserializedTicket) {
	    for (RSAKeyParameters pubKey : getPublicValidationKeys()) {
	      RSADigestSigner signer = new RSADigestSigner(new SHA256Digest());
	      signer.init(false, pubKey);
	      signer.update(deserializedTicket, 0, deserializedTicket.length);
	      if (signer.verifySignature(deserializedTicket))
	        return true;
	    }
	    return false;
	  }
	
	private List<RSAKeyParameters> getPublicValidationKeys() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Calculates the signature of a ticket and updates the given {@link Ticket} object directly.
	 * 
	 * @throws IllegalStateException
	 *           If ticket cannot be signed.
	 */
	public void signTicket() throws IllegalStateException {
	  byte[] serialized = new byte[32]; // replaced actual method here
	  byte[] claimBytes = new byte[32]; // replaced actual method here

	  RSAPrivateCrtKeyParameters signingKey = getPrivateSigningKey();

	  if (signingKey == null)
	    throw new IllegalStateException("Cannot sign ticket because there is no private signing key available.");

	  RSADigestSigner signer = new RSADigestSigner(new SHA256Digest());
	  signer.init(true, signingKey);
	  signer.update(claimBytes, 0, claimBytes.length);
	  try {
	    byte[] signature = signer.generateSignature();
	    setSignature(signature);
	  } catch (DataLengthException | CryptoException e) {
	    throw new IllegalStateException("Cannot sign ticket", e);
	  }
	}

	private RSAPrivateCrtKeyParameters getPrivateSigningKey() {
		// TODO Auto-generated method stub
		// replaced actual method here
		return null;
	}

	private void setSignature(byte[] signature) {
		// TODO Auto-generated method stub
		// replaced actual method here
	}
	
	
}
