/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package tests.error.nothardcoded;

import java.math.BigInteger;

public class NotHardCoded {

    public void operation(int value) {}

    public void operation(String value) {}

    public void operation(BigInteger value) {}

    public void operation(char[] value) {}

    public void operation(int[] value) {}
}
