/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.analysis.errors;

import crypto.analysis.IAnalysisSeed;
import crypto.constraints.RequiredPredicate;
import crysl.rule.CrySLRule;
import java.util.Objects;

public class PredicateContradictionError extends AbstractConstraintsError {

    private final RequiredPredicate contradictedPredicate;

    public PredicateContradictionError(
            IAnalysisSeed seed, CrySLRule rule, RequiredPredicate predicate) {
        super(seed, predicate.statement(), rule);

        this.contradictedPredicate = predicate;
    }

    public RequiredPredicate getContradictedPredicate() {
        return contradictedPredicate;
    }

    @Override
    public String toErrorMarkerString() {
        return "Predicate "
                + contradictedPredicate.predicate()
                + " is ensured although it should not";
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), contradictedPredicate);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && obj instanceof PredicateContradictionError other
                && Objects.equals(contradictedPredicate, other.getContradictedPredicate());
    }

    @Override
    public String toString() {
        return "PredicateContradictionError: " + toErrorMarkerString();
    }
}
