package cbc_aes_example;

import java.security.SecureRandom;
import java.util.Arrays;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

// Copied from https://github.com/p120ph37/cbc-aes-example/
public class CBCAESBouncyCastle {

    private final CBCBlockCipher cbcBlockCipher = new CBCBlockCipher(new AESEngine());
    private final SecureRandom random = new SecureRandom();

    private KeyParameter key;
    private BlockCipherPadding bcp = new PKCS7Padding();

    public void setPadding(BlockCipherPadding bcp) {
        this.bcp = bcp;
    }

    public void setKey(byte[] key) {
        this.key = new KeyParameter(key);
    }

    public byte[] encrypt(byte[] input)
            throws DataLengthException, InvalidCipherTextException {
        return processing(input, true);
    }

    public byte[] decrypt(byte[] input)
            throws DataLengthException, InvalidCipherTextException {
        return processing(input, false);
    }

    private byte[] processing(byte[] input, boolean encrypt)
            throws DataLengthException, InvalidCipherTextException {

        PaddedBufferedBlockCipher pbbc =
            new PaddedBufferedBlockCipher(cbcBlockCipher, bcp);

        int blockSize = cbcBlockCipher.getBlockSize();
        int inputOffset = 0;
        int inputLength = input.length;
        int outputOffset = 0;

        byte[] iv = new byte[blockSize];
        if(encrypt) {
            outputOffset += blockSize;
            random.nextBytes(iv);
        } else {
            System.arraycopy(input, 0 , iv, 0, blockSize);
            inputOffset += blockSize;
            inputLength -= blockSize;
        }

        pbbc.init(encrypt, new ParametersWithIV(key, iv));
        byte[] output = new byte[pbbc.getOutputSize(inputLength) + outputOffset];

        if(encrypt) {
            System.arraycopy(iv, 0 , output, 0, blockSize);
        }

        int outputLength = outputOffset + pbbc.processBytes(
            input, inputOffset, inputLength, output, outputOffset);

        outputLength += pbbc.doFinal(output, outputLength);

        return Arrays.copyOf(output, outputLength);

    }
 }