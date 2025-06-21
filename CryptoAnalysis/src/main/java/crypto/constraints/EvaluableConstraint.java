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
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.errors.AbstractConstraintsError;
import crypto.constraints.violations.ImpreciseValueConstraint;
import crypto.constraints.violations.SatisfiedConstraint;
import crypto.constraints.violations.ViolatedConstraint;
import crypto.extractparameter.ParameterWithExtractedValues;
import crysl.rule.CrySLComparisonConstraint;
import crysl.rule.CrySLConstraint;
import crysl.rule.CrySLPredicate;
import crysl.rule.CrySLValueConstraint;
import crysl.rule.ISLConstraint;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Supper class that construct and evaluate a CrySL constraint from the CONSTRAINTS or REQUIRES
 * section. A call to {@link EvaluableConstraint#getInstance(AnalysisSeedWithSpecification,
 * ISLConstraint, Collection, Multimap)}} creates an instance for a corresponding CrySL constraint
 * and a call to {@link EvaluableConstraint#evaluate()} evaluates it and returns the result. With
 * {@link EvaluableConstraint#getErrors()}, one can retrieve the collected violations/errors.
 */
public abstract class EvaluableConstraint {

    public static final Collection<String> predefinedPredicates =
            Set.of("callTo", "noCallTo", "neverTypeOf", "length", "instanceOf", "notHardCoded");

    protected final AnalysisSeedWithSpecification seed;
    protected final Collection<Statement> statements;
    protected final Multimap<Statement, ParameterWithExtractedValues> extractedValues;
    protected final Collection<AbstractConstraintsError> errors;

    protected final Collection<SatisfiedConstraint> satisfiedConstraints;
    protected final Collection<ViolatedConstraint> violatedConstraints;
    protected final Collection<ImpreciseValueConstraint> impreciseConstraints;

    public enum EvaluationResult {
        /** Returned if the constraint is satisfied with the given information */
        ConstraintIsSatisfied,

        /** Returned if the constraint is violated with the given information */
        ConstraintIsNotSatisfied,

        /**
         * Returned if the constraint contains variables that are not part of the given statements.
         * In this case, the constraint cannot be evaluated and is declared as 'not relevant'.
         */
        ConstraintIsNotRelevant
    }

    protected EvaluableConstraint(
            AnalysisSeedWithSpecification seed,
            Collection<Statement> statements,
            Multimap<Statement, ParameterWithExtractedValues> extractedValues) {
        this.seed = seed;
        this.statements = statements;
        this.extractedValues = extractedValues;
        this.errors = new HashSet<>();

        this.satisfiedConstraints = new HashSet<>();
        this.violatedConstraints = new HashSet<>();
        this.impreciseConstraints = new HashSet<>();
    }

    public static EvaluableConstraint getInstance(
            AnalysisSeedWithSpecification seed,
            ISLConstraint constraint,
            Collection<Statement> statements,
            Multimap<Statement, ParameterWithExtractedValues> extractedValues) {
        Multimap<Statement, ParameterWithExtractedValues> filteredParameters =
                filterExtractedValuesOnStatements(statements, extractedValues);

        if (constraint instanceof CrySLValueConstraint valueConstraint) {
            return new ValueConstraint(seed, valueConstraint, statements, filteredParameters);
        } else if (constraint instanceof CrySLComparisonConstraint comparisonConstraint) {
            return new ComparisonConstraint(
                    seed, comparisonConstraint, statements, filteredParameters);
        } else if (constraint instanceof CrySLPredicate predicateConstraint) {
            return new PredefinedPredicateConstraint(
                    seed, statements, filteredParameters, predicateConstraint);
        } else if (constraint instanceof CrySLConstraint cryslConstraint) {
            return new BinaryConstraint(seed, cryslConstraint, statements, filteredParameters);
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

    public Multimap<Statement, ParameterWithExtractedValues> getExtractedValues() {
        return extractedValues;
    }

    public Collection<AbstractConstraintsError> getErrors() {
        return errors;
    }

    public Collection<SatisfiedConstraint> getSatisfiedConstraints() {
        return satisfiedConstraints;
    }

    public Collection<ViolatedConstraint> getViolatedConstraints() {
        return violatedConstraints;
    }

    public Collection<ImpreciseValueConstraint> getImpreciseConstraints() {
        return impreciseConstraints;
    }

    private static Multimap<Statement, ParameterWithExtractedValues>
            filterExtractedValuesOnStatements(
                    Collection<Statement> statements,
                    Multimap<Statement, ParameterWithExtractedValues> extractedValues) {
        Multimap<Statement, ParameterWithExtractedValues> result = HashMultimap.create();

        for (Statement statement : statements) {
            Collection<ParameterWithExtractedValues> params = extractedValues.get(statement);

            result.putAll(statement, params);
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
