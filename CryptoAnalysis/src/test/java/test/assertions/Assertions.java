package test.assertions;

public class Assertions {

    public static void extValue(int pos) {}

    public static void mustBeInAcceptingState(Object o) {}

    public static void mayBeInAcceptingState(Object o) {}

    public static void mustNotBeInAcceptingState(Object o) {}

    public static void assertState(Object o, String label) {}

    public static void violatedConstraint() {}

    public static void evaluatedConstraints(Object o, int i) {}

    public static void satisfiedConstraints(Object o, int i) {}

    public static void violatedConstraints(Object o, int i) {}

    public static void notRelevantConstraints(Object o, int i) {}

    public static void callToForbiddenMethod() {}

    public static void hasEnsuredPredicate(Object o) {}

    public static void hasEnsuredPredicate(Object o, String predName) {}

    public static void notHasEnsuredPredicate(Object o) {}

    public static void notHasEnsuredPredicate(Object o, String predName) {}

    public static void missingTypestateChange() {}

    public static void noMissingTypestateChange() {}

    public static void predicateContradictionErrors(int i) {}

    public static void predicateErrors(int i) {}

    public static void constraintErrors(Object o, int i) {}

    public static void typestateErrors(Object o, int i) {}

    public static void incompleteOperationErrors(int i) {}

    public static void forbiddenMethodErrors(int i) {}

    public static void impreciseValueExtractionErrors(int i) {}
}
