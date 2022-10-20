package crypto.reporting;

import java.io.File;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.rules.CrySLRule;
import soot.SootClass;
import soot.SootMethod;


/**
 *  This class creates {@link SARIFReporter} a constructor with reportDir and rules as parameter
 *
 * @param reportDir a {@link String} path giving the location of the report directory
 * @param rules {@link CrySLRule} the rules with which the project is analyzed
 */


@SuppressWarnings("unchecked")
public class SARIFReporter extends ErrorMarkerListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(SARIFReporter.class);
	
	private File outputFolder;
	// private List<CrySLRule> rules;
	private Collection<IAnalysisSeed> objects = new HashSet<>();
	private JSONObject files = new JSONObject(), resources = new JSONObject(), rules = new JSONObject();
	private JSONArray results = new JSONArray();
	private SARIFHelper sarifHelper;
	private Map<String, Integer> errorCountMap;
	/**
	 * name of the analysis report
	 */
	private static final String REPORT_NAME = "CryptoAnalysis-Report.json";

	public SARIFReporter(String reportDir, List<CrySLRule> rules) {
		this.outputFolder = (reportDir != null ? new File(reportDir) : new File(System.getProperty("user.dir")));
		this.sarifHelper = new SARIFHelper();
		this.errorCountMap = new HashMap<String, Integer>();
		initializeMap();
	}
	
	public SARIFReporter(String string, List<CrySLRule> rules, SourceCodeLocater sourceLocater) {
		this(string, rules);
		this.sarifHelper = new SARIFHelper(sourceLocater);
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
	}

	private void addFile(SootClass c) {
		String filePath = this.sarifHelper.getFileName(c);
		JSONObject mimeType = new JSONObject();
		mimeType.put(SARIFConfig.MIME_TYPE_KEY, SARIFConfig.MIME_TYPE_VALUE);
		this.files.put(filePath, mimeType);
	}

	private String addRules(String errorType) {
		String finalErrorType = errorType;
		if (this.rules.containsKey(errorType)) {
			int count = this.errorCountMap.get(errorType);
			count++;
			finalErrorType = errorType.concat("-".concat(Integer.toString(count)));
			this.errorCountMap.put(errorType, count);
		}
		JSONObject ruleInfo = new JSONObject();
		JSONObject fullDescription = new JSONObject();
		fullDescription.put(SARIFConfig.TEXT_KEY, this.sarifHelper.getRuleDescription(errorType));
		ruleInfo.put(SARIFConfig.RULES_ID_KEY, errorType);
		ruleInfo.put(SARIFConfig.FULL_DESCRIPTION_KEY, fullDescription);
		this.rules.put(finalErrorType, ruleInfo);
		return finalErrorType;
	}

	private void addResults(String errorType, SootClass c, String methodName, int lineNumber, String text,
			String richText) {
		JSONObject result = new JSONObject();
		String finalErrorType = addRules(errorType);
		result.put(SARIFConfig.RULE_ID_KEY, finalErrorType);
		result.put(SARIFConfig.MESSAGE_KEY, this.sarifHelper.getMessage(text, richText));
		result.put(SARIFConfig.LOCATIONS_KEY, this.sarifHelper.getLocations(c, methodName, lineNumber));
		this.results.add(result);
	}

	private JSONObject makeSARIF() {
		this.resources.put(SARIFConfig.RULES_KEY, this.rules);
		JSONObject sarif = new JSONObject();
		sarif.put(SARIFConfig.SARIF_VERSION, SARIFConfig.SARIF_VERSION_NUMBER);
		JSONArray runs = new JSONArray();
		JSONObject run = new JSONObject();
		run.put(SARIFConfig.TOOL_KEY, this.sarifHelper.getToolInfo());
		run.put(SARIFConfig.FILES_KEY, this.files);
		run.put(SARIFConfig.RESULTS_KEY, this.results);
		run.put(SARIFConfig.RESOURCES_KEY, this.resources);
		runs.add(run);
		sarif.put(SARIFConfig.RUNS_KEY, runs);
		return sarif;
	}

	@Override
	public void discoveredSeed(IAnalysisSeed object) {
		this.objects.add(object);
	}

	@Override
	public void afterAnalysis() {
		for (SootClass c : this.errorMarkers.rowKeySet()) {
			addFile(c);

			for (Entry<SootMethod, Set<AbstractError>> e : this.errorMarkers.row(c).entrySet()) {
				for (AbstractError marker : e.getValue()) {
					String errorType = marker.getClass().getSimpleName();
					String richText = String.format("%s violating CrySL rule for %s.",
							marker.getClass().getSimpleName(), marker.getRule().getClassName());
					String text = String.format("%s.", marker.toErrorMarkerString());
					int lineNumber = marker.getErrorLocation().getUnit().get().getJavaSourceStartLineNumber();
					this.addResults(errorType, c, e.getKey().getName(), lineNumber, text, richText);
				}
			}
		}
		JSONObject sarif = makeSARIF();
		try {
			ObjectMapper mapper = new ObjectMapper();
			ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
			writer.writeValue(Paths.get(outputFolder + File.separator + REPORT_NAME).toFile(), sarif);
			LOGGER.info("SARIF Report generated to file : "+ outputFolder + File.separator + REPORT_NAME);
		} catch (IOException e) {
			LOGGER.error("Could not write to file: "+outputFolder.getAbsolutePath() + File.separator+ REPORT_NAME, e);
		}
	}
}
