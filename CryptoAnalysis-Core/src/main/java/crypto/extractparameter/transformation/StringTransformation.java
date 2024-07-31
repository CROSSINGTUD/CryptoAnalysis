package crypto.extractparameter.transformation;

import boomerang.scene.AllocVal;
import boomerang.scene.InvokeExpr;
import boomerang.scene.Statement;
import boomerang.scene.Val;
import crypto.definition.ExtractParameterDefinition;

import java.util.Optional;

public class StringTransformation extends Transformation {

    private static final String REPLACE_CHAR_SEQUENCE_CHAR_SEQUENCE = "<java.lang.String: java.lang.String replace(java.lang.CharSequence,java.lang.CharSequence)>";
    private static final String TO_CHAR_ARRAY = "<java.lang.String: char[] toCharArray()>";
    private static final String GET_BYTES = "<java.lang.String: byte[] getBytes()>";

    public StringTransformation(ExtractParameterDefinition definition) {
        super(definition);
    }

    @Override
    public Optional<AllocVal> evaluateExpression(Statement statement) {
        if (!statement.containsInvokeExpr() || !statement.isAssign()) {
            return Optional.empty();
        }

        InvokeExpr invokeExpr = statement.getInvokeExpr();
        String signature = invokeExpr.getMethod().getSignature();

        if (signature.equals(REPLACE_CHAR_SEQUENCE_CHAR_SEQUENCE)) {
            return evaluateReplaceCharSequenceCharSequence(statement, invokeExpr);
        }

        if (signature.equals(TO_CHAR_ARRAY)) {
            return evaluateToCharArray(statement, invokeExpr);
        }

        if (signature.equals(GET_BYTES)) {
            return evaluateGetBytes(statement, invokeExpr);
        }

        return Optional.empty();
    }

    private Optional<AllocVal> evaluateReplaceCharSequenceCharSequence(Statement statement, InvokeExpr invokeExpr) {
        Val base = invokeExpr.getBase();
        Val arg1 = invokeExpr.getArg(0);
        Val arg2 = invokeExpr.getArg(1);

        Optional<String> baseStringOpt = extractStringFromVal(statement, base);
        Optional<String> arg1StringOpt = extractStringFromVal(statement, arg1);
        Optional<String> arg2StringOpt = extractStringFromVal(statement, arg2);

        if (baseStringOpt.isEmpty() || arg1StringOpt.isEmpty() || arg2StringOpt.isEmpty()) {
            return Optional.empty();
        }

        String baseString = baseStringOpt.get();
        String arg1String = arg1StringOpt.get();
        String arg2String = arg2StringOpt.get();

        String result = baseString.replace(arg1String, arg2String);

        AllocVal allocVal = createTransformedAllocVal(result, statement);
        return Optional.of(allocVal);
    }

    private Optional<AllocVal> evaluateToCharArray(Statement statement, InvokeExpr invokeExpr) {
        Val base = invokeExpr.getBase();

        Optional<String> baseStringOpt = extractStringFromVal(statement, base);

        if (baseStringOpt.isEmpty()) {
            return Optional.empty();
        }

        String baseString = baseStringOpt.get();

        AllocVal allocVal = createTransformedAllocVal(baseString, statement);
        return Optional.of(allocVal);
    }

    private Optional<AllocVal> evaluateGetBytes(Statement statement, InvokeExpr invokeExpr) {
        Val base = invokeExpr.getBase();

        Optional<String> baseStringOpt = extractStringFromVal(statement, base);

        if (baseStringOpt.isEmpty()) {
            return Optional.empty();
        }

        String baseString = baseStringOpt.get();

        AllocVal allocVal = createTransformedAllocVal(baseString, statement);
        return Optional.of(allocVal);
    }

}
