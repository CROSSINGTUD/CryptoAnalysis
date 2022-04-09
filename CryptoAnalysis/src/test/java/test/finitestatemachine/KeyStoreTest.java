package test.finitestatemachine;

import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;

public class KeyStoreTest extends FiniteStateMachineTestingFramework{
	
	public KeyStoreTest() {
		super("KeyStore", Ruleset.JavaCryptographicArchitecture);
		this.order = new Simple(new E("getInstance"), new E("load"), 
				new Star(new Or(new Simple(new Opt(new E("getEntry")), new E("getKey")), 
						new Simple(new E("setEntry"), new E("store")))));

	}
	// Gets, Loads, ((GetEntry?, GetKey) | (SetEntry, Stores))*

}
