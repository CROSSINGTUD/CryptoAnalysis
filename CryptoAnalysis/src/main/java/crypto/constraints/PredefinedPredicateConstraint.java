package crypto.constraints;

import boomerang.scene.DeclaredMethod;
import boomerang.scene.Statement;
import boomerang.scene.Type;
import boomerang.scene.Val;
import com.google.common.collect.Multimap;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.errors.ConstraintError;
import crypto.extractparameter.ExtractedValue;
import crypto.extractparameter.ParameterWithExtractedValues;
import crypto.utils.MatcherUtils;
import crysl.rule.CrySLMethod;
import crysl.rule.CrySLObject;
import crysl.rule.CrySLPredicate;
import crysl.rule.ICrySLPredicateParameter;
import crysl.rule.ISLConstraint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

            DeclaredMethod foundCall = statement.getInvokeExpr().getMethod();
            Collection<CrySLMethod> matchingCryslMethods =
                    MatcherUtils.getMatchingCryslMethodsToDeclaredMethod(
                            seed.getSpecification(), foundCall);

            if (requiredCalls.stream().anyMatch(matchingCryslMethods::contains)) {
                isCalled = true;
            }
        }

        if (!isCalled) {
            IViolatedConstraint violatedConstraint =
                    new IViolatedConstraint.ViolatedCallToConstraint(requiredCalls);
            ConstraintError error =
                    new ConstraintError(
                            seed,
                            seed.getOrigin(),
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

            DeclaredMethod foundCall = statement.getInvokeExpr().getMethod();
            for (CrySLMethod method : notAllowedCalls) {
                if (MatcherUtils.matchCryslMethodAndDeclaredMethod(method, foundCall)) {
                    IViolatedConstraint violatedConstraint =
                            new IViolatedConstraint.ViolatedNoCallToConstraint(method);
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
            for (ExtractedValue extractedValue : parameter.extractedValues()) {
                for (Type type : extractedValue.types()) {
                    if (type.toString().equals(parameterType.getJavaType())) {
                        IViolatedConstraint violatedConstraint =
                                new IViolatedConstraint.ViolatedNeverTypeOfConstraint(
                                        parameter.param(),
                                        parameter.index(),
                                        parameterType.getJavaType());
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

            for (ExtractedValue extractedValue : parameter.extractedValues()) {
                for (Type type : extractedValue.types()) {
                    if (type.isNullType()) {
                        continue;
                    }

                    if (type.isSubtypeOf(parameterType.getJavaType())) {
                        isSubType = true;
                    }
                }
            }

            if (!isSubType) {
                IViolatedConstraint violatedConstraint =
                        new IViolatedConstraint.ViolatedInstanceOfConstraint(
                                parameter.param(), parameter.index(), parameterType.getJavaType());
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
            for (ExtractedValue extractedValue : parameter.extractedValues()) {
                if (isHardCoded(extractedValue)) {
                    IViolatedConstraint violatedConstraint =
                            new IViolatedConstraint.ViolatedNotHardCodedConstraint(
                                    parameter.param(), parameter.index(), extractedValue);
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

    private boolean isHardCoded(ExtractedValue extractedValue) {
        if (extractedValue.val().isConstant()) {
            return true;
        }

        Statement statement = extractedValue.initialStatement();
        if (statement.isAssign()) {
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
        return constraint.toString();
    }
}
