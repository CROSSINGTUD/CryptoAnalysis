package crypto.constraints;

import crypto.analysis.errors.ConstraintError;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CrySLObject;
import crypto.rules.CrySLSplitter;
import crypto.rules.CrySLValueConstraint;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class ValueConstraint extends EvaluableConstraint {

	protected ValueConstraint(ISLConstraint origin, ConstraintSolver context) {
		super(origin, context);
	}

	@Override
	public void evaluate() {
		CrySLValueConstraint valCons = (CrySLValueConstraint) origin;
		List<Entry<String, CallSiteWithExtractedValue>> values = getValFromVar(valCons.getVar(), valCons);
		if (values.isEmpty()) {
			return;
		}

		List<String> lowerCaseValues = valCons.getValueRange().parallelStream().map(String::toLowerCase).collect(Collectors.toList());
		for (Entry<String, CallSiteWithExtractedValue> val : values) {
			if (!lowerCaseValues.contains(val.getKey().toLowerCase())) {
				errors.add(new ConstraintError(val.getValue(), context.getClassSpec().getRule(), context.getObject(), valCons));
			}
		}
    }

	private List<Entry<String, CallSiteWithExtractedValue>> getValFromVar(CrySLObject var, ISLConstraint cons) {
		final String varName = var.getVarName();
		final Map<String, CallSiteWithExtractedValue> valueCollection = extractValueAsString(varName);

		List<Entry<String, CallSiteWithExtractedValue>> values = new ArrayList<>();
		if (couldNotExtractValues(valueCollection, cons)) {
			return values;
		}

		for (Entry<String, CallSiteWithExtractedValue> e : valueCollection.entrySet()) {
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
					values.add(new AbstractMap.SimpleEntry<>(val.split(splitElement)[ind], location));
				}
			} else {
				values.add(new AbstractMap.SimpleEntry<>(val, location));
			}
		}
		return values;
	}

}
