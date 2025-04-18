/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package tests.error.predicate.requiredpredicate;

public class A {

    public byte[] attr1 = "Attr1".getBytes();
    public byte[] attr2 = "Attr2".getBytes();
    public byte[] attr3 = "Attr3".getBytes();

    public B ensurePred1OnReturnB() {
        return new B();
    }

    public B ensurePred2OnReturnB() {
        return new B();
    }

    public C ensurePred1OnReturnC() {
        return new C();
    }

    public C ensurePred2OnReturnC() {
        return new C();
    }

    public void ensurePred1onThis() {
        return;
    }

    public void ensurePred2onThis() {
        return;
    }

    public void ensurePred1OnAttr1() {
        return;
    }

    public void ensurePred1OnAttr2() {
        return;
    }

    public void ensurePred1OnAttr3() {
        return;
    }

    public void ensurePred2OnAttr1() {
        return;
    }

    public void ensurePred2OnAttr2() {
        return;
    }

    public void ensurePred2OnAttr3() {
        return;
    }

    public byte[] getAttr1() {
        return attr1;
    }

    public byte[] getAttr2() {
        return attr2;
    }

    public byte[] getAttr3() {
        return attr3;
    }
}
