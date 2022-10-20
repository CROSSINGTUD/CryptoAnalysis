package test.finitestatemachine;

import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;

public class SSLEngineTest extends FiniteStateMachineTestingFramework{
	
	public SSLEngineTest() {
		super("SSLEngine", Ruleset.JavaCryptographicArchitecture);
		this.order = new Or(new Simple(new E("setEnabledCipherSuites"), new E("setEnabledProtocols")), new Simple(new E("setEnabledProtocols"), new E("setEnabledCipherSuites")));
	}
	// (EnableCipher, EnableProtocol) | (EnableProtocol, EnableCipher) 

}
