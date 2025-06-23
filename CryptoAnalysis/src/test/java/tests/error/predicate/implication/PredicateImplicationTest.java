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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.Ruleset;
import test.TestRules;
import test.TestRunnerInterceptor;
import test.assertions.Assertions;

@ExtendWith(TestRunnerInterceptor.class)
@Ruleset(TestRules.PREDICATE_IMPLICATION)
public class PredicateImplicationTest {

    @Test
    public void testImplicationSatisfiedOnReturn() {
        Generator trueGenerator = new Generator(true);

        // i == 10 => generatedReceiver
        Receiver trueReceiver = trueGenerator.generateReceiver();
        Assertions.hasEnsuredPredicate(trueReceiver, "generatedReceiver");

        // Condition satisfied (true) => predicate ensured (true)
        trueReceiver.condition(10);
        Assertions.hasEnsuredPredicate(trueReceiver, "generatedThis");

        Receiver falseReceiver1 = trueGenerator.generateReceiver();
        Assertions.hasEnsuredPredicate(falseReceiver1, "generatedReceiver");

        // Condition not satisfied (false) => predicate ensured (true)
        falseReceiver1.condition(100);
        Assertions.hasEnsuredPredicate(falseReceiver1, "generatedThis");

        Generator falseGenerator = new Generator(false);
        Receiver falseReceiver2 = falseGenerator.generateReceiver();
        Assertions.notHasEnsuredPredicate(falseReceiver2, "generatedReceiver");

        // Condition not satisfied (false) => predicate not ensured (false)
        falseReceiver2.condition(100);
        Assertions.hasEnsuredPredicate(falseReceiver2, "generatedThis");

        Assertions.predicateErrors(0);
    }

    @Test
    public void testImplicationNotRelevantOnReturn() {
        Generator trueGenerator = new Generator(true);
        Receiver receiver1 = trueGenerator.generateReceiver();
        Assertions.hasEnsuredPredicate(receiver1, "generatedReceiver");

        // Condition not relevant (ignore) => predicate not ensured (true)
        Assertions.hasEnsuredPredicate(receiver1, "generatedThis");

        Generator falseGenerator = new Generator(false);
        Receiver receiver2 = falseGenerator.generateReceiver();
        Assertions.notHasEnsuredPredicate(receiver2, "generatedReceiver");

        // Condition not relevant (ignore) => predicate not ensured (false)
        Assertions.hasEnsuredPredicate(receiver2, "generatedThis");

        Assertions.predicateErrors(0);
    }

    @Test
    public void testImplicationUnsatisfiedOnReturn() {
        Generator generator = new Generator(false);

        // i == 10 => generatedReceiver
        Receiver receiver = generator.generateReceiver();
        Assertions.notHasEnsuredPredicate(receiver);

        // Condition satisfied (true) => predicate has to be ensured (false)
        receiver.condition(10);
        Assertions.notHasEnsuredPredicate(receiver, "generatedThis");

        Assertions.predicateErrors(1);
    }

    @Test
    public void testImplicationSatisfiedOnParameter() {
        String target1 = "target";
        Generator trueGenerator = new Generator(true);
        trueGenerator.ensureParameter(target1);
        Assertions.hasEnsuredPredicate(target1, "ensuredParameter");

        // Condition satisfied (true) => predicate ensured (true)
        Receiver trueReceiver1 = new Receiver();
        trueReceiver1.condition(20);
        trueReceiver1.requiredPredicate(target1);
        Assertions.hasEnsuredPredicate(trueReceiver1, "generatedThis");

        String target2 = "target";
        Generator falseGenerator = new Generator(false);
        falseGenerator.ensureParameter(target2);
        Assertions.notHasEnsuredPredicate(target2, "ensuredParameter");

        // Condition not satisfied (false) => predicate not ensured (false)
        Receiver trueReceiver2 = new Receiver();
        trueReceiver2.condition(200);
        trueReceiver2.requiredPredicate(target2);
        Assertions.hasEnsuredPredicate(trueReceiver2, "generatedThis");

        String target3 = "target";
        trueGenerator.ensureParameter(target3);
        Assertions.hasEnsuredPredicate(target3, "ensuredParameter");

        // Condition not satisfied (false) => predicate not ensured (true)
        Receiver falseReceiver = new Receiver();
        falseReceiver.condition(200);
        falseReceiver.requiredPredicate(target2);
        Assertions.hasEnsuredPredicate(falseReceiver, "generatedThis");

        Assertions.predicateErrors(0);
    }

    @Test
    public void testImplicationNotRelevantOnParameter() {
        String target1 = "target";
        Generator trueGenerator = new Generator(true);
        trueGenerator.ensureParameter(target1);
        Assertions.hasEnsuredPredicate(target1, "ensuredParameter");

        // Condition not relevant (ignored) => predicate ensured (true)
        Receiver receiver1 = new Receiver();
        receiver1.requiredPredicate(target1);
        Assertions.hasEnsuredPredicate(receiver1, "generatedThis");

        String target2 = "target";
        Generator falseGenerator = new Generator(false);
        falseGenerator.ensureParameter(target2);
        Assertions.notHasEnsuredPredicate(target2, "ensuredParameter");

        // Condition not relevant (ignored) => predicate not ensured (false)
        Receiver receiver2 = new Receiver();
        receiver2.requiredPredicate(target2);
        Assertions.hasEnsuredPredicate(receiver2, "generatedThis");

        Assertions.predicateErrors(0);
    }

    @Test
    public void testImplicationUnsatisfiedOnParameter() {
        String target = "target";
        Generator falseGenerator = new Generator(false);
        falseGenerator.ensureParameter(target);
        Assertions.notHasEnsuredPredicate(target, "ensuredParameter");

        // condition satisfied (true) => predicate not ensured (false)
        Receiver receiver = new Receiver();
        receiver.condition(20);
        receiver.requiredPredicate(target);
        Assertions.notHasEnsuredPredicate(receiver, "generatedThis");

        Assertions.predicateErrors(1);
    }
}
