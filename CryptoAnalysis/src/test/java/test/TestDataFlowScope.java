package test;

import boomerang.scope.DataFlowScope;
import boomerang.scope.DeclaredMethod;
import boomerang.scope.Method;
import boomerang.scope.WrappedClass;
import crysl.rule.CrySLRule;
import java.util.Collection;
import java.util.HashSet;
import test.assertions.Assertion;

public class TestDataFlowScope implements DataFlowScope {

    private static final String ASSERTION = Assertion.class.getName();
    private final Collection<String> ruleNames;

    public TestDataFlowScope(Collection<CrySLRule> rules) {
        ruleNames = new HashSet<>();

        for (CrySLRule rule : rules) {
            ruleNames.add(rule.getClassName());
        }
    }

    @Override
    public boolean isExcluded(DeclaredMethod method) {
        WrappedClass declaringClass = method.getDeclaringClass();
        if (!declaringClass.isApplicationClass()) {
            return true;
        }

        if (declaringClass.getFullyQualifiedName().contains(ASSERTION)) {
            return true;
        }

        String declaringClassName = method.getDeclaringClass().getFullyQualifiedName();
        return ruleNames.contains(declaringClassName);
    }

    @Override
    public boolean isExcluded(Method method) {
        WrappedClass declaringClass = method.getDeclaringClass();
        if (!declaringClass.isApplicationClass()) {
            return true;
        }

        if (declaringClass.getFullyQualifiedName().contains(ASSERTION)) {
            return true;
        }

        String declaringClassName = method.getDeclaringClass().getFullyQualifiedName();
        return ruleNames.contains(declaringClassName);
    }
}
