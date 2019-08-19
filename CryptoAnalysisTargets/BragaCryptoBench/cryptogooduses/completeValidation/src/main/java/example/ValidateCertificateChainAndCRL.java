package example;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.PKIXRevocationChecker;
import java.security.cert.PKIXRevocationChecker.Option;
import java.security.cert.PolicyNode;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import example._utils.*;

public final class ValidateCertificateChainAndCRL {

  public static void main(String[] args) {
    Security.addProvider(new BouncyCastleProvider());
    try {
    // Step 1. Obtain CA root certs and the certification path to be validated
      X509Certificate[] certs = getCertificateList();
      List l = Arrays.asList(certs);
      CertificateFactory cf = CertificateFactory.getInstance("X.509","SUN");
      CertPath cp = cf.generateCertPath(l);

    // Step 2. Create a PKIXParameters with the trust anchors
      TrustAnchor ta = new TrustAnchor(rootCA,null);
      PKIXParameters params = new PKIXParameters(Collections.singleton(ta));
      
    // Step 3. Use a CertPathValidator to validate the certificate path
      CertPathValidator cpv = CertPathValidator.getInstance("PKIX","SUN");
      PKIXRevocationChecker rc = 
              (PKIXRevocationChecker)cpv.getRevocationChecker();
      rc.setOptions(EnumSet.of(Option.SOFT_FAIL));
      rc.setOptions(EnumSet.of(Option.NO_FALLBACK));
      rc.setOptions(EnumSet.of(Option.ONLY_END_ENTITY));
      rc.setOptions(EnumSet.of(Option.PREFER_CRLS));
      params.addCertPathChecker(rc);
      
      // now it revokes the very same list of certificates
      X509CRL crl = CertUtils.revokeCertList(rootCA,rootKP.getPrivate(),certs);
      List list = new ArrayList();list.add(crl);
      CertStoreParameters csp = new CollectionCertStoreParameters(list);
      CertStore store = CertStore.getInstance("Collection", csp);
      params.addCertStore(store);
      //params.setRevocationEnabled(false);
      //params.setRevocationEnabled(true);
     
      // validate certification path with specified params
      PKIXCertPathValidatorResult cpvr = 
              (PKIXCertPathValidatorResult) cpv.validate(cp, params);
      PolicyNode policyTree = cpvr.getPolicyTree();
      PublicKey subjectPK = cpvr.getPublicKey();
      System.out.println("Certificate Chain successfully validated");
      //System.out.println(subjectPK);
    } 
    catch (InvalidAlgorithmParameterException 
            | CertPathValidatorException 
            | CertificateException 
            | NoSuchAlgorithmException e) { System.out.println(e);} 
    catch (Exception e)                   { System.out.println(e);}
  }

  // keys and cert for root CA
  static KeyPair rootKP;
  static X509Certificate rootCA;
  
  static X509Certificate[] getCertificateList() throws Exception {
      getRootCert();
      // generate intermediate (middle) certificate
      KeyPair mkp1 = CertUtils.genRSAKeyPair();
      X509Certificate middle1 = CertUtils.buildMiddleCert(
              mkp1.getPublic(), "CN=Intermediate CA Certificate",
              rootKP.getPrivate(), rootCA);
      
      KeyPair mkp2 = CertUtils.genRSAKeyPair();
      X509Certificate middle2 = CertUtils.buildMiddleCert(
              mkp2.getPublic(), "CN=Intermediate CA Certificate",
              mkp1.getPrivate(), middle1);
      
      // generate end entity certificate
      KeyPair ekp = CertUtils.genRSAKeyPair();
      X509Certificate user = CertUtils.buildEndCert(
              ekp.getPublic(), "CN=End User CA Certificate",
              mkp2.getPrivate(), middle2);
    X509Certificate[] certs = new X509Certificate[]{user,middle2,middle1};
    return certs;
  }

  static X509Certificate getRootCert() throws Exception {
    // create keys for root CA
    if (rootCA == null) {
      rootKP = CertUtils.genRSAKeyPair();
      // generate certificate
      rootCA = CertUtils.buildSelfSignedCert(rootKP);
    }
    return rootCA;  
  }

}
