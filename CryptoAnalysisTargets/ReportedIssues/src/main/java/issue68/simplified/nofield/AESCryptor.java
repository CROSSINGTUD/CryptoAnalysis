package issue68.simplified.nofield;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class AESCryptor  {


    private byte[] encryptImpl(final byte[] bytes) {
        try {

            SecureRandom random = new SecureRandom();
            final byte[] iv = new byte[2];
            random.nextBytes(iv);

            final IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            use(ivParameterSpec);
        } catch (final BufferUnderflowException e) {
            return null;
        }
        return null;
    }

    private void use(IvParameterSpec ivParameterSpec) {
		// TODO Auto-generated method stub
		
	}

	public byte[] encrypt(final byte[] rawData) {
        return encryptImpl(rawData);
    }
}