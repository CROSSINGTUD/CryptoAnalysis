/*
Introdução à Criptografia para Programadores
Evitando Maus Usos de Criptografia em Sistemas de Software
@author Alexandre Braga
*/
package pdf.insecureStreamCipher;

import _utils.U;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


public final class MalealableStreamCipher {

  public static void main(String args[]) { 
    try {
      Security.addProvider(new BouncyCastleProvider()); // provedor BC
    byte[][]M={("Ana   para Carlo").getBytes(),("Valor:010.000,00").getBytes()};
    byte[][] iv = {U.x2b("0123456789ABCDEF0123456789ABCDEF"),
                   U.x2b("0123456789ABCDEF0123456789ABCDEF")};
    byte[][] iv2 ={iv[0].clone(),iv[0].clone()};
    byte[] k = U.x2b("00112233445566778899AABBCCDDEEFF"), X = null;
    byte[][] C = new byte[2][], N = new byte[2][];

    SecretKeySpec ks = new SecretKeySpec(k, "AES");
    Cipher c = Cipher.getInstance("AES/CTR/NoPadding", "BC");

    for (int i= 0; i < M.length; i++){
      c.init(Cipher.ENCRYPT_MODE, ks, new IvParameterSpec(iv[i]));
      C[i] = c.doFinal(M[i]);
      if (i < M.length -1) U.stdIncIV(iv[i+1],iv[i+1].length/2); 
    }
    
    //Ivo passa a ser o novo recebedor de um valor muito mais alto
    X = U.x_or("Ana   para Carlo".getBytes(),"Ana   para   Ivo".getBytes());
    C[0] = U.x_or(C[0], X);
    X = U.x_or("Valor:010.000,00".getBytes(),"Valor:100.998,54".getBytes());
    C[1] = U.x_or(C[1], X);
    
    for (int i = 0; i < C.length; i++){
      c.init(Cipher.DECRYPT_MODE, ks, new IvParameterSpec(iv2[i]));
      N[i] = c.doFinal(C[i]);
      if (i < C.length -1) U.stdIncIV(iv2[i+1],iv2[i+1].length/2);
      U.println("N[" + i + "] =" + U.b2s(N[i]));
    }  
  } catch (NoSuchAlgorithmException | NoSuchProviderException | 
          NoSuchPaddingException | InvalidKeyException | 
          InvalidAlgorithmParameterException | IllegalBlockSizeException | 
          BadPaddingException ex) { }}
}
