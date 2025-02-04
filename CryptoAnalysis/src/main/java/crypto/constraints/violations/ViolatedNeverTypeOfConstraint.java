package crypto.constraints.violations;

import crypto.extractparameter.ParameterWithExtractedValues;
import crypto.utils.CrySLUtils;

/**
 * Represents a violation of the predefined predicate 'neverTypeOf[$variable, $type]'
 *
 * @param parameter the parameter with its extracted values
 * @param notAllowedType the type that is not allowed
 */
public record ViolatedNeverTypeOfConstraint(
        ParameterWithExtractedValues parameter, String notAllowedType)
        implements IViolatedConstraint {

    @Override
    public String getErrorMessage() {
        return CrySLUtils.getIndexAsString(parameter.index())
                + " @ "
                + parameter.statement()
                + " should never be of type "
                + notAllowedType;
    }
}
