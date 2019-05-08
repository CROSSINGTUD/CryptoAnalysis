package gwt_crypto;

import java.math.BigInteger;
import java.util.Arrays;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.signers.ISO9796d2PSSSigner;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

public class ISO9796SignerTest {

	private static final byte[] shortPartialSig = Base64.decode(
			"sb8yyKk6HM1cJhICScMx7QRQunRyrZ1fbI42+T+TBGNjOknvzKuvG7aftGX7"
					+ "O/RXuYgk6LTxpXv7+O5noUhMBsR2PKaHveuylU1WSPmDxDCui3kp4frqVH0w"
					+ "8Vjpl5CsKqBsmKkbGCKE+smM0xFXhYxV8QUTB2XsWNCQiFiHPgwbpfWzZUNY"
					+ "QPWd0A99P64EuUIYz1tkkDnLFmwQ19/PJu1a8orIQInmkVYWSsBsZ/7Ks6lx"
					+ "nDHpAvgiRe+OXmJ/yuQy1O3FJYdyoqvjYRPBu3qYeBK9+9L3lExLilImH5aD"
					+ "nJznaXcO8QFOxVPbrF2s4GdPIMDonEyAHdrnzoghlg==");
	
	private static final byte[] longMessage = Base64.decode(
	        "VVNIKzErU0U2ODAxNTMyOTcxOSsyKzErNisyKzErMTo6OTk5OTk5OTk5OTk5"
	      + "OTo6OSsyOjo3Nzc3Nzc3Nzc3Nzc3Ojo5Kys1OjIwMTMwNDA1OjExMzUyMCdV"
	      + "U0ErMTo6OjE2OjEnVVNDKzRmYjk3YzFhNDI5ZGIyZDYnVVNBKzY6MTY6MTox"
	      + "MDoxKzE0OjIwNDgrMTI6/vn3S0h96eNhfmPN6OZUxXhd815h0tP871Hl+V1r"
	      + "fHHUXvrPXmjHV0vdb8fYY1zxwvnQUcFBWXT43PFi7Xbow0/9e9l6/mhs1UJq"
	      + "VPvp+ELbeXfn4Nj02ttk0e3H5Hfa69NYRuHv1WBO6lfizNnM9m9XYmh9TOrg"
	      + "f9rDRtd+ZNbf4lz9fPTt9OXyxOJWRPr/0FLzxUVsddplfHxM3ndETFD7ffjI"
	      + "/mhRYuL8WXZ733LeWFRCeOzKzmDz/HvT3GZx/XJMbFpqyOZjedzh6vZr1vrD"
	      + "615TQfN7wtJJ29bN2Hvzb2f1xGHaXl7af0/w9dpR2dr7/HzuZEJKYc7JSkv4"
	      + "/k37yERIbcrfbVTeVtR+dcVoeeRT41fmzMfzf8RnWOX4YMNifl0rMTM68EFA"
	      + "QSdCR00rMzgwKzk5OTk5OTk5J0RUTSsxMzc6MjAxMzA0MDU6MTAyJ0ZUWCtB"
	      + "QUkrKytJTlZPSUNFIFRFU1QnUkZGK09OOjEyMzQ1NidSRkYrRFE6MjIyMjIy"
	      + "MjIyJ0RUTSsxNzE6MjAxMzA0MDE6MTAyJ05BRCtTVSs5OTk5OTk5OTk5OTk5"
	      + "Ojo5KytURVNUIFNVUFBMSUVSOjpUcmFzZSByZWdpc3RlciBYWFhYWFhYK1Rl"
	      + "c3QgYWRkcmVzcyBzdXBwbGllcitDaXR5KysxMjM0NStERSdSRkYrVkE6QTEy"
	      + "MzQ1Njc4J05BRCtTQ08rOTk5OTk5OTk5OTk5OTo6OSsrVEVTVCBTVVBQTElF"
	      + "Ujo6VHJhc2UgcmVnaXN0ZXIgWFhYWFhYWCtUZXN0IGFkZHJlc3Mgc3VwcGxp"
	      + "ZXIrQ2l0eSsrMTIzNDUrREUnUkZGK1ZBOkExMjM0NTY3OCdOQUQrQlkrODg4"
	      + "ODg4ODg4ODg4ODo6OSdOQUQrSVYrNzc3Nzc3Nzc3Nzc3Nzo6OSsrVEVTVCBC"
	      + "VVlFUitUZXN0IGFkZHJlc3MgYnV5ZXIrQ2l0eTIrKzU0MzIxK0RFJ1JGRitW"
	      + "QTpKODc2NTQzMjEnTkFEK0JDTys3Nzc3Nzc3Nzc3Nzc3Ojo5KytURVNUIEJV"
	      + "WUVSK1Rlc3QgYWRkcmVzcyBidXllcitDaXR5MisrNTQzMjErREUnUkZGK1ZB"
	      + "Oko4NzY1NDMyMSdOQUQrRFArODg4ODg4ODg4ODg4ODo6OSdOQUQrUFIrNzc3"
	      + "Nzc3Nzc3Nzc3Nzo6OSdDVVgrMjpFVVI6NCdQQVQrMzUnRFRNKzEzOjIwMTMw"
	      + "NjI0OjEwMidMSU4rMSsrMTExMTExMTExMTExMTpFTidQSUErMStBQUFBQUFB"
	      + "OlNBJ0lNRCtGK00rOjo6UFJPRFVDVCBURVNUIDEnUVRZKzQ3OjEwLjAwMCdN"
	      + "T0ErNjY6Ny4wMCdQUkkrQUFCOjEuMDAnUFJJK0FBQTowLjcwJ1JGRitPTjox"
	      + "MjM0NTYnUkZGK0RROjIyMjIyMjIyMidUQVgrNytWQVQrKys6OjoyMS4wMDAn"
	      + "QUxDK0ErKysxK1REJ1BDRCsxOjMwLjAwMCdNT0ErMjA0OjMuMDAnTElOKzIr"
	      + "KzIyMjIyMjIyMjIyMjI6RU4nUElBKzErQkJCQkJCQjpTQSdJTUQrRitNKzo6"
	      + "OlBST0RVQ1QgVEVTVCAyJ1FUWSs0NzoyMC4wMDAnTU9BKzY2OjgwLjAwJ1BS"
	      + "SStBQUI6NS4wMCdQUkkrQUFBOjQuMDAnUkZGK09OOjEyMzQ1NidSRkYrRFE6"
	      + "MjIyMjIyMjIyJ1RBWCs3K1ZBVCsrKzo6OjIxLjAwMCdBTEMrQSsrKzErVEQn"
	      + "UENEKzE6MjAuMDAwJ01PQSsyMDQ6MjAuMDAnVU5TK1MnQ05UKzI6MidNT0Er"
	      + "Nzk6ODcuMDAnTU9BKzEzOToxMDUuMjcnTU9BKzEyNTo4Ny4wMCdNT0ErMjYw"
	      + "OjAuMDAnTU9BKzI1OTowLjAwJ01PQSsxNzY6MTguMjcnVEFYKzcrVkFUKysr"
	      + "Ojo6MjEuMDAwJ01PQSsxNzY6MTguMjcnTU9BKzEyNTo4Ny4wMCc=");
	
	public void doShortPartialTest()
			throws Exception
	{
		byte[]     recovered = Hex.decode("5553482b312b534536383031353332393731392b322b312b362b322b312b313a3a393939393939393939393939393a3a392b323a3a373737373737373737373737373a3a392b2b353a32303133303430353a313133");
		BigInteger exp = new BigInteger("10001", 16);
		BigInteger mod = new BigInteger("b9b70b083da9e37e23cde8e654855db31e21d2d3fc11a5f91d2b3c311efa8f5e28c757dd6fc798631cb1b9d051c14119749cb122ad76e8c3fd7bd93abe282c026a14fba9f8023977a7a0d8b49a24d1ad87e4379a931846a1ef9520ea57e28c998cf65722683d0caaa0da8306973e2496a25cbd3cb4adb4b284e25604fabf12f385456c75da7c3c4cde37440cfb7db8c8fe6851e2bc59767b9f7218540238ac8acef3bc7bd3dc6671320c2c1a2ac8a6799ce1eaf62b9683ab1e1341b37b9249dbd6cd987b2f27b5c4619a1eda7f0fb0b59a519afbbc3cee640261cec90a4bb8fefbc844082dca9f549e56943e758579a453a357e6ccb37fc46718a5b8c3227e5d", 16);

		AsymmetricKeyParameter pubKey = new RSAKeyParameters(false, mod, exp);

		ISO9796d2PSSSigner pssSign = new ISO9796d2PSSSigner(new RSAEngine(), new SHA1Digest(), 20);

		pssSign.init(false, pubKey);

		pssSign.updateWithRecoveredMessage(shortPartialSig);

		pssSign.update(longMessage, pssSign.getRecoveredMessage().length, longMessage.length - pssSign.getRecoveredMessage().length);

		if (!pssSign.verifySignature(shortPartialSig))
		{
			System.out.println("short partial PSS sig verification failed.");
		}

		byte[] mm = pssSign.getRecoveredMessage();

		if (!Arrays.equals(recovered, mm))
		{
			System.out.println("short partial PSS recovery failed");
		}
	}
	
	private void doFullMessageTest()
		    throws Exception
		{
		    BigInteger modulus = new BigInteger(1, Hex.decode("CDCBDABBF93BE8E8294E32B055256BBD0397735189BF75816341BB0D488D05D627991221DF7D59835C76A4BB4808ADEEB779E7794504E956ADC2A661B46904CDC71337DD29DDDD454124EF79CFDD7BC2C21952573CEFBA485CC38C6BD2428809B5A31A898A6B5648CAA4ED678D9743B589134B7187478996300EDBA16271A861"));
		    BigInteger pubExp = new BigInteger(1, Hex.decode("010001"));
		    BigInteger privExp = new BigInteger(1, Hex.decode("4BA6432AD42C74AA5AFCB6DF60FD57846CBC909489994ABD9C59FE439CC6D23D6DE2F3EA65B8335E796FD7904CA37C248367997257AFBD82B26F1A30525C447A236C65E6ADE43ECAAF7283584B2570FA07B340D9C9380D88EAACFFAEEFE7F472DBC9735C3FF3A3211E8A6BBFD94456B6A33C17A2C4EC18CE6335150548ED126D"));

		    RSAKeyParameters pubParams = new RSAKeyParameters(false, modulus, pubExp);
		    RSAKeyParameters privParams = new RSAKeyParameters(true, modulus, privExp);

		    AsymmetricBlockCipher rsaEngine = new RSABlindedEngine();

		    // set challenge to all zero's for verification
		    byte[] challenge = new byte[8];

		    ISO9796d2PSSSigner pssSign = new ISO9796d2PSSSigner(new RSAEngine(), new SHA256Digest(), 20, true);

		    pssSign.init(true, privParams);

		    pssSign.update(challenge, 0, challenge.length);

		    byte[] sig = pssSign.generateSignature();

		    pssSign.init(false, pubParams);

		    pssSign.updateWithRecoveredMessage(sig);

		    if (!pssSign.verifySignature(sig))
		    {
		    	System.out.println("challenge PSS sig verification failed.");
		    }

		    byte[] mm = pssSign.getRecoveredMessage();

		    if (!Arrays.equals(challenge, mm))
		    {
		    	System.out.println("challenge partial PSS recovery failed");
		    }
		}
}
