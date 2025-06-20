/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package de.fraunhofer.iem.cryptoanalysis.handler;

import boomerang.scope.Method;
import boomerang.scope.Val;
import org.jspecify.annotations.NonNull;

public interface FrameworkHandler {

    Val createIntConstant(@NonNull int value, @NonNull Method method);

    Val createLongConstant(@NonNull long value, @NonNull Method method);

    Val createStringConstant(@NonNull String value, @NonNull Method method);

    boolean isBinaryExpr(@NonNull Val val);
}
