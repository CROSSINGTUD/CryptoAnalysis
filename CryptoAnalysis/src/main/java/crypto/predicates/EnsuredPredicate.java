/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.predicates;

import boomerang.scope.Statement;
import crypto.analysis.AnalysisSeedWithSpecification;
import crysl.rule.CrySLPredicate;
import java.util.Objects;

/**
 * Wrapper class for a single {@link CrySLPredicate} to keep track of ensured predicate during the
 * analysis. A predicate is only ensured if there are no violations for a corresponding rule.
 * Otherwise, the analysis propagates an {@link UnEnsuredPredicate}.
 */
public class EnsuredPredicate extends AbstractPredicate {

    public EnsuredPredicate(
            AnalysisSeedWithSpecification generatingSeed,
            CrySLPredicate predicate,
            Statement statement,
            int index) {
        super(generatingSeed, predicate, statement, index);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof EnsuredPredicate;
    }

    @Override
    public String toString() {
        return "Ensured: " + getPredicate().getPredName();
    }
}
