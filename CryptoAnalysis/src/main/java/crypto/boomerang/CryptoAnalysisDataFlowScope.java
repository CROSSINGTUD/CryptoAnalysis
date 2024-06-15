package crypto.boomerang;

import boomerang.scene.DataFlowScope;
import boomerang.scene.DeclaredMethod;
import boomerang.scene.Method;
import boomerang.scene.jimple.JimpleDeclaredMethod;
import boomerang.scene.jimple.JimpleMethod;
import crypto.analysis.CryptoScanner;
import crypto.rules.CrySLRule;

import java.util.Collection;
import java.util.HashSet;

public class CryptoAnalysisDataFlowScope implements DataFlowScope {

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

        return ruleNames.contains(declaringClassName);
    }

    @Override
    public boolean isExcluded(Method method) {
        JimpleMethod jimpleMethod = (JimpleMethod) method;
        String declaringClassName = jimpleMethod.getDeclaringClass().getName();

        return ruleNames.contains(declaringClassName);
    }
}
