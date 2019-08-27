package wc.brokenInsecureMAC;


import static _utils.U.b2x;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import javax.crypto.*;
import javax.crypto.spec.*;

public final class InsecureMAC2 {

    static Object[] macs = {"HMACSHA1","HMACMD5"};

    public static void main(String[] args) {

        System.out.println("Macs " + Arrays.toString(macs));
        for (int i = 0; i < macs.length; i++) {
            try{
            KeyGenerator kg = KeyGenerator.getInstance(macs[i].toString(),"SunJCE");
            SecretKey sk = kg.generateKey();
            Mac mac = Mac.getInstance(macs[i].toString(),"SunJCE");
            mac.init(sk);
            String msg = "Minha terra tem palmeiras, onde canta o sabiá";
            mac.update(msg.getBytes());
            byte[] result = mac.doFinal();
            byte[] key2 = sk.getEncoded();
            System.out.println("\nAlgoritmo: " + mac.getAlgorithm());
            System.out.println("Tamanho : " + mac.getMacLength());
            //System.out.println("Chave: " + b2x(key2));
            System.out.println("MSG:   " + msg);
            System.out.println("MAC1:  " + b2x(result));
            SecretKeySpec ks  = new SecretKeySpec(key2,macs[i].toString());
            Mac mac2 = Mac.getInstance(macs[i].toString(),"SunJCE");
            mac2.init(ks);
            mac2.update(msg.getBytes());
            byte[] result2 = mac2.doFinal();
            System.out.println("MAC2:  " + b2x(result2));
            //result[0] = (byte) (result[0] & 0x01);
            if (Arrays.equals(result,result2)) System.out.println("MAC confere!");
            else System.out.println("MAC Não confere!");
            } catch (NoSuchAlgorithmException | InvalidKeyException | 
                    IllegalStateException | NoSuchProviderException e) { 
                System.out.println("\n"+macs[i].toString()+" não disponível\n");
            }
        }
    }
}
