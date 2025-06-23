/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package test.framework;

import boomerang.scope.DataFlowScope;
import boomerang.scope.Method;
import de.fraunhofer.iem.cryptoanalysis.scope.CryptoAnalysisScope;

public interface TestSetup {

    void initialize(String classPath, String className, String testName);

    Method getTestMethod();

    CryptoAnalysisScope createFrameworkScope(DataFlowScope dataFlowScope);
}
