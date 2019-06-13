package example.predictablekeystorepassword;

import java.io.IOException;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class PredictableKeyStorePasswordABSCase1 {
    CryptoPredictableKeyStorePassword1 crypto;
    public PredictableKeyStorePasswordABSCase1() {
        String key = "changeit";
        crypto = new CryptoPredictableKeyStorePassword1(key);
    }
}

class CryptoPredictableKeyStorePassword1 {
    String defKey;
    URL cacerts;

    public CryptoPredictableKeyStorePassword1(String key){
        defKey = key;
    }

    public void encrypt(String passedKey) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {

        passedKey = defKey;

        String type = "JKS";
        KeyStore ks = KeyStore.getInstance(type);
        cacerts = new URL("https://www.google.com");
        ks.load(cacerts.openStream(), passedKey.toCharArray());
    }
}
