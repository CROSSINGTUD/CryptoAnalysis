package test.finitestatemachine;

import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;

public class SecureRandom extends FiniteStateMachineTestingFramework{
	
	public SecureRandom() {
		super("SecureRandom", Ruleset.JavaCryptographicArchitecture);
		
		this.order = new Simple(new E("getInstance"), new Star(new Simple(new Opt(new E("setSeed")), new Star(new E("generateSeed")))));

	}
	// Ins, (Seeds?, Ends*)*

}
