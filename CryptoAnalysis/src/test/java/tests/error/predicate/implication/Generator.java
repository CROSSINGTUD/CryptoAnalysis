/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package tests.error.predicate.implication;

public class Generator {

    public Generator(@SuppressWarnings("unused") boolean constraint) {}

    public Receiver generateReceiver() {
        return new Receiver();
    }

    public void ensureParameter(@SuppressWarnings("unused") String s) {}
}
