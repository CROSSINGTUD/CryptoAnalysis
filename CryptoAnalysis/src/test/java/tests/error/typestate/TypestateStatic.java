/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package tests.error.typestate;

public class TypestateStatic {

    private TypestateStatic() {}

    public static TypestateStatic createTypestate() {
        return new TypestateStatic();
    }

    public static TypestateStatic createTypestate(@SuppressWarnings("unused") String s) {
        return new TypestateStatic();
    }

    public static TypestateStatic createTypestate(
            @SuppressWarnings("unused") String s, @SuppressWarnings("unused") int i) {
        return new TypestateStatic();
    }

    public void operation1() {}

    public void operation2() {}
}
