package _utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Date;
import javax.net.ssl.SSLSocket;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v1CertificateBuilder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v1CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

/**
 *
 * @author Alexandre
 */
public final class CertUtils {
  
    
  public static KeyPair genRSAKeyPair() throws Exception {
    KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
    kpGen.initialize(2048, new SecureRandom());
    return kpGen.generateKeyPair();
  }

  public static X509Certificate buildSelfSignedCert(KeyPair keyPair) 
          throws Exception{
    X509v1CertificateBuilder certBldr = new JcaX509v1CertificateBuilder(
            new X500Name("CN=Root Certificate"),
            BigInteger.valueOf(1),
            new Date(System.currentTimeMillis()),
            new Date(System.currentTimeMillis() + oneWeek),
            new X500Name("CN=Root Certificate"),
            keyPair.getPublic());

    // this is how to actually sign a certificate
    JcaContentSignerBuilder jcsb = new JcaContentSignerBuilder("SHA256withRSA");
    jcsb.setProvider("BC");
    ContentSigner signer = jcsb.build(keyPair.getPrivate());

    // this is necessary to convert BC objects to JCA objects
    JcaX509CertificateConverter jX509c = new JcaX509CertificateConverter();
    jX509c.setProvider("BC");
    X509Certificate cert = jX509c.getCertificate(certBldr.build(signer));
    return cert;

  }

  //Build a sample V3 certificate to use as an intermediate CA certificate
  public static X509Certificate buildMiddleCert(PublicKey pk, String cn,
          PrivateKey caKey,
          X509Certificate cac) {
    X509Certificate cert = null;
    try {
      X509v3CertificateBuilder cb = new JcaX509v3CertificateBuilder(
              cac.getSubjectX500Principal(),
              BigInteger.valueOf(serialNumberCounter++),
              new Date(System.currentTimeMillis()),
              new Date(System.currentTimeMillis() + validity),
              new X500Principal(cn),
              pk);
      JcaX509ExtensionUtils utils;
      utils = new JcaX509ExtensionUtils();

      cb.addExtension(Extension.authorityKeyIdentifier, false,
              utils.createAuthorityKeyIdentifier(cac));
      cb.addExtension(Extension.subjectKeyIdentifier, false,
              utils.createSubjectKeyIdentifier(pk));
      cb.addExtension(Extension.basicConstraints,true,new BasicConstraints(0));
      cb.addExtension(Extension.keyUsage, true, new KeyUsage(
              KeyUsage.digitalSignature|KeyUsage.keyCertSign|KeyUsage.cRLSign));

      // this is how to actually sign a certificate
      JcaContentSignerBuilder jcsb = new JcaContentSignerBuilder("SHA256withRSA");
      jcsb.setProvider("BC");
      ContentSigner signer = jcsb.build(caKey);

      // this is necessary to convert BC objects to JCA objects
      JcaX509CertificateConverter jX509c = new JcaX509CertificateConverter();
      jX509c.setProvider("BC");
      cert = jX509c.getCertificate(cb.build(signer));
    } 
    catch (NoSuchAlgorithmException|CertIOException|OperatorCreationException 
          |CertificateException ex) {System.out.println(ex);}
    return cert;
  }

  //Build a sample V3 certificate to use as an end entity certificate
  public static X509Certificate buildEndCert(PublicKey pk, String cn,
                                         PrivateKey caKey,X509Certificate ca) {
    
    X509Certificate cert = null;
    try {
      X509v3CertificateBuilder cb = new JcaX509v3CertificateBuilder(
              ca.getSubjectX500Principal(),
              BigInteger.valueOf(serialNumberCounter++),
              new Date(System.currentTimeMillis()),
              new Date(System.currentTimeMillis() + validity),
              new X500Principal(cn),
              pk);

      JcaX509ExtensionUtils utils;

      utils = new JcaX509ExtensionUtils();

      cb.addExtension(Extension.authorityKeyIdentifier, false,
              utils.createAuthorityKeyIdentifier(ca));
      cb.addExtension(Extension.subjectKeyIdentifier, false,
              utils.createSubjectKeyIdentifier(pk));
      cb.addExtension(Extension.basicConstraints, true, 
                                                   new BasicConstraints(false));
      cb.addExtension(Extension.keyUsage, true,
              new KeyUsage(KeyUsage.digitalSignature|KeyUsage.keyEncipherment));

      // this is how to actually sign a certificate
      JcaContentSignerBuilder jcsb = 
                                   new JcaContentSignerBuilder("SHA256withRSA");
      jcsb.setProvider("BC");
      ContentSigner signer = jcsb.build(caKey);

      // this is necessary to convert BC objects to JCA objects
      JcaX509CertificateConverter jX509c = new JcaX509CertificateConverter();
      jX509c.setProvider("BC");
      cert = jX509c.getCertificate(cb.build(signer));
    } catch (NoSuchAlgorithmException | CertIOException 
            |OperatorCreationException | CertificateException ex) {
      System.out.println(ex);
    }
    return cert;
  }
  
  private static final int oneSecond = 1000;
  private static final int oneMinute = oneSecond * 60;
  private static final int oneHour   = oneMinute * 60;
  private static final int oneDay    = oneHour * 24;
  private static final int oneWeek   = oneDay * 7;
  private static final int validity  = oneWeek;
  public static final String middleCN = "CN=Intermediate CA Certificate";
  public static final String endUserCN = "CN=End User Certificate";
  
  private static int serialNumberCounter = 1;

  public static void handleSocket(SSLSocket socket) {
    try {
      if (socket != null) {
        try (final PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())))) {
          out.println("GET / HTTP/1.0");
          out.println();
          out.flush();
          if (out.checkError()) {
            System.out.println("SSLSocketClient:  java.io.PrintWriter error");
          }
          try (final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
              System.out.println(inputLine);
            }
          }
        }
        socket.close();
      }
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  public static X509CRL getCRL(X509Certificate ca) {
    X509CRL crl = null; // this is a stub !!!!!
    return crl;
  }
}
