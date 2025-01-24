package crypto.constraints;

import boomerang.scene.ControlFlowGraph;
import boomerang.scene.Statement;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.errors.AbstractConstraintsError;
import crypto.analysis.errors.AlternativeReqPredicateError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.definition.Definitions;
import crypto.extractparameter.ExtractParameterAnalysis;
import crypto.extractparameter.ParameterWithExtractedValues;
import crypto.predicates.UnEnsuredPredicate;
import crysl.rule.CrySLConstraint;
import crysl.rule.CrySLPredicate;
import crysl.rule.ICrySLPredicateParameter;
import crysl.rule.ISLConstraint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class ConstraintsAnalysis {

    private final AnalysisSeedWithSpecification seed;
    private final Definitions.ConstraintsDefinition definition;

    private final Collection<Statement> collectedCalls;
    private final Collection<IRequiredPredicate> requiredPredicates;
    private final Multimap<Statement, ParameterWithExtractedValues> extractedValues;

    public ConstraintsAnalysis(
            AnalysisSeedWithSpecification seed, Definitions.ConstraintsDefinition definition) {
        this.seed = seed;
        this.definition = definition;

        this.collectedCalls = new HashSet<>();
        this.requiredPredicates = new HashSet<>();
        this.extractedValues = HashMultimap.create();
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

        for (ParameterWithExtractedValues param : params) {
            extractedValues.put(param.statement(), param);
        }

        definition.reporter().extractedParameterValues(seed, extractedValues);
    }

    private void initializeRequiredPredicates() {
        requiredPredicates.clear();

        for (Statement statement : collectedCalls) {
            Collection<IRequiredPredicate> reqPreds = extractPredicatesAtStatement(statement);

            requiredPredicates.addAll(reqPreds);
        }
    }

    private Collection<IRequiredPredicate> extractPredicatesAtStatement(Statement statement) {
        Collection<IRequiredPredicate> result = new HashSet<>();

        for (ISLConstraint constraint : seed.getSpecification().getRequiredPredicates()) {
            if (constraint instanceof CrySLPredicate predicate) {
                Collection<RequiredPredicate> reqPreds =
                        extractPredicateAtStatement(predicate, statement);

                result.addAll(reqPreds);
            } else if (constraint instanceof CrySLConstraint cryslConstraint) {
                Collection<CrySLPredicate> altPreds = extractAlternativePredicates(cryslConstraint);

                Collection<RequiredPredicate> reqPreds = new HashSet<>();
                for (CrySLPredicate pred : altPreds) {
                    Collection<RequiredPredicate> reqPredsForAlt =
                            extractPredicateAtStatement(pred, statement);

                    reqPreds.addAll(reqPredsForAlt);
                }

                if (!reqPreds.isEmpty()) {
                    AlternativeReqPredicate altPredicate =
                            new AlternativeReqPredicate(statement, altPreds, reqPreds);
                    result.add(altPredicate);
                }
            }
        }

        return result;
    }

    private Collection<RequiredPredicate> extractPredicateAtStatement(
            CrySLPredicate predicate, Statement statement) {
        Collection<RequiredPredicate> result = new HashSet<>();
        Collection<ParameterWithExtractedValues> params = extractedValues.get(statement);

        for (ICrySLPredicateParameter parameter : predicate.getParameters()) {
            String paramName = parameter.getName();

            // TODO Fix Cipher rule
            if (paramName.equals("transformation")) {
                continue;
            }

            // Predicates with _ can have any type
            if (paramName.equals("_")) {
                continue;
            }

            for (ParameterWithExtractedValues param : params) {
                if (param.varName().equals(paramName)) {
                    result.add(new RequiredPredicate(predicate, statement, param.index()));
                }
            }
        }

        if (statement.equals(seed.getOrigin())) {
            if (predicate.getParameters().get(0).getName().equals("this")) {
                result.add(new RequiredPredicate(predicate, statement, -1));
            }
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
        Collection<RequiredPredicate> reqPreds = new HashSet<>();

        for (IRequiredPredicate reqPred : requiredPredicates) {
            if (reqPred instanceof RequiredPredicate singlePred) {
                reqPreds.add(singlePred);
            } else if (reqPred instanceof AlternativeReqPredicate altPred) {
                reqPreds.addAll(altPred.predicates());
            }
        }

        return reqPreds;
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

        for (IRequiredPredicate reqPred : requiredPredicates) {
            if (reqPred instanceof RequiredPredicate singlePred) {
                Collection<AbstractConstraintsError> predErrors =
                        evaluateSingleRequiredPredicate(singlePred, statements);

                errors.addAll(predErrors);
            } else if (reqPred instanceof AlternativeReqPredicate altPred) {
                Collection<AbstractConstraintsError> predErrors =
                        evaluateAlternativeReqPredicate(altPred, statements);

                errors.addAll(predErrors);
            }
        }

        return errors;
    }

    private Collection<AbstractConstraintsError> evaluateSingleRequiredPredicate(
            RequiredPredicate predicate, Collection<Statement> statements) {
        RequiredPredicateConstraint constraint =
                new RequiredPredicateConstraint(seed, statements, extractedValues, predicate);
        EvaluableConstraint.EvaluationResult result = constraint.evaluate();
        definition.reporter().onEvaluatedPredicate(seed, constraint, result);

        Collection<AbstractConstraintsError> errors = new HashSet<>();
        if (constraint.isViolated()) {
            errors.addAll(constraint.getErrors());
        }

        return errors;
    }

    private Collection<AbstractConstraintsError> evaluateAlternativeReqPredicate(
            AlternativeReqPredicate altPred, Collection<Statement> statements) {
        Collection<AbstractConstraintsError> errors = new HashSet<>();

        Multimap<RequiredPredicate, AbstractConstraintsError> violatedPredsToErrors =
                HashMultimap.create();
        for (RequiredPredicate predicate : altPred.predicates()) {
            RequiredPredicateConstraint constraint =
                    new RequiredPredicateConstraint(seed, statements, extractedValues, predicate);
            EvaluableConstraint.EvaluationResult result = constraint.evaluate();
            definition.reporter().onEvaluatedPredicate(seed, constraint, result);

            if (constraint.isViolated()) {
                violatedPredsToErrors.putAll(predicate, constraint.getErrors());
            }
        }

        Collection<RequiredPredicate> ensuredPreds = new ArrayList<>(altPred.predicates());
        for (RequiredPredicate violatedPred : violatedPredsToErrors.keySet()) {
            ensuredPreds.remove(violatedPred);
        }

        // If no alternative is ensured, report a single error for all alternatives
        if (ensuredPreds.isEmpty()) {
            Collection<UnEnsuredPredicate> unEnsuredPredicates = new HashSet<>();
            for (AbstractConstraintsError error : violatedPredsToErrors.values()) {
                if (error instanceof RequiredPredicateError reqPredError) {
                    unEnsuredPredicates.addAll(reqPredError.getHiddenPredicates());
                }
            }

            AlternativeReqPredicateError error =
                    new AlternativeReqPredicateError(
                            seed, seed.getSpecification(), altPred, unEnsuredPredicates);
            errors.add(error);
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
