package crypto.extractparameter.transformation;

import boomerang.options.BoomerangOptions;
import boomerang.scope.AllocVal;
import boomerang.scope.FrameworkScope;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Statement;
import boomerang.scope.Type;
import boomerang.scope.Val;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import crypto.extractparameter.scope.IntVal;
import crypto.extractparameter.scope.LongVal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Class for transformations for Java's wrapper classes, e.g. Integer, BigInteger, Long, etc. */
public class WrapperTransformation extends Transformation {

    private static final Signature INTEGER_PARSE_INT =
            new Signature("java.lang.Integer", "int", "parseInt", List.of("java.lang.String"));
    private static final Signature BIG_INTEGER_CONSTRUCTOR_STRING =
            new Signature("java.math.BigInteger", "void", "<init>", List.of("java.lang.String"));
    private static final Signature BIG_INTEGER_CONSTRUCTOR_STRING_INT =
            new Signature(
                    "java.math.BigInteger", "void", "<init>", List.of("java.lang.String", "int"));
    private static final Signature BIG_INTEGER_VALUE_OF =
            new Signature(
                    "java.math.BigInteger", "java.math.BigInteger", "valueOf", List.of("long"));

    private static final Collection<Signature> WRAPPER_SIGNATURES =
            Set.of(
                    INTEGER_PARSE_INT,
                    BIG_INTEGER_VALUE_OF,
                    BIG_INTEGER_CONSTRUCTOR_STRING,
                    BIG_INTEGER_CONSTRUCTOR_STRING_INT);

    protected static boolean isWrapperTransformation(Signature signature) {
        return WRAPPER_SIGNATURES.contains(signature);
    }

    protected WrapperTransformation(FrameworkScope frameworkScope, BoomerangOptions options) {
        super(frameworkScope, options);
    }

    @Override
    protected Multimap<Val, Type> evaluateExpression(Statement statement, Signature signature) {
        if (signature.equals(INTEGER_PARSE_INT)) {
            return evaluateIntegerParseInt(statement);
        }

        if (signature.equals(BIG_INTEGER_CONSTRUCTOR_STRING)) {
            return evaluateBigIntegerConstructorString(statement);
        }

        if (signature.equals(BIG_INTEGER_CONSTRUCTOR_STRING_INT)) {
            return evaluateBigIntegerConstructorStringInt(statement);
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

    private Multimap<Val, Type> evaluateBigIntegerConstructorString(Statement statement) {
        InvokeExpr invokeExpr = statement.getInvokeExpr();
        Val param = invokeExpr.getArg(0);

        Multimap<AllocVal, Type> allocSites = computeAllocSites(statement, param);
        Multimap<Val, Type> extractedParams = extractAllocValues(allocSites);

        Multimap<Val, Type> result = HashMultimap.create();
        for (Val extractedParam : extractedParams.keySet()) {
            if (!extractedParam.isStringConstant()) {
                continue;
            }

            BigInteger bigInteger = new BigInteger(extractedParam.getStringValue());

            // Using Integer.MAX_VALUE is sufficient to model large integers
            int intValue;
            try {
                intValue = bigInteger.intValueExact();
            } catch (ArithmeticException e) {
                intValue = Integer.MAX_VALUE;
            }

            IntVal intVal = new IntVal(intValue, statement.getMethod());
            Collection<Type> types = new HashSet<>();
            types.add(intVal.getType());

            result.putAll(intVal, types);
        }

        return result;
    }

    private Multimap<Val, Type> evaluateBigIntegerConstructorStringInt(Statement statement) {
        InvokeExpr invokeExpr = statement.getInvokeExpr();
        Val param1 = invokeExpr.getArg(0);
        Val param2 = invokeExpr.getArg(1);

        Multimap<AllocVal, Type> param1Sites = computeAllocSites(statement, param1);
        Multimap<AllocVal, Type> param2Sites = computeAllocSites(statement, param2);

        Multimap<Val, Type> extractedParams1 = extractAllocValues(param1Sites);
        Multimap<Val, Type> extractedParams2 = extractAllocValues(param2Sites);

        Multimap<Val, Type> result = HashMultimap.create();
        for (Val extractedParam1 : extractedParams1.keySet()) {
            for (Val extractedParam2 : extractedParams2.keySet()) {
                if (!extractedParam1.isStringConstant() || !extractedParam2.isIntConstant()) {
                    continue;
                }

                BigInteger bigInteger =
                        new BigInteger(
                                extractedParam1.getStringValue(), extractedParam2.getIntValue());

                // Using Integer.MAX_VALUE is sufficient to model large integers
                int intValue;
                try {
                    intValue = bigInteger.intValueExact();
                } catch (ArithmeticException e) {
                    intValue = Integer.MAX_VALUE;
                }

                IntVal intVal = new IntVal(intValue, statement.getMethod());
                Collection<Type> types = new HashSet<>();
                types.add(intVal.getType());

                result.putAll(intVal, types);
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
