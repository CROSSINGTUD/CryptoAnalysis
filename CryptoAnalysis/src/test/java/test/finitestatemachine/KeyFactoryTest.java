package test.finitestatemachine;

import crypto.analysis.CrySLRulesetSelector.Ruleset;

public class KeyFactoryTest extends FiniteStateMachineTestingFramework{
	
	public KeyFactoryTest() {
		super("KeyFactory", Ruleset.JavaCryptographicArchitecture);
		this.order = new Simple(new E("getInstance"), new Star(new Or(new Star(new E("generatePrivate")), new Star(new E("generatePublic")))));
	}
	// Gets, (GenPriv* | GenPubl*)*

}
