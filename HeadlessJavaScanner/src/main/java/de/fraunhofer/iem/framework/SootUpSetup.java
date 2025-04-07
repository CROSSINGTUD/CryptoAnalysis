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

public class SootUpSetup extends FrameworkSetup {

    public SootUpSetup(String applicationPath, ScannerSettings.CallGraphAlgorithm algorithm) {
        super(applicationPath, algorithm);
    }

    @Override
    public void initializeFramework() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public FrameworkScope createFrameworkScope() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
