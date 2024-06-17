package crypto.analysis;

import boomerang.debugger.Debugger;
import boomerang.scene.CallGraph;
import boomerang.scene.DataFlowScope;
import boomerang.scene.Method;
import boomerang.scene.WrappedClass;
import com.google.common.collect.Table;
import crypto.analysis.errors.AbstractError;
import crypto.listener.IAnalysisListener;
import crypto.listener.IErrorListener;
import crypto.listener.IResultsListener;
import crypto.rules.CrySLRule;
import ideal.IDEALSeedSolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import typestate.TransitionFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public abstract class CryptoScanner {

	private static final Logger LOGGER = LoggerFactory.getLogger(CryptoScanner.class);

	private final AnalysisReporter analysisReporter;
	private final ErrorCollector errorCollector;
	private final Collection<CrySLRule> ruleset;
	private final DataFlowScope dataFlowScope;
	private final Map<IAnalysisSeed, IAnalysisSeed> discoveredSeeds = new HashMap<>();
	private final PredicateHandler predicateHandler = new PredicateHandler(this);

	public CryptoScanner(Collection<CrySLRule> rules) {
		analysisReporter = new AnalysisReporter();

		AnalysisPrinter analysisPrinter = new AnalysisPrinter();
		addAnalysisListener(analysisPrinter);

		errorCollector = new ErrorCollector();
		addErrorListener(errorCollector);

		ruleset = new HashSet<>(rules);
		dataFlowScope = new CryptoAnalysisDataFlowScope(rules);
	}

	public void scan() {
		this.getAnalysisReporter().beforeAnalysis();
		SeedGenerator generator = new SeedGenerator(this, ruleset);
		List<IAnalysisSeed> seeds = new ArrayList<>(generator.computeSeeds());

		for (IAnalysisSeed seed : seeds) {
			discoveredSeeds.put(seed, seed);
		}

		this.getAnalysisReporter().addProgress(0, seeds.size());
		for (int i = 0; i < seeds.size(); i++) {
			seeds.get(i).execute();
			this.getAnalysisReporter().addProgress(i + 1, seeds.size());
		}

		this.getAnalysisReporter().beforePredicateCheck();
		predicateHandler.checkPredicates();
		this.getAnalysisReporter().afterPredicateCheck();

		this.getAnalysisReporter().afterAnalysis();
	}

	public abstract CallGraph callGraph();

	public DataFlowScope getDataFlowScope() {
		return dataFlowScope;
	}

	public Debugger<TransitionFunction> debugger(IDEALSeedSolver<TransitionFunction> solver, IAnalysisSeed analyzedObject) {
		return new Debugger<>();
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

	public Collection<CrySLRule> getRuleset() {
		return ruleset;
	}

	public Collection<IAnalysisSeed> getDiscoveredSeeds() {
		return discoveredSeeds.keySet();
	}

	protected boolean isOnIgnoreSectionList(Method method) {
		String declaringClass = method.getDeclaringClass().getName();
		String methodName = declaringClass + "." + method.getName();

		for (String ignoredSection : getIgnoredSections()) {
			// Check for class name
			if (ignoredSection.equals(declaringClass)) {
				LOGGER.info("Ignoring seeds in class " + declaringClass);
				return true;
			}

			// Check for method name
			if (ignoredSection.equals(methodName)) {
				LOGGER.info("Ignoring seeds in method " + methodName);
				return true;
			}

			// Check for wildcards (i.e. *)
			if (ignoredSection.endsWith(".*") && declaringClass.startsWith(ignoredSection.substring(0, ignoredSection.length() - 2))) {
				LOGGER.info("Ignoring seeds in class " + declaringClass + " and method " + methodName);
				return true;
			}
		}

		return false;
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

	public Optional<AnalysisSeedWithSpecification> getSeedWithSpec(AnalysisSeedWithSpecification seedAtStatement) {
		if (discoveredSeeds.containsKey(seedAtStatement)) {
			AnalysisSeedWithSpecification seed = (AnalysisSeedWithSpecification) discoveredSeeds.get(seedAtStatement);
			return Optional.of(seed);
		}
		return Optional.empty();
	}

	public Optional<AnalysisSeedWithEnsuredPredicate> getSeedWithoutSpec(AnalysisSeedWithEnsuredPredicate seedAtStatement) {
		if (discoveredSeeds.containsKey(seedAtStatement)) {
			AnalysisSeedWithEnsuredPredicate seed = (AnalysisSeedWithEnsuredPredicate) discoveredSeeds.get(seedAtStatement);
			return Optional.of(seed);
		}
		return Optional.empty();
	}

	public PredicateHandler getPredicateHandler() {
		return predicateHandler;
	}

	public Collection<String> getIgnoredSections() {
		return new ArrayList<>();
	}
}
