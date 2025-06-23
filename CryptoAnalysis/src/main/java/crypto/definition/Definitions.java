/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.definition;

import boomerang.scope.FrameworkScope;
import crypto.listener.AnalysisReporter;
import crysl.rule.CrySLRule;
import de.fraunhofer.iem.cryptoanalysis.scope.CryptoAnalysisScope;
import java.util.Collection;
import sparse.SparsificationStrategy;

public interface Definitions {

    record SeedDefinition(
            FrameworkScope frameworkScope,
            int timeout,
            SparsificationStrategy<?, ?> strategy,
            AnalysisReporter reporter) {}

    record TypestateDefinition(
            FrameworkScope frameworkScope, Collection<CrySLRule> rules, int timeout) {}

    record ConstraintsDefinition(
            CryptoAnalysisScope scope,
            int timeout,
            SparsificationStrategy<?, ?> strategy,
            AnalysisReporter reporter) {}

    record ExtractParameterDefinition(
            CryptoAnalysisScope scope,
            int timeout,
            SparsificationStrategy<?, ?> strategy,
            AnalysisReporter reporter) {}

    record QuerySolverDefinition(
            CryptoAnalysisScope scope,
            int timeout,
            SparsificationStrategy<?, ?> strategy,
            AnalysisReporter reporter) {}
}
