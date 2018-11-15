package tests.pattern;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import org.junit.Test;

import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class ExtractProviderTest extends UsagePatternTestingFramework{

	@Test
	public void UsagePatternTestExtractProvider1() throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
	  MessageDigest md = MessageDigest.getInstance("SHA-256", "BC");
	  Assertions.extValue(1);
	}
	
	@Test
	public void UsagePatternTestExtractProvider2() throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
	  Cipher md = Cipher.getInstance("SHA-256", "BC");
	  Assertions.extValue(1);
	}
	
	@Test
	public void UsagePatternTestExtractProvider3() throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
	  MessageDigest md = MessageDigest.getInstance("SHA-256", "GT");
	  Assertions.extValue(1);
	}
	
	@Test
	public void UsagePatternTestExtractProvider4() throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
	  Cipher md = Cipher.getInstance("SHA-256", "GT");
	  Assertions.extValue(1);
	}
	
}
