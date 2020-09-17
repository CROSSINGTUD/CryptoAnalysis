package tests.headless;

import java.io.File;
import java.util.List;

import com.google.common.collect.Lists;
import org.junit.Ignore;
import org.junit.Test;


import crypto.HeadlessCryptoScanner;
import crypto.exceptions.CryptoAnalysisException;
import crypto.rules.CrySLRule;
import crypto.rules.CrySLRuleReader;

public class ExternalTest {

	@Ignore
	@Test
	public void testExternal(){
		HeadlessCryptoScanner scanner = new HeadlessCryptoScanner() {
			@Override
			protected String applicationClassPath() {
				return "YOUR/PATH";
			}

			@Override
			protected List<CrySLRule> getRules() {
				try {
					return CrySLRuleReader.readFromDirectory(new File("./src/main/resources/JavaCryptographicArchitecture"));
				} catch (CryptoAnalysisException e) {
					e.printStackTrace();
				}
				return Lists.newArrayList();
			}
		};

		scanner.exec();
	}
}
