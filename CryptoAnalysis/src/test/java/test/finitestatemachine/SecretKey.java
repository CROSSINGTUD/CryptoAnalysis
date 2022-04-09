package test.finitestatemachine;

import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;

public class SecretKey extends FiniteStateMachineTestingFramework{
	
	private Order order;

	public SecretKey() {
		super("SecretKey", Ruleset.JavaCryptographicArchitecture);
		
		this.order = new Simple(new Star(new E("getEncoded")), new Opt(new E("destroy")));

	}
	// GetEnc*, Destroy?
	
	@Test
	public void benchmark() {
		for(int i=0; i<10000; i++) {
			assertInSMG(String.join(",", order.get()));
		}
	}
}
