package test.finitestatemachine;

import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;

public class KeyFactoryTest extends FiniteStateMachineTestingFramework{
	
	private Order order;

	public KeyFactoryTest() {
		super("KeyFactory", Ruleset.JavaCryptographicArchitecture);
		
		this.order = new Simple(new E("getInstance"), new Star(new Or(new Star(new E("generatePrivate")), new Star(new E("generatePublic")))));

	}
	// Gets, (GenPriv* | GenPubl*)*
	
	@Test
	public void benchmark() {
		for(int i=0; i<10000; i++) {
			assertInSMG(String.join(",", order.get()));
		}
	}
}
