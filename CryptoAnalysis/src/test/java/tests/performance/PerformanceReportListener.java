package tests.performance;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

import boomerang.BackwardQuery;
import boomerang.Query;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import boomerang.results.ForwardBoomerangResults;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.CrySLAnalysisListener;
import crypto.analysis.EnsuredCryptSLPredicate;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractedValue;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CryptSLPredicate;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;

public class PerformanceReportListener extends CrySLAnalysisListener {

	private Stopwatch analysisTime = Stopwatch.createUnstarted();
	
	@Override
	public void beforeAnalysis() {
		analysisTime.start();
	}

	@Override
	public void afterAnalysis() {
		analysisTime.stop();
		System.out.println("From new listener - " + analysisTime.elapsed(TimeUnit.SECONDS));
		
	}

	@Override
	public void beforeConstraintCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterConstraintCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforePredicateCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterPredicateCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void seedStarted(IAnalysisSeed analysisSeedWithSpecification) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void boomerangQueryStarted(Query seed, BackwardQuery q) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void boomerangQueryFinished(Query seed, BackwardQuery q) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ensuredPredicates(Table<Statement, Val, Set<EnsuredCryptSLPredicate>> existingPredicates,
			Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> expectedPredicates,
			Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> missingPredicates) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reportError(AbstractError error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkedConstraints(AnalysisSeedWithSpecification analysisSeedWithSpecification,
			Collection<ISLConstraint> relConstraints) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSeedTimeout(Node<Statement, Val> seed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSeedFinished(IAnalysisSeed seed, ForwardBoomerangResults<TransitionFunction> analysisResults) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void collectedValues(AnalysisSeedWithSpecification seed,
			Multimap<CallSiteWithParamIndex, ExtractedValue> collectedValues) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void discoveredSeed(IAnalysisSeed curr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSecureObjectFound(IAnalysisSeed analysisObject) {
		// TODO Auto-generated method stub
		
	}

}
