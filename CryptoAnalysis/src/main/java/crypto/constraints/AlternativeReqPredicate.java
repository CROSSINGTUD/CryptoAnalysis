/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.constraints;

import boomerang.scope.Statement;
import crysl.rule.CrySLPredicate;
import java.util.Collection;

/**
 * Wrapper class for required predicates with alternatives. Predicates from the REQUIRES section may
 * have the following form:
 *
 * <pre>{@code
 * REQUIRES
 *    generatedKey[...] || generatedPubKey[...] || generatedPrivKey[...];
 * }</pre>
 *
 * According to the CrySL specification, "generatedKey" is the base predicate that determines the
 * statement where the predicates should be ensured. If an alternative refers to some other
 * statement, it is ignored for this predicate.
 *
 * @param statement the statement the alternatives are required
 * @param allAlternatives all alternatives that are specified in the rule
 * @param predicates the actual predicates that are expected to be ensured at the given statement
 */
public record AlternativeReqPredicate(
        Statement statement,
        Collection<CrySLPredicate> allAlternatives,
        Collection<RequiredPredicate> predicates)
        implements IRequiredPredicate {}
