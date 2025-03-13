/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package de.fraunhofer.iem.android;

import boomerang.scope.DeclaredMethod;
import boomerang.scope.Method;
import crypto.analysis.CryptoAnalysisDataFlowScope;
import crysl.rule.CrySLRule;
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

        String declaringClassName = method.getDeclaringClass().getFullyQualifiedName();
        return declaringClassName.startsWith(ANDROID) || declaringClassName.startsWith(ANDROIDX);
    }

    @Override
    public boolean isExcluded(Method method) {
        if (super.isExcluded(method)) {
            return true;
        }

        String declaringClassName = method.getDeclaringClass().getFullyQualifiedName();
        return declaringClassName.startsWith(ANDROID) || declaringClassName.startsWith(ANDROIDX);
    }
}
