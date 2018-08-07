

package	Crypto;

import	java.security.InvalidAlgorithmParameterException;
	
import	java.security.InvalidKeyException;
	
import	java.security.NoSuchAlgorithmException;
	
import	java.security.NoSuchAlgorithmException;
	
import	javax.crypto.SecretKey;
	
import	javax.crypto.BadPaddingException;
	
import	javax.crypto.Cipher;
	
import	javax.crypto.IllegalBlockSizeException;
	
import	javax.crypto.NoSuchPaddingException;
	
import	java.security.SecureRandom;
	
import	javax.crypto.spec.IvParameterSpec;
	
import	javax.crypto.spec.SecretKeySpec;
	
import	java.security.spec.InvalidKeySpecException;
	
import	java.util.List;
	
import	java.util.Base64;
	
import	java.io.InputStream;
	
import	java.io.OutputStream;
	
import	java.util.Properties;
	
import	java.io.FileOutputStream;
	
			public class Output {
			public void templateUsage(String archiveName, String pathName, String
			fileName, String keyStorePath, String keyStorePassword) throws
			Exception {
			LongTermArchivingClient archivingClient = new LongTermArchivingClient();
			Archive archive = archivingClient.createArchive(archiveName);

			final File toBeArchivedFile = new File(fileName + ".mops.zip");
			archivingClient.prepareFileForArchiving(fileName, new File(pathName +
			fileName), toBeArchivedFile, keyStorePath, keyStorePassword);

			Document archivedDoc = archivingClient.addFileToArchive(archive,
			toBeArchivedFile);
			System.out.println("Document storage " + ((archivedDoc != null) ? "successful" : "failed"));

			File retrievedArchiveFile =
			archivingClient.retrieveFileFromArchive(archive, archivedDoc,
			pathName + "ret.mops.zip");
			System.out.println("File retrieval " + ((retrievedArchiveFile != null) ? "successful" :
			"failed"));

			boolean verified = archivingClient.verifyFileInArchive(archive,
			archivedDoc);
			System.out.println("Verification " + ((verified) ? "successful" : "failed"));
			}
			}
		