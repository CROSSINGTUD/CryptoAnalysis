package scanner.setup;

import java.util.Arrays;

public class MethodWrapper {

    private final String declaringClass;
    private final String methodName;
    private final int argsCount;

    public MethodWrapper(String declaringClass, String methodName, int argsCount) {
        this.declaringClass = declaringClass;
        this.methodName = methodName;
        this.argsCount = argsCount;
    }

    public String getDeclaringClass() {
        return declaringClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public int getArgsCount() {
        return argsCount;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {declaringClass, methodName, argsCount});
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        MethodWrapper other = (MethodWrapper) obj;
        return declaringClass.equals(other.getDeclaringClass())
                && methodName.equals(other.getMethodName())
                && argsCount == other.getArgsCount();
    }

    @Override
    public String toString() {
        return "<Class: "
                + declaringClass
                + ", Method: "
                + methodName
                + ", #Args: "
                + argsCount
                + ">";
    }
}
