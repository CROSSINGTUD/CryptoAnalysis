package tests.error.predicate.requiredpredicateswiththis;

public class Source {

    public void causeConstraintError(boolean value) {}

    public SimpleTarget generateTarget() {
        return new SimpleTarget();
    }

    public TargetWithAlternatives generateTargetWithAlternatives() {
        return new TargetWithAlternatives();
    }

    public TargetAlternative1 generateTargetAlternative1() {
        return new TargetAlternative1();
    }

    public TargetAlternative2 generateTargetAlternative2() {
        return new TargetAlternative2();
    }

}
