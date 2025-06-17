/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package tests.seeds;

public class SeedDiscovery {

    public SeedDiscovery() {}

    public SeedTarget seedFromReturn() {
        return new SeedTarget();
    }

    public String predicateSeedFromReturn() {
        return "";
    }

    public void predicateSeedFromParameter(@SuppressWarnings("unused") String s) {}

    public String noPredicateFromReturn() {
        return "";
    }

    public void noPredicateSeedFromParameter(@SuppressWarnings("unused") String s) {}
}
