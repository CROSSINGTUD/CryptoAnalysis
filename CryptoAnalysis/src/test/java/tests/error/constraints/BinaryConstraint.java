package tests.error.constraints;

public class BinaryConstraint {

    public BinaryConstraint() {}

    public void implication1(@SuppressWarnings("unused") String s) {}

    public void implication2(@SuppressWarnings("unused") int i) {}

    public void implication(
            @SuppressWarnings("unused") String s, @SuppressWarnings("unused") int i) {}

    public void or1(@SuppressWarnings("unused") String s) {}

    public void or2(@SuppressWarnings("unused") int i) {}

    public void or(@SuppressWarnings("unused") String s, @SuppressWarnings("unused") int i) {}

    public void and1(@SuppressWarnings("unused") String s) {}

    public void and2(@SuppressWarnings("unused") int i) {}

    public void and(@SuppressWarnings("unused") String s, @SuppressWarnings("unused") int i) {}
}
