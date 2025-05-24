/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.typestate;

import boomerang.results.ForwardBoomerangResults;
import typestate.TransitionFunction;

/**
 * Wrapper class to store the IDEal results for a single query
 *
 * @param query the solved query
 * @param results the results for the query
 */
public record IdealResult(
        ForwardSeedQuery query, ForwardBoomerangResults<TransitionFunction> results) {}
