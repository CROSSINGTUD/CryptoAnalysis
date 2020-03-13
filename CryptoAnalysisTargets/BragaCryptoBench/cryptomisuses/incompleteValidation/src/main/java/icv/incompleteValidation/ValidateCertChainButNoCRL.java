package icv.incompleteValidation;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public final class ValidateCertChainButNoCRL {

	private static final int oneSecond = 1000;
	private static final int oneMinute = oneSecond * 60;
	private static final int oneHour = oneMinute * 60;
	private static final int oneDay = oneHour * 24;
	private static final int oneWeek = oneDay * 7;
	private static final int validity = oneWeek;

	private static int serialNumberCounter = 1;

	public static void main(String[] args) {
		Security.addProvider(new BouncyCastleProvider());
		try {
			List certList = getCertificateList();
			CertificateFactory cf = CertificateFactory.getInstance("X.509", "SUN");
			CertPath cp = cf.generateCertPath(certList);

			TrustAnchor a = new TrustAnchor(rootCA, null);
			PKIXParameters params = new PKIXParameters(Collections.singleton(a));

			params.setRevocationEnabled(false);

			CertPathValidator cpv = CertPathValidator.getInstance("PKIX", "SUN");

			PKIXCertPathValidatorResult cpvr = (PKIXCertPathValidatorResult) cpv.validate(cp, params);
		} catch (InvalidAlgorithmParameterException | CertPathValidatorException | CertificateException
				| NoSuchAlgorithmException e) {
		} catch (Exception e) {
		}
	}

	static KeyPair rootKP;
	static X509Certificate rootCA;

	static List getCertificateList() throws Exception {
		getRootCert();
		KeyPair mkp1 = genRSAKeyPair();
		X509Certificate middle1 = buildMiddleCert(mkp1.getPublic(), "CN=Intermediate CA Certificate",
				rootKP.getPrivate(), rootCA);

		KeyPair mkp2 = genRSAKeyPair();
		X509Certificate middle2 = buildMiddleCert(mkp2.getPublic(), "CN=Intermediate CA Certificate", mkp1.getPrivate(),
				middle1);

		KeyPair ekp = genRSAKeyPair();
		X509Certificate user = buildEndCert(ekp.getPublic(), "CN=End User Certificate", mkp2.getPrivate(), middle2);
		Certificate[] certs = new Certificate[] { user, middle2, middle1, rootCA };
		List certList = Arrays.asList(certs);
		return certList;
	}

	static X509Certificate getRootCert() throws Exception {
		if (rootCA == null) {
			rootKP = genRSAKeyPair();
			rootCA = buildSelfSignedCert(rootKP);
		}
		return rootCA;
	}

	public static KeyPair genRSAKeyPair() throws Exception {
		KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
		kpGen.initialize(2048, new SecureRandom());
		return kpGen.generateKeyPair();
	}

	public static X509Certificate buildMiddleCert(PublicKey pk, String cn, PrivateKey caKey, X509Certificate cac)
			throws NoSuchAlgorithmException, CertIOException, OperatorCreationException, CertificateException {
		X509Certificate cert = null;

		X509v3CertificateBuilder cb = new JcaX509v3CertificateBuilder(cac.getSubjectX500Principal(),
				BigInteger.valueOf(serialNumberCounter++), new Date(System.currentTimeMillis()),
				new Date(System.currentTimeMillis() + validity), new X500Principal(cn), pk);
		JcaX509ExtensionUtils utils;
		utils = new JcaX509ExtensionUtils();

		cb.addExtension(Extension.authorityKeyIdentifier, false, utils.createAuthorityKeyIdentifier(cac));
		cb.addExtension(Extension.subjectKeyIdentifier, false, utils.createSubjectKeyIdentifier(pk));
		cb.addExtension(Extension.basicConstraints, true, new BasicConstraints(0));
		cb.addExtension(Extension.keyUsage, true,
				new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign | KeyUsage.cRLSign));

		JcaContentSignerBuilder jcsb = new JcaContentSignerBuilder("SHA256withRSA");
		jcsb.setProvider("BC");
		ContentSigner signer = jcsb.build(caKey);

		JcaX509CertificateConverter jX509c = new JcaX509CertificateConverter();
		jX509c.setProvider("BC");
		cert = jX509c.getCertificate(cb.build(signer));

		return cert;
	}

	public static X509Certificate buildEndCert(PublicKey pk, String cn, PrivateKey caKey, X509Certificate ca) {

		X509Certificate cert = null;
		try {
			X509v3CertificateBuilder cb = new JcaX509v3CertificateBuilder(ca.getSubjectX500Principal(),
					BigInteger.valueOf(serialNumberCounter++), new Date(System.currentTimeMillis()),
					new Date(System.currentTimeMillis() + validity), new X500Principal(cn), pk);

			JcaX509ExtensionUtils utils;

			utils = new JcaX509ExtensionUtils();

			cb.addExtension(Extension.authorityKeyIdentifier, false, utils.createAuthorityKeyIdentifier(ca));
			cb.addExtension(Extension.subjectKeyIdentifier, false, utils.createSubjectKeyIdentifier(pk));
			cb.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));
			cb.addExtension(Extension.keyUsage, true,
					new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));

			JcaContentSignerBuilder jcsb = new JcaContentSignerBuilder("SHA256withRSA");
			jcsb.setProvider("BC");
			ContentSigner signer = jcsb.build(caKey);

			JcaX509CertificateConverter jX509c = new JcaX509CertificateConverter();
			jX509c.setProvider("BC");
			cert = jX509c.getCertificate(cb.build(signer));
		} catch (NoSuchAlgorithmException | CertIOException | OperatorCreationException | CertificateException ex) {
		}
		return cert;
	}

	public static X509Certificate buildSelfSignedCert(KeyPair keyPair) throws Exception {
		X509v1CertificateBuilder certBldr = new JcaX509v1CertificateBuilder(new X500Name("CN=Root Certificate"),
				BigInteger.valueOf(1), new Date(System.currentTimeMillis()),
				new Date(System.currentTimeMillis() + oneWeek), new X500Name("CN=Root Certificate"),
				keyPair.getPublic());

		JcaContentSignerBuilder jcsb = new JcaContentSignerBuilder("SHA256withRSA");
		jcsb.setProvider("BC");
		ContentSigner signer = jcsb.build(keyPair.getPrivate());

		JcaX509CertificateConverter jX509c = new JcaX509CertificateConverter();
		jX509c.setProvider("BC");
		X509Certificate cert = jX509c.getCertificate(certBldr.build(signer));
		return cert;

	}

}
