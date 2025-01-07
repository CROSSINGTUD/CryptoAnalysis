package crypto.constraints;

import boomerang.scene.Val;
import crypto.extractparameter.ExtractedValue;
import crysl.rule.CrySLMethod;
import java.util.Collection;

public sealed interface IViolatedConstraint {

    record ViolatedBinaryConstraint(BinaryConstraint constraint) implements IViolatedConstraint {}

    record ViolatedComparisonConstraint(ComparisonConstraint constraint)
            implements IViolatedConstraint {}

    record ViolatedValueConstraint(ValueConstraint constraint) implements IViolatedConstraint {}

    record ViolatedCallToConstraint(Collection<CrySLMethod> requiredMethods)
            implements IViolatedConstraint {}

    record ViolatedNoCallToConstraint(CrySLMethod method) implements IViolatedConstraint {}

    record ViolatedNeverTypeOfConstraint(Val val, int index, String notAllowedType)
            implements IViolatedConstraint {}

    record ViolatedInstanceOfConstraint(Val val, int index, String notAllowedInstance)
            implements IViolatedConstraint {}

    record ViolatedNotHardCodedConstraint(Val val, int index, ExtractedValue extractedValue)
            implements IViolatedConstraint {}
}
