package icv.brokenSSLorTLS;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;

public final class SSLClientDateCertValidation {

	public static void main(String[] args) throws Exception {
		SSLSocket socket = null;
		boolean ok = true;
		try {
			SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			socket = (SSLSocket) factory.createSocket("www.google.com", 443);
			socket.startHandshake();

			SSLSession session = socket.getSession();

			Principal peerPrincipal = session.getPeerPrincipal();

			Certificate[] peerCertificates = session.getPeerCertificates();
			if (peerCertificates != null && peerCertificates.length >= 2) {

				((X509Certificate) peerCertificates[0]).checkValidity();

				peerCertificates[0].verify(peerCertificates[1].getPublicKey());
			}

		} catch (CertificateExpiredException | CertificateNotYetValidException | NoSuchAlgorithmException
				| InvalidKeyException | NoSuchProviderException | SignatureException e) {
			ok = false;
		}
	}
}
