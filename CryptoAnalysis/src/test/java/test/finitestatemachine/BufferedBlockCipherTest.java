package test.finitestatemachine;

public class BufferedBlockCipherTest extends FiniteStateMachineTestingFramework {

    public BufferedBlockCipherTest() {
        super("BufferedBlockCipher");
        this.order =
                new Simple(
                        new E("BufferedBlockCipher"),
                        new Plus(
                                new Simple(new E("init"), new E("processByte"), new E("doFinal"))));
    }
    // Cons, (Inits, Procs, DOFINALS)+

}
