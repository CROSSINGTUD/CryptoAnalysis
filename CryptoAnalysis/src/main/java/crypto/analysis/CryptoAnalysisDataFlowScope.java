package crypto.analysis;

import boomerang.scene.DataFlowScope;
import boomerang.scene.DeclaredMethod;
import boomerang.scene.Method;
import boomerang.scene.jimple.JimpleDeclaredMethod;
import boomerang.scene.jimple.JimpleMethod;
import crypto.rules.CrySLRule;

import java.util.Collection;
import java.util.HashSet;

public class CryptoAnalysisDataFlowScope implements DataFlowScope {

    private static final String JAVA_IDENTIFIER = "java";
    private static final String BOUNCY_CASTLE_IDENTIFIER = "org.bouncycastle";

    private final Collection<String> ruleNames;

    public CryptoAnalysisDataFlowScope(Collection<CrySLRule> rules) {
        this.ruleNames = new HashSet<>();

        for (CrySLRule rule : rules) {
            ruleNames.add(rule.getClassName());
        }
    }

    public boolean isExcluded(DeclaredMethod method) {
        JimpleDeclaredMethod jimpleMethod = (JimpleDeclaredMethod) method;
        String declaringClassName = jimpleMethod.getDeclaringClass().getName();

        if (declaringClassName.startsWith(JAVA_IDENTIFIER)) {
            return true;
        }

        if (declaringClassName.startsWith(BOUNCY_CASTLE_IDENTIFIER)) {
            return true;
        }

        return ruleNames.contains(declaringClassName);
    }

    @Override
    public boolean isExcluded(Method method) {
        JimpleMethod jimpleMethod = (JimpleMethod) method;
        String declaringClassName = jimpleMethod.getDeclaringClass().getName();

        if (declaringClassName.startsWith(JAVA_IDENTIFIER)) {
            return true;
        }

        if (declaringClassName.startsWith(BOUNCY_CASTLE_IDENTIFIER)) {
            return true;
        }

        return ruleNames.contains(declaringClassName);
    }
}
