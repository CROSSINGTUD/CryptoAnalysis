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

public class ComparisonConstraint {

    public ComparisonConstraint() {}

    public void equal(
            @SuppressWarnings("unused") boolean b1, @SuppressWarnings("unused") boolean b2) {}

    public void equalConstant(@SuppressWarnings("unused") boolean b) {}

    public void unequal(
            @SuppressWarnings("unused") boolean c1, @SuppressWarnings("unused") boolean c2) {}

    public void unequalConstant(@SuppressWarnings("unused") boolean c) {}

    public void greater(@SuppressWarnings("unused") int f1, @SuppressWarnings("unused") int f2) {}

    public void greaterConstant(@SuppressWarnings("unused") int f) {}

    public void greaterEqual(
            @SuppressWarnings("unused") int i1, @SuppressWarnings("unused") int i2) {}

    public void greaterEqualConstant(
            @SuppressWarnings("unused") int i1, @SuppressWarnings("unused") int i2) {}

    public void less(
            @SuppressWarnings("unused") int d1,
            @SuppressWarnings("unused") int d2,
            @SuppressWarnings("unused") int d3) {}

    public void lessConstant(@SuppressWarnings("unused") int d) {}

    public void lessEqual(
            @SuppressWarnings("unused") int l1,
            @SuppressWarnings("unused") int l2,
            @SuppressWarnings("unused") int l3) {}

    public void lessEqualConstant(@SuppressWarnings("unused") int l) {}
}
