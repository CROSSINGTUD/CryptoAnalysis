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
import boomerang.scope.opal.OpalFrameworkScope;
import de.fraunhofer.iem.cryptoanalysis.handler.FrameworkHandler;
import de.fraunhofer.iem.cryptoanalysis.handler.OpalFrameworkHandler;
import org.jspecify.annotations.NonNull;
import org.opalj.br.analyses.Project;
import org.opalj.tac.cg.CallGraph;
import scala.collection.immutable.Set;

public class CryptoAnalysisOpalScope extends OpalFrameworkScope implements CryptoAnalysisScope {

    public CryptoAnalysisOpalScope(
            @NonNull Project<?> project,
            @NonNull CallGraph callGraph,
            @NonNull Set<org.opalj.br.Method> entryPoints,
            @NonNull DataFlowScope dataFlowScope) {
        super(project, callGraph, entryPoints, dataFlowScope);
    }

    @Override
    public FrameworkScope asFrameworkScope() {
        return this;
    }

    @Override
    public FrameworkHandler getFrameworkHandler() {
        return new OpalFrameworkHandler();
    }
}
