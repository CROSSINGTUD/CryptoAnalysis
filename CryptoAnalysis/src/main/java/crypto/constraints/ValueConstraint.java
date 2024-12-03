package crypto.constraints;

import crypto.analysis.errors.ConstraintError;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crysl.rule.CrySLObject;
import crysl.rule.CrySLSplitter;
import crysl.rule.CrySLValueConstraint;
import crysl.rule.ISLConstraint;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ValueConstraint extends EvaluableConstraint {

    protected ValueConstraint(ISLConstraint origin, ConstraintSolver context) {
        super(origin, context);
    }

    @Override
    public void evaluate() {
        CrySLValueConstraint valCons = (CrySLValueConstraint) origin;
        List<Map.Entry<String, CallSiteWithExtractedValue>> values =
                getValFromVar(valCons.getVar(), valCons);
        if (values.isEmpty()) {
            return;
        }

        List<String> lowerCaseValues =
                valCons.getValueRange().parallelStream()
                        .map(String::toLowerCase)
                        .collect(Collectors.toList());
        for (Map.Entry<String, CallSiteWithExtractedValue> val : values) {
            if (!lowerCaseValues.contains(val.getKey().toLowerCase())) {
                ConstraintError error =
                        new ConstraintError(
                                context.getSeed(),
                                val.getValue(),
                                context.getSpecification(),
                                valCons);
                errors.add(error);
            }
        }
    }

    private List<Map.Entry<String, CallSiteWithExtractedValue>> getValFromVar(
            CrySLObject var, ISLConstraint cons) {
        final String varName = var.getVarName();
        final Map<String, CallSiteWithExtractedValue> valueCollection =
                extractValueAsString(varName);

        List<Map.Entry<String, CallSiteWithExtractedValue>> values = new ArrayList<>();
        if (couldNotExtractValues(valueCollection, cons)) {
            return values;
        }

        for (Map.Entry<String, CallSiteWithExtractedValue> e : valueCollection.entrySet()) {
            CrySLSplitter splitter = var.getSplitter();
            final CallSiteWithExtractedValue location = e.getValue();
            String val = e.getKey();
            if (splitter != null) {
                int ind = splitter.getIndex();
                String splitElement = splitter.getSplitter();
                if (ind > 0) {
                    String[] splits = val.split(splitElement);
                    if (splits.length > ind) {
                        values.add(new AbstractMap.SimpleEntry<>(splits[ind], location));
                    } else {
                        values.add(new AbstractMap.SimpleEntry<>("", location));
                    }
                } else {
                    values.add(
                            new AbstractMap.SimpleEntry<>(val.split(splitElement)[ind], location));
                }
            } else {
                values.add(new AbstractMap.SimpleEntry<>(val, location));
            }
        }
        return values;
    }
}
