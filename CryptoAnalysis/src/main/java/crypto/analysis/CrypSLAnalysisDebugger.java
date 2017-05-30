package crypto.analysis;

import com.google.common.collect.Multimap;

import crypto.typestate.CallSiteWithParamIndex;
import soot.Value;

public interface CrypSLAnalysisDebugger {

	public void collectedValues(ClassSpecification classSpecification,
			Multimap<CallSiteWithParamIndex, Value> collectedValues);

}
