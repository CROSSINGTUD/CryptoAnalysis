package crypto.analysis;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
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
import typestate.TypestateDomainValue;
import typestate.interfaces.ISLConstraint;

public interface CryptSLAnalysisListener extends ResultReporter<TypestateDomainValue<StateNode>> {

	void collectedValues(AnalysisSeedWithSpecification seed,
			Multimap<CallSiteWithParamIndex, Unit> collectedValues);

	void callToForbiddenMethod(ClassSpecification classSpecification, Unit callSite);

	void discoveredSeed(IAnalysisSeed curr);
	
	void violateConstraint(ClassSpecification spec, Unit callSite);

	void ensuredPredicates(Table<Unit, AccessGraph, Set<EnsuredCryptSLPredicate>> existingPredicates, Table<Unit, IAnalysisSeed, Set<CryptSLPredicate>> expectedPredicates, Table<Unit, IAnalysisSeed, Set<CryptSLPredicate>> missingPredicates);

	void seedFinished(IAnalysisSeed analysisSeedWithSpecification);

	void seedStarted(IAnalysisSeed analysisSeedWithSpecification);

	void boomerangQueryStarted(IFactAtStatement seed, AdditionalBoomerangQuery q);

	void boomerangQueryFinished(IFactAtStatement seed, AdditionalBoomerangQuery q);

	void predicateContradiction(Unit stmt, AccessGraph key,	Entry<CryptSLPredicate, CryptSLPredicate> disPair);

	void missingPredicates(AnalysisSeedWithSpecification seed, Set<CryptSLPredicate> missingPredicates);

	void constraintViolation(AnalysisSeedWithSpecification analysisSeedWithSpecification, ISLConstraint con);

	void checkedConstraints(AnalysisSeedWithSpecification analysisSeedWithSpecification,
			Collection<ISLConstraint> relConstraints);
}
