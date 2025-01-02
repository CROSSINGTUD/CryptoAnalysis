package test.assertions;

public class Assertions {

    public static void assertState(Object o, int state) {}

    public static void extValue(int pos) {}

    public static void assertValue(Object o, Object v) {}

    public static void mustNotBeInAcceptingState(Object o) {}

    public static void mustBeInAcceptingState(Object o) {}

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

    public static void hasGeneratedPredicate(Object o) {}

    public static void hasNotGeneratedPredicate(Object o) {}

    public static void missingTypestateChange() {}

    public static void noMissingTypestateChange() {}

    public static void predicateContradictionErrors(int i) {}

    public static void predicateErrors(int i) {}

    public static void constraintErrors(Object o, int i) {}

    public static void typestateErrors(int i) {}

    public static void incompleteOperationErrors(int i) {}

    public static void forbiddenMethodErrors(int i) {}

    public static void impreciseValueExtractionErrors(int i) {}

    public static void callToErrors(int i) {}

    public static void noCallToErrors(int i) {}

    public static void neverTypeOfErrors(int i) {}

    public static void notHardCodedErrors(int i) {}

    public static void instanceOfErrors(int i) {}

    public static void dependentError(int thisErrorNr) {}

    public static void dependentError(int thisErrorNr, int precedingError1) {}

    public static void dependentError(int thisErrorNr, int precedingError1, int precedingError2) {}

    public static void dependentError(
            int thisErrorNr, int precedingError1, int precedingError2, int precedingError3) {}

    public static void dependentError(
            int thisErrorNr,
            int precedingError1,
            int precedingError2,
            int precedingError3,
            int precedingError4) {}

    public static void dependentError(
            int thisErrorNr,
            int precedingError1,
            int precedingError2,
            int precedingError3,
            int precedingError4,
            int precedingError5) {}
}
