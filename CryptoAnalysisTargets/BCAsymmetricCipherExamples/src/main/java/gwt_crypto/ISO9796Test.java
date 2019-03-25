package gwt_crypto;

import java.math.BigInteger;
import java.util.Arrays;

import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.encodings.ISO9796d1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.ParametersWithSalt;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.signers.ISO9796d2PSSSigner;
import org.bouncycastle.util.encoders.Hex;

public class ISO9796Test {

	static BigInteger mod1 = new BigInteger("0100000000000000000000000000000000bba2d15dbb303c8a21c5ebbcbae52b7125087920dd7cdf358ea119fd66fb064012ec8ce692f0a0b8e8321b041acd40b7", 16);
	static BigInteger pub1 = new BigInteger("03", 16);
	static byte msg1[] = Hex.decode("0cbbaa99887766554433221100");
	static byte sig1[] = mod1.subtract(new BigInteger("309f873d8ded8379490f6097eaafdabc137d3ebfd8f25ab5f138d56a719cdc526bdd022ea65dabab920a81013a85d092e04d3e421caab717c90d89ea45a8d23a", 16)).toByteArray();
	static BigInteger pri1 = new BigInteger("2aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaac9f0783a49dd5f6c5af651f4c9d0dc9281c96a3f16a85f9572d7cc3f2d0f25a9dbf1149e4cdc32273faadd3fda5dcda7", 16);

	public void doTestOne()
			throws Exception
	{
		RSAKeyParameters pubParameters = new RSAKeyParameters(false, mod1, pub1);
		RSAKeyParameters privParameters = new RSAKeyParameters(true, mod1, pri1);
		RSAEngine rsa = new RSAEngine();
		byte[] data;

		//
		// ISO 9796-1 - public encrypt, private decrypt
		//
		ISO9796d1Encoding eng = new ISO9796d1Encoding(rsa);

		eng.init(true, privParameters);

		eng.setPadBits(4);

		data = eng.processBlock(msg1, 0, msg1.length);

		eng.init(false, pubParameters);

		if (!Arrays.equals(sig1, data))
		{
			System.out.println("failed ISO9796-1 generation");
		}

		data = eng.processBlock(data, 0, data.length);

		if (!Arrays.equals(msg1, data))
		{
			System.out.println("failed ISO9796-1 retrieve");
		}
	}
	public void doTestThree()
			throws Exception
	{

		BigInteger mod1 = new BigInteger("0100000000000000000000000000000000bba2d15dbb303c8a21c5ebbcbae52b7125087920dd7cdf358ea119fd66fb064012ec8ce692f0a0b8e8321b041acd40b7", 16);
		BigInteger pub1 = new BigInteger("03", 16);	
		BigInteger pri1 = new BigInteger("2aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaac9f0783a49dd5f6c5af651f4c9d0dc9281c96a3f16a85f9572d7cc3f2d0f25a9dbf1149e4cdc32273faadd3fda5dcda7", 16);

		RSAKeyParameters pubParameters = new RSAKeyParameters(false, mod1, pub1);
		RSAKeyParameters privParameters = new RSAKeyParameters(true, mod1, pri1);
		RSAEngine rsa = new RSAEngine();
		byte[] data;

		//
		// ISO 9796-1 - public encrypt, private decrypt
		//
		ISO9796d1Encoding eng = new ISO9796d1Encoding(rsa);

		eng.init(true, privParameters);

		eng.setPadBits(4);

		data = eng.processBlock(msg1, 0, msg1.length);

		eng.init(false, pubParameters);

		if (!Arrays.equals(sig1, data))
		{
			System.out.println("failed ISO9796-1 generation");
		}

		data = eng.processBlock(data, 0, data.length);

		if (!Arrays.equals(msg1, data))
		{
			System.out.println("failed ISO9796-1 retrieve");
		}
	}
	public void doTestTwo()
			throws Exception
	{

		byte[] salt = Hex.decode("61DF870C4890FE85D6E3DD87C3DCE3723F91DB49");
		RSAKeyParameters pubParameters = new RSAKeyParameters(false, mod1, pub1);
		RSAKeyParameters privParameters = new RSAKeyParameters(true, mod1, pri1);
		ParametersWithSalt sigParameters = new ParametersWithSalt(privParameters, salt);
		RSAEngine rsa = new RSAEngine();
		byte[] data;

		//
		// ISO 9796-2 - PSS Signing
		//
		ISO9796d2PSSSigner eng = new ISO9796d2PSSSigner(rsa, new RIPEMD160Digest(), 20, true);

		eng.init(true, sigParameters);

		data = eng.generateSignature();

		if (eng.getRecoveredMessage().length != 0)
		{
			System.out.println("failed zero check");
		}

		eng.init(false, pubParameters);

		if (!isSameAs(sig1, 1, data))
		{
			System.out.println("failed ISO9796-2 generation");
		}

		if (!eng.verifySignature(data))
		{
			System.out.println("failed ISO9796-2 verify");
		}
	}

	private boolean isSameAs(
			byte[] a,
			int off,
			byte[] b)
	{
		if ((a.length - off) != b.length)
		{
			return false;
		}

		for (int i = 0; i != b.length; i++)
		{
			if (a[i + off] != b[i])
			{
				return false;
			}
		}

		return true;
	}
}
