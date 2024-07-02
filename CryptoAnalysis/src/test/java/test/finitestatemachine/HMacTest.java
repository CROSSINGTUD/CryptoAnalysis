package test.finitestatemachine;

public class HMacTest extends FiniteStateMachineTestingFramework{

	public HMacTest() {
		super("HMac");
		this.order = new Simple(new E("HMac"), new Plus(new Simple(new E("init"), new Plus(new E("update")), new E("doFinal"))));
	}
	// Cons, (Init, Updates+, Finals)+

}
