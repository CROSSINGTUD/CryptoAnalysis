/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package tests.error.imprecisevalueextraction;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.Ruleset;
import test.TestRules;
import test.TestRunnerInterceptor;
import test.assertions.Assertions;

@ExtendWith(TestRunnerInterceptor.class)
@Ruleset(TestRules.IMPRECISE_VALUE_EXTRACTION)
public class ImpreciseValueExtractionTest {

    @Test
    public void testCouldExtractInteger() {
        ImpreciseValueExtraction extraction = new ImpreciseValueExtraction(10);
        Assertions.extValue(0);

        extraction.extractInteger(100);
        Assertions.extValue(0);

        Assertions.impreciseValueExtractionErrors(0);
    }

    @Test
    public void testCouldNotExtractInteger() {
        int randomValue = (int) (Math.random() * 20);

        // For both statements, the value cannot be extracted
        ImpreciseValueExtraction extraction = new ImpreciseValueExtraction(randomValue);
        extraction.extractInteger(randomValue);

        Assertions.impreciseValueExtractionErrors(2);
    }

    @Test
    public void testCouldExtractString() {
        ImpreciseValueExtraction extraction = new ImpreciseValueExtraction("Value");
        Assertions.extValue(0);

        extraction.extractString("Value");
        Assertions.extValue(0);

        Assertions.impreciseValueExtractionErrors(0);
    }

    @Test
    public void testCouldNotExtractString() {
        char[] charValue = new char[] {'v', 'a', 'l', 'u', 'e'};
        String stringValue = String.valueOf(charValue);

        // For both statements, the value cannot be extracted
        ImpreciseValueExtraction extraction = new ImpreciseValueExtraction(stringValue);
        extraction.extractString(stringValue);

        Assertions.impreciseValueExtractionErrors(2);
    }

    @Test
    public void testCouldExtractConditionalValue() {
        ImpreciseValueExtraction extraction1 = new ImpreciseValueExtraction(100);
        Assertions.extValue(0);

        // Value can be extracted and condition satisfied => required call to 'missingCallTo'
        Assertions.impreciseValueExtractionErrors(0);
        Assertions.constraintErrors(extraction1, 1);
    }

    @Test
    public void testCouldNotExtractConditionalValue() {
        int randomValue = (int) (Math.random() * 20);
        ImpreciseValueExtraction extraction2 = new ImpreciseValueExtraction(randomValue);

        // Value cannot be extract => Condition cannot be evaluated and call to 'missingCallTo' not
        // known
        Assertions.impreciseValueExtractionErrors(1);
        Assertions.constraintErrors(extraction2, 0);
    }

    @Test
    public void testCouldNotExtractTransformedValue() {
        String randomString = UUID.randomUUID().toString();
        int intVal = Integer.parseInt(randomString);

        ImpreciseValueExtraction extraction = new ImpreciseValueExtraction(intVal);
        Assertions.constraintErrors(extraction, 0);
        Assertions.impreciseValueExtractionErrors(1);
    }
}
