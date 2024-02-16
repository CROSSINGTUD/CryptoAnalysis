package tests.custom.predicate;

public class Source {

    public void causeConstraintError(boolean value) {}

    public SimpleTarget generateTarget() {
        return new SimpleTarget();
    }

    public TargetWithAlternatives generateTargetWithAlternatives() {
        return new TargetWithAlternatives();
    }

}
