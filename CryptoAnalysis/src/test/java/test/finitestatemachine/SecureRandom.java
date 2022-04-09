package test.finitestatemachine;

import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;

public class SecureRandom extends FiniteStateMachineTestingFramework{
	
	private Order order;

	public SecureRandom() {
		super("SecureRandom", Ruleset.JavaCryptographicArchitecture);
		
		this.order = new Simple(new E("getInstance"), new Star(new Simple(new Opt(new E("setSeed")), new Star(new E("generateSeed")))));

	}
	// Ins, (Seeds?, Ends*)*
	
	@Test
	public void benchmark() {
		for(int i=0; i<10000; i++) {
			assertInSMG(String.join(",", order.get()));
		}
	}
}
