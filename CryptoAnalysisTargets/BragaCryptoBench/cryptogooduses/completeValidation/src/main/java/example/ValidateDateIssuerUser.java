package example;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Date;
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

public final class ValidateDateIssuerUser {

	private static final int oneSecond = 1000;
	private static final int oneMinute = oneSecond * 60;
	private static final int oneHour = oneMinute * 60;
	private static final int oneDay = oneHour * 24;
	private static final int oneWeek = oneDay * 7;
	private static final int validity = oneWeek;

	private static int serialNumberCounter = 1;

	public static boolean validate(X509Certificate cert, X509Certificate ca, X500Principal issuer, X500Principal subj,
			Date date) {
		boolean ok = false;
		try {
			if (date != null) {
				cert.checkValidity(date);
			} else {
				cert.checkValidity();
			}
			ok = true;
		} catch (CertificateExpiredException | CertificateNotYetValidException ex) {
			ok = false;
		}

		if (ok) {
			try {
				ok = false;
				cert.verify(ca.getPublicKey());
				ok = true;
			} catch (CertificateException ex) {
				ok = false;
			} catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchProviderException | SignatureException ex) {
				ok = false;
			}
		}

		if (ok) {
			ok = false;
			if (cert.getIssuerX500Principal().equals(issuer)) {
				ok = true;
			} else {
				ok = false;
			}

			if (ok && cert.getSubjectX500Principal().equals(subj)) {
				ok = true;
			} else {
				ok = false;
			}
		}
		return ok;
	}

	public static void main(String[] args) {
		Security.addProvider(new BouncyCastleProvider());
		try {
			KeyPair rkp = genRSAKeyPair();
			X509Certificate root = buildSelfSignedCert(rkp);

			KeyPair mkp = genRSAKeyPair();
			X509Certificate middle = buildMiddleCert(mkp.getPublic(), "CN=Intermediate CA Certificate",
					rkp.getPrivate(), root);

			KeyPair ekp = genRSAKeyPair();
			X509Certificate user = buildEndCert(ekp.getPublic(), "CN=End User Certificate", mkp.getPrivate(), middle);

			X500Principal issuer = new X500Principal("CN=Root Certificate");
			X500Principal subj1 = new X500Principal("CN=Intermediate CA Certificate");
			X500Principal subj2 = new X500Principal("CN=End User Certificate");

			boolean ok = false;

			ok = validate(user, middle, subj1, subj2, null);

		} catch (Exception ex) {
		}
	}

	public static KeyPair genRSAKeyPair() throws Exception {
		KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
		kpGen.initialize(2048, new SecureRandom());
		return kpGen.generateKeyPair();
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

	public static X509Certificate buildEndCert(PublicKey pk, String cn, PrivateKey caKey, X509Certificate ca) throws NoSuchAlgorithmException, CertIOException, OperatorCreationException, CertificateException {

		X509Certificate cert = null;

		X509v3CertificateBuilder cb = new JcaX509v3CertificateBuilder(ca.getSubjectX500Principal(),
				BigInteger.valueOf(serialNumberCounter++), new Date(System.currentTimeMillis()),
				new Date(System.currentTimeMillis() + validity), new X500Principal(cn), pk);

		JcaX509ExtensionUtils utils;

		utils = new JcaX509ExtensionUtils();

		cb.addExtension(Extension.authorityKeyIdentifier, false, utils.createAuthorityKeyIdentifier(ca));
		cb.addExtension(Extension.subjectKeyIdentifier, false, utils.createSubjectKeyIdentifier(pk));
		cb.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));
		cb.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));

		JcaContentSignerBuilder jcsb = new JcaContentSignerBuilder("SHA256withRSA");
		jcsb.setProvider("BC");
		ContentSigner signer = jcsb.build(caKey);

		JcaX509CertificateConverter jX509c = new JcaX509CertificateConverter();
		jX509c.setProvider("BC");
		cert = jX509c.getCertificate(cb.build(signer));

		return cert;
	}
}
