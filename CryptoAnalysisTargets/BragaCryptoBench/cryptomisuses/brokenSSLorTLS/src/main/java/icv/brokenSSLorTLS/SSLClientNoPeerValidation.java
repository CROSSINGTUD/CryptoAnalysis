package icv.brokenSSLorTLS;

import java.security.Principal;
import java.security.cert.Certificate;
import javax.net.ssl.*;

public final class SSLClientNoPeerValidation {

	public static void main(String[] args) throws Exception {
		SSLSocket socket = null;
		Boolean ok = true;
		try {
			SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			socket = (SSLSocket) factory.createSocket("www.google.com", 443);
			socket.startHandshake();

			SSLSession session = socket.getSession();

			Principal peerPrincipal = session.getPeerPrincipal();

			Certificate[] peerCertificates = session.getPeerCertificates();

		} catch (Exception e) {
			ok = false;
		}
	}
}
