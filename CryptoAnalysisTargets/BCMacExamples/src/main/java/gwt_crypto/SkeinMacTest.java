package gwt_crypto;

import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.SkeinMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

public class SkeinMacTest {
	
	private byte[] msg = Hex.decode("d3090c72");
	private byte[] key = Hex.decode("cb41f1706cde09651203c2d0efbaddf847a0d315cb2e53ff8bac41da0002672e");
	private byte[] digest1 = Hex.decode("1d658372cbea2f9928493cc47599d6f4ad8ce33536bedfa20b739f07516519d5");
	private int blockSize = 256;
    private int outputSize = 256;

	public void performTestOne()
	{
	    Mac digest = new SkeinMac(blockSize, outputSize);
	    digest.init(new KeyParameter(key));

	    byte[] message = msg;
	    digest.update(message, 0, message.length);

	    byte[] output = new byte[digest.getMacSize()];
	    digest.doFinal(output, 0);

	    if (!Arrays.areEqual(output, digest1))
	    {
	        System.out.println(digest.getAlgorithmName() + " message " + (digest1.length * 8) + " mismatch.\n Message  " + new String(Hex.encode(message))
	            + "\n Key      " + new String(Hex.encode(key)) + "\n Expected "
	            + new String(Hex.encode(digest1)) + "\n Actual   " + new String(Hex.encode(output)));
	    }

	}
}
