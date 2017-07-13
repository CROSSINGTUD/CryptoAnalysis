package crypto.analysis;

import java.util.Set;

import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

import boomerang.accessgraph.AccessGraph;
import crypto.rules.CryptSLPredicate;
import crypto.rules.StateNode;
import crypto.typestate.CallSiteWithParamIndex;
import crypto.typestate.CryptoTypestateAnaylsisProblem.AdditionalBoomerangQuery;
import ideal.IFactAtStatement;
import ideal.ResultReporter;
import soot.Unit;
import soot.Value;
import typestate.TypestateDomainValue;

public interface CryptSLAnalysisListener extends ResultReporter<TypestateDomainValue<StateNode>> {

	void collectedValues(AnalysisSeedWithSpecification seed,
			Multimap<CallSiteWithParamIndex, Value> collectedValues);

	void callToForbiddenMethod(ClassSpecification classSpecification, Unit callSite);

	void discoveredSeed(IAnalysisSeed curr);

	
	void violateConstraint(ClassSpecification spec, Unit callSite);

	void ensuredPredicates(Table<Unit, AccessGraph, Set<EnsuredCryptSLPredicate>> existingPredicates, Table<Unit, IAnalysisSeed, Set<CryptSLPredicate>> expectedPredicates, Table<Unit, IAnalysisSeed, Set<CryptSLPredicate>> missingPredicates);

	void seedFinished(IAnalysisSeed analysisSeedWithSpecification);

	void seedStarted(IAnalysisSeed analysisSeedWithSpecification);

	void boomerangQueryStarted(IFactAtStatement seed, AdditionalBoomerangQuery q);

	void boomerangQueryFinished(IFactAtStatement seed, AdditionalBoomerangQuery q);
}
