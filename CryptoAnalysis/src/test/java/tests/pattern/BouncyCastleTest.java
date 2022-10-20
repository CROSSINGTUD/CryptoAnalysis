package tests.pattern;

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
import org.junit.Ignore;
import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class BouncyCastleTest extends UsagePatternTestingFramework {

	@Override
	protected Ruleset getRuleSet() {
		return Ruleset.BouncyCastle;
	}
	
	@Test
	public void testEncryptTwo() throws InvalidCipherTextException {
	    String edgeInput = "ff6f77206973207468652074696d6520666f7220616c6c20676f6f64206d656e";
	    byte[] data = Hex.decode(edgeInput);
		RSAKeyParameters pubParameters = new RSAKeyParameters(false, null, null);
		AsymmetricBlockCipher eng = new RSAEngine();
        // missing init()
//		eng.init(true, pubParameters);
        byte[] cipherText = eng.processBlock(data, 0, data.length);
        Assertions.mustNotBeInAcceptingState(eng);
	}
	
	
	@Test
	public void rsKeyParameters() {
		BigInteger mod = new BigInteger("a0b8e8321b041acd40b7", 16);
		BigInteger pub = new BigInteger("9f0783a49...da", 16);	
		BigInteger pri = new BigInteger("21231...cda7", 16); 
		RSAKeyParameters privParameters = new RSAKeyParameters(true, mod, pri); //<--- warning here
		Assertions.mustBeInAcceptingState(privParameters);
		Assertions.notHasEnsuredPredicate(privParameters);
		RSAKeyParameters pubParameters = new RSAKeyParameters(false, mod, pub); //<--- but no warning here
		Assertions.mustBeInAcceptingState(pubParameters);
		Assertions.hasEnsuredPredicate(pubParameters);
	}
	
	@Test
	public void testORingTwoPredicates1() throws GeneralSecurityException {
		BigInteger mod = new BigInteger("a0b8e8321b041acd40b7", 16);
		BigInteger pub = new BigInteger("9f0783a49...da", 16);	
		RSAKeyParameters params = new RSAKeyParameters(false, mod, pub);
		Assertions.mustBeInAcceptingState(params);
		Assertions.hasEnsuredPredicate(params);
		
		ParametersWithRandom randomParam1 = new ParametersWithRandom(params);
		Assertions.mustBeInAcceptingState(randomParam1);
		Assertions.hasEnsuredPredicate(randomParam1);
		
		BigInteger priv = new BigInteger("92e08f83...19", 16);
		Random randomGenerator = SecureRandom.getInstance("SHA1PRNG");
		Assertions.mustBeInAcceptingState(randomGenerator);
		Assertions.hasEnsuredPredicate(randomGenerator);
		BigInteger p = new BigInteger(1024, randomGenerator);
		BigInteger q = new BigInteger(1024, randomGenerator);
		BigInteger pExp = new BigInteger("1d1a2d3ca8...b5", 16);
		BigInteger qExp = new BigInteger("6c929e4e816...ed", 16);
		BigInteger crtCoef = new BigInteger("dae7651ee...39", 16);
		RSAPrivateCrtKeyParameters privParam = new RSAPrivateCrtKeyParameters(mod, pub, priv, p, q, pExp, qExp, crtCoef);
		Assertions.mustBeInAcceptingState(privParam);
		Assertions.notHasEnsuredPredicate(privParam); // because p & q are of type BigInteger which cannot ensure randomized predicate
		
		ParametersWithRandom randomParam2 = new ParametersWithRandom(privParam);
		Assertions.mustBeInAcceptingState(randomParam2);
		Assertions.notHasEnsuredPredicate(randomParam2);
	}
	
	@Ignore
	@Test
	public void testORingTwoPredicates2() throws GeneralSecurityException, IllegalStateException, InvalidCipherTextException {
		SecureRandom random = new SecureRandom();
		byte[] genSeed = random.generateSeed(128);
		KeyParameter keyParam = new KeyParameter(genSeed);
		byte[] nonce = random.generateSeed(128);
		AEADParameters aeadParam = new AEADParameters(keyParam, 128, nonce);
		Assertions.hasEnsuredPredicate(aeadParam);
		Assertions.mustBeInAcceptingState(aeadParam);
		AESEngine engine = new AESEngine();
		Assertions.hasEnsuredPredicate(engine);
		byte[] input = new byte[100];
		byte[] output = new byte[100];
		
		GCMBlockCipher cipher1 = new GCMBlockCipher(engine);
		cipher1.init(false, aeadParam);
		cipher1.processAADBytes(input, 0, input.length);
		cipher1.doFinal(output, 0);
		Assertions.hasEnsuredPredicate(cipher1);
		Assertions.mustBeInAcceptingState(cipher1);
		
		ParametersWithIV ivParam = new ParametersWithIV(keyParam, genSeed);
		Assertions.hasEnsuredPredicate(ivParam);
		Assertions.mustBeInAcceptingState(ivParam);
		
		GCMBlockCipher cipher2 = new GCMBlockCipher(engine);
		cipher2.init(false, ivParam);
//		cipher2.processAADBytes(input, 0, input.length);
//		cipher2.doFinal(output, 0);
		Assertions.hasEnsuredPredicate(cipher2);
		Assertions.mustNotBeInAcceptingState(cipher2);	
	}
	
	@Test
	public void testORingThreePredicates1() throws GeneralSecurityException {
		BigInteger mod = new BigInteger("a0b8e8321b041acd40b7", 16);
		BigInteger pub = new BigInteger("9f0783a49...da", 16);	
		RSAKeyParameters params = new RSAKeyParameters(false, mod, pub);

		ParametersWithRandom randomParam1 = new ParametersWithRandom(params);
		Assertions.mustBeInAcceptingState(randomParam1);
		Assertions.hasEnsuredPredicate(randomParam1);
		
		BigInteger priv = new BigInteger("92e08f83...19", 16);
		Random randomGenerator = SecureRandom.getInstance("SHA1PRNG");
		Assertions.mustBeInAcceptingState(randomGenerator);
		Assertions.hasEnsuredPredicate(randomGenerator);
		BigInteger p = new BigInteger(1024, randomGenerator);
		BigInteger q = new BigInteger(1024, randomGenerator);
		BigInteger pExp = new BigInteger("1d1a2d3ca8...b5", 16);
		BigInteger qExp = new BigInteger("6c929e4e816...ed", 16);
		BigInteger crtCoef = new BigInteger("dae7651ee...39", 16);
		RSAPrivateCrtKeyParameters privParam = new RSAPrivateCrtKeyParameters(mod, pub, priv, p, q, pExp, qExp, crtCoef);
		Assertions.mustBeInAcceptingState(privParam);
		Assertions.notHasEnsuredPredicate(privParam); // because p & q are of type BigInteger which cannot ensure randomized predicate
		
		ParametersWithRandom randomParam2 = new ParametersWithRandom(privParam);
		Assertions.mustBeInAcceptingState(randomParam2);
		Assertions.notHasEnsuredPredicate(randomParam2);
		
		BigInteger n = new BigInteger("62771017353866");
		ECCurve.Fp curve = new ECCurve.Fp(new BigInteger("2343"), new BigInteger("2343"), new BigInteger("2343"), n, ECConstants.ONE);
		ECDomainParameters ecParams = new ECDomainParameters(curve, curve.decodePoint(Hex.decode("03188da80eb03090f67cbf20eb43a18800f4ff0afd82ff1012")), n);
		Assertions.mustBeInAcceptingState(ecParams);
		Assertions.hasEnsuredPredicate(ecParams);
		ECPublicKeyParameters pubKeyValid = new ECPublicKeyParameters( curve.decodePoint(Hex.decode("0262b12d")), ecParams);
		Assertions.mustBeInAcceptingState(pubKeyValid);
		Assertions.hasEnsuredPredicate(pubKeyValid);
		
		ParametersWithRandom randomParam3 = new ParametersWithRandom(pubKeyValid);
		Assertions.mustBeInAcceptingState(randomParam3);
		Assertions.hasEnsuredPredicate(randomParam3);
	}
	
	@Test
	public void testORingThreePredicates2() throws GeneralSecurityException {
		BigInteger mod = new BigInteger("a0b8e8321b041acd40b7", 16);
		BigInteger pub = new BigInteger("9f0783a49...da", 16);	
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
		ECCurve.Fp curve = new ECCurve.Fp(new BigInteger("2343"), new BigInteger("2343"), new BigInteger("2343"), n, ECConstants.ONE);
		ECDomainParameters ecParams = new ECDomainParameters(curve, curve.decodePoint(Hex.decode("03188da80eb03090f67cbf20eb43a18800f4ff0afd82ff1012")), n);
		Assertions.mustBeInAcceptingState(ecParams);
		Assertions.hasEnsuredPredicate(ecParams);
		ECPublicKeyParameters pubKeyValid = new ECPublicKeyParameters( curve.decodePoint(Hex.decode("0262b12d")), ecParams);
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
		String bouncyCastleJarPath = new File("src/test/resources/bcprov-jdk15on-1.60.jar").getAbsolutePath();
		return super.getSootClassPath() +File.pathSeparator +bouncyCastleJarPath;
	}
}
