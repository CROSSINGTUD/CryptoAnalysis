package test.finitestatemachine;

import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;

public class HMacTest extends FiniteStateMachineTestingFramework{
	
	private Order order;

	public HMacTest() {
		super("HMac", Ruleset.BouncyCastle);
		
		this.order = new Simple(new E("HMac"), new Plus(new Simple(new E("init"), new Plus(new E("update")), new E("doFinal"))));

	}
	// Cons, (Init, Updates+, Finals)+
	
	@Test
	public void benchmark() {
		for(int i=0; i<10000; i++) {
			assertInSMG(String.join(",", order.get()));
		}
	}
}
