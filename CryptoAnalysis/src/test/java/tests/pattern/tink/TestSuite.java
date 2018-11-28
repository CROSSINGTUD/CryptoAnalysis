package tests.pattern.tink;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	TestAEADCipher.class,
	TestDeterministicAEADCipher.class,
	TestDigitalSignature.class, 
	TestHybridEncryption.class, 
	TestMAC.class,
	TestStreamingAEADCipher.class
})
public class TestSuite {

}
