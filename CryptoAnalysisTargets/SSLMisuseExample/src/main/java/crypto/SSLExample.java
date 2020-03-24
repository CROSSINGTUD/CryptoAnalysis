package crypto;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.SSLParameters;

public class SSLExample {
	
	public void NoMisuse() throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		String[] paramOne = {"TLSv1.1", "TLSv1.2"};
		String[] paramTwo = {"TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384"};
		SSLParameters params = new SSLParameters();
		params.setCipherSuites(paramTwo);
		params.setProtocols(paramOne);
	}
	
	public void MisuseOne() throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		String[] paramOne = {"IPv4", "TLSv1.2"};
		String[] paramTwo = {"TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384"};
		SSLParameters params = new SSLParameters();
		params.setCipherSuites(paramTwo);
		params.setProtocols(paramOne);
	}
	
	public void MisuseTwo() throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		String[] paramOne = {"TLSv1.1", "TLSv1.2"};
		String[] paramTwo = {"TLS_ECDHE_ECDSA_WITH_AES_256_SHA_SHA384"};
		SSLParameters params = new SSLParameters();
		params.setCipherSuites(paramTwo);
		params.setProtocols(paramOne);
	}
	
	public void MisuseThree() throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		String[] paramOne = {"TLSv2.1", "TLSv1.2"};
		String[] paramTwo = {"TL_ECDHE_ECDSA_WITH_AES_256_SHA_SHA384"};
		SSLParameters params = new SSLParameters();
		params.setCipherSuites(paramTwo);
		params.setProtocols(paramOne);
	}

}
