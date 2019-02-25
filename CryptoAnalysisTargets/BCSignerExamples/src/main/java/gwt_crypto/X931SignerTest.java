package gwt_crypto;

import java.math.BigInteger;

import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.signers.X931Signer;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

public class X931SignerTest {

	public void shouldPassSignatureTestOne()
			throws Exception
	{
		BigInteger n = new BigInteger("c9be1b28f8caccca65d86cc3c9bbcc13eccc059df3b80bd2292b811eff3aa0dd75e1e85c333b8e3fa9bed53bb20f5359ff4e6900c5e9a388e3a4772a583a79e2299c76582c2b27694b65e9ba22e66bfb817f8b70b22206d7d8ae488c86dbb7137c26d5eff9b33c90e6cee640630313b7a715802e15142fef498c404a8de19674974785f0f852e2d470fe85a2e54ffca9f5851f672b71df691785a5cdabe8f14aa628942147de7593b2cf962414a5b59c632c4e14f1768c0ab2e9250824beea60a3529f11bf5e070ce90a47686eb0be1086fb21f0827f55295b4a48307db0b048c05a4aec3f488c576ca6f1879d354224c7e84cbcd8e76dd217a3de54dba73c35", 16);
		BigInteger e = new BigInteger("e75b1b", 16);
		byte[] msg = Hex.decode("5bb0d1c0ef9b5c7af2477fe08d45523d3842a4b2db943f7033126c2a7829bacb3d2cfc6497ec91688189e81b7f8742488224ba320ce983ce9480722f2cc5bc42611f00bb6311884f660ccc244788378673532edb05284fd92e83f6f6dab406209032e6af9a33c998677933e32d6fb95fd27408940d7728f9c9c40267ca1d20ce");
		byte[] sig = Hex.decode("0fe8bb8e3109a1eb7489ef35bf4c1a0780071da789c8bd226a4170538eafefdd30b732d628f0e87a0b9450051feae9754d4fb61f57862d10f0bacc4f660d13281d0cd1141c006ade5186ff7d961a4c6cd0a4b352fc1295c5afd088f80ac1f8e192ef116a010a442655fe8ff5eeacea15807906fb0f0dfa86e680d4c005872357f7ece9aa4e20b15d5f709b30f08648ecaa34f2fbf54eb6b414fa2ff6f87561f70163235e69ccb4ac82a2e46d3be214cc2ef5263b569b2d8fd839b21a9e102665105ea762bda25bb446cfd831487a6b846100dee113ae95ae64f4af22c428c87bab809541c962bb3a56d4c86588e0af4ebc7fcc66dadced311051356d3ea745f7");

		RSAKeyParameters rsaPublic = new RSAKeyParameters(false, n, e);
		X931Signer signer = new X931Signer(new RSAEngine(), new SHA1Digest());

		signer.init(false, rsaPublic);

		signer.update(msg, 0, msg.length);

		if (!signer.verifySignature(sig))
		{
			System.out.println("RSA X931 verify test 1 failed.");
		}
	}
	
	public void shouldPassSignatureTestTwo() throws Exception
	{
	    BigInteger rsaPubMod = new BigInteger(Base64.decode("AIASoe2PQb1IP7bTyC9usjHP7FvnUMVpKW49iuFtrw/dMpYlsMMoIU2jupfifDpdFxIktSB4P+6Ymg5WjvHKTIrvQ7SR4zV4jaPTu56Ys0pZ9EDA6gb3HLjtU+8Bb1mfWM+yjKxcPDuFjwEtjGlPHg1Vq+CA9HNcMSKNn2+tW6qt"));
	    BigInteger rsaPubExp = new BigInteger(Base64.decode("EQ=="));
	    BigInteger rsaPrivMod = new BigInteger(Base64.decode("AIASoe2PQb1IP7bTyC9usjHP7FvnUMVpKW49iuFtrw/dMpYlsMMoIU2jupfifDpdFxIktSB4P+6Ymg5WjvHKTIrvQ7SR4zV4jaPTu56Ys0pZ9EDA6gb3HLjtU+8Bb1mfWM+yjKxcPDuFjwEtjGlPHg1Vq+CA9HNcMSKNn2+tW6qt"));
	    BigInteger rsaPrivDP = new BigInteger(Base64.decode("JXzfzG5v+HtLJIZqYMUefJfFLu8DPuJGaLD6lI3cZ0babWZ/oPGoJa5iHpX4Ul/7l3s1PFsuy1GhzCdOdlfRcQ=="));
	    BigInteger rsaPrivDQ = new BigInteger(Base64.decode("YNdJhw3cn0gBoVmMIFRZzflPDNthBiWy/dUMSRfJCxoZjSnr1gysZHK01HteV1YYNGcwPdr3j4FbOfri5c6DUQ=="));
	    BigInteger rsaPrivExp = new BigInteger(Base64.decode("DxFAOhDajr00rBjqX+7nyZ/9sHWRCCp9WEN5wCsFiWVRPtdB+NeLcou7mWXwf1Y+8xNgmmh//fPV45G2dsyBeZbXeJwB7bzx9NMEAfedchyOwjR8PYdjK3NpTLKtZlEJ6Jkh4QihrXpZMO4fKZWUm9bid3+lmiq43FwW+Hof8/E="));
	    BigInteger rsaPrivP = new BigInteger(Base64.decode("AJ9StyTVW+AL/1s7RBtFwZGFBgd3zctBqzzwKPda6LbtIFDznmwDCqAlIQH9X14X7UPLokCDhuAa76OnDXb1OiE="));
	    BigInteger rsaPrivQ = new BigInteger(Base64.decode("AM3JfD79dNJ5A3beScSzPtWxx/tSLi0QHFtkuhtSizeXdkv5FSba7lVzwEOGKHmW829bRoNxThDy4ds1IihW1w0="));
	    BigInteger rsaPrivQinv = new BigInteger(Base64.decode("Lt0g7wrsNsQxuDdB8q/rH8fSFeBXMGLtCIqfOec1j7FEIuYA/ACiRDgXkHa0WgN7nLXSjHoy630wC5Toq8vvUg=="));
	    RSAKeyParameters rsaPublic = new RSAKeyParameters(false, rsaPubMod, rsaPubExp);
	    RSAPrivateCrtKeyParameters rsaPrivate = new RSAPrivateCrtKeyParameters(rsaPrivMod, rsaPubExp, rsaPrivExp, rsaPrivP, rsaPrivQ, rsaPrivDP, rsaPrivDQ, rsaPrivQinv);

	    byte[] msg = new byte[] { 1, 6, 3, 32, 7, 43, 2, 5, 7, 78, 4, 23 };

	    X931Signer signer = new X931Signer(new RSAEngine(), new SHA1Digest());
	    signer.init(true, rsaPrivate);
	    signer.update(msg, 0, msg.length);
	    byte[] sig = signer.generateSignature();

	    signer = new X931Signer(new RSAEngine(), new SHA1Digest());
	    signer.init(false, rsaPublic);
	    signer.update(msg, 0, msg.length);
	    if (!signer.verifySignature(sig))
	    {
	        System.out.println("X9.31 Signer failed.");
	    }
	}
}
