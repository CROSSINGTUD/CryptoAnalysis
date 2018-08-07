import java.io.File;


import Crypto.LongTermArchivingClient;
import de.tu_darmstadt.cs.cdc.mops.model.archiving_system.Archive;

public class Main {

	
//	templateUsage(archiveName, pathName, fileName, keyStorePath, keyStorePassword);
	public static void main(String... args) throws Exception {
		String archiveName = "TestArchive";
		String pathName = ".\\bin\\";
		String fileName = "test.txt";
		final String keyStorePath = ".\\bin\\sk.p12";
		final String keyStorePassword = "1234";

		LongTermArchivingClient archivingClient = new LongTermArchivingClient();
		Archive archive = archivingClient.createArchive(archiveName);

		final File toBeArchivedFile = new File(fileName + ".mops.zip");
		archivingClient.prepareFileForArchiving(fileName, new File(pathName + fileName), toBeArchivedFile, keyStorePath,
				keyStorePassword);

		de.tu_darmstadt.cs.cdc.mops.model.archiving_system.Document archivedDoc = archivingClient.addFileToArchive(archive, toBeArchivedFile);
		System.out.println("Document storage " + ((archivedDoc != null) ? "successful" : "failed"));

		File retrievedArchiveFile = archivingClient.retrieveFileFromArchive(archive, archivedDoc,
				pathName + "ret.mops.zip");
		System.out.println("File retrieval " + ((retrievedArchiveFile != null) ? "successful" : "failed"));

		boolean verified = archivingClient.verifyFileInArchive(archive, archivedDoc);
		System.out.println("Verification " + ((verified) ? "successful" : "failed"));
	}

}