package tests.performance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
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
	private Stopwatch seedAnalysisWatch = Stopwatch.createUnstarted();
	private Stopwatch boomerangAnalysisWatch = Stopwatch.createUnstarted();
	private List<Double> seedAnalysisTime = new ArrayList<>();
	private List<Double> boomerangAnalysisTime = new ArrayList<>();
	private String statisticsFilePath;
	private int seeds = 0, secureObjectsFound = 0;
	private Set<AbstractError> errors = Sets.newHashSet();
	
	public PerformanceReportListener(String statsFilePath) {
		statisticsFilePath = statsFilePath;
	}
	
	@Override
	public void beforeAnalysis() {
		analysisTime.start();
	}

	@Override
	public void afterAnalysis() {
		analysisTime.stop();
		//Analysis time
		//Average seed analysis time
		//Number of seeds
		//Total secure objects found
		//Boomerang analysis time
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
		seedAnalysisWatch.start();
	}

	@Override
	public void boomerangQueryStarted(Query seed, BackwardQuery q) {
		boomerangAnalysisWatch.start();
	}

	@Override
	public void boomerangQueryFinished(Query seed, BackwardQuery q) {
		boomerangAnalysisTime.add((double) boomerangAnalysisWatch.elapsed(TimeUnit.SECONDS));
		boomerangAnalysisWatch.reset();
	}

	@Override
	public void ensuredPredicates(Table<Statement, Val, Set<EnsuredCryptSLPredicate>> existingPredicates,
			Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> expectedPredicates,
			Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> missingPredicates) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reportError(AbstractError error) {
		errors.add(error);
	}

	@Override
	public void checkedConstraints(AnalysisSeedWithSpecification analysisSeedWithSpecification,
			Collection<ISLConstraint> relConstraints) {
		
	}

	@Override
	public void onSeedTimeout(Node<Statement, Val> seed) {
		seedAnalysisWatch.reset();
	}

	@Override
	public void onSeedFinished(IAnalysisSeed seed, ForwardBoomerangResults<TransitionFunction> analysisResults) {
		seedAnalysisTime.add((double) seedAnalysisWatch.elapsed(TimeUnit.SECONDS));
		seedAnalysisWatch.reset();
	}

	@Override
	public void collectedValues(AnalysisSeedWithSpecification seed,
			Multimap<CallSiteWithParamIndex, ExtractedValue> collectedValues) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void discoveredSeed(IAnalysisSeed curr) {
		seeds++;
	}

	@Override
	public void onSecureObjectFound(IAnalysisSeed analysisObject) {
		secureObjectsFound++;
	}

}
