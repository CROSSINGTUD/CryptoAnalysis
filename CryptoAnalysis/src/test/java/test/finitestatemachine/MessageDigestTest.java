package test.finitestatemachine;

import crypto.analysis.CrySLRulesetSelector.Ruleset;

public class MessageDigestTest extends FiniteStateMachineTestingFramework{
	
	public MessageDigestTest() {
		super("MessageDigest", Ruleset.JavaCryptographicArchitecture);
		this.order = new Simple(new E("getInstance"), new Plus(new Or(new E("digest"), new Simple(new Plus(new E("update")), new E("digest")))));
	}
	// Gets, (DWOU | (Updates+, Digests))+

}
