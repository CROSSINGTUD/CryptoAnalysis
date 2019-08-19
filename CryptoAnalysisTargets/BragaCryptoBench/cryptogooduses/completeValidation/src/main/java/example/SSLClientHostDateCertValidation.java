package example;

import example._utils.CertUtils;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;

/* https://docs.oracle.com/javase/8/docs/technotes/guides/security/jsse/JSSERefGuide.html
 * When using raw SSLSocket and SSLEngine classes, you should always check the
 * peer's credentials before sending any data. The SSLSocket and SSLEngine 
 * classes do not automatically verify that the host name in a URL matches the
 * host name in the peer's credentials. An application could be exploited with 
 * URL spoofing if the host name is not verified.
 */
public final class SSLClientHostDateCertValidation {

  public static void main(String[] args) throws Exception {
    SSLSocket socket = null;
    boolean ok = true;
    try {
      SSLSocketFactory factory
              = (SSLSocketFactory) SSLSocketFactory.getDefault();
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
      System.out.println("SSL Peer Principal: " + peerPrincipal);

      if (peerPrincipal.getName().contains("CN=" + session.getPeerHost())) {
        System.out.println("Host and Principal match");
      } else {
        System.out.println("Host and Principal mismatch");
        throw new CertificateException("Host and Principal mismatch");
      }

      Certificate[] peerCertificates = session.getPeerCertificates();
      if (peerCertificates != null && peerCertificates.length >= 2) {

        ((X509Certificate) peerCertificates[0]).checkValidity();

        peerCertificates[0].verify(peerCertificates[1].getPublicKey());
        // DANGER: No CA Cert Validation
      } else {
        throw new CertificateException("Unable to verify certificate");
      }

    } catch (CertificateExpiredException | CertificateNotYetValidException |
            NoSuchAlgorithmException | InvalidKeyException |
            NoSuchProviderException | SignatureException e) {
      System.out.println(e);
      ok = false;
    } catch (CertificateException e) {
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
