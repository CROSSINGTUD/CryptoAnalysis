/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package tests.error.predicate.contradiction;

import org.junit.jupiter.api.Test;
import test.TestConstants;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class PredicateContradictionTest extends UsagePatternTestingFramework {

    private static final String ENSURED_PRED = "generatedContradiction";

    @Override
    protected String getRulesetPath() {
        return TestConstants.RULES_TEST_DIR + "predicateContradiction";
    }

    @Test
    public void positivePredicateContradictionTest() {
        // ConstraintError -> no predicate is ensured
        PredicateEnsurer ensurer = new PredicateEnsurer(false);
        byte[] noPredicate = ensurer.createPredicate();
        Assertions.notHasEnsuredPredicate(noPredicate);

        Contradiction contradiction = new Contradiction(false);
        contradiction.contradictPredicate(noPredicate);
        Assertions.hasEnsuredPredicate(contradiction, ENSURED_PRED);

        Assertions.predicateContradictionErrors(0);
    }

    @Test
    public void negativePredicateContradictionTest() {
        // No ConstraintError -> Predicate is ensured -> contradiction
        PredicateEnsurer ensurer = new PredicateEnsurer(true);
        byte[] predicate = ensurer.createPredicate();
        Assertions.hasEnsuredPredicate(predicate);

        Contradiction contradiction = new Contradiction(false);
        contradiction.contradictPredicate(predicate);
        Assertions.notHasEnsuredPredicate(contradiction);

        Assertions.predicateContradictionErrors(1);
    }

    @Test
    public void positivePredicateContradictionWithConditionTest() {
        PredicateEnsurer ensurer1 = new PredicateEnsurer(false);
        char[] predicate1 = ensurer1.createCondPredicate();
        Assertions.notHasEnsuredPredicate(predicate1);

        // Condition is true -> Predicate not expected (True -> True)
        Contradiction contradiction1 = new Contradiction(true);
        contradiction1.contradictPredicate(predicate1);
        Assertions.hasEnsuredPredicate(contradiction1, ENSURED_PRED);

        PredicateEnsurer ensurer2 = new PredicateEnsurer(true);
        char[] predicate2 = ensurer2.createCondPredicate();
        Assertions.hasEnsuredPredicate(predicate2);

        // Although predicate2 has a predicate, no error is reported because
        // condition is not satisfied (False -> False)
        Contradiction contradiction2 = new Contradiction(false);
        contradiction2.contradictPredicate(predicate2);
        Assertions.hasEnsuredPredicate(contradiction2, ENSURED_PRED);

        PredicateEnsurer ensurer3 = new PredicateEnsurer(false);
        char[] predicate3 = ensurer3.createCondPredicate();
        Assertions.notHasEnsuredPredicate(predicate3);

        // predicate3 has no ensured predicate and condition is not satisfied (False -> True)
        Contradiction contradiction3 = new Contradiction(false);
        contradiction3.contradictPredicate(predicate3);
        Assertions.hasEnsuredPredicate(contradiction3, ENSURED_PRED);

        Assertions.predicateContradictionErrors(0);
    }

    @Test
    public void negativePredicateContradictionWithConditionTest() {
        PredicateEnsurer ensurer = new PredicateEnsurer(true);
        char[] predicate = ensurer.createCondPredicate();
        Assertions.hasEnsuredPredicate(predicate);

        // predicate1 has predicate and condition is satisfied -> contradiction (True -> False)
        Contradiction contradiction = new Contradiction(true);
        contradiction.contradictPredicate(predicate);
        Assertions.notHasEnsuredPredicate(contradiction);

        Assertions.predicateContradictionErrors(1);
    }
}
