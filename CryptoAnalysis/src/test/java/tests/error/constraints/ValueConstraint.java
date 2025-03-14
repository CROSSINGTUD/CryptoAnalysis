/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package tests.error.constraints;

public class ValueConstraint {

    public ValueConstraint(@SuppressWarnings("unused") String s) {}

    public void operation1(@SuppressWarnings("unused") int i) {}

    public void operation2(
            @SuppressWarnings("unused") String s, @SuppressWarnings("unused") int i) {}

    public void operation3(@SuppressWarnings("unused") String s) {}
}
