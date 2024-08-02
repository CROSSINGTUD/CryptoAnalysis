package tests.error.predicate.contradiction;

public class PredicateEnsurer {

    public PredicateEnsurer(boolean condition) {}

    public byte[] createPredicate() {
        return new byte[]{'t', 'e', 's', 't'};
    }

    public char[] createCondPredicate() {
        return new char[]{'t', 'e', 's', 't'};
    }
}
