/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package test.assertions;

import boomerang.scope.Statement;
import boomerang.scope.ValCollection;
import com.google.common.collect.Multimap;
import crypto.extractparameter.ParameterWithExtractedValues;
import java.util.Collection;

public class ExtractedValueAssertion implements Assertion {

    private final Statement stmt;
    private final int index;
    private boolean satisfied;

    public ExtractedValueAssertion(Statement stmt, int index) {
        this.stmt = stmt;
        this.index = index;
    }

    public void computedValues(Multimap<Statement, ParameterWithExtractedValues> extractedValues) {
        Collection<ParameterWithExtractedValues> paramsAtStatement = extractedValues.get(stmt);

        for (ParameterWithExtractedValues parameter : paramsAtStatement) {
            Statement statement = parameter.statement();

            // TODO Maybe distinguish between "MayExtracted" and "MustExtracted"
            if (parameter.extractedValues().stream()
                    .anyMatch(v -> v.getTransformedVal().equals(ValCollection.zero()))) {
                continue;
            }

            if (statement.equals(stmt) && parameter.index() == index) {
                satisfied = true;
            }
        }
    }

    @Override
    public boolean isUnsound() {
        return !satisfied;
    }

    @Override
    public boolean isImprecise() {
        return false;
    }

    @Override
    public String getErrorMessage() {
        return "Did not extract parameter with index: " + index + " @ " + stmt;
    }
}
