package test.finitestatemachine;

public class SSLEngineTest extends FiniteStateMachineTestingFramework {

    public SSLEngineTest() {
        super("SSLEngine");
        this.order =
                new Or(
                        new Simple(new E("setEnabledCipherSuites"), new E("setEnabledProtocols")),
                        new Simple(new E("setEnabledProtocols"), new E("setEnabledCipherSuites")));
    }
    // (EnableCipher, EnableProtocol) | (EnableProtocol, EnableCipher)

}
