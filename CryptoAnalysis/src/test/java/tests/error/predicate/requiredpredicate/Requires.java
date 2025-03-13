/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package tests.error.predicate.requiredpredicate;

public class Requires {

    // SIMPLE

    public void pred1onPos1(A p1) {}

    public void notPred1onPos1(A p1) {}

    // AND

    // same predicate
    public void pred1onPos1_AND_pred1onPos2(A p1, A p2) {}

    public void pred1onPos1_AND_notPred1onPos2(A p1, A p2) {}

    public void notPred1onPos1_AND_pred1onPos2(A p1, A p2) {}

    public void notPred1onPos1_AND_notPred1onPos2(A p1, A p2) {}

    // multi predicates
    public void pred1onPos1_AND_pred2onPos2(A p1, A p2) {}

    public void pred1onPos1_AND_notPred2onPos2(A p1, A p2) {}

    public void notPred1onPos1_AND_pred2onPos2(A p1, A p2) {}

    public void notPred1onPos1_AND_notPred2onPos2(A p1, A p2) {}

    // OR

    // same predicate
    public void pred1onPos1_OR_pred1onPos2(A p1, A p2) {}

    public void pred1onPos1_OR_notPred1onPos2(A p1, A p2) {}

    public void notPred1onPos1_OR_pred1onPos2(A p1, A p2) {}

    public void notPred1onPos1_OR_notPred1onPos2(A p1, A p2) {}

    // multi predicates
    public void pred1onPos1_OR_pred2onPos2(A p1, A p2) {}

    public void pred1onPos1_OR_notPred2onPos2(A p1, A p2) {}

    public void notPred1onPos1_OR_pred2onPos2(A p1, A p2) {}

    public void notPred1onPos1_OR_notPred2onPos2(A p1, A p2) {}

    // 3 cases same predicate
    public void pred1onPos1_OR_pred1onPos2_OR_pred1onPos3(A p1, A p2, A p3) {}

    public void pred1onPos1_OR_notPred1onPos2_OR_pred1onPos3(A p1, A p2, A p3) {}

    public void notPred1onPos1_OR_pred1onPos2_OR_pred1onPos3(A p1, A p2, A p3) {}

    public void notPred1onPos1_OR_notPred1onPos2_OR_pred1onPos3(A p1, A p2, A p3) {}

    public void pred1onPos1_OR_pred1onPos2_OR_notPred1onPos3(A p1, A p2, A p3) {}

    public void pred1onPos1_OR_notPred1onPos2_OR_notPred1onPos3(A p1, A p2, A p3) {}

    public void notPred1onPos1_OR_pred1onPos2_OR_notPred1onPos3(A p1, A p2, A p3) {}

    public void notPred1onPos1_OR_notPred1onPos2_OR_notPred1onPos3(A p1, A p2, A p3) {}

    // IMPLICATE

    // same predicate
    public void pred1onPos1_IMPL_pred1onPos2(A p1, A p2) {}

    public void pred1onPos1_IMPL_notPred1onPos2(A p1, A p2) {}

    public void notPred1onPos1_IMPL_pred1onPos2(A p1, A p2) {}

    public void notPred1onPos1_IMPL_notPred1onPos2(A p1, A p2) {}

    // multi predicates
    public void pred1onPos1_IMPL_pred2onPos2(A p1, A p2) {}

    public void pred1onPos1_IMPL_notPred2onPos2(A p1, A p2) {}

    public void notPred1onPos1_IMPL_pred2onPos2(A p1, A p2) {}

    public void notPred1onPos1_IMPL_notPred2onPos2(A p1, A p2) {}

    // OR WITH IMPLICATION
    public void pred1onPos1_OR_pred2onPos1_IMPL_pred1onPos2(A p1, A p2) {}

    public void pred2onPos1_IMPL_pred1onPos2_OR_pred2onPos2(A p1, A p2) {}
}
