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

import crypto.analysis.IAnalysisSeed;
import java.util.Collection;

public interface IPredicateCheckListener {

    void beforePredicateChecks(Collection<IAnalysisSeed> seeds);

    void propagatePredicates();

    void afterPredicateChecks();
}
