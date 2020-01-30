package icv.brokenSSLorTLS;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import javax.net.ssl.*;

public final class SSLctxNoKeyMgmtNoTrustMgmt1 {

	public static void main(String[] args) throws Exception {
		SSLSocket socket = null;
		Boolean ok = true;
		try {

			SSLContext sslctx = SSLContext.getInstance("TLS");
			sslctx.init(new KeyManager[] {}, null, SecureRandom.getInstanceStrong());
			SSLSocketFactory factory = sslctx.getSocketFactory();

			socket = (SSLSocket) factory.createSocket("www.google.com", 443);
			socket.startHandshake();

			SSLSession session = socket.getSession();

			Principal peerPrincipal = session.getPeerPrincipal();

			Certificate[] peerCertificates = session.getPeerCertificates();

		} catch (NoSuchAlgorithmException | KeyManagementException | IOException e) {
			ok = false;
		}
	}
}
