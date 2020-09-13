package crypto;

import crypto.analysis.CryptoScannerSettings;
import crypto.exceptions.CryptoAnalysisParserException;

public class HeadlessCryptoScannerTest {

	public static void main(String[] args) {
		CryptoScannerSettings settings = new CryptoScannerSettings();
		try {
			settings.parseSettingsFromCLI(args);
		} catch (CryptoAnalysisParserException e) {
			e.printStackTrace();
		}
	}
}
