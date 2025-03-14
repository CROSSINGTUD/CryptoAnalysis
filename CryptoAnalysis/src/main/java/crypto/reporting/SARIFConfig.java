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

    public static final String VERSION = "version";
    public static final String SARIF_VERSION = "sarifVersion";
    public static final String SARIF_VERSION_NUMBER = "2.0.0";
    public static final String VERSION_NUMBER = "1.0.0";
    public static final String RUNS_KEY = "runs";

    public static final String TOOL_KEY = "tool";
    public static final String TOOL_NAME_KEY = "name";
    public static final String TOOL_NAME_VALUE = "CogniCrypt";
    public static final String TOOL_FULL_NAME_KEY = "fullName";
    public static final String TOOL_FULL_NAME_VALUE = "CogniCrypt (en-US)";
    public static final String SEMANTIC_VERSION_KEY = "semanticVersion";
    public static final String SEMANTIC_VERSION_VALUE = "1.0.0";
    public static final String LANGUAGE_KEY = "language";
    public static final String LANGUAGE_VALUE = "en-US";

    public static final String ANALYSIS_TOOL_NAME_KEY = "name";
    public static final String ANALYSIS_TOOL_NAME_VALUE = "CryptoAnalysis";

    public static final String FILES_KEY = "files";
    public static final String MIME_TYPE_KEY = "mimeType";
    public static final String MIME_TYPE_VALUE = "text/java";

    public static final String RESULTS_KEY = "results";
    public static final String VIOLATED_RULE_ID_KEY = "violatedRule";
    public static final String ERROR_TYPE_KEY = "errorType";
    public static final String MESSAGE_KEY = "message";
    public static final String TEXT_KEY = "text";
    public static final String RICH_TEXT_KEY = "richText";
    public static final String LOCATIONS_KEY = "locations";
    public static final String PHYSICAL_LOCATION_KEY = "physicalLocation";
    public static final String FILE_LOCATION_KEY = "fileLocation";
    public static final String URI_KEY = "uri";
    public static final String REGION_KEY = "region";
    public static final String START_LINE_KEY = "startLine";
    public static final String METHOD_KEY = "method";
    public static final String STATEMENT_KEY = "statement";
    public static final String FULLY_QUALIFIED_LOGICAL_NAME_KEY = "fullyQualifiedLogicalName";

    public static final String RESOURCES_KEY = "resources";
    public static final String RULES_KEY = "rules";

    // keys for the summary and statistics
    public static final String SUMMARY_KEY = "summary";
    public static final String ERROR_COUNTS_KEY = "errorCounts";
    public static final String STATISTICS_KEY = "statistics";
    public static final String ANALYSIS_TIME_KEY = "analysisTime";
    public static final String CALL_GRAPH_TIME_KEY = "callGraphConstructionTime";
    public static final String TYPESTATE_TIME_KEY = "typestateAnalysisTime";
    public static final String REACHABLE_METHODS_KEY = "reachableMethods";
    public static final String EDGES_IN_CALL_GRAPH_KEY = "edgesInCallGraph";
    public static final String ENTRY_POINTS_KEY = "entryPoints";
}
