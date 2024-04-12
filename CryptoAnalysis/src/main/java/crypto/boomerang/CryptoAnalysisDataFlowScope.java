package crypto.boomerang;

import boomerang.scene.DataFlowScope;
import boomerang.scene.DeclaredMethod;
import boomerang.scene.Method;
import boomerang.scene.jimple.JimpleDeclaredMethod;
import boomerang.scene.jimple.JimpleMethod;

import java.util.Collection;

public class CryptoAnalysisDataFlowScope {

    public static DataFlowScope make(Collection<String> excludedClasses) {
        return new DataFlowScope() {
            @Override
            public boolean isExcluded(DeclaredMethod method) {
                JimpleDeclaredMethod jimpleMethod = (JimpleDeclaredMethod) method;
                String declaringClassName = jimpleMethod.getDeclaringClass().getName();

                return excludedClasses.contains(declaringClassName);
            }

            @Override
            public boolean isExcluded(Method method) {
                JimpleMethod jimpleMethod = (JimpleMethod) method;
                String declaringClassName = jimpleMethod.getDeclaringClass().getName();

                return excludedClasses.contains(declaringClassName);
            }
        };
    }
}
