package test.finitestatemachine;

public class KeyFactoryTest extends FiniteStateMachineTestingFramework{
	
	public KeyFactoryTest() {
		super("KeyFactory");
		this.order = new Simple(new E("getInstance"), new Star(new Or(new Star(new E("generatePrivate")), new Star(new E("generatePublic")))));
	}
	// Gets, (GenPriv* | GenPubl*)*

}
