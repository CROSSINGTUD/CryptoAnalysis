package crypto.extractparameter.transformation;

import boomerang.scene.AllocVal;
import boomerang.scene.InvokeExpr;
import boomerang.scene.Statement;
import boomerang.scene.Val;
import crypto.definition.ExtractParameterDefinition;

import java.util.Optional;

public class WrapperTransformation extends Transformation {

    private static final String INTEGER_PARSE_INT = "<java.lang.Integer: int parseInt(java.lang.String)>";

    public WrapperTransformation(ExtractParameterDefinition definition) {
        super(definition);
    }

    @Override
    public Optional<AllocVal> evaluateExpression(Statement statement) {
        if (!statement.containsInvokeExpr() || !statement.isAssign()) {
            return Optional.empty();
        }

        InvokeExpr invokeExpr = statement.getInvokeExpr();
        String signature = invokeExpr.getMethod().getSignature();

        if (signature.equals(INTEGER_PARSE_INT)) {
            return evaluateIntegerParseInt(statement, invokeExpr);
        }

        return Optional.empty();
    }

    private Optional<AllocVal> evaluateIntegerParseInt(Statement statement, InvokeExpr invokeExpr) {
        Val param = invokeExpr.getArg(0);

        Optional<String> stringParamOpt = extractStringFromVal(statement, param);

        if (stringParamOpt.isEmpty()) {
            return Optional.empty();
        }

        String paramString = stringParamOpt.get();

        try {
            int result = Integer.parseInt(paramString);

            AllocVal allocVal = createTransformedAllocVal(result, statement);
            return Optional.of(allocVal);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

}
