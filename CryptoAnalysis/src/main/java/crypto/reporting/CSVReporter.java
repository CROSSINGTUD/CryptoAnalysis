package crypto.reporting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

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
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.ForbiddenMethodError;
import crypto.analysis.errors.HardCodedError;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.NeverTypeOfError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import crypto.exceptions.CryptoAnalysisException;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractedValue;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CrySLPredicate;
import crypto.rules.CrySLRule;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.util.queue.QueueReader;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;

public class CSVReporter extends CrySLAnalysisListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CSVReporter.class);
	
	private static final String CSV_SEPARATOR = ";";
	private Set<AbstractError> errors = Sets.newHashSet();
	private int seeds;
	private List<String> headers = Lists.newArrayList();
	private Map<String,String> headersToValues = Maps.newHashMap();
	private List<CrySLRule> rules;
	private Set<SootMethod> dataflowReachableMethods = Sets.newHashSet();
	private Stopwatch analysisTime = Stopwatch.createUnstarted();
	private String csvReportFileName;
	private enum Headers{
		SoftwareID,SeedObjectCount,CallGraphTime_ms,CryptoAnalysisTime_ms,CallGraphReachableMethods,
		CallGraphReachableMethods_ActiveBodies,DataflowVisitedMethod
	}

	public CSVReporter(String csvReportFileName, String softwareId,  List<CrySLRule> rules, long callGraphConstructionTime) {
		this.csvReportFileName = csvReportFileName;
		this.rules = rules;
		ReachableMethods reachableMethods = Scene.v().getReachableMethods();
		QueueReader<MethodOrMethodContext> listener = reachableMethods.listener();
		Set<SootMethod> visited = Sets.newHashSet();
		int callgraphReachableMethodsWithActiveBodies = 0;
		while (listener.hasNext()) {
			MethodOrMethodContext next = listener.next();
			visited.add(next.method());
			if (next.method().hasActiveBody()) {
				callgraphReachableMethodsWithActiveBodies++;
			}
		}
		int callgraphReachableMethods = visited.size();
		for(Headers h : Headers.values()){
			headers.add(h.toString());
		}
		put(Headers.SoftwareID,softwareId);
		put(Headers.CallGraphTime_ms,callGraphConstructionTime);
		put(Headers.CallGraphReachableMethods,callgraphReachableMethods);
		put(Headers.CallGraphReachableMethods_ActiveBodies,callgraphReachableMethodsWithActiveBodies);
		addDynamicHeader(ConstraintError.class.getSimpleName());
		addDynamicHeader(NeverTypeOfError.class.getSimpleName());
		addDynamicHeader(HardCodedError.class.getSimpleName());
		addDynamicHeader(TypestateError.class.getSimpleName());
		addDynamicHeader(RequiredPredicateError.class.getSimpleName());
		addDynamicHeader(IncompleteOperationError.class.getSimpleName());
		addDynamicHeader(ImpreciseValueExtractionError.class.getSimpleName());
		addDynamicHeader(ForbiddenMethodError.class.getSimpleName());
	}
	
	private void addDynamicHeader(String name) {
		headers.add(name+"_sum");
		for(CrySLRule r : rules){
			headers.add(name+"_"+r.getClassName());
		}
	}

	@Override
	public void beforeAnalysis() {
		analysisTime.start();
	}

	@Override
	public void afterAnalysis() {
		analysisTime.stop();
		put(Headers.DataflowVisitedMethod, dataflowReachableMethods.size());
		put(Headers.CryptoAnalysisTime_ms, analysisTime.elapsed(TimeUnit.MILLISECONDS));
		put(Headers.SeedObjectCount, seeds);
		
		Table<Class, CrySLRule, Integer> errorTable = HashBasedTable.create(); 
		for(AbstractError err : errors){
			Integer integer = errorTable.get(err.getClass(), err.getRule());
			if(integer == null){
				integer = 0;
			}
			integer++;
			errorTable.put(err.getClass(), err.getRule(),integer);
		}


		for(Cell<Class, CrySLRule, Integer> c : errorTable.cellSet()){
			put(c.getRowKey().getSimpleName() + "_" + c.getColumnKey().getClassName(), c.getValue());
		}
		
		Map<Class, Integer> errorsAccumulated = Maps.newHashMap(); 
		for(Cell<Class, CrySLRule, Integer> c : errorTable.cellSet()){
			Integer integer = errorsAccumulated.get(c.getRowKey());	
			if(integer == null){
				integer = 0;
			}
			integer += c.getValue();
			errorsAccumulated.put(c.getRowKey(),integer);
		}

		for(Entry<Class, Integer> c : errorsAccumulated.entrySet()){
			put(c.getKey().getSimpleName() + "_sum", c.getValue());
		}
		
		writeToFile();
	}

	private void writeToFile() {
		try {
			File reportFile = new File(csvReportFileName).getAbsoluteFile();
			if (!reportFile.getParentFile().exists()) {
				try {
					Files.createDirectories(reportFile.getParentFile().toPath());
				} catch (IOException e) {
					throw new CryptoAnalysisException("Was not able to create directories for IDEViz output for given directory: "+reportFile.getAbsolutePath());
				}
			}
			boolean fileExisted = reportFile.exists();
			FileWriter writer = new FileWriter(reportFile, true);
			if (!fileExisted) {
				writer.write(Joiner.on(CSV_SEPARATOR).join(headers) + "\n");
			}
			List<String> line = Lists.newArrayList();
			for(String h : headers){
				String string = headersToValues.get(h);
				if(string == null){
					string = "";
				}
				line.add(string);
			}
			writer.write(Joiner.on(CSV_SEPARATOR).join(line) + "\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void put(String key, Object val) {
		if (!headers.contains(key)) {
			LOGGER.error("Did not create a header to this value " + key);
		} else {
			headersToValues.put(key, val.toString());
		}
	}
	private void put(Headers key, Object val) {
		put(key.toString(),val);
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void boomerangQueryStarted(Query seed, BackwardQuery q) {
	}

	@Override
	public void boomerangQueryFinished(Query seed, BackwardQuery q) {
		
	}

	@Override
	public void reportError(AbstractError error) {
		errors.add(error);
	}

	@Override
	public void ensuredPredicates(Table<Statement, Val, Set<EnsuredCrySLPredicate>> existingPredicates,
			Table<Statement, IAnalysisSeed, Set<CrySLPredicate>> expectedPredicates,
			Table<Statement, IAnalysisSeed, Set<CrySLPredicate>> missingPredicates) {
		
	}

	@Override
	public void checkedConstraints(AnalysisSeedWithSpecification analysisSeedWithSpecification,
			Collection<ISLConstraint> relConstraints) {
	}

	@Override
	public void onSeedTimeout(Node<Statement, Val> seed) {
		
	}

	@Override
	public void onSeedFinished(IAnalysisSeed seed, ForwardBoomerangResults<TransitionFunction> forwardResults) {
		dataflowReachableMethods.addAll(forwardResults.getStats().getCallVisitedMethods());
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addProgress(int processedSeeds, int workListsize) {
		// TODO Auto-generated method stub
		
	}

}
