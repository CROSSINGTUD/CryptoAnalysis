package icv.brokenSSLorTLS;

import _utils.CertUtils;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import javax.net.ssl.*;

/* https://docs.oracle.com/javase/8/docs/technotes/guides/security/jsse/JSSERefGuide.html
 * When using raw SSLSocket and SSLEngine classes, you should always check the
 * peer's credentials before sending any data. The SSLSocket and SSLEngine 
 * classes do not automatically verify that the host name in a URL matches the
 * host name in the peer's credentials. An application could be exploited with 
 * URL spoofing if the host name is not verified.
 */
public final class SSLctxNoKeyMgmtNoTrustMgmt1 {

  public static void main(String[] args) throws Exception {
    SSLSocket socket = null;
    Boolean ok = true;
    try {
      
      SSLContext sslctx = SSLContext.getInstance("TLS");
      sslctx.init(new KeyManager[]{},null, // the misuse
              SecureRandom.getInstanceStrong());
      SSLSocketFactory factory = sslctx.getSocketFactory();
      
      socket = (SSLSocket) factory.createSocket("www.google.com", 443);
      socket.startHandshake();
      // all validations should happen here after the handshake and before
      // any data exchange

      System.out.println();
      System.out.println("Session infos");
      SSLSession session = socket.getSession();
      System.out.println("Protocol: " + session.getProtocol());
      System.out.println("Ciphersuite: " + session.getCipherSuite());
      System.out.println("Host name: " + session.getPeerHost());
      System.out.println();

      Principal peerPrincipal = session.getPeerPrincipal();
      System.out.println(peerPrincipal);

      System.out.println();
      System.out.println("Peer certificates");
      Certificate[] peerCertificates = session.getPeerCertificates();
      for (Certificate c : peerCertificates) {
        System.out.println(c);
      }

    } catch (NoSuchAlgorithmException | KeyManagementException | IOException e){
      System.out.println(e);
      ok = false;
    }
    System.out.println();
    if (ok) {
      CertUtils.handleSocket(socket);
    } else {
      System.out.println("Something went wrong with certificate validation.");
    }
  }

}