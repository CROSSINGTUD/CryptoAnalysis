package test.finitestatemachine;

public class MessageDigestTest extends FiniteStateMachineTestingFramework {

    public MessageDigestTest() {
        super("MessageDigest");
        this.order =
                new Simple(
                        new E("getInstance"),
                        new Plus(
                                new Or(
                                        new E("digest"),
                                        new Simple(new Plus(new E("update")), new E("digest")))));
    }
    // Gets, (DWOU | (Updates+, Digests))+

}
