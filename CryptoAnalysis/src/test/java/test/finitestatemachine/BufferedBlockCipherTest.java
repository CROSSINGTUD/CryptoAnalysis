package test.finitestatemachine;

import crypto.analysis.CrySLRulesetSelector.Ruleset;

public class BufferedBlockCipherTest extends FiniteStateMachineTestingFramework{
	
	public BufferedBlockCipherTest() {
		super("BufferedBlockCipher", Ruleset.BouncyCastle);
		this.order = new Simple(new E("BufferedBlockCipher"), new Plus(new Simple(new E("init"), new E("processByte"), new E("doFinal"))));
	}
	// Cons, (Inits, Procs, DOFINALS)+
	
}
