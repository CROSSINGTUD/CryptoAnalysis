package crypto.reporting;

import java.io.File;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import boomerang.scene.Method;
import boomerang.scene.WrappedClass;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import crypto.analysis.errors.AbstractError;
import crypto.rules.CrySLRule;


/**
 *  This class extends the class {@link Reporter} by generating an analysis report and write it into a
 *  JSON file in the SARIF format.
 */
@SuppressWarnings("unchecked")
public class SARIFReporter extends Reporter {

	private static final Logger LOGGER = LoggerFactory.getLogger(SARIFReporter.class);
	
	private JSONObject files = new JSONObject();
	private JSONObject resources = new JSONObject();
	private JSONObject rules = new JSONObject();
	private JSONArray results = new JSONArray();
	private SARIFHelper sarifHelper;
	private Map<String, Integer> errorCountMap;
	/** name of the analysis report */
	private static final String REPORT_NAME = "CryptoAnalysis-Report.json";

	/**
	 * Subclass of {@link Reporter}. Creates an instance of {@link SARIFReporter}, which
	 * can be used to create a json file containing the analysis report in the SARIF format.
	 * 
	 * @param reportDir A {@link String} path giving the location of the report directory.
	 *                  The reportPath should end without an ending file separator.
	 * @param softwareId A {@link String} for the analyzed software.
	 * @param rules A {@link List} of {@link CrySLRule} containing the rules the program is analyzed with.
	 * @param callgraphConstructionTime The time in milliseconds for the construction of the callgraph.
	 * @param includeStatistics Set this value to true, if the analysis report should contain some
	 *                          analysis statistics (e.g. the callgraph construction time). If this value is set
	 *                          to false, no statistics will be output. 
	 */
	public SARIFReporter(String reportDir, String softwareId, List<CrySLRule> rules, long callgraphConstructionTime, boolean includeStatistics) {
		super((reportDir != null ? new File(reportDir) : new File(System.getProperty("user.dir"))), softwareId, rules, callgraphConstructionTime, includeStatistics);
		
		this.sarifHelper = new SARIFHelper();
		this.errorCountMap = new HashMap<String, Integer>();
		initializeMap();
	}
	
	private void initializeMap() {
		this.errorCountMap.put(SARIFConfig.CONSTRAINT_ERROR_KEY, 0);
		this.errorCountMap.put(SARIFConfig.NEVER_TYPE_OF_ERROR_KEY, 0);
		this.errorCountMap.put(SARIFConfig.HARDCODED_ERROR_KEY, 0);
		this.errorCountMap.put(SARIFConfig.INSTANCE_OF_ERROR_KEY, 0);
		this.errorCountMap.put(SARIFConfig.FORBIDDEN_METHOD_ERROR_KEY, 0);
		this.errorCountMap.put(SARIFConfig.IMPRECISE_VALUE_EXTRACTION_ERROR_KEY, 0);
		this.errorCountMap.put(SARIFConfig.TYPE_STATE_ERROR_KEY, 0);
		this.errorCountMap.put(SARIFConfig.REQUIRED_PREDICATE_ERROR_KEY, 0);
		this.errorCountMap.put(SARIFConfig.INCOMPLETE_OPERATION_ERROR_KEY, 0);
		this.errorCountMap.put(SARIFConfig.UNCAUGHT_EXCEPTION_ERROR_KEY, 0);
	}

	private void addFile(WrappedClass c) {
		String filePath = this.sarifHelper.getFileName(c);
		JSONObject mimeType = new JSONObject();
		mimeType.put(SARIFConfig.MIME_TYPE_KEY, SARIFConfig.MIME_TYPE_VALUE);
		this.files.put(filePath, mimeType);
	}

	private String addRules(String errorType) {
		if (this.rules.containsKey(errorType)) {
			int count = this.errorCountMap.get(errorType);
			this.errorCountMap.put(errorType, count + 1);
			JSONObject ruleInfo = new JSONObject();
			JSONObject fullDescription = new JSONObject();
			fullDescription.put(SARIFConfig.TEXT_KEY, this.sarifHelper.getRuleDescription(errorType));
			ruleInfo.put(SARIFConfig.RULES_ID_KEY, errorType);
			ruleInfo.put(SARIFConfig.FULL_DESCRIPTION_KEY, fullDescription);
			if (count == 0) {
				this.rules.put(errorType, ruleInfo);
			}
		}
		return errorType;
	}

	private void addResults(String errorType, WrappedClass c, String methodName, int lineNumber, String method, String statement, String text,
			String richText) {
		JSONObject result = new JSONObject();
		String finalErrorType = addRules(errorType);
		
		result.put(SARIFConfig.RULE_ID_KEY, finalErrorType);
		result.put(SARIFConfig.MESSAGE_KEY, this.sarifHelper.getMessage(text, richText));
		result.put(SARIFConfig.LOCATIONS_KEY, this.sarifHelper.getLocations(c, methodName, lineNumber, method, statement));
		this.results.add(result);
	}

	private JSONObject makeSARIF() {
		this.resources.put(SARIFConfig.RULES_KEY, this.rules);
		
		JSONObject sarif = new JSONObject();
		sarif.put(SARIFConfig.SARIF_VERSION, SARIFConfig.SARIF_VERSION_NUMBER);
		
		JSONArray runs = new JSONArray();
		JSONObject run = new JSONObject();
		
		run.put(SARIFConfig.TOOL_KEY, this.sarifHelper.getToolInfo());
		
		if (includeStatistics()) {
			run.put(SARIFConfig.STATISTICS_KEY, this.sarifHelper.getStatisticsInfo(getStatistics()));
		}
		
		run.put(SARIFConfig.FILES_KEY, this.files);
		run.put(SARIFConfig.RESULTS_KEY, this.results);
		run.put(SARIFConfig.RESOURCES_KEY, this.resources);
		runs.add(run);
		
		sarif.put(SARIFConfig.RUNS_KEY, runs);
		
		return sarif;
	}

	@Override
	public void handleAnalysisResults() {
		for (WrappedClass c : this.errorMarkers.rowKeySet()) {
			addFile(c);

			for (Entry<Method, Set<AbstractError>> e : this.errorMarkers.row(c).entrySet()) {
				for (AbstractError marker : e.getValue()) {
					String errorType = marker.getClass().getSimpleName();
					String richText = String.format("%s violating CrySL rule for %s.",
							marker.getClass().getSimpleName(), marker.getRule().getClassName());
					String text = String.format("%s.", marker.toErrorMarkerString());
					int lineNumber = marker.getErrorStatement().getStartLineNumber();
					String method = e.getKey().getSubSignature();
					String statement = marker.getErrorStatement().toString();
					this.addResults(errorType, c, e.getKey().getName(), lineNumber, method, statement, text, richText);
				}
			}
		}
		
		JSONObject sarif = makeSARIF();
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
			writer.writeValue(Paths.get(getOutputFolder() + File.separator + REPORT_NAME).toFile(), sarif);
			
			LOGGER.info("SARIF Report generated to file : " + getOutputFolder() + File.separator + REPORT_NAME);
		} catch (IOException e) {
			LOGGER.error("Could not write to file: " + getOutputFolder().getAbsolutePath() + File.separator + REPORT_NAME, e);
		}
	}
	
}
