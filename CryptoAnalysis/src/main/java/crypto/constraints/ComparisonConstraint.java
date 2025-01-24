package crypto.constraints;

import boomerang.scene.Statement;
import com.google.common.collect.Multimap;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.extractparameter.ExtractedValue;
import crypto.extractparameter.ParameterWithExtractedValues;
import crysl.rule.CrySLArithmeticConstraint;
import crysl.rule.CrySLComparisonConstraint;
import crysl.rule.CrySLObject;
import crysl.rule.CrySLPredicate;
import crysl.rule.ISLConstraint;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ComparisonConstraint extends EvaluableConstraint {

    private final CrySLComparisonConstraint constraint;
    private final IArithmeticConstraint left;
    private final IArithmeticConstraint right;

    protected ComparisonConstraint(
            AnalysisSeedWithSpecification seed,
            CrySLComparisonConstraint constraint,
            Collection<Statement> statements,
            Multimap<Statement, ParameterWithExtractedValues> extractedValues) {
        super(seed, statements, extractedValues);

        this.constraint = constraint;

        this.left = new ArithmeticConstraint(constraint.getLeft(), extractedValues);
        this.right = new ArithmeticConstraint(constraint.getRight(), extractedValues);
    }

    @Override
    public ISLConstraint getConstraint() {
        return constraint;
    }

    @Override
    public EvaluationResult evaluate() {
        boolean isRelevant = false;

        for (Statement statement : statements) {
            IntermediateResult leftResult = left.evaluate(statement);
            IntermediateResult rightResult = right.evaluate(statement);

            Collection<ImpreciseResult> impreciseResults = new HashSet<>();
            impreciseResults.addAll(leftResult.impreciseResults());
            impreciseResults.addAll(rightResult.impreciseResults());

            if (!(leftResult.evaluatedValues().isEmpty() || rightResult.evaluatedValues().isEmpty())
                    || !impreciseResults.isEmpty()) {
                isRelevant = true;
            }

            if (impreciseResults.isEmpty()) {
                evaluateConstraint(statement, leftResult, rightResult);
            } else {
                handleImpreciseResult(statement, impreciseResults);
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

    private boolean isOperatorSatisfied(int leftValue, int rightValue) {
        switch (constraint.getOperator()) {
            case eq -> {
                return leftValue == rightValue;
            }
            case neq -> {
                return leftValue != rightValue;
            }
            case g -> {
                return leftValue > rightValue;
            }
            case ge -> {
                return leftValue >= rightValue;
            }
            case l -> {
                return leftValue < rightValue;
            }
            case le -> {
                return leftValue <= rightValue;
            }
            default ->
                    throw new UnsupportedOperationException(
                            "Compare operator " + constraint.getOperator() + " is not implemented");
        }
    }

    private void evaluateConstraint(
            Statement statement, IntermediateResult leftResult, IntermediateResult rightResult) {
        for (Integer leftValue : leftResult.evaluatedValues()) {
            for (Integer rightValue : rightResult.evaluatedValues()) {
                boolean satisfied = isOperatorSatisfied(leftValue, rightValue);

                if (!satisfied) {
                    IViolatedConstraint violatedConstraint =
                            new IViolatedConstraint.ViolatedComparisonConstraint(this);
                    ConstraintError error =
                            new ConstraintError(
                                    seed,
                                    statement,
                                    seed.getSpecification(),
                                    this,
                                    violatedConstraint);
                    errors.add(error);
                }
            }
        }
    }

    private void handleImpreciseResult(
            Statement statement, Collection<ImpreciseResult> impreciseResults) {
        for (ImpreciseResult result : impreciseResults) {
            // TODO Consider imprecise constants
            if (result instanceof ImpreciseResult.ImpreciseExtractedValue value) {
                ImpreciseValueExtractionError error =
                        new ImpreciseValueExtractionError(
                                seed,
                                statement,
                                seed.getSpecification(),
                                value.parameter(),
                                value.impreciseValue(),
                                this);
                errors.add(error);
            }
        }
    }

    @Override
    public String toString() {
        switch (constraint.getOperator()) {
            case eq -> {
                return left + " == " + right;
            }
            case neq -> {
                return left + " != " + right;
            }
            case g -> {
                return left + " > " + right;
            }
            case ge -> {
                return left + " >= " + right;
            }
            case l -> {
                return left + " < " + right;
            }
            case le -> {
                return left + " <= " + right;
            }
            default -> {
                return left + " <unknown operator> " + right;
            }
        }
    }

    private abstract static class IArithmeticConstraint {

        protected final Multimap<Statement, ParameterWithExtractedValues> statementToValues;

        public IArithmeticConstraint(
                Multimap<Statement, ParameterWithExtractedValues> statementToValues) {
            this.statementToValues = statementToValues;
        }

        public abstract IntermediateResult evaluate(Statement statement);
    }

    private static class ArithmeticConstraint extends IArithmeticConstraint {

        private final CrySLArithmeticConstraint constraint;
        private final IArithmeticConstraint left;
        private final IArithmeticConstraint right;

        public ArithmeticConstraint(
                CrySLArithmeticConstraint constraint,
                Multimap<Statement, ParameterWithExtractedValues> statementToValues) {
            super(statementToValues);

            this.constraint = constraint;

            if (constraint.getLeft() instanceof CrySLObject leftObject) {
                left = new ArithmeticSingleton(leftObject, statementToValues);
            } else if (constraint.getLeft() instanceof CrySLArithmeticConstraint leftConstraint) {
                left = new ArithmeticConstraint(leftConstraint, statementToValues);
            } else if (constraint.getLeft() instanceof CrySLPredicate leftPredicate) {
                left = new ArithmeticLength(leftPredicate, statementToValues);
            } else {
                throw new IllegalArgumentException("Could not evaluate constraint");
            }

            if (constraint.getRight() instanceof CrySLObject rightObject) {
                right = new ArithmeticSingleton(rightObject, statementToValues);
            } else if (constraint.getRight() instanceof CrySLArithmeticConstraint rightConstraint) {
                right = new ArithmeticConstraint(rightConstraint, statementToValues);
            } else if (constraint.getRight() instanceof CrySLPredicate rightPredicate) {
                right = new ArithmeticLength(rightPredicate, statementToValues);
            } else {
                throw new IllegalArgumentException("Could not evaluate constraint");
            }
        }

        public IntermediateResult evaluate(Statement statement) {
            IntermediateResult leftResult = left.evaluate(statement);
            IntermediateResult rightResult = right.evaluate(statement);

            Collection<Integer> preciseValues = new HashSet<>();
            for (Integer leftValue : leftResult.evaluatedValues()) {
                for (Integer rightValue : rightResult.evaluatedValues()) {
                    int result = applyOperator(leftValue, rightValue);

                    preciseValues.add(result);
                }
            }

            Collection<ImpreciseResult> impreciseConstants = new HashSet<>();
            impreciseConstants.addAll(leftResult.impreciseResults());
            impreciseConstants.addAll(rightResult.impreciseResults());

            return new IntermediateResult(preciseValues, impreciseConstants);
        }

        private int applyOperator(int leftValue, int rightValue) {
            switch (constraint.getOperator()) {
                case p -> {
                    return leftValue + rightValue;
                }
                case n -> {
                    return leftValue - rightValue;
                }
                case m -> {
                    return leftValue % rightValue;
                }
                default ->
                        throw new UnsupportedOperationException(
                                "Arithmetic operator "
                                        + constraint.getOperator()
                                        + " is not implemented");
            }
        }

        @Override
        public String toString() {
            switch (constraint.getOperator()) {
                case p -> {
                    return left + " + " + right;
                }
                case n -> {
                    return left + " - " + right;
                }
                case m -> {
                    return left + " % " + right;
                }
                default -> {
                    return left + " <unknown operator> " + right;
                }
            }
        }
    }

    private static class ArithmeticSingleton extends IArithmeticConstraint {

        private final CrySLObject object;

        private ArithmeticSingleton(
                CrySLObject object,
                Multimap<Statement, ParameterWithExtractedValues> statementToValues) {
            super(statementToValues);

            this.object = object;
        }

        public IntermediateResult evaluate(Statement statement) {
            Collection<ParameterWithExtractedValues> params = statementToValues.get(statement);
            Collection<ParameterWithExtractedValues> values = filterParameters(params, object);

            if (values.isEmpty()) {
                if (object.getVarName().matches("-?(0|[1-9]\\d*)")) {
                    int value = Integer.parseInt(object.getVarName());

                    return new IntermediateResult(Set.of(value), Collections.emptySet());
                } else if (object.getVarName().equals("true")) {
                    return new IntermediateResult(Set.of(1), Collections.emptySet());
                } else if (object.getVarName().equals("false")) {
                    return new IntermediateResult(Set.of(0), Collections.emptySet());

                    // TODO Deal with constants that are not numeric values
                    // ImpreciseResult.ImpreciseConstant constant = new
                    // ImpreciseResult.ImpreciseConstant(object);
                    // return new IntermediateResult(Collections.emptySet(), Set.of(constant));
                }
            }

            Collection<Integer> preciseValues = new HashSet<>();
            Collection<ImpreciseResult> impreciseResults = new HashSet<>();

            for (ParameterWithExtractedValues parameter : values) {
                for (ExtractedValue value : parameter.extractedValues()) {
                    if (value.val().isIntConstant()) {
                        preciseValues.add(value.val().getIntValue());
                    } else {
                        ImpreciseResult.ImpreciseExtractedValue result =
                                new ImpreciseResult.ImpreciseExtractedValue(parameter, value);
                        impreciseResults.add(result);
                    }
                }
            }

            return new IntermediateResult(preciseValues, impreciseResults);
        }

        private Collection<ParameterWithExtractedValues> filterParameters(
                Collection<ParameterWithExtractedValues> parameters, CrySLObject obj) {
            String varName = obj.getVarName();

            Collection<ParameterWithExtractedValues> result = new HashSet<>();
            for (ParameterWithExtractedValues parameter : parameters) {
                if (parameter.varName().equals(varName)) {
                    result.add(parameter);
                }
            }

            return result;
        }

        @Override
        public String toString() {
            return object.getVarName();
        }
    }

    private static class ArithmeticLength extends IArithmeticConstraint {

        private final CrySLPredicate predicate;

        public ArithmeticLength(
                CrySLPredicate predicate,
                Multimap<Statement, ParameterWithExtractedValues> statementToValues) {
            super(statementToValues);

            this.predicate = predicate;
        }

        @Override
        public IntermediateResult evaluate(Statement statement) {
            return new IntermediateResult(Collections.emptySet(), Collections.emptySet());
        }

        @Override
        public String toString() {
            return predicate.toString();
        }
    }

    private record IntermediateResult(
            Collection<Integer> evaluatedValues, Collection<ImpreciseResult> impreciseResults) {}

    private sealed interface ImpreciseResult {
        record ImpreciseExtractedValue(
                ParameterWithExtractedValues parameter, ExtractedValue impreciseValue)
                implements ImpreciseResult {}

        record ImpreciseConstant(CrySLObject object) implements ImpreciseResult {}
    }
}
