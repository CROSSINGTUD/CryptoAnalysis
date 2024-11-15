package test.finitestatemachine;

public class SHA256DigestTest extends FiniteStateMachineTestingFramework {

    public SHA256DigestTest() {
        super("SHA256Digest");

        this.order =
                new Simple(
                        new E("SHA256Digest"),
                        new Star(new Simple(new Plus(new E("update")), new E("doFinal"))));
    }
    // Cons, (Updates+, Finals)*

}
