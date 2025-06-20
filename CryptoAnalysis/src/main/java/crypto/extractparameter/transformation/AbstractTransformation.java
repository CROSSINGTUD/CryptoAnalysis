/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.extractparameter.transformation;

import de.fraunhofer.iem.cryptoanalysis.handler.FrameworkHandler;

public abstract class AbstractTransformation {

    protected final FrameworkHandler frameworkHandler;

    public AbstractTransformation(FrameworkHandler frameworkHandler) {
        this.frameworkHandler = frameworkHandler;
    }
}
