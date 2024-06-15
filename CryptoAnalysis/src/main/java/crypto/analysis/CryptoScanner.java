package crypto.analysis;

import boomerang.callgraph.BoomerangResolver;
import boomerang.callgraph.ObservableDynamicICFG;
import boomerang.callgraph.ObservableICFG;
import boomerang.controlflowgraph.DynamicCFG;
import boomerang.debugger.Debugger;
import boomerang.scene.CallGraph;
import boomerang.scene.DataFlowScope;
import boomerang.scene.Method;
import boomerang.scene.Statement;
import boomerang.scene.jimple.BoomerangPretransformer;
import boomerang.scene.jimple.SootCallGraph;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import crypto.boomerang.CryptoAnalysisDataFlowScope;
import crypto.preanalysis.TransformerSetup;
import crypto.predicates.PredicateHandler;
import crypto.rules.CrySLRule;
import ideal.IDEALSeedSolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import typestate.TransitionFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public abstract class CryptoScanner {

	private static final Logger LOGGER = LoggerFactory.getLogger(CryptoScanner.class);

	private final LinkedList<IAnalysisSeed> worklist = Lists.newLinkedList();
	private final PredicateHandler predicateHandler = new PredicateHandler(this);
	private final CrySLResultsReporter resultsReporter;

	private final Map<IAnalysisSeed, IAnalysisSeed> discoveredSeeds = new HashMap<>();
	private final Collection<CrySLRule> ruleset;

	private int solvedObject;
	private Stopwatch analysisWatch;
	private CallGraph callGraph;
	private DataFlowScope dataFlowScope;

	public ObservableICFG<Statement, Method> icfg() {
		return new ObservableDynamicICFG(new DynamicCFG(), new BoomerangResolver(callGraph(), getDataFlowScope()));
	}

	public CallGraph callGraph() {
		return callGraph;
	}

	public DataFlowScope getDataFlowScope() {
		return dataFlowScope;
	}

	public CrySLResultsReporter getAnalysisListener() {
		return resultsReporter;
	}

	public CryptoScanner(Collection<CrySLRule> rules) {
		TransformerSetup.v().reset();
		TransformerSetup.v().setupPreTransformer(rules);

		resultsReporter = getAnalysisListener();
		ruleset = new HashSet<>(rules);
		callGraph = new SootCallGraph();
		dataFlowScope = new CryptoAnalysisDataFlowScope(rules);
	}

	public void scan() {
		SeedGenerator generator = new SeedGenerator(this, ruleset);
		List<IAnalysisSeed> seeds = new ArrayList<>(generator.computeSeeds());

		for (IAnalysisSeed seed : seeds) {
			discoveredSeeds.put(seed, seed);
		}

		resultsReporter.addProgress(0, seeds.size());
		for (int i = 0; i < seeds.size(); i++) {
			seeds.get(i).execute();
			resultsReporter.addProgress(i, seeds.size());
		}

		predicateHandler.checkPredicates();

		resultsReporter.afterAnalysis();
	}

	public void scan2(List<CrySLRule> specs) {
		int processedSeeds = 0;

		CrySLResultsReporter listener = getAnalysisListener();
		listener.beforeAnalysis();
		analysisWatch = Stopwatch.createStarted();
		LOGGER.info("Searching for seeds for the analysis!");
		long elapsed = analysisWatch.elapsed(TimeUnit.SECONDS);
		LOGGER.info("Discovered " + worklist.size() + " analysis seeds within " + elapsed + " seconds!");
		while (!worklist.isEmpty()) {
			IAnalysisSeed curr = worklist.poll();
			listener.discoveredSeed(curr);
			curr.execute();
			processedSeeds++;
			listener.addProgress(processedSeeds,worklist.size());
			estimateAnalysisTime();
		}

//		IDebugger<TypestateDomainValue<StateNode>> debugger = debugger();
//		if (debugger instanceof CryptoVizDebugger) {
//			CryptoVizDebugger ideVizDebugger = (CryptoVizDebugger) debugger;
//			ideVizDebugger.addEnsuredPredicates(this.existingPredicates);
//		}
		predicateHandler.checkPredicates();

		for (AnalysisSeedWithSpecification seed : getAnalysisSeedsWithSpec()) {
			if (seed.isSecure()) {
				listener.onSecureObjectFound(seed);
			}
		}
		
		listener.afterAnalysis();
		elapsed = analysisWatch.elapsed(TimeUnit.SECONDS);
		LOGGER.info("Static Analysis took " + elapsed + " seconds!");
//		debugger().afterAnalysis();
	}

	private void estimateAnalysisTime() {
		int remaining = worklist.size();
		solvedObject++;
		if (remaining != 0) {
//			Duration elapsed = analysisWatch.elapsed();
//			Duration estimate = elapsed.dividedBy(solvedObject);
//			Duration remainingTime = estimate.multipliedBy(remaining);
//			System.out.println(String.format("Analysis Time: %s", elapsed));
//			System.out.println(String.format("Estimated Time: %s", remainingTime));
			LOGGER.info(String.format("Analyzed Objects: %s of %s", solvedObject, remaining + solvedObject));
			LOGGER.info(String.format("Percentage Completed: %s\n",
					((float) Math.round((float) solvedObject * 100 / (remaining + solvedObject))) / 100));
		}
	}

	public Collection<CrySLRule> getRuleset() {
		return ruleset;
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

	public Debugger<TransitionFunction> debugger(IDEALSeedSolver<TransitionFunction> solver,
			IAnalysisSeed analyzedObject) {
		return new Debugger<>();
	}

	public PredicateHandler getPredicateHandler() {
		return predicateHandler;
	}

	public Collection<String> getForbiddenPredicates() {
		return new ArrayList<>();
	}

	public Collection<String> getIgnoredSections() {
		return new ArrayList<>();
	}
}
