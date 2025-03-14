/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.listener;

import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.AlternativeReqPredicateError;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.ForbiddenMethodError;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.PredicateContradictionError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import crypto.analysis.errors.UncaughtExceptionError;

public interface IErrorListener {

    void reportError(ConstraintError constraintError);

    void reportError(ForbiddenMethodError forbiddenMethodError);

    void reportError(ImpreciseValueExtractionError impreciseValueExtractionError);

    void reportError(IncompleteOperationError incompleteOperationError);

    void reportError(PredicateContradictionError predicateContradictionError);

    void reportError(RequiredPredicateError requiredPredicateError);

    void reportError(AlternativeReqPredicateError alternativeReqPredicateError);

    void reportError(TypestateError typestateError);

    void reportError(UncaughtExceptionError uncaughtExceptionError);

    void reportError(AbstractError error);
}
