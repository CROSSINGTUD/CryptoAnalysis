package crypto.constraints;

import boomerang.scene.Statement;
import boomerang.scene.Val;
import com.google.common.collect.Multimap;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.constraints.violations.IViolatedConstraint;
import crypto.constraints.violations.ViolatedValueConstraint;
import crypto.exceptions.CryptoAnalysisException;
import crypto.extractparameter.ExtractedValue;
import crypto.extractparameter.ParameterWithExtractedValues;
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
            Collection<ExtractedValue> violatingValues = new HashSet<>();

            for (ExtractedValue extractedValue : parameter.extractedValues()) {
                // TODO Extract call sites that are not part of the dataflow scope
                if (extractedValue.val().equals(Val.zero())
                        || (!extractedValue.val().isConstant() && !extractedValue.val().isNull())) {
                    ImpreciseValueExtractionError error =
                            new ImpreciseValueExtractionError(
                                    seed, parameter.statement(), seed.getSpecification(), this);
                    errors.add(error);
                } else {
                    // TODO Make this case sensitive?
                    String valAsString =
                            convertConstantToString(extractedValue.val()).toLowerCase();
                    valAsString =
                            checkForSplitterValue(valAsString, constraint.getVar().getSplitter());

                    if (!allowedValues.contains(valAsString)) {
                        violatingValues.add(extractedValue);
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
