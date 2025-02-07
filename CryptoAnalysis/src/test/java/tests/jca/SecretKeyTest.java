package tests.jca;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.DestroyFailedException;
import org.junit.Test;
import test.TestConstants;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class SecretKeyTest extends UsagePatternTestingFramework {

    @Override
    protected String getRulesetPath() {
        return TestConstants.JCA_RULESET_PATH;
    }

    @Test
    public void test() throws GeneralSecurityException {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        SecretKey key = generator.generateKey();

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
    }

    @Test
    public void test2() {
        byte[] bytes = new byte[32];

        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);

        SecureRandom random1 = new SecureRandom();
        random1.setSeed(bytes);
    }

    @Test
    public void test3() throws GeneralSecurityException {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        SecretKey key = generator.generateKey();
        Assertions.hasEnsuredPredicate(key);

        byte[] bytes = key.getEncoded();
        Assertions.hasEnsuredPredicate(bytes);
        SecretKeySpec spec = new SecretKeySpec(bytes, "AES");
    }

    @Test
    public void secretKeyUsagePatternTestReqPredOr() throws GeneralSecurityException {
        SecureRandom secRand = new SecureRandom();
        Assertions.hasEnsuredPredicate(secRand);

        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        Assertions.extValue(0);
        keygen.init(128, secRand);
        Assertions.extValue(0);
        SecretKey key = keygen.generateKey();
        Assertions.hasEnsuredPredicate(key);
    }

    @Test
    public void secretKeyUsagePatternTest1Simple() throws GeneralSecurityException {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        Assertions.extValue(0);
        keygen.init(128);
        Assertions.extValue(0);
        SecretKey key = keygen.generateKey();
        Assertions.hasEnsuredPredicate(key);
        Assertions.mustBeInAcceptingState(keygen);
    }

    @Test
    public void secretKeyUsagePattern2() throws GeneralSecurityException {
        final byte[] salt = new byte[32];
        SecureRandom.getInstanceStrong().nextBytes(salt);

        char[] corPwd = generateRandomPassword();
        final PBEKeySpec pbekeyspec = new PBEKeySpec(corPwd, salt, 65000, 128);
        Assertions.extValue(1);
        Assertions.extValue(2);
        Assertions.extValue(3);
        Assertions.hasEnsuredPredicate(pbekeyspec);
        Assertions.mustNotBeInAcceptingState(pbekeyspec);

        final SecretKeyFactory secFac = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        Assertions.extValue(0);

        final Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
        Assertions.extValue(0);

        SecretKey tmpKey = secFac.generateSecret(pbekeyspec);
        Assertions.mustBeInAcceptingState(secFac);
        pbekeyspec.clearPassword();

        byte[] keyMaterial = tmpKey.getEncoded();
        final SecretKeySpec actKey = new SecretKeySpec(keyMaterial, "AES");
        Assertions.extValue(1);
        Assertions.hasEnsuredPredicate(actKey);

        c.init(Cipher.ENCRYPT_MODE, actKey);
        Assertions.extValue(0);
        Assertions.mustBeInAcceptingState(actKey);

        byte[] encText = c.doFinal("TEST_PLAIN".getBytes(StandardCharsets.UTF_8));
        Assertions.hasEnsuredPredicate(encText);
        c.getIV();

        Assertions.mustBeInAcceptingState(c);
    }

    @Test
    public void secretKeyUsagePattern3() throws GeneralSecurityException {
        final byte[] salt = new byte[32];
        SecureRandom.getInstanceStrong().nextBytes(salt);

        final PBEKeySpec pbekeyspec = new PBEKeySpec(generateRandomPassword(), salt, 65000, 128);
        Assertions.extValue(2);
        Assertions.extValue(3);
        Assertions.mustNotBeInAcceptingState(pbekeyspec);

        final SecretKeyFactory secFac = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        Assertions.extValue(0);

        SecretKey tmpKey = secFac.generateSecret(pbekeyspec);
        Assertions.mustBeInAcceptingState(secFac);
        pbekeyspec.clearPassword();

        byte[] keyMaterial = tmpKey.getEncoded();
        final SecretKeySpec actKey = new SecretKeySpec(keyMaterial, "AES");
        Assertions.extValue(1);
        Assertions.hasEnsuredPredicate(actKey);
    }

    public char[] generateRandomPassword() {
        SecureRandom rnd = new SecureRandom();

        return IntStream.generate(() -> rnd.nextInt('a', 'z'))
                .mapToObj(Character::toString)
                .limit(10)
                .collect(Collectors.joining())
                .toCharArray();
    }

    @Test
    public void clearPasswordPredicateTest() throws GeneralSecurityException {
        Encryption encryption = new Encryption();
        byte[] encrypted = encryption.encryptData(new byte[] {}, "Test");
        System.out.println(Arrays.toString(encrypted));
    }

    public static class Encryption {
        byte[] salt = {15, -12, 94, 0, 12, 3, -65, 73, -1, -84, -35};

        private SecretKey generateKey(String password) throws GeneralSecurityException {
            PBEKeySpec pBEKeySpec = new PBEKeySpec(password.toCharArray(), salt, 10000, 256);

            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithSHA256");
            Assertions.notHasEnsuredPredicate(pBEKeySpec);
            SecretKey generateSecret = secretKeyFactory.generateSecret(pBEKeySpec);
            Assertions.notHasEnsuredPredicate(generateSecret);
            byte[] keyMaterial = generateSecret.getEncoded();
            Assertions.notHasEnsuredPredicate(keyMaterial);
            SecretKey encryptionKey = new SecretKeySpec(keyMaterial, "AES");
            // pBEKeySpec.clearPassword();
            Assertions.notHasEnsuredPredicate(encryptionKey);
            return encryptionKey;
        }

        private byte[] encrypt(byte[] plainText, SecretKey encryptionKey)
                throws GeneralSecurityException {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey);
            return cipher.doFinal(plainText);
        }

        public byte[] encryptData(byte[] plainText, String password)
                throws GeneralSecurityException {
            return encrypt(plainText, generateKey(password));
        }
    }

    @Test
    public void clearPasswordPredicateTest2() throws GeneralSecurityException {
        String password = "test";
        byte[] salt = {15, -12, 94, 0, 12, 3, -65, 73, -1, -84, -35};
        PBEKeySpec pBEKeySpec = new PBEKeySpec(password.toCharArray(), salt, 10000, 256);

        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithSHA256");
        Assertions.extValue(0);
        Assertions.notHasEnsuredPredicate(pBEKeySpec);
        SecretKey generateSecret = secretKeyFactory.generateSecret(pBEKeySpec);
        Assertions.notHasEnsuredPredicate(generateSecret);
        byte[] keyMaterial = generateSecret.getEncoded();
        Assertions.notHasEnsuredPredicate(keyMaterial);
    }

    @Test
    public void secretKeyTest4() throws NoSuchAlgorithmException, DestroyFailedException {
        KeyGenerator c = KeyGenerator.getInstance("AES");
        SecretKey key = c.generateKey();
        Assertions.mustBeInAcceptingState(key);
        byte[] enc = key.getEncoded();
        Assertions.mustBeInAcceptingState(key);
        enc = key.getEncoded();
        Assertions.hasEnsuredPredicate(enc);

        Assertions.mustBeInAcceptingState(key);
        key.destroy();
        Assertions.mustBeInAcceptingState(key);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void setEntryKeyStore() throws GeneralSecurityException, IOException {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null, null);
        Assertions.mustBeInAcceptingState(keyStore);

        // Add private and public key (certificate) to keystore
        keyStore.setEntry("alias", null, null);
        keyStore.store(null, "Password".toCharArray());
        Assertions.mustBeInAcceptingState(keyStore);
    }

    @Test
    public void secretKeyUsagePatternTest5() throws GeneralSecurityException {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        Assertions.extValue(0);
        keygen.init(1);
        Assertions.extValue(0);
        SecretKey key = keygen.generateKey();
        Assertions.notHasEnsuredPredicate(key);
        Assertions.mustBeInAcceptingState(keygen);
        Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Assertions.extValue(0);
        cCipher.init(Cipher.ENCRYPT_MODE, key);
        Assertions.extValue(0);

        byte[] encText = cCipher.doFinal("".getBytes());
        Assertions.notHasEnsuredPredicate(encText);
        Assertions.mustBeInAcceptingState(cCipher);
    }

    @Test
    public void secretKeyUsagePatternTest6() throws GeneralSecurityException {
        Encryptor enc = new Encryptor();
        byte[] encText = enc.encrypt("Test");
        Assertions.notHasEnsuredPredicate(encText);
    }

    public static class Encryptor {

        Cipher cipher;

        public Encryptor() throws GeneralSecurityException {
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            keygen.init(128);
            SecretKey key = keygen.generateKey();
            this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            this.cipher.init(Cipher.ENCRYPT_MODE, key);
            this.cipher.getIV();
        }

        public byte[] encrypt(String plainText) throws GeneralSecurityException {
            byte[] encText = this.cipher.doFinal(plainText.getBytes());
            Assertions.hasEnsuredPredicate(encText);
            return encText;
        }
    }

    @Test
    public void secretKeyUsagePattern7() throws GeneralSecurityException {
        final byte[] salt = new byte[32];
        SecureRandom.getInstanceStrong().nextBytes(salt);

        char[] falsePwd = "password".toCharArray();
        final PBEKeySpec pbekeyspec = new PBEKeySpec(falsePwd, salt, 65000, 128);
        Assertions.extValue(0);
        Assertions.extValue(1);
        Assertions.extValue(2);
        Assertions.extValue(3);
        Assertions.mustNotBeInAcceptingState(pbekeyspec);

        final SecretKeyFactory secFac = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        final Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
        Assertions.extValue(0);

        SecretKey tmpKey = secFac.generateSecret(pbekeyspec);
        pbekeyspec.clearPassword();

        byte[] keyMaterial = tmpKey.getEncoded();
        final SecretKeySpec actKey = new SecretKeySpec(keyMaterial, "AES");
        Assertions.extValue(1);
        Assertions.notHasEnsuredPredicate(actKey);

        c.init(Cipher.ENCRYPT_MODE, actKey);
        Assertions.extValue(0);
        Assertions.mustBeInAcceptingState(actKey);

        byte[] encText = c.doFinal("TEST_PLAIN".getBytes(StandardCharsets.UTF_8));
        Assertions.notHasEnsuredPredicate(encText);

        c.getIV();
        Assertions.mustBeInAcceptingState(c);
    }

    @Test
    public void exceptionFlowTest() {
        KeyGenerator keygen;
        try {
            keygen = KeyGenerator.getInstance("AES");
            Assertions.extValue(0);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error while generating key");
            return;
        }
        keygen.init(128);
        Assertions.extValue(0);
        SecretKey key = keygen.generateKey();
        Assertions.mustBeInAcceptingState(keygen);
        Assertions.hasEnsuredPredicate(key);
    }

    @Test
    public void secretKeyUsagePatternTestConfigFile() throws GeneralSecurityException, IOException {
        List<String> s = Files.readAllLines(Paths.get("../../../resources/config.txt"));
        KeyGenerator keygen = KeyGenerator.getInstance(s.get(0));
        keygen.init(128);
        Assertions.extValue(0);
        SecretKey key = keygen.generateKey();
        Assertions.notHasEnsuredPredicate(key);
        Assertions.mustBeInAcceptingState(keygen);

        Assertions.impreciseValueExtractionErrors(1);
    }
}
