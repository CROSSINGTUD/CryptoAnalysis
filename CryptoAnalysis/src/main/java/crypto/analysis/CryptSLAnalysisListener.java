package crypto.analysis;

import com.google.common.collect.Multimap;

import crypto.rules.StateNode;
import crypto.typestate.CallSiteWithParamIndex;
import ideal.ResultReporter;
import soot.Unit;
import soot.Value;
import typestate.TypestateDomainValue;

public interface CryptSLAnalysisListener extends ResultReporter<TypestateDomainValue<StateNode>> {

	void collectedValues(AnalysisSeedWithSpecification seed,
			Multimap<CallSiteWithParamIndex, Value> collectedValues);

	void callToForbiddenMethod(ClassSpecification classSpecification, Unit callSite);

	void discoveredSeed(AnalysisSeedWithSpecification curr);

	
}
