package crypto.constraints.violations;

import crypto.constraints.ValueConstraint;
import crypto.extractparameter.ExtractedValue;
import crypto.utils.CrySLUtils;
import crysl.rule.CrySLValueConstraint;
import java.util.Collection;

/**
 * Represents the violation of a {@link ValueConstraint}
 *
 * @param constraint the violated constraint
 */
public record ViolatedValueConstraint(
        CrySLValueConstraint constraint, Collection<ExtractedValue> violatingValues, int index)
        implements IViolatedConstraint {

    @Override
    public String getErrorMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append(CrySLUtils.getIndexAsString(index));

        if (violatingValues.size() > 1) {
            builder.append(" (with values ");

            String values =
                    String.join(
                            ", ",
                            violatingValues.stream()
                                    .map(value -> value.val().getVariableName())
                                    .toList());
            builder.append(values);
        } else {
            builder.append(" (with value ");

            String value = violatingValues.iterator().next().val().getVariableName();
            builder.append(value);
        }

        builder.append(")");
        builder.append(" should be any of ");
        builder.append("{");

        String expectedValues = getExpectedValuesAsString();
        builder.append(expectedValues);

        builder.append("}");

        return builder.toString();
    }

    private String getExpectedValuesAsString() {
        return String.join(", ", constraint.getValueRange());
    }
}
