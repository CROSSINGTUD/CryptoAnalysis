package tests.error.constraints;

public class ValueConstraint {

    public ValueConstraint(@SuppressWarnings("unused") String s) {}

    public void operation1(@SuppressWarnings("unused") int i) {}

    public void operation2(
            @SuppressWarnings("unused") String s, @SuppressWarnings("unused") int i) {}

    public void operation3(@SuppressWarnings("unused") String s) {}
}
