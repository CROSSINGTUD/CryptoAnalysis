package tests.pattern;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

import org.junit.Test;

import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class ExtractProviderTest extends UsagePatternTestingFramework{

	@Test
	public void UsagePatternTestExtractProvider1() throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
	  MessageDigest md = MessageDigest.getInstance("SHA-256", "BC");
	//Added assertion for Provider Detection
	  Assertions.extValue(1);
	}
	
}
