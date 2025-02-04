package crypto.constraints.violations;

import crypto.extractparameter.ParameterWithExtractedValues;
import crypto.utils.CrySLUtils;

public record ViolatedInstanceOfConstraint(
        ParameterWithExtractedValues parameter, String notAllowedInstance)
        implements IViolatedConstraint {

    @Override
    public String getErrorMessage() {
        return CrySLUtils.getIndexAsString(parameter.index())
                + " @ "
                + parameter.statement()
                + " should not be an instance of class "
                + notAllowedInstance;
    }
}
