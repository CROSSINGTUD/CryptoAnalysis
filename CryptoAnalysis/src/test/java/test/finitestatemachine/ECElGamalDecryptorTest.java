package test.finitestatemachine;

public class ECElGamalDecryptorTest extends FiniteStateMachineTestingFramework {

    public ECElGamalDecryptorTest() {
        super("ECElGamalDecryptor");
        order =
                new Simple(
                        new E("ECElGamalDecryptor"),
                        new Plus(new Simple(new E("init"), new Plus(new E("decrypt")))));
    }
    // Cons, (Inits, Decrypts+)+

}
