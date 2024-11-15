package crypto.reporting;

import boomerang.scene.Method;
import boomerang.scene.WrappedClass;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.Table;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.listener.AnalysisStatistics;
import crypto.rules.CrySLRule;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SARIFReporter extends Reporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SARIFReporter.class);
    private static final String FILE_ENDING = ".json";

    private final JSONObject files = new JSONObject();
    private final JSONObject resources = new JSONObject();
    private final JSONArray results = new JSONArray();
    private final JSONObject stats = new JSONObject();

    private final Map<String, Integer> errorCountMap;

    public SARIFReporter(String outputDir, Collection<CrySLRule> ruleset) throws IOException {
        super(outputDir, ruleset);

        this.errorCountMap = new HashMap<>();
    }

    @Override
    public void createAnalysisReport(
            Collection<IAnalysisSeed> seeds,
            Table<WrappedClass, Method, Set<AbstractError>> errorCollection,
            AnalysisStatistics statistics) {
        for (WrappedClass wrappedClass : errorCollection.rowKeySet()) {
            addFile(wrappedClass);

            for (Map.Entry<Method, Set<AbstractError>> entry :
                    errorCollection.row(wrappedClass).entrySet()) {
                String methodName = entry.getKey().toString();

                for (AbstractError error : entry.getValue()) {
                    String violatedRule = error.getRule().getClassName();
                    String errorType = error.getClass().getSimpleName();
                    String richText =
                            errorType
                                    + " violating CrySL rule for "
                                    + error.getRule().getClassName();
                    String errorMarker = error.toErrorMarkerString();
                    int lineNumber = error.getLineNumber();
                    String statement = error.getErrorStatement().toString();

                    addResults(
                            violatedRule,
                            errorType,
                            wrappedClass,
                            methodName,
                            lineNumber,
                            methodName,
                            statement,
                            errorMarker,
                            richText);
                }
            }
        }
        setStatistics(statistics);

        JSONObject sarif = makeSARIF();

        writeToFile(sarif);
    }

    private void writeToFile(JSONObject sarif) {
        String fileName = outputFile.getAbsolutePath() + File.separator + REPORT_NAME + FILE_ENDING;

        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
            writer.writeValue(Paths.get(fileName).toFile(), sarif.toMap());

            LOGGER.info("SARIF report generated to file {}", fileName);
        } catch (IOException e) {
            LOGGER.error("Could not write to file {}: {}", fileName, e.getMessage());
        }
    }

    private JSONObject makeSARIF() {
        Collection<String> ruleNames = new HashSet<>();
        for (CrySLRule rule : ruleset) {
            ruleNames.add(rule.getClassName());
        }
        resources.put(SARIFConfig.RULES_KEY, ruleNames);

        JSONObject sarif = new JSONObject();
        sarif.put(SARIFConfig.SARIF_VERSION, SARIFConfig.SARIF_VERSION_NUMBER);

        JSONArray runs = new JSONArray();
        JSONObject run = new JSONObject();

        run.put(SARIFConfig.TOOL_KEY, getToolInfo());
        run.put(SARIFConfig.FILES_KEY, files);
        run.put(SARIFConfig.RESULTS_KEY, results);
        run.put(SARIFConfig.RESOURCES_KEY, resources);
        run.put(SARIFConfig.SUMMARY_KEY, getSummary());
        runs.put(run);

        sarif.put(SARIFConfig.RUNS_KEY, runs);

        return sarif;
    }

    private void addResults(
            String violatedRule,
            String errorType,
            WrappedClass c,
            String methodName,
            int lineNumber,
            String method,
            String statement,
            String text,
            String richText) {
        JSONObject result = new JSONObject();

        addError(errorType);
        result.put(SARIFConfig.VIOLATED_RULE_ID_KEY, violatedRule);
        result.put(SARIFConfig.ERROR_TYPE_KEY, errorType);
        result.put(SARIFConfig.MESSAGE_KEY, getMessage(text, richText));
        result.put(
                SARIFConfig.LOCATIONS_KEY,
                getLocations(c, methodName, lineNumber, method, statement));
        results.put(result);
    }

    private void addError(String errorType) {
        if (errorCountMap.containsKey(errorType)) {
            int errorCount = errorCountMap.get(errorType);
            errorCountMap.put(errorType, errorCount + 1);
        } else {
            errorCountMap.put(errorType, 1);
        }
    }

    private void addFile(WrappedClass c) {
        String filePath = getFileName(c);
        JSONObject mimeType = new JSONObject();
        mimeType.put(SARIFConfig.MIME_TYPE_KEY, SARIFConfig.MIME_TYPE_VALUE);

        files.put(filePath, mimeType);
    }

    public JSONObject getToolInfo() {
        JSONObject tool = new JSONObject();

        // TODO Put correct CryptoAnalysis version in report
        tool.put(SARIFConfig.ANALYSIS_TOOL_NAME_KEY, SARIFConfig.ANALYSIS_TOOL_NAME_VALUE);
        tool.put(SARIFConfig.VERSION, getClass().getPackage().getImplementationVersion());
        tool.put(
                SARIFConfig.SEMANTIC_VERSION_KEY,
                getClass().getPackage().getImplementationVersion());
        tool.put(SARIFConfig.LANGUAGE_KEY, SARIFConfig.LANGUAGE_VALUE);

        return tool;
    }

    public JSONObject getMessage(String text, String richText) {
        JSONObject message = new JSONObject();

        message.put(SARIFConfig.TEXT_KEY, text);
        message.put(SARIFConfig.RICH_TEXT_KEY, richText);

        return message;
    }

    public String getFileName(WrappedClass c) {
        return c.getName().replace(".", "/") + ".java";
    }

    public JSONArray getLocations(
            WrappedClass c, String methodName, int lineNumber, String method, String statement) {
        JSONArray locations = new JSONArray();
        JSONObject location = new JSONObject();

        JSONObject region = new JSONObject();
        region.put(SARIFConfig.START_LINE_KEY, String.valueOf(lineNumber));
        region.put(SARIFConfig.METHOD_KEY, method);
        region.put(SARIFConfig.STATEMENT_KEY, statement);

        JSONObject uri = new JSONObject();
        uri.put(SARIFConfig.URI_KEY, getFileName(c));

        JSONObject physicalLocation = new JSONObject();
        physicalLocation.put(SARIFConfig.FILE_LOCATION_KEY, uri);
        physicalLocation.put(SARIFConfig.REGION_KEY, region);

        location.put(SARIFConfig.PHYSICAL_LOCATION_KEY, physicalLocation);

        String fullyQualifiedLogicalName = c.getName().replace(".", "::") + "::" + methodName;
        location.put(SARIFConfig.FULLY_QUALIFIED_LOGICAL_NAME_KEY, fullyQualifiedLogicalName);

        locations.put(location);

        return new JSONArray(Collections.singletonList(locations));
    }

    private JSONObject getSummary() {
        JSONObject summary = new JSONObject();

        JSONObject errorCounts = new JSONObject(errorCountMap);
        JSONObject statistics = new JSONObject(stats.toMap());

        summary.put(SARIFConfig.ERROR_COUNTS_KEY, errorCounts);
        summary.put(SARIFConfig.STATISTICS_KEY, statistics);

        return summary;
    }

    private void setStatistics(AnalysisStatistics statistics) {
        stats.put(SARIFConfig.ANALYSIS_TIME_KEY, statistics.getAnalysisTime());
        stats.put(SARIFConfig.CALL_GRAPH_TIME_KEY, statistics.getCallGraphTime());
        stats.put(SARIFConfig.TYPESTATE_TIME_KEY, statistics.getTypestateTime());
        stats.put(SARIFConfig.REACHABLE_METHODS_KEY, statistics.getReachableMethods());
        stats.put(SARIFConfig.EDGES_IN_CALL_GRAPH_KEY, statistics.getEdges());
        stats.put(SARIFConfig.ENTRY_POINTS_KEY, statistics.getEntryPoints());
    }
}
