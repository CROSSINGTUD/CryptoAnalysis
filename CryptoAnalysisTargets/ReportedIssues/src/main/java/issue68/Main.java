package issue68;

public class Main {
	public void main(String...args) throws EncryptionException{
		AESCryptor c = new AESCryptor("");
		c.decrypt("te".getBytes());
		c.encrypt("e".getBytes());
	}
	
}
