package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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

import boomerang.debugger.Debugger;
import boomerang.jimple.Statement;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.CogniCryptCLIReporter;
import crypto.analysis.CrySLAnalysisResultsAggregator;
import crypto.analysis.CryptoScanner;
import crypto.analysis.IAnalysisSeed;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLRule;
import crypto.rules.CryptSLRuleReader;
import crypto.rules.StateNode;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.infoflow.android.TestApps.Test;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import soot.util.queue.QueueReader;
import typestate.TransitionFunction;
import typestate.interfaces.ISLConstraint;

public class PerAPKAnalyzer {

	private static List<String> relevantCalls = Lists.newLinkedList();
	private static FileWriter fout;
	private static boolean runCryptoScanner;
	private static boolean VISUALIZATION = false;
	private static Debugger<TransitionFunction> debugger = (VISUALIZATION ? null : new Debugger<TransitionFunction>());
	private static JimpleBasedInterproceduralCFG icfg;
	private static File ideVizFile;
	private static CrySLAnalysisResultsAggregator reporter;
	private static File apkFile;
	private static long analysisTime;
	private static long callGraphTime;
	private static int reachableMethodsCount;
	public final static String RESOURCE_PATH = "../CryptoAnalysis/src/test/resources/";
	private static final String CSV_SEPARATOR = ";";
	private static Set<PackageFilter> filters = Sets.newHashSet();

	private static void readInRelevantCalls() throws FileNotFoundException, IOException {
		String line;
		try (
			InputStream fis = new FileInputStream("RelevantCalls.txt"); InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
			BufferedReader br = new BufferedReader(isr);) {
			while ((line = br.readLine()) != null) {
				relevantCalls.add(line);
			}
		}
	}

	public static Debugger<TransitionFunction> getDebugger() {
		if (debugger == null) {
			if (!ideVizFile.getParentFile().exists()) {
				ideVizFile.getParentFile().mkdirs();
			}
//			debugger = new CryptoVizDebugger(ideVizFile, icfg);
		}
		return debugger;
	}

	public static void main(String... args) throws InterruptedException, IOException {
		readInRelevantCalls();
		apkFile = new File(args[0]);
		// TODO create dir if necessary.
		ideVizFile = new File("target/IDEViz/" + apkFile.getName().replace(".apk", ".viz"));
		Stopwatch callGraphWatch = Stopwatch.createStarted();

		try {
			Test.main(new String[] { args[0], args[1], "--notaintanalysis", "--callbackanalyzer", "FAST" });
		} catch (Exception e) {
			PrintWriter writer = new PrintWriter(new FileOutputStream(new File("CallGraphGenerationExceptions.txt"), true));
			writer.format("FlowDroid call graph generation crashed on %s", apkFile);
			e.printStackTrace(writer);
			writer.close();
			String folder = apkFile.getParent();
			String analyzedFolder = folder + File.separator + "flowdroid-crashed";
			File dir = new File(analyzedFolder);
			if (!dir.exists()) {
				dir.mkdir();
			}
			File moveTo = new File(dir.getAbsolutePath() + File.separator + apkFile.getName());
			Files.move(apkFile, moveTo);
			return;
		}
		callGraphTime = callGraphWatch.elapsed(TimeUnit.MILLISECONDS);
		ReachableMethods reachableMethods = Scene.v().getReachableMethods();
		QueueReader<MethodOrMethodContext> listener = reachableMethods.listener();
		fout = new FileWriter("Report.txt", true);

		try {
			log(0, "Analyzing " + apkFile.getName());
			Set<SootMethod> visited = Sets.newHashSet();
			while (listener.hasNext()) {
				MethodOrMethodContext next = listener.next();
				analyzeMethod(next.method());
				visited.add(next.method());
			}
			reachableMethodsCount = visited.size();
			log(1, "Call graph reachable methods: " + visited.size());
			log(1, "APK file reachable methods: " + visited.size());
			if (runCryptoScanner) {
				try {
					runCryptoAnalysis();
				} catch (Exception e) {
					PrintWriter writer = new PrintWriter(new FileOutputStream(new File("CryptoAnalysisExceptions.txt"), true));
					writer.format("CryptoAnalysis crashed on %s", apkFile);
					e.printStackTrace(writer);
					writer.close();
				}
			}
			String folder = apkFile.getParent();
			String analyzedFolder = folder + File.separator + "analyzed" + (runCryptoScanner ? "" : "-no-crypto");
			File dir = new File(analyzedFolder);
			if (!dir.exists()) {
				dir.mkdir();
			}
			File moveTo = new File(dir.getAbsolutePath() + File.separator + apkFile.getName());
			Files.move(apkFile, moveTo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			fout.close();
		}
	}

	protected static List<CryptSLRule> getRules() {
		LinkedList<CryptSLRule> rules = Lists.newLinkedList();

		File[] listFiles = new File(RESOURCE_PATH).listFiles();
		for (File file : listFiles) {
			if (file.getName().endsWith(".cryptslbin")) {
				rules.add(CryptSLRuleReader.readFromFile(file));
			}
		}
		return rules;
	}

	private static void runCryptoAnalysis() {
		icfg = new JimpleBasedInterproceduralCFG(false);
		reporter = new CrySLAnalysisResultsAggregator(apkFile);
		reporter.addReportListener(new CogniCryptCLIReporter());
		CryptoScanner scanner = new CryptoScanner(getRules()) {

			@Override
			public CrySLAnalysisResultsAggregator getAnalysisListener() {
				return reporter;
			}

//			@Override
//			public IDebugger<TypestateDomainValue<StateNode>> debugger() {
//				return getDebugger();
//			}

			@Override
			public boolean isCommandLineMode() {
				return true;
			}

			@Override
			public BiDiInterproceduralCFG<Unit, SootMethod> icfg() {
				return icfg;
			}

		};
		scanner.scan();
		detailedOutput();
		summarizedOutput(new PackageFilter("com.google."));
		summarizedOutput(new PackageFilter("com.unity3d."));
		summarizedOutput(new PackageFilter("com.facebook.ads."));
		summarizedOutput(new PackageFilter("com.android."));
		summarizedOutput(new Predicate<IAnalysisSeed>() {

			@Override
			public boolean test(IAnalysisSeed t) {
				return t.toString().contains("Cipher");
			}

			@Override
			public String toString() {
				return "Cipher";
			}

		});

		summarizedOutput(new Predicate<IAnalysisSeed>() {

			@Override
			public boolean test(IAnalysisSeed t) {
				for (PackageFilter f : filters)
					if (f.test(t))
						return false;
				return true;
			}

			@Override
			public String toString() {
				return "Complement";
			}
		});
		summarizedOutput(new Predicate<IAnalysisSeed>() {

			@Override
			public boolean test(IAnalysisSeed t) {
				return true;
			}

			@Override
			public String toString() {
				return "AllSeeds";
			}
		});
	}

	private static class PackageFilter implements Predicate<IAnalysisSeed> {

		private String filterString;

		public PackageFilter(String filterString) {
			this.filterString = filterString;
			filters.add(this);
		}

		@Override
		public boolean test(IAnalysisSeed t) {
			return t.getMethod().getDeclaringClass().toString().contains(filterString);
		}

		@Override
		public String toString() {
			return filterString;
		}
	}

	private static String getSummaryFile() {
		String property = System.getProperty("SummaryFile");
		if (property != null)
			return property;
		return "summary-report";
	}

	private static void summarizedOutput(Predicate<IAnalysisSeed> filter) {
		try {
			File file = new File(getSummaryFile() + filter.toString() + ".csv");
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
				line.add("max_accesspath");
				fileWriter.write(Joiner.on(CSV_SEPARATOR).join(line) + "\n");
			}
			List<String> line = Lists.newLinkedList();
			line.add(apkFile.getName());
			line.add(Integer.toString(subset(reporter.getAnalysisSeeds(), filter).size()));
			line.add(Integer.toString(reporter.getCallToForbiddenMethod().entries().size()));
			line.add(Integer.toString(subset(reporter.getTypestateTimeouts(), filter).size()));
			line.add(Integer.toString(subset(reporter.getTypestateErrors().keySet(), filter).size()));
			addTypestateDetails(line, reporter.getTypestateErrors(),filter);
			line.add(Integer.toString(subset(reporter.getTypestateErrorsEndOfLifecycle().keySet(), filter).size()));
			addTypestateDetails(line,reporter.getTypestateErrorsEndOfLifecycle(), filter);
			
			Multimap<IAnalysisSeed, Statement> merged = HashMultimap.create(reporter.getTypestateErrors());
			merged.putAll(reporter.getTypestateErrorsEndOfLifecycle());
			
			line.add(Integer.toString(subset(merged.keySet(), filter).size()));
			addTypestateDetails(line,merged, filter);
			
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
			line.add(Long.toString(computeTime(reporter.getTypestateAnalysisTime(), filter) - computeTime(reporter.getBoomerangTime(), filter)));
			line.add(Long.toString(computeTime(reporter.getTaintAnalysisTime(), filter)));
			line.add(Long.toString(computeTime(reporter.getBoomerangTime(), filter)));
			line.add(Long.toString(computeTime(reporter.getConstraintSolvingTime(), filter)));
			line.add(Long.toString(computeTime(reporter.getPredicateSolvingTime(), filter)));
			line.add(Integer.toString(computeVisitedMethod(reporter.getVisitedMethods(), filter).size()));
			line.add(Integer.toString(reachableMethodsCount));
			fileWriter.write(Joiner.on(CSV_SEPARATOR).join(line) + "\n");
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	private static Set<SootMethod> computeVisitedMethod(Multimap<IAnalysisSeed, SootMethod> map, Predicate<IAnalysisSeed> filter) {
		Set<? extends IAnalysisSeed> sub = subset(map.keySet(), filter);
		Set<SootMethod> res = Sets.newHashSet();
		for (IAnalysisSeed s : sub) {
			res.addAll(map.get(s));
		}
		return res;
	}
	private static Set<? extends IAnalysisSeed> subset(Collection<? extends IAnalysisSeed> analysisSeeds, Predicate<IAnalysisSeed> filter) {
		Set<IAnalysisSeed> filtered = Sets.newHashSet();
		for (IAnalysisSeed s : analysisSeeds)
			if (filter.test(s))
				filtered.add(s);
		return filtered;
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

	private static void addTypestateDetails(List<String> line,Multimap<IAnalysisSeed, Statement> typestateErrors, Predicate<IAnalysisSeed> filter) {
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

	private static void addRuleHeader(String string, List<String> line) {
		List<CryptSLRule> rules = getRules();
		for (CryptSLRule r : rules) {
			line.add(string + r.getClassName());
		}

	}

	private static void detailedOutput() {
		File file = new File("target/reports/cognicrypt/" + apkFile.getName().replace(".apk", ".txt"));
		file.getParentFile().mkdirs();
		try {
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(reporter.toString());
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void log(int i, String string) throws IOException {
		String s = "";
		for (int j = 0; j < i; j++) {
			s += "\t";
		}
		fout.write(s + string + " \n");
	}

	private static void analyzeMethod(SootMethod method) throws IOException {
		if (!method.hasActiveBody())
			return;
		for (Unit u : method.getActiveBody().getUnits()) {
			if (!(u instanceof Stmt))
				continue;

			Stmt stmt = (Stmt) u;
			if (!stmt.containsInvokeExpr())
				continue;
			InvokeExpr invokeExpr = stmt.getInvokeExpr();
			if (invokeExpr == null)
				continue;
			SootMethod calledMethod = invokeExpr.getMethod();
			if (calledMethod == null)
				continue;
			for (String relevantCall : relevantCalls) {
				if (calledMethod.toString().contains(relevantCall)) {
					runCryptoScanner = true;
				}
			}
		}
	}
}
