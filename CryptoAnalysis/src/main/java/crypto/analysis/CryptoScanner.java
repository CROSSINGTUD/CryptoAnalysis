package crypto.analysis;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;

import boomerang.Query;
import boomerang.debugger.Debugger;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.predicates.PredicateHandler;
import crypto.rules.CryptSLRule;
import crypto.typestate.CryptSLMethodToSootMethod;
import heros.utilities.DefaultValueMap;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;

public abstract class CryptoScanner {

	public static boolean APPLICATION_CLASS_SEEDS_ONLY = false;
	private final LinkedList<IAnalysisSeed> worklist = Lists.newLinkedList();
	private final List<ClassSpecification> specifications = Lists.newLinkedList();
	private final PredicateHandler predicateHandler = new PredicateHandler(this);
	private CrySLResultsReporter resultsAggregator = new CrySLResultsReporter();

	

	private DefaultValueMap<Node<Statement,Val>, AnalysisSeedWithEnsuredPredicate> seedsWithoutSpec = new DefaultValueMap<Node<Statement,Val>, AnalysisSeedWithEnsuredPredicate>() {

		@Override
		protected AnalysisSeedWithEnsuredPredicate createItem(Node<Statement,Val> key) {
			return new AnalysisSeedWithEnsuredPredicate(CryptoScanner.this, key);
		}
	};
	private DefaultValueMap<AnalysisSeedWithSpecification, AnalysisSeedWithSpecification> seedsWithSpec = new DefaultValueMap<AnalysisSeedWithSpecification, AnalysisSeedWithSpecification>() {

		@Override
		protected AnalysisSeedWithSpecification createItem(AnalysisSeedWithSpecification key) {
			return new AnalysisSeedWithSpecification(CryptoScanner.this, key.stmt(),key.var(), key.getSpec());
		}
	};

	public abstract BiDiInterproceduralCFG<Unit, SootMethod> icfg();

	public CrySLResultsReporter getAnalysisListener() {
		return resultsAggregator;
	};

	public abstract boolean isCommandLineMode();


	public CryptoScanner(List<CryptSLRule> specs) {
		CryptSLMethodToSootMethod.reset();
		for (CryptSLRule rule : specs) {
			specifications.add(new ClassSpecification(rule, this));
		}
	}

	


	public void scan() {
		getAnalysisListener().beforeAnalysis();
		Stopwatch watch = Stopwatch.createStarted();
		initialize();
		long elapsed = watch.elapsed(TimeUnit.SECONDS);
		System.out.println("Discovered "+worklist.size() + " analysis seeds within " + elapsed + " seconds!");
		while (!worklist.isEmpty()) {
			IAnalysisSeed curr = worklist.poll();
			getAnalysisListener().discoveredSeed(curr);
			curr.execute();
		}
		
//		IDebugger<TypestateDomainValue<StateNode>> debugger = debugger();
//		if (debugger instanceof CryptoVizDebugger) {
//			CryptoVizDebugger ideVizDebugger = (CryptoVizDebugger) debugger;
//			ideVizDebugger.addEnsuredPredicates(this.existingPredicates);
//		}
		predicateHandler.checkPredicates();
		
		getAnalysisListener().afterAnalysis();
		elapsed = watch.elapsed(TimeUnit.SECONDS);
		System.out.println("Static Analysis took "+elapsed+ " seconds!");
//		debugger().afterAnalysis();
	}

	

	private void initialize() {
		for (ClassSpecification spec : getClassSpecifictions()) {
			spec.checkForForbiddenMethods();
			if (!isCommandLineMode() && !spec.isLeafRule())
				continue;

			for (Query seed : spec.getInitialSeeds()) {
				getOrCreateSeedWithSpec(new AnalysisSeedWithSpecification(this, seed.stmt(),seed.var(),spec));
			}
		}
	}

	public List<ClassSpecification> getClassSpecifictions() {
		return specifications;
	}

	protected void addToWorkList(IAnalysisSeed analysisSeedWithSpecification) {
		worklist.add(analysisSeedWithSpecification);
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


	
	public Debugger<TransitionFunction> debugger() {
		return new Debugger<>();
	}

	public PredicateHandler getPredicateHandler() {
		return predicateHandler;
	}

	public Collection<AnalysisSeedWithSpecification> getAnalysisSeeds() {
		return this.seedsWithSpec.values();
	}
}
