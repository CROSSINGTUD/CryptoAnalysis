package example;

import example._utils.CertUtils;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreParameters;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.PKIXRevocationChecker;
import java.security.cert.PolicyNode;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import javax.net.ssl.*;

/* https://docs.oracle.com/javase/8/docs/technotes/guides/security/jsse/JSSERefGuide.html
 * When using raw SSLSocket and SSLEngine classes, you should always check the
 * peer's credentials before sending any data. The SSLSocket and SSLEngine 
 * classes do not automatically verify that the host name in a URL matches the
 * host name in the peer's credentials. An application could be exploited with 
 * URL spoofing if the host name is not verified.
 */
public final class SSLClientCertPathCRLValidation {

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
      Principal peerPrincipal = session.getPeerPrincipal();
      System.out.println("SSL Peer Principal: " + peerPrincipal);

      // Step 1. Obtain CA root certs and the certification path to be validated
      Certificate[] certs = session.getPeerCertificates();
      X509Certificate[] x509certs = new X509Certificate[certs.length-1];
      for (int i = 0; i < certs.length-1; i++) {
        x509certs[i] = (X509Certificate) certs[i];
        //System.out.println(x509certs[i]);
      }
      X509Certificate anchor = (X509Certificate) certs[certs.length-1];
      
      List l = Arrays.asList(x509certs);
      CertificateFactory cf = CertificateFactory.getInstance("X.509","SUN");
      //System.out.println(cf.getProvider().getName());
      CertPath cp = cf.generateCertPath(l);

      // Step 2. Create a PKIXParameters with the trust anchors
      TrustAnchor ta = new TrustAnchor(anchor, null);
      PKIXParameters params = new PKIXParameters(Collections.singleton(ta));

      // Step 3. Use a CertPathValidator to validate the certificate path
      CertPathValidator cpv = CertPathValidator.getInstance("PKIX","SUN");
      //System.out.println(cpv.getProvider().getName());
      PKIXRevocationChecker rc = 
              (PKIXRevocationChecker)cpv.getRevocationChecker();
      rc.setOptions(EnumSet.of(PKIXRevocationChecker.Option.SOFT_FAIL));
      rc.setOptions(EnumSet.of(PKIXRevocationChecker.Option.NO_FALLBACK));
      rc.setOptions(EnumSet.of(PKIXRevocationChecker.Option.ONLY_END_ENTITY));
      rc.setOptions(EnumSet.of(PKIXRevocationChecker.Option.PREFER_CRLS));
      params.addCertPathChecker(rc);
      
      // now it revokes the very same list of certificates
      X509CRL crl = CertUtils.getCRL(anchor);// supposed valid CRL
      List list = new ArrayList();list.add(crl);
      CertStoreParameters csp = new CollectionCertStoreParameters(list);
      CertStore store = CertStore.getInstance("Collection", csp);
      params.addCertStore(store);
      
      // validate certification path with specified params
      PKIXCertPathValidatorResult cpvr
              = (PKIXCertPathValidatorResult) cpv.validate(cp, params);
      PolicyNode policyTree = cpvr.getPolicyTree();
      PublicKey subjectPK = cpvr.getPublicKey();
      System.out.println("Certificate Chain successfully validated");
      System.out.println(subjectPK);

    } catch (CertificateException | 
             InvalidAlgorithmParameterException | 
             NoSuchAlgorithmException | 
             CertPathValidatorException e) {
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
