package crypto.constraints.violations;

import crypto.extractparameter.ExtractedValue;
import crypto.extractparameter.ParameterWithExtractedValues;
import crypto.utils.CrySLUtils;

/**
 * Represents a violation of the predefined predicate 'notHardCoded[$variable]'
 *
 * @param parameter the parameter with its extracted values
 * @param extractedValue the concrete hard coded value
 */
public record ViolatedNotHardCodedConstraint(
        ParameterWithExtractedValues parameter, ExtractedValue extractedValue)
        implements IViolatedConstraint {

    @Override
    public String getErrorMessage() {
        // TODO Improve the error message
        return CrySLUtils.getIndexAsString(parameter.index())
                + " @ "
                + parameter.statement()
                + " should never be hard coded";
    }
}
