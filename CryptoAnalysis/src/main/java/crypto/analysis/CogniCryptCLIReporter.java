package crypto.analysis;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

import boomerang.accessgraph.AccessGraph;
import boomerang.util.StmtWithMethod;
import crypto.rules.CryptSLMethod;
import crypto.rules.CryptSLPredicate;
import crypto.rules.StateNode;
import crypto.typestate.CallSiteWithParamIndex;
import crypto.typestate.CryptoTypestateAnaylsisProblem.AdditionalBoomerangQuery;
import ideal.AnalysisSolver;
import ideal.IFactAtStatement;
import soot.SootMethod;
import soot.Unit;
import typestate.TypestateDomainValue;
import typestate.interfaces.ISLConstraint;

public class CogniCryptCLIReporter extends CrySLAnalysisListener {

	@Override
	public void collectedValues(AnalysisSeedWithSpecification seed, Multimap<CallSiteWithParamIndex, Unit> collectedValues) {}

	@Override
	public void callToForbiddenMethod(ClassSpecification classSpecification, StmtWithMethod callSite, List<CryptSLMethod> alternatives) {}

	@Override
	public void discoveredSeed(IAnalysisSeed curr) {}

	@Override
	public void ensuredPredicates(Table<Unit, AccessGraph, Set<EnsuredCryptSLPredicate>> existingPredicates, Table<Unit, IAnalysisSeed, Set<CryptSLPredicate>> expectedPredicates, Table<Unit, IAnalysisSeed, Set<CryptSLPredicate>> missingPredicates) {}

	@Override
	public void seedFinished(IAnalysisSeed seed) {

	}

	@Override
	public void seedStarted(IAnalysisSeed seed) {

	}

	@Override
	public void boomerangQueryStarted(IFactAtStatement seed, AdditionalBoomerangQuery q) {}

	@Override
	public void boomerangQueryFinished(IFactAtStatement seed, AdditionalBoomerangQuery q) {}

	@Override
	public void predicateContradiction(StmtWithMethod stmt, AccessGraph accessGraph, Entry<CryptSLPredicate, CryptSLPredicate> disPair) {}

	@Override
	public void missingPredicates(AnalysisSeedWithSpecification seed, Set<CryptSLPredicate> missingPredicates) {}

	@Override
	public void constraintViolation(AnalysisSeedWithSpecification analysisSeedWithSpecification, ISLConstraint con, StmtWithMethod unit) {}

	@Override
	public void checkedConstraints(AnalysisSeedWithSpecification seed, Collection<ISLConstraint> cons) {}

	@Override
	public void beforeAnalysis() {}

	@Override
	public void afterAnalysis() {}

	@Override
	public void beforeConstraintCheck(AnalysisSeedWithSpecification seed) {}

	@Override
	public void afterConstraintCheck(AnalysisSeedWithSpecification seed) {}

	@Override
	public void beforePredicateCheck(AnalysisSeedWithSpecification seed) {}

	@Override
	public void afterPredicateCheck(AnalysisSeedWithSpecification seed) {}

	@Override
	public void onSeedFinished(IFactAtStatement seed, AnalysisSolver<TypestateDomainValue<StateNode>> solver) {

	}

	@Override
	public void onSeedTimeout(IFactAtStatement seed) {

	}


	@Override
	public void typestateErrorAt(AnalysisSeedWithSpecification classSpecification, StmtWithMethod stmt,
			Collection<SootMethod> expectedCalls) {
	}

	@Override
	public void typestateErrorEndOfLifeCycle(AnalysisSeedWithSpecification classSpecification, StmtWithMethod stmt) {
		
	}

}
