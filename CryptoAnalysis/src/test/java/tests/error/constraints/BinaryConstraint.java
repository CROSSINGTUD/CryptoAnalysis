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

public class BinaryConstraint {

    public BinaryConstraint() {}

    public void implication1(@SuppressWarnings("unused") String s) {}

    public void implication2(@SuppressWarnings("unused") int i) {}

    public void implication(
            @SuppressWarnings("unused") String s, @SuppressWarnings("unused") int i) {}

    public void or1(@SuppressWarnings("unused") String s) {}

    public void or2(@SuppressWarnings("unused") int i) {}

    public void or(@SuppressWarnings("unused") String s, @SuppressWarnings("unused") int i) {}

    public void and1(@SuppressWarnings("unused") String s) {}

    public void and2(@SuppressWarnings("unused") int i) {}

    public void and(@SuppressWarnings("unused") String s, @SuppressWarnings("unused") int i) {}
}
