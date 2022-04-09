package test.finitestatemachine;

import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;

public class ECElGamalDecryptorTest extends FiniteStateMachineTestingFramework{
	
	public ECElGamalDecryptorTest() {
		super("ECElGamalDecryptor", Ruleset.BouncyCastle);
		order = new Simple(new E("ECElGamalDecryptor"), new Plus(new Simple(new E("init"), new Plus(new E("decrypt")))));
	}
	// Cons, (Inits, Decrypts+)+
	
}
