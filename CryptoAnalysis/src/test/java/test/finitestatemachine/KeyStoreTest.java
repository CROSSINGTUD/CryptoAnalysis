package test.finitestatemachine;

public class KeyStoreTest extends FiniteStateMachineTestingFramework {

    public KeyStoreTest() {
        super("KeyStore");
        this.order =
                new Simple(
                        new E("getInstance"),
                        new E("load"),
                        new Star(
                                new Or(
                                        new Simple(new Opt(new E("getEntry")), new E("getKey")),
                                        new Simple(new E("setEntry"), new E("store")))));
    }
    // Gets, Loads, ((GetEntry?, GetKey) | (SetEntry, Stores))*

}
