package gcm_aes_example;

import java.security.SecureRandom;
import java.util.Arrays;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;

public class GCMAESBouncyCastle {

    private byte[] key;

    public byte[] processing(byte[] input, boolean encrypt)
            throws DataLengthException, InvalidCipherTextException {
         byte[] nonce = new byte[16];
         GCMBlockCipher cipher = new GCMBlockCipher(new AESEngine());
         AEADParameters parameters = new AEADParameters(new KeyParameter(key), 0, nonce);
         cipher.init(false, parameters);

         byte[] out = new byte[cipher.getOutputSize(12)];

         byte[] output = new byte[cipher.getOutputSize(12)];
         int pos = cipher.processBytes(input, 123 , 123, output, 123);
         pos += cipher.doFinal(out, pos);

         return Arrays.copyOf(out, pos);
    }
    
    public byte[] processingCorrect(byte[] input, boolean encrypt)
            throws DataLengthException, InvalidCipherTextException {
         byte[] nonce = new byte[16];
         SecureRandom secRand = new SecureRandom();
         byte[] keyParam = new byte[16]; 
         secRand.nextBytes(keyParam);
         key = keyParam;
         GCMBlockCipher cipher = new GCMBlockCipher(new AESEngine());
         AEADParameters parameters = new AEADParameters(new KeyParameter(key), 0, nonce);
         cipher.init(false, parameters);

         byte[] out = new byte[cipher.getOutputSize(12)];

         byte[] output = new byte[cipher.getOutputSize(12)];
         int pos = cipher.processBytes(input, 123 , 123, output, 123);
         pos += cipher.doFinal(out, pos);

         return Arrays.copyOf(out, pos);
    }
 }