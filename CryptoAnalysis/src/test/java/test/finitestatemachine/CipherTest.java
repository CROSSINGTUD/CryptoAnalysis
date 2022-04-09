package test.finitestatemachine;

import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;

public class CipherTest extends FiniteStateMachineTestingFramework{
	
	private Order order;

	public CipherTest() {
		super("Cipher", Ruleset.JavaCryptographicArchitecture);
		
		this.order = new Simple(new E("getInstance"), new Plus(new E("init")), new Or(new Plus(new E("wrap")), new Plus(new Or(new E("doFinal"), new Simple(new Plus(new E("update")), new E("doFinal"))))));

	}
	// Gets, Inits+, WKB+ | (FINWOU | (Updates+, DOFINALS))+
	
	@Test
	public void benchmark() {
		for(int i=0; i<10000; i++) {
			assertInSMG(String.join(",", order.get()));
		}
	}
	
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
