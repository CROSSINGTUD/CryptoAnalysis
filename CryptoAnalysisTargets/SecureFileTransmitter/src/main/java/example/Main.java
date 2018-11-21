package example;

import Crypto.TLSClient;

public class Main {

	public static void main(String... args) throws Exception {
		FileReader f = new FileReader(".\\bin\\input.txt");
		String fileContent = f.getContent();

		// TLSClient tls = new TLSClient("127.0.0.1", 9999);
		// tls.sendData();
		// tls.closeConnection();
		TLSClient tls = new TLSClient("127.0.0.1", 9999);

		Boolean sendingSuccessful = tls.sendData(fileContent);
		if(!sendingSuccessful)
			System.out.println("Data was not sent.");
		fileContent = tls.receiveData();

		tls.closeConnection();
	}
	
}
