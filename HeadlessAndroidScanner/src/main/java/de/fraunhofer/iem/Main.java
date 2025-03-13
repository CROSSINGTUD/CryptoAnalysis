package de.fraunhofer.iem;

import crypto.exceptions.CryptoAnalysisParserException;
import de.fraunhofer.iem.android.HeadlessAndroidScanner;

public class Main {

    public static void main(String[] args) {
        try {
            HeadlessAndroidScanner scanner = HeadlessAndroidScanner.createFromCLISettings(args);
            scanner.scan();
        } catch (CryptoAnalysisParserException e) {
            throw new RuntimeException("Error while parsing the CLI arguments: " + e.getMessage());
        }
    }
}
