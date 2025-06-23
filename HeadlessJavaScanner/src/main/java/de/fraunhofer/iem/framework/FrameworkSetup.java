/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package de.fraunhofer.iem.framework;

import boomerang.scope.DataFlowScope;
import de.fraunhofer.iem.cryptoanalysis.scope.CryptoAnalysisScope;
import de.fraunhofer.iem.scanner.ScannerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FrameworkSetup {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FrameworkSetup.class);

    protected final String applicationPath;
    protected final ScannerSettings.CallGraphAlgorithm callGraphAlgorithm;
    protected final DataFlowScope dataFlowScope;

    protected FrameworkSetup(
            String applicationPath,
            ScannerSettings.CallGraphAlgorithm callGraphAlgorithm,
            DataFlowScope dataFlowScope) {
        this.applicationPath = applicationPath;
        this.callGraphAlgorithm = callGraphAlgorithm;
        this.dataFlowScope = dataFlowScope;
    }

    public abstract void initializeFramework();

    public abstract CryptoAnalysisScope createFrameworkScope();
}
