/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package de.fraunhofer.iem.cryptoanalysis.scope;

import boomerang.scope.DataFlowScope;
import boomerang.scope.FrameworkScope;
import boomerang.scope.sootup.SootUpFrameworkScope;
import de.fraunhofer.iem.cryptoanalysis.handler.FrameworkHandler;
import de.fraunhofer.iem.cryptoanalysis.handler.SootUpFrameworkHandler;
import java.util.Collection;
import org.jspecify.annotations.NonNull;
import sootup.callgraph.CallGraph;
import sootup.java.core.JavaSootMethod;
import sootup.java.core.views.JavaView;

public class CryptoAnalysisSootUpScope extends SootUpFrameworkScope implements CryptoAnalysisScope {

    public CryptoAnalysisSootUpScope(
            @NonNull JavaView view,
            @NonNull CallGraph callGraph,
            @NonNull Collection<JavaSootMethod> entryPoints,
            @NonNull DataFlowScope dataFlowScope) {
        super(view, callGraph, entryPoints, dataFlowScope);
    }

    @Override
    public FrameworkScope asFrameworkScope() {
        return this;
    }

    @Override
    public FrameworkHandler getFrameworkHandler() {
        return new SootUpFrameworkHandler();
    }
}
