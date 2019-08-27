package wc.customCrypto;

import _utils.U;
import static _utils.U.x2s;
import java.security.*;

public final class RawSignatureRSA {

    public static void main(String args[]) {
        byte[] mensagem = U.cancaoDoExilio.getBytes();
        try {
            // Gerando um par de chaves RSA de 1024 bits
            KeyPairGenerator gerador = KeyPairGenerator.getInstance("RSA","SunJSSE");
            gerador.initialize(2048);
            KeyPair chaves = gerador.generateKeyPair();
            Signature sig = Signature.getInstance("SHA1WithRSA", "SunJSSE");
            // Inicializa com a chave privada
            sig.initSign(chaves.getPrivate());
            // assina a mensagem e devolve a assinatura
            sig.update(mensagem);
            byte[] assinatura = sig.sign();
            System.out.println("Assinatura "+x2s(assinatura));
            // verifica a assinatura com a chave publica
            sig.initVerify(chaves.getPublic());
            //mensagem[0] = (byte) (mensagem[0] & 0x01);
            sig.update(mensagem);
            if (sig.verify(assinatura)) {
                System.out.println("Assinatura confere.");
            } else {  System.out.println("Assinatura invalida.");}
        } catch (NoSuchAlgorithmException | NoSuchProviderException | 
                InvalidKeyException | SignatureException e) 
        { System.out.println(e);}
    }

    
}
