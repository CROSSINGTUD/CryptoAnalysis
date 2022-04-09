package test.finitestatemachine;

import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;

public class BufferedBlockCipherTest extends FiniteStateMachineTestingFramework{
	
	private Order order;

	public BufferedBlockCipherTest() {
		super("BufferedBlockCipher", Ruleset.BouncyCastle);
		
		this.order = new Simple(new E("BufferedBlockCipher"), new Plus(new Simple(new E("init"), new E("processByte"), new E("doFinal"))));

	}
	// Cons, (Inits, Procs, DOFINALS)+
	
	@Test
	public void benchmark() {
		for(int i=0; i<10000; i++) {
			assertInSMG(String.join(",", order.get()));
		}
	}
}
