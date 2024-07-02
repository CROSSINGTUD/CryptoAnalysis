package tests.error.predicate.requiredpredicateswiththis;

public class Source {

    public void causeConstraintError(boolean value) {}

    public SimpleTarget generateTarget() {
        return new SimpleTarget();
    }

    public TargetWithAlternatives generateTargetWithAlternatives() {
        return new TargetWithAlternatives();
    }

}
