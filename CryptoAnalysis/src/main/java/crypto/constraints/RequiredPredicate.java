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

/**
 * Wrapper class for predicates from the REQUIRES section. This class only stores single predicates,
 * that is, predicates of the form
 *
 * <pre>{@code
 * REQUIRES
 *    generatedKey[...];
 * }</pre>
 *
 * If a predicate has alternatives, a {@link AlternativeReqPredicate} is used.
 *
 * @param predicate the predicate wrapped in this object
 * @param statement the statement where the predicate is required
 * @param index the statement's parameter index
 */
public record RequiredPredicate(CrySLPredicate predicate, Statement statement, int index)
        implements IRequiredPredicate {}
