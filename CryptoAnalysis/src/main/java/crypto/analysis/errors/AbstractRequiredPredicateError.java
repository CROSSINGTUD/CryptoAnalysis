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

import boomerang.scope.Statement;
import crypto.analysis.IAnalysisSeed;
import crypto.predicates.UnEnsuredPredicate;
import crysl.rule.CrySLRule;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

/**
 * Super class for errors that work with a {@link UnEnsuredPredicate}. Currently, there are {@link
 * RequiredPredicateError} that hold errors with single violated predicates and {@link
 * AlternativeReqPredicateError} that hold errors for violated predicates with alternatives.
 */
public abstract class AbstractRequiredPredicateError extends AbstractConstraintsError {

    private final Collection<UnEnsuredPredicate> unEnsuredPredicates;

    public AbstractRequiredPredicateError(
            IAnalysisSeed seed,
            Statement errorStmt,
            CrySLRule rule,
            Collection<UnEnsuredPredicate> unEnsuredPredicates) {
        super(seed, errorStmt, rule);

        this.unEnsuredPredicates = Set.copyOf(unEnsuredPredicates);
    }

    public Collection<UnEnsuredPredicate> getHiddenPredicates() {
        return unEnsuredPredicates;
    }

    protected String getParamIndexAsText(int paramIndex) {
        return switch (paramIndex) {
            case -1 -> "Return value";
            case 0 -> "First parameter";
            case 1 -> "Second parameter";
            case 2 -> "Third parameter";
            case 3 -> "Fourth parameter";
            case 4 -> "Fifth parameter";
            case 5 -> "Sixth parameter";
            default -> (paramIndex + 1) + "th parameter";
        };
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), unEnsuredPredicates);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && obj instanceof AbstractRequiredPredicateError other
                && Objects.equals(unEnsuredPredicates, other.getHiddenPredicates());
    }
}
