
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
	
import java.util.List;
import javax.ws.rs.core.Response;
import de.tu_darmstadt.cs.cdc.mops.services.ServiceType;
import org.apache.commons.io.IOUtils;
import de.tu_darmstadt.cs.cdc.mops.model.archiving_system.Document;

				public class LongTermArchivingClient {

				private ArchivingSystem archivingSystem;
				private ArchiveConfiguration archiveConfig;
				private static String url =	"https://moltas.cdc.informatik.tu-darmstadt.de";

				public LongTermArchivingClient() throws ServiceClientCreationException {
				if (archivingSystem == null) {
				this.archivingSystem = (ArchivingSystem)
				ServiceClientCreator.createServiceClient(ServiceType.ARCHIVING_SYSTEM, url + "/archiving-system");

				archiveConfig = new ArchiveConfiguration();
				archiveConfig.setDataStructure(DataStructureType.
				
						NOTARIAL_ATTESTATION_WRAPPER
					);
				archiveConfig.setScheme(Scheme.MODULAR);
				archiveConfig.setAddingNewDocuments(false);
				archiveConfig.setMultipleDocuments(false);
				Set<AttestationTechnique> attsTec = new HashSet<AttestationTechnique>();
				
					attsTec.add(AttestationTechnique.SIGNATURE_BASED_TIMESTAMPING);
					attsTec.add(AttestationTechnique.NOTARISATION);
				
				archiveConfig.setAttestationTechniques(attsTec);
				}
				}

				public Archive createArchive(String archiveName) throws
				InternalServiceErrorException, IOException {
				return archivingSystem.createArchive(archiveName, archiveConfig);
				}

				public void renameArchive(long archiveId, String newName) throws
				EntityNotFoundException, InternalServiceErrorException {
				archivingSystem.renameArchive(archiveId, newName);
				}

				public List<Archive> getArchives() throws
				InternalServiceErrorException {
				return archivingSystem.getArchives();
				}

				public void deleteArchive(long archiveID) throws EntityNotFoundException,
				InternalServiceErrorException, IOException {
				archivingSystem.deleteArchive(archiveID);
				}

				public void prepareFileForArchiving(String documentName, File
				documentFile, File mopsFile, final String keyStorePath, final String
				keyStorePassword) throws Exception {
				if (!mopsFile.exists()) {
				mopsFile.createNewFile();
				}

				XadesBesSigner signer = new XadesBesSigner(new File(keyStorePath),
				keyStorePassword);

				// prepare a zip output stream that does no compression
				OutputStream outputStream = Files.newOutputStream(mopsFile.toPath());
				ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
				zipOutputStream.setLevel(Deflater.NO_COMPRESSION);

				// copy the document into the zip file
				zipOutputStream.putNextEntry(new ZipEntry(documentName));
				IOUtils.copy(Files.newInputStream(documentFile.toPath()),
				zipOutputStream);

				// sign the document and copy the signature into the zip file
				zipOutputStream.putNextEntry(new ZipEntry(documentName +
				".sig.xml"));
				signer.sign(documentFile, zipOutputStream);

				zipOutputStream.close();
				}

				public Document addFileToArchive(Archive arch, File archFile) {
				List<ArchiveElement> elements;
				try {
				elements = archivingSystem.importArchiveElements(new
				FileInputStream(archFile));
				UpdateParameters updaters = new UpdateParameters();
				updaters.setNewDigestMethod(true);
				final Set<AttestationTechnique> attestationTechniques =
				archiveConfig.getAttestationTechniques();
				if
				(attestationTechniques.contains(AttestationTechnique.SIGNATURE_BASED_TIMESTAMPING))
				{
				updaters.setServiceAddress(url + "/tsa");
				updaters.setAttestationTechnique(AttestationTechnique.SIGNATURE_BASED_TIMESTAMPING);
				} else {
				updaters.setServiceAddress(url + "/notary");
				updaters.setAttestationTechnique(AttestationTechnique.NOTARISATION);
				}
				updaters.setDigestMethodName("SHA-256");
				return archivingSystem.addDocument(arch.getId(), elements.get(0).getId(),
				updaters);
				} catch (InternalServiceErrorException | IOException | XMLException |
				CoreServiceException | EntityNotFoundException e) {
				return null;
				}
				}

				public File retrieveFileFromArchive(Archive arch, Document file, String
				filepath) throws IOException, EntityNotFoundException,
				InternalServiceErrorException {
				Response resp = archivingSystem.exportDocument(arch.getId(), file.getId());
				InputStream ins = ((InputStream) resp.getEntity());

				File retrievedFile = new File(filepath);
				retrievedFile.createNewFile();
				OutputStream outputStream = new FileOutputStream(retrievedFile);
				IOUtils.copy(ins, outputStream);
				outputStream.close();
				return retrievedFile;
				}

				public boolean verifyFileInArchive(Archive arch, Document file) {
				try {
				return archivingSystem.verifyDocument(file.getId()) == null;
				} catch (EntityNotFoundException | InternalServiceErrorException e) {
				return false;
				}
				}

				}
			