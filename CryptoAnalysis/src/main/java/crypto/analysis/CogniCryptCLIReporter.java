package crypto.analysis;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

import boomerang.BackwardQuery;
import boomerang.Query;
import boomerang.WeightedBoomerang;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.rules.CryptSLMethod;
import crypto.rules.CryptSLPredicate;
import crypto.typestate.CallSiteWithParamIndex;
import crypto.typestate.ExtendedIDEALAnaylsis.AdditionalBoomerangQuery;
import soot.SootMethod;
import soot.Unit;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;
import typestate.interfaces.ISLConstraint;

public class CogniCryptCLIReporter extends CrySLAnalysisListener {

	@Override
	public void collectedValues(AnalysisSeedWithSpecification seed, Multimap<CallSiteWithParamIndex, Statement> collectedValues) {}

	@Override
	public void callToForbiddenMethod(ClassSpecification classSpecification, Statement callSite, List<CryptSLMethod> alternatives) {}

	@Override
	public void discoveredSeed(IAnalysisSeed curr) {}

	@Override
	public void ensuredPredicates(Table<Statement, Val, Set<EnsuredCryptSLPredicate>> existingPredicates, Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> expectedPredicates, Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> missingPredicates) {}

	@Override
	public void seedStarted(IAnalysisSeed seed) {

	}

	@Override
	public void boomerangQueryStarted(Query seed, BackwardQuery q) {}

	@Override
	public void boomerangQueryFinished(Query seed, BackwardQuery q) {}

	@Override
	public void predicateContradiction(Node<Statement,Val> node, Entry<CryptSLPredicate, CryptSLPredicate> disPair) {}

	@Override
	public void missingPredicates(AnalysisSeedWithSpecification seed, Set<CryptSLPredicate> missingPredicates) {}

	@Override
	public void constraintViolation(AnalysisSeedWithSpecification analysisSeedWithSpecification, ISLConstraint con, Statement unit) {}

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
	public void onSeedFinished(IAnalysisSeed seed, WeightedBoomerang<TransitionFunction> solver) {

	}

	@Override
	public void onSeedTimeout(Node<Statement,Val> seed) {

	}


	@Override
	public void typestateErrorAt(AnalysisSeedWithSpecification classSpecification, Statement stmt,
			Collection<SootMethod> expectedCalls) {
	}

	@Override
	public void typestateErrorEndOfLifeCycle(AnalysisSeedWithSpecification classSpecification, Statement stmt) {
		
	}
	@Override
	public void unevaluableConstraint(AnalysisSeedWithSpecification seed, ISLConstraint con, Statement location) {
	}

}
