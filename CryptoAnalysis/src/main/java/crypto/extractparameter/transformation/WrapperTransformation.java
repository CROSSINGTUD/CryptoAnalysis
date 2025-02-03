package crypto.extractparameter.transformation;

import boomerang.scene.AllocVal;
import boomerang.scene.InvokeExpr;
import boomerang.scene.Statement;
import boomerang.scene.Type;
import boomerang.scene.Val;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import crypto.definition.Definitions;
import crypto.extractparameter.scope.IntVal;
import crypto.extractparameter.scope.LongVal;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/** Class for transformations for Java's wrapper classes, e.g. Integer, BigInteger, Long, etc. */
public class WrapperTransformation extends Transformation {

    private static final Signature INTEGER_PARSE_INT =
            new Signature("java.lang.Integer", "int", "parseInt", List.of("java.lang.String"));
    private static final Signature BIG_INTEGER_VALUE_OF =
            new Signature(
                    "java.math.BigInteger", "java.math.BigInteger", "valueOf", List.of("long"));

    private static final Collection<Signature> WRAPPER_SIGNATURES =
            Set.of(INTEGER_PARSE_INT, BIG_INTEGER_VALUE_OF);

    protected static boolean isWrapperTransformation(Signature signature) {
        return WRAPPER_SIGNATURES.contains(signature);
    }

    protected WrapperTransformation(Definitions.BoomerangOptionsDefinition definition) {
        super(definition);
    }

    @Override
    protected Multimap<Val, Type> evaluateExpression(Statement statement, Signature signature) {
        if (signature.equals(INTEGER_PARSE_INT)) {
            return evaluateIntegerParseInt(statement);
        }

        if (signature.equals(BIG_INTEGER_VALUE_OF)) {
            return evaluateBigIntegerValueOf(statement);
        }

        return HashMultimap.create();
    }

    private Multimap<Val, Type> evaluateIntegerParseInt(Statement statement) {
        InvokeExpr invokeExpr = statement.getInvokeExpr();
        Val param = invokeExpr.getArg(0);

        Multimap<AllocVal, Type> allocSites = computeAllocSites(statement, param);
        Multimap<Val, Type> extractedParams = extractAllocValues(allocSites);

        Multimap<Val, Type> result = HashMultimap.create();
        for (Val extractedParam : extractedParams.keySet()) {
            if (!extractedParam.isStringConstant()) {
                continue;
            }

            try {
                int parsedInt = Integer.parseInt(extractedParam.getStringValue());
                IntVal intVal = new IntVal(parsedInt, statement.getMethod());

                Collection<Type> types = extractedParams.get(extractedParam);
                types.add(intVal.getType());

                result.putAll(intVal, types);
            } catch (NumberFormatException ignored) {
            }
        }

        return result;
    }

    private Multimap<Val, Type> evaluateBigIntegerValueOf(Statement statement) {
        InvokeExpr invokeExpr = statement.getInvokeExpr();
        Val param = invokeExpr.getArg(0);

        Multimap<AllocVal, Type> allocSites = computeAllocSites(statement, param);
        Multimap<Val, Type> extractedParams = extractAllocValues(allocSites);

        Multimap<Val, Type> result = HashMultimap.create();
        for (Val extractedParam : extractedParams.keySet()) {
            if (!extractedParam.isLongConstant()) {
                continue;
            }

            // Instead of a BigInteger, we continue propagating the long value
            LongVal longVal = new LongVal(extractedParam.getLongValue(), statement.getMethod());
            Collection<Type> types = extractedParams.get(extractedParam);
            types.add(longVal.getType());

            result.putAll(longVal, types);
        }

        return result;
    }
}
