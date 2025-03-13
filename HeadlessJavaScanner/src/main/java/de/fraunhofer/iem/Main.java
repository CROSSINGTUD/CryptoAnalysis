package de.fraunhofer.iem;

import crypto.exceptions.CryptoAnalysisParserException;
import de.fraunhofer.iem.scanner.HeadlessJavaScanner;

public class Main {

    public static void main(String[] args) {
        try {
            HeadlessJavaScanner scanner = HeadlessJavaScanner.createFromCLISettings(args);
            scanner.scan();
        } catch (CryptoAnalysisParserException e) {
            throw new RuntimeException("Error while parsing the CLI arguments: " + e.getMessage());
        }
    }
}
