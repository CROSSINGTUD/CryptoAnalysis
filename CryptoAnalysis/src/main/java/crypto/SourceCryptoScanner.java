package crypto;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import com.beust.jcommander.internal.Sets;
import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import boomerang.jimple.Statement;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.CogniCryptCLIReporter;
import crypto.analysis.CrySLAnalysisResultsAggregator;
import crypto.analysis.CryptoScanner;
import crypto.analysis.IAnalysisSeed;
import crypto.preanalysis.SeedFactory;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLRule;
import crypto.rules.CryptSLRuleReader;
import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.MethodOrMethodContext;
import soot.PackManager;
import soot.PhaseOptions;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.Transform;
import soot.Transformer;
import soot.Unit;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import soot.options.Options;
import soot.util.queue.QueueReader;
import typestate.interfaces.ISLConstraint;

public abstract class SourceCryptoScanner {
	private CrySLAnalysisResultsAggregator reporter;
	private boolean hasSeeds;
	private static Stopwatch callGraphWatch;

	public static enum CG {
		CHA, SPARK_LIBRARY, SPARK
	}

	public static void main(String... args) {
		System.out.println(Arrays.toString(args));
		final String resourcesPath;
		if (args.length > 3)
			resourcesPath = args[3];
		else 
			resourcesPath = "rules";
		final CG callGraphAlogrithm;
		if (args.length > 4) {
			if (args[4].equalsIgnoreCase("cha")) {
				callGraphAlogrithm = CG.CHA;
			} else if (args[4].equalsIgnoreCase("spark")) {
				callGraphAlogrithm = CG.SPARK;
			} else if (args[4].equalsIgnoreCase("spark-library")) {
				callGraphAlogrithm = CG.SPARK_LIBRARY;
			} else if (args[4].equalsIgnoreCase("library")) {
				callGraphAlogrithm = CG.SPARK_LIBRARY;
			} else {
				callGraphAlogrithm = CG.CHA;
			}
		} else {
			callGraphAlogrithm = CG.CHA;
		}
		SourceCryptoScanner sourceCryptoScanner = new SourceCryptoScanner() {

			@Override
			protected String sootClassPath() {
				return args[2];
			}

			@Override
			protected String applicationClassPath() {
				return args[1];
			}

			@Override
			protected CG callGraphAlogrithm() {
				return callGraphAlogrithm;
			}

			@Override
			protected String softwareIdentifier() {
				return args[0];
			}

			@Override
			protected String getRulesDirectory() {
				return resourcesPath;
			}

		};
		sourceCryptoScanner.exec();

	}

	public void exec() {
		initializeSootWithEntryPointAllReachable(false);
		checkIfUsesObject();
		if (hasSeeds()) {
			System.out.println("Using call graph algorithm " + callGraphAlogrithm());
			initializeSootWithEntryPointAllReachable(true);
			analyse();
		}
	}

	public boolean hasSeeds(){
		return hasSeeds;
	}
	private void checkIfUsesObject() {
		final SeedFactory seedFactory = new SeedFactory(getRules());
		PackManager.v().getPack("jap").add(new Transform("jap.myTransform", new BodyTransformer() {
			protected void internalTransform(Body body, String phase, Map options) {
				if (!body.getMethod().getDeclaringClass().isApplicationClass()) {
					return;
				}
				for (Unit u : body.getUnits()) {
					seedFactory.generate(body.getMethod(), u);
				}
			}
		}));
		PhaseOptions.v().setPhaseOption("jap.npc", "on");
		PackManager.v().runPacks();
		hasSeeds = seedFactory.hasSeeds();
	}

	private void analyse() {
		// PackManager.v().getPack("wjtp").add(new Transform("wjtp.prepare", new
		// PreparationTransformer()));
		Transform transform = new Transform("wjtp.ifds", createAnalysisTransformer());
		PackManager.v().getPack("wjtp").add(transform);
		callGraphWatch = Stopwatch.createStarted();
		PackManager.v().getPack("cg").apply();
		PackManager.v().getPack("wjtp").apply();
	}

	private Transformer createAnalysisTransformer() {
		return new SceneTransformer() {

			@Override
			protected void internalTransform(String phaseName, Map<String, String> options) {
				final JimpleBasedInterproceduralCFG icfg = new JimpleBasedInterproceduralCFG(false);
				CryptoScanner scanner = new CryptoScanner(SourceCryptoScanner.this.getRules()) {

					@Override
					public BiDiInterproceduralCFG<Unit, SootMethod> icfg() {
						return icfg;
					}

					@Override
					public CrySLAnalysisResultsAggregator getAnalysisListener() {
						return getReporter();
					}

					// @Override
					// public IDebugger<TypestateDomainValue<StateNode>>
					// debugger() {
					// return new NullDebugger<>();
					// }

					@Override
					public boolean isCommandLineMode() {
						return true;
					}

				};
				scanner.scan();
				System.out.println(getReporter());

				ReachableMethods reachableMethods = Scene.v().getReachableMethods();
				QueueReader<MethodOrMethodContext> listener = reachableMethods.listener();
				Set<SootMethod> visited = Sets.newHashSet();
				int activeBodies = 0;
				while (listener.hasNext()) {
					MethodOrMethodContext next = listener.next();
					visited.add(next.method());
					if (next.method().hasActiveBody()) {
						activeBodies++;
					}
				}
				summarizedOutput("analysis-results", new Predicate<IAnalysisSeed>() {

					@Override
					public boolean test(IAnalysisSeed t) {
						return true;
					}

					@Override
					public String toString() {
						return "";
					}
				}, callGraphWatch.elapsed(TimeUnit.MILLISECONDS), visited.size(), activeBodies);
			}
		};
	}

	protected List<CryptSLRule> getRules() {
		List<CryptSLRule> rules = Lists.newArrayList();
		if(getRulesDirectory() == null){
			throw new RuntimeException("Please specify a directory the CrySL rules (.cryptslbin Files) are located in.");
		}
		File[] listFiles = new File(getRulesDirectory()).listFiles();
		for (File file : listFiles) {
			if (file != null && file.getName().endsWith(".cryptslbin")) {
				rules.add(CryptSLRuleReader.readFromFile(file));
			}
		}
		if (rules.isEmpty())
			System.out.println(
					"CogniCrypt did not find any rules to start the analysis for. \n It checked for rules in "
							+ getRulesDirectory());
		return rules;
	}

	protected abstract String getRulesDirectory();
	
	public CrySLAnalysisResultsAggregator getReporter(){
		if(reporter == null){
			reporter = new CrySLAnalysisResultsAggregator(null);
			reporter.addReportListener(new CogniCryptCLIReporter());
		}
		return reporter;
	}

	private void initializeSootWithEntryPointAllReachable(boolean wholeProgram) {
		G.v().reset();

		Options.v().set_whole_program(wholeProgram);

		switch (callGraphAlogrithm()) {
		case CHA:
			Options.v().setPhaseOption("cg.cha", "on");
			Options.v().setPhaseOption("cg", "all-reachable:true");
			break;
		case SPARK_LIBRARY:
			Options.v().setPhaseOption("cg.spark", "on");
			Options.v().setPhaseOption("cg", "all-reachable:true,library:any-subtype");
			break;
		case SPARK:
			Options.v().setPhaseOption("cg.spark", "on");
			Options.v().setPhaseOption("cg", "all-reachable:true");
			break;
		default:
			throw new RuntimeException("No call graph option selected!");
		}
		Options.v().set_output_format(Options.output_format_none);
		Options.v().set_no_bodies_for_excluded(true);
		Options.v().set_allow_phantom_refs(true);

		Options.v().set_prepend_classpath(true);
		Options.v().set_soot_classpath(sootClassPath() + File.pathSeparator + pathToJCE());
		Options.v().set_process_dir(Lists.newArrayList(applicationClassPath()));

		Scene.v().loadNecessaryClasses();
	}

	protected CG callGraphAlogrithm() {
		return CG.CHA;
	}

	protected abstract String sootClassPath();

	protected abstract String applicationClassPath();

	protected abstract String softwareIdentifier();

	private static String pathToJCE() {
		// When whole program mode is disabled, the classpath misses jce.jar
		return System.getProperty("java.home") + File.separator + "lib" + File.separator + "jce.jar";
	}

	private static final String CSV_SEPARATOR = ";";

	private void summarizedOutput(String fileName, Predicate<IAnalysisSeed> filter, long callGraphTime,
			int reachableMethodsCount, int activeBodies) {
		try {
			File dir = new File(fileName);
			if (!dir.exists()) {
				dir.mkdir();
			}
			File file = new File(fileName + "/" + softwareIdentifier() + filter.toString() + ".csv");
			boolean fileExisted = true;
			if (!file.exists()) {
				fileExisted = false;
			}
			FileWriter fileWriter = new FileWriter(file, true);
			if (!fileExisted) {
				List<String> line = Lists.newLinkedList();
				line.add("apk_name");
				line.add("analysisSeeds");
				line.add("forbiddenMethodErrors");
				line.add("typestateErrorTimeouts(seed)");
				line.add("typestateError(seed)");
				addRuleHeader("typestateError_", line);
				line.add("typestateErrorEndOfObjectLifetime(seed)");
				addRuleHeader("typestateErrorEndOfObjectLifetime_", line);
				line.add("typestateErrorTotal(seed)");
				addRuleHeader("typestateErrorTotal_", line);
				line.add("typestateError(unit)");
				// line.add("expectedPredicates");
				line.add("missingPredicates");
				addRuleHeader("missingPredicates_", line);
				line.add("checkedConstraints");
				line.add("internalConstraintViolations");
				addRuleHeader("internalConstraintViolations_", line);
				line.add("negationContradictions");
				line.add("callgraphTime(ms)");
				line.add("totalTime(ms)");
				line.add("typestateTime(ms)");
				line.add("typestateTime/NoBoomerang(ms)");
				line.add("taintTime(ms)");
				line.add("boomerangTime(ms)");
				line.add("constraint(ms)");
				line.add("predicate(ms)");
				line.add("visitedMethods");
				line.add("allMethods");
				line.add("allMethods_activeBodies");
				line.add("max_accesspath");
				fileWriter.write(Joiner.on(CSV_SEPARATOR).join(line) + "\n");
			}
			List<String> line = Lists.newLinkedList();
			line.add(softwareIdentifier());
			line.add(Integer.toString(subset(getReporter().getAnalysisSeeds(), filter).size()));
			line.add(Integer.toString(getReporter().getCallToForbiddenMethod().entries().size()));
			line.add(Integer.toString(subset(getReporter().getTypestateTimeouts(), filter).size()));
			line.add(Integer.toString(subset(getReporter().getTypestateErrors().keySet(), filter).size()));
			addTypestateDetails(line, getReporter().getTypestateErrors(), filter);
			line.add(Integer.toString(subset(getReporter().getTypestateErrorsEndOfLifecycle().keySet(), filter).size()));
			addTypestateDetails(line, getReporter().getTypestateErrorsEndOfLifecycle(), filter);

			Multimap<IAnalysisSeed, Statement> merged = HashMultimap.create(getReporter().getTypestateErrors());
			merged.putAll(getReporter().getTypestateErrorsEndOfLifecycle());

			line.add(Integer.toString(subset(merged.keySet(), filter).size()));
			addTypestateDetails(line, merged, filter);

			line.add(Integer.toString(subset(getReporter().getTypestateErrors().keySet(), filter).size()));

			// line.add(Integer.toString(getReporter().getExpectedPredicates().rowKeySet().size()));
			line.add(Integer.toString(subset(getReporter().getMissingPredicates().keySet(), filter).size()));
			addMissingPredicatesDetails(line, filter);
			line.add(Integer.toString(subset(getReporter().getCheckedConstraints().keySet(), filter).size()));
			line.add(Integer.toString(subset(getReporter().getInternalConstraintsViolations().keySet(), filter).size()));
			addMissingInternalConstraintDetails(line, filter);
			line.add(Integer.toString(getReporter().getPredicateContradictions().entries().size()));

			// Time reporting starts here
			line.add(Long.toString(callGraphTime));
			line.add(Long.toString(getReporter().getTotalAnalysisTime(TimeUnit.MILLISECONDS)));
			line.add(Long.toString(computeTime(getReporter().getTypestateAnalysisTime(), filter)));
			line.add(Long.toString(computeTime(getReporter().getTypestateAnalysisTime(), filter)
					- computeTime(getReporter().getBoomerangTime(), filter)));
			line.add(Long.toString(computeTime(getReporter().getTaintAnalysisTime(), filter)));
			line.add(Long.toString(computeTime(getReporter().getBoomerangTime(), filter)));
			line.add(Long.toString(computeTime(getReporter().getConstraintSolvingTime(), filter)));
			line.add(Long.toString(computeTime(getReporter().getPredicateSolvingTime(), filter)));
			line.add(Integer.toString(computeVisitedMethod(getReporter().getVisitedMethods(), filter).size()));
			line.add(Integer.toString(reachableMethodsCount));
			line.add(Integer.toString(activeBodies));
			fileWriter.write(Joiner.on(CSV_SEPARATOR).join(line) + "\n");
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Set<SootMethod> computeVisitedMethod(Multimap<IAnalysisSeed, SootMethod> map,
			Predicate<IAnalysisSeed> filter) {
		Set<? extends IAnalysisSeed> sub = subset(map.keySet(), filter);
		Set<SootMethod> res = Sets.newHashSet();
		for (IAnalysisSeed s : sub) {
			res.addAll(map.get(s));
		}
		return res;
	}

	private long computeTime(Multimap<IAnalysisSeed, Long> map, Predicate<IAnalysisSeed> filter) {
		Set<? extends IAnalysisSeed> sub = subset(map.keySet(), filter);
		long val = 0;
		for (IAnalysisSeed s : sub) {
			Collection<Long> collection = map.get(s);
			for (Long v : collection)
				val += v;
		}
		return val;
	}

	private Set<? extends IAnalysisSeed> subset(Collection<? extends IAnalysisSeed> analysisSeeds,
			Predicate<IAnalysisSeed> filter) {
		Set<IAnalysisSeed> filtered = Sets.newHashSet();
		for (IAnalysisSeed s : analysisSeeds)
			if (filter.test(s))
				filtered.add(s);
		return filtered;
	}

	private void addRuleHeader(String string, List<String> line) {
		List<CryptSLRule> rules = getRules();
		for (CryptSLRule r : rules) {
			line.add(string + r.getClassName());
		}
	}

	private void addMissingInternalConstraintDetails(List<String> line, Predicate<IAnalysisSeed> filter) {
		HashMap<String, Integer> classToInteger = new HashMap<>();
		Multimap<AnalysisSeedWithSpecification, ISLConstraint> map = getReporter().getInternalConstraintsViolations();
		for (AnalysisSeedWithSpecification seed : map.keySet()) {
			Collection<ISLConstraint> col = map.get(seed);
			if (col == null)
				continue;
			if (!filter.test(seed))
				continue;
			int size = col.size();
			String className = seed.getSpec().getRule().getClassName();
			Integer i = classToInteger.get(className);
			if (i == null || i == 0) {
				i = size;
			} else {
				i += size;
			}
			classToInteger.put(className, i);
		}
		List<CryptSLRule> rules = getRules();
		for (CryptSLRule r : rules) {
			Integer i = classToInteger.get(r.getClassName());
			if (i == null) {
				i = 0;
			}
			line.add(Integer.toString(i));
		}
	}

	private void addMissingPredicatesDetails(List<String> line, Predicate<IAnalysisSeed> filter) {
		HashMap<String, Integer> classToInteger = new HashMap<>();
		Multimap<AnalysisSeedWithSpecification, CryptSLPredicate> map = getReporter().getMissingPredicates();
		for (AnalysisSeedWithSpecification seed : map.keySet()) {
			Collection<CryptSLPredicate> col = map.get(seed);
			if (col == null)
				continue;
			if (!filter.test(seed))
				continue;
			int size = col.size();
			String className = seed.getSpec().getRule().getClassName();
			Integer i = classToInteger.get(className);
			if (i == null || i == 0) {
				i = size;
			} else {
				i += size;
			}
			classToInteger.put(className, i);
		}
		List<CryptSLRule> rules = getRules();
		for (CryptSLRule r : rules) {
			Integer i = classToInteger.get(r.getClassName());
			if (i == null) {
				i = 0;
			}
			line.add(Integer.toString(i));
		}
	}

	private void addTypestateDetails(List<String> line, Multimap<IAnalysisSeed, Statement> typestateErrors,
			Predicate<IAnalysisSeed> filter) {
		HashMap<String, Integer> classToInteger = new HashMap<>();
		for (IAnalysisSeed seed : typestateErrors.keySet()) {
			if (seed instanceof AnalysisSeedWithSpecification) {
				if (!filter.test(seed))
					continue;
				AnalysisSeedWithSpecification seedWithSpec = (AnalysisSeedWithSpecification) seed;
				String className = seedWithSpec.getSpec().getRule().getClassName();
				Integer i = classToInteger.get(className);
				if (i == null || i == 0) {
					i = 1;
				} else {
					i++;
				}
				classToInteger.put(className, i);
			}
		}
		List<CryptSLRule> rules = getRules();
		for (CryptSLRule r : rules) {
			Integer i = classToInteger.get(r.getClassName());
			if (i == null) {
				i = 0;
			}
			line.add(Integer.toString(i));
		}
	}
}
