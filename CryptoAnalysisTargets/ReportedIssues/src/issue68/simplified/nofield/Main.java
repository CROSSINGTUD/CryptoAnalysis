package issue68.simplified.nofield;

public class Main {
	public void main(String...args) throws EncryptionException{
		AESCryptor c = new AESCryptor();
		c.encrypt("e".getBytes());
	}
	
}
