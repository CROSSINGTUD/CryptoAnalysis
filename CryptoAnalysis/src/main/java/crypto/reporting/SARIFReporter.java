/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.reporting;

import boomerang.scope.Method;
import boomerang.scope.WrappedClass;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.Table;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.listener.AnalysisStatistics;
import crysl.rule.CrySLRule;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SARIFReporter extends Reporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SARIFReporter.class);
    private static final String FILE_ENDING = ".sarif.json";

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
        JSONArray results = new JSONArray();

        // Extract the files where errors occur
        List<String> sortedFiles = new ArrayList<>();
        for (WrappedClass wrappedClass : errorCollection.rowKeySet()) {
            if (!sortedFiles.contains(wrappedClass.getFullyQualifiedName())) {
                sortedFiles.add(wrappedClass.getFullyQualifiedName());
            }
        }

        JSONObject files = new JSONObject();

        for (WrappedClass wrappedClass : errorCollection.rowKeySet()) {
            addFile(files, wrappedClass);

            for (Collection<AbstractError> errors : errorCollection.row(wrappedClass).values()) {
                for (AbstractError error : errors) {
                    addError(error.getClass().getSimpleName());

                    int fileIndex = sortedFiles.indexOf(wrappedClass.getFullyQualifiedName());
                    JSONObject errorObject = createErrorObject(error, fileIndex);
                    results.put(errorObject);
                }
            }
        }

        JSONObject sarif = makeSARIF(results, files, seeds.size(), statistics);

        writeToFile(sarif);
    }

    private JSONObject makeSARIF(
            JSONArray results, JSONObject files, int seedCount, AnalysisStatistics statistics) {
        JSONArray runs = new JSONArray();
        JSONObject run = new JSONObject();
        JSONArray artifacts = new JSONArray(files.toMap().values());

        run.put(SARIFConfig.TOOL_KEY, getToolInfo());
        run.put(SARIFConfig.ARTIFACTS_KEY, artifacts);
        run.put(SARIFConfig.RESULTS_KEY, results);
        run.put(SARIFConfig.PROPERTIES_KEY, createPropertyObject(seedCount, statistics));
        runs.put(run);

        JSONObject sarif = new JSONObject();
        sarif.put(SARIFConfig.SARIF_VERSION, SARIFConfig.SARIF_VERSION_NUMBER);
        sarif.put(SARIFConfig.SCHEMA_KEY, SARIFConfig.SCHEMA_VALUE);
        sarif.put(SARIFConfig.RUNS_KEY, runs);

        return sarif;
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

    private JSONObject createErrorObject(AbstractError error, int uriLocation) {
        JSONObject errorObject = new JSONObject();
        errorObject.put(SARIFConfig.VIOLATED_RULE_ID_KEY, error.getRule().getClassName());
        errorObject.put(SARIFConfig.MESSAGE_KEY, createMessageObject(error));
        errorObject.put(SARIFConfig.LOCATIONS_KEY, createLocationsObject(error, uriLocation));

        JSONObject properties = new JSONObject();
        properties.put(SARIFConfig.ERROR_ID_KEY, error.getErrorId());
        properties.put(SARIFConfig.ERROR_TYPE_KEY, error.getClass().getSimpleName());

        JSONArray preErrors = new JSONArray();
        for (AbstractError preError : error.getPrecedingErrors()) {
            preErrors.put(preError.getErrorId());
        }

        JSONArray subErrors = new JSONArray();
        for (AbstractError subError : error.getSubsequentErrors()) {
            subErrors.put(subError.getErrorId());
        }

        properties.put(SARIFConfig.PRECEDING_ERRORS_KEY, preErrors);
        properties.put(SARIFConfig.SUBSEQUENT_ERRORS_KEY, subErrors);

        errorObject.put(SARIFConfig.PROPERTIES_KEY, properties);

        return errorObject;
    }

    private String sanitizeMessage(String message) {
        return message.replace("\\", "\\\\").replace("\n", "\\n").replace("\t", "\\t");
    }

    private JSONArray createLocationsObject(AbstractError error, int uriIndex) {
        JSONArray locations = new JSONArray();

        JSONObject physicalLocation = new JSONObject();
        physicalLocation.put(
                SARIFConfig.PHYSICAL_LOCATION_KEY, createPhysicalLocationObject(error, uriIndex));
        locations.put(physicalLocation);

        JSONObject logicalLocation = new JSONObject();
        logicalLocation.put(SARIFConfig.LOGICAL_LOCATION_KEY, createLogicalLocationForError(error));
        locations.put(logicalLocation);

        return locations;
    }

    private JSONObject createPhysicalLocationObject(AbstractError error, int uriIndex) {
        JSONObject physicalLocation = new JSONObject();

        JSONObject artefactLocation = new JSONObject();
        artefactLocation.put(
                SARIFConfig.URI_KEY,
                getFileName(error.getErrorStatement().getMethod().getDeclaringClass()));
        artefactLocation.put(SARIFConfig.INDEX_KEY, uriIndex);
        physicalLocation.put(SARIFConfig.ARTIFACT_LOCATION_KEY, artefactLocation);

        JSONObject region = new JSONObject();
        region.put(SARIFConfig.START_LINE_KEY, error.getLineNumber());

        JSONObject regionSnippet = new JSONObject();
        regionSnippet.put(SARIFConfig.TEXT_KEY, error.getErrorStatement().toString());
        region.put(SARIFConfig.SNIPPET_KEY, regionSnippet);

        physicalLocation.put(SARIFConfig.REGION_KEY, region);

        return physicalLocation;
    }

    private JSONArray createLogicalLocationForError(AbstractError error) {
        JSONArray logicalLocation = new JSONArray();

        JSONObject classObject = new JSONObject();
        classObject.put(
                SARIFConfig.NAME_KEY,
                error.getErrorStatement().getMethod().getDeclaringClass().getFullyQualifiedName());
        classObject.put(SARIFConfig.KIND_KEY, "class");
        logicalLocation.put(classObject);

        JSONObject functionObject = new JSONObject();
        functionObject.put(
                SARIFConfig.NAME_KEY, error.getErrorStatement().getMethod().getSubSignature());
        functionObject.put(SARIFConfig.KIND_KEY, "method");
        logicalLocation.put(functionObject);

        return logicalLocation;
    }

    private void addError(String errorType) {
        if (errorCountMap.containsKey(errorType)) {
            int errorCount = errorCountMap.get(errorType);
            errorCountMap.put(errorType, errorCount + 1);
        } else {
            errorCountMap.put(errorType, 1);
        }
    }

    private void addFile(JSONObject files, WrappedClass c) {
        String filePath = getFileName(c);
        JSONObject mimeType = new JSONObject();
        JSONObject mimeLocation = new JSONObject();

        mimeLocation.put(SARIFConfig.URI_KEY, filePath);
        mimeType.put(SARIFConfig.ARTIFACTS_LOCATION_KEY, mimeLocation);
        mimeType.put(SARIFConfig.MIME_TYPE_KEY, SARIFConfig.MIME_TYPE_VALUE);

        files.put(filePath, mimeType);
    }

    public JSONObject getToolInfo() {
        JSONObject tool = new JSONObject();
        JSONObject driver = new JSONObject();

        // TODO Put correct CryptoAnalysis version in report
        driver.put(SARIFConfig.ANALYSIS_TOOL_NAME_KEY, SARIFConfig.ANALYSIS_TOOL_NAME_VALUE);
        driver.put(SARIFConfig.TOOL_FULL_NAME_KEY, SARIFConfig.TOOL_FULL_NAME_VALUE);
        driver.put(SARIFConfig.INFORMATION_URI_KEY, SARIFConfig.INFORMATION_URI_VALUE);
        driver.put(SARIFConfig.SEMANTIC_VERSION_KEY, SARIFConfig.SEMANTIC_VERSION_VALUE);
        driver.put(SARIFConfig.LANGUAGE_KEY, SARIFConfig.LANGUAGE_VALUE);

        tool.put(SARIFConfig.DRIVER_KEY, driver);

        return tool;
    }

    public JSONObject createMessageObject(AbstractError error) {
        String errorType = error.getClass().getSimpleName();
        String richText = errorType + " violating CrySL rule for " + error.getRule().getClassName();
        String errorMarker = sanitizeMessage(error.toErrorMarkerString());

        JSONObject message = new JSONObject();
        message.put(SARIFConfig.TEXT_KEY, errorMarker);
        message.put(SARIFConfig.MARKDOWN_KEY, richText);

        return message;
    }

    public String getFileName(WrappedClass c) {
        return c.getFullyQualifiedName().replace(".", "/") + ".java";
    }

    private JSONObject createPropertyObject(int seedCount, AnalysisStatistics statistics) {
        JSONObject properties = new JSONObject();
        JSONObject summary = new JSONObject();

        Collection<String> ruleNames = new HashSet<>();
        for (CrySLRule rule : ruleset) {
            ruleNames.add(rule.getClassName());
        }

        JSONObject resources = new JSONObject();
        resources.put(SARIFConfig.RULES_KEY, ruleNames);
        summary.put(SARIFConfig.RESOURCES_KEY, resources);

        // Total seed count;
        summary.put(SARIFConfig.TOTAL_SEEDS_KEY, seedCount);

        // Total rules count
        summary.put(SARIFConfig.TOTAL_RULES_KEY, ruleset.size());

        // Total error counts
        int totalErrorCount = 0;
        for (int errors : errorCountMap.values()) {
            totalErrorCount += errors;
        }

        summary.put(SARIFConfig.TOTAL_ERRORS_KEY, totalErrorCount);

        // Individual error counts
        JSONObject errorCounts = new JSONObject(errorCountMap);
        summary.put(SARIFConfig.ERROR_COUNTS_KEY, errorCounts);

        JSONObject statsObject = createStatistics(statistics);
        summary.put(SARIFConfig.STATISTICS_KEY, statsObject);

        properties.put(SARIFConfig.SUMMARY_KEY, summary);

        return properties;
    }

    private JSONObject createStatistics(AnalysisStatistics statistics) {
        JSONObject stats = new JSONObject();

        stats.put(SARIFConfig.ANALYSIS_TIME_KEY, statistics.getAnalysisTime());
        stats.put(SARIFConfig.CALL_GRAPH_TIME_KEY, statistics.getCallGraphTime());
        stats.put(SARIFConfig.TYPESTATE_TIME_KEY, statistics.getTypestateTime());
        stats.put(SARIFConfig.REACHABLE_METHODS_KEY, statistics.getReachableMethods());
        stats.put(SARIFConfig.EDGES_IN_CALL_GRAPH_KEY, statistics.getEdges());
        stats.put(SARIFConfig.ENTRY_POINTS_KEY, statistics.getEntryPoints());

        return stats;
    }
}
