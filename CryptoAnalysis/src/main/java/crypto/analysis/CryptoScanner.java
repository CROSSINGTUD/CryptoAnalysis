package crypto.analysis;

import boomerang.debugger.Debugger;
import boomerang.scene.CallGraph;
import boomerang.scene.DataFlowScope;
import boomerang.scene.Method;
import boomerang.scene.WrappedClass;
import boomerang.scene.sparse.SparseCFGCache;
import com.google.common.collect.Table;
import crypto.analysis.errors.AbstractError;
import crypto.cryslhandler.RulesetReader;
import crypto.exceptions.CryptoAnalysisException;
import crypto.listener.AnalysisReporter;
import crypto.listener.AnalysisStatistics;
import crypto.listener.AnalysisPrinter;
import crypto.listener.ErrorCollector;
import crypto.listener.IAnalysisListener;
import crypto.listener.IErrorListener;
import crypto.listener.IResultsListener;
import crypto.rules.CrySLRule;
import ideal.IDEALSeedSolver;
import typestate.TransitionFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class CryptoScanner {

	private final AnalysisReporter analysisReporter;
	private final AnalysisPrinter analysisPrinter;
	private final ErrorCollector errorCollector;
	private final Map<IAnalysisSeed, IAnalysisSeed> discoveredSeeds;
	private final PredicateHandler predicateHandler;

	private CallGraph callGraph;
	private Collection<CrySLRule> ruleset;
	private DataFlowScope dataFlowScope;

	protected CryptoScanner() {
		this.analysisReporter = new AnalysisReporter();
		this.discoveredSeeds = new HashMap<>();
		this.predicateHandler = new PredicateHandler(this);

		analysisPrinter = new AnalysisPrinter();
		addAnalysisListener(analysisPrinter);

		errorCollector = new ErrorCollector();
		addErrorListener(errorCollector);
	}

	protected final void initialize() {
		// Read the ruleset
		analysisReporter.beforeReadingRuleset(getRulesetPath());
		try {
			RulesetReader reader = new RulesetReader();
			ruleset = reader.readRulesFromPath(getRulesetPath());
		} catch (IOException e) {
			throw new CryptoAnalysisException("Could not read rules: " + e.getMessage());
		}
		analysisReporter.afterReadingRuleset(getRulesetPath(), ruleset);

		// Construct the call graph
		analysisReporter.beforeCallGraphConstruction();
		callGraph = constructCallGraph(ruleset);
		analysisReporter.afterCallGraphConstruction(callGraph);

		// Initialize the dataflow scope
		dataFlowScope = createDataFlowScope(ruleset);
	}

	protected final void scan() {
		// Check whether the fields have been initialized correctly
		if (ruleset == null) {
			throw new CryptoAnalysisException("Cannot start the scan. The ruleset must not be null");
		}
		if (callGraph == null) {
			throw new CryptoAnalysisException("Cannot start the scan. The call graph must not be null");
		}
		if (dataFlowScope == null) {
			throw new CryptoAnalysisException("Cannot start the scan. The dataflow scope must not be null");
		}

		// Start analysis
		analysisReporter.beforeAnalysis();

		SeedGenerator generator = new SeedGenerator(this, ruleset);
		List<IAnalysisSeed> seeds = new ArrayList<>(generator.computeSeeds());
		analysisReporter.onDiscoveredSeeds(seeds);

		for (IAnalysisSeed seed : seeds) {
			discoveredSeeds.put(seed, seed);
		}

		for (int i = 0; i < seeds.size(); i++) {
			seeds.get(i).execute();
			analysisReporter.addProgress(i + 1, seeds.size());
		}

		analysisReporter.beforePredicateCheck();
		predicateHandler.checkPredicates();
		analysisReporter.afterPredicateCheck();

		analysisReporter.afterAnalysis();
	}

	public Collection<CrySLRule> getRuleset() {
		return ruleset;
	}

	public CallGraph getCallGraph() {
		return callGraph;
	}

	public DataFlowScope getDataFlowScope() {
		return dataFlowScope;
	}

	public void addAnalysisListener(IAnalysisListener analysisListener) {
		analysisReporter.addAnalysisListener(analysisListener);
	}

	public void addErrorListener(IErrorListener errorListener) {
		analysisReporter.addErrorListener(errorListener);
	}

	public void addResultsListener(IResultsListener resultsListener) {
		analysisReporter.addResultsListener(resultsListener);
	}

	public AnalysisReporter getAnalysisReporter() {
		return analysisReporter;
	}

	public Table<WrappedClass, Method, Set<AbstractError>> getCollectedErrors() {
		return errorCollector.getErrorCollection();
	}

	public Collection<IAnalysisSeed> getDiscoveredSeeds() {
		return discoveredSeeds.keySet();
	}

	public Collection<AnalysisSeedWithSpecification> getAnalysisSeedsWithSpec() {
		Collection<AnalysisSeedWithSpecification> seeds = new HashSet<>();

		for (IAnalysisSeed seed : discoveredSeeds.keySet()) {
			if (seed instanceof AnalysisSeedWithSpecification) {
				seeds.add((AnalysisSeedWithSpecification) seed);
			}
		}
		return seeds;
	}

	public AnalysisStatistics getStatistics() {
		return analysisPrinter.getStatistics();
	}

	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 *               Methods that may or must be overridden by subclasses                *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	protected abstract String getRulesetPath();

	protected abstract CallGraph constructCallGraph(Collection<CrySLRule> ruleset);

	protected DataFlowScope createDataFlowScope(Collection<CrySLRule> ruleset) {
		return new CryptoAnalysisDataFlowScope(ruleset, Collections.emptySet());
	}

	public Debugger<TransitionFunction> debugger(IDEALSeedSolver<TransitionFunction> solver) {
		return new Debugger<>();
	}

	public SparseCFGCache.SparsificationStrategy getSparsificationStrategy() {
		return SparseCFGCache.SparsificationStrategy.NONE;
	}

	public int getTimeout() {
		return 100000;
	}
}
