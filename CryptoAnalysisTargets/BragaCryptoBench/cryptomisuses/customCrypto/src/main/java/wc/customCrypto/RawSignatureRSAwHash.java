package wc.customCrypto;

import org.alexmbraga.utils.U;
import java.util.Arrays;
import javax.crypto.Cipher;
import java.security.*;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public final class RawSignatureRSAwHash {

    public static void main(String args[]) {
        byte[] mensagem = U.cancaoDoExilio.getBytes();
        try {
            // Gera o hash da messagem
            MessageDigest md = MessageDigest.getInstance("SHA-512","SUN");
            md.update(mensagem);
            byte[] hashGerado = md.digest();
            // Gerando um par de chaves RSA de 1024 bits
            KeyPairGenerator gerador = KeyPairGenerator.getInstance("RSA","SunJSSE");
            gerador.initialize(3072);
            KeyPair chaves = gerador.generateKeyPair();
            // Cria a implementacao de RSA
            Cipher cifra = Cipher.getInstance("RSA","SunJCE");
            //System.out.println(cifra.getProvider().getName());
            // Criptografando o hash com a chave Privada (para assinatura)
            // inicializa o algoritmo para criptografia
            cifra.init(Cipher.ENCRYPT_MODE, chaves.getPrivate());
            // criptografa o hash
            byte[] hashAssinado = cifra.doFinal(hashGerado);

            // Verificando a assinatura
            // Primeiro, gera o hash a partir da mensagem original
            // estamos apenas usando um outro metodo, para fazer
            // o digest de uma vez so.

            // modificando a mensagem
            //mensagem[0]= (byte)(mensagem[0] & 0x01);
            byte[] hashCalculado = md.digest(mensagem);
            // Descriptografando o hash com a chave Publica
            // (assim, verificamos a assinatura)
            cifra.init(Cipher.DECRYPT_MODE, chaves.getPublic());
            byte[] hashOriginal = cifra.doFinal(hashAssinado);
            // Agora, verificamos se o hash recebido eh igual ao hash calculado
            if (Arrays.equals(hashOriginal, hashCalculado)) {
                System.out.println ("Assinatura confere.");
            } else {
                System.out.println ("Assinatura NÃO confere.");
            }

        } catch  (NoSuchAlgorithmException | NoSuchPaddingException | 
                  InvalidKeyException | IllegalBlockSizeException | 
                  BadPaddingException | NoSuchProviderException e) 
        { System.out.println(e); }
    }
}
