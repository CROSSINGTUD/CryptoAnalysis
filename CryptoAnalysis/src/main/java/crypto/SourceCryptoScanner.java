package crypto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
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
import com.google.common.io.Files;

import boomerang.jimple.Statement;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.CogniCryptCLIReporter;
import crypto.analysis.CrySLAnalysisResultsAggregator;
import crypto.analysis.CryptoScanner;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.ICrySLResultsListener;
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
import soot.SootClass;
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

public class SourceCryptoScanner {
	public static String RESOURCE_PATH = "rules";
	private static CrySLAnalysisResultsAggregator reporter;
	private static JimpleBasedInterproceduralCFG icfg;
	private static CG callGraphAlogrithm = CG.SPARK;
	private static Stopwatch callGraphWatch;
	private static String SOFTWARE_IDENTIFIER;

	private static enum CG {
		CHA, SPARK_LIBRARY, SPARK
	}

	public static void main(String... args) {
		SOFTWARE_IDENTIFIER = args[0];
		if (args.length > 3)
			RESOURCE_PATH = args[3];
		if (args.length > 4) {
			if (args[4].equalsIgnoreCase("cha")) {
				callGraphAlogrithm = CG.CHA;
			}
			if (args[4].equalsIgnoreCase("spark")) {
				callGraphAlogrithm = CG.SPARK;
			}
			if (args[4].equalsIgnoreCase("spark-library")) {
				callGraphAlogrithm = CG.SPARK_LIBRARY;
			}
			if (args[4].equalsIgnoreCase("library")) {
				callGraphAlogrithm = CG.SPARK_LIBRARY;
			}
		}
		callGraphWatch = Stopwatch.createStarted();
		initializeSootWithEntryPointAllReachable(args[1], args[2], false);
		if (checkIfUsesObject()) {
			System.out.println("Using call graph algorithm " + callGraphAlogrithm);
			initializeSootWithEntryPointAllReachable(args[1], args[2], true);
			analyse();
		}
	}

	private static boolean checkIfUsesObject() {
		
		final SeedFactory seedFactory = new SeedFactory(getRules());
		PackManager.v().getPack("jap").add(new Transform("jap.myTransform", new BodyTransformer() {
			protected void internalTransform(Body body, String phase, Map options) {
				if(!body.getMethod().getDeclaringClass().isApplicationClass()){
					return;
				}
				for (Unit u : body.getUnits()) {
					seedFactory.generate(body.getMethod(),u);
				}
			}
		}));
		PhaseOptions.v().setPhaseOption("jap.npc", "on");
		PackManager.v().runPacks();
		return seedFactory.hasSeeds();
	}

	public static void runAnalysis(String cp, String mainClass, String resPath) {
		initializeSootWithEntryPoint(cp, mainClass);
		RESOURCE_PATH = resPath;
		analyse();
	}

	public static void runAnalysisAllReachable(String applicationClasses, String cp, String resPath) {
		initializeSootWithEntryPointAllReachable(applicationClasses, cp, true);
		RESOURCE_PATH = resPath;
		analyse();
	}

	private static void analyse() {
		// PackManager.v().getPack("wjtp").add(new Transform("wjtp.prepare", new
		// PreparationTransformer()));
		Transform transform = new Transform("wjtp.ifds", createAnalysisTransformer());
		PackManager.v().getPack("wjtp").add(transform);
		PackManager.v().getPack("cg").apply();
		PackManager.v().getPack("wjtp").apply();
	}

	protected static String processDir;

	private static Transformer createAnalysisTransformer() {
		return new SceneTransformer() {

			@Override
			protected void internalTransform(String phaseName, Map<String, String> options) {
				icfg = new JimpleBasedInterproceduralCFG(false);
				reporter = new CrySLAnalysisResultsAggregator(null);
				reporter.addReportListener(new CogniCryptCLIReporter());
				CryptoScanner scanner = new CryptoScanner(getRules()) {

					@Override
					public BiDiInterproceduralCFG<Unit, SootMethod> icfg() {
						return icfg;
					}

					@Override
					public CrySLAnalysisResultsAggregator getAnalysisListener() {
						return reporter;
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
				System.out.println(reporter);

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

	protected static List<CryptSLRule> getRules() {
		List<CryptSLRule> rules = Lists.newArrayList();

		File[] listFiles = new File(RESOURCE_PATH).listFiles();
		for (File file : listFiles) {
			if (file.getName().endsWith(".cryptslbin")) {
				rules.add(CryptSLRuleReader.readFromFile(file));
			}
		}
		return rules;
	}

	private static void initializeSootWithEntryPoint(String sootClassPath, String mainClass) {
		G.v().reset();
		Options.v().set_whole_program(true);
		Options.v().setPhaseOption("cg.spark", "on");
		Options.v().set_output_format(Options.output_format_none);
		Options.v().set_no_bodies_for_excluded(true);
		Options.v().set_allow_phantom_refs(true);
		// Options.v().setPhaseOption("cg", "all-reachable:true");

		Options.v().set_prepend_classpath(true);
		Options.v().set_soot_classpath(sootClassPath);
		Options.v().set_main_class(mainClass);

		Scene.v().addBasicClass(mainClass, SootClass.BODIES);
		Scene.v().loadNecessaryClasses();
		SootClass c = Scene.v().forceResolve(mainClass, SootClass.BODIES);
		if (c != null) {
			c.setApplicationClass();
		}
		SootMethod methodByName = c.getMethodByName("main");
		List<SootMethod> ePoints = new LinkedList<>();
		ePoints.add(methodByName);
		Scene.v().setEntryPoints(ePoints);
	}

	private static void initializeSootWithEntryPointAllReachable(String applicationClasses, String sootClassPath, boolean wholeProgram) {
		processDir = applicationClasses;
		G.v().reset();
		
		Options.v().set_whole_program(wholeProgram);
		
		switch (callGraphAlogrithm) {
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
		// Options.v().setPhaseOption("cg", "all-reachable:true,apponly:true");

		Options.v().set_prepend_classpath(true);
		Options.v().set_soot_classpath(sootClassPath);
		Options.v().set_process_dir(Lists.newArrayList(applicationClasses));

		Scene.v().loadNecessaryClasses();
	}

	private static final String CSV_SEPARATOR = ";";

	private static void summarizedOutput(String fileName, Predicate<IAnalysisSeed> filter, long callGraphTime,
			int reachableMethodsCount, int activeBodies) {
		try {
			File dir = new File(fileName);
			if (!dir.exists()) {
				dir.mkdir();
			}
			File file = new File(fileName + "/" + SOFTWARE_IDENTIFIER + filter.toString() + ".csv");
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
			line.add(SOFTWARE_IDENTIFIER);
			line.add(Integer.toString(subset(reporter.getAnalysisSeeds(), filter).size()));
			line.add(Integer.toString(reporter.getCallToForbiddenMethod().entries().size()));
			line.add(Integer.toString(subset(reporter.getTypestateTimeouts(), filter).size()));
			line.add(Integer.toString(subset(reporter.getTypestateErrors().keySet(), filter).size()));
			addTypestateDetails(line, reporter.getTypestateErrors(), filter);
			line.add(Integer.toString(subset(reporter.getTypestateErrorsEndOfLifecycle().keySet(), filter).size()));
			addTypestateDetails(line, reporter.getTypestateErrorsEndOfLifecycle(), filter);

			Multimap<IAnalysisSeed, Statement> merged = HashMultimap.create(reporter.getTypestateErrors());
			merged.putAll(reporter.getTypestateErrorsEndOfLifecycle());

			line.add(Integer.toString(subset(merged.keySet(), filter).size()));
			addTypestateDetails(line, merged, filter);

			line.add(Integer.toString(subset(reporter.getTypestateErrors().keySet(), filter).size()));

			// line.add(Integer.toString(reporter.getExpectedPredicates().rowKeySet().size()));
			line.add(Integer.toString(subset(reporter.getMissingPredicates().keySet(), filter).size()));
			addMissingPredicatesDetails(line, filter);
			line.add(Integer.toString(subset(reporter.getCheckedConstraints().keySet(), filter).size()));
			line.add(Integer.toString(subset(reporter.getInternalConstraintsViolations().keySet(), filter).size()));
			addMissingInternalConstraintDetails(line, filter);
			line.add(Integer.toString(reporter.getPredicateContradictions().entries().size()));

			// Time reporting starts here
			line.add(Long.toString(callGraphTime));
			line.add(Long.toString(reporter.getTotalAnalysisTime(TimeUnit.MILLISECONDS)));
			line.add(Long.toString(computeTime(reporter.getTypestateAnalysisTime(), filter)));
			line.add(Long.toString(computeTime(reporter.getTypestateAnalysisTime(), filter)
					- computeTime(reporter.getBoomerangTime(), filter)));
			line.add(Long.toString(computeTime(reporter.getTaintAnalysisTime(), filter)));
			line.add(Long.toString(computeTime(reporter.getBoomerangTime(), filter)));
			line.add(Long.toString(computeTime(reporter.getConstraintSolvingTime(), filter)));
			line.add(Long.toString(computeTime(reporter.getPredicateSolvingTime(), filter)));
			line.add(Integer.toString(computeVisitedMethod(reporter.getVisitedMethods(), filter).size()));
			line.add(Integer.toString(reachableMethodsCount));
			line.add(Integer.toString(activeBodies));
			fileWriter.write(Joiner.on(CSV_SEPARATOR).join(line) + "\n");
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Set<SootMethod> computeVisitedMethod(Multimap<IAnalysisSeed, SootMethod> map,
			Predicate<IAnalysisSeed> filter) {
		Set<? extends IAnalysisSeed> sub = subset(map.keySet(), filter);
		Set<SootMethod> res = Sets.newHashSet();
		for (IAnalysisSeed s : sub) {
			res.addAll(map.get(s));
		}
		return res;
	}

	private static long computeTime(Multimap<IAnalysisSeed, Long> map, Predicate<IAnalysisSeed> filter) {
		Set<? extends IAnalysisSeed> sub = subset(map.keySet(), filter);
		long val = 0;
		for (IAnalysisSeed s : sub) {
			Collection<Long> collection = map.get(s);
			for (Long v : collection)
				val += v;
		}
		return val;
	}

	private static Set<? extends IAnalysisSeed> subset(Collection<? extends IAnalysisSeed> analysisSeeds,
			Predicate<IAnalysisSeed> filter) {
		Set<IAnalysisSeed> filtered = Sets.newHashSet();
		for (IAnalysisSeed s : analysisSeeds)
			if (filter.test(s))
				filtered.add(s);
		return filtered;
	}

	private static void addRuleHeader(String string, List<String> line) {
		List<CryptSLRule> rules = getRules();
		for (CryptSLRule r : rules) {
			line.add(string + r.getClassName());
		}
	}

	private static void addMissingInternalConstraintDetails(List<String> line, Predicate<IAnalysisSeed> filter) {
		HashMap<String, Integer> classToInteger = new HashMap<>();
		Multimap<AnalysisSeedWithSpecification, ISLConstraint> map = reporter.getInternalConstraintsViolations();
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

	private static void addMissingPredicatesDetails(List<String> line, Predicate<IAnalysisSeed> filter) {
		HashMap<String, Integer> classToInteger = new HashMap<>();
		Multimap<AnalysisSeedWithSpecification, CryptSLPredicate> map = reporter.getMissingPredicates();
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

	private static void addTypestateDetails(List<String> line, Multimap<IAnalysisSeed, Statement> typestateErrors,
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
