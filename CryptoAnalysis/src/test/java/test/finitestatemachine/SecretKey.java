package test.finitestatemachine;

public class SecretKey extends FiniteStateMachineTestingFramework {

    public SecretKey() {
        super("SecretKey");
        this.order = new Simple(new Star(new E("getEncoded")), new Opt(new E("destroy")));
    }
    // GetEnc*, Destroy?

}
