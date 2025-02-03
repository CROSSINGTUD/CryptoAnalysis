package tests.jca;

import java.io.File;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Random;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;
import test.TestConstants;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class BouncyCastleTest extends UsagePatternTestingFramework {

    @Override
    protected String getRulesetPath() {
        return TestConstants.BOUNCY_CASTLE_RULESET_PATH;
    }

    @Test
    public void testEncryptTwo() throws InvalidCipherTextException {
        String edgeInput = "ff6f77206973207468652074696d6520666f7220616c6c20676f6f64206d656e";
        byte[] data = Hex.decode(edgeInput);

        RSAKeyParameters pubParameters = new RSAKeyParameters(false, null, null);
        Assertions.notHasEnsuredPredicate(pubParameters);

        AsymmetricBlockCipher eng = new RSAEngine();
        // missing init()
        // eng.init(true, pubParameters);
        byte[] cipherText = eng.processBlock(data, 0, data.length);
        Assertions.mustNotBeInAcceptingState(eng);
        Assertions.notHasEnsuredPredicate(cipherText);
    }

    @Test
    public void rsaKeyParameters() {
        BigInteger mod = new BigInteger("a0b8e8321b041acd40b7", 16);
        BigInteger pub = new BigInteger("499602D2", 16); // 1234567890
        BigInteger pri = new BigInteger("24CB016EA", 16); // 9876543210

        RSAKeyParameters privateParameters = new RSAKeyParameters(true, mod, pri);
        Assertions.mustBeInAcceptingState(privateParameters);
        Assertions.notHasEnsuredPredicate(privateParameters);

        RSAKeyParameters publicParameters = new RSAKeyParameters(false, mod, pub);
        Assertions.mustBeInAcceptingState(publicParameters);
        Assertions.hasEnsuredPredicate(publicParameters);
    }

    @Test
    public void testORingTwoPredicates1() throws GeneralSecurityException {
        BigInteger mod = new BigInteger("a0b8e8321b041acd40b7", 16);
        BigInteger pub = new BigInteger("499602D2", 16); // 1234567890
        BigInteger prv = new BigInteger("24CB016EA", 16); // 9876543210

        RSAKeyParameters params = new RSAKeyParameters(false, mod, pub);
        Assertions.mustBeInAcceptingState(params);
        Assertions.hasEnsuredPredicate(params);

        ParametersWithRandom randomParam1 = new ParametersWithRandom(params);
        Assertions.mustBeInAcceptingState(randomParam1);
        Assertions.hasEnsuredPredicate(randomParam1);

        Random randomGenerator = SecureRandom.getInstance("SHA1PRNG");
        Assertions.mustBeInAcceptingState(randomGenerator);
        Assertions.hasEnsuredPredicate(randomGenerator);

        BigInteger p = new BigInteger(1024, randomGenerator);
        BigInteger q = new BigInteger(1024, randomGenerator);
        BigInteger pExp =
                new BigInteger(
                        "1d1a2d3ca8e52068b3094d501c9a842fec37f54db16e9a67070a8b3f53cc03d4257ad252a1a640eadd603724d7bf3737914b544ae332eedf4f34436cac25ceb5",
                        16);
        BigInteger qExp =
                new BigInteger(
                        "6c929e4e81672fef49d9c825163fec97c4b7ba7acb26c0824638ac22605d7201c94625770984f78a56e6e25904fe7db407099cad9b14588841b94f5ab498dded",
                        16);
        BigInteger crtCoefficient =
                new BigInteger(
                        "dae7651ee69ad1d081ec5e7188ae126f6004ff39556bde90e0b870962fa7b926d070686d8244fe5a9aa709a95686a104614834b0ada4b10f53197a5cb4c97339",
                        16);

        RSAPrivateCrtKeyParameters privateParam =
                new RSAPrivateCrtKeyParameters(mod, pub, prv, p, q, pExp, qExp, crtCoefficient);
        Assertions.mustBeInAcceptingState(privateParam);
        Assertions.notHasEnsuredPredicate(
                privateParam); // because p & q are of type BigInteger which cannot ensure
        // randomized predicate

        ParametersWithRandom randomParam2 = new ParametersWithRandom(privateParam);
        Assertions.mustBeInAcceptingState(randomParam2);
        Assertions.notHasEnsuredPredicate(randomParam2);
    }

    @Test
    public void testORingTwoPredicates2() throws IllegalStateException, InvalidCipherTextException {
        SecureRandom random = new SecureRandom();
        byte[] genSeed = random.generateSeed(128);
        KeyParameter keyParam = new KeyParameter(genSeed);
        byte[] nonce = random.generateSeed(128);

        AEADParameters params = new AEADParameters(keyParam, 128, nonce);
        Assertions.hasEnsuredPredicate(params);
        Assertions.mustBeInAcceptingState(params);
        AESEngine engine = (AESEngine) AESEngine.newInstance();
        Assertions.hasEnsuredPredicate(engine);

        byte[] input = new byte[100];
        byte[] output = new byte[100];

        GCMBlockCipher cipher1 = (GCMBlockCipher) GCMBlockCipher.newInstance(engine);
        cipher1.init(false, params);
        cipher1.processAADBytes(input, 0, input.length);
        Assertions.hasEnsuredPredicate(cipher1);

        // Missing call to 'processBytes' causes TypestateError -> No predicate at 'doFinal' call
        cipher1.doFinal(output, 0);
        Assertions.notHasEnsuredPredicate(cipher1);
        Assertions.mustNotBeInAcceptingState(cipher1);

        ParametersWithIV ivParam = new ParametersWithIV(keyParam, genSeed);
        Assertions.hasEnsuredPredicate(ivParam);
        Assertions.mustBeInAcceptingState(ivParam);

        GCMBlockCipher cipher2 = (GCMBlockCipher) GCMBlockCipher.newInstance(engine);
        cipher2.init(false, ivParam);
        /* Missing:
         * cipher2.processAADBytes(input, 0, input.length);
         * cipher2.doFinal(output, 0);
         */
        Assertions.hasEnsuredPredicate(cipher2);
        Assertions.mustNotBeInAcceptingState(cipher2);
    }

    @Test
    public void testORingThreePredicates1() throws GeneralSecurityException {
        BigInteger mod = new BigInteger("a0b8e8321b041acd40b7", 16);
        BigInteger pub = new BigInteger("499602D2", 16);
        RSAKeyParameters params = new RSAKeyParameters(false, mod, pub);

        ParametersWithRandom randomParam1 = new ParametersWithRandom(params);
        Assertions.mustBeInAcceptingState(randomParam1);
        Assertions.hasEnsuredPredicate(randomParam1);

        BigInteger prv =
                new BigInteger(
                        "92e08f83cc9920746989ca5034dcb384a094fb9c5a6288fcc4304424ab8f56388f72652d8fafc65a4b9020896f2cde297080f2a540e7b7ce5af0b3446e1258d1dd7f245cf54124b4c6e17da21b90a0ebd22605e6f45c9f136d7a13eaac1c0f7487de8bd6d924972408ebb58af71e76fd7b012a8d0e165f3ae2e5077a8648e619",
                        16);
        Random randomGenerator = SecureRandom.getInstance("SHA1PRNG");
        Assertions.mustBeInAcceptingState(randomGenerator);
        Assertions.hasEnsuredPredicate(randomGenerator);
        BigInteger p = new BigInteger(1024, randomGenerator);
        BigInteger q = new BigInteger(1024, randomGenerator);
        BigInteger pExp =
                new BigInteger(
                        "1d1a2d3ca8e52068b3094d501c9a842fec37f54db16e9a67070a8b3f53cc03d4257ad252a1a640eadd603724d7bf3737914b544ae332eedf4f34436cac25ceb5",
                        16);
        BigInteger qExp =
                new BigInteger(
                        "6c929e4e81672fef49d9c825163fec97c4b7ba7acb26c0824638ac22605d7201c94625770984f78a56e6e25904fe7db407099cad9b14588841b94f5ab498dded",
                        16);
        BigInteger crtCoefficient =
                new BigInteger(
                        "dae7651ee69ad1d081ec5e7188ae126f6004ff39556bde90e0b870962fa7b926d070686d8244fe5a9aa709a95686a104614834b0ada4b10f53197a5cb4c97339",
                        16);
        RSAPrivateCrtKeyParameters privateParam =
                new RSAPrivateCrtKeyParameters(mod, pub, prv, p, q, pExp, qExp, crtCoefficient);
        Assertions.mustBeInAcceptingState(privateParam);
        Assertions.notHasEnsuredPredicate(
                privateParam); // because p & q are of type BigInteger which cannot ensure
        // randomized predicate

        ParametersWithRandom randomParam2 = new ParametersWithRandom(privateParam);
        Assertions.mustBeInAcceptingState(randomParam2);
        Assertions.notHasEnsuredPredicate(randomParam2);

        BigInteger n = new BigInteger("62771017353866");
        ECCurve.Fp curve =
                new ECCurve.Fp(
                        new BigInteger("2343"),
                        new BigInteger("2343"),
                        new BigInteger("2343"),
                        n,
                        ECConstants.ONE);
        ECDomainParameters ecParams =
                new ECDomainParameters(
                        curve,
                        curve.decodePoint(
                                Hex.decode("03188da80eb03090f67cbf20eb43a18800f4ff0afd82ff1012")),
                        n);
        Assertions.mustBeInAcceptingState(ecParams);
        Assertions.hasEnsuredPredicate(ecParams);
        ECPublicKeyParameters pubKeyValid =
                new ECPublicKeyParameters(curve.decodePoint(Hex.decode("0262b12d")), ecParams);
        Assertions.mustBeInAcceptingState(pubKeyValid);
        Assertions.hasEnsuredPredicate(pubKeyValid);

        ParametersWithRandom randomParam3 = new ParametersWithRandom(pubKeyValid);
        Assertions.mustBeInAcceptingState(randomParam3);
        Assertions.hasEnsuredPredicate(randomParam3);
    }

    @Test
    public void testORingThreePredicates2() {
        BigInteger mod = new BigInteger("a0b8e8321b041acd40b7", 16);
        BigInteger pub = new BigInteger("499602D2", 16);
        RSAKeyParameters params = new RSAKeyParameters(false, mod, pub);
        Assertions.mustBeInAcceptingState(params);
        Assertions.hasEnsuredPredicate(params);
        byte[] message = new byte[100];

        RSAEngine engine1 = new RSAEngine();
        Assertions.hasEnsuredPredicate(engine1);
        engine1.init(false, params);
        byte[] cipherText1 = engine1.processBlock(message, 0, message.length);
        Assertions.mustBeInAcceptingState(engine1);
        Assertions.hasEnsuredPredicate(cipherText1);

        BigInteger n = new BigInteger("62771017353866");
        ECCurve.Fp curve =
                new ECCurve.Fp(
                        new BigInteger("2343"),
                        new BigInteger("2343"),
                        new BigInteger("2343"),
                        n,
                        ECConstants.ONE);
        ECDomainParameters ecParams =
                new ECDomainParameters(
                        curve,
                        curve.decodePoint(
                                Hex.decode("03188da80eb03090f67cbf20eb43a18800f4ff0afd82ff1012")),
                        n);
        Assertions.mustBeInAcceptingState(ecParams);
        Assertions.hasEnsuredPredicate(ecParams);
        ECPublicKeyParameters pubKeyValid =
                new ECPublicKeyParameters(curve.decodePoint(Hex.decode("0262b12d")), ecParams);
        Assertions.mustBeInAcceptingState(pubKeyValid);
        Assertions.hasEnsuredPredicate(pubKeyValid);

        RSAEngine engine2 = new RSAEngine();
        Assertions.hasEnsuredPredicate(engine2);
        engine2.init(false, pubKeyValid);
        byte[] cipherText2 = engine2.processBlock(message, 0, message.length);
        Assertions.mustBeInAcceptingState(engine2);
        Assertions.hasEnsuredPredicate(cipherText2);
    }

    @Override
    protected String getSootClassPath() {
        String bouncyCastleJarPath =
                new File("src/test/resources/bcprov-jdk18on-1.76.jar").getAbsolutePath();
        return super.getSootClassPath() + File.pathSeparator + bouncyCastleJarPath;
    }
}
