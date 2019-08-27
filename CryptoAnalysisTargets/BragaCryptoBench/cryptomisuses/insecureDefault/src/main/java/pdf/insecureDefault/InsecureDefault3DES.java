package pdf.insecureDefault;

import _utils.U;
import javax.crypto.*;
import java.security.*;

public final class InsecureDefault3DES {

    public static void main (String args[])
            throws
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            BadPaddingException,
            IllegalBlockSizeException,
            NoSuchProviderException
    {
        byte[] mensagem = "Este é um teste do DES".getBytes();
        // Gerando uma chave DES de 56 bits
        KeyGenerator gerador = KeyGenerator.getInstance("DESede","SunJCE");
        gerador.init(168);
        Key chave = gerador.generateKey();
        // Cria a implementacao de DES
        Cipher cifra = Cipher.getInstance("DESede","SunJCE");
        // Criptografando a mensagem
        // inicializa o algoritmo para criptografia
        cifra.init(Cipher.ENCRYPT_MODE, chave);
        // criptografa o texto inteiro
        byte[] mensagemCifrada = cifra.doFinal(mensagem);
        System.out.println("Cifrado com " + cifra.getAlgorithm());
        System.out.println("A mensagem cifrada fica:");
        System.out.println(U.b2x(mensagemCifrada));
        // Descriptografando a mensagem
        cifra.init(Cipher.DECRYPT_MODE, chave);
        byte[] mensagemOriginal = cifra.doFinal(mensagemCifrada);
        System.out.println("A mensagem original era:"
                + new String(mensagemOriginal));
    }

    
}
