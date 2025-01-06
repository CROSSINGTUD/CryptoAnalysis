package crypto.constraints;

import boomerang.scene.Statement;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.errors.AbstractConstraintsError;
import crypto.extractparameter.ParameterWithExtractedValues;
import crysl.rule.CrySLComparisonConstraint;
import crysl.rule.CrySLConstraint;
import crysl.rule.CrySLPredicate;
import crysl.rule.CrySLValueConstraint;
import crysl.rule.ISLConstraint;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class EvaluableConstraint {

    public static final Collection<String> predefinedPredicates =
            Set.of("callTo", "noCallTo", "neverTypeOf", "length", "instanceOf", "notHardCoded");

    protected final AnalysisSeedWithSpecification seed;
    protected final Collection<Statement> statements;
    protected final Collection<ParameterWithExtractedValues> extractedValues;
    protected final Collection<AbstractConstraintsError> errors;

    public enum EvaluationResult {
        ConstraintIsSatisfied,
        ConstraintIsNotSatisfied,
        ConstraintIsNotRelevant
    }

    protected EvaluableConstraint(
            AnalysisSeedWithSpecification seed,
            Collection<Statement> statements,
            Collection<ParameterWithExtractedValues> extractedValues) {
        this.seed = seed;
        this.statements = statements;
        this.extractedValues = extractedValues;
        this.errors = new HashSet<>();
    }

    public static EvaluableConstraint getInstance(
            AnalysisSeedWithSpecification seed,
            ISLConstraint constraint,
            Collection<Statement> statements,
            Collection<ParameterWithExtractedValues> extractedValues) {
        Collection<ParameterWithExtractedValues> filteredStatements =
                filterExtractedValuesOnStatements(statements, extractedValues);

        if (constraint instanceof CrySLValueConstraint valueConstraint) {
            return new ValueConstraint(seed, valueConstraint, statements, filteredStatements);
        } else if (constraint instanceof CrySLComparisonConstraint comparisonConstraint) {
            return new ComparisonConstraint(
                    seed, comparisonConstraint, statements, filteredStatements);
        } else if (constraint instanceof CrySLPredicate predicateConstraint) {
            if (predefinedPredicates.contains(predicateConstraint.getPredName())) {
                return new PredefinedPredicateConstraint(
                        seed, statements, filteredStatements, predicateConstraint);
            } else {
                return new RequiredPredicateConstraint(
                        seed, statements, filteredStatements, predicateConstraint);
            }
        } else if (constraint instanceof CrySLConstraint cryslConstraint) {
            return new BinaryConstraint(seed, cryslConstraint, statements, filteredStatements);
        }

        throw new UnsupportedOperationException("Constraint type is not supported");
    }

    public abstract EvaluationResult evaluate();

    public abstract ISLConstraint getConstraint();

    public boolean isSatisfied() {
        return errors.isEmpty();
    }

    public boolean isViolated() {
        return !errors.isEmpty();
    }

    public Collection<AbstractConstraintsError> getErrors() {
        return errors;
    }

    private static Collection<ParameterWithExtractedValues> filterExtractedValuesOnStatements(
            Collection<Statement> statements,
            Collection<ParameterWithExtractedValues> extractedValues) {
        Collection<ParameterWithExtractedValues> result = new HashSet<>();

        for (ParameterWithExtractedValues parameter : extractedValues) {
            if (statements.contains(parameter.statement())) {
                result.add(parameter);
            }
        }

        return result;
    }

    protected Collection<ParameterWithExtractedValues> filterRelevantParameterResults(
            String varName, Collection<ParameterWithExtractedValues> extractedValues) {
        Collection<ParameterWithExtractedValues> result = new HashSet<>();

        for (ParameterWithExtractedValues parameter : extractedValues) {
            if (parameter.varName().equals(varName)) {
                result.add(parameter);
            }
        }

        return result;
    }
}
