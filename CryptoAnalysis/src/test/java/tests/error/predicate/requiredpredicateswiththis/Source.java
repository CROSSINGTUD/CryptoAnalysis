/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package tests.error.predicate.requiredpredicateswiththis;

public class Source {

    public void causeConstraintError(boolean value) {}

    public SimpleTarget generateTarget() {
        return new SimpleTarget();
    }

    public TargetWithAlternatives generateTargetWithAlternatives() {
        return new TargetWithAlternatives();
    }

    public TargetAlternative1 generateTargetAlternative1() {
        return new TargetAlternative1();
    }

    public TargetAlternative2 generateTargetAlternative2() {
        return new TargetAlternative2();
    }
}
