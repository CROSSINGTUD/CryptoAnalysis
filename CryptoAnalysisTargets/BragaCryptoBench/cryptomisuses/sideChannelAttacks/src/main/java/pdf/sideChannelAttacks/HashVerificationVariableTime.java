
package pdf.sideChannelAttacks;

import _utils.U;
import javax.crypto.*;
import java.security.*;
import org.bouncycastle.jce.provider.*;
import org.bouncycastle.util.Arrays;

//Tempo variável na verificação de hashes e MACs
public final class HashVerificationVariableTime {

  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException {

    Security.addProvider(new BouncyCastleProvider()); // provedor BC

    MessageDigest md = MessageDigest.getInstance("SHA512", "BC");
    boolean ok;
    long t1, t2;
    long t[] = new long[64], tt[] = new long[64], ttt[] = new long[64];
    long tttt[] = new long[64], ttttt[] = new long[64];
    md.reset();
    byte[] hash1 = md.digest(cancaoDoExilio.getBytes());
    for (int j = 0; j < 1; j++) {

      for (int i = 0; i < t.length; i++) { // 64 bytes
        md.reset();
        byte[] hash2 = md.digest(cancaoDoExilio.getBytes());
        hash2[i] = (byte) (hash2[i] ^ 0x01);
        t1 = System.nanoTime();
        ok = ehIgualVar(hash2, hash1);
        t2 = System.nanoTime();
        t[i] = t2 - t1;
      }

      for (int i = 0; i < t.length; i++) { // 64 bytes
        md.reset();
        byte[] hash2 = md.digest(cancaoDoExilio.getBytes());
        hash2[i] = (byte) (hash2[i] ^ 0x01);
        t1 = System.nanoTime();
        ok = MessageDigest.isEqual(hash2, hash1);
        t2 = System.nanoTime();
        tt[i] = t2 - t1;
      }

      for (int i = 0; i < t.length; i++) { // 64 bytes
        md.reset();
        byte[] hash2 = md.digest(cancaoDoExilio.getBytes());
        hash2[i] = (byte) (hash2[i] ^ 0x01);
        t1 = System.nanoTime();
        ok = ehIgualConst(hash2, hash1);
        t2 = System.nanoTime();
        ttt[i] = t2 - t1;
      }

      for (int i = 0; i < t.length; i++) { // 64 bytes
        md.reset();
        byte[] hash2 = md.digest(cancaoDoExilio.getBytes());
        hash2[i] = (byte) (hash2[i] ^ 0x01);
        t1 = System.nanoTime();
        ok = Arrays.areEqual(hash2, hash1);
        t2 = System.nanoTime();
        tttt[i] = t2 - t1;
      }

      for (int i = 0; i < t.length; i++) { // 64 bytes
        md.reset();
        byte[] hash2 = md.digest(cancaoDoExilio.getBytes());
        hash2[i] = (byte) (hash2[i] ^ 0x01);
        t1 = System.nanoTime();
        ok = Arrays.constantTimeAreEqual(hash2, hash1);
        t2 = System.nanoTime();
        ttttt[i] = t2 - t1;
      }
    }
    md.reset();
    byte[] hash2 = md.digest(cancaoDoExilio.getBytes());
    t1 = System.nanoTime();
    ok = ehIgualConst(hash2, hash1);
    t2 = System.nanoTime();
    U.println("igual:" + (t2 - t1) + "ok = " + ok);
    U.println("i;t[i];tt[i];ttt[i];tttt[i];ttttt[i]");
    for (int i = 0; i < t.length; i++) {
      U.println(i + ";" + t[i] + ";" + tt[i] + ";" + ttt[i] + ";" + tttt[i] + ";" + ttttt[i]);
    }
  }

  static boolean ehIgualVar(byte[] a, byte[] b) {
    boolean igual = true;
    if (a.length != b.length) {
      igual = false;
    } else {
      for (int i = 0; i < a.length; i++) {
        if (a[i] != b[i]) {
          igual = false;
          break;
        }
      }
    }
    return igual;
  }

  static boolean ehIgualConst(byte[] a, byte[] b) {
    boolean igual = true;
    if (a.length != b.length) {
      igual = false;
    } else {
      for (int i = 0; i < a.length; i++) {
        if (a[i] != b[i]) {
          igual = false;
        }
      }
    }
    return igual;
  }

  //de Antônio Gonçalves Dias - versão sem acentos ...
  // deste modo, cada caracter é um byte. e aconta do getBytes acerta...
  static String cancaoDoExilio = "Minha terra tem palmeiras"
          + "Onde canta o sabiah."
          + "As aves que aqui gorjeiam"
          + "Nao gorjeiam como lah."
          //+ ""
          + "Nosso ceu tem mais estrelas,"
          + "Nossas varzeas tem mais flores."
          + "Nossos bosques tem mais vida,"
          + "Nossa vida mais amores."
          //+ ""
          + "Em cismar, sozinho, aa noite,"
          + "Mais prazer encontro eu lah."
          + "Minha terra tem palmeiras"
          + "Onde canta o sabiah."
          //+ ""
          + "Minha terra tem primores"
          + "Que tais nao encontro eu cah;"
          + "Em cismar — sozinho, aa noite —"
          + "Mais prazer encontro eu lah."
          + "Minha terra tem palmeiras"
          + "Onde canta o sabiah."
          //+ ""
          + "Nao permita Deus que eu morra"
          + "Sem que eu volte para lah;"
          + "Sem que desfrute os primores"
          + "Que nao encontro por cah;"
          + "Sem que ainda aviste as palmeiras"
          + "Onde canta o sabiah.";
}
