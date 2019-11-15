package crypto.rules;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import crypto.cryptslhandler.CrySLModelReader;

public class CryptSLRuleReader {

	private static CrySLModelReader csmr;

	private static CrySLModelReader getReader(){
		if (csmr == null)
		{
			try {
				csmr = new CrySLModelReader();
			}
			catch (MalformedURLException e){
				e.printStackTrace();
				// Sebastian:
				// TODO: Current code could cause a NullPointerException
				// Question: Is this Exception ever likely to happen?
				// If no: Swallow it and 'accept' the NullPointerException
				// If it can happen: Maybe we should re-throw a different exception (with this as the inner exception)
				// Reason: In both cases (either NullPointerException or a custom) the creation of CryptSLRules
				// is impossible if the MalformedURLException was thrown. So instead of allowing a generic NullPointerException
				// we could throw something new that could be caught.
			}
		}
		return csmr;
	}

	public static CryptSLRule readFromSourceFile(File file) {
		return getReader().readRule(file);
	}

    public static List<CryptSLRule> readFromDirectory(File directory) {
        return readFromDirectory(directory, false);
    }

    public static List<CryptSLRule> readFromDirectory(File directory, boolean recursive) {
        if (!directory.exists() || !directory.isDirectory())
            return new ArrayList<>();

        List<File> cryptSLFiles = new ArrayList<>();
        findCryptSLFiles(directory, recursive, cryptSLFiles);
        if (cryptSLFiles.size() == 0)
            return new ArrayList<>();

        CrySLModelReader reader = getReader();
        List<CryptSLRule> rules = new ArrayList<>();
        for (File file :cryptSLFiles)
            rules.add(reader.readRule(file));

        // TODO: Decide what happens with potential duplicates
        return rules.stream().filter((x) -> x != null).collect(Collectors.toList());
    }

    // TODO: Discuss about .zip layout: Only Root or allow recursive.
    public static List<CryptSLRule> readFromZipFile(File file) {
        if (!file.exists() || !file.isFile() || !file.getName().endsWith(".zip"))
            return new ArrayList<>();

        List<CryptSLRule> rules = new ArrayList<>();

        try {
            ZipFile zip = new ZipFile(file);
            for (Enumeration e = zip.entries(); e.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) e.nextElement();
                if (!entry.isDirectory()) {
                    CryptSLRule rule = getCrySLRuleFromZipEntry(entry, zip, file);
                    rules.add(rule);
                }
            }
        }
        catch (IOException e) {
            return new ArrayList<>();
        }

        // TODO: Decide what happens with potential duplicates
        return rules.stream().filter((x) -> x != null).collect(Collectors.toList());
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

    private  static CryptSLRule getCrySLRuleFromZipEntry(ZipEntry entry, ZipFile zip, File zipFile)
    {
        if (entry.isDirectory() || !entry.getName().endsWith(CrySLModelReader.cryslFileEnding))
            return null;
        try {
            String name = createUniqueZipEntryName(zipFile, entry);
            CryptSLRule rule = getReader().readRule(zip.getInputStream(entry), name);
            return rule;
        }
        catch (IOException ex) {
            return null;
        }
    }

    // For zip file entries there is no real URI. Using the raw absolute path of the zip file will cause a exception
    // when trying to resolve/create the resource in the CrySLModelReader:readRule() methods.
    // Solution: Create a custom URI with the following scheme:
    // uri := [HexHashedAbsoluteZipFilePath][SystemFileSeparator][ZipEntryName]
    // This scheme has the properties that it still is unique system-wide,
    // The hash will be the same for the same file, so you could know if two rules come from the same ruleset file
    // and you still can get the information of the zipped file.
    private static String createUniqueZipEntryName(File zipFile, ZipEntry zipEntry) {
        if (!zipFile.exists() || !zipFile.isFile() || zipEntry == null)
            return null;
        StringBuilder sb = new StringBuilder();

        String partFileName;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(zipFile.getAbsolutePath().getBytes());
            partFileName = bytesToHex(messageDigest.digest());
        }
        catch (NoSuchAlgorithmException e) {
            partFileName = zipFile.getName();
        }

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
