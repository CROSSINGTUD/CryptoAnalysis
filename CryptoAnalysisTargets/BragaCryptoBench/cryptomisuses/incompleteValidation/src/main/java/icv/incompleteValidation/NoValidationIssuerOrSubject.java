package icv.incompleteValidation;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Date;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.alexmbraga.utils.CertUtils;

public final class NoValidationIssuerOrSubject {

	public static boolean validate(X509Certificate cert, X509Certificate ca, X500Principal issuer, X500Principal subj,
			Date date) {
		boolean ok = false;
		try {
			if (date != null) {
				cert.checkValidity(date);
			} else {
				cert.checkValidity();
			}
			ok = true;
		} catch (CertificateExpiredException | CertificateNotYetValidException ex) {
			ok = false;
		}

		if (ok) {
			try {
				ok = false;
				cert.verify(ca.getPublicKey());
				ok = true;
			} catch (CertificateException ex) {
				ok = false;
			} catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchProviderException | SignatureException ex) {
				ok = false;
			}
		}

		return ok;
	}

	public static void main(String[] args) {
		Security.addProvider(new BouncyCastleProvider());
		try {
			KeyPair rkp = CertUtils.genRSAKeyPair();
			X509Certificate root = CertUtils.buildSelfSignedCert(rkp);

			KeyPair mkp = CertUtils.genRSAKeyPair();
			X509Certificate middle = CertUtils.buildMiddleCert(mkp.getPublic(), "CN=Intermediate CA Certificate",
					rkp.getPrivate(), root);

			KeyPair ekp = CertUtils.genRSAKeyPair();
			X509Certificate user = CertUtils.buildEndCert(ekp.getPublic(), "CN=End User Certificate", mkp.getPrivate(),
					middle);

			X500Principal issuer = new X500Principal("CN=Root Certificate");
			X500Principal subj1 = new X500Principal("CN=Intermediate CA Certificate");
			X500Principal subj2 = new X500Principal("CN=End User Certificate");

			boolean ok = false;
			ok = validate(middle, root, issuer, subj1, null);

			ok = validate(user, middle, subj1, subj2, null);

		} catch (Exception ex) {
		}
	}
}
