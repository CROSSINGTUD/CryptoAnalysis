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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.Ruleset;
import test.TestRules;
import test.TestRunnerInterceptor;
import test.assertions.Assertions;

@ExtendWith(TestRunnerInterceptor.class)
@Ruleset(TestRules.REQUIRED_PREDICATE_WITH_THIS)
public class RequiredPredicateWithThisTest {

    @Test
    public void positiveRequiredPredicateWithThis() {
        Source source = new Source();
        source.causeConstraintError(false);

        SimpleTarget target = source.generateTarget();
        Assertions.hasEnsuredPredicate(target, "generatedTarget");
        target.doNothing();

        UsingTarget usingTarget = new UsingTarget();
        usingTarget.useTarget(target);
        Assertions.hasEnsuredPredicate(usingTarget);

        Assertions.predicateErrors(0);
    }

    @Test
    public void negativeRequiredPredicateWithThis() {
        Source source = new Source();
        source.causeConstraintError(true);

        // No ensured predicate is passed to target -> RequiredPredicateError
        SimpleTarget target = source.generateTarget();
        target.doNothing();
        Assertions.notHasEnsuredPredicate(target);

        // RequiredPredicateError for calling useTarget with insecure target
        UsingTarget usingTarget = new UsingTarget();
        usingTarget.useTarget(target);
        Assertions.notHasEnsuredPredicate(usingTarget);

        Assertions.predicateErrors(2);
    }

    @Test
    public void positiveRequiredAlternativePredicateWithThis() {
        Source source = new Source();
        source.causeConstraintError(false);

        TargetWithAlternatives target1 = source.generateTargetWithAlternatives();
        Assertions.hasEnsuredPredicate(target1, "generatedTargetWithAlternatives");
        target1.doNothing();

        UsingTarget usingTarget1 = new UsingTarget();
        usingTarget1.useTarget(target1);
        Assertions.hasEnsuredPredicate(usingTarget1);

        TargetWithAlternatives target2 = source.generateTargetAlternative1();
        Assertions.hasEnsuredPredicate(target2, "generatedAlternative1");
        target2.doNothing();

        UsingTarget usingTarget2 = new UsingTarget();
        usingTarget2.useTarget(target2);
        Assertions.hasEnsuredPredicate(usingTarget2);

        TargetWithAlternatives target3 = source.generateTargetAlternative2();
        Assertions.hasEnsuredPredicate(target3, "generatedAlternative2");
        target3.doNothing();

        UsingTarget usingTarget3 = new UsingTarget();
        usingTarget3.useTarget(target3);
        Assertions.hasEnsuredPredicate(usingTarget3);

        Assertions.predicateErrors(0);
    }

    @Test
    public void negativeRequiredAlternativePredicateWithThis() {
        Source source = new Source();
        source.causeConstraintError(true);

        // No ensured predicate is passed to target -> RequiredPredicateError
        TargetWithAlternatives target1 = source.generateTargetWithAlternatives();
        target1.doNothing();
        Assertions.notHasEnsuredPredicate(target1);

        // RequiredPredicateError for calling useTarget with insecure target
        UsingTarget usingTarget1 = new UsingTarget();
        usingTarget1.useTarget(target1);
        Assertions.notHasEnsuredPredicate(usingTarget1);

        // No ensured predicate is passed to target -> RequiredPredicateError
        TargetWithAlternatives target2 = source.generateTargetAlternative1();
        target2.doNothing();
        Assertions.notHasEnsuredPredicate(target2);

        // RequiredPredicateError for calling useTarget with insecure target
        UsingTarget usingTarget2 = new UsingTarget();
        usingTarget2.useTarget(target2);
        Assertions.notHasEnsuredPredicate(usingTarget2);

        // No ensured predicate is passed to target -> RequiredPredicateError
        TargetWithAlternatives target3 = source.generateTargetAlternative2();
        target3.doNothing();
        Assertions.notHasEnsuredPredicate(target3);

        // RequiredPredicateError for calling useTarget with insecure target
        UsingTarget usingTarget3 = new UsingTarget();
        usingTarget3.useTarget(target3);
        Assertions.notHasEnsuredPredicate(usingTarget3);

        Assertions.predicateErrors(6);
    }
}
