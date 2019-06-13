package example.predictableseeds;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PredictableSeedsABSCase2 {
    CryptoPredictableSeed2 crypto;
    public PredictableSeedsABSCase2() throws NoSuchAlgorithmException, NoSuchPaddingException {
        byte seed = 100;
        crypto = new CryptoPredictableSeed2(seed);
    }
}

class CryptoPredictableSeed2 {
    byte defSeed;

    public CryptoPredictableSeed2(byte seed) throws NoSuchPaddingException, NoSuchAlgorithmException {
        defSeed = seed;
    }

    public void encrypt(byte passedSeed) throws UnsupportedEncodingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException {

        passedSeed = defSeed;
        SecureRandom sr = new SecureRandom(new byte[]{passedSeed});
        int v = sr.nextInt();
        System.out.println(v);
    }
}
