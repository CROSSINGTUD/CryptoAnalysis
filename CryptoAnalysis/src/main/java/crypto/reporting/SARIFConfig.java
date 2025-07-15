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

public class SARIFConfig {

    public static final String SCHEMA_KEY = "$schema";
    public static final String SCHEMA_VALUE = "https://json.schemastore.org/sarif-2.1.0.json";

    public static final String SARIF_VERSION = "version";
    public static final String SARIF_VERSION_NUMBER = "2.1.0";
    public static final String RUNS_KEY = "runs";

    public static final String TOOL_KEY = "tool";
    public static final String DRIVER_KEY = "driver";
    public static final String TOOL_FULL_NAME_KEY = "fullName";
    public static final String TOOL_FULL_NAME_VALUE = "CogniCrypt (en-US)";
    public static final String INFORMATION_URI_KEY = "informationUri";
    public static final String INFORMATION_URI_VALUE =
            "https://github.com/CROSSINGTUD/CryptoAnalysis";
    public static final String SEMANTIC_VERSION_KEY = "semanticVersion";
    public static final String SEMANTIC_VERSION_VALUE = "5.0.0";
    public static final String LANGUAGE_KEY = "language";
    public static final String LANGUAGE_VALUE = "en-US";

    public static final String ANALYSIS_TOOL_NAME_KEY = "name";
    public static final String ANALYSIS_TOOL_NAME_VALUE = "CryptoAnalysis";

    public static final String ARTIFACTS_KEY = "artifacts";
    public static final String ARTIFACTS_LOCATION_KEY = "location";
    public static final String MIME_TYPE_KEY = "mimeType";
    public static final String MIME_TYPE_VALUE = "text/java";

    public static final String RESULTS_KEY = "results";
    public static final String ERROR_ID_KEY = "errorId";
    public static final String VIOLATED_RULE_ID_KEY = "ruleId";
    public static final String ERROR_TYPE_KEY = "errorType";
    public static final String PRECEDING_ERRORS_KEY = "precedingErrors";
    public static final String SUBSEQUENT_ERRORS_KEY = "subsequentErrors";
    public static final String MESSAGE_KEY = "message";
    public static final String TEXT_KEY = "text";
    public static final String MARKDOWN_KEY = "markdown";
    public static final String LOCATIONS_KEY = "locations";
    public static final String PHYSICAL_LOCATION_KEY = "physicalLocation";
    public static final String ARTIFACT_LOCATION_KEY = "artifactLocation";
    public static final String LOGICAL_LOCATION_KEY = "logicalLocations";

    public static final String URI_KEY = "uri";
    public static final String INDEX_KEY = "index";
    public static final String REGION_KEY = "region";
    public static final String START_LINE_KEY = "startLine";
    public static final String SNIPPET_KEY = "snippet";
    public static final String NAME_KEY = "name";
    public static final String KIND_KEY = "kind";

    public static final String RESOURCES_KEY = "resources";
    public static final String RULES_KEY = "rules";

    // Keys for the summary and statistics
    public static final String PROPERTIES_KEY = "properties";
    public static final String SUMMARY_KEY = "summary";
    public static final String TOTAL_SEEDS_KEY = "totalSeedsCount";
    public static final String TOTAL_RULES_KEY = "totalRulesCount";
    public static final String TOTAL_ERRORS_KEY = "totalErrorCount";
    public static final String ERROR_COUNTS_KEY = "errorCounts";

    public static final String STATISTICS_KEY = "statistics";
    public static final String ANALYSIS_TIME_KEY = "analysisTime";
    public static final String CALL_GRAPH_TIME_KEY = "callGraphConstructionTime";
    public static final String TYPESTATE_TIME_KEY = "typestateAnalysisTime";
    public static final String REACHABLE_METHODS_KEY = "reachableMethods";
    public static final String EDGES_IN_CALL_GRAPH_KEY = "edgesInCallGraph";
    public static final String ENTRY_POINTS_KEY = "entryPoints";
}
