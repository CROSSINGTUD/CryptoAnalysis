package test;

import boomerang.scene.DataFlowScope;
import boomerang.scene.DeclaredMethod;
import boomerang.scene.Method;
import boomerang.scene.WrappedClass;
import boomerang.scene.jimple.JimpleDeclaredMethod;
import boomerang.scene.jimple.JimpleMethod;

import java.util.Collection;

public class TestDataFlowScope {

    public static DataFlowScope make(Collection<String> excludedClasses) {
        return new DataFlowScope() {
            @Override
            public boolean isExcluded(DeclaredMethod method) {
                WrappedClass declaringClass = method.getDeclaringClass();
                if (declaringClass.getName().contains("Assertion")) {
                    return true;
                }

                JimpleDeclaredMethod jimpleMethod = (JimpleDeclaredMethod) method;
                String declaringClassName = jimpleMethod.getDeclaringClass().getName();

                return excludedClasses.contains(declaringClassName);
            }

            @Override
            public boolean isExcluded(Method method) {
                WrappedClass declaringClass = method.getDeclaringClass();
                if (declaringClass.getName().contains("Assertion")) {
                    return true;
                }

                JimpleMethod jimpleMethod = (JimpleMethod) method;
                String declaringClassName = jimpleMethod.getDeclaringClass().getName();

                return excludedClasses.contains(declaringClassName);
            }
        };
    }
}
