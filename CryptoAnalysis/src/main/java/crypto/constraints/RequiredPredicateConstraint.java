package crypto.constraints;

import boomerang.scene.Statement;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.errors.PredicateContradictionError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.extractparameter.ParameterWithExtractedValues;
import crypto.predicates.EnsuredPredicate;
import crypto.predicates.UnEnsuredPredicate;
import crysl.rule.CrySLPredicate;
import crysl.rule.ICrySLPredicateParameter;
import crysl.rule.ISLConstraint;
import java.util.Collection;
import java.util.HashSet;

public class RequiredPredicateConstraint extends EvaluableConstraint {

    private final CrySLPredicate predicate;
    private final Multimap<Statement, ParameterWithExtractedValues> statementToValues;

    public RequiredPredicateConstraint(
            AnalysisSeedWithSpecification seed,
            Collection<Statement> statements,
            Collection<ParameterWithExtractedValues> extractedValues,
            CrySLPredicate predicate) {
        super(seed, statements, extractedValues);

        this.predicate = predicate;

        statementToValues = HashMultimap.create();
        for (ParameterWithExtractedValues param : extractedValues) {
            statementToValues.put(param.statement(), param);
        }
    }

    @Override
    public ISLConstraint getConstraint() {
        return predicate;
    }

    @Override
    public EvaluationResult evaluate() {
        // TODO Move this outside
        if (predicate.getConstraint().isPresent()) {
            EvaluableConstraint constraint =
                    EvaluableConstraint.getInstance(
                            seed, predicate.getConstraint().get(), statements, extractedValues);
            EvaluationResult result = constraint.evaluate();

            if (result == EvaluationResult.ConstraintIsNotRelevant) {
                return EvaluationResult.ConstraintIsNotRelevant;
            } else if (result == EvaluationResult.ConstraintIsNotSatisfied) {
                return EvaluationResult.ConstraintIsNotRelevant;
            }
        }

        boolean isRelevant = false;

        for (Statement statement : statements) {
            Collection<Integer> indices = getIndicesForPredicateAtStatement(predicate, statement);

            if (!indices.isEmpty()) {
                isRelevant = true;
            }

            for (Integer index : indices) {
                if (predicate.isNegated()) {
                    evaluateNegatedPredicateAtStatement(predicate, statement, index);
                } else {
                    evaluatePredicateAtStatement(predicate, statement, index);
                }
            }
        }

        if (!isRelevant) {
            return EvaluationResult.ConstraintIsNotRelevant;
        }

        if (isSatisfied()) {
            return EvaluationResult.ConstraintIsSatisfied;
        } else if (isViolated()) {
            return EvaluationResult.ConstraintIsNotSatisfied;
        }

        return EvaluationResult.ConstraintIsNotRelevant;
    }

    private Collection<Integer> getIndicesForPredicateAtStatement(
            CrySLPredicate predicate, Statement statement) {
        Collection<Integer> indices = new HashSet<>();

        Collection<ParameterWithExtractedValues> values = statementToValues.get(statement);
        for (ParameterWithExtractedValues value : values) {
            // Predicates with _ can have any type
            if (value.varName().equals("_")) {
                continue;
            }

            for (ICrySLPredicateParameter parameter : predicate.getParameters()) {
                String paramName = parameter.getName();

                // TODO: FIX Cipher rule
                if (paramName.equals("transformation")) {
                    continue;
                }

                if (paramName.equals(value.varName())) {
                    indices.add(value.index());
                }
            }
        }

        /* A predicate may contain the keyword 'this' as the first parameter. In this case,
         * the predicate is expected to be ensured when generating the seed.
         */
        if (statement.equals(seed.getOrigin())) {
            if (predicate.getParameters().get(0).getName().equals("this")) {
                indices.add(-1);
            }
        }

        return indices;
    }

    private void evaluatePredicateAtStatement(
            CrySLPredicate predicate, Statement statement, int index) {
        boolean ensured = false;

        Collection<EnsuredPredicate> ensuredPredsAtStatement =
                seed.getEnsuredPredicatesAtStatement(statement);
        for (EnsuredPredicate ensuredPred : ensuredPredsAtStatement) {
            if (doReqPredAndEnsPredMatch(predicate, index, ensuredPred)) {
                ensured = true;
            }
        }

        if (!ensured) {
            Collection<UnEnsuredPredicate> predicates =
                    extractUnEnsuredPredicatesAtStatement(statement, predicate, index);

            RequiredPredicateError error =
                    new RequiredPredicateError(
                            seed, statement, seed.getSpecification(), predicate, index, predicates);
            errors.add(error);
        }
    }

    private void evaluateNegatedPredicateAtStatement(
            CrySLPredicate negatedPred, Statement statement, int index) {
        boolean ensured = false;

        Collection<EnsuredPredicate> ensuredPredsAtStatement =
                seed.getEnsuredPredicatesAtStatement(statement);
        for (EnsuredPredicate ensuredPred : ensuredPredsAtStatement) {
            if (doReqPredAndEnsPredMatch(negatedPred, index, ensuredPred)) {
                ensured = true;
            }
        }

        if (ensured) {
            PredicateContradictionError error =
                    new PredicateContradictionError(
                            seed, statement, seed.getSpecification(), predicate, index);
            errors.add(error);
        }
    }

    private boolean doReqPredAndEnsPredMatch(
            CrySLPredicate reqPred, int reqPredIndex, EnsuredPredicate ensPred) {
        CrySLPredicate predToCheck;
        if (reqPred.isNegated()) {
            predToCheck = reqPred.invertNegation();
        } else {
            predToCheck = reqPred;
        }
        return predToCheck.equals(ensPred.getPredicate())
                // && doPredsMatch(predToCheck, ensPred.getKey())
                && reqPredIndex == ensPred.getIndex();
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

            if (unEnsuredPredicate.getPredicate().equals(predicate)) { // && doPredsMatch())
                result.add(unEnsuredPredicate);
            }
        }

        return result;
    }
}
