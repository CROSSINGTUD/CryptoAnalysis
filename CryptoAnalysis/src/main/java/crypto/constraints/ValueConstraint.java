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
import boomerang.scope.Val;
import com.google.common.collect.Multimap;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.constraints.violations.IViolatedConstraint;
import crypto.constraints.violations.ViolatedValueConstraint;
import crypto.exceptions.CryptoAnalysisException;
import crypto.extractparameter.ParameterWithExtractedValues;
import crypto.extractparameter.TransformedValue;
import crypto.extractparameter.UnknownVal;
import crysl.rule.CrySLSplitter;
import crysl.rule.CrySLValueConstraint;
import crysl.rule.ISLConstraint;
import java.util.Collection;
import java.util.HashSet;

/** Value constraints correspond to constraints of the form 'varName in {C1, ..., Cn}' */
public class ValueConstraint extends EvaluableConstraint {

    private final CrySLValueConstraint constraint;

    public ValueConstraint(
            AnalysisSeedWithSpecification seed,
            CrySLValueConstraint constraint,
            Collection<Statement> statements,
            Multimap<Statement, ParameterWithExtractedValues> extractedValues) {
        super(seed, statements, extractedValues);

        this.constraint = constraint;
    }

    @Override
    public ISLConstraint getConstraint() {
        return constraint;
    }

    @Override
    public EvaluationResult evaluate() {
        // TODO Make this case sensitive?
        String varName = constraint.getVar().getVarName();
        Collection<ParameterWithExtractedValues> relevantParameters =
                filterRelevantParameterResults(varName, extractedValues.values());

        if (relevantParameters.isEmpty()) {
            return EvaluationResult.ConstraintIsNotRelevant;
        }

        Collection<String> allowedValues = constraint.getValueRange();
        allowedValues = formatAllowedValues(allowedValues);

        for (ParameterWithExtractedValues parameter : relevantParameters) {
            Collection<TransformedValue> violatingValues = new HashSet<>();

            for (TransformedValue value : parameter.extractedValues()) {
                Val val = value.getTransformedVal();
                // Boomerang returns the array values and the array allocation site. We are only
                // interested in the array values
                if (val.isArrayAllocationVal()) {
                    continue;
                }

                if (val.equals(UnknownVal.getInstance()) || (!val.isConstant() && !val.isNull())) {
                    ImpreciseValueExtractionError error =
                            new ImpreciseValueExtractionError(
                                    seed, parameter.statement(), seed.getSpecification(), this);
                    errors.add(error);
                } else {
                    // TODO Make this case sensitive?
                    String valAsString = convertConstantToString(val).toLowerCase();
                    valAsString =
                            checkForSplitterValue(valAsString, constraint.getVar().getSplitter());

                    if (!allowedValues.contains(valAsString)) {
                        violatingValues.add(value);
                    }
                }
            }

            if (!violatingValues.isEmpty()) {
                IViolatedConstraint violatedConstraint =
                        new ViolatedValueConstraint(constraint, violatingValues, parameter.index());
                ConstraintError error =
                        new ConstraintError(
                                seed,
                                parameter.statement(),
                                seed.getSpecification(),
                                this,
                                violatedConstraint);
                errors.add(error);
            }
        }

        if (errors.isEmpty()) {
            return EvaluationResult.ConstraintIsSatisfied;
        } else {
            return EvaluationResult.ConstraintIsNotSatisfied;
        }
    }

    private Collection<String> formatAllowedValues(Collection<String> originalValues) {
        Collection<String> formattedValues = new HashSet<>();

        for (String originalValue : originalValues) {
            if (originalValue.equalsIgnoreCase("true")) {
                formattedValues.add("1");
            } else if (originalValue.equalsIgnoreCase("false")) {
                formattedValues.add("0");
            } else {
                formattedValues.add(originalValue.toLowerCase());
            }
        }

        return formattedValues;
    }

    private String checkForSplitterValue(String valAsString, CrySLSplitter splitter) {
        if (splitter == null) {
            return valAsString;
        }

        String splitValue = splitter.getSplitter();
        String[] splits = valAsString.split(splitValue);

        if (splits.length <= splitter.getIndex()) {
            return "";
        }

        return splits[splitter.getIndex()];
    }

    private String convertConstantToString(Val val) {
        // Constants can be returned directly
        if (val.isConstant()) {
            if (val.isStringConstant()) {
                return val.getStringValue();
            } else if (val.isIntConstant()) {
                return String.valueOf(val.getIntValue());
            } else if (val.isLongConstant()) {
                return String.valueOf(val.getLongValue());
            } else if (val.isClassConstant()) {
                return val.getClassConstantType().toString();
            }
        } else if (val.isNull()) {
            return "null";
        }

        throw new CryptoAnalysisException("Val " + val.getVariableName() + " is not a constant");
    }

    @Override
    public String toString() {
        return constraint.getVar().getVarName()
                + " in {"
                + String.join(", ", constraint.getValueRange())
                + "}";
    }
}
