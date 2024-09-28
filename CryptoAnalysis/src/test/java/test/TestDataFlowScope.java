package test;

import boomerang.scene.DataFlowScope;
import boomerang.scene.DeclaredMethod;
import boomerang.scene.Method;
import boomerang.scene.WrappedClass;
import boomerang.scene.jimple.JimpleDeclaredMethod;
import boomerang.scene.jimple.JimpleMethod;
import crypto.rules.CrySLRule;

import java.util.Collection;
import java.util.HashSet;

public class TestDataFlowScope implements DataFlowScope {

    private static final String ASSERTION = "Assertion";
    private static final String STRING_CLASS = "java.lang.String";
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
        if (declaringClass.getName().contains(ASSERTION)) {
            return true;
        }

        if (declaringClass.getName().contains(STRING_CLASS)) {
            return true;
        }

        JimpleDeclaredMethod jimpleMethod = (JimpleDeclaredMethod) method;
        String declaringClassName = jimpleMethod.getDeclaringClass().getName();

        return ruleNames.contains(declaringClassName);
    }

    @Override
    public boolean isExcluded(Method method) {
        WrappedClass declaringClass = method.getDeclaringClass();
        if (declaringClass.getName().contains(ASSERTION)) {
            return true;
        }

        if (declaringClass.getName().contains(STRING_CLASS)) {
            return true;
        }

        JimpleMethod jimpleMethod = (JimpleMethod) method;
        String declaringClassName = jimpleMethod.getDeclaringClass().getName();

        return ruleNames.contains(declaringClassName);
    }
}
