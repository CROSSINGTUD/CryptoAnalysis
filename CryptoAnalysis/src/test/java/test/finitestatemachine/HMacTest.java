package test.finitestatemachine;

import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;

public class HMacTest extends FiniteStateMachineTestingFramework{

	public HMacTest() {
		super("HMac", Ruleset.BouncyCastle);
		this.order = new Simple(new E("HMac"), new Plus(new Simple(new E("init"), new Plus(new E("update")), new E("doFinal"))));
	}
	// Cons, (Init, Updates+, Finals)+

}
