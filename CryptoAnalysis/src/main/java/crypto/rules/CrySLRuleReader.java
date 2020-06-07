package crypto.rules;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import crypto.cryslhandler.CrySLModelReader;
import crypto.exceptions.CryptoAnalysisException;


public class CrySLRuleReader {
	
	private static CrySLModelReader csmr;
	
	private static CrySLModelReader getReader(){
		if (csmr == null)
		{
			try {
				csmr = new CrySLModelReader();
			}
			catch (MalformedURLException e){
				e.printStackTrace();
			}
		}
		return csmr;
	}

	/**
	 * Returns a {@link CrySLRule} read from a single CrySL file.
	 * 
	 * @param file the CrySL file
	 * @return the {@link CrySLRule} object
	 * @throws CryptoAnalysisException Throws when a file could not get processed to a {@link CrySLRule}
	 */
	public static CrySLRule readFromSourceFile(File file) throws CryptoAnalysisException {
		return getReader().readRule(file);
	}

	/**
	 * Returns a {@link List} of {@link CrySLRule} objects read from a directory
	 * 
	 * @param directory the {@link File} with the directory where the rules are located
	 * @return the {@link List} with {@link CrySLRule} objects. If no rules are found it returns an empty list.
	 * @throws CryptoAnalysisException Throws when a file could not get processed to a {@link CrySLRule}
	 */
	public static List<CrySLRule> readFromDirectory(File directory) throws CryptoAnalysisException {
		return readFromDirectory(directory, false);
	}

	/**
	 * Returns a {@link List} of {@link CrySLRule} objects read from a directory.
	 * In the case the directory contains further sub directories, they can also searched 
	 * if the recursive argument is <code>true</code>.
	 * 
	 * @param directory the {@link File} with the directory where the rules are located
	 * @param recursive <code>true</code> the subfolders will be searched too
	 * @return the {@link List} with {@link CrySLRule} objects. If no rules are found it returns an empty list.
	 * @throws CryptoAnalysisException Throws when a file could not get processed to a {@link CrySLRule}
	 */
	public static List<CrySLRule> readFromDirectory(File directory, boolean recursive) throws CryptoAnalysisException {
		Map<String, CrySLRule> ruleMap = new HashMap<String, CrySLRule>();

		if (!directory.exists() || !directory.isDirectory())
			throw new CryptoAnalysisException("The specified path is not a directory " + directory.getAbsolutePath());

		List<File> cryptSLFiles = new ArrayList<>();
		findCryptSLFiles(directory, recursive, cryptSLFiles);

		CrySLModelReader reader = getReader();
		for (File file : cryptSLFiles) {
			CrySLRule rule = reader.readRule(file);

			if(rule != null) {
				if(!ruleMap.containsKey(rule.getClassName())) {
					ruleMap.put(rule.getClassName(), rule);
				}
			}
		}
		
		return new ArrayList<>(ruleMap.values());
	}

	/**
	 * Returns a {@link List} of {@link CrySLRule} objects read from a Zip {@link File}.
	 * @param file Zip that contains the CrySL files
	 * @return the {@link List} with {@link CrySLRule} objects. If no rules are found it returns an empty list.
	 * @throws CryptoAnalysisException 
	 */
	public static List<CrySLRule> readFromZipFile(File file) throws CryptoAnalysisException {
		if (!file.exists() || !file.isFile() || !file.getName().endsWith(".zip"))
			throw new CryptoAnalysisException("The specified path is not a ZIP file " + file.getAbsolutePath());

		Map<String, CrySLRule> ruleMap = new HashMap<String, CrySLRule>();
		try {
			ZipFile zip = new ZipFile(file);
			for (Enumeration e = zip.entries(); e.hasMoreElements(); ) {
				ZipEntry entry = (ZipEntry) e.nextElement();
				if (!entry.isDirectory()) {
					CrySLRule rule = getCrySLRuleFromZipEntry(entry, zip, file);
					if(rule != null) {
						if(!ruleMap.containsKey(rule.getClassName())) {
							ruleMap.put(rule.getClassName(), rule);
						}
					}
				} 
			}
		}
		catch (IOException e) {
			throw new CryptoAnalysisException(e.getMessage());
		}
		
		return new ArrayList<>(ruleMap.values());
	}	

	private static void findCryptSLFiles(File directory, boolean recursive, Collection<File> resultCollection) {
		for (File file: directory.listFiles())
		{
			if (file.isFile() && file.getName().endsWith(CrySLModelReader.cryslFileEnding))
				resultCollection.add(file);

			if (recursive && file.isDirectory())
				findCryptSLFiles(file, recursive, resultCollection);
		}
	}

	private static CrySLRule getCrySLRuleFromZipEntry(ZipEntry entry, ZipFile zip, File zipFile) throws CryptoAnalysisException
	{
		if (entry.isDirectory() || !entry.getName().endsWith(CrySLModelReader.cryslFileEnding))
			throw new CryptoAnalysisException("ZIP entry is a directory or not a CrySL file");
		
		CrySLRule rule = null;
		try {
			String name = createUniqueZipEntryName(zipFile, entry);
			rule = getReader().readRule(zip.getInputStream(entry), name);
		}
		catch (IllegalArgumentException | IOException | NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		}
		return rule;

	}

	// For zip file entries there is no real URI. Using the raw absolute path of the zip file will cause a exception
	// when trying to resolve/create the resource in the CrySLModelReader:readRule() methods.
	// Solution: Create a custom URI with the following scheme:
	// uri := [HexHashedAbsoluteZipFilePath][SystemFileSeparator][ZipEntryName]
	// This scheme has the properties that it still is unique system-wide,
	// The hash will be the same for the same file, so you could know if two rules come from the same ruleset file
	// and you still can get the information of the zipped file.
	private static String createUniqueZipEntryName(File zipFile, ZipEntry zipEntry) throws NoSuchAlgorithmException, CryptoAnalysisException {
		if (!zipFile.exists() || !zipFile.isFile() || zipEntry == null)
			throw new CryptoAnalysisException("The specified path is not a ZIP file " + zipFile.getAbsolutePath());
		
		StringBuilder sb = new StringBuilder();

		String partFileName;
		
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		messageDigest.update(zipFile.getAbsolutePath().getBytes());
		partFileName = bytesToHex(messageDigest.digest());
	

		sb.append(partFileName);
		sb.append(File.separator);
		sb.append(zipEntry.getName());
		return sb.toString();
	}

	private static String bytesToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes)
			sb.append(String.format("%02x", b));
		return sb.toString();
	}
}
