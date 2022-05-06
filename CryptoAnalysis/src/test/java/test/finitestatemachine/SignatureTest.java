package test.finitestatemachine;

import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;

public class SignatureTest extends FiniteStateMachineTestingFramework{
	
	public SignatureTest() {
		super("Signature", Ruleset.JavaCryptographicArchitecture);
		this.order = new Simple(new E("getInstance"),
				new Or(
						new Simple(new Plus(new E("initSign")), new Plus(new Simple(new Plus( new E("update")), new Plus(new E("sign"))))),
						new Simple(new Plus(new E("initVerify")), new Plus(new Simple(new Star( new E("update")), new Plus(new E("verify")))))
						)
				);
	}
	// THIS HAS TO MANY REPEATITIONS
	// The Test framework is not able to generate all possible paths for the following
	// Gets, ((InitSigns+, (Updates+, Signs+)+ )+ | (InitVerifies+, (Updates*, Verifies+)+ )+ )

	// We simplify it to
	// Gets, ( InitSigns+, (Updates+, Signs+)+ | InitVerifies+, (Updates*, Verifies+)+ )
}
