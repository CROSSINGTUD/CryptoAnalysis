package crypto.analysis;

import boomerang.Query;
import boomerang.callgraph.ObservableICFG;
import boomerang.debugger.Debugger;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import crypto.predicates.PredicateHandler;
import crypto.rules.CrySLRule;
import crypto.typestate.CrySLMethodToSootMethod;
import heros.utilities.DefaultValueMap;
import ideal.IDEALSeedSolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.util.queue.QueueReader;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class CryptoScanner {

	private final LinkedList<IAnalysisSeed> worklist = Lists.newLinkedList();
	private final List<ClassSpecification> specifications = Lists.newLinkedList();
	private final PredicateHandler predicateHandler = new PredicateHandler(this);
	private CrySLResultsReporter resultsAggregator = new CrySLResultsReporter();
	private static final Logger logger = LoggerFactory.getLogger(CryptoScanner.class);

	private DefaultValueMap<Node<Statement, Val>, AnalysisSeedWithEnsuredPredicate> seedsWithoutSpec = new DefaultValueMap<Node<Statement, Val>, AnalysisSeedWithEnsuredPredicate>() {

		@Override
		protected AnalysisSeedWithEnsuredPredicate createItem(Node<Statement, Val> key) {
			return new AnalysisSeedWithEnsuredPredicate(CryptoScanner.this, key);
		}
	};
	private DefaultValueMap<AnalysisSeedWithSpecification, AnalysisSeedWithSpecification> seedsWithSpec = new DefaultValueMap<AnalysisSeedWithSpecification, AnalysisSeedWithSpecification>() {

		@Override
		protected AnalysisSeedWithSpecification createItem(AnalysisSeedWithSpecification key) {
			return new AnalysisSeedWithSpecification(CryptoScanner.this, key.stmt(), key.var(), key.getSpec());
		}
	};
	private int solvedObject;
	private Stopwatch analysisWatch;

	public abstract ObservableICFG<Unit, SootMethod> icfg();

	public CrySLResultsReporter getAnalysisListener() {
		return resultsAggregator;
	};

	public CryptoScanner() {
		CrySLMethodToSootMethod.reset();
	}

	public void scan(List<CrySLRule> specs) {
		int processedSeeds = 0;
		for (CrySLRule rule : specs) {
			specifications.add(new ClassSpecification(rule, this));
		}
		CrySLResultsReporter listener = getAnalysisListener();
		listener.beforeAnalysis();
		analysisWatch = Stopwatch.createStarted();
		logger.info("Searching for seeds for the analysis!");
		initialize();
		long elapsed = analysisWatch.elapsed(TimeUnit.SECONDS);
		logger.info("Discovered " + worklist.size() + " analysis seeds within " + elapsed + " seconds!");
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

		for (AnalysisSeedWithSpecification seed : getAnalysisSeeds()) {
			if (seed.isSecure()) {
				listener.onSecureObjectFound(seed);
			}
		}
		
		listener.afterAnalysis();
		elapsed = analysisWatch.elapsed(TimeUnit.SECONDS);
		logger.info("Static Analysis took " + elapsed + " seconds!");
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
			logger.info(String.format("Analyzed Objects: %s of %s", solvedObject, remaining + solvedObject));
			logger.info(String.format("Percentage Completed: %s\n",
					((float) Math.round((float) solvedObject * 100 / (remaining + solvedObject))) / 100));
		}
	}

	private void initialize() {
		ReachableMethods rm = Scene.v().getReachableMethods();
		QueueReader<MethodOrMethodContext> listener = rm.listener();
		while (listener.hasNext()) {
			MethodOrMethodContext next = listener.next();
			SootMethod method = next.method();

			if (method == null || !method.hasActiveBody() || !method.getDeclaringClass().isApplicationClass()) {
				continue;
			}

			if (isOnIgnoreSectionList(method)) {
				continue;
			}

			for (ClassSpecification spec : getClassSpecifications()) {
				spec.invokesForbiddenMethod(method);
				
				for (Query seed : spec.getInitialSeeds(method)) {
					getOrCreateSeedWithSpec(new AnalysisSeedWithSpecification(this, seed.stmt(), seed.var(), spec));
				}
			}
		}
	}

	public List<ClassSpecification> getClassSpecifications() {
		return specifications;
	}

	protected void addToWorkList(IAnalysisSeed analysisSeedWithSpecification) {
		worklist.add(analysisSeedWithSpecification);
	}

	protected boolean isOnIgnoreSectionList(SootMethod method) {
		String declaringClass = method.getDeclaringClass().getName();
		String methodName = declaringClass + "." + method.getName();

		for (String ignoredSection : getIgnoredSections()) {
			// Check for class name
			if (ignoredSection.equals(declaringClass)) {
				logger.info("Ignoring seeds in class " + declaringClass);
				return true;
			}

			// Check for method name
			if (ignoredSection.equals(methodName)) {
				logger.info("Ignoring seeds in method " + methodName);
				return true;
			}

			// Check for wildcards (i.e. *)
			if (ignoredSection.endsWith(".*") && declaringClass.startsWith(ignoredSection.substring(0, ignoredSection.length() - 2))) {
				logger.info("Ignoring seeds in class " + declaringClass + " and method " + methodName);
				return true;
			}
		}

		return false;
	}

	public AnalysisSeedWithEnsuredPredicate getOrCreateSeed(Node<Statement,Val> factAtStatement) {
		boolean addToWorklist = false;
		if (!seedsWithoutSpec.containsKey(factAtStatement))
			addToWorklist = true;

		AnalysisSeedWithEnsuredPredicate seed = seedsWithoutSpec.getOrCreate(factAtStatement);
		if (addToWorklist)
			addToWorkList(seed);
		return seed;
	}

	public AnalysisSeedWithSpecification getOrCreateSeedWithSpec(AnalysisSeedWithSpecification factAtStatement) {
		boolean addToWorklist = false;
		if (!seedsWithSpec.containsKey(factAtStatement))
			addToWorklist = true;
		AnalysisSeedWithSpecification seed = seedsWithSpec.getOrCreate(factAtStatement);
		if (addToWorklist)
			addToWorkList(seed);
		return seed;
	}

	public Debugger<TransitionFunction> debugger(IDEALSeedSolver<TransitionFunction> solver,
			IAnalysisSeed analyzedObject) {
		return new Debugger<>();
	}

	public PredicateHandler getPredicateHandler() {
		return predicateHandler;
	}

	public Collection<AnalysisSeedWithSpecification> getAnalysisSeeds() {
		return this.seedsWithSpec.values();
	}

	public Collection<String> getForbiddenPredicates() {
		return new ArrayList<>();
	}

	public Collection<String> getIgnoredSections() {
		return new ArrayList<>();
	}
}
