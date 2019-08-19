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


public final class DoNotPrintSecKey2 {

  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException,
          InvalidAlgorithmParameterException {

    Security.addProvider(new BouncyCastleProvider()); // provedor BC
    byte[] ptAna = ("non-static CTR....").getBytes();

    byte[] prefixCTR = new byte[8];
    SecureRandom.getInstanceStrong().nextBytes(prefixCTR);
    
    ByteBuffer sufixCTR = ByteBuffer.allocate(Long.BYTES); // 8 bytes (64 bit)
    long l = SecureRandom.getInstanceStrong().nextLong();
    sufixCTR.putLong(l);
    byte[] ictr = Arrays.concatenate(prefixCTR, sufixCTR.array());// 16 bytes
    
    KeyGenerator g = KeyGenerator.getInstance("AES", "BC");
    g.init(128);
    Key k = g.generateKey();

    U.println("Plaintext: " + new String(ptAna));
    //U.println("Enc key  : " + U.b2x(k.getEncoded()));
    U.println("counter  : " + U.b2x(ictr) + "\n");

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
    
    sufixCTR = ByteBuffer.allocate(Long.BYTES); // 8 bytes (64 bit)
    sufixCTR.putLong(l+1);
    ictr = Arrays.concatenate(prefixCTR, sufixCTR.array());
    
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
