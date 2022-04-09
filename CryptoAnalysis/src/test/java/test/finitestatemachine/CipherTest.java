package test.finitestatemachine;

import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;
import test.IDEALCrossingTestingFramework;

public class CipherTest extends FiniteStateMachineTestingFramework{

	public CipherTest() {
		super("Cipher", Ruleset.JavaCryptographicArchitecture);
	}
	// Gets, Inits+, WKB+ | (FINWOU | (Updates+, DOFINALS))+
	
	@Test
	public void assertTrue() {
		assertInSMG("getInstance,init,wrap");
		assertInSMG("getInstance,init,doFinal");
		assertInSMG("getInstance,init,update,doFinal");
		
		assertInSMG("getInstance,init,init,wrap");
		assertInSMG("getInstance,init,init,doFinal");
		assertInSMG("getInstance,init,init,update,doFinal");
		
		assertInSMG("getInstance,init,wrap,wrap");
		assertInSMG("getInstance,init,update,update,doFinal");
		
		assertInSMG("getInstance,init,doFinal,doFinal");
		assertInSMG("getInstance,init,update,doFinal,doFinal");
		assertInSMG("getInstance,init,update,doFinal,update,doFinal");
	}
	
	@Test
	public void assertFalse() {
		assertNotInSMG("init");
		assertNotInSMG("getInstance,wrap");
		assertNotInSMG("getInstance,init,wrap,doFinal");
		assertNotInSMG("getInstance,init,wrap,init,doFinal");
	}
}
