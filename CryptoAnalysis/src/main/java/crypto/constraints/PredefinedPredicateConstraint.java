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

import boomerang.scope.DeclaredMethod;
import boomerang.scope.Statement;
import boomerang.scope.Type;
import boomerang.scope.Val;
import com.google.common.collect.Multimap;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.errors.ConstraintError;
import crypto.constraints.violations.IViolatedConstraint;
import crypto.constraints.violations.ViolatedCallToConstraint;
import crypto.constraints.violations.ViolatedInstanceOfConstraint;
import crypto.constraints.violations.ViolatedNeverTypeOfConstraint;
import crypto.constraints.violations.ViolatedNoCallToConstraint;
import crypto.constraints.violations.ViolatedNotHardCodedConstraint;
import crypto.extractparameter.ParameterWithExtractedValues;
import crypto.extractparameter.TransformedValue;
import crypto.extractparameter.scope.UnknownType;
import crypto.utils.CrySLUtils;
import crypto.utils.MatcherUtils;
import crysl.rule.CrySLMethod;
import crysl.rule.CrySLObject;
import crysl.rule.CrySLPredicate;
import crysl.rule.ICrySLPredicateParameter;
import crysl.rule.ISLConstraint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Constraint that evaluates the predefined predicates 'callTo', 'noCallTo', 'neverTypeOf',
 * 'instanceOf' and 'notHardCoded'. The predicate 'length' is evaluated as part of an arithmetic
 * constraint in an {@link ComparisonConstraint}, the predicate 'elements' is currently not
 * supported.
 */
public class PredefinedPredicateConstraint extends EvaluableConstraint {

    private final CrySLPredicate constraint;

    public PredefinedPredicateConstraint(
            AnalysisSeedWithSpecification seed,
            Collection<Statement> statements,
            Multimap<Statement, ParameterWithExtractedValues> extractedValues,
            CrySLPredicate constraint) {
        super(seed, statements, extractedValues);

        this.constraint = constraint;
    }

    @Override
    public ISLConstraint getConstraint() {
        return constraint;
    }

    @Override
    public EvaluationResult evaluate() {
        String predName = constraint.getPredName();

        if (predefinedPredicates.contains(predName)) {
            return handlePredefinedPredicate(constraint);
        }

        return EvaluationResult.ConstraintIsNotRelevant;
    }

    private EvaluationResult handlePredefinedPredicate(CrySLPredicate predicate) {
        switch (predicate.getPredName()) {
            case "callTo" -> {
                return evaluateCallToPredicate(predicate);
            }
            case "noCallTo" -> {
                return evaluateNoCallToPredicate(predicate);
            }
            case "neverTypeOf" -> {
                return evaluateNeverTypeOfPredicate(predicate);
            }
            case "length" -> {
                return evaluateLengthPredicate(predicate);
            }
            case "instanceOf" -> {
                return evaluateInstanceOfPredicate(predicate);
            }
            case "notHardCoded" -> {
                return evaluateNotHardCodedPredicate(predicate);
            }
            default ->
                    throw new UnsupportedOperationException(
                            "Predicate " + predicate.getPredName() + " is not supported");
        }
    }

    private EvaluationResult evaluateCallToPredicate(CrySLPredicate predicate) {
        // callTo[$methods]
        boolean isCalled = false;
        Collection<CrySLMethod> requiredCalls = parametersToCryslMethods(predicate.getParameters());

        for (Statement statement : statements) {
            if (!statement.containsInvokeExpr()) {
                continue;
            }

            DeclaredMethod foundCall = statement.getInvokeExpr().getDeclaredMethod();
            Collection<CrySLMethod> matchingCryslMethods =
                    MatcherUtils.getMatchingCryslMethodsToDeclaredMethod(
                            seed.getSpecification().getEvents(), foundCall);

            if (requiredCalls.stream().anyMatch(matchingCryslMethods::contains)) {
                isCalled = true;
            }
        }

        if (!isCalled) {
            IViolatedConstraint violatedConstraint = new ViolatedCallToConstraint(requiredCalls);
            ConstraintError error =
                    new ConstraintError(
                            seed,
                            seed.getInitialStatement(),
                            seed.getSpecification(),
                            this,
                            violatedConstraint);
            errors.add(error);

            return EvaluationResult.ConstraintIsNotSatisfied;
        }

        return EvaluationResult.ConstraintIsSatisfied;
    }

    private EvaluationResult evaluateNoCallToPredicate(CrySLPredicate predicate) {
        // noCallTo[$methods]
        EvaluationResult result = EvaluationResult.ConstraintIsSatisfied;
        Collection<CrySLMethod> notAllowedCalls =
                parametersToCryslMethods(predicate.getParameters());

        for (Statement statement : statements) {
            if (!statement.containsInvokeExpr()) {
                continue;
            }

            DeclaredMethod foundCall = statement.getInvokeExpr().getDeclaredMethod();
            for (CrySLMethod method : notAllowedCalls) {
                if (MatcherUtils.matchCryslMethodAndDeclaredMethod(method, foundCall)) {
                    IViolatedConstraint violatedConstraint = new ViolatedNoCallToConstraint(method);
                    ConstraintError error =
                            new ConstraintError(
                                    seed,
                                    statement,
                                    seed.getSpecification(),
                                    this,
                                    violatedConstraint);
                    errors.add(error);

                    result = EvaluationResult.ConstraintIsNotSatisfied;
                }
            }
        }

        return result;
    }

    private EvaluationResult evaluateNeverTypeOfPredicate(CrySLPredicate predicate) {
        EvaluationResult result = EvaluationResult.ConstraintIsSatisfied;
        List<CrySLObject> objects = parametersToCryslObjects(predicate.getParameters());

        // neverTypeOf[$variable, $type]
        if (objects.size() != 2) {
            return EvaluationResult.ConstraintIsNotRelevant;
        }

        CrySLObject variable = objects.get(0);
        CrySLObject parameterType = objects.get(1);

        Collection<ParameterWithExtractedValues> relevantExtractedValues =
                filterRelevantParameterResults(variable.getName(), extractedValues.values());
        if (relevantExtractedValues.isEmpty()) {
            return EvaluationResult.ConstraintIsNotRelevant;
        }

        for (ParameterWithExtractedValues parameter : relevantExtractedValues) {
            for (TransformedValue value : parameter.extractedValues()) {
                for (Type type : value.getTrackedTypes()) {
                    if (type.equals(UnknownType.getInstance())) {
                        // TODO Discuss if an error should be reported when the type is unknown
                    }

                    if (type.toString().equals(parameterType.getJavaType())) {
                        IViolatedConstraint violatedConstraint =
                                new ViolatedNeverTypeOfConstraint(
                                        parameter, parameterType.getJavaType());
                        ConstraintError error =
                                new ConstraintError(
                                        seed,
                                        parameter.statement(),
                                        seed.getSpecification(),
                                        this,
                                        violatedConstraint);
                        errors.add(error);

                        result = EvaluationResult.ConstraintIsNotSatisfied;
                    }
                }
            }
        }

        return result;
    }

    private EvaluationResult evaluateLengthPredicate(
            @SuppressWarnings("unused") CrySLPredicate predicate) {
        // length(...) is only used in combination with arithmetic constraints
        return EvaluationResult.ConstraintIsNotRelevant;
    }

    private EvaluationResult evaluateInstanceOfPredicate(CrySLPredicate predicate) {
        EvaluationResult result = EvaluationResult.ConstraintIsSatisfied;
        List<CrySLObject> objects = parametersToCryslObjects(predicate.getParameters());

        // instanceOf[$variable, $type]
        if (objects.size() != 2) {
            return EvaluationResult.ConstraintIsNotRelevant;
        }

        CrySLObject variable = objects.get(0);
        CrySLObject parameterType = objects.get(1);

        Collection<ParameterWithExtractedValues> relevantExtractedValues =
                filterRelevantParameterResults(variable.getName(), extractedValues.values());
        if (relevantExtractedValues.isEmpty()) {
            return EvaluationResult.ConstraintIsNotRelevant;
        }

        for (ParameterWithExtractedValues parameter : relevantExtractedValues) {
            boolean isSubType = false;

            Collection<Type> typesAtStatement = new HashSet<>();
            for (TransformedValue value : parameter.extractedValues()) {
                typesAtStatement.add(value.getTransformedVal().getType());
            }

            for (Type type : typesAtStatement) {
                if (type.isNullType()) {
                    continue;
                }

                if (type.equals(UnknownType.getInstance())) {
                    // TODO Report imprecise extraction error when the type is not known
                }

                if (MatcherUtils.isTypeOrSubType(type.toString(), parameterType.getJavaType())) {
                    isSubType = true;
                }
            }

            if (!isSubType) {
                IViolatedConstraint violatedConstraint =
                        new ViolatedInstanceOfConstraint(parameter, parameterType.getJavaType());
                ConstraintError error =
                        new ConstraintError(
                                seed,
                                parameter.statement(),
                                seed.getSpecification(),
                                this,
                                violatedConstraint);
                errors.add(error);

                result = EvaluationResult.ConstraintIsNotSatisfied;
            }
        }

        return result;
    }

    private EvaluationResult evaluateNotHardCodedPredicate(CrySLPredicate predicate) {
        EvaluationResult result = EvaluationResult.ConstraintIsSatisfied;
        List<CrySLObject> objects = parametersToCryslObjects(predicate.getParameters());

        // notHardCoded[$variable]
        if (objects.size() != 1) {
            return EvaluationResult.ConstraintIsNotRelevant;
        }

        CrySLObject variable = objects.get(0);

        Collection<ParameterWithExtractedValues> relevantExtractedValues =
                filterRelevantParameterResults(variable.getName(), extractedValues.values());
        if (relevantExtractedValues.isEmpty()) {
            return EvaluationResult.ConstraintIsNotRelevant;
        }

        for (ParameterWithExtractedValues parameter : relevantExtractedValues) {
            for (TransformedValue value : parameter.extractedValues()) {
                if (isHardCoded(value)) {
                    IViolatedConstraint violatedConstraint =
                            new ViolatedNotHardCodedConstraint(parameter, value);
                    ConstraintError error =
                            new ConstraintError(
                                    seed,
                                    parameter.statement(),
                                    seed.getSpecification(),
                                    this,
                                    violatedConstraint);
                    errors.add(error);

                    result = EvaluationResult.ConstraintIsNotSatisfied;
                }
            }
        }

        return result;
    }

    private boolean isHardCoded(TransformedValue value) {
        if (value.getTransformedVal().isConstant()) {
            return true;
        }

        Statement statement = value.getStatement();
        if (statement.isAssignStmt()) {
            Val rightOp = statement.getRightOp();

            return rightOp.isNewExpr() || rightOp.isArrayAllocationVal();
        }

        return false;
    }

    private Collection<CrySLMethod> parametersToCryslMethods(
            Collection<ICrySLPredicateParameter> parameters) {
        List<CrySLMethod> methods = new ArrayList<>();

        for (ICrySLPredicateParameter parameter : parameters) {
            if (parameter instanceof CrySLMethod crySLMethod) {
                methods.add(crySLMethod);
            }
        }
        return methods;
    }

    private List<CrySLObject> parametersToCryslObjects(
            Collection<ICrySLPredicateParameter> parameters) {
        List<CrySLObject> objects = new ArrayList<>();

        for (ICrySLPredicateParameter parameter : parameters) {
            if (parameter instanceof CrySLObject crySLObject) {
                objects.add(crySLObject);
            }
        }
        return objects;
    }

    @Override
    public String toString() {
        switch (constraint.getPredName()) {
            case "callTo" -> {
                Collection<CrySLMethod> methods =
                        parametersToCryslMethods(constraint.getParameters());
                String formattedMethods = CrySLUtils.formatMethodNames(methods);

                return "callTo["
                        + formattedMethods.substring(1, formattedMethods.length() - 1)
                        + "]";
            }
            case "noCallTo" -> {
                Collection<CrySLMethod> methods =
                        parametersToCryslMethods(constraint.getParameters());
                String formattedMethods = CrySLUtils.formatMethodNames(methods);

                return "noCallTo["
                        + formattedMethods.substring(1, formattedMethods.length() - 1)
                        + "]";
            }
            case "neverTypeOf" -> {
                List<CrySLObject> objects = parametersToCryslObjects(constraint.getParameters());

                CrySLObject variable = objects.get(0);
                CrySLObject parameterType = objects.get(1);

                return "neverTypeOf["
                        + variable.getVarName()
                        + ", "
                        + parameterType.getJavaType()
                        + "]";
            }
            case "instanceOf" -> {
                List<CrySLObject> objects = parametersToCryslObjects(constraint.getParameters());

                CrySLObject variable = objects.get(0);
                CrySLObject parameterType = objects.get(1);

                return "instanceOf["
                        + variable.getVarName()
                        + ", "
                        + parameterType.getJavaType()
                        + "]";
            }
            case "notHardCoded" -> {
                List<CrySLObject> objects = parametersToCryslObjects(constraint.getParameters());

                CrySLObject variable = objects.get(0);
                return "notHardCoded[" + variable.getVarName() + "]";
            }
            default -> {
                return constraint.toString();
            }
        }
    }
}
