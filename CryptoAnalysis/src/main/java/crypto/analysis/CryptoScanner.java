package crypto.analysis;

import boomerang.Query;
import boomerang.callgraph.BoomerangResolver;
import boomerang.callgraph.ObservableDynamicICFG;
import boomerang.callgraph.ObservableICFG;
import boomerang.controlflowgraph.DynamicCFG;
import boomerang.debugger.Debugger;
import boomerang.scene.CallGraph;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.DataFlowScope;
import boomerang.scene.Method;
import boomerang.scene.SootDataFlowScope;
import boomerang.scene.Statement;
import boomerang.scene.Val;
import boomerang.scene.jimple.JimpleMethod;
import boomerang.scene.jimple.SootCallGraph;
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
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.util.queue.QueueReader;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public abstract class CryptoScanner {

	private final LinkedList<IAnalysisSeed> worklist = Lists.newLinkedList();
	private final List<ClassSpecification> specifications = Lists.newLinkedList();
	private final PredicateHandler predicateHandler = new PredicateHandler(this);
	private CrySLResultsReporter resultsAggregator = new CrySLResultsReporter();
	private static final Logger logger = LoggerFactory.getLogger(CryptoScanner.class);

	private DefaultValueMap<Node<ControlFlowGraph.Edge, Val>, AnalysisSeedWithEnsuredPredicate> seedsWithoutSpec = new DefaultValueMap<Node<ControlFlowGraph.Edge, Val>, AnalysisSeedWithEnsuredPredicate>() {

		@Override
		protected AnalysisSeedWithEnsuredPredicate createItem(Node<ControlFlowGraph.Edge, Val> key) {
			return new AnalysisSeedWithEnsuredPredicate(CryptoScanner.this, key);
		}
	};
	private DefaultValueMap<AnalysisSeedWithSpecification, AnalysisSeedWithSpecification> seedsWithSpec = new DefaultValueMap<AnalysisSeedWithSpecification, AnalysisSeedWithSpecification>() {

		@Override
		protected AnalysisSeedWithSpecification createItem(AnalysisSeedWithSpecification key) {
			return new AnalysisSeedWithSpecification(CryptoScanner.this, key.cfgEdge(), key.var(), key.getSpec());
		}
	};
	private int solvedObject;
	private Stopwatch analysisWatch;
	private CallGraph callGraph;

	public ObservableICFG<Statement, Method> icfg() {
		return new ObservableDynamicICFG(new DynamicCFG(), new BoomerangResolver(callGraph(), getDataFlowScope()));
	}

	public CallGraph callGraph() {
		return callGraph;
	}

	public DataFlowScope getDataFlowScope() {
		return SootDataFlowScope.make(Scene.v());
	}

	public CrySLResultsReporter getAnalysisListener() {
		return resultsAggregator;
	};

	public CryptoScanner() {
		CrySLMethodToSootMethod.reset();
		callGraph = new SootCallGraph();
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

	/*private void initialize() {
		Set<Method> methods = callGraph().getReachableMethods();

		for (Method method : methods) {

			for (ClassSpecification spec : getClassSpecifications()) {
				if (!((JimpleMethod) method).getDelegate().hasActiveBody())) {
					continue;
				}

				spec.invokesForbiddenMethod(method);

				if (isOnIgnoreSectionList(method)) {
					continue;
				}

				for (Query seed : spec.getInitialSeeds(method)) {
					getOrCreateSeedWithSpec(new AnalysisSeedWithSpecification(this, seed.cfgEdge(), seed.var(), spec));
				}
			}
		}
	}*/

	private void initialize() {
		ReachableMethods rm = Scene.v().getReachableMethods();
		QueueReader<MethodOrMethodContext> listener = rm.listener();
		while (listener.hasNext()) {
			MethodOrMethodContext next = listener.next();
			SootMethod sootMethod = next.method();

			if (sootMethod == null || !sootMethod.hasActiveBody() || !sootMethod.getDeclaringClass().isApplicationClass()) {
				continue;
			}

			Method method = JimpleMethod.of(sootMethod);

			if (isOnIgnoreSectionList(method)) {
				continue;
			}

			for (ClassSpecification spec : getClassSpecifications()) {
				spec.invokesForbiddenMethod(method);
				
				for (Query seed : spec.getInitialSeeds(method)) {
					getOrCreateSeedWithSpec(new AnalysisSeedWithSpecification(this, seed.cfgEdge(), seed.var(), spec));
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

	protected boolean isOnIgnoreSectionList(Method method) {
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

	public AnalysisSeedWithEnsuredPredicate getOrCreateSeed(Node<ControlFlowGraph.Edge, Val> factAtStatement) {
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
