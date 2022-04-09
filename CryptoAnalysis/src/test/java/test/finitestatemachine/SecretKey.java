package test.finitestatemachine;

import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;

public class SecretKey extends FiniteStateMachineTestingFramework{
	
	public SecretKey() {
		super("SecretKey", Ruleset.JavaCryptographicArchitecture);
		this.order = new Simple(new Star(new E("getEncoded")), new Opt(new E("destroy")));
	}
	// GetEnc*, Destroy?

}
