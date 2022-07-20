package test.finitestatemachine;

import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;

public class CipherTest extends FiniteStateMachineTestingFramework{

	public CipherTest() {
		super("Cipher", Ruleset.JavaCryptographicArchitecture);
		this.order = new Simple(new E("getInstance"), new Plus(new E("init")), new Or(new Plus(new E("wrap")), new Plus(new Or(new E("doFinal"), new Simple(new Plus(new E("update")), new E("doFinal"))))));
	}
	// Gets, Inits+, WKB+ | (FINWOU | (Updates+, DOFINALS))+
	
}
