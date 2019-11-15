package crypto.rules;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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

    private static void findCryptSLFiles(File directory, boolean recursive, Collection<File> resultCollection) {
        for (File file: directory.listFiles())
        {
            if (file.isFile() && file.getName().endsWith(CrySLModelReader.cryslFileEnding))
                resultCollection.add(file);

            if (recursive && file.isDirectory())
                findCryptSLFiles(file, recursive, resultCollection);
        }
    }
}
