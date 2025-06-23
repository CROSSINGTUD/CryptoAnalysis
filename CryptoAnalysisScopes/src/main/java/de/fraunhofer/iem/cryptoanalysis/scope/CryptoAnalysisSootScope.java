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
import boomerang.scope.soot.SootFrameworkScope;
import de.fraunhofer.iem.cryptoanalysis.handler.FrameworkHandler;
import de.fraunhofer.iem.cryptoanalysis.handler.SootFrameworkHandler;
import java.util.Collection;
import org.jspecify.annotations.NonNull;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;

public class CryptoAnalysisSootScope extends SootFrameworkScope implements CryptoAnalysisScope {

    public CryptoAnalysisSootScope(
            @NonNull Scene scene,
            @NonNull CallGraph callGraph,
            @NonNull Collection<SootMethod> entryPoints,
            @NonNull DataFlowScope dataFlowScope) {
        super(scene, callGraph, entryPoints, dataFlowScope);
    }

    @Override
    public FrameworkScope asFrameworkScope() {
        return this;
    }

    @Override
    public FrameworkHandler getFrameworkHandler() {
        return new SootFrameworkHandler();
    }
}
