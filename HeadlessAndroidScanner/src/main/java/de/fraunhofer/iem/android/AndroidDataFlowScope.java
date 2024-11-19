package de.fraunhofer.iem.android;

import boomerang.scene.DeclaredMethod;
import boomerang.scene.Method;
import crypto.analysis.CryptoAnalysisDataFlowScope;
import crypto.rules.CrySLRule;
import java.util.Collection;

public class AndroidDataFlowScope extends CryptoAnalysisDataFlowScope {

    private static final String ANDROID = "android";
    private static final String ANDROIDX = "androidx";

    public AndroidDataFlowScope(Collection<CrySLRule> rules, Collection<String> ignoredSections) {
        super(rules, ignoredSections);
    }

    @Override
    public boolean isExcluded(DeclaredMethod method) {
        if (super.isExcluded(method)) {
            return true;
        }

        String declaringClassName = method.getDeclaringClass().getName();
        return declaringClassName.startsWith(ANDROID) || declaringClassName.startsWith(ANDROIDX);
    }

    @Override
    public boolean isExcluded(Method method) {
        if (super.isExcluded(method)) {
            return true;
        }

        String declaringClassName = method.getDeclaringClass().getName();
        return declaringClassName.startsWith(ANDROID) || declaringClassName.startsWith(ANDROIDX);
    }
}
