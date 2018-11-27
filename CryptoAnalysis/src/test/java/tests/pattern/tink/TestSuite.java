package tests.pattern.tink;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	TestAEADCipher.class,
	TestMAC.class,
	TestDeterministicAEADCipher.class
})
public class TestSuite {

}
