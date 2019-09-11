package pdf.insecureDefault;

import static org.alexmbraga.utils.U.x2s;
import javax.crypto.*;
import java.security.*;

public final class InsecureDefaultAES {

    public static void main (String args[])
            throws
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            BadPaddingException,
            IllegalBlockSizeException,
            NoSuchProviderException
    {
        byte[] mensagem = "Este é um teste do AES".getBytes();
        // Gerando uma chave AES de 256 bits
        KeyGenerator gerador = KeyGenerator.getInstance("AES","SunJCE");
        gerador.init(256);
        Key chave = gerador.generateKey();
        // Cria a implementacao de AES que usa por default o modo ECB
        Cipher cifra = Cipher.getInstance("AES","SunJCE"); 
        // Criptografando a mensagem
        // inicializa o algoritmo para criptografia
        cifra.init(Cipher.ENCRYPT_MODE, chave);
        // criptografa o texto inteiro
        byte[] mensagemCifrada = cifra.doFinal(mensagem);
        System.out.println("Cifrado com " + cifra.getAlgorithm());
        System.out.println("A mensagem cifrada fica:");
        System.out.println(x2s(mensagemCifrada));
        // Descriptografando a mensagem
        cifra.init(Cipher.DECRYPT_MODE, chave);
        byte[] mensagemOriginal = cifra.doFinal(mensagemCifrada);
        System.out.println("A mensagem original era:"
                + new String(mensagemOriginal));
    }
}
