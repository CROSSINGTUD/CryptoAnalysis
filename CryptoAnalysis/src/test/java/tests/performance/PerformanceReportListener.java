package tests.performance;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
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
import crypto.rules.CryptSLRule;
import soot.Scene;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;

public class PerformanceReportListener extends CrySLAnalysisListener {

	private Stopwatch analysisTime = Stopwatch.createUnstarted();
	private Stopwatch seedAnalysisWatch = Stopwatch.createUnstarted();
	private Stopwatch boomerangAnalysisWatch = Stopwatch.createUnstarted();
	private List<Double> seedAnalysisTime = new ArrayList<>();
	private List<Double> boomerangAnalysisTime = new ArrayList<>();
	private int seeds = 0, secureObjectsFound = 0;
	private Set<AbstractError> errors = Sets.newHashSet();
	private String gitCommitId;
	private BenchmarkProject curProject;
	private int sootReachableMethods;
	long memoryUsed;
	int noOfRules;
	private long MEGABYTE = 1024L * 1024L;

	public PerformanceReportListener(BenchmarkProject proj, String commitId, List<CryptSLRule> rules) {
		gitCommitId = commitId;
		curProject = proj;
		noOfRules = rules.size();
	}

	@Override
	public void beforeAnalysis() {
		analysisTime.start();
	}

	@Override
	public void afterAnalysis() {
		Runtime rt = Runtime.getRuntime();
		rt.gc();
		memoryUsed = (rt.totalMemory() - rt.freeMemory()) / MEGABYTE;
		sootReachableMethods = Scene.v().getReachableMethods().size();
		try {
			GoogleSpreadsheetWriter.write(asCSVLine(), curProject.getName());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void beforeConstraintCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification) {
	}

	@Override
	public void afterConstraintCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification) {
	}

	@Override
	public void beforePredicateCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification) {
	}

	@Override
	public void afterPredicateCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification) {
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
	}

	@Override
	public void discoveredSeed(IAnalysisSeed curr) {
		seeds++;
	}

	@Override
	public void onSecureObjectFound(IAnalysisSeed analysisObject) {
		secureObjectsFound++;
	}

	private List<Object> asCSVLine() {
		long elapsed = analysisTime.elapsed(TimeUnit.SECONDS);
		analysisTime.stop();
		String analysisTime = String.valueOf(elapsed);
		String memUsed = String.valueOf(memoryUsed);
		String reachableMethods = String.valueOf(sootReachableMethods);
		String nRules = String.valueOf(noOfRules);
		Double avgSAT = seedAnalysisTime.stream().mapToDouble(d -> d).average().orElse(0.0);
		String averageSeedAnalysisTime = String.valueOf(avgSAT);
		Double avgBAT = boomerangAnalysisTime.stream().mapToDouble(d -> d).average().orElse(0.0);
		String averageBoomerangAnalysisTime = String.valueOf(avgBAT);
		String numberOfSeeds = String.valueOf(seeds);
		String numberOfSecureObjects = String.valueOf(secureObjectsFound);
		return Arrays.asList(new String[] { gitCommitId, 
				analysisTime, 
				memUsed, 
				reachableMethods, 
				nRules, 
				numberOfSeeds, 
				numberOfSecureObjects,
				averageSeedAnalysisTime,
				averageBoomerangAnalysisTime
				});
	}
}
