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

import boomerang.scope.FrameworkScope;
import de.fraunhofer.iem.scanner.ScannerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FrameworkSetup {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FrameworkSetup.class);

    protected final String applicationPath;
    protected final ScannerSettings.CallGraphAlgorithm callGraphAlgorithm;

    protected FrameworkSetup(
            String applicationPath, ScannerSettings.CallGraphAlgorithm callGraphAlgorithm) {
        this.applicationPath = applicationPath;
        this.callGraphAlgorithm = callGraphAlgorithm;
    }

    public abstract void initializeFramework();

    public abstract FrameworkScope createFrameworkScope();
}
