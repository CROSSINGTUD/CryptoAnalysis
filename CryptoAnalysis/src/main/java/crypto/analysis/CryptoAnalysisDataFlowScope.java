package crypto.analysis;

import boomerang.scene.DataFlowScope;
import boomerang.scene.DeclaredMethod;
import boomerang.scene.Method;
import crypto.rules.CrySLRule;
import java.util.Collection;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CryptoAnalysisDataFlowScope implements DataFlowScope {

    private static final Logger LOGGER = LoggerFactory.getLogger(CryptoAnalysisDataFlowScope.class);
    private static final String STRING_CLASS = "java.lang.String";

    private final Collection<String> ruleNames;
    private final Collection<String> ignoredSections;

    public CryptoAnalysisDataFlowScope(
            Collection<CrySLRule> rules, Collection<String> ignoredSections) {
        this.ruleNames = new HashSet<>();
        this.ignoredSections = ignoredSections;

        for (CrySLRule rule : rules) {
            ruleNames.add(rule.getClassName());
        }
    }

    @Override
    public boolean isExcluded(DeclaredMethod method) {
        String declaringClassName = method.getDeclaringClass().getName();

        if (!method.getDeclaringClass().isApplicationClass()) {
            return true;
        }

        if (declaringClassName.contains(STRING_CLASS)) {
            return true;
        }

        if (isOnIgnoredSectionList(method)) {
            return true;
        }

        return ruleNames.contains(declaringClassName);
    }

    @Override
    public boolean isExcluded(Method method) {
        String declaringClassName = method.getDeclaringClass().getName();

        if (!method.getDeclaringClass().isApplicationClass()) {
            return true;
        }

        if (declaringClassName.contains(STRING_CLASS)) {
            return true;
        }

        if (isOnIgnoredSectionList(method)) {
            return true;
        }

        return ruleNames.contains(declaringClassName);
    }

    private boolean isOnIgnoredSectionList(Method method) {
        String declaringClass = method.getDeclaringClass().getName();
        String methodName = declaringClass + "." + method.getName();

        return isOnIgnoredSectionList(declaringClass, methodName);
    }

    private boolean isOnIgnoredSectionList(DeclaredMethod declaredMethod) {
        String declaringClass = declaredMethod.getDeclaringClass().getName();
        String methodName = declaringClass + "." + declaredMethod.getName();

        return isOnIgnoredSectionList(declaringClass, methodName);
    }

    private boolean isOnIgnoredSectionList(String declaringClass, String methodName) {
        for (String ignoredSection : ignoredSections) {
            // Check for class name
            if (ignoredSection.equals(declaringClass)) {
                LOGGER.debug("Ignoring dataflow in class " + declaringClass);
                return true;
            }

            // Check for method name
            if (ignoredSection.equals(methodName)) {
                LOGGER.debug("Ignoring dataflow in method " + methodName);
                return true;
            }

            // Check for wildcards (i.e. *)
            if (ignoredSection.endsWith(".*")
                    && declaringClass.startsWith(
                            ignoredSection.substring(0, ignoredSection.length() - 2))) {
                LOGGER.debug(
                        "Ignoring dataflow in class "
                                + declaringClass
                                + " and method "
                                + methodName);
                return true;
            }
        }

        return false;
    }
}
