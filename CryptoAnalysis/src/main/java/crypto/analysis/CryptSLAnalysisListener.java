package crypto.analysis;

import java.util.Set;

import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

import boomerang.accessgraph.AccessGraph;
import crypto.rules.StateNode;
import crypto.typestate.CallSiteWithParamIndex;
import ideal.ResultReporter;
import soot.Unit;
import typestate.TypestateDomainValue;

public interface CryptSLAnalysisListener extends ResultReporter<TypestateDomainValue<StateNode>> {

	void collectedValues(AnalysisSeedWithSpecification seed,
			Multimap<CallSiteWithParamIndex, Unit> collectedValues);

	void callToForbiddenMethod(ClassSpecification classSpecification, Unit callSite);

	void discoveredSeed(IAnalysisSeed curr);

	
	void violateConstraint(ClassSpecification spec, Unit callSite);

	void ensuredPredicates(Table<Unit, AccessGraph, Set<EnsuredCryptSLPredicate>> existingPredicates);
}
