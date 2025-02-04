package tests.error.predicate.implication;

public class Generator {

    public Generator(@SuppressWarnings("unused") boolean constraint) {}

    public Receiver generateReceiver() {
        return new Receiver();
    }

    public void ensureParameter(@SuppressWarnings("unused") String s) {}
}
