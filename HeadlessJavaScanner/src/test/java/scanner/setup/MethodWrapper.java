/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package scanner.setup;

import java.util.Arrays;

public class MethodWrapper {

    private final String declaringClass;
    private final String methodName;
    private final int argsCount;

    public MethodWrapper(String declaringClass, String methodName, int argsCount) {
        this.declaringClass = declaringClass;
        this.methodName = methodName;
        this.argsCount = argsCount;
    }

    public String getDeclaringClass() {
        return declaringClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public int getArgsCount() {
        return argsCount;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {declaringClass, methodName, argsCount});
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        MethodWrapper other = (MethodWrapper) obj;
        return declaringClass.equals(other.getDeclaringClass())
                && methodName.equals(other.getMethodName())
                && argsCount == other.getArgsCount();
    }

    @Override
    public String toString() {
        return "<Class: "
                + declaringClass
                + ", Method: "
                + methodName
                + ", #Args: "
                + argsCount
                + ">";
    }
}
