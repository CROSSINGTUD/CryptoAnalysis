package example;

import example._utils.U;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;


public final class UseCTR {

  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException,
          InvalidAlgorithmParameterException {

    Security.addProvider(new BouncyCastleProvider()); // provedor BC
    byte[] ptAna = ("non-static CTR....").getBytes();

    byte[] prefixCTR = new byte[8];
    SecureRandom.getInstanceStrong().nextBytes(prefixCTR);
    
    byte[] ictr = CTR.create();
    
    KeyGenerator g = KeyGenerator.getInstance("AES", "BC");
    g.init(256);
    Key k = g.generateKey();

    Cipher enc = Cipher.getInstance("AES/CTR/NoPadding", "BC");
    Cipher dec = Cipher.getInstance("AES/CTR/NoPadding", "BC");
    U.println("Encriptado com: " + enc.getAlgorithm());
    byte[] ct;

    enc.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(ictr));
    ct = enc.doFinal(ptAna);
    byte[] ctr = enc.getIV();
    dec.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(ctr));
    byte[] ptBeto = dec.doFinal(ct);
    U.println("Ciphertext: " + U.b2x(ct));
    U.println("Plaintext : " + new String(ptBeto));
    U.println("counter   : " + U.b2x(ictr) + "\n");
    
    for (int i = 0; i < 10; i++) {
      ictr = CTR.increment();
      enc.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(ictr));
      ct = enc.doFinal(ptAna);
      ctr = enc.getIV();
      dec.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(ctr));
      ptBeto = dec.doFinal(ct);
      U.println("Ciphertext: " + U.b2x(ct));
      U.println("Plaintext : " + new String(ptBeto));
      U.println("counter   : " + U.b2x(ictr) + "\n");
    }
  }
}
  
class CTR {

  static byte[] prefixCTR = new byte[8];
  static ByteBuffer sufixCTR = ByteBuffer.allocate(Long.BYTES);//8 bytes(64 bits)
  static byte[] currentCounter;
  static long l;
  
  static byte[] increment() throws NoSuchAlgorithmException {
    sufixCTR = ByteBuffer.allocate(Long.BYTES); // 8 bytes (64 bit)
    l =l +1;
    sufixCTR.putLong(l);
    currentCounter = Arrays.concatenate(prefixCTR, sufixCTR.array());
    return currentCounter;
  }
  
  static byte[] create() throws NoSuchAlgorithmException {
    SecureRandom.getInstanceStrong().nextBytes(prefixCTR);
    l = SecureRandom.getInstanceStrong().nextLong();
    sufixCTR.putLong(l);
    currentCounter = Arrays.concatenate(prefixCTR, sufixCTR.array());// 16 bytes
    return currentCounter;
  }
  
  
}

