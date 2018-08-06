
package Crypto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Properties;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/** @author CogniCrypt */
public class TLSClient {
	private static SSLSocket sslsocket = null;
	private static BufferedWriter bufW = null;
	private static BufferedReader bufR = null;

	public TLSClient(String host, int port) {
		Properties prop = new Properties();
		InputStream input = null;
		String pwd = null;
		System.setProperty("javax.net.ssl.trustStore", "testPath");
		try {
			// If you move the generated code in another package (default of CogniCrypt is
			// Crypto),
			// you need to change the parameter (replacing Crypto with the package name).
			input = Object.class.getClass().getResourceAsStream("/Crypto/clientConfig.properties");
			prop.load(input);
			pwd = prop.getProperty("pwd");
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.setProperty("javax.net.ssl.trustStorePassword", pwd);
		SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		try {
			sslsocket = (SSLSocket) sslsocketfactory.createSocket(host, port);

			setCipherSuites();
			setProtocols();
			sslsocket.startHandshake();
			bufW = new BufferedWriter(new OutputStreamWriter(sslsocket.getOutputStream()));
			bufR = new BufferedReader(new InputStreamReader(sslsocket.getInputStream()));
		} catch (IOException ex) {
			System.out.println(
					"Connection to server could not be established. Please check whether the ip/hostname and port are correct");
			ex.printStackTrace();
		}

	}

	private void setCipherSuites() {
		if (sslsocket != null) {
			// Insert cipher suites here
			sslsocket.setEnabledCipherSuites(new String[] { "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
					"TLS_DHE_RSA_WITH_AES_256_GCM_SHA384", "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
					"TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384", "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384",
					"TLS_DHE_RSA_WITH_AES_256_CBC_SHA256", "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256", });
		}
	}

	private void setProtocols() {
		if (sslsocket != null) {
			// Insert TLSxx here
			sslsocket.setEnabledProtocols(new String[] { "TLSv1.1", "TLSv1.2" });
		}
	}

	public void closeConnection() {
		try {
			if (!sslsocket.isClosed()) {
				sslsocket.close();
			}
		} catch (IOException ex) {
			System.out.println("Could not close channel.");
			ex.printStackTrace();
		}
	}

	public boolean sendData(String content) {
		try {
			bufW.write(content + "\n");
			bufW.flush();
			return true;
		} catch (IOException ex) {
			System.out.println("Sending data failed.");
			ex.printStackTrace();
			return false;
		}
	}

	public String receiveData() {
		try {
			return bufR.readLine();
		} catch (IOException ex) {
			System.out.println("Receiving data failed.");
			ex.printStackTrace();
			return null;
		}
	}

}
