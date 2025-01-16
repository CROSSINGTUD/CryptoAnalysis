package crypto.constraints;

import boomerang.scene.ControlFlowGraph;
import boomerang.scene.Statement;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.errors.AbstractConstraintsError;
import crypto.analysis.errors.AlternativeReqPredicateError;
import crypto.analysis.errors.PredicateContradictionError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.definition.Definitions;
import crypto.extractparameter.ExtractParameterAnalysis;
import crypto.extractparameter.ParameterWithExtractedValues;
import crysl.rule.CrySLConstraint;
import crysl.rule.CrySLPredicate;
import crysl.rule.ICrySLPredicateParameter;
import crysl.rule.ISLConstraint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class ConstraintsAnalysis {

    private final AnalysisSeedWithSpecification seed;
    private final Definitions.ConstraintsDefinition definition;

    private final Collection<Statement> collectedCalls;
    private final Collection<RequiredPredicate> requiredPredicates;
    private final Collection<ParameterWithExtractedValues> extractedValues;

    public ConstraintsAnalysis(
            AnalysisSeedWithSpecification seed, Definitions.ConstraintsDefinition definition) {
        this.seed = seed;
        this.definition = definition;

        this.collectedCalls = new HashSet<>();
        this.requiredPredicates = new HashSet<>();
        this.extractedValues = new HashSet<>();
    }

    public void initialize() {
        initializeCollectedCalls();
        initializeExtractedValues();
        initializeRequiredPredicates();
    }

    private void initializeCollectedCalls() {
        collectedCalls.clear();

        Collection<ControlFlowGraph.Edge> edges = seed.getAllCallsOnObject().keySet();
        for (ControlFlowGraph.Edge edge : edges) {
            collectedCalls.add(edge.getStart());
        }
    }

    private void initializeExtractedValues() {
        extractedValues.clear();

        Definitions.ExtractParameterDefinition parameterDefinition =
                new Definitions.ExtractParameterDefinition(
                        definition.callGraph(),
                        definition.dataFlowScope(),
                        definition.timeout(),
                        definition.strategy(),
                        definition.reporter());
        ExtractParameterAnalysis analysis = new ExtractParameterAnalysis(parameterDefinition);

        Collection<ParameterWithExtractedValues> params =
                analysis.extractParameters(collectedCalls, seed.getSpecification());
        extractedValues.addAll(params);

        definition.reporter().extractedParameterValues(seed, extractedValues);
    }

    private void initializeRequiredPredicates() {
        requiredPredicates.clear();

        for (ISLConstraint constraint : seed.getSpecification().getRequiredPredicates()) {
            if (constraint instanceof CrySLPredicate predicate) {
                Collection<RequiredPredicate> reqPreds =
                        extractRequiredPredicates(predicate, extractedValues);

                requiredPredicates.addAll(reqPreds);
            } else if (constraint instanceof CrySLConstraint cryslConstraint) {
                Collection<CrySLPredicate> allAlts = extractAlternativePredicates(cryslConstraint);

                for (CrySLPredicate predicate : allAlts) {
                    Collection<RequiredPredicate> reqPreds =
                            extractRequiredPredicates(predicate, extractedValues);

                    requiredPredicates.addAll(reqPreds);
                }
            }
        }
    }

    private Collection<RequiredPredicate> extractRequiredPredicates(
            CrySLPredicate pred, Collection<ParameterWithExtractedValues> values) {
        Collection<RequiredPredicate> result = new HashSet<>();

        for (ICrySLPredicateParameter parameter : pred.getParameters()) {
            String paramName = parameter.getName();

            // TODO Fix Cipher rule
            if (paramName.equals("transformation")) {
                continue;
            }

            // Predicates with _ can have any type
            if (paramName.equals("_")) {
                continue;
            }

            for (ParameterWithExtractedValues value : values) {
                if (value.varName().equals(paramName)) {
                    result.add(new RequiredPredicate(pred, value.statement(), value.index()));
                }
            }
        }

        if (pred.getParameters().stream().anyMatch(param -> param.getName().equals("this"))) {
            result.add(new RequiredPredicate(pred, seed.getOrigin(), -1));
        }

        return result;
    }

    private List<CrySLPredicate> extractAlternativePredicates(CrySLConstraint cons) {
        List<CrySLPredicate> result = new ArrayList<>();

        if (cons.getLeft() instanceof CrySLPredicate predicate) {
            result.add(predicate);
        }

        ISLConstraint right = cons.getRight();
        if (right instanceof CrySLPredicate predicate) {
            result.add(predicate);
        } else if (right instanceof CrySLConstraint constraint) {
            Collection<CrySLPredicate> rightPreds = extractAlternativePredicates(constraint);
            result.addAll(rightPreds);
        }

        return result;
    }

    public Collection<RequiredPredicate> getRequiredPredicates() {
        return requiredPredicates;
    }

    public Collection<AbstractConstraintsError> evaluateConstraints() {
        return evaluateConstraints(collectedCalls);
    }

    public Collection<AbstractConstraintsError> evaluateConstraints(
            Collection<Statement> statements) {
        Collection<AbstractConstraintsError> errors = new HashSet<>();

        for (ISLConstraint cons : seed.getSpecification().getConstraints()) {
            EvaluableConstraint constraint =
                    EvaluableConstraint.getInstance(seed, cons, statements, extractedValues);
            EvaluableConstraint.EvaluationResult result = constraint.evaluate();

            definition.reporter().onEvaluatedConstraint(seed, constraint, result);

            if (constraint.isViolated()) {
                errors.addAll(constraint.getErrors());
            }
        }

        return errors;
    }

    public Collection<AbstractConstraintsError> evaluateRequiredPredicates() {
        return evaluateRequiredPredicates(collectedCalls);
    }

    public Collection<AbstractConstraintsError> evaluateRequiredPredicates(
            Collection<Statement> statements) {
        Collection<AbstractConstraintsError> errors = new HashSet<>();

        for (ISLConstraint cons : seed.getSpecification().getRequiredPredicates()) {
            if (cons instanceof CrySLPredicate predicate) {
                Collection<AbstractConstraintsError> singleErrors =
                        evaluateSingleRequiredPredicate(predicate, statements, extractedValues);

                errors.addAll(singleErrors);
            } else if (cons instanceof CrySLConstraint constraint) {
                Collection<AbstractConstraintsError> altErrors =
                        evaluateAlternativeReqPredicate(constraint, statements, extractedValues);

                errors.addAll(altErrors);
            }
        }

        return errors;
    }

    private Collection<AbstractConstraintsError> evaluateSingleRequiredPredicate(
            CrySLPredicate predicate,
            Collection<Statement> statements,
            Collection<ParameterWithExtractedValues> extractedValues) {
        EvaluableConstraint constraint =
                EvaluableConstraint.getInstance(seed, predicate, statements, extractedValues);
        EvaluableConstraint.EvaluationResult result = constraint.evaluate();

        definition.reporter().onEvaluatedPredicate(seed, constraint, result);

        Collection<AbstractConstraintsError> errors = new HashSet<>();
        if (constraint.isViolated()) {
            errors.addAll(constraint.getErrors());
        }

        return errors;
    }

    private Collection<AbstractConstraintsError> evaluateAlternativeReqPredicate(
            CrySLConstraint cons,
            Collection<Statement> statements,
            Collection<ParameterWithExtractedValues> extractedValues) {
        Collection<AbstractConstraintsError> errors = new HashSet<>();

        List<CrySLPredicate> predicates = extractAlternativePredicates(cons);
        Collections.reverse(predicates);
        if (predicates.isEmpty()) {
            return errors;
        }

        Multimap<Statement, AbstractConstraintsError> statementToErrors = HashMultimap.create();
        for (CrySLPredicate predicate : predicates) {
            EvaluableConstraint constraint =
                    EvaluableConstraint.getInstance(seed, predicate, statements, extractedValues);
            EvaluableConstraint.EvaluationResult result = constraint.evaluate();
            definition.reporter().onEvaluatedPredicate(seed, constraint, result);

            for (AbstractConstraintsError error : constraint.getErrors()) {
                statementToErrors.put(error.getErrorStatement(), error);
            }
        }

        for (Statement statement : statementToErrors.keySet()) {
            Collection<CrySLPredicate> ensuredPreds = new ArrayList<>(predicates);

            for (AbstractConstraintsError error : statementToErrors.get(statement)) {
                if (error instanceof RequiredPredicateError reqPredError) {
                    ensuredPreds.remove(reqPredError.getContradictedPredicates());
                } else if (error instanceof PredicateContradictionError predContradictionError) {
                    ensuredPreds.remove(predContradictionError.getContradictedPredicate());
                }
            }

            if (ensuredPreds.isEmpty()) {
                AlternativeReqPredicateError error =
                        new AlternativeReqPredicateError(
                                seed,
                                statement,
                                seed.getSpecification(),
                                predicates,
                                statementToErrors.get(statement));
                errors.add(error);
            }
        }

        return errors;
    }

    /**
     * Check for a predicate A =&gt; B, whether the condition A of B is satisfied
     *
     * @param pred the predicate to be checked
     * @return true if the condition is satisfied
     */
    public boolean isPredConditionViolated(CrySLPredicate pred) {
        return isPredConditionViolated(pred, collectedCalls);
    }

    public boolean isPredConditionViolated(CrySLPredicate pred, Collection<Statement> statements) {
        if (pred.getConstraint().isEmpty()) {
            return false;
        }

        EvaluableConstraint constraint =
                EvaluableConstraint.getInstance(
                        seed, pred.getConstraint().get(), statements, extractedValues);
        EvaluableConstraint.EvaluationResult result = constraint.evaluate();

        return result == EvaluableConstraint.EvaluationResult.ConstraintIsNotSatisfied;
    }
}
