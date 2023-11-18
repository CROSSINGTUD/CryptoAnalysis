package crypto.reporting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Joiner;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.ForbiddenMethodError;
import crypto.analysis.errors.HardCodedError;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.NeverTypeOfError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import crypto.rules.CrySLRule;

/**
 * This class extends the class {@link Reporter} by generating a summary of the analysis and write it into a
 * csv file. Compared to the {@link CSVReporter} this reporter will not output any information about the concrete
 * errors found in the analysis. The summary will only contain the number of error types and in which classes from
 * the rule set the errors were found.
 */
public class CSVSummaryReporter extends Reporter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CSVSummaryReporter.class);
	
	private static final String CSV_SEPARATOR = ";";
	private Set<AbstractError> errors = Sets.newHashSet();
	private List<String> headers = Lists.newArrayList();
	private Map<String,String> headersToValues = Maps.newHashMap();
	
	/** Name of the analysis report */
	private static final String REPORT_NAME = "CryptoAnalysis-Report-Summary.csv";
	
	/** The headers of CSV report */
	private enum Headers {
		SoftwareID, SeedObjectCount, CryptoAnalysisTime_ms, CallGraphTime_ms, CallGraphReachableMethods,
		CallGraphReachableMethods_ActiveBodies, DataflowVisitedMethod
	}

	/**
	 * Subclass of {@link Reporter}. Creates an instance of {@link CSVSummaryReporter}, which
	 * can be used to create a csv file containing a summary of the analysis.
	 * 
	 * @param reportDir A {@link String} path giving the location of the report directory.
	 *                  The reportPath should end without an ending file separator.
	 * @param softwareId A {@link String} for the analyzed software.
	 * @param rules A {@link List} of {@link CrySLRule} containing the rules the program is analyzed with.
	 * @param callGraphConstructionTime The time in milliseconds for the construction of the callgraph.
	 * @param includeStatistics Set this value to true, if the analysis report should contain some
	 *                          analysis statistics (e.g. the callgraph construction time). If this value is set
	 *                          to false, no statistics will be output. 
	 */
	public CSVSummaryReporter(String reportDir, String softwareId,  List<CrySLRule> rules, long callGraphConstructionTime, boolean includeStatistics) {
		super((reportDir != null ? new File(reportDir) : new File(System.getProperty("user.dir"))), softwareId, rules, callGraphConstructionTime, includeStatistics);
		
		// include statistics only if wanted
		if (includeStatistics()) {
			// Create headers for the statistics
			for (Headers h : Headers.values()) {
				headers.add(h.toString());
			}
			
			put(Headers.SoftwareID, getStatistics().getSoftwareID());
			put(Headers.CallGraphTime_ms, getStatistics().getCallgraphTime());
			put(Headers.CallGraphReachableMethods, getStatistics().getCallgraphReachableMethods());
			put(Headers.CallGraphReachableMethods_ActiveBodies, getStatistics().getCallgraphReachableMethodsWithActiveBodies());
		}
		
		// Create headers for the errors depending on the used set of rules
		addDynamicHeader(ConstraintError.class.getSimpleName());
		addDynamicHeader(NeverTypeOfError.class.getSimpleName());
		addDynamicHeader(HardCodedError.class.getSimpleName());
		addDynamicHeader(TypestateError.class.getSimpleName());
		addDynamicHeader(RequiredPredicateError.class.getSimpleName());
		addDynamicHeader(IncompleteOperationError.class.getSimpleName());
		addDynamicHeader(ImpreciseValueExtractionError.class.getSimpleName());
		addDynamicHeader(ForbiddenMethodError.class.getSimpleName());
	}
	
	/**
	 * Create headers for all specified rules with the format <error class>_<class of rule>
	 * (e.g. ConstraintError_java.security.AlgorithmParameterGenerator).
	 * 
	 * @param name Name of the error class
	 */
	private void addDynamicHeader(String name) {
		headers.add(name + "_sum");
		
		for (CrySLRule r : getRules()) {
			headers.add(name + "_" + r.getClassName());
		}
	}
	
	private void put(Headers key, Object val) {
		put(key.toString(), val);
	}
	
	private void put(String key, Object val) {
		if (!headers.contains(key)) {
			LOGGER.error("Did not create a header to this value " + key);
		} else {
			if (val == null) {
				LOGGER.info(key + " is null");
			} else {
				headersToValues.put(key, val.toString());
			}
		}
	}

	@Override
	public void handleAnalysisResults() {
		if (includeStatistics()) {
			put(Headers.DataflowVisitedMethod, getStatistics().getDataflowVisitedMethods());
			put(Headers.CryptoAnalysisTime_ms, getStatistics().getAnalysisTime());
			put(Headers.SeedObjectCount, getStatistics().getSeedObjectCount());
		}
		
		// Count the number of each error class
		Table<Class<?>, CrySLRule, Integer> errorTable = HashBasedTable.create(); 
		
		for (AbstractError err : errors) {
			Integer integer = errorTable.get(err.getClass(), err.getRule());
			
			if(integer == null) {
				integer = 0;
			}
			
			integer++;
			errorTable.put(err.getClass(), err.getRule(), integer);
		}

		// Set the corresponding error headers to the number of occurred errors
		for (Cell<Class<?>, CrySLRule, Integer> c : errorTable.cellSet()) {
			put(c.getRowKey().getSimpleName() + "_" + c.getColumnKey().getClassName(), c.getValue());
		}
		
		Map<Class<?>, Integer> errorsAccumulated = Maps.newHashMap();
		
		for (Cell<Class<?>, CrySLRule, Integer> c : errorTable.cellSet()) {
			Integer integer = errorsAccumulated.get(c.getRowKey());
			
			if(integer == null) {
				integer = 0;
			}
			
			integer += c.getValue();
			errorsAccumulated.put(c.getRowKey(), integer);
		}

		for (Entry<Class<?>, Integer> c : errorsAccumulated.entrySet()) {
			put(c.getKey().getSimpleName() + "_sum", c.getValue());
		}
		
		writeToFile();
	}

	private void writeToFile() {
		try {
			FileWriter writer = new FileWriter(getOutputFolder() + File.separator + REPORT_NAME);
			writer.write(Joiner.on(CSV_SEPARATOR).join(headers) + "\n");
			
			List<String> line = Lists.newArrayList();
			
			for (String h : headers) {
				String string = headersToValues.get(h);
				
				if (string == null) {
					string = "";
				}
				
				line.add(string);
			}
			
			writer.write(Joiner.on(CSV_SEPARATOR).join(line) + "\n");
			writer.write("\n" + SARIFConfig.ANALYSISTOOL_NAME_VALUE + "\n");
			
			String version = getClass().getPackage().getImplementationVersion();
			
			if (version == null) {
				version = "Version is not known";
			}
			
			writer.write(version);
			writer.close();
			LOGGER.info("CSV Report generated to file : " + getOutputFolder().getAbsolutePath() + File.separator + REPORT_NAME);
		} catch (IOException e) {
			LOGGER.error("Could not write to " + getOutputFolder().getAbsolutePath() + File.separator + REPORT_NAME, e);
		}
	}
	
	@Override
	public void reportError(AbstractError error) {
		errors.add(error);
	}

}
