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
import crypto.predicates.UnEnsuredPredicate;
import crysl.rule.CrySLRule;
import java.util.Collection;
import java.util.Objects;

/**
 * This error models the violation of predicates from the REQUIRES section. An error is only
 * reported for single predicates, that is, predicates of the form
 *
 * <pre>{@code
 * REQUIRES
 *    generatedKey[...];
 * }</pre>
 *
 * If a predicate has alternatives, an {@link AlternativeReqPredicateError} is reported.
 */
public class RequiredPredicateError extends AbstractRequiredPredicateError {

    private final RequiredPredicate contradictedPredicate;

    public RequiredPredicateError(
            IAnalysisSeed seed,
            CrySLRule rule,
            RequiredPredicate predicate,
            Collection<UnEnsuredPredicate> unEnsuredPredicates) {
        super(seed, predicate.statement(), rule, unEnsuredPredicates);

        this.contradictedPredicate = predicate;
    }

    public RequiredPredicate getContradictedPredicates() {
        return contradictedPredicate;
    }

    @Override
    public String toErrorMarkerString() {
        StringBuilder msg = new StringBuilder(getParamIndexAsText(contradictedPredicate.index()));
        msg.append(" was not properly generated as ");
        String[] parts = contradictedPredicate.predicate().getPredName().split("(?=[A-Z])");
        msg.append(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            msg.append(parts[i]);
        }
        return msg.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), contradictedPredicate);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && obj instanceof RequiredPredicateError other
                && Objects.equals(contradictedPredicate, other.getContradictedPredicates());
    }

    @Override
    public String toString() {
        return "RequiredPredicateError: " + toErrorMarkerString();
    }
}
