package test.finitestatemachine;

import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;

public class SHA256DigestTest extends FiniteStateMachineTestingFramework{
	
	private Order order;

	public SHA256DigestTest() {
		super("SHA256Digest", Ruleset.BouncyCastle);
		
		this.order = new Simple(new E("SHA256Digest"), new Star(new Simple(new Plus(new E("update")), new E("doFinal"))));

	}
	// Cons, (Updates+, Finals)*
	
	@Test
	public void benchmark() {
		for(int i=0; i<10000; i++) {
			assertInSMG(String.join(",", order.get()));
		}
	}
}
