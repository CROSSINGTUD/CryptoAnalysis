package crypto.extractparameter.transformation;

import boomerang.ForwardQuery;
import boomerang.scene.AllocVal;
import boomerang.scene.Statement;
import boomerang.scene.Val;
import crypto.extractparameter.ExtractParameterDefinition;
import java.util.Optional;

public class OperatorTransformation extends Transformation {

    public OperatorTransformation(ExtractParameterDefinition definition) {
        super(definition);
    }

    @Override
    public Optional<AllocVal> evaluateExpression(Statement statement) {
        if (!statement.isAssign()) {
            return Optional.empty();
        }

        Val rightOp = statement.getRightOp();
        if (rightOp.isLengthExpr()) {
            return evaluateLengthExpr(statement, rightOp);
        }

        return Optional.empty();
    }

    private Optional<AllocVal> evaluateLengthExpr(Statement statement, Val lengthExpr) {
        Val lengthOp = lengthExpr.getLengthOp();

        Optional<ForwardQuery> allocSite = triggerBackwardQuery(statement, lengthOp);
        if (allocSite.isEmpty()) {
            return Optional.empty();
        }

        Val val = allocSite.get().var();
        if (!(val instanceof AllocVal)) {
            return Optional.empty();
        }

        Val allocVal = ((AllocVal) val).getAllocVal();

        if (allocVal.isArrayAllocationVal()) {
            Val arraySize = allocVal.getArrayAllocationSize();

            AllocVal arrayLengthVal =
                    new TransformedAllocVal(statement.getLeftOp(), statement, arraySize);
            return Optional.of(arrayLengthVal);
        } else if (allocVal.isStringConstant()) {
            int stringLength = allocVal.getStringValue().length();

            AllocVal stringLengthVal = createTransformedAllocVal(stringLength, statement);
            return Optional.of(stringLengthVal);
        }

        return Optional.empty();
    }
}
