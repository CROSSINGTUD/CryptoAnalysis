package crypto.cryslhandler;

import crypto.exceptions.CryptoAnalysisException;
import crypto.rules.CrySLRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class CrySLRuleReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrySLRule.class);
    private static final String CRYSL_FILE_ENDING = ".crysl";

    public Collection<CrySLRule> readRulesFromPath(String path) throws IOException {
        if (isZipFile(path)) {
            return readRulesFromZipArchive(path);
        }

        return readRulesFromDirectory(path);
    }

    public Collection<CrySLRule> readRulesFromDirectory(String path) throws IOException {
        File directory = new File(path);
        if (!directory.exists()) {
            throw new FileNotFoundException("Directory " + path + " does not exist");
        }

        if (!directory.isDirectory()) {
            throw new IOException(path + " is not a directory");
        }

        Collection<File> files = Arrays.asList(directory.listFiles());
        return readRulesFromFiles(files);
    }

    public Collection<CrySLRule> readRulesFromFiles(Collection<File> files) {
        Collection<CrySLRule> result = new HashSet<>();
        for (File file : files) {
            try {
                CrySLRule rule = readRuleFromFile(file);

                if (result.contains(rule)) {
                    LOGGER.warn("Rule for class {} appears multiple times", rule.getClassName());
                    continue;
                }

                result.add(rule);
            } catch (CryptoAnalysisException e) {
                LOGGER.warn(e.toString());
            }
        }
        return result;
    }

    public CrySLRule readRuleFromFile(File file) throws CryptoAnalysisException {
        String fileName = file.getName();
        if (!fileName.endsWith(CRYSL_FILE_ENDING)) {
            throw new CryptoAnalysisException("The extension of " + fileName + " does not match " + CRYSL_FILE_ENDING);
        }

        CrySLModelReader modelReader = new CrySLModelReader();
        return modelReader.readRule(file);
    }

    private boolean isZipFile(String path) {
        File file = new File(path);

        // Copied from https://stackoverflow.com/questions/33934178/how-to-identify-a-zip-file-in-java
        int fileSignature;

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            fileSignature = raf.readInt();
        } catch (IOException e) {
            return false;
        }
        return fileSignature == 0x504B0304 || fileSignature == 0x504B0506 || fileSignature == 0x504B0708;
    }

    public Collection<CrySLRule> readRulesFromZipArchive(String path) throws IOException {
        try(ZipFile zipFile = new ZipFile(path)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                if (entry.isDirectory()) {
                    continue;
                }

                // TODO
                InputStream inputStream = zipFile.getInputStream(entry);
                inputStream.close();
            }
        }
        return Collections.emptySet();
    }

}
