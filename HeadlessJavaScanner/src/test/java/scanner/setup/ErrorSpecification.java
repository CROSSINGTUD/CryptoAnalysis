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

import java.util.HashMap;
import java.util.Map;

public class ErrorSpecification {

    private final MethodWrapper methodWrapper;
    private final Map<Class<?>, Integer> findings;

    private ErrorSpecification(Builder builder) {
        this.methodWrapper = builder.methodWrapper;
        this.findings = builder.findings;
    }

    public MethodWrapper getMethodWrapper() {
        return methodWrapper;
    }

    public Map<Class<?>, Integer> getFindings() {
        return findings;
    }

    public static class Builder {

        private final MethodWrapper methodWrapper;
        private final Map<Class<?>, Integer> findings;

        public Builder(String declaringClass, String methodName, int argsCount) {
            this.methodWrapper = new MethodWrapper(declaringClass, methodName, argsCount);
            this.findings = new HashMap<>();
        }

        public Builder withNoErrors(Class<?> errorType) {
            findings.put(errorType, 0);
            return this;
        }

        public Builder withTPs(Class<?> errorType, int numberOfFindings) {
            int count = findings.getOrDefault(errorType, 0);
            findings.put(errorType, count + numberOfFindings);
            return this;
        }

        public Builder withFPs(
                Class<?> errorType,
                int numberOfFindings,
                @SuppressWarnings("unused") String explanation) {
            int count = findings.getOrDefault(errorType, 0);
            findings.put(errorType, count + numberOfFindings);
            return this;
        }

        public ErrorSpecification build() {
            return new ErrorSpecification(this);
        }
    }
}
