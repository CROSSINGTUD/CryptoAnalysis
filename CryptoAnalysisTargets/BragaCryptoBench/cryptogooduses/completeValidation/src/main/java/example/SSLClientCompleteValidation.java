package example;

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

public final class SSLClientCompleteValidation {

	public static void main(String[] args) throws Exception {
		SSLSocket socket = null;
		boolean ok = true;
		try {
			SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			socket = (SSLSocket) factory.createSocket("www.google.com", 443);
			socket.startHandshake();

			SSLSession session = socket.getSession();
			Principal peerPrincipal = session.getPeerPrincipal();

			Certificate[] peerCertificates = session.getPeerCertificates();
			if (peerCertificates != null && peerCertificates.length >= 2) {

				((X509Certificate) peerCertificates[0]).checkValidity();

				peerCertificates[0].verify(peerCertificates[1].getPublicKey());
			} else {
				throw new CertificateException("Unable to verify certificate");
			}

			Certificate[] certs = session.getPeerCertificates();
			X509Certificate[] x509certs = new X509Certificate[certs.length - 1];
			for (int i = 0; i < certs.length - 1; i++) {
				x509certs[i] = (X509Certificate) certs[i];
			}
			X509Certificate anchor = (X509Certificate) certs[certs.length - 1];

			List l = Arrays.asList(x509certs);
			CertificateFactory cf = CertificateFactory.getInstance("X.509", "SUN");
			CertPath cp = cf.generateCertPath(l);

			TrustAnchor ta = new TrustAnchor(anchor, null);
			PKIXParameters params = new PKIXParameters(Collections.singleton(ta));

			CertPathValidator cpv = CertPathValidator.getInstance("PKIX", "SUN");

			PKIXRevocationChecker rc = (PKIXRevocationChecker) cpv.getRevocationChecker();
			rc.setOptions(EnumSet.of(PKIXRevocationChecker.Option.SOFT_FAIL));
			rc.setOptions(EnumSet.of(PKIXRevocationChecker.Option.NO_FALLBACK));
			rc.setOptions(EnumSet.of(PKIXRevocationChecker.Option.ONLY_END_ENTITY));
			rc.setOptions(EnumSet.of(PKIXRevocationChecker.Option.PREFER_CRLS));
			params.addCertPathChecker(rc);

			X509CRL crl = null;
			List list = new ArrayList();
			list.add(crl);
			CertStoreParameters csp = new CollectionCertStoreParameters(list);
			CertStore store = CertStore.getInstance("Collection", csp);
			params.addCertStore(store);

			PKIXCertPathValidatorResult cpvr = (PKIXCertPathValidatorResult) cpv.validate(cp, params);
			PolicyNode policyTree = cpvr.getPolicyTree();
			PublicKey subjectPK = cpvr.getPublicKey();

		} catch (CertificateException | InvalidAlgorithmParameterException | NoSuchAlgorithmException
				| CertPathValidatorException e) {
			ok = false;
		}
	}
}
