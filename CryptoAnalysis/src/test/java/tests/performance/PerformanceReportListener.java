package tests.performance;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import crypto.analysis.EnsuredCrySLPredicate;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractedValue;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CrySLPredicate;
import crypto.rules.CrySLRule;
import soot.Scene;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;

public class PerformanceReportListener extends CrySLAnalysisListener {

	private int seeds = 0, secureObjectsFound = 0;
	private Set<AbstractError> errors = Sets.newHashSet();
	private int sootReachableMethods;
	long memoryUsed;
	private long MEGABYTE = 1024L * 1024L;
	private Map<String, String> observations;

	public PerformanceReportListener(List<CrySLRule> rules,  Map<String, String> observations) {
		this.observations = observations;
		this.observations.put(SpreadSheetConstants.NO_OF_RULES, String.valueOf(rules.size()));
	}

	@Override
	public void beforeAnalysis() {
	}

	@Override
	public void afterAnalysis() {
		Runtime rt = Runtime.getRuntime();
		rt.gc();
		memoryUsed = (rt.totalMemory() - rt.freeMemory()) / MEGABYTE;
		sootReachableMethods = Scene.v().getReachableMethods().size();
		this.observations.put(SpreadSheetConstants.MEMORY_USED, String.valueOf(memoryUsed));
		this.observations.put(SpreadSheetConstants.SOOT_REACHABLE_METHODS, String.valueOf(sootReachableMethods));
		this.observations.put(SpreadSheetConstants.NO_OF_SEEDS, String.valueOf(seeds));
		this.observations.put(SpreadSheetConstants.NO_OF_SECURE_OBJECTS, String.valueOf(secureObjectsFound));
		this.observations.put(SpreadSheetConstants.NO_OF_FINDINGS, String.valueOf(errors.size()));
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
	}

	@Override
	public void boomerangQueryStarted(Query seed, BackwardQuery q) {
	}

	@Override
	public void boomerangQueryFinished(Query seed, BackwardQuery q) {
	}

	@Override
	public void ensuredPredicates(Table<Statement, Val, Set<EnsuredCrySLPredicate>> existingPredicates,
			Table<Statement, IAnalysisSeed, Set<CrySLPredicate>> expectedPredicates,
			Table<Statement, IAnalysisSeed, Set<CrySLPredicate>> missingPredicates) {
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
	}

	@Override
	public void onSeedFinished(IAnalysisSeed seed, ForwardBoomerangResults<TransitionFunction> analysisResults) {
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

	@Override
	public void addProgress(int processedSeeds, int workListsize) {
	}
}
