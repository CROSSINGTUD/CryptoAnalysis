
package _utils;

import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.Mac;

/**
 *
 * @author Alexandre
 */
public final class U {

  //de Antônio Gonçalves Dias - versão sem acentos ...
  // deste modo, cada caracter é um byte e a conta do getBytes acerta...

  public static final String cancaoDoExilio = //+ ""
          //+ ""
          //+ ""
          //+ ""
          "Minha terra tem palmeiras" + 
          "Onde canta o sabiah." + 
          "As aves que aqui gorjeiam" + 
          "Nao gorjeiam como lah." + 
          "Nosso ceu tem mais estrelas," +
          "Nossas varzeas tem mais flores." +
          "Nossos bosques tem mais vida," +
          "Nossa vida mais amores." +
          "Em cismar, sozinho, aa noite," +
          "Mais prazer encontro eu lah." +
          "Minha terra tem palmeiras" + "Onde canta o sabiah." +
          "Minha terra tem primores" +
          "Que tais nao encontro eu cah;" +
          "Em cismar, sozinho, aa noite," +
          "Mais prazer encontro eu lah." +
          "Minha terra tem palmeiras" +
          "Onde canta o sabiah." +
          "Nao permita Deus que eu morra" +
          "Sem que eu volte para lah;" +
          "Sem que desfrute os primores" +
          "Que nao encontro por cah;" +
          "Sem que ainda aviste as palmeiras" +
          "Onde canta o sabiah.";

  public static void println(String s) {
    System.out.println(s);
  }

  public static String b2s(byte[] ba) {
    return new String(ba);
  }

  public static String x2s(byte[] b) {
    String str = "";
    for (int i = 0; i < b.length; i++) {
      str += Integer.toHexString((byte) (b[i] >> 4) & 0x0000000f);
      str += Integer.toHexString((byte) b[i] & 0x0000000f);
    }
    return str;
  }

  public static byte[] x2b(String str) {
    if (str == null) {
      return null;
    } else if (str.length() < 2) {
      return null;
    } else {
      int len = str.length() / 2;
      byte[] buffer = new byte[len];
      for (int i = 0; i < len; i++) {
        buffer[i] = (byte) Integer.parseInt(
                str.substring(i * 2, i * 2 + 2), 16);
      }
      return buffer;
    }
  }

  public static String b2x(byte[] data) {
    if (data == null) {
      return null;
    } else {
      int len = data.length;
      String str = "";
      for (int i = 0; i < len; i++) {
        if ((data[i] & 0xFF) < 16) {
          str = str + "0" + java.lang.Integer.toHexString(data[i] & 0xFF);
        } else {
          str = str + java.lang.Integer.toHexString(data[i] & 0xFF);
        }
      }
      return str.toUpperCase();
    }
  }

  public static byte[] x_or(byte[] a, byte[] b) {
    byte[] x = new byte[a.length];
    if (a.length <= b.length) {
      for (int i = 0; i < a.length; i++) {
        x[i] = (byte) (a[i] ^ b[i]);
      }
    }
    return x;
  }

  // incremento padrão para IV no modo CTR
  public static void stdIncIV(byte[] a, int mark) {
    int l = 0;
    if (mark >= 0 && mark < a.length) {
      l = mark;
    }
    for (int i = a.length - 1; i >= l; i--) {
      if ((a[i] & 0xFF) != 0xFF) {
        a[i] = (byte) (a[i] + 1);
        break;
      } else {
        a[i] = (byte) (0x00);
        continue;
      }
    }
  }

  public static void delay(int seg) {
    long x = seg * 1000;
    long t = System.currentTimeMillis();
    while ((System.currentTimeMillis() - t) < x) {
      ; // atraso de x segundos
    }
  }

  public static void p(String s, byte[] t, byte[] tc, MessageDigest m, Cipher c, boolean ok) {
    U.println(s);
    U.println("Verificado com " + m.getAlgorithm() + ": " + ok);
    U.println("Encriptado com: " + c.getAlgorithm());
    U.println("Valor da   tag: " + U.b2x(t));
    U.println("Texto claro do Beto: " + new String(tc) + "\n");
  }

  public static void p(String s, byte[] t, byte[] tc, Mac m, Cipher c, boolean ok) {
    U.println(s);
    U.println("Verificado com " + m.getAlgorithm() + ": " + ok);
    U.println("Encriptado com: " + c.getAlgorithm());
    U.println("Valor da   tag: " + U.b2x(t));
    U.println("Texto claro do Beto: " + new String(tc) + "\n");
  }

}
