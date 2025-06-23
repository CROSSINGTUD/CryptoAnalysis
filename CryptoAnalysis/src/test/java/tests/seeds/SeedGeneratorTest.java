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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.Ruleset;
import test.TestRules;
import test.TestRunnerInterceptor;
import test.assertions.Assertions;

@ExtendWith(TestRunnerInterceptor.class)
@Ruleset(TestRules.SEEDS)
public class SeedGeneratorTest {

    @Test
    public void seedDiscoveryWithRulesTest() {
        SeedDiscovery discovery = new SeedDiscovery();
        SeedTarget target = discovery.seedFromReturn();
        Assertions.assertState(target, "0");

        Assertions.discoveredSeeds(2);
        Assertions.discoveredRuleSeeds(2);
        Assertions.discoveredPredicateSeeds(0);
    }

    @Test
    public void positivePredicateSeedDiscoveryWithReturnTest() {
        SeedDiscovery discovery = new SeedDiscovery();
        String returnSeed = discovery.predicateSeedFromReturn();
        Assertions.hasEnsuredPredicate(returnSeed);

        Assertions.discoveredSeeds(2);
        Assertions.discoveredRuleSeeds(1);
        Assertions.discoveredPredicateSeeds(1);
    }

    @Test
    public void negativePredicateSeedDiscoveryWithReturnTest() {
        SeedDiscovery discovery = new SeedDiscovery();

        // Does not generate a predicate -> no seed required
        String returnSeed = discovery.noPredicateFromReturn();
        Assertions.notHasEnsuredPredicate(returnSeed);

        Assertions.discoveredSeeds(1);
        Assertions.discoveredRuleSeeds(1);
        Assertions.discoveredPredicateSeeds(0);
    }

    @Test
    public void positivePredicateSeedDiscoveryWithParameterTest() {
        SeedDiscovery discovery = new SeedDiscovery();
        String paramSeed = "someValue";

        discovery.predicateSeedFromParameter(paramSeed);
        Assertions.hasEnsuredPredicate(paramSeed);

        Assertions.discoveredSeeds(2);
        Assertions.discoveredRuleSeeds(1);
        Assertions.discoveredPredicateSeeds(1);
    }

    @Test
    public void negativePredicateSeedDiscoveryWithParameterTest() {
        SeedDiscovery discovery = new SeedDiscovery();
        String paramSeed = "someValue";

        // No predicate is generated -> no seed required
        discovery.noPredicateSeedFromParameter(paramSeed);
        Assertions.notHasEnsuredPredicate(paramSeed);

        Assertions.discoveredSeeds(1);
        Assertions.discoveredRuleSeeds(1);
        Assertions.discoveredPredicateSeeds(0);
    }
}
