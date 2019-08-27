package icv.incompleteValidation;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.PolicyNode;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import _utils.CertUtils;

public final class ValidateCertChainButNoCRL {

  public static void main(String[] args) {
    Security.addProvider(new BouncyCastleProvider());
    try {
    // Step 1. Obtain CA root certs and the certification path to be validated.
      List certList = getCertificateList();
      // instantiate a CertificateFactory for X.509
      CertificateFactory cf = CertificateFactory.getInstance("X.509","SUN");
      // extract cert path from the List of Certificates
      CertPath cp = cf.generateCertPath(certList); 

    // Step 2. Create a PKIXParameters with the trust anchors.
      TrustAnchor a = new TrustAnchor(rootCA,null);
      PKIXParameters params = new PKIXParameters(Collections.singleton(a));
      
      // Here is the misuse instantiated by desabling the CRL 
      params.setRevocationEnabled(false); //DANGER:Desable revocation checks! 
      
    // Step 3. Use a CertPathValidator to validate the certificate path.
      // create CertPathValidator that implements the "PKIX" algorithm
      CertPathValidator cpv = CertPathValidator.getInstance("PKIX","SUN");
      
      
      // validate certification path "cp" with specified params
      PKIXCertPathValidatorResult cpvr = 
              (PKIXCertPathValidatorResult) cpv.validate(cp, params);
      //PolicyNode policyTree = cpvr.getPolicyTree();
      System.out.println("Certificate Chain successfully validated");
      //System.out.println(cpvr.getPublicKey());
    } catch (InvalidAlgorithmParameterException 
            | CertPathValidatorException 
            | CertificateException 
            | NoSuchAlgorithmException e) {
      System.out.println(e);
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // keys and cert for root CA
  static KeyPair rootKP;
  static X509Certificate rootCA;
  
  static List getCertificateList() throws Exception {
      getRootCert();
      // generate intermediate (middle) certificate
      KeyPair mkp1 = CertUtils.genRSAKeyPair();
      X509Certificate middle1 = CertUtils.buildMiddleCert(
              mkp1.getPublic(), "CN=Intermediate CA Certificate",
              rootKP.getPrivate(), rootCA);
      
      KeyPair mkp2 = CertUtils.genRSAKeyPair();
      X509Certificate middle2 =CertUtils.buildMiddleCert(
              mkp2.getPublic(), "CN=Intermediate CA Certificate",
              mkp1.getPrivate(), middle1);
      
      // generate end entity certificate
      KeyPair ekp =CertUtils.genRSAKeyPair();
      X509Certificate user = CertUtils.buildEndCert(
              ekp.getPublic(), "CN=End User Certificate",
              mkp2.getPrivate(), middle2);
    Certificate[] certs = new Certificate[]{user,middle2,middle1,rootCA};
    List certList = Arrays.asList(certs);
    return certList;
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
