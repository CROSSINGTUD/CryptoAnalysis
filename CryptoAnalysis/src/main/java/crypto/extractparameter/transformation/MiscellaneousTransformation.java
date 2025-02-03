package crypto.extractparameter.transformation;

import boomerang.scene.AllocVal;
import boomerang.scene.InvokeExpr;
import boomerang.scene.Statement;
import boomerang.scene.Type;
import boomerang.scene.Val;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import crypto.definition.Definitions;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/** Class for transformations that do not belong to a specific type of transformation. */
public class MiscellaneousTransformation extends Transformation {

    private static final Signature HEX_DECODE =
            new Signature(
                    "org.bouncycastle.util.encoders.Hex",
                    "byte[]",
                    "decode",
                    List.of("java.lang.String"));

    private static final Collection<Signature> MISCELLANEOUS_SIGNATURES = Set.of(HEX_DECODE);

    protected static boolean isMiscellaneousTransformation(Signature signature) {
        return MISCELLANEOUS_SIGNATURES.contains(signature);
    }

    protected MiscellaneousTransformation(Definitions.BoomerangOptionsDefinition definition) {
        super(definition);
    }

    @Override
    protected Multimap<Val, Type> evaluateExpression(Statement statement, Signature signature) {
        if (signature.equals(HEX_DECODE)) {
            return evaluateHexDecode(statement);
        }

        return HashMultimap.create();
    }

    private Multimap<Val, Type> evaluateHexDecode(Statement statement) {
        InvokeExpr invokeExpr = statement.getInvokeExpr();
        Val param = invokeExpr.getArg(0);

        Multimap<AllocVal, Type> allocSites = computeAllocSites(statement, param);
        Multimap<Val, Type> extractedParams = extractAllocValues(allocSites);

        Multimap<Val, Type> result = HashMultimap.create();
        for (Val extractedParam : extractedParams.keySet()) {
            if (!extractedParam.isStringConstant()) {
                continue;
            }

            Collection<Type> types = extractedParams.get(extractedParam);
            types.add(extractedParam.getType());

            result.putAll(extractedParam, types);
        }

        return result;
    }
}
