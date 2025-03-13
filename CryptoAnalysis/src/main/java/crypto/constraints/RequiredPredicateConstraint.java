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
import com.google.common.collect.Multimap;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.errors.PredicateContradictionError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.extractparameter.ParameterWithExtractedValues;
import crypto.predicates.EnsuredPredicate;
import crypto.predicates.UnEnsuredPredicate;
import crysl.rule.CrySLPredicate;
import crysl.rule.ISLConstraint;
import java.util.Collection;
import java.util.HashSet;

/** Constraint that evaluates a single required predicate from the REQUIRES section */
public class RequiredPredicateConstraint extends EvaluableConstraint {

    private final RequiredPredicate predicate;

    public RequiredPredicateConstraint(
            AnalysisSeedWithSpecification seed,
            Collection<Statement> statements,
            Multimap<Statement, ParameterWithExtractedValues> extractedValues,
            RequiredPredicate predicate) {
        super(seed, statements, extractedValues);

        this.predicate = predicate;
    }

    @Override
    public ISLConstraint getConstraint() {
        return predicate.predicate();
    }

    @Override
    public EvaluationResult evaluate() {
        Statement statement = predicate.statement();
        if (!statements.contains(statement)) {
            return EvaluationResult.ConstraintIsNotRelevant;
        }

        CrySLPredicate pred = predicate.predicate();
        if (pred.getConstraint().isPresent()) {
            EvaluableConstraint constraint =
                    EvaluableConstraint.getInstance(
                            seed, pred.getConstraint().get(), statements, extractedValues);
            EvaluationResult result = constraint.evaluate();

            if (result == EvaluationResult.ConstraintIsNotRelevant) {
                return EvaluationResult.ConstraintIsNotRelevant;
            } else if (result == EvaluationResult.ConstraintIsNotSatisfied) {
                return EvaluationResult.ConstraintIsNotRelevant;
            }
        }

        if (pred.isNegated()) {
            evaluateNegatedPredicateAtStatement(predicate);
        } else {
            evaluatePredicateAtStatement(predicate);
        }

        if (isSatisfied()) {
            return EvaluationResult.ConstraintIsSatisfied;
        } else if (isViolated()) {
            return EvaluationResult.ConstraintIsNotSatisfied;
        }

        return EvaluationResult.ConstraintIsNotRelevant;
    }

    private void evaluatePredicateAtStatement(RequiredPredicate predicate) {
        boolean ensured = false;

        Collection<EnsuredPredicate> ensuredPredsAtStatement =
                seed.getEnsuredPredicatesAtStatement(predicate.statement());
        for (EnsuredPredicate ensuredPred : ensuredPredsAtStatement) {
            if (doReqPredAndEnsPredMatch(predicate, ensuredPred)) {
                ensured = true;
            }
        }

        if (!ensured) {
            Collection<UnEnsuredPredicate> unEnsuredPredicates =
                    extractUnEnsuredPredicatesAtStatement(
                            predicate.statement(), predicate.predicate(), predicate.index());

            RequiredPredicateError error =
                    new RequiredPredicateError(
                            seed, seed.getSpecification(), predicate, unEnsuredPredicates);
            errors.add(error);
        }
    }

    private void evaluateNegatedPredicateAtStatement(RequiredPredicate predicate) {
        boolean ensured = false;

        Collection<EnsuredPredicate> ensuredPredsAtStatement =
                seed.getEnsuredPredicatesAtStatement(predicate.statement());
        for (EnsuredPredicate ensuredPred : ensuredPredsAtStatement) {
            if (doReqPredAndEnsPredMatch(predicate, ensuredPred)) {
                ensured = true;
            }
        }

        if (ensured) {
            PredicateContradictionError error =
                    new PredicateContradictionError(seed, seed.getSpecification(), predicate);
            errors.add(error);
        }
    }

    private boolean doReqPredAndEnsPredMatch(RequiredPredicate reqPred, EnsuredPredicate ensPred) {
        CrySLPredicate predToCheck = predicate.predicate();
        if (predToCheck.isNegated()) {
            predToCheck = predToCheck.invertNegation();
        }

        return predToCheck.equals(ensPred.getPredicate())
                // && do parameters match
                && reqPred.index() == ensPred.getIndex();
    }

    private Collection<UnEnsuredPredicate> extractUnEnsuredPredicatesAtStatement(
            Statement statement, CrySLPredicate predicate, int index) {
        Collection<UnEnsuredPredicate> result = new HashSet<>();

        Collection<UnEnsuredPredicate> unEnsuredPreds =
                seed.getUnEnsuredPredicatesAtStatement(statement);
        for (UnEnsuredPredicate unEnsuredPredicate : unEnsuredPreds) {
            if (unEnsuredPredicate.getIndex() != index) {
                continue;
            }

            if (unEnsuredPredicate.getPredicate().equals(predicate)) { // && do parameters match
                result.add(unEnsuredPredicate);
            }
        }

        return result;
    }
}
