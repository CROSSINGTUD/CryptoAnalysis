package crypto.reporting;

import boomerang.scene.Method;
import boomerang.scene.WrappedClass;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.Table;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.listener.AnalysisStatistics;
import crypto.rules.CrySLRule;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SARIFReporter extends Reporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SARIFReporter.class);
    private static final String FILE_ENDING = ".json";

    private final JSONObject files = new JSONObject();
    private final JSONObject resources = new JSONObject();
    private final JSONObject rules = new JSONObject();
    private final JSONArray results = new JSONArray();

    private final SARIFHelper sarifHelper;
    private final Map<String, Integer> errorCountMap;

    public SARIFReporter(String outputDir, Collection<CrySLRule> ruleset) throws IOException {
        super(outputDir, ruleset);

        this.sarifHelper = new SARIFHelper();
        this.errorCountMap = new HashMap<>();
        initializeMap();
    }

    private void initializeMap() {
        this.errorCountMap.put(SARIFConfig.CALL_TO_ERROR_KEY, 0);
        this.errorCountMap.put(SARIFConfig.CONSTRAINT_ERROR_KEY, 0);
        this.errorCountMap.put(SARIFConfig.FORBIDDEN_METHOD_ERROR_KEY, 0);
        this.errorCountMap.put(SARIFConfig.HARD_CODED_ERROR_KEY, 0);
        this.errorCountMap.put(SARIFConfig.IMPRECISE_VALUE_EXTRACTION_ERROR_KEY, 0);
        this.errorCountMap.put(SARIFConfig.INCOMPLETE_OPERATION_ERROR_KEY, 0);
        this.errorCountMap.put(SARIFConfig.INSTANCE_OF_ERROR_KEY, 0);
        this.errorCountMap.put(SARIFConfig.NEVER_TYPE_OF_ERROR_KEY, 0);
        this.errorCountMap.put(SARIFConfig.NO_CALL_TO_ERROR_KEY, 0);
        this.errorCountMap.put(SARIFConfig.PREDICATE_CONTRADICTION_ERROR_KEY, 0);
        this.errorCountMap.put(SARIFConfig.REQUIRED_PREDICATE_ERROR_KEY, 0);
        this.errorCountMap.put(SARIFConfig.TYPESTATE_ERROR_KEY, 0);
        this.errorCountMap.put(SARIFConfig.UNCAUGHT_EXCEPTION_ERROR_KEY, 0);
    }

    @Override
    public void createAnalysisReport(Collection<IAnalysisSeed> seeds, Table<WrappedClass, Method, Set<AbstractError>> errorCollection, AnalysisStatistics statistics) {
        for (WrappedClass wrappedClass : errorCollection.rowKeySet()) {
            addFile(wrappedClass);

            for (Map.Entry<Method, Set<AbstractError>> entry : errorCollection.row(wrappedClass).entrySet()) {
                String methodName = entry.getKey().toString();

                for (AbstractError error : entry.getValue()) {
                    String errorType = error.getClass().getSimpleName();
                    String richText = errorType + " violating CrySL rule for " + error.getRule().getClassName();
                    String errorMarker = error.toErrorMarkerString();
                    int lineNumber = error.getLineNumber();
                    String statement = error.getErrorStatement().toString();

                    addResults(errorType, wrappedClass, methodName, lineNumber, methodName, statement, errorMarker, richText);
                }
            }
        }

        JSONObject sarif = makeSARIF();

        writeToFile(sarif);
    }

    private void writeToFile(JSONObject sarif) {
        String fileName = outputFile.getAbsolutePath() + File.separator + REPORT_NAME + FILE_ENDING;

        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
            writer.writeValue(Paths.get(fileName).toFile(), sarif);

            LOGGER.info("SARIF report generated to file {}", fileName);
        } catch (IOException e) {
            LOGGER.error("Could not write to file {}: {}", fileName, e.getMessage());
        }
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
        runs.put(run);

        sarif.put(SARIFConfig.RUNS_KEY, runs);

        return sarif;
    }


    private void addResults(String errorType, WrappedClass c, String methodName, int lineNumber, String method, String statement, String text, String richText) {
        JSONObject result = new JSONObject();
        String finalErrorType = addRules(errorType);

        result.put(SARIFConfig.RULE_ID_KEY, finalErrorType);
        result.put(SARIFConfig.MESSAGE_KEY, this.sarifHelper.getMessage(text, richText));
        result.put(SARIFConfig.LOCATIONS_KEY, this.sarifHelper.getLocations(c, methodName, lineNumber, method, statement));
        results.put(result);
    }


    private String addRules(String errorType) {
        if (rules.has(errorType)) {
            int count = errorCountMap.get(errorType);
            errorCountMap.put(errorType, count + 1);
            JSONObject ruleInfo = new JSONObject();
            JSONObject fullDescription = new JSONObject();
            fullDescription.put(SARIFConfig.TEXT_KEY, sarifHelper.getRuleDescription(errorType));
            ruleInfo.put(SARIFConfig.RULES_ID_KEY, errorType);
            ruleInfo.put(SARIFConfig.FULL_DESCRIPTION_KEY, fullDescription);

            if (count == 0) {
                rules.put(errorType, ruleInfo);
            }
        }
        return errorType;
    }

    private void addFile(WrappedClass c) {
        String filePath = this.sarifHelper.getFileName(c);
        JSONObject mimeType = new JSONObject();
        mimeType.put(SARIFConfig.MIME_TYPE_KEY, SARIFConfig.MIME_TYPE_VALUE);

        files.put(filePath, mimeType);
    }

}
