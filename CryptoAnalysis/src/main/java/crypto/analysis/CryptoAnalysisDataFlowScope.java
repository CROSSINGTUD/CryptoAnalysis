/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.analysis;

import boomerang.scope.DataFlowScope;
import boomerang.scope.DeclaredMethod;
import boomerang.scope.Method;
import crysl.rule.CrySLRule;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CryptoAnalysisDataFlowScope implements DataFlowScope {

    private static final Logger LOGGER = LoggerFactory.getLogger(CryptoAnalysisDataFlowScope.class);

    private final Collection<String> ruleNames;
    private final Collection<String> ignoredSections;

    public CryptoAnalysisDataFlowScope(Collection<CrySLRule> rules) {
        this(rules, Collections.emptySet());
    }

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
        String declaringClassName = method.getDeclaringClass().getFullyQualifiedName();

        if (!method.getDeclaringClass().isApplicationClass()) {
            return true;
        }

        if (isOnIgnoredSectionList(method)) {
            return true;
        }

        return ruleNames.contains(declaringClassName);
    }

    @Override
    public boolean isExcluded(Method method) {
        String declaringClassName = method.getDeclaringClass().getFullyQualifiedName();

        if (!method.getDeclaringClass().isApplicationClass()) {
            return true;
        }

        if (isOnIgnoredSectionList(method)) {
            return true;
        }

        return ruleNames.contains(declaringClassName);
    }

    private boolean isOnIgnoredSectionList(Method method) {
        String declaringClass = method.getDeclaringClass().getFullyQualifiedName();
        String methodName = declaringClass + "." + method.getName();

        return isOnIgnoredSectionList(declaringClass, methodName);
    }

    private boolean isOnIgnoredSectionList(DeclaredMethod declaredMethod) {
        String declaringClass = declaredMethod.getDeclaringClass().getFullyQualifiedName();
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
