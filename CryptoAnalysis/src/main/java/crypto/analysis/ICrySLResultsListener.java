package crypto.analysis;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

import boomerang.accessgraph.AccessGraph;
import boomerang.util.StmtWithMethod;
import crypto.rules.CryptSLPredicate;
import crypto.rules.StateNode;
import crypto.typestate.CallSiteWithParamIndex;
import ideal.AnalysisSolver;
import ideal.IFactAtStatement;
import ideal.ResultReporter;
import soot.Unit;
import typestate.TypestateDomainValue;
import typestate.interfaces.ISLConstraint;

public interface ICrySLResultsListener extends ResultReporter<TypestateDomainValue<StateNode>> {

	void typestateErrorAt(AnalysisSeedWithSpecification classSpecification, StmtWithMethod stmt);
	
	void typestateErrorEndOfLifeCycle(AnalysisSeedWithSpecification classSpecification, StmtWithMethod stmt);
	
	void callToForbiddenMethod(ClassSpecification classSpecification, StmtWithMethod callSite);
	
	void ensuredPredicates(Table<Unit, AccessGraph, Set<EnsuredCryptSLPredicate>> existingPredicates, Table<Unit, IAnalysisSeed, Set<CryptSLPredicate>> expectedPredicates, Table<Unit, IAnalysisSeed, Set<CryptSLPredicate>> missingPredicates);

	void predicateContradiction(StmtWithMethod stmt, AccessGraph key, Entry<CryptSLPredicate, CryptSLPredicate> disPair);

	void missingPredicates(AnalysisSeedWithSpecification seed, Set<CryptSLPredicate> missingPredicates);

	void constraintViolation(AnalysisSeedWithSpecification analysisSeedWithSpecification, ISLConstraint con, StmtWithMethod unit);

	void checkedConstraints(AnalysisSeedWithSpecification analysisSeedWithSpecification, Collection<ISLConstraint> relConstraints);
	
	void onSeedTimeout(IFactAtStatement seed);
	
	void onSeedFinished(IFactAtStatement seed, AnalysisSolver<TypestateDomainValue<StateNode>> solver);
	
	void collectedValues(AnalysisSeedWithSpecification seed, Multimap<CallSiteWithParamIndex, Unit> collectedValues);

	void discoveredSeed(IAnalysisSeed curr);

}
