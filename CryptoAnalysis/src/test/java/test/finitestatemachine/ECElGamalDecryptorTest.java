package test.finitestatemachine;

import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;

public class ECElGamalDecryptorTest extends FiniteStateMachineTestingFramework{
	
	private Order order;

	public ECElGamalDecryptorTest() {
		super("ECElGamalDecryptor", Ruleset.BouncyCastle);
		
		this.order = new Simple(new E("ECElGamalDecryptor"), new Plus(new Simple(new E("init"), new Plus(new E("decrypt")))));

	}
	// Cons, (Inits, Decrypts+)+
	
	@Test
	public void benchmark() {
		for(int i=0; i<10000; i++) {
			assertInSMG(String.join(",", order.get()));
		}
	}
}
