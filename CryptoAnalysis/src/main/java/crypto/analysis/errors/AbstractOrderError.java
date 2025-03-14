/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.analysis.errors;

import boomerang.scope.Statement;
import crypto.analysis.IAnalysisSeed;
import crysl.rule.CrySLRule;

/** Super class for all errors from the ORDER section */
public abstract class AbstractOrderError extends AbstractError {

    public AbstractOrderError(IAnalysisSeed seed, Statement errorStmt, CrySLRule rule) {
        super(seed, errorStmt, rule);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
