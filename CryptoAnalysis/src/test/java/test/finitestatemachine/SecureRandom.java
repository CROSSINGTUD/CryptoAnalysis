package test.finitestatemachine;

public class SecureRandom extends FiniteStateMachineTestingFramework {

    public SecureRandom() {
        super("SecureRandom");

        this.order =
                new Simple(
                        new E("getInstance"),
                        new Star(
                                new Simple(
                                        new Opt(new E("setSeed")),
                                        new Star(new E("generateSeed")))));
    }
    // Ins, (Seeds?, Ends*)*

}
