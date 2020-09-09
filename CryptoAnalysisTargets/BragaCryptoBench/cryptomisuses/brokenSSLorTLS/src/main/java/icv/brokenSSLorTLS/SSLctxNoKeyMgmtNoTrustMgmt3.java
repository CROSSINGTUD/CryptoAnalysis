package icv.brokenSSLorTLS;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;

public final class SSLctxNoKeyMgmtNoTrustMgmt3 {

	public static void main(String[] args) throws Exception {
		SSLSocket socket = null;
		Boolean ok = true;
		try {

			SSLContext sslctx = SSLContext.getInstance("TLS");
			TrustManager[] tac = { new TrustAllCerts() };
			sslctx.init(null, tac, SecureRandom.getInstanceStrong());
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

class TrustAllCerts implements X509TrustManager {

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}

	@Override
	public void checkClientTrusted(X509Certificate[] xcs, String string) {

	}

	@Override
	public void checkServerTrusted(X509Certificate[] xcs, String string) {

	}
}

class TrustAllHosts implements HostnameVerifier {

	@Override
	public boolean verify(final String hostname, final SSLSession session) {
		return true;
	}
}
