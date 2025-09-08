/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package tests.misc.flowsensitivity;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.Ruleset;
import test.TestRules;
import test.TestRunnerInterceptor;
import test.assertions.Assertions;

@ExtendWith(TestRunnerInterceptor.class)
@Ruleset(TestRules.FLOW_SENSITIVITY)
public class PredicateAfterTest {

    private boolean staticallyUnknown() {
        return Math.random() > 0.5;
    }

    @Test
    public void positivePredicateAfterBranchingTest() {
        PredicateBranching branching = new PredicateBranching();
        branching.operation1();
        Assertions.hasEnsuredPredicate(branching, "generatedPredicateBranching");

        branching.operation2();
        Assertions.hasEnsuredPredicate(branching, "generatedPredicateBranching");

        Assertions.typestateErrors(branching, 0);
        Assertions.incompleteOperationErrors(0);
    }

    @Test
    public void negativePredicateAfterBranchingTest() {
        PredicateBranching branching = new PredicateBranching();
        if (staticallyUnknown()) {
            // After this call, there is only one path with the corresponding event Op1
            branching.operation1();
            Assertions.hasEnsuredPredicate(branching, "generatedPredicateBranching");
        }

        // After this call, there is a sequence where Op1 is not called -> do not ensure predicate
        branching.operation2();
        Assertions.notHasEnsuredPredicate(branching, "generatedPredicateBranching");

        Assertions.typestateErrors(branching, 0);
        Assertions.incompleteOperationErrors(0);
    }

    @Test
    public void positivePredicateAfterGeneratedTest() {
        PredicateGenerator generator = new PredicateGenerator();
        PredicateReceiver receiver = generator.generate();

        String secret = receiver.generateWord();
        Assertions.hasEnsuredPredicate(secret, "generatedWord");

        Assertions.typestateErrors(generator, 0);
        Assertions.typestateErrors(receiver, 0);
        Assertions.incompleteOperationErrors(0);
    }

    @Disabled("Requires rework of detection of seeds with multiple definition sites")
    @Test
    public void negativePredicateAfterGeneratedTest() {
        PredicateGenerator generator = new PredicateGenerator();
        PredicateReceiver receiver = generator.generate();

        String secret = null;
        if (staticallyUnknown()) {
            secret = receiver.generateWord();
            Assertions.hasEnsuredPredicate(secret, "generatedWord");
        }

        /* TODO
         *  This requires a new approach because we need to make sure that both
         *  'secret' definitions (null and generateWord()) define the same seed.
         *  Note that this holds for seeds with and without rules
         */
        Assertions.notHasEnsuredPredicate(secret, "generatedWord");

        Assertions.typestateErrors(generator, 0);
        Assertions.typestateErrors(receiver, 0);
        Assertions.incompleteOperationErrors(0);
    }
}
