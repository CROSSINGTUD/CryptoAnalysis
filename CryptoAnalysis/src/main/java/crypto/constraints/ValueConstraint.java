package crypto.constraints;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import crypto.analysis.errors.ConstraintError;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CrySLObject;
import crypto.rules.CrySLSplitter;
import crypto.rules.CrySLValueConstraint;

public class ValueConstraint extends EvaluableConstraint {

	protected ValueConstraint(ISLConstraint origin, ConstraintSolver context) {
		super(origin, context);
	}

	@Override
	public void evaluate() {
		CrySLValueConstraint valCons = (CrySLValueConstraint) origin;

		CrySLObject var = valCons.getVar();
		final List<Entry<String, CallSiteWithExtractedValue>> vals = getValFromVar(var, valCons);
		if (vals.isEmpty()) {
			// TODO: Check whether this works as desired
			return;
		}
		for (Entry<String, CallSiteWithExtractedValue> val : vals) {
			List<String> values = valCons.getValueRange().parallelStream().map(e -> e.toLowerCase())
					.collect(Collectors.toList());
			if (!values.contains(val.getKey().toLowerCase())) {
				errors.add(new ConstraintError(val.getValue(), context.getClassSpec().getRule(), context.getObject(), valCons));
			}
		}
		return;
	}

	private List<Entry<String, CallSiteWithExtractedValue>> getValFromVar(CrySLObject var, ISLConstraint cons) {
		final String varName = var.getVarName();
		final Map<String, CallSiteWithExtractedValue> valueCollection = extractValueAsString(varName, cons);
		List<Entry<String, CallSiteWithExtractedValue>> vals = new ArrayList<>();
		if (valueCollection.isEmpty()) {
			return vals;
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
						vals.add(new AbstractMap.SimpleEntry<>(splits[ind], location));
					} else {
						vals.add(new AbstractMap.SimpleEntry<>("", location));
					}
				} else {
					vals.add(new AbstractMap.SimpleEntry<>(val.split(splitElement)[ind], location));
				}
			} else {
				vals.add(new AbstractMap.SimpleEntry<>(val, location));
			}
		}
		return vals;
	}

}
